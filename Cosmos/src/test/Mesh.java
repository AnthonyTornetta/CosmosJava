package test;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

public class Mesh
{
	final private int vao;
	final private int verticies;
	
	private Mesh(int verticies)
	{
		vao = GL30.glGenVertexArrays(); 
		this.verticies = verticies;
	}
	
	public void storeData(int index, int dimensions, float[] data)
	{
		int vbo = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		
		FloatBuffer dataBuffer = BufferUtils.createFloatBuffer(data.length);
		dataBuffer.put(data);
		dataBuffer.flip();

		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, dataBuffer, GL15.GL_STATIC_DRAW);
		GL30.glVertexAttribPointer(index, dimensions, GL11.GL_FLOAT, false, 0, 0);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	public void storeIndicies(int[] data)
	{
		int vbo = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo);
		
		IntBuffer buf = BufferUtils.createIntBuffer(data.length);
		buf.put(data);
		buf.flip();
		
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buf, GL15.GL_STATIC_DRAW);
	}
	
	public static Mesh createMesh(float[] verticies, int[] indicies, float[] uv)
	{
		Mesh m = new Mesh(indicies.length);
		
		GL30.glBindVertexArray(m.vao());
		
		m.storeData(0, 3, verticies);
		
		// color is index 1. I ignore this in frag shader
		m.storeData(2, 4, uv);

		m.storeIndicies(indicies);
		
		GL30.glBindVertexArray(0);
		
		return m;
	}

	public int vao()
	{
		return vao;
	}
	
	public int verticies()
	{
		return verticies;
	}
}
