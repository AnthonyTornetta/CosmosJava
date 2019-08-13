package com.cornchipss.rendering;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.PNGDecoder;

public class Texture
{
	private int id;
	
	public Texture(int id)
	{
		this.id = id;
	}
	
	public static Texture loadTexture(String texture)
	{
		try
		{
			PNGDecoder decoder = new PNGDecoder(new FileInputStream("assets/images/" + texture));
			ByteBuffer buffer = BufferUtils.createByteBuffer(decoder.getWidth() * decoder.getHeight() * 4); //4 -> rgba
			decoder.decode(buffer, decoder.getWidth() * 4, PNGDecoder.RGBA);
			buffer.rewind();
			
			int id = GL11.glGenTextures();
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
			
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
			
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
			
			return new Texture(id);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public void bind()
	{
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, getId());
	}
	
	public static void unbind()
	{
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
	
	public int getId() { return id; }
}
