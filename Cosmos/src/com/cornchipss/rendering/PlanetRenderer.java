package com.cornchipss.rendering;

import java.util.Map;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL33;

import com.cornchipss.Game;
import com.cornchipss.rendering.shaders.PlanetShader;
import com.cornchipss.utils.Maths;
import com.cornchipss.utils.datatypes.ArrayListF;
import com.cornchipss.world.entities.Player;
import com.cornchipss.world.planet.Planet;

public class PlanetRenderer
{
	private PlanetShader shader;
	
	private int timeLocation, projectionLocation, viewLocation, chunkLocation;
	
	private Texture atlas;
	
	private Matrix4f projectionMatrix;
	private Matrix4f viewMatrix;
	
	private int positionsVBO;
	
	public PlanetRenderer()
	{
		shader = new PlanetShader();
		
		timeLocation = shader.getUniformLocation("u_time");
		projectionLocation = shader.getUniformLocation("projection");
		viewLocation = shader.getUniformLocation("view");
		chunkLocation = shader.getUniformLocation("chunkLocation");
		
		atlas = Texture.loadTexture("atlas/main.png");
		projectionMatrix = new Matrix4f();
		projectionMatrix.perspective((float)Math.toRadians(90), Game.getInstance().getWindow().getWidth() / (float)Game.getInstance().getWindow().getHeight(), 0.1f, 1000);
		
		viewMatrix = new Matrix4f();
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		// For use w/ storing positions of where models are
		// This is initialized here just to minimize interactions w/ the GPU to save some performace (i think that's how it works)
		positionsVBO = GL15.glGenBuffers();
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, positionsVBO);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	public void setupRender(Player player)
	{
		GL11.glEnable(GL13.GL_TEXTURE0);
		
		Maths.createViewMatrix(player.getX(), player.getY(), player.getZ(), 
				player.getRx(), player.getRy(), player.getRz(), viewMatrix);
		
		shader.start();
		shader.setUniformF(timeLocation, (float)GLFW.glfwGetTime());
		shader.loadUniformMatrix(projectionMatrix, projectionLocation);
		shader.loadUniformMatrix(viewMatrix, viewLocation);
		
		atlas.bind();
	}
	
	public void stopRender()
	{
		Texture.unbind();
		
		shader.stop();
	}
	
	public void render(Planet planet)
	{
		if(planet == null)
			throw new IllegalArgumentException("Cannot render a null planet!");
		
		if(planet.isGenerated())
		{
			shader.setUniformVector(chunkLocation, planet.getAbsoluteX(), planet.getAbsoluteY(), planet.getAbsoluteZ());
			
			Map<Model, ArrayListF> modelsAndPositions = planet.getModelsAndPositions();
			
			for(Model m : modelsAndPositions.keySet())
			{
				ArrayListF posList = modelsAndPositions.get(m);
				float[] positions = posList.getArray();
				
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
	}
}
