package com.xbit.engine.math;

public class vec2
{
	private float x;
	private float y;
	
	public vec2(float x, float y)
	{
		this.x = x;
		this.y = y;
	}

	public float length()
	{
		return (float)Math.sqrt(x * x + y * y);
	}
	
	public float dot (vec2 vector)
	{
		return x * vector.getX() + y * vector.getY();
	}
	
	public vec2 normalize()
	{
		float length = length();
		
		x /= length;
		y /= length;
		
		return this;
	}
	
	public vec2 rotate(float angle)
	{
		double rad = Math.toRadians(angle);
		double cos = Math.cos(rad);
		double sin = Math.sin(rad);
		
		return new vec2((float)(x * cos - y * sin), (float)(x * sin + y * cos));
	}
	
	public vec2 add(vec2 vector)
	{
		return new vec2(x + vector.getX(), y + vector.getY());
	}
	
	public vec2 add(float amount)
	{
		return new vec2(x + amount, y + amount);
	}
	
	public vec2 sub(vec2 vector)
	{
		return new vec2(x - vector.getX(), y - vector.getY());
	}
	
	public vec2 sub(float amount)
	{
		return new vec2(x - amount, y - amount);
	}
	
	public vec2 mul(vec2 vector)
	{
		return new vec2(x * vector.getX(), y * vector.getY());
	}
	
	public vec2 mul(float amount)
	{
		return new vec2(x * amount, y * amount);
	}
	
	public vec2 div(vec2 vector)
	{
		return new vec2(x / vector.getX(), y / vector.getY());
	}
	
	public vec2 div(float amount)
	{
		return new vec2(x / amount, y / amount);
	}
	
	public String toString()
	{
		return "(" + x + " " + y + ")";
	}
	
	public float getX()
	{
		return x;
	}

	public void setX(float x)
	{
		this.x = x;
	}

	public float getY()
	{
		return y;
	}

	public void setY(float y)
	{
		this.y = y;
	}
}
