package com.xbit.engine.render.light;

import com.xbit.engine.math.vec3;

public class BaseLight
{
	private vec3 color;
	private float intensity;
	
	public BaseLight(vec3 color, float intensity)
	{
		this.color = color;
		this.intensity = intensity;
	}

	public vec3 getColor()
	{
		return color;
	}

	public void setColor(vec3 color)
	{
		this.color = color;
	}

	public float getIntensity()
	{
		return intensity;
	}

	public void setIntensity(float intensity)
	{
		this.intensity = intensity;
	}
}
