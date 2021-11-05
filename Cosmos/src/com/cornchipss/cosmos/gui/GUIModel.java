package com.cornchipss.cosmos.gui;

import com.cornchipss.cosmos.blocks.BlockFace;
import com.cornchipss.cosmos.gui.measurement.MeasurementPair;
import com.cornchipss.cosmos.gui.measurement.PixelMeasurement;
import com.cornchipss.cosmos.material.Material;
import com.cornchipss.cosmos.models.CubeModel;
import com.cornchipss.cosmos.rendering.Mesh;

public class GUIModel extends GUIElement
{
	private Mesh mesh;
	private Material mat;

	public GUIModel(MeasurementPair position, float scale, CubeModel model)
	{
		this(position, scale, model.createMesh(0, 0, -1, scale, BlockFace.FRONT), model.material());
	}

	public GUIModel(MeasurementPair position, float scale, Mesh m, Material mat)
	{
		super(position, new MeasurementPair(new PixelMeasurement(scale), new PixelMeasurement(scale)), 1);

		this.mesh = m;
		this.mat = mat;
	}

	@Override
	public Mesh guiMesh()
	{
		return mesh;
	}

	@Override
	public Material material()
	{
		return mat;
	}
}
