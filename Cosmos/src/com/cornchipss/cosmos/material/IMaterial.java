package com.cornchipss.cosmos.material;

import org.joml.Matrix4fc;

import com.cornchipss.cosmos.shaders.Shader;

public interface IMaterial
{
	public Shader shader();
	
	public void init();
	
	public void use();
	public void stop();
	
	public void initUniforms(Matrix4fc projectionMatrix, Matrix4fc camera, Matrix4fc transform, boolean inGUI);
}
