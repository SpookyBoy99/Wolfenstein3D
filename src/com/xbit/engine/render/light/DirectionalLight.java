package com.xbit.engine.render.light;

import com.xbit.engine.math.vec3;

public class DirectionalLight
{
	private BaseLight base;
	private vec3 direction;
	
	public DirectionalLight(BaseLight base, vec3 direction)
	{
		this.base = base;
		this.direction = direction.normalize();
	}

	public BaseLight getBase()
	{
		return base;
	}

	public void setBase(BaseLight base)
	{
		this.base = base;
	}

	public vec3 getDirection()
	{
		return direction;
	}

	public void setDirection(vec3 direction)
	{
		this.direction = direction;
	}
}
