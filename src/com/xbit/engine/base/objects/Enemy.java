package com.xbit.engine.base.objects;

import java.util.ArrayList;
import java.util.Random;

import com.xbit.engine.base.Game;
import com.xbit.engine.base.Player;
import com.xbit.engine.base.ResourceLoader;
import com.xbit.engine.base.Time;
import com.xbit.engine.math.Transform;
import com.xbit.engine.math.vec2;
import com.xbit.engine.math.vec3;
import com.xbit.engine.render.Material;
import com.xbit.engine.render.Mesh;
import com.xbit.engine.render.Shader;
import com.xbit.engine.render.Texture;
import com.xbit.engine.render.Vertex;

public class Enemy
{
	public static final float SCALE = 0.8f;
	public static final float SIZEY = SCALE;
	public static final float SIZEX = (float)((double)SIZEY / (1.910344827586206896551724137931 * 2));
	public static final float ZERO = 0;
	
	public static final float OFFSET_X = 0.05f;
	public static final float OFFSET_Y = 0.01f;
	public static final float OFFSET_FROM_GROUND = -0.075f;
	
	public static final float TEX_MAX_X = -1.0f - OFFSET_X;
	public static final float TEX_MIN_X =  0.0f - OFFSET_X;
	public static final float TEX_MAX_Y =  1.0f - OFFSET_Y;
	public static final float TEX_MIN_Y =  0.0f - OFFSET_Y;
	
	public static final int STATE_IDLE = 0;
	public static final int STATE_CHASE = 1;
	public static final int STATE_ATTACK = 2;
	public static final int STATE_DYING = 3;
	public static final int STATE_DEAD = 4;
	
	public static final float MOVE_SPEED = 2.0f;
	public static final float MOVEMENT_STOP_DISTANCE = 1.5f;
	public static final float ENEMY_SIZE = 0.2f;
	
	public static final float SHOOT_DISTANCE = 1000.0f;
	public static final float SHOT_ANGLE = 10.0f;
	public static final float ATTACK_CHANCE = 0.5f;
	public static final int MAX_HEALTH = 100;
	public static final int DAMAGE_MIN = 5;
	public static final int DAMAGE_MAX = 30;
	
	private static Mesh mesh;
	private static ArrayList<Texture> animations;
	
	private Material material;
	private Transform transform;
	private Random random;
	private int state;
	private int health;
	private boolean canLook;
	private boolean canAttack;
	private double deathTime;
	
	public Enemy(Transform transform)
	{		
		if (animations == null)
		{
			animations = new ArrayList<Texture>();
			
			animations.add(ResourceLoader.loadTexture("SSWVA1.png"));
			animations.add(ResourceLoader.loadTexture("SSWVB1.png"));
			animations.add(ResourceLoader.loadTexture("SSWVC1.png"));
			animations.add(ResourceLoader.loadTexture("SSWVD1.png"));
			
			animations.add(ResourceLoader.loadTexture("SSWVE0.png"));
			animations.add(ResourceLoader.loadTexture("SSWVF0.png"));
			animations.add(ResourceLoader.loadTexture("SSWVG0.png"));
			animations.add(ResourceLoader.loadTexture("SSWVH0.png"));
			
			animations.add(ResourceLoader.loadTexture("SSWVI0.png"));
			animations.add(ResourceLoader.loadTexture("SSWVJ0.png"));
			animations.add(ResourceLoader.loadTexture("SSWVK0.png"));
			animations.add(ResourceLoader.loadTexture("SSWVL0.png"));
			
			animations.add(ResourceLoader.loadTexture("SSWVM0.png"));
		}
		
		if (mesh == null)
		{
			mesh = new Mesh();
			
			Vertex[] vertices = new Vertex[]
				{
					new Vertex(new vec3(-SIZEX, ZERO,  ZERO), new vec2(TEX_MAX_X, TEX_MAX_Y)),
					new Vertex(new vec3(-SIZEX, SIZEY, ZERO), new vec2(TEX_MAX_X, TEX_MIN_Y)),
					new Vertex(new vec3( SIZEX, SIZEY, ZERO), new vec2(TEX_MIN_X, TEX_MIN_Y)),
					new Vertex(new vec3( SIZEX, ZERO,  ZERO), new vec2(TEX_MIN_X, TEX_MAX_Y)),
				};
			
			int[] indices = new int[]
				{
					0, 1, 2,
					0, 2, 3
				};
			
			mesh.addVertices(vertices, indices, false);
		}
		
		this.transform = transform;
		this.state = STATE_IDLE;
		this.health = MAX_HEALTH;
		this.material = new Material(animations.get(0));
		this.random = new Random();
		this.canLook = false;
		this.canAttack = false;
		this.deathTime = 0;
	}
	
	public void update()
	{
		transform.getTranslation().setY(OFFSET_FROM_GROUND);
		
		vec3 directionToCamera = Transform.getCamera().getPos().sub(transform.getTranslation());
		float angleToFaceTheCamera = (float)Math.toDegrees(Math.atan(directionToCamera.getZ() / directionToCamera.getX()));
		float distance = directionToCamera.length();
		vec3 orientation = directionToCamera.div(distance);
		
		if (directionToCamera.getX() < 0)
			angleToFaceTheCamera += 180;
		
		transform.getRotation().setY(angleToFaceTheCamera + 90);
		
		switch (state)
		{
		case STATE_IDLE:
			idleUpdate(orientation, distance);
			break;
		case STATE_CHASE:
			chaseUpdate(orientation, distance);
			break;
		case STATE_ATTACK:
			attackUpdate(orientation, distance);
			break;
		case STATE_DYING:
			dyingUpdate(orientation, distance);
			break;
		case STATE_DEAD:
			deadUpdate(orientation, distance);
			break;
		}
	}
	
	public void damage(int amt)
	{
		if (state == STATE_IDLE)
			state = STATE_CHASE;
		
		health -= amt;
		
		if (health <= 0)
			state = STATE_DYING;
	}
	
	private void idleUpdate(vec3 orientation, float distance)
	{
		double time = (double)Time.getTime()/(double)Time.SECOND;
		double timeDecimals = time - Math.floor(time);
		
		if (timeDecimals < 0.5)
		{
			canLook = true;
			material.setTexture(animations.get(0));
		}
		else
		{
			material.setTexture(animations.get(1));
			
			if (canLook)
			{
				vec2 lineStart = new vec2(transform.getTranslation().getX(), transform.getTranslation().getZ());
				vec2 castDirection = new vec2(orientation.getX(), orientation.getZ()).rotate((random.nextFloat() - 0.5f));
				vec2 lineEnd = lineStart.add(castDirection.mul(SHOOT_DISTANCE));
				
				vec2 collisionVector = Game.getLevel().checkIntersections(lineStart, lineEnd, false);
				
				vec2 playerIntersectVector = new vec2(Transform.getCamera().getPos().getX(), Transform.getCamera().getPos().getZ());
				
				if (playerIntersectVector != null && (collisionVector == null || playerIntersectVector.sub(lineStart).length() < collisionVector.sub(lineStart).length()))
					state = STATE_CHASE;
					
				canLook = false;
			}
		}
	}
	
	private void chaseUpdate(vec3 orientation, float distance)
	{
		double time = (double)Time.getTime()/(double)Time.SECOND;
		double timeDecimals = time - Math.floor(time);
		
		if (timeDecimals < 0.5)
		{
			material.setTexture(animations.get(0));
		}
		else if (timeDecimals < 0.25f)
		{
			material.setTexture(animations.get(1));
		}
		else if (timeDecimals < 0.72f)
		{
			material.setTexture(animations.get(2));
		}
		else
		{
			material.setTexture(animations.get(3));
		}
		
		if (random.nextDouble() < ATTACK_CHANCE * Time.getDelta())
			state = STATE_ATTACK;
		
		if (distance > MOVEMENT_STOP_DISTANCE)
		{
			float moveAmount = MOVE_SPEED * (float)Time.getDelta();
			
			vec3 oldPos = transform.getTranslation();
			vec3 newPos = transform.getTranslation().add(orientation.mul(moveAmount));
			
			vec3 collisionVector = Game.getLevel().checkCollision(oldPos, newPos, ENEMY_SIZE, ENEMY_SIZE);
			
			vec3 movementVector = collisionVector.mul(orientation);

			if (movementVector.length() > 0)
				transform.setTranslation(transform.getTranslation().add(movementVector.mul(moveAmount)));
			
			if (movementVector.sub(orientation).length() != 0)
				Game.getLevel().openDoors(transform.getTranslation(), false);
		}
		else
		{
			state = STATE_ATTACK;
		}
	}
	
	private void attackUpdate(vec3 orientation, float distance)
	{
		double time = (double)Time.getTime()/(double)Time.SECOND;
		double timeDecimals = time - Math.floor(time);
		
		if (timeDecimals < 0.25)
		{
			canAttack = true;
			material.setTexture(animations.get(5));
		}
		else if (timeDecimals < 0.5)
		{
			material.setTexture(animations.get(6));
		}
		else if (timeDecimals < 0.75)
		{
			material.setTexture(animations.get(7));
			
			if (canAttack)
			{
				vec2 lineStart = new vec2(transform.getTranslation().getX(), transform.getTranslation().getZ());
				vec2 castDirection = new vec2(orientation.getX(), orientation.getZ()).rotate((random.nextFloat() - 0.5f) * SHOT_ANGLE);
				vec2 lineEnd = lineStart.add(castDirection.mul(SHOOT_DISTANCE));
				
				vec2 collisionVector = Game.getLevel().checkIntersections(lineStart, lineEnd, false);
				
				vec2 playerIntersectVector = Game.getLevel().lineIntersectRect(lineStart, lineEnd, new vec2(Transform.getCamera().getPos().getX(), Transform.getCamera().getPos().getZ()), new vec2(Player.PLAYER_SIZE, Player.PLAYER_SIZE));
				
				if (playerIntersectVector != null && (collisionVector == null || playerIntersectVector.sub(lineStart).length() < collisionVector.sub(lineStart).length()))
					Game.getLevel().damagePlayer(random.nextInt(DAMAGE_MAX - DAMAGE_MIN) + DAMAGE_MIN);
				
				if (collisionVector == null)
					System.err.println("Error: You enemy hasnt hit anything (and that is impossible unless you cant win without hacking)");
			
				state = STATE_CHASE;
				canAttack = false;
			}
			else
			{
				material.setTexture(animations.get(5));
			}
		}
	}
	
	private void dyingUpdate(vec3 orientation, float distance)
	{
		double time = ((double)Time.getTime())/((double)Time.SECOND);
		
		if (deathTime == 0)
			deathTime = time;
		
		final float time1 = 0.1f;
		final float time2 = 0.3f;
		final float time3 = 0.45f;
		final float time4 = 0.6f;
		
		if (time < deathTime + time1)
		{
			material.setTexture(animations.get(8));
			transform.setScale(1,0.96428571428571428571428571428571f,1);
		}
		else if (time < deathTime + time2)
		{
			material.setTexture(animations.get(9));
			transform.setScale(1.7f,0.9f,1);
		}
		else if (time < deathTime + time3)
		{
			material.setTexture(animations.get(10));
			transform.setScale(1.7f,0.9f,1);
		}
		else if (time < deathTime + time4)
		{
			material.setTexture(animations.get(11));
			transform.setScale(1.7f,0.5f,1);
		}
		else
		{
			state = STATE_DEAD;
		}
	}
	
	private void deadUpdate(vec3 orientation, float distance)
	{
		material.setTexture(animations.get(12));
		transform.setScale(1.7586206896551724137931034482759f,0.28571428571428571428571428571429f,1);
	}
	
	public void render()
	{
		Shader shader = Game.getLevel().getShader();
		shader.updateUniforms(transform.getTransformation(), transform.getProjectedTransformation(), material);
		mesh.draw();
	}
	
	public Transform getTransform()
	{
		return transform;
	}
	
	public vec2 getEnemySize()
	{
		return new vec2(ENEMY_SIZE, ENEMY_SIZE);
	}
}
