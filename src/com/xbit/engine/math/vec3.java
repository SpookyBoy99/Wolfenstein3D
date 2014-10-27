package com.xbit.engine.math;

public class vec3
{
	private float x;
	private float y;
	private float z;
	
	public vec3(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public float length()
	{
		return (float)Math.sqrt(x * x + y * y + z * z);
	}
	
	public float dot (vec3 vector)
	{
		return x * vector.getX() + y * vector.getY() + z * vector.getZ();
	}
	
	public vec3 normalize()
	{
		float length = length();
		
		x /= length;
		y /= length;
		z /= length;
		
		return this;
	}
	
	public vec3 cross(vec3 vector)
	{
		float x_ = y * vector.getZ() - z * vector.getY();
		float y_ = z * vector.getX() - x * vector.getZ();
		float z_ = x * vector.getY() - y * vector.getX();
		
		return new vec3(x_, y_, z_);
	}
	
	public vec3 rotate(float angle, vec3 axis)
	{
		float sinHalfAngle = (float)Math.sin(Math.toRadians(angle / 2));
		float cosHalfAngle = (float)Math.cos(Math.toRadians(angle / 2));
		
		float rX = axis.getX() * sinHalfAngle;
		float rY = axis.getY() * sinHalfAngle;
		float rZ = axis.getZ() * sinHalfAngle;
		float rW = cosHalfAngle;
		
		Quaternion rotation = new Quaternion(rX, rY, rZ, rW);
		Quaternion conjugate = rotation.conjugate();
		Quaternion w = rotation.mul(this).mul(conjugate);
		
		x = w.getX();
		y = w.getY();
		z = w.getZ();
		
		return this;
	}
	
	public vec3 add(vec3 vector)
	{
		return new vec3(x + vector.getX(), y + vector.getY(), z + vector.getZ());
	}
	
	public vec3 add(float amount)
	{
		return new vec3(x + amount, y + amount, z + amount);
	}
	
	public vec3 sub(vec3 vector)
	{
		return new vec3(x - vector.getX(), y - vector.getY(), z - vector.getZ());
	}
	
	public vec3 sub(float amount)
	{
		return new vec3(x - amount, y - amount, z - amount);
	}
	
	public vec3 mul(vec3 vector)
	{
		return new vec3(x * vector.getX(), y * vector.getY(), z * vector.getZ());
	}
	
	public vec3 mul(float amount)
	{
		return new vec3(x * amount, y * amount, z * amount);
	}
	
	public vec3 div(vec3 vector)
	{
		return new vec3(x / vector.getX(), y / vector.getY(), z / vector.getZ());
	}
	
	public vec3 div(float amount)
	{
		return new vec3(x / amount, y / amount, z / amount);
	}
	
	public String toString()
	{
		return "(" + x + " " + y + " " + z + ")";
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

	public float getZ()
	{
		return z;
	}

	public void setZ(float z)
	{
		this.z = z;
	}
}
