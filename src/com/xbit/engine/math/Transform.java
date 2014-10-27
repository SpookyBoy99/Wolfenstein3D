package com.xbit.engine.math;

import com.xbit.engine.base.Camera;


public class Transform
{
	private static Camera camera;
	
	private static float zNear;
	private static float zFar;
	private static float width;
	private static float height;
	private static float fov;
	
	private vec3 translation;
	private vec3 rotation;
	private vec3 scale;
	
	public Transform()
	{
		translation = new vec3(0, 0, 0);
		rotation = new vec3(0, 0, 0);
		scale = new vec3(1, 1, 1);
	}
	
	public Transform(vec3 translation, vec3 rotation, vec3 scale)
	{
		this.translation = translation;
		this.rotation = rotation;
		this.scale = scale;
	}

	public mat4 getTransformation()
	{
		mat4 translation = new mat4().initTranslation(this.translation.getX(), this.translation.getY(), this.translation.getZ());
		mat4 rotation = new mat4().initRotation(this.rotation.getX(), this.rotation.getY(), this.rotation.getZ());
		mat4 scale = new mat4().initScale(this.scale.getX(), this.scale.getY(), this.scale.getZ());
		
		return translation.mul(rotation.mul(scale));
	}
	
	public mat4 getProjectedTransformation()
	{
		mat4 transformation = getTransformation();
		mat4 projection = new mat4().initProjection(fov, width, height, zNear, zFar);
		mat4 cameraRotation = new mat4().initCamera(camera.getForward(), camera.getUp());
		mat4 cameraTranslation = new mat4().initTranslation(-camera.getPos().getX(), -camera.getPos().getY(), -camera.getPos().getZ());
		
		return projection.mul(cameraRotation.mul(cameraTranslation.mul(transformation)));
	}
	
	public static void setProjection(float fov, float width, float height, float zNear, float zFar)
	{
		Transform.fov = fov;
		Transform.width = width;
		Transform.height = height;
		Transform.zNear = zNear;
		Transform.zFar = zFar;
	}

	
	public vec3 getTranslation()
	{
		return translation;
	}

	public void setTranslation(vec3 translation)
	{
		this.translation = translation;
	}
	
	public void setTranslation(float x, float y, float z)
	{
		this.translation = new vec3(x, y, z);
	}

	public vec3 getRotation()
	{
		return rotation;
	}

	public void setRotation(vec3 rotation)
	{
		this.rotation = rotation;
	}
	
	public void setRotation(float x, float y, float z)
	{
		this.rotation = new vec3(x, y, z);
	}

	public vec3 getScale()
	{
		return scale;
	}

	public void setScale(vec3 scale)
	{
		this.scale = scale;
	}
	
	public void setScale(float x, float y, float z)
	{
		this.scale = new vec3(x, y, z);
	}

	public static Camera getCamera()
	{
		return camera;
	}

	public static void setCamera(Camera camera)
	{
		Transform.camera = camera;
	}
}
