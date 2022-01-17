package com.cornchipss.cosmos.material;

import com.cornchipss.cosmos.shaders.Shader;

public abstract class Material implements IMaterial
{
	private Shader shader;

	public Material(String shaderLoc)
	{
		this(new Shader(shaderLoc));
	}

	public Material(Shader s)
	{
		shader = s;
	}

	/**
	 * Used to get the uniform locations
	 */
	protected abstract void initShader();

	@Override
	public Shader shader()
	{
		return shader;
	}

	protected void shader(Shader s)
	{
		shader = s;
	}

	public void useShader()
	{
		shader.use();
	}

	public void stopShader()
	{
		shader.stop();
	}

	@Override
	public void use()
	{
		shader.use();
	}

	@Override
	public void stop()
	{
		shader.stop();
	}

	@Override
	public void init()
	{
		shader.init();
		initShader();
	}
}
