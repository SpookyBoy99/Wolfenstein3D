package com.xbit.engine.math;


public class Quaternion
{
	private float x;
	private float y;
	private float z;
	private float w;
	
	public Quaternion(float x, float y, float z, float w)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	public float length()
	{
		return (float)Math.sqrt(x * x + y * y + z * z + w * w);
	}
	
	public Quaternion normalize()
	{
		float length = length();
		
		x /= length;
		y /= length;
		z /= length;
		w /= length;
		
		return this;
	}
	
	public Quaternion conjugate()
	{
		return new Quaternion(-x, -y, -z, w);
	}
	
	public Quaternion mul(Quaternion quaternion)
	{
		float w_ = w * quaternion.getW() - x * quaternion.getX() - y * quaternion.getY() - z * quaternion.getZ();
		float x_ = x * quaternion.getW() + w * quaternion.getX() + y * quaternion.getZ() - z * quaternion.getY();
		float y_ = y * quaternion.getW() + w * quaternion.getY() + z * quaternion.getX() - x * quaternion.getZ();
		float z_ = z * quaternion.getW() + w * quaternion.getZ() + x * quaternion.getY() - y * quaternion.getX();
		
		return new Quaternion(x_, y_, z_, w_);
	}
	
	public Quaternion mul(vec3 vector)
	{
		float w_ = -x * vector.getX() - y * vector.getY() - z * vector.getZ();
		float x_ =  w * vector.getX() + y * vector.getZ() - z * vector.getY();
		float y_ =  w * vector.getY() + z * vector.getX() - x * vector.getZ();
		float z_ =  w * vector.getZ() + x * vector.getY() - y * vector.getX();
		
		return new Quaternion(x_, y_, z_, w_);
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

	public float getW()
	{
		return w;
	}

	public void setW(float w)
	{
		this.w = w;
	}
}
