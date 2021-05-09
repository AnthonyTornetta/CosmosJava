package com.cornchipss.cosmos.material;

import org.joml.Matrix4fc;

import com.cornchipss.cosmos.shaders.Shader;

public class RawImageMaterial extends Material
{
	private int guiProjLoc, guiTransLoc, camLoc;
	
	private static Shader shader;
	
	/**
	 * A raw image
	 * @param image The image w/out the .png ending
	 */
	public RawImageMaterial(String image)
	{
		super((Shader)null, image);
		
		if(shader == null)
			shader = new Shader("assets/shaders/image");
		
		shader(shader);
	}

	@Override
	public void initUniforms(Matrix4fc projectionMatrix, Matrix4fc cam, Matrix4fc transform, boolean isGUI)
	{
		shader().setUniformMatrix(guiTransLoc, transform);
		shader().setUniformMatrix(guiProjLoc, projectionMatrix);
		shader().setUniformMatrix(camLoc, cam);
	}

	@Override
	protected void initShader()
	{
		guiTransLoc = shader().uniformLocation("u_transform");
		guiProjLoc = shader().uniformLocation("u_projection");
		camLoc = shader().uniformLocation("u_camera");
	}

	@Override
	public float uvWidth()
	{
		return 1;
	}

	@Override
	public float uvHeight()
	{
		return 1;
	}
}
