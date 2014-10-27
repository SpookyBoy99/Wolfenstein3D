package com.xbit.engine.render.light;

import com.xbit.engine.math.vec3;

public class PointLight
{
	private BaseLight baseLight;
	private Attenuation attenuation;
	private vec3 position;
	private float range;
	
	public PointLight(BaseLight baseLight, Attenuation attenuation, vec3 position, float range)
	{
		this.baseLight = baseLight;
		this.attenuation = attenuation;
		this.position = position;
		this.range = range;
	}
	
	public BaseLight getBaseLight()
	{
		return baseLight;
	}
	
	public void setBaseLight(BaseLight baseLight)
	{
		this.baseLight = baseLight;
	}
	
	public Attenuation getAttenuation()
	{
		return attenuation;
	}
	
	public void setAttenuation(Attenuation attenuation)
	{
		this.attenuation = attenuation;
	}
	
	public vec3 getPosition()
	{
		return position;
	}
	
	public void setPosition(vec3 position)
	{
		this.position = position;
	}

	public float getRange()
	{
		return range;
	}

	public void setRange(float range)
	{
		this.range = range;
	}
}
