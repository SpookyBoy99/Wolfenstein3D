package com.xbit.engine.render.shaders;

import com.xbit.engine.base.ResourceLoader;
import com.xbit.engine.math.mat4;
import com.xbit.engine.render.Material;
import com.xbit.engine.render.RenderUtil;
import com.xbit.engine.render.Shader;

public class BasicShader extends Shader
{
	private static final BasicShader instance = new BasicShader();
	
	public static BasicShader getInstance()
	{
		return instance;
	}
	
	private BasicShader()
	{
		super();
		
		addVertexShader(ResourceLoader.loadShader("basicVertex.vs"));
		addFragmentShader(ResourceLoader.loadShader("basicFragment.fs"));
		compileShader();
		
		addUniform("transform");
		addUniform("color");
	}
	
	public void updateUniforms(mat4 worldMatrix, mat4 projectedMatrix, Material material)
	{
		if (material.getTexture() != null)
			material.getTexture().bind();
		else
			RenderUtil.unbindTextures();
		
		setUniform("transform", projectedMatrix);
		setUniform("color", material.getColor());
	}
}
