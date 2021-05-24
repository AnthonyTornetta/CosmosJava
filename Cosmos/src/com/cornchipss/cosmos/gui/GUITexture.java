package com.cornchipss.cosmos.gui;

import com.cornchipss.cosmos.gui.measurement.MeasurementPair;
import com.cornchipss.cosmos.material.Material;
import com.cornchipss.cosmos.material.Materials;
import com.cornchipss.cosmos.rendering.Mesh;
import com.cornchipss.cosmos.rendering.Window;

public class GUITexture extends GUIElement
{
	public static final int[] indices = new int[]
			{
				0, 1, 3,
				1, 2, 3
			};
	
	public static float[] makeVerts(float w, float h)
	{
		return new float[]
			{
				 w,  h, 0,  // top right
				 w,  0, 0,  // bottom right
			     0,  0, 0,  // bottom left
			     0,  h, 0   // top left 
			};
	}
	
	public static float[] makeUVs(float u, float v, float uWidth, float uHeight)
	{
		return new float[]
			{
				u + uWidth, v,
				u + uWidth, v + uHeight,
				u, v + uHeight,
				u, v
			};
	}
	
	private Mesh guiMesh;
	private Material material;
	
	private MeasurementPair dimensions;
	
	private float initialWidth, initialHeight;
	
	public GUITexture(MeasurementPair position, MeasurementPair dimensions, float u, float v)
	{
		this(position, dimensions, u, v, Materials.GUI_MATERIAL);
	}
	
	public GUITexture(MeasurementPair position, MeasurementPair dimensions, float u, float v, Material material)
	{
		super(position);
		
		initialWidth = dimensions.x().actualValue(Window.instance().getWidth());
		initialHeight = dimensions.y().actualValue(Window.instance().getHeight());
		
		this.dimensions = dimensions;
		
		guiMesh = Mesh.createMesh(
				makeVerts(initialWidth, initialHeight), 
				indices, makeUVs(u, v, material.uvWidth(), material.uvHeight()));
		
		this.material = material;
	}
	
	@Override
	public Material material()
	{
		return material;
	}
	
	@Override
	public Mesh guiMesh()
	{
		return guiMesh;
	}
	
	@Override
	public void onResize(float w, float h)
	{
		super.onResize(w, h);
		
		float newWidth = dimensions.x().actualValue(w);
		float newHeight = dimensions.y().actualValue(h);
		
		float scaleX = 1 + (newWidth - initialWidth) / initialWidth, 
				scaleY = 1 + (newHeight - initialHeight) / initialHeight;
		
		this.transform.scale(scaleX, scaleY, 1);
	}
}
