package com.cornchipss.cosmos.client.world;

import java.util.LinkedList;
import java.util.List;

import org.joml.Matrix4fc;

import com.cornchipss.cosmos.physx.PhysicalObject;
import com.cornchipss.cosmos.rendering.IRenderable;
import com.cornchipss.cosmos.world.World;
import com.cornchipss.cosmos.world.entities.player.ClientPlayer;

public class ClientWorld extends World implements IRenderable
{
	private List<IRenderable> renderables = new LinkedList<>();
	
	public void addPhysicalObject(PhysicalObject bdy)
	{
		super.addPhysicalObject(bdy);
		
		if (bdy instanceof IRenderable)
			renderables.add((IRenderable)bdy);
	}
	
	@Override
	public void updateGraphics()
	{
		for (IRenderable r : renderables)
			r.updateGraphics();
	}

	@Override
	public void draw(Matrix4fc projectionMatrix, Matrix4fc camera, ClientPlayer p)
	{
		for (IRenderable r : renderables)
			if(r.shouldBeDrawn())
				r.draw(projectionMatrix, camera, p);
	}

	@Override
	public boolean shouldBeDrawn()
	{
		return true;
	}
}
