package com.cornchipss.cosmos.models;

import com.cornchipss.cosmos.rendering.Mesh;

public interface Model
{
	public Mesh createMesh(float offX, float offY, float offZ, float scale);
}
