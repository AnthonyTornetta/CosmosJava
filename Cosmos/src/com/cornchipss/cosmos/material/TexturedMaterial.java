package com.cornchipss.cosmos.material;

import com.cornchipss.cosmos.rendering.Texture;
import com.cornchipss.cosmos.shaders.Shader;

public abstract class TexturedMaterial extends Material
{
	private String textureLoc;

	private Texture texture;

	public TexturedMaterial(String shaderLoc, String textureLoc)
	{
		super(shaderLoc);

		this.textureLoc = textureLoc;
	}

	public TexturedMaterial(Shader s, String t)
	{
		super(s);

		this.textureLoc = t;
	}

	public TexturedMaterial(Shader s, Texture texture)
	{
		super(s);

		this.texture = texture;
	}

	public Texture texture()
	{
		return texture;
	}

	public void bindTexture()
	{
		texture.bind();
	}

	@Override
	public void use()
	{
		super.use();
		bindTexture();
	}

	@Override
	public void stop()
	{
		super.stop();
		Texture.unbind();
	}

	@Override
	public void init()
	{
		super.init();

		if (texture == null)
			texture = Texture.loadTexture(textureLoc);
	}

	public abstract float uLength();

	public abstract float vLength();
}
