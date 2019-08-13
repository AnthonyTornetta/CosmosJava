package com.cornchipss.world;

import com.cornchipss.registry.Blocks;
import com.cornchipss.rendering.Model;
import com.cornchipss.rendering.ModelCreator;

public class Block
{
	private static short lastUsedId = 0;
	
	private short id;
	
	private boolean opaque = true;
	
	public Block()
	{
		this.id = getNextId();
	}
	
	/**
	 * Creates the default cube texture
	 * @param mc The model creator to use for this (you can use {@link ModelCreator#DEFAULT}
	 */
	public Model createModel(ModelCreator mc)
	{
		int atlasWidth  = 16;
		int atlasHeight = 16;
		
		float u, v;		
		float percentageV = 1.0f / atlasHeight;
		float percentageU = 1.0f / atlasWidth;
		
		mc.color(1, 1, 1);
		
		float vertices[] =
		{
			// Front face
			-0.5f, -0.5f, +0.5f,
			+0.5f, -0.5f, +0.5f,
			+0.5f, +0.5f, +0.5f,
			-0.5f, +0.5f, +0.5f,
			
			// Back face
			-0.5f, -0.5f, -0.5f,
			-0.5f, +0.5f, -0.5f,
			+0.5f, +0.5f, -0.5f,
			+0.5f, -0.5f, -0.5f,
			
			// Top face
			-0.5f, +0.5f, -0.5f,
			-0.5f, +0.5f, +0.5f,
			+0.5f, +0.5f, +0.5f,
			+0.5f, +0.5f, -0.5f,
			
			// Bottom face
			-0.5f, -0.5f, -0.5f,
			+0.5f, -0.5f, -0.5f,
			+0.5f, -0.5f, +0.5f,
			-0.5f, -0.5f, +0.5f,
			
			// Right face
			+0.5f, -0.5f, -0.5f,
			+0.5f, +0.5f, -0.5f,
			+0.5f, +0.5f, +0.5f,
			+0.5f, -0.5f, +0.5f,
			
			// Left face
			-0.5f, -0.5f, -0.5f,
			-0.5f, -0.5f, +0.5f,
			-0.5f, +0.5f, +0.5f,
			-0.5f, +0.5f, -0.5f,
		};
		
		float texcoords[] = 
		{
			// Front
			0.0f,  1.0f,
			1.0f,  1.0f,
			1.0f,  0.0f,
			0.0f,  0.0f,
			// Back
			1.0f,  1.0f,
			1.0f,  0.0f,
			0.0f,  0.0f,
			0.0f,  1.0f,
			// Top
			1.0f,  0.0f,
			1.0f,  1.0f,
			0.0f,  1.0f,
			0.0f,  0.0f,
			// Bottom
			0.0f,  0.0f,
			1.0f,  0.0f,
			1.0f,  1.0f,
			0.0f,  1.0f,
			// Right
			1.0f,  1.0f,
			1.0f,  0.0f,
			0.0f,  0.0f,
			0.0f,  1.0f,
			// Left
			0.0f,  1.0f,
			1.0f,  1.0f,
			1.0f,  0.0f,
			0.0f,  0.0f,
		};
		
		int cubeIndices[] = 
		{
			0,  1,  2,      0,  2,  3,    // front
			4,  5,  6,      4,  6,  7,    // back
			8,  9,  10,     8,  10, 11,   // top
			12, 13, 14,     12, 14, 15,   // bottom
			16, 17, 18,     16, 18, 19,   // right
			20, 21, 22,     20, 22, 23,   // left
		};
		
		for(int i = 0; i < vertices.length; i += 3)
		{
			mc.vertex(vertices[i], vertices[i + 1], vertices[i + 2]);
		}
		
		for(int i = 0; i < texcoords.length; i += 2)
		{
			// u,v = texture x,y
	        int texture = getTexture(i / 8);
			
			v = texture / atlasWidth; // row
			u = texture % 16;     // column
			
			mc.uv((u + texcoords[i]) * percentageU, (v + texcoords[i + 1]) * percentageV);
		}
		
		for(int i = 0; i < cubeIndices.length; i++)
		{
			mc.index(cubeIndices[i]);
		}
		
		return mc.create();
	}
	
	public int getTexture(int face) { return 0; }
	
	public short getId() { return id; }
	
	private static short getNextId()
	{
		return lastUsedId++;
	}

	public boolean isOpaque() { return opaque; }
	public void setOpaque(boolean opaque) { this.opaque = opaque; }

	public Model getModel()
	{
		return Blocks.getModel(getId());
	}
}
