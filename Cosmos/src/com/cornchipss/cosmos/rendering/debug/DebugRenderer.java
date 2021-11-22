package com.cornchipss.cosmos.rendering.debug;

import java.awt.Color;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;
import org.lwjgl.opengl.GL30;

import com.cornchipss.cosmos.client.world.entities.ClientPlayer;
import com.cornchipss.cosmos.material.types.DebugMaterial;
import com.cornchipss.cosmos.models.ModelLoader;
import com.cornchipss.cosmos.rendering.IRenderable;
import com.cornchipss.cosmos.rendering.Mesh;

public class DebugRenderer implements IRenderable
{
	private static class DebugInfo
	{
		Mesh mesh;
		Matrix4fc transform;
		DrawMode mode;
	}
	
	public static enum DrawMode
	{
		LINES,
		FILL
	}

	private List<DebugInfo> info = new LinkedList<>();
	private DebugMaterial debugMaterial;

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

			switch(m.mode)
			{
				case LINES:
					GL30.glPolygonMode(GL30.GL_FRONT_AND_BACK, GL30.GL_LINE);
					break;
				default:
					GL30.glPolygonMode(GL30.GL_FRONT_AND_BACK, GL30.GL_FILL);
			}
			
			m.mesh.prepare();
			m.mesh.draw();
			m.mesh.finish();
		}

		debugMaterial.stop();

		for (DebugInfo m : info)
		{
			m.mesh.delete();
		}

		info.clear();
		
		GL30.glPolygonMode(GL30.GL_FRONT_AND_BACK, GL30.GL_FILL);
	}

	@Override
	public boolean shouldBeDrawn()
	{
		return debugMaterial != null;
	}

	public void drawRectangle(Matrix4fc transform, Vector3fc halfwidths,
		Color color, DrawMode mode)
	{
		try
		{
			Mesh m = ModelLoader.fromFile("assets/models/rectangle").createMesh(
				0, 0, 0, halfwidths.x(), halfwidths.y(), halfwidths.z(), false);

			float r, g, b;
			r = color.getRed() / 255.0f;
			g = color.getGreen() / 255.0f;
			b = color.getBlue() / 255.0f;

			m.storeData(Mesh.COLOR_INDEX, 3,
				new float[] { 
					r, g, b, r, g, b, r, g, b, r, g, b, r, g, b, r, g, b, 
					r, g, b, r, g, b, r, g, b, r, g, b, r, g, b, r, g, b,
					r, g, b, r, g, b, r, g, b, r, g, b, r, g, b, r, g, b, 
					r, g, b, r, g, b, r, g, b, r, g, b, r, g, b, r, g, b});

			m.unbind();

			DebugInfo dinfo = new DebugInfo();

			dinfo.mesh = m;
			dinfo.transform = new Matrix4f().set(transform);
			dinfo.mode = mode;

			info.add(dinfo);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
