package com.cornchipss.rendering.debug;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.newdawn.slick.Color;

import com.cornchipss.rendering.Renderer;
import com.cornchipss.rendering.shaders.DebugShader;
import com.cornchipss.rendering.shaders.Shader;
import com.cornchipss.utils.Maths;
import com.cornchipss.world.entities.Player;

public class DebugRenderer extends Renderer
{
	private static DebugRenderer instance;
	
	private int transformMatrixLoc;
	
	static
	{
		instance = new DebugRenderer(new DebugShader());
	}
	
	public DebugRenderer(Shader shader)
	{
		super(shader);
		
		instance = this;
	}

	private static List<DebugShape> shapes = new ArrayList<>();
	
	public static void drawLine(Vector3fc start, Vector3fc end, Color color)
	{
		shapes.add(new DebugLine(start, end, color));
	}
	
	public static void draw(Player player)
	{
		instance.setupRender(player);
		
		for(DebugShape s : shapes)
		{
			instance.getShader().loadUniformMatrix(instance.transformMatrixLoc, s.matrix());
			
			s.draw();
			
			//s.draw();
		}
		
		instance.stopRender();
		
		shapes.clear();
	}
	
	public static void drawSquare(Vector3fc vector3f)
	{
		DebugRectangle rs = new DebugRectangle(Color.blue, new Vector3f(0, 0, 0), Maths.blankQuaternion(), vector3f);
		shapes.add(rs);
	}

	@Override
	protected void loadTextures()
	{
		
	}

	@Override
	protected void loadUniforms()
	{
		
	}

	@Override
	protected void bindTextures()
	{
		
	}

	@Override
	protected void loadUniformLocations()
	{
		transformMatrixLoc = getShader().getUniformLocation("u_transformation_matrix");
	}

	public static void drawShape(DebugShape x)
	{
		shapes.add(x);
	}
}
