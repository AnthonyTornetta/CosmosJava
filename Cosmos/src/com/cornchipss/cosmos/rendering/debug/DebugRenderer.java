package com.cornchipss.cosmos.rendering.debug;

import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.opengl.GL30;

import com.cornchipss.cosmos.client.world.entities.ClientPlayer;
import com.cornchipss.cosmos.material.types.DebugMaterial;
import com.cornchipss.cosmos.models.ModelLoader;
import com.cornchipss.cosmos.physx.collision.obb.OBBCollider;
import com.cornchipss.cosmos.rendering.IRenderable;
import com.cornchipss.cosmos.rendering.Mesh;

public class DebugRenderer implements IRenderable
{
	private static class DebugInfo
	{
		MeshKey mesh;
		Matrix4fc transform;
		DrawMode mode;
	}

	private static class MeshKey
	{
		Vector3fc halfwidths;
		Color c;
		
		public MeshKey(Color c, Vector3fc halfwidths)
		{
			this.c = c;
			this.halfwidths = halfwidths;
		}
		
		public int hashCode()
		{
			return halfwidths.hashCode() + c.hashCode();
		}
		
		public boolean equals(Object o)
		{
			if(o instanceof MeshKey)
			{
				return halfwidths.equals(((MeshKey)o).halfwidths) && c.equals(((MeshKey)o).c);
			}
			return false;
		}
	}
	
	public static enum DrawMode
	{
		LINES, FILL
	}

	private Map<MeshKey, Mesh> meshCache = new HashMap<>();
	
	private List<DebugInfo> info = new LinkedList<>();
	private DebugMaterial debugMaterial;

	private boolean enabled = false;

	private DebugRenderer()
	{
	}

	private static DebugRenderer instance;

	public static DebugRenderer instance()
	{
		if (instance == null)
			instance = new DebugRenderer();

		return instance;
	}

	@Override
	public void updateGraphics()
	{
		if (debugMaterial == null)
		{
			debugMaterial = new DebugMaterial();
			debugMaterial.init();
		}
	}

	public void draw(Matrix4fc projectionMatrix, Matrix4fc camera,
		ClientPlayer p)
	{
		debugMaterial.use();

		for (DebugInfo m : info)
		{
			debugMaterial.initUniforms(projectionMatrix, camera, m.transform,
				false);

			switch (m.mode)
			{
				case LINES:
					GL30.glPolygonMode(GL30.GL_FRONT_AND_BACK, GL30.GL_LINE);
					break;
				default:
					GL30.glPolygonMode(GL30.GL_FRONT_AND_BACK, GL30.GL_FILL);
			}

			Mesh mesh = meshCache.get(m.mesh);
			mesh.prepare();
			mesh.draw();
			mesh.finish();
		}

		debugMaterial.stop();
		
		info.clear();

		GL30.glPolygonMode(GL30.GL_FRONT_AND_BACK, GL30.GL_FILL);
	}

	@Override
	public boolean shouldBeDrawn()
	{
		return debugMaterial != null;
	}
	
	public void freeResources()
	{
		for(Mesh m : meshCache.values())
			m.delete();
		meshCache.clear();
	}

	public void drawOBB(OBBCollider c, Color color, DrawMode mode)
	{
		if (!enabled())
			return;

		Matrix4f mat = new Matrix4f();

		mat.translate(c.center());

		c.orientation().applyRotation(mat);

		drawRectangle(mat, c.halfwidths(), color, mode);
	}

	public void drawRectangle(Matrix4fc transform, Vector3fc halfwidths,
		Color color, DrawMode mode)
	{
		if (!enabled())
			return;

		try
		{
			MeshKey mk = new MeshKey(color, halfwidths);
			
			if(!meshCache.containsKey(mk))
			{
				Mesh m = ModelLoader.fromFile("assets/models/rectangle").createMesh(
					0, 0, 0, halfwidths.x() * 2, halfwidths.y() * 2,
					halfwidths.z() * 2, false);
	
				float r, g, b;
				r = color.getRed() / 255.0f;
				g = color.getGreen() / 255.0f;
				b = color.getBlue() / 255.0f;
	
				m.storeData(Mesh.COLOR_INDEX, 3,
					new float[] { r, g, b, r, g, b, r, g, b, r, g, b, r, g, b, r, g,
						b, r, g, b, r, g, b, r, g, b, r, g, b, r, g, b, r, g, b, r,
						g, b, r, g, b, r, g, b, r, g, b, r, g, b, r, g, b, r, g, b,
						r, g, b, r, g, b, r, g, b, r, g, b, r, g, b });
	
				m.unbind();
				
				meshCache.put(mk, m);
			}

			DebugInfo dinfo = new DebugInfo();

			dinfo.mesh = mk;
			dinfo.transform = new Matrix4f().set(transform);
			dinfo.mode = mode;

			info.add(dinfo);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void drawPoint(Vector3fc lineStart, Color color)
	{
		if (!enabled())
			return;

		drawRectangle(new Matrix4f().translate(lineStart),
			new Vector3f(0.01f, 0.01f, 0.01f), color, DrawMode.FILL);
	}

	public boolean enabled()
	{
		return enabled;
	}

	public void enabled(boolean b)
	{
		enabled = b;
	}

	public void toggleEnabled()
	{
		enabled = !enabled;
	}
}
