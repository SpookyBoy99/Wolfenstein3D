package com.xbit.engine.render;

import com.xbit.engine.math.vec3;

public class Material
{
	private Texture texture;
	private vec3 color;
	private float specularIntensity;
	private float specularPower;
	
	public Material(Texture texture)
	{
		this(texture, new vec3(1.0f, 1.0f, 1.0f));
	}
	
	public Material(Texture texture, vec3 color)
	{
		this(texture, new vec3(1.0f, 1.0f, 1.0f), 2, 32);
	}
	
	public Material(Texture texture, vec3 color, float specularIntensity, float specularPower)
	{
		this.texture = texture;
		this.color = color;
		this.specularIntensity = specularIntensity;
		this.specularPower = specularPower;
	}

	public Texture getTexture()
	{
		return texture;
	}

	public void setTexture(Texture texture)
	{
		this.texture = texture;
	}

	public vec3 getColor()
	{
		return color;
	}

	public void setColor(vec3 color)
	{
		this.color = color;
	}

	public float getSpecularIntensity()
	{
		return specularIntensity;
	}

	public void setSpecularIntensity(float specularIntensity)
	{
		this.specularIntensity = specularIntensity;
	}

	public float getSpecularPower()
	{
		return specularPower;
	}

	public void setSpecularPower(float specularPower)
	{
		this.specularPower = specularPower;
	}
}
