package com.xbit.engine.render.shaders;

import com.xbit.engine.base.ResourceLoader;
import com.xbit.engine.math.mat4;
import com.xbit.engine.math.Transform;
import com.xbit.engine.math.vec3;
import com.xbit.engine.render.Material;
import com.xbit.engine.render.RenderUtil;
import com.xbit.engine.render.Shader;
import com.xbit.engine.render.light.BaseLight;
import com.xbit.engine.render.light.DirectionalLight;
import com.xbit.engine.render.light.PointLight;
import com.xbit.engine.render.light.SpotLight;

public class PhongShader extends Shader
{
	private static final int MAX_POINT_LIGHTS = 4;
	private static final int MAX_SPOT_LIGHTS = 4;
	private static final PhongShader instance = new PhongShader();
	
	private static vec3 ambientLight = new vec3(0.1f, 0.1f, 0.1f);
	private static DirectionalLight directionalLight = new DirectionalLight(new BaseLight(new vec3(1.0f, 1.0f, 1.0f), 0), new vec3(0.0f, 0.0f, 0.0f));
	private static PointLight[] pointLights = new PointLight[]{};
	private static SpotLight[] spotLights = new SpotLight[]{};
	
	public static PhongShader getInstance()
	{
		return instance;
	}
	
	private PhongShader()
	{
		super();
		
		addVertexShader(ResourceLoader.loadShader("phongVertex.vs"));
		addFragmentShader(ResourceLoader.loadShader("phongFragment.fs"));
		compileShader();
		
		addUniform("transform");
		addUniform("transformProjected");
		
		addUniform("baseColor");
		addUniform("ambientLight");
		
		addUniform("directionalLight.base.color");
		addUniform("directionalLight.base.intensity");
		addUniform("directionalLight.direction");
		
		for (int i = 0; i < MAX_POINT_LIGHTS; i++)
		{
			addUniform("pointLights[" + i + "].base.color");
			addUniform("pointLights[" + i + "].base.intensity");
			addUniform("pointLights[" + i + "].attenuation.constant");
			addUniform("pointLights[" + i + "].attenuation.linear");
			addUniform("pointLights[" + i + "].attenuation.exponent");
			addUniform("pointLights[" + i + "].position");
			addUniform("pointLights[" + i + "].range");
		}
		
		for (int i = 0; i < MAX_SPOT_LIGHTS; i++)
		{
			addUniform("spotLights[" + i + "].pointLight.base.color");
			addUniform("spotLights[" + i + "].pointLight.base.intensity");
			addUniform("spotLights[" + i + "].pointLight.attenuation.constant");
			addUniform("spotLights[" + i + "].pointLight.attenuation.linear");
			addUniform("spotLights[" + i + "].pointLight.attenuation.exponent");
			addUniform("spotLights[" + i + "].pointLight.position");
			addUniform("spotLights[" + i + "].pointLight.range");
			addUniform("spotLights[" + i + "]" + ".direction");
			addUniform("spotLights[" + i + "]" + ".cutoff");
		}
		
		addUniform("specularIntensity");
		addUniform("specularPower");
		
		addUniform("eyePos");
	}
	
	public void updateUniforms(mat4 worldMatrix, mat4 projectedMatrix, Material material)
	{
		if (material.getTexture() != null)
			material.getTexture().bind();
		else
			RenderUtil.unbindTextures();
		
		setUniform("transform", worldMatrix);
		setUniform("transformProjected", projectedMatrix);
		
		setUniform("baseColor", material.getColor());
		setUniform("ambientLight", ambientLight);
		
		setUniform("directionalLight", directionalLight);
		
		for (int i = 0; i < pointLights.length; i++)
		{
			setUniform("pointLights[" + i + "]", pointLights[i]);
		}
		
		for (int i = 0; i < spotLights.length; i++)
		{
			setUniform("spotLights[" + i + "]", spotLights[i]);
		}
		
		setUniformf("specularIntensity", material.getSpecularIntensity());
		setUniformf("specularPower", material.getSpecularPower());
		
		setUniform("eyePos", Transform.getCamera().getPos());
	}

	public static vec3 getAmbientLight()
	{
		return ambientLight;
	}

	public static void setAmbientLight(vec3 ambientLight)
	{
		PhongShader.ambientLight = ambientLight;
	}
	
	public static void setDirectionalLight(DirectionalLight directionalLight)
	{
		PhongShader.directionalLight = directionalLight;
	}
	
	public static void setPointLight(PointLight[] pointLights)
	{
		if (pointLights.length > MAX_POINT_LIGHTS)
		{
			System.err.println("Error: You passed in too many point lights. Max allow is " + MAX_POINT_LIGHTS + ". You passed in " + pointLights.length);
			new Exception().printStackTrace();
			System.exit(1);
		}
		
		PhongShader.pointLights = pointLights;
	}
	
	public static void setSpotLight(SpotLight[] spotLights)
	{
		if (spotLights.length > MAX_POINT_LIGHTS)
		{
			System.err.println("Error: You passed in too many spot lights. Max allow is " + MAX_SPOT_LIGHTS + ". You passed in " + spotLights.length);
			new Exception().printStackTrace();
			System.exit(1);
		}
		
		PhongShader.spotLights = spotLights;
	}
	
	public void setUniform(String uniformName, BaseLight baseLight)
	{
		setUniform(uniformName + ".color", baseLight.getColor());
		setUniformf(uniformName + ".intensity", baseLight.getIntensity());
	}
	
	public void setUniform(String uniformName, DirectionalLight directionalLight)
	{
		setUniform(uniformName + ".base", directionalLight.getBase());
		setUniform(uniformName + ".direction", directionalLight.getDirection());
	}
	
	public void setUniform(String uniformName, PointLight pointLight)
	{
		setUniform(uniformName + ".base", pointLight.getBaseLight());
		setUniformf(uniformName + ".attenuation.constant", pointLight.getAttenuation().getConstant());
		setUniformf(uniformName + ".attenuation.linear", pointLight.getAttenuation().getLinear());
		setUniformf(uniformName + ".attenuation.exponent", pointLight.getAttenuation().getExponent());
		setUniform(uniformName + ".position", pointLight.getPosition());
		setUniformf(uniformName + ".range", pointLight.getRange());
	}
	
	public void setUniform(String uniformName, SpotLight spotLight)
	{
		setUniform(uniformName + ".pointLight", spotLight.getPointLight());
		setUniform(uniformName + ".direction", spotLight.getDirection());
		setUniformf(uniformName + ".cutoff", spotLight.getCutoff());
	}
}