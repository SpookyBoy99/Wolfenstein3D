package com.xbit.engine.base.objects;

import com.xbit.engine.base.Game;
import com.xbit.engine.base.Player;
import com.xbit.engine.base.ResourceLoader;
import com.xbit.engine.math.Transform;
import com.xbit.engine.math.vec2;
import com.xbit.engine.math.vec3;
import com.xbit.engine.render.Material;
import com.xbit.engine.render.Mesh;
import com.xbit.engine.render.Shader;
import com.xbit.engine.render.Vertex;

public class MedKit
{
	public static final float PICKUP_DISTANCE = 0.5f;
	public static final int HEAL_AMOUNT = 25;
	
	public static final float SCALE = 0.4f;
	public static final float SIZEY = SCALE;
	public static final float SIZEX = (float)((double)SIZEY / (0.67857142857142857142857142857143 * 3.0));
	public static final float ZERO = 0;
	
	public static final float OFFSET_X = 0.05f;
	public static final float OFFSET_Y = 0.07f;
	public static final float OFFSET_FROM_GROUND = -0.75f;
	
	public static final float TEX_MAX_X = -1.0f - OFFSET_X;
	public static final float TEX_MIN_X =  0.0f - OFFSET_X;
	public static final float TEX_MAX_Y =  1.0f - OFFSET_Y;
	public static final float TEX_MIN_Y =  0.0f - OFFSET_Y;
	
	private static Mesh mesh;
	private static Material material;
	
	private Transform transform;
	
	public MedKit(vec3 position)
	{
		transform = new Transform();
		transform.setTranslation(position);
		
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
		
		if (material == null)
		{
			material = new Material(ResourceLoader.loadTexture("MEDIA0.png"));
		}
	}
	
	public void update()
	{
		vec3 directionToCamera = Transform.getCamera().getPos().sub(transform.getTranslation());
		float angleToFaceTheCamera = (float)Math.toDegrees(Math.atan(directionToCamera.getZ() / directionToCamera.getX()));
		
		if (directionToCamera.getX() < 0)
			angleToFaceTheCamera += 180;
		
		transform.getRotation().setY(angleToFaceTheCamera + 90);
		
		if (directionToCamera.length() < PICKUP_DISTANCE)
		{
			Player player = Game.getLevel().getPlayer();
			
			if (player.getHealth() < player.getMaxHealth())
			{
				Game.getLevel().removeMedkits(this);
				player.damage(-HEAL_AMOUNT);
			}
			
		}
	}
	
	public void render()
	{
		Shader shader = Game.getLevel().getShader();
		shader.updateUniforms(transform.getTransformation(), transform.getProjectedTransformation(), material);
		mesh.draw();
	}
}
