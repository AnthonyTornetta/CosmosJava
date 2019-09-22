package com.cornchipss.rendering;

import java.util.LinkedList;
import java.util.List;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL33;

import com.cornchipss.rendering.shaders.DebugShader;
import com.cornchipss.utils.Utils;

public class DebugRenderer extends Renderer
{
	private List<Model> models = new LinkedList<>();
	
	private ModelCreator creator = new ModelCreator();
	
	public DebugRenderer()
	{
		super(new DebugShader());
	}
	
	@Override
	protected void loadUniforms()
	{
		
	}
	
	@Override
	protected void loadUniformLocations()
	{
		
	}
	
	public void addLine(Vector3f start, Vector3f end, int r, int g, int b)
	{
		creator.color(r, g, b);
		creator.vertex(start);
		creator.vertex(end);
		models.add(creator.create());
	}
	
	public void render()
	{
		for(Model m : models)
		{
			GL30.glBindVertexArray(m.getVao());
			
			// Draw it all!
			GL30.glEnableVertexAttribArray(0);
			GL30.glEnableVertexAttribArray(1);
			//GL30.glEnableVertexAttribArray(2);
			GL30.glEnableVertexAttribArray(3);
			
			GL31.glDrawElements(GL11.GL_TRIANGLES, m.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			
			GL33.glDisableVertexAttribArray(3);
			//GL30.glDisableVertexAttribArray(2);
			GL30.glDisableVertexAttribArray(1);
			GL30.glDisableVertexAttribArray(0);
			
			GL30.glBindVertexArray(0);
		}
	}
	
	@Override
	protected void loadTextures() { }

	@Override
	protected void bindTextures() { }

	public void clear()
	{
		models.clear();
	}
}
