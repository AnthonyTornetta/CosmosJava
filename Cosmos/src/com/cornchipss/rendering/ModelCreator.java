package com.cornchipss.rendering;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import com.cornchipss.utils.datatypes.ArrayListF;
import com.cornchipss.utils.datatypes.ArrayListI;

public class ModelCreator
{
	private int vertexCount = 0;
	private ArrayListF positions, uvs, colors;
	private ArrayListI indicies;
	
	private int indexOffset = 0, largestIndex = -1;
	
	private float r = 1.0f, g = 1.0f, b = 1.0f;
	
	/**
	 * <p>Pre-created instance for you to use.</p>
	 * <p><b>ONLY USE ON MAIN THREAD OR THINGS MAY BREAK.</b></p>
	 */
	public static final ModelCreator DEFAULT = new ModelCreator();
	
	public ModelCreator()
	{
		// These default sizes are equal to the amount of values that a cube would have
		positions = new ArrayListF(72);
		colors = new ArrayListF(36);
		uvs = new ArrayListF(48);
		indicies = new ArrayListI(36);
	}
	
	public Model create()
	{
		int vao = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao);
		
		int positionsVBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, positionsVBO);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, positions.getArray(), GL15.GL_STATIC_DRAW);
		GL30.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		int colorsVBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, colorsVBO);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, colors.getArray(), GL15.GL_STATIC_DRAW);
		GL30.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		int uvsVBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, uvsVBO);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, uvs.getArray(), GL15.GL_STATIC_DRAW);
		GL30.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		int indiciesVBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indiciesVBO);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicies.getArray(), GL15.GL_STATIC_DRAW);
		
		GL30.glBindVertexArray(0);
		
		this.r = 1;
		this.g = 1;
		this.b = 1;
		
		positions.clear();
		colors.clear();
		uvs.clear();
		indicies.clear();
		
		indexOffset = 0;
		largestIndex = -1;
		
		Model model = new Model(vao, vertexCount);
		vertexCount = 0;
		
		return model;
	}
	
	public void vertex(float x, float y, float z)
	{
		positions.add(x);
		positions.add(y);
		positions.add(z);
	}
	
	public void color(float r, float g, float b)
	{
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public void uv(float u, float v)
	{
		uvs.add(u);
		uvs.add(v);
	}
	
	public void index(int i)
	{
		i += indexOffset;
		
		colors.add(r);
		colors.add(g);
		colors.add(b);
		
		indicies.add(i);
		
		if(largestIndex < i)
			largestIndex = i;
		
		vertexCount++;
	}
	
	public void newIndicies()
	{
		indexOffset = largestIndex + 1;
	}
	
	public int getVertexCount() { return vertexCount; }
}
