package com.xbit.engine.render.light;

import com.xbit.engine.math.vec3;

public class SpotLight
{
	private PointLight pointLight;
	private vec3 direction;
	private float cutoff;
	
	public SpotLight(PointLight pointLight, vec3 direction, float cutoff)
	{
		this.pointLight = pointLight;
		this.direction = direction.normalize();
		this.cutoff = cutoff;
	}
	
	public PointLight getPointLight()
	{
		return pointLight;
	}
	public void setPointLight(PointLight pointLight)
	{
		this.pointLight = pointLight;
	}
	
	public vec3 getDirection()
	{
		return direction;
	}
	
	public void setDirection(vec3 direction)
	{
		this.direction = direction;
	}
	
	public float getCutoff()
	{
		return cutoff;
	}
	
	public void setCutoff(float cutoff)
	{
		this.cutoff = cutoff;
	}
}
