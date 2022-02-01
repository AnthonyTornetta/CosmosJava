package com.cornchipss.cosmos.models.skybox;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.PNGDecoder;

import com.cornchipss.cosmos.client.world.entities.ClientPlayer;
import com.cornchipss.cosmos.memory.MemoryPool;
import com.cornchipss.cosmos.models.ModelLoader;
import com.cornchipss.cosmos.rendering.IRenderable;
import com.cornchipss.cosmos.rendering.Mesh;
import com.cornchipss.cosmos.shaders.Shader;

public class Skybox implements IRenderable
{
	private Shader shader;

	public Skybox()
	{

	}

	private int loadCubemap(String[] faces)
	{
		int id = GL30.glGenTextures();
		GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, id);

		for (int i = 0; i < faces.length; i++)
		{
			try
			{
				PNGDecoder decoder = new PNGDecoder(new FileInputStream(
					"assets/images/skybox/" + faces[i] + ".png"));
				ByteBuffer buffer = BufferUtils.createByteBuffer(
					decoder.getWidth() * decoder.getHeight() * 3);
				// 3 for RGB

				decoder.decode(buffer, decoder.getWidth() * 3, PNGDecoder.RGB);
				buffer.rewind();

				GL30.glTexImage2D(GL30.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0,
					GL30.GL_RGB, decoder.getWidth(), decoder.getHeight(), 0,
					GL30.GL_RGB, GL30.GL_UNSIGNED_BYTE, buffer);
			}
			catch (IOException ex)
			{
				throw new RuntimeException(ex);
			}
			// might want to free texture here, idk tho
			// https://learnopengl.com/Advanced-OpenGL/Cubemaps
		}

		GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP,
			GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR);
		GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP,
			GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);
		GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_WRAP_S,
			GL30.GL_CLAMP_TO_EDGE);
		GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_WRAP_T,
			GL30.GL_CLAMP_TO_EDGE);
		GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_WRAP_R,
			GL30.GL_CLAMP_TO_EDGE);

		return id;
	}

	private Mesh m;
	private int cubemapID;

	private int projection_location, view_location;

	@Override
	public void updateGraphics()
	{
		cubemapID = loadCubemap(
//			new String[] { "right", "left", "up", "down", "front", "back" }
			new String[] { "back", "back", "back", "back", "back", "back" }
			);

		shader = new Shader("assets/shaders/skybox");
		shader.init();

		projection_location = shader.uniformLocation("projection");
		view_location = shader.uniformLocation("view");

		try
		{
			m = ModelLoader.fromFile("assets/models/rectangle").createMesh(0, 0,
				0, 2.0f);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public void draw(Matrix4fc projectionMatrix, Matrix4fc camera,
		ClientPlayer p)
	{
		GL30.glDepthMask(false);
		shader.use();

		shader.setUniformMatrix(projection_location, projectionMatrix);

		Matrix3f mat3 = MemoryPool.getInstanceOrCreate(Matrix3f.class);
		mat3.set(camera);

		// Remove's player location
		Matrix4f mat4 = MemoryPool.getInstanceOrCreate(Matrix4f.class);
		mat4.set(mat3);

		shader.setUniformMatrix(view_location, mat4);

		GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, cubemapID);
		m.prepare();
		m.draw();
		m.finish();
		GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, 0);
		
		GL30.glDepthMask(true);

		shader.stop();
	}

	@Override
	public boolean shouldBeDrawn()
	{
		return m != null;
	}
}
