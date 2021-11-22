package com.cornchipss.cosmos.rendering;

import com.cornchipss.cosmos.material.TexturedMaterial;

/**
 * It's a mesh and a material.
 */
public class MaterialMesh
{
	private TexturedMaterial mat;
	private Mesh mesh;

	public MaterialMesh(TexturedMaterial mat, Mesh mesh)
	{
		this.mat = mat;
		this.mesh = mesh;
	}

	public TexturedMaterial material()
	{
		return mat;
	}

	public Mesh mesh()
	{
		return mesh;
	}
}
