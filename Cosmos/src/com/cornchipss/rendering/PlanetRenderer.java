package com.cornchipss.rendering;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL33;

import com.cornchipss.rendering.shaders.PlanetShader;
import com.cornchipss.utils.datatypes.Vector3fList;
import com.cornchipss.world.planet.Planet;

public class PlanetRenderer extends Renderer
{
	private int timeLocation, chunkLocation;
	
	private Texture atlas;
	
	// Used for mass rendering models
	private int positionsVBO;
	
	public PlanetRenderer()
	{
		super(new PlanetShader());
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		// For use w/ storing positions of where models are
		// This is initialized here just to minimize interactions w/ the GPU to save some performace (i think that's how it works)
		positionsVBO = GL15.glGenBuffers();
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, positionsVBO);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	@Override
	public void loadTextures()
	{
		atlas = Texture.loadTexture("atlas/main.png");
	}
	
	@Override
	public void loadUniformLocations()
	{
		timeLocation = getShader().getUniformLocation("u_time");
		chunkLocation = getShader().getUniformLocation("chunkLocation");
	}
	
	@Override
	public void loadUniforms()
	{
		getShader().setUniformF(timeLocation, (float)GLFW.glfwGetTime());
	}
	
	@Override
	public void bindTextures()
	{
		atlas.bind();
	}
	
	public void render(Planet planet)
	{
		if(planet == null)
			throw new IllegalArgumentException("Cannot render a null planet!");
		
		if(planet.isGenerated())
		{
			getShader().setUniformVector(chunkLocation, planet.getAbsoluteX(), planet.getAbsoluteY(), planet.getAbsoluteZ());
			
			Map<Model, Vector3fList> modelsAndPositions = planet.getModelsAndPositions();
			
			List<Model> transparentModels = new LinkedList<>();
			
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glCullFace(GL11.GL_BACK);
			
			for(Model m : modelsAndPositions.keySet())
			{
				if(m.isTransparent())
					transparentModels.add(m);
				else
					renderModel(m, modelsAndPositions.get(m));
			}
			
			GL11.glEnable(GL11.GL_CULL_FACE); 
			GL11.glCullFace(GL11.GL_FRONT);
//			
//			GL11.glEnable(GL30.GL_SAMPLE_ALPHA_TO_COVERAGE);
//			
//			GL11.glEnable(GL11.GL_BLEND);
//			GL11.glAlphaFunc(GL11.GL_GREATER, 0.05f);
//			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			
			for(Model m : transparentModels)
			{
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				
				renderModel(m, modelsAndPositions.get(m));
				
				GL11.glDisable(GL11.GL_BLEND);
			}
			
			GL11.glCullFace(GL11.GL_BACK);
			for(Model m : transparentModels)
			{
				renderModel(m, modelsAndPositions.get(m));
			}
			
			GL11.glDisable(GL11.GL_BLEND);
		}
	}
	
	private void renderModel(Model m, Vector3fList posList)
	{
		float[] positions = posList.asFloats();
		
		GL30.glBindVertexArray(m.getVao());
		
		// Update every position in the model
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, positionsVBO);
		
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, positions, GL15.GL_STATIC_DRAW);
		GL30.glVertexAttribPointer(3, 3, GL11.GL_FLOAT, false, 0, 0);
		GL33.glVertexAttribDivisor(3, 1);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		// Draw it all!
		GL30.glEnableVertexAttribArray(0);
		GL30.glEnableVertexAttribArray(1);
		GL30.glEnableVertexAttribArray(2);
		GL30.glEnableVertexAttribArray(3);
		
		GL31.glDrawElementsInstanced(GL30.GL_TRIANGLES, m.getVertexCount(), GL11.GL_UNSIGNED_INT, 0, posList.size() / 3);
		
		GL33.glDisableVertexAttribArray(3);
		GL30.glDisableVertexAttribArray(2);
		GL30.glDisableVertexAttribArray(1);
		GL30.glDisableVertexAttribArray(0);
		
		GL30.glBindVertexArray(0);
	}
}
