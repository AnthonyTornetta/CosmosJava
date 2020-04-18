package com.cornchipss.rendering;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import com.cornchipss.Cosmos;
import com.cornchipss.registry.Options;
import com.cornchipss.rendering.shaders.Shader;
import com.cornchipss.utils.Maths;
import com.cornchipss.utils.Utils;
import com.cornchipss.world.entities.Player;

public abstract class Renderer
{
	private int u_projectionLocation, u_viewLocation;
	
	private Shader shader;
	
	private Matrix4f projectionMatrix, viewMatrix;
	
	public Renderer(Shader shader)
	{
		this.shader = shader;
		
		projectionMatrix = new Matrix4f();
		projectionMatrix.perspective((float)Math.toRadians(90), 
				Cosmos.getInstance().getWindow().getWidth() / 
				(float)Cosmos.getInstance().getWindow().getHeight(), 
				0.1f, Options.getIntOption("cosmos:render_distance"));	
		
		viewMatrix = new Matrix4f();
		
		u_projectionLocation = getShader().getUniformLocation("projection");
		u_viewLocation = getShader().getUniformLocation("view");
		
		loadUniformLocations();
		loadTextures();
	}
	
	public void setupRender(Player player)
	{
		GL11.glEnable(GL13.GL_TEXTURE0);
		
		getViewMatrix().identity();
		getViewMatrix().rotate(player.camera().rotation());
		getViewMatrix().translate(Maths.mul(player.camera().position(), -1));
		
//		getViewMatrix().set(player.camera().asMatrix());
//		
//		getViewMatrix().translate(Maths.mul(player.camera().position(), -2));
		
//		Maths.createViewMatrix(player.camera().position(),
//				player.camera().rotation(), getViewMatrix());
		
		getShader().start();
		getShader().loadUniformMatrix(u_projectionLocation, getProjectionMatrix());
		getShader().loadUniformMatrix(u_viewLocation, getViewMatrix());
		
		loadUniforms();
		bindTextures();
	}
	
	protected abstract void loadTextures();
	
	protected abstract void loadUniforms();
	
	protected abstract void bindTextures();
	
	protected abstract void loadUniformLocations();
	
	public void stopRender()
	{
		Texture.unbind();
		
		Shader.stop();
	}
	
	public Shader getShader() { return shader; }
	public void setShader(Shader shader) { this.shader = shader; }
	
	public Matrix4f getProjectionMatrix() { return projectionMatrix; }
	public Matrix4f getViewMatrix() { return viewMatrix; }
}
