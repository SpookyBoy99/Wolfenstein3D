package com.xbit.engine.base;

import com.xbit.engine.math.vec3;

public class Camera
{
	public static final vec3 yAxis = new vec3(0, 1, 0);
	
	private vec3 pos;
	private vec3 forward;
	private vec3 up;
	
	public Camera()
	{
		this(new vec3(0, 0, 0), new vec3(0, 0, 1), new vec3(0, 1, 0));
	}
	
	public Camera(vec3 position)
	{
		this(position, new vec3(0, 0, 1), new vec3(0, 1, 0));
	}
	
	public Camera(vec3 pos, vec3 forward, vec3 up)
	{
		this.pos = pos;
		this.forward = forward;
		this.up = up;
		
		up.normalize();
		forward.normalize();
	}

	public void move(vec3 direction, float amount)
	{
		pos = pos.add(direction.mul(amount));
	}
	
	public void rotateY(float angle)
	{
		vec3 hAxis = yAxis.cross(forward);
		hAxis.normalize();
		
		forward.rotate(angle, yAxis);
		forward.normalize();
		
		up = forward.cross(hAxis);
		up.normalize();
	}
	
	public void rotateX(float angle)
	{
		vec3 hAxis = yAxis.cross(forward);
		hAxis.normalize();
		
		forward.rotate(angle, hAxis);
		forward.normalize();
		
		up = forward.cross(hAxis);
		up.normalize();
	}
	
	public vec3 getLeft()
	{
		vec3 left = forward.cross(up);
		left.normalize();
		return left;
	}
	
	public vec3 getRight()
	{
		vec3 right = up.cross(forward);
		right.normalize();
		return right;
	}
	
	public vec3 getPos()
	{
		return pos;
	}

	public void setPos(vec3 pos)
	{
		this.pos = pos;
	}

	public vec3 getForward()
	{
		return forward;
	}

	public void setForward(vec3 forward)
	{
		this.forward = forward;
	}

	public vec3 getUp()
	{
		return up;
	}

	public void setUp(vec3 up)
	{
		this.up = up;
	}
}
