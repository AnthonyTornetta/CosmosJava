package com.cornchipss.cosmos.client.world;

import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.List;

import org.joml.Matrix4fc;

import com.cornchipss.cosmos.client.world.entities.ClientPlayer;
import com.cornchipss.cosmos.physx.PhysicalObject;
import com.cornchipss.cosmos.rendering.IRenderable;
import com.cornchipss.cosmos.rendering.debug.DebugRenderer;
import com.cornchipss.cosmos.world.World;

public class ClientWorld extends World implements IRenderable
{
	private List<IRenderable> renderables = new LinkedList<>();
	
	public void addObjectDuringUnlock(PhysicalObject bdy)
	{
		super.addObjectDuringUnlock(bdy);
		
		if (bdy instanceof IRenderable)
			renderables.add((IRenderable)bdy);
	}
	
	public void removeObjectDuringUnlock(PhysicalObject obj)
	{
		super.removeObjectDuringUnlock(obj);
		
		if (obj instanceof IRenderable)
			renderables.remove((IRenderable)obj);
	}
	
	@Override
	public void updateGraphics()
	{
		for (IRenderable r : renderables)
			r.updateGraphics();
		
		DebugRenderer.instance().updateGraphics();
	}

	@Override
	public void draw(Matrix4fc projectionMatrix, Matrix4fc camera, ClientPlayer p)
	{
		try
		{
			for (IRenderable r : renderables)
				if(r.shouldBeDrawn())
					r.draw(projectionMatrix, camera, p);
			
			if(DebugRenderer.instance().shouldBeDrawn())
				DebugRenderer.instance().draw(projectionMatrix, camera, p);
		}
		catch(ConcurrentModificationException ex)
		{
			ex.printStackTrace();
		}
	}

	@Override
	public boolean shouldBeDrawn()
	{
		return true;
	}
}
