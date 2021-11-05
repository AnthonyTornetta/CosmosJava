package com.cornchipss.cosmos.gui;

import java.awt.Color;
import java.awt.image.BufferedImage;

import com.cornchipss.cosmos.gui.measurement.MeasurementPair;
import com.cornchipss.cosmos.material.Material;
import com.cornchipss.cosmos.material.RawImageMaterial;
import com.cornchipss.cosmos.rendering.Texture;

public class GUIRectangle extends GUITexture
{
	public GUIRectangle(MeasurementPair position, MeasurementPair dimensions, Color color)
	{
		super(position, dimensions, 0, 0, generateMaterial(color));
	}

	private static Material generateMaterial(Color c)
	{
		BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		bi.setRGB(0, 0, c.getRGB());

		Texture texture = Texture.loadTexture(bi);
		Material mat = new RawImageMaterial(texture);
		mat.init();

		return mat;
	}
}
