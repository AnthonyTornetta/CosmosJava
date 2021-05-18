package com.cornchipss.cosmos.gui;

import com.cornchipss.cosmos.gui.measurement.MeasurementPair;
import com.cornchipss.cosmos.rendering.Mesh;
import com.cornchipss.cosmos.rendering.Window;

public class GUITextureMultiple extends GUITexture
{
	private Mesh[] meshes;
	private int state;
	
	public GUITextureMultiple(MeasurementPair position, MeasurementPair dimensions, 
			float... uvs)
	{
		super(position, dimensions, uvs[0], uvs[1]);
		
		assert uvs.length % 2 != 0 || uvs.length < 2;
		
		meshes = new Mesh[uvs.length / 2];
		
		state = 0;
		
		meshes[0] = super.guiMesh();
		
		float[] verts = 
				makeVerts(dimensions.x().actualValue(Window.instance().getWidth()),
						dimensions.y().actualValue(Window.instance().getHeight()));
		
		for(int i = 2; i < uvs.length; i+=2)
		{
			meshes[i/2] = Mesh.createMesh(verts, indices, 
					makeUVs(uvs[i], uvs[i + 1], material().uvWidth(), material().uvHeight()));
		}
	}
	
	@Override
	public Mesh guiMesh()
	{
		return meshes[state];
	}

	public int state() { return state; }

	public void state(int state) 
	{
		if(state > maxState() || state < minState())
			throw new IllegalArgumentException("minState() <= state <= maxState");
		
		this.state = state;
	}
	
	public int maxState()
	{
		return meshes.length - 1;
	}
	
	public int minState()
	{
		return 0;
	}
}
