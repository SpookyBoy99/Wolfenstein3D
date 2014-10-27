package com.xbit.engine.base;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import com.xbit.engine.base.objects.Door;
import com.xbit.engine.base.objects.Enemy;
import com.xbit.engine.base.objects.MedKit;
import com.xbit.engine.math.Transform;
import com.xbit.engine.math.vec2;
import com.xbit.engine.math.vec3;
import com.xbit.engine.render.Bitmap;
import com.xbit.engine.render.Material;
import com.xbit.engine.render.Mesh;
import com.xbit.engine.render.Shader;
import com.xbit.engine.render.Vertex;
import com.xbit.engine.render.shaders.BasicShader;

public class Level
{
	private static final float SPOT_WIDTH = 1;
	private static final float SPOT_LENGTH = 1;
	private static final float SPOT_HEIGHT = 1;
	private static final float OPEN_DISTANCE = 1.5f;
	private static final float DOOR_OPEN_MOVEMENT_AMOUNT = 0.9f;
	private static final int NUM_TEX_EXP = 4;
	private static final int NUM_TEXTURES = (int)Math.pow(2, NUM_TEX_EXP);
	
	private Player player;
	private Mesh mesh;
	private Bitmap level;
	private Shader shader;
	private Material material;
	private Transform transform;
	private ArrayList<Door> doors;
	private ArrayList<Enemy> enemies;
	private ArrayList<MedKit> medkits;
	private ArrayList<MedKit> medkitsToRemove;
	private ArrayList<vec3> exitPoints;
	
	private ArrayList<vec2> collisionPosStart;
	private ArrayList<vec2> collisionPosEnd;

	public Level(String levelName)
	{
		this(levelName, "WolfCollection.png");
	}
	
	public Level(String levelName, String textureName)
	{		
		level = ResourceLoader.loadBitmap(levelName).flipY();
		material = new Material(ResourceLoader.loadTexture(textureName));
		mesh = new Mesh();
		transform = new Transform();
		doors = new ArrayList<Door>();
		enemies = new ArrayList<Enemy>();
		medkits = new ArrayList<MedKit>();
		medkitsToRemove = new ArrayList<MedKit>();
		exitPoints = new ArrayList<vec3>();
		shader = BasicShader.getInstance();
		
		collisionPosStart = new ArrayList<vec2>();
		collisionPosEnd = new ArrayList<vec2>();
		
		generateLevel();
	}
	
	public void input()
	{
		if (Input.getKeyDown(Keyboard.KEY_E))
		{
			openDoors(player.getCamera().getPos(), false);
		}
		
		player.input();
	}
	
	public void update()
	{		
		for (Door door : doors)
			door.update();
		
		for (Enemy enemy : enemies)
			enemy.update();
		
		for (MedKit medkit : medkits)
			medkit.update();
		
		for (MedKit medkit : medkitsToRemove)
			medkits.remove(medkit);
		
		player.update();
	}
	
	public void render()
	{
		shader.bind();
		shader.updateUniforms(transform.getTransformation(), transform.getProjectedTransformation(), material);
		mesh.draw();
		
		for (Door door : doors)
			door.render();
		
		for (Enemy enemy : enemies)
			enemy.render();
		
		for (MedKit medkit : medkits)
			medkit.render();
		
		player.render();
	}
	
	public void openDoors(vec3 position, boolean tryExitLevel)
	{
		for (Door door : doors)
		{
			if (door.getTransform().getTranslation().sub(position).length() < OPEN_DISTANCE)
			{
				door.open();
			}
		}
		
		if (tryExitLevel)
		{
			for (vec3 exitPoint : exitPoints)
			{
				if (exitPoint.sub(position).length() < OPEN_DISTANCE)
				{
					Game.loadNextLevel();
				}
			}
		}
	}
	
	public vec2 checkIntersections(vec2 lineStart, vec2 lineEnd, boolean hurtEnemy)
	{
		vec2 nearestIntersect = null;
		
		for (int i = 0; i < collisionPosStart.size(); i++)
		{
			vec2 collisionVector = lineInterSect(lineStart, lineEnd, collisionPosStart.get(i), collisionPosEnd.get(i));
			
			if (collisionVector != null && (nearestIntersect == null || nearestIntersect.sub(lineStart).length() > collisionVector.sub(lineStart).length()))
			{
				nearestIntersect = collisionVector;
			}
		}
		
		for (Door door : doors)
		{
			vec2 collisionVector = lineIntersectRect(lineStart, lineEnd, new vec2(door.getTransform().getTranslation().getX(), door.getTransform().getTranslation().getZ()), door.getDoorSize());
			
			if (collisionVector != null && (nearestIntersect == null || nearestIntersect.sub(lineStart).length() > collisionVector.sub(lineStart).length()))
			{
				nearestIntersect = collisionVector;
			}
		}
		
		if (hurtEnemy)
		{
			vec2 nearestEnemyIntersect = null;
			Enemy nearestEnemy = null;
			
			for (Enemy enemy : enemies)
			{
				vec2 collisionVector = lineIntersectRect(lineStart, lineEnd, new vec2(enemy.getTransform().getTranslation().getX(), enemy.getTransform().getTranslation().getZ()), enemy.getEnemySize());
				nearestEnemyIntersect = findNearestVector(nearestEnemyIntersect, collisionVector, lineStart);
				
				if (nearestEnemyIntersect == collisionVector)
					nearestEnemy = enemy;
			}
			
			if (nearestEnemyIntersect != null && (nearestIntersect == null || nearestEnemyIntersect.sub(lineStart).length() < nearestIntersect.sub(lineStart).length()))
			{
				nearestEnemy.damage(player.getDamage());
			}
		}
		
		return nearestIntersect;
	}
	
	private float crossVector(vec2 a, vec2 b)
	{
		return a.getX() * b.getY() - a.getY() * b.getX();
	}
	
	public vec3 checkCollision(vec3 oldPos, vec3 newPos, float objectWidth, float objectLength)
	{
		vec2 collisionVector = new vec2(1, 1);
		vec3 movementVector = newPos.sub(oldPos);
		
		if(movementVector.length() > 0)
		{
			vec2 blockSize = new vec2(SPOT_WIDTH, SPOT_LENGTH);
			vec2 objectSize = new vec2(objectWidth, objectLength);
			
			vec2 oldPos2 = new vec2(oldPos.getX(), oldPos.getZ());
			vec2 newPos2 = new vec2(newPos.getX(), newPos.getZ());
			
			for (int i = 0; i < level.getWidth(); i++)
			{
				for (int j = 0; j < level.getWidth(); j++)
				{
					if ((level.getPixel(i, j) & 0xFFFFFF) == 0)
					{
						collisionVector = collisionVector.mul(rectCollide(oldPos2, newPos2, objectSize, blockSize.mul(new vec2(i, j)), blockSize));
					}
				}
			}
			
			for (Door door : doors)
				collisionVector = collisionVector.mul(rectCollide(oldPos2, newPos2, objectSize, new vec2(door.getTransform().getTranslation().getX(), door.getTransform().getTranslation().getZ()), door.getDoorSize()));
		}
		
		return new vec3(collisionVector.getX(), 0, collisionVector.getY());
	}
	
	private vec2 rectCollide(vec2 oldPos, vec2 newPos, vec2 size1, vec2 pos2, vec2 size2)
	{
		vec2 result = new vec2(0, 0);
		
		if (newPos.getX() + size1.getX() < pos2.getX() ||
			newPos.getX() - size1.getX() > pos2.getX() + size2.getX() * size2.getX() ||
			oldPos.getY() + size1.getY() < pos2.getY() ||
			oldPos.getY() - size1.getY() > pos2.getY() + size2.getY() * size2.getY())
				result.setX(1);
		
		if (oldPos.getX() + size1.getX() < pos2.getX() ||
			oldPos.getX() - size1.getX() > pos2.getX() + size2.getX() * size2.getX() ||
			newPos.getY() + size1.getY() < pos2.getY() ||
			newPos.getY() - size1.getY() > pos2.getY() + size2.getY() * size2.getY())
				result.setY(1);
		
		return result;
	}
	
	private vec2 lineInterSect(vec2 lineStart1, vec2 lineEnd1, vec2 lineStart2, vec2 lineEnd2)
	{
		vec2 line1 = lineEnd1.sub(lineStart1);
		vec2 line2 = lineEnd2.sub(lineStart2);
		
		float cross = crossVector(line1, line2);
		
		if (cross == 0)
			return null;
		
		vec2 distanceBetweenLineStarts = lineStart2.sub(lineStart1);
		
		float a = crossVector(distanceBetweenLineStarts, line2) / cross;
		float b = crossVector(distanceBetweenLineStarts, line1) / cross;
		
		if (0.0f < a && a < 1.0f && 0.0f < b && b < 1.0f)
			return lineStart1.add(line1.mul(a));
		
		return null;
	}
	
	private vec2 findNearestVector(vec2 a, vec2 b, vec2 positionRelativeTo)
	{
		if (b != null && (a == null || a.sub(positionRelativeTo).length() > b.sub(positionRelativeTo).length()))
			return b;
		
		return a;
	}
	
	public vec2 lineIntersectRect(vec2 lineStart, vec2 lineEnd, vec2 rectPos, vec2 rectSize)
	{
		vec2 result = null;
		vec2 collisionVector = null;
		
		collisionVector = lineInterSect(lineStart, lineEnd, rectPos, new vec2(rectPos.getX() + rectSize.getX(), rectPos.getY()));
		result = findNearestVector(result, collisionVector, lineStart);
		
		collisionVector = lineInterSect(lineStart, lineEnd, rectPos, new vec2(rectPos.getX(), rectPos.getY() + rectSize.getY()));
		result = findNearestVector(result, collisionVector, lineStart);
		
		collisionVector = lineInterSect(lineStart, lineEnd, new vec2(rectPos.getX(), rectPos.getY() + rectSize.getY()), rectPos.add(rectSize));
		result = findNearestVector(result, collisionVector, lineStart);
		
		collisionVector = lineInterSect(lineStart, lineEnd, new vec2(rectPos.getX() + rectSize.getX(), rectPos.getY()), rectPos.add(rectSize));
		result = findNearestVector(result, collisionVector, lineStart);
		
		return result;
	}
	
	public void removeMedkits(MedKit medkit)
	{
		medkitsToRemove.add(medkit);
	}
	
	private void generateLevel()
	{
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
		ArrayList<Integer> indices = new ArrayList<Integer>();
	
		for (int i = 0; i < level.getWidth(); i++)
		{
			for (int j = 0; j < level.getHeight(); j++)
			{
				if ((level.getPixel(i, j) & 0xFFFFFF) == 0)
					continue;
				
				if ((level.getPixel(i, j) & 0x0000FF) == 16)
				{
					Transform doorTransform = new Transform();
					
					boolean xDoor = (level.getPixel(i, j - 1) & 0xFFFFFF) == 0 && (level.getPixel(i, j + 1) & 0xFFFFFF) == 0;
					boolean yDoor = (level.getPixel(i - 1, j) & 0xFFFFFF) == 0 && (level.getPixel(i + 1, j) & 0xFFFFFF) == 0;
					
					vec3 openPosition = null;
					
					if (xDoor && yDoor || !xDoor && !yDoor)
					{
						System.err.println("Error: Level generation failed: invalid door at " + i + ", " + j);
						new Exception().printStackTrace();
						System.exit(1);
					}
					
					if (yDoor)
					{
						doorTransform.setTranslation(i, 0, j + SPOT_LENGTH / 2);
						openPosition = doorTransform.getTranslation().sub(new vec3(DOOR_OPEN_MOVEMENT_AMOUNT, 0.0f, 0.0f));
					}
					
					if (xDoor)
					{
						doorTransform.setTranslation(i + SPOT_WIDTH / 2, 0, j);
						doorTransform.setRotation(0, 90, 0);
						openPosition = doorTransform.getTranslation().sub(new vec3(0.0f, 0.0f, DOOR_OPEN_MOVEMENT_AMOUNT));
					}
					
					doors.add(new Door(doorTransform, material, openPosition));
				}
				
				if ((level.getPixel(i, j) & 0x0000FF) == 1)
				{
					player = new Player(new vec3((i + 0.5f) * SPOT_WIDTH, 0.45f, (j + 0.5f) * SPOT_LENGTH));
				}
				
				if ((level.getPixel(i, j) & 0x0000FF) == 128)
				{
					enemies.add(new Enemy(new Transform(new vec3((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH), new vec3(0, 0, 0), new vec3(1, 1, 1))));
				}
				
				if ((level.getPixel(i, j) & 0x0000FF) == 192)
				{
					medkits.add(new MedKit(new vec3((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH)));
				}
				
				if ((level.getPixel(i, j) & 0x0000FF) == 97)
				{
					exitPoints.add(new vec3((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH));
				}
				
				int texX = ((level.getPixel(i, j) & 0x00FF00) >> 8) / NUM_TEXTURES;
				int texY = texX % NUM_TEX_EXP;
				texX /= NUM_TEX_EXP;
				
				float Xhigher = 1.0f - (float)texX / (float)NUM_TEX_EXP;
				float Xlower = Xhigher - 1.0f / (float)NUM_TEX_EXP;
				float Ylower = 1.0f - (float)texY / (float)NUM_TEX_EXP;
				float Yhigher = Ylower - 1.0f / (float)NUM_TEX_EXP;
				
				//Generate floor
				indices.add(vertices.size() + 2);
				indices.add(vertices.size() + 1);
				indices.add(vertices.size() + 0);
				indices.add(vertices.size() + 3);
				indices.add(vertices.size() + 2);
				indices.add(vertices.size() + 0);
				
				vertices.add(new Vertex(new vec3( i 	 * SPOT_WIDTH, 0,  j 	  * SPOT_LENGTH), new vec2(Xlower,  Ylower )));
				vertices.add(new Vertex(new vec3((i + 1) * SPOT_WIDTH, 0,  j 	  * SPOT_LENGTH), new vec2(Xhigher, Ylower )));
				vertices.add(new Vertex(new vec3((i + 1) * SPOT_WIDTH, 0, (j + 1) * SPOT_LENGTH), new vec2(Xhigher, Yhigher)));
				vertices.add(new Vertex(new vec3( i 	 * SPOT_WIDTH, 0, (j + 1) * SPOT_LENGTH), new vec2(Xlower,  Yhigher)));
			
				//Generate ceiling
				indices.add(vertices.size() + 0);
				indices.add(vertices.size() + 1);
				indices.add(vertices.size() + 2);
				indices.add(vertices.size() + 0);
				indices.add(vertices.size() + 2);
				indices.add(vertices.size() + 3);
				
				vertices.add(new Vertex(new vec3( i 	 * SPOT_WIDTH, SPOT_HEIGHT,  j 	  * SPOT_LENGTH), new vec2(Xlower,  Ylower )));
				vertices.add(new Vertex(new vec3((i + 1) * SPOT_WIDTH, SPOT_HEIGHT,  j 	  * SPOT_LENGTH), new vec2(Xhigher, Ylower )));
				vertices.add(new Vertex(new vec3((i + 1) * SPOT_WIDTH, SPOT_HEIGHT, (j + 1) * SPOT_LENGTH), new vec2(Xhigher, Yhigher)));
				vertices.add(new Vertex(new vec3( i 	 * SPOT_WIDTH, SPOT_HEIGHT, (j + 1) * SPOT_LENGTH), new vec2(Xlower,  Yhigher)));
			
				texX = ((level.getPixel(i, j) & 0xFF0000) >> 16) / NUM_TEXTURES;
				texY = texX % NUM_TEX_EXP;
				texX /= NUM_TEX_EXP;
				
				Xhigher = 1.0f - (float)texX / (float)NUM_TEX_EXP;
				Xlower = Xhigher - 1.0f / (float)NUM_TEX_EXP;
				Ylower = 1.0f - (float)texY / (float)NUM_TEX_EXP;
				Yhigher = Ylower - 1.0f / (float)NUM_TEX_EXP;
				
				
				//Generate Walls
				if ((level.getPixel(i, j - 1) & 0xFFFFFF) == 0)
				{
					collisionPosStart.add(new vec2(i * SPOT_WIDTH, j * SPOT_LENGTH));
					collisionPosEnd.add(new vec2((i + 1) * SPOT_WIDTH, j * SPOT_LENGTH));
					
					indices.add(vertices.size() + 0);
					indices.add(vertices.size() + 1);
					indices.add(vertices.size() + 2);
					indices.add(vertices.size() + 0);
					indices.add(vertices.size() + 2);
					indices.add(vertices.size() + 3);
					
					vertices.add(new Vertex(new vec3( i 	 * SPOT_WIDTH, 0, 			j * SPOT_LENGTH), new vec2(Xlower,  Ylower )));
					vertices.add(new Vertex(new vec3((i + 1) * SPOT_WIDTH, 0, 			j * SPOT_LENGTH), new vec2(Xhigher, Ylower )));
					vertices.add(new Vertex(new vec3((i + 1) * SPOT_WIDTH, SPOT_HEIGHT, j * SPOT_LENGTH), new vec2(Xhigher, Yhigher)));
					vertices.add(new Vertex(new vec3( i 	 * SPOT_WIDTH, SPOT_HEIGHT, j * SPOT_LENGTH), new vec2(Xlower,  Yhigher)));
				}
				
				if ((level.getPixel(i, j + 1) & 0xFFFFFF) == 0)
				{
					collisionPosStart.add(new vec2(i * SPOT_WIDTH, (j + 1) * SPOT_LENGTH));
					collisionPosEnd.add(new vec2((i + 1) * SPOT_WIDTH, (j + 1) * SPOT_LENGTH));
					
					indices.add(vertices.size() + 2);
					indices.add(vertices.size() + 1);
					indices.add(vertices.size() + 0);
					indices.add(vertices.size() + 3);
					indices.add(vertices.size() + 2);
					indices.add(vertices.size() + 0);
					
					vertices.add(new Vertex(new vec3( i 	 * SPOT_WIDTH, 0, 			(j + 1) * SPOT_LENGTH), new vec2(Xlower,  Ylower )));
					vertices.add(new Vertex(new vec3((i + 1) * SPOT_WIDTH, 0, 			(j + 1) * SPOT_LENGTH), new vec2(Xhigher, Ylower )));
					vertices.add(new Vertex(new vec3((i + 1) * SPOT_WIDTH, SPOT_HEIGHT, (j + 1) * SPOT_LENGTH), new vec2(Xhigher, Yhigher)));
					vertices.add(new Vertex(new vec3( i 	 * SPOT_WIDTH, SPOT_HEIGHT, (j + 1) * SPOT_LENGTH), new vec2(Xlower,  Yhigher)));
				}
				
				if ((level.getPixel(i - 1, j) & 0xFFFFFF) == 0)
				{
					collisionPosStart.add(new vec2(i * SPOT_WIDTH, j * SPOT_LENGTH));
					collisionPosEnd.add(new vec2(i * SPOT_WIDTH, (j + 1) * SPOT_LENGTH));
					
					indices.add(vertices.size() + 2);
					indices.add(vertices.size() + 1);
					indices.add(vertices.size() + 0);
					indices.add(vertices.size() + 3);
					indices.add(vertices.size() + 2);
					indices.add(vertices.size() + 0);
					
					vertices.add(new Vertex(new vec3(i * SPOT_WIDTH, 0, 		   j 	  * SPOT_LENGTH), new vec2(Xlower,  Ylower )));
					vertices.add(new Vertex(new vec3(i * SPOT_WIDTH, 0, 		  (j + 1) * SPOT_LENGTH), new vec2(Xhigher, Ylower )));
					vertices.add(new Vertex(new vec3(i * SPOT_WIDTH, SPOT_HEIGHT, (j + 1) * SPOT_LENGTH), new vec2(Xhigher, Yhigher)));
					vertices.add(new Vertex(new vec3(i * SPOT_WIDTH, SPOT_HEIGHT,  j 	  * SPOT_LENGTH), new vec2(Xlower,  Yhigher)));
				}
				
				if ((level.getPixel(i + 1, j) & 0xFFFFFF) == 0)
				{
					collisionPosStart.add(new vec2((i + 1) * SPOT_WIDTH, j * SPOT_LENGTH));
					collisionPosEnd.add(new vec2((i + 1) * SPOT_WIDTH, (j + 1) * SPOT_LENGTH));
					
					indices.add(vertices.size() + 0);
					indices.add(vertices.size() + 1);
					indices.add(vertices.size() + 2);
					indices.add(vertices.size() + 0);
					indices.add(vertices.size() + 2);
					indices.add(vertices.size() + 3);
					
					vertices.add(new Vertex(new vec3((i + 1) * SPOT_WIDTH, 0, 		     j 	    * SPOT_LENGTH), new vec2(Xlower,  Ylower )));
					vertices.add(new Vertex(new vec3((i + 1) * SPOT_WIDTH, 0, 		    (j + 1) * SPOT_LENGTH), new vec2(Xhigher, Ylower )));
					vertices.add(new Vertex(new vec3((i + 1) * SPOT_WIDTH, SPOT_HEIGHT, (j + 1) * SPOT_LENGTH), new vec2(Xhigher, Yhigher)));
					vertices.add(new Vertex(new vec3((i + 1) * SPOT_WIDTH, SPOT_HEIGHT,  j 	    * SPOT_LENGTH), new vec2(Xlower,  Yhigher)));
				}
			}
		}
		
		Vertex[] vertArray = new Vertex[vertices.size()];
		Integer[] intArray = new Integer[indices.size()];
		
		vertices.toArray(vertArray);
		indices.toArray(intArray);
		
		mesh.addVertices(vertArray, Util.toIntArray(intArray), false);
	}
	
	public void damagePlayer(int amt)
	{
		player.damage(amt);
	}
	
	public Shader getShader()
	{
		return shader;
	}
	
	public Player getPlayer()
	{
		return player;
	}
}
