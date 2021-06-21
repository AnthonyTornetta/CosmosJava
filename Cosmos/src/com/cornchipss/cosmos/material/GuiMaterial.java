package com.cornchipss.cosmos.material;

import org.joml.Matrix4fc;

public class GuiMaterial extends Material
{
	private int guiProjLoc, guiTransLoc;
	
	public GuiMaterial()
	{
		super("assets/shaders/gui", "assets/images/atlas/gui");
	}
	
	public GuiMaterial(String atlas)
	{
		super("assets/shaders/gui", atlas);
	}

	@Override
	public void initUniforms(Matrix4fc projectionMatrix, Matrix4fc cam, Matrix4fc transform, boolean isGUI)
	{
		shader().setUniformMatrix(guiTransLoc, transform);
		shader().setUniformMatrix(guiProjLoc, projectionMatrix);
	}

	@Override
	protected void initShader()
	{
		guiTransLoc = shader().uniformLocation("u_transform");
		guiProjLoc = shader().uniformLocation("u_projection");
	}

	@Override
	public float uvWidth()
	{
		return 16.0f / 64.0f;
	}

	@Override
	public float uvHeight()
	{
		return 16.0f / 64.0f;
	}
}