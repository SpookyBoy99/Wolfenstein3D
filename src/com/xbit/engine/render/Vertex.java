package com.xbit.engine.render;

import com.xbit.engine.math.vec2;
import com.xbit.engine.math.vec3;

public class Vertex
{
	public static final int SIZE = 8;
	
	private vec3 pos;
	private vec2 texCoord;
	private vec3 normal;
	
	public Vertex(vec3 pos)
	{
		this(pos, new vec2(0.0f, 0.0f), new vec3(0.0f, 0.0f, 0.0f));
	}
	
	public Vertex(vec3 pos, vec2 texCoord)
	{
		this(pos, texCoord, new vec3(0.0f, 0.0f, 0.0f));
	}
	
	public Vertex(vec3 pos, vec2 texCoord, vec3 normal)
	{
		this.pos = pos;
		this.texCoord = texCoord;
		this.normal = normal;
	}

	public vec3 getPos()
	{
		return pos;
	}

	public void setPos(vec3 pos)
	{
		this.pos = pos;
	}

	public vec2 getTexCoord()
	{
		return texCoord;
	}

	public void setTexCoord(vec2 texCoord)
	{
		this.texCoord = texCoord;
	}

	public vec3 getNormal()
	{
		return normal;
	}

	public void setNormal(vec3 normal)
	{
		this.normal = normal;
	}
}