package com.cornchipss.rendering.debug;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import org.joml.Vector3fc;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL33;
import org.newdawn.slick.Color;

import com.cornchipss.rendering.Model;
import com.cornchipss.rendering.ModelCreator;
import com.cornchipss.utils.Maths;

public class DebugRectangle implements DebugShape
{
	private Matrix4f mat;
	private Color c;
	
	private Model model;
	
	public DebugRectangle(Color c, Vector3fc pos, Quaternionfc rot, Vector3fc dimensions)
	{
		mat = Maths.createTransformationMatrix(pos, rot);
		this.c = c;
		
		ModelCreator mc = ModelCreator.DEFAULT;
		
		float[] rectVerts = ModelCreator.generateRectangleShape(dimensions);
		int[] rectIndicies = ModelCreator.rectangleIndicies();
		
		mc.color(c.r, c.g, c.b);
		
		for(int i = 0; i < rectVerts.length; i += 3)
		{
			mc.vertex(rectVerts[i], rectVerts[i + 1], rectVerts[i + 2]);
		}
		
		for(int i = 0; i < rectIndicies.length; i++)
		{
			mc.index(rectIndicies[i]);
		}
		
		model = mc.create();
		model.setTransparent(true);
	}
	
	@Override
	public void draw()
	{		
		GL30.glBindVertexArray(model.getVao());
		
		// Draw it
		GL30.glEnableVertexAttribArray(0);
		GL30.glEnableVertexAttribArray(1);
		GL30.glEnableVertexAttribArray(2);
		GL30.glEnableVertexAttribArray(3);
		
		GL31.glDrawElements(GL30.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		
		GL33.glDisableVertexAttribArray(3);
		GL30.glDisableVertexAttribArray(2);
		GL30.glDisableVertexAttribArray(1);
		GL30.glDisableVertexAttribArray(0);
		
		GL30.glBindVertexArray(0);
	}

	@Override
	public Color color()
	{
		return c;
	}

	@Override
	public Matrix4fc matrix()
	{
		return mat;
	}

}
