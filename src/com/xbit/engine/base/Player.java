package com.xbit.engine.base;

import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2f;

import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.input.Keyboard;

import com.xbit.engine.math.Transform;
import com.xbit.engine.math.vec2;
import com.xbit.engine.math.vec3;
import com.xbit.engine.render.Material;
import com.xbit.engine.render.Mesh;
import com.xbit.engine.render.Shader;
import com.xbit.engine.render.Texture;
import com.xbit.engine.render.Vertex;

public class Player
{
	public static final float GUN_OFFSET = -0.095f;
	
	public static final float PLAYER_SIZE = 0.3f;
	public static final float SHOOT_DISTANCE = 1000.0f;
	public static final int DAMAGE_MIN = 20;
	public static final int DAMAGE_MAX = 60;
	public static final int MAX_HEALTH = 100;
	
	public static final float SCALE = 0.06f;
	public static final float SIZEY = SCALE;
	public static final float SIZEX = (float)((double)SIZEY / (1.0379746835443037974683544303797 * 2));
	public static final float ZERO = 0;
	
	public static final float OFFSET_X = 0.05f;
	public static final float OFFSET_Y = 0.01f;
	public static final float OFFSET_FROM_GROUND = -0.075f;
	
	public static final float TEX_MAX_X = -1.0f - OFFSET_X;
	public static final float TEX_MIN_X =  0.0f - OFFSET_X;
	public static final float TEX_MAX_Y =  1.0f - OFFSET_Y;
	public static final float TEX_MIN_Y =  0.0f - OFFSET_Y;
	
	private static final float MOUSE_SENSIVITY = 0.2f;
	private static final float MOVE_SPEED = 7.0f;
	private static final vec3 zeroVector = new vec3(0, 0, 0);
	
	private Camera camera;
	private Random random;
	private static Mesh gunMesh;
	private static Mesh healthMesh;
	private static ArrayList<Texture> textures;
	private Material material;
	private Transform transform;
	
	private static boolean mouseLocked = false;
	private vec2 centerPosition = new vec2(Window.getWidth() / 2, Window.getHeight() / 2);
	private vec3 movementVector;
	private int health;

	public Player(vec3 position)
	{
		if (gunMesh == null)
		{
			gunMesh = new Mesh();
			
			Vertex[] vertices = new Vertex[]
				{
					new Vertex(new vec3(-SIZEX, ZERO,  ZERO), new vec2(TEX_MAX_X + 0.5f, TEX_MAX_Y)),
					new Vertex(new vec3(-SIZEX, SIZEY, ZERO), new vec2(TEX_MAX_X + 0.5f, TEX_MIN_Y)),
					new Vertex(new vec3( SIZEX, SIZEY, ZERO), new vec2(TEX_MIN_X, TEX_MIN_Y)),
					new Vertex(new vec3( SIZEX, ZERO,  ZERO), new vec2(TEX_MIN_X, TEX_MAX_Y)),
				};
			
			int[] indices = new int[]
				{
					0, 1, 2,
					0, 2, 3
				};
			
			gunMesh.addVertices(vertices, indices, false);
		}
		
		if (healthMesh == null)
		{
			healthMesh = new Mesh();
			
			Vertex[] vertices = new Vertex[]
				{
					new Vertex(new vec3(-SIZEX * 0.5f + 0.06f, ZERO * 0.5f + 0.03f,  ZERO * 0.5f), new vec2(TEX_MAX_X, TEX_MAX_Y)),
					new Vertex(new vec3(-SIZEX * 0.5f + 0.06f, SIZEY * 0.5f + 0.03f, ZERO * 0.5f), new vec2(TEX_MAX_X, TEX_MIN_Y)),
					new Vertex(new vec3( SIZEX * 0.5f + 0.06f, SIZEY * 0.5f + 0.03f, ZERO * 0.5f), new vec2(TEX_MIN_X - 0.5f, TEX_MIN_Y)),
					new Vertex(new vec3( SIZEX * 0.5f + 0.06f, ZERO * 0.5f + 0.03f,  ZERO * 0.5f), new vec2(TEX_MIN_X - 0.5f, TEX_MAX_Y)),
				};
			
			int[] indices = new int[]
				{
					0, 1, 2,
					0, 2, 3
				};
			
			healthMesh.addVertices(vertices, indices, false);
		}
		
		if (textures == null)
		{
			textures = new ArrayList<Texture>();
			
			textures.add(ResourceLoader.loadTexture("GUI0.png"));
			textures.add(ResourceLoader.loadTexture("GUI1.png"));
			textures.add(ResourceLoader.loadTexture("GUI2.png"));
			textures.add(ResourceLoader.loadTexture("GUI3.png"));
			textures.add(ResourceLoader.loadTexture("GUI4.png"));
			textures.add(ResourceLoader.loadTexture("GUI5.png"));
			textures.add(ResourceLoader.loadTexture("GUI6.png"));
			textures.add(ResourceLoader.loadTexture("GUI7.png"));
		}
		
		this.camera = new Camera(position);
		this.random = new Random();
		this.health = MAX_HEALTH;
		this.material = new Material(textures.get(0));
		this.transform = new Transform(new vec3(4, 0, 29), new vec3(0, 0, 0), new vec3(1, 1, 1));
		this.movementVector = zeroVector; 
	}
	
	public void damage(int amt)
	{
		health -= amt;
		
		if (health > MAX_HEALTH)
			health = MAX_HEALTH;
			
		if ((int)Math.floor(MAX_HEALTH / health) <= 7)
			material.setTexture(textures.get((int)Math.floor(MAX_HEALTH / health)));
		else
			material.setTexture(textures.get(7));
		
		if (health <= 0)
		{
			Game.setIsRunning(false);
		}
	}
	
	public void input()
	{
		movementVector = zeroVector;
		
		if (Input.getKey(Keyboard.KEY_W))
			movementVector = movementVector.add(camera.getForward());//camera.move(camera.getForward(), movAmt);
		if (Input.getKey(Keyboard.KEY_S))
			movementVector = movementVector.sub(camera.getForward());//camera.move(camera.getForward(), -movAmt);
		if (Input.getKey(Keyboard.KEY_A))
			movementVector = movementVector.add(camera.getLeft());//camera.move(camera.getLeft(), movAmt);
		if (Input.getKey(Keyboard.KEY_D))
			movementVector = movementVector.add(camera.getRight());//camera.move(camera.getRight(), movAmt);

		if (Input.getKey(Keyboard.KEY_ESCAPE))
		{
			Input.setCursor(true);
			mouseLocked = false;
		}
		if (Input.getMouseDown(0))
		{
			if (!mouseLocked)
			{
				Input.setMousePosition(centerPosition);
				Input.setCursor(false);
				mouseLocked = true;
			}
			else
			{
				vec2 lineStart = new vec2(camera.getPos().getX(), camera.getPos().getZ());
				vec2 castDirection = new vec2(camera.getForward().getX(), camera.getForward().getZ());
				vec2 lineEnd = lineStart.add(castDirection.mul(SHOOT_DISTANCE));
			
				Game.getLevel().checkIntersections(lineStart, lineEnd, true);
			}
		}
		
		if (mouseLocked)
		{
			vec2 deltaPos = Input.getMousePosition().sub(centerPosition);
			
			boolean rotY = deltaPos.getX() != 0;
			boolean rotX = deltaPos.getY() != 0;
			
			if (rotY)
				camera.rotateY(deltaPos.getX() * MOUSE_SENSIVITY);
			if (rotX)
				camera.rotateX(-deltaPos.getY() * MOUSE_SENSIVITY);
			
			if (rotY || rotX)
				Input.setMousePosition(centerPosition);
		}
	}
	
	public void update()
	{
		float movAmt = (float)(MOVE_SPEED * Time.getDelta());
		
		movementVector.setY(0);
		if (movementVector.length() > 0)
			movementVector = movementVector.normalize();
		
		vec3 oldPos = camera.getPos();
		vec3 newPos = oldPos.add(movementVector.mul(movAmt));
		
		vec3 collisionVector = Game.getLevel().checkCollision(oldPos, newPos, PLAYER_SIZE, PLAYER_SIZE);
		movementVector = movementVector.mul(collisionVector);
		
		if (movementVector.length() > 0)
			camera.move(movementVector, movAmt);
		
		//Gun movement
		transform.setTranslation(camera.getPos().add(camera.getForward().normalize().mul(0.105f)));
		transform.getTranslation().setY(transform.getTranslation().getY() + GUN_OFFSET);
		
		vec3 directionToCamera = Transform.getCamera().getPos().sub(transform.getTranslation());
		float angleToFaceTheCameraY = (float)Math.toDegrees(Math.atan(directionToCamera.getZ() / directionToCamera.getX()));
		
		if (directionToCamera.getX() < 0)
			angleToFaceTheCameraY += 180;
		
		transform.getRotation().setY(angleToFaceTheCameraY + 90);
	}
	
    public void guiRender()
    {
		glDisable(GL_LIGHTING);
		glColor3f(1f, .5f, 1f);
		// draw quad
		glBegin(GL_QUADS);
		glVertex2f(0, 0);
		glVertex2f(1, 0);
		glVertex2f(1, 1);
		glVertex2f(0, 1);
		glEnd();
		glEnable(GL_LIGHTING);
    }
	
	public void render()
	{
		Shader shader = Game.getLevel().getShader();
		
		shader.updateUniforms(transform.getTransformation(), transform.getProjectedTransformation(), material);
		
		guiRender();
		
		gunMesh.draw();
		healthMesh.draw();
	}

	public Camera getCamera()
	{
		return camera;
	}
	
	public int getDamage()
	{
		return random.nextInt(DAMAGE_MAX - DAMAGE_MIN) + DAMAGE_MIN;
	}

	public int getHealth()
	{
		return health;
	}
	
	public int getMaxHealth()
	{
		return MAX_HEALTH;
	}
}
