package com.cornchipss.cosmos.gui;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.joml.Vector3fc;

import com.cornchipss.cosmos.material.Material;
import com.cornchipss.cosmos.material.RawImageMaterial;
import com.cornchipss.cosmos.rendering.Texture;

public class GUIRectangle extends GUITexture
{
	public GUIRectangle(Vector3fc position, float w, float h, Color color)
	{
		super(position, w, h, 0, 0, generateMaterial(color));
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
