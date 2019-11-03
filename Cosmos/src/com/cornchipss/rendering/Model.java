package com.cornchipss.rendering;

public class Model
{
	private int vao, vertexCount;
	
	public Model(int vao, int vertexCount)
	{
		this.vao = vao;
		this.vertexCount = vertexCount;
	}
	
	public int getVao() { return vao; }
	public int getVertexCount() { return vertexCount; }
	
	@Override
	public String toString()
	{
		return "Model [vertexCount:" + getVertexCount() + ", vao:" + getVao() + "]";
	}
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof Model && ((Model)o).getVao() == getVao();
	}
}
