package com.cornchipss.cosmos.client.world;

import java.awt.Color;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Vector3f;

import org.joml.Matrix4fc;

import com.bulletphysics.linearmath.DebugDrawModes;
import com.bulletphysics.linearmath.IDebugDraw;
import com.cornchipss.cosmos.client.world.entities.ClientPlayer;
import com.cornchipss.cosmos.physx.PhysicalObject;
import com.cornchipss.cosmos.rendering.IRenderable;
import com.cornchipss.cosmos.rendering.debug.DebugRenderer;
import com.cornchipss.cosmos.utils.Logger;
import com.cornchipss.cosmos.world.World;

public class ClientWorld extends World implements IRenderable
{
	private List<IRenderable> renderables = new LinkedList<>();

	public ClientWorld()
	{
		world.setDebugDrawer(new IDebugDraw()
		{
			int debugMode;

			@Override
			public void setDebugMode(int debugMode)
			{
				this.debugMode = debugMode;
			}

			@Override
			public void reportErrorWarning(String warningString)
			{
				Logger.LOGGER.warning(warningString);
			}

			@Override
			public int getDebugMode()
			{
				return debugMode;
			}

			@Override
			public void drawLine(Vector3f from, Vector3f to, Vector3f color)
			{
				DebugRenderer.instance().drawLine(
					new org.joml.Vector3f(from.x, from.y, from.z),
					new org.joml.Vector3f(to.x, to.y, to.z),
					new Color(color.x, color.y, color.z));
			}

			@Override
			public void drawContactPoint(Vector3f PointOnB, Vector3f normal,
				float distance, int lifeTime, Vector3f color)
			{
				DebugRenderer.instance()
					.drawPoint(
						new org.joml.Vector3f(PointOnB.x, PointOnB.y,
							PointOnB.z),
						new Color(color.x, color.y, color.z));

				DebugRenderer.instance().drawLine(
					new org.joml.Vector3f(PointOnB.x, PointOnB.y,
						PointOnB.z),
					new org.joml.Vector3f(PointOnB.x + normal.x * distance,
						PointOnB.y + normal.y * distance,
						PointOnB.z + normal.z * distance),
					new Color(color.x, color.y, color.z));
			}

			@Override
			public void draw3dText(Vector3f location, String textString)
			{
				Logger.LOGGER.info(textString);
			}
		});
		
		world.getDebugDrawer().setDebugMode(DebugDrawModes.DRAW_WIREFRAME);
	}
	
	@Override
	public void addObjectDuringUnlock(PhysicalObject bdy)
	{
		super.addObjectDuringUnlock(bdy);

		if (bdy instanceof IRenderable)
			renderables.add((IRenderable) bdy);
	}

	@Override
	public void removeObjectDuringUnlock(PhysicalObject obj)
	{
		super.removeObjectDuringUnlock(obj);

		if (obj instanceof IRenderable)
			renderables.remove((IRenderable) obj);
	}

	@Override
	public void updateGraphics()
	{
		try
		{
			for (IRenderable r : renderables)
				r.updateGraphics();

			DebugRenderer.instance().updateGraphics();
		}
		catch (ConcurrentModificationException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void draw(Matrix4fc projectionMatrix, Matrix4fc camera,
		ClientPlayer p)
	{
		try
		{
//			for (IRenderable r : renderables)
//				if (r.shouldBeDrawn())
//					r.draw(projectionMatrix, camera, p);

			if (DebugRenderer.instance().shouldBeDrawn())
				DebugRenderer.instance().draw(projectionMatrix, camera, p);
			
			world.debugDrawWorld();
		}
		catch (ConcurrentModificationException ex)
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
