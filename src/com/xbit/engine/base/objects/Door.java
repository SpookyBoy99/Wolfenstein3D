package com.xbit.engine.base.objects;

import com.xbit.engine.base.Game;
import com.xbit.engine.base.Time;
import com.xbit.engine.math.Transform;
import com.xbit.engine.math.vec2;
import com.xbit.engine.math.vec3;
import com.xbit.engine.render.Material;
import com.xbit.engine.render.Mesh;
import com.xbit.engine.render.Shader;
import com.xbit.engine.render.Vertex;

public class Door
{
	public static final float LENGTH = 1;
	public static final float HEIGTH = 1;
	public static final float WIDTH = 0.125f;
	public static final float ZERO = 0;
	public static final double TIME_TO_OPEN = 1.0f;
	public static final double CLOSE_DELAY = 2.0f;
	
	private static Mesh mesh;
	private Material material;
	private Transform transform;
	
	private vec3 openPosition;
	private vec3 closePosition;
	
	private boolean isOpening;
	private double openingStartTime;
	private double openTime;
	private double closingStartTime;
	private double closeTime;
	
	public Door(Transform transform, Material material, vec3 openPosition)
	{
		this.material = material;
		this.transform = transform;
		this.isOpening = false;
		this.closePosition = transform.getTranslation();
		this.openPosition = openPosition;
		
		if (mesh == null)
		{
			mesh = new Mesh();
			
			Vertex[] vertices = new Vertex[]
				{
					new Vertex(new vec3(ZERO,   ZERO,   ZERO ), new vec2(1.00f, 0.25f)),
					new Vertex(new vec3(ZERO,   HEIGTH, ZERO ), new vec2(1.00f, 0.00f)),
					new Vertex(new vec3(LENGTH, HEIGTH, ZERO ), new vec2(0.75f, 0.00f)),
					new Vertex(new vec3(LENGTH, ZERO,   ZERO ), new vec2(0.75f, 0.25f)),
					
					new Vertex(new vec3(ZERO,   ZERO,   ZERO ), new vec2(0.97f, 0.25f)),
					new Vertex(new vec3(ZERO,   HEIGTH, ZERO ), new vec2(0.97f, 0.00f)),
					new Vertex(new vec3(ZERO,   HEIGTH, WIDTH), new vec2(1.00f, 0.00f)),
					new Vertex(new vec3(ZERO,   ZERO,   WIDTH), new vec2(1.00f, 0.25f)),
				
					new Vertex(new vec3(ZERO,   ZERO,   WIDTH), new vec2(1.00f, 0.25f)),
					new Vertex(new vec3(ZERO,   HEIGTH, WIDTH), new vec2(1.00f, 0.00f)),
					new Vertex(new vec3(LENGTH, HEIGTH, WIDTH), new vec2(0.75f, 0.00f)),
					new Vertex(new vec3(LENGTH, ZERO,   WIDTH), new vec2(0.75f, 0.25f)),
					                                                                 
					new Vertex(new vec3(LENGTH, ZERO,   ZERO ), new vec2(0.97f, 0.25f)),
					new Vertex(new vec3(LENGTH, HEIGTH, ZERO ), new vec2(0.97f, 0.00f)),
					new Vertex(new vec3(LENGTH, HEIGTH, WIDTH), new vec2(1.00f, 0.00f)),
					new Vertex(new vec3(LENGTH, ZERO,   WIDTH), new vec2(1.00f, 0.25f))
				};
			
			int[] indices = new int[]
				{
					0, 1, 2,
					0, 2, 3,
					
					6, 5, 4,
					7, 6, 4,
					
					10, 9, 8,
					11, 10, 8,
					
					12, 13, 14,
					12, 14, 15
				};
			
			mesh.addVertices(vertices, indices, false);
		}
	}
	
	public void update()
	{
		if (isOpening)
		{
			double time = (double)Time.getTime() / (double)Time.SECOND;
			
			if (time < openTime)
			{
				float lerpFactor = (float)((time - openingStartTime) / TIME_TO_OPEN);
				getTransform().setTranslation(VectorLerp(closePosition, openPosition, lerpFactor));
			}
			else if (time < closingStartTime)
			{
				getTransform().setTranslation(openPosition);
			}
			else if (time < closeTime)
			{
				float lerpFactor = (float)((time - closingStartTime) / TIME_TO_OPEN);
				getTransform().setTranslation(VectorLerp(openPosition, closePosition, lerpFactor));
			}
			else
			{
				getTransform().setTranslation(closePosition);
				isOpening = false;
			}
		}
	}
	
	public void render()
	{
		Shader shader = Game.getLevel().getShader();
		shader.updateUniforms(transform.getTransformation(), transform.getProjectedTransformation(), material);
		mesh.draw();
	}
	
	private vec3 VectorLerp(vec3 startPos, vec3 endPos, float lerpFactor)
	{
		return startPos.add(endPos.sub(startPos).mul(lerpFactor));
	}

	public void open()
	{
		if (isOpening)
			return;
		
		openingStartTime = (double)Time.getTime() / (double)Time.SECOND;
		openTime = openingStartTime + TIME_TO_OPEN;
		closingStartTime = openTime + CLOSE_DELAY;
		closeTime = closingStartTime + TIME_TO_OPEN;
		
		isOpening = true;
	}
	
	public vec2 getDoorSize()
	{
		if (getTransform().getRotation().getY() == 90)
			return new vec2(Door.WIDTH, Door.LENGTH);
		else
			return new vec2(Door.LENGTH, Door.WIDTH);
	}
	
	public Transform getTransform()
	{
		return transform;
	}
}
