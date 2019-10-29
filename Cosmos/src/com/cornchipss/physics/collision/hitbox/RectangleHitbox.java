package com.cornchipss.physics.collision.hitbox;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.physics.shapes.Rectangle;
import com.cornchipss.utils.Utils;
import com.cornchipss.world.blocks.BlockFace;

public class RectangleHitbox extends Hitbox implements Rectangle
{
	private final Vector3fc[] corners;
	private final Vector3fc[] triangles;
	private final BlockFace[] faces;
	private final Vector3fc dimensions;
	
	/**
	 * A hitbox that represents a rectangle
	 * @param width The total width of each side
	 * @param height The total height of each side
	 * @param length The total length of each side
	 */
	public RectangleHitbox(float width, float height, float length)
	{
		this(new Vector3f(-width / 2f, -height / 2f, -length / 2f), 
				new Vector3f(width / 2f, height / 2f, length / 2f));
	}
	
	/**
	 * A hitbox that represents a rectangle
	 * @param start The start of the rectangle
	 * @param width How far out from the start does it go (x)
	 * @param height How far out from the start does it go (y)
	 * @param length How far out from the start does it go (z)
	 */
	public RectangleHitbox(Vector3fc start, 
			float width, float height, float length)
	{
		this(start, new Vector3f(width, height, length));
	}
	
	/**
	 * A hitbox that represents a rectangle
	 * @param start The first corner
	 * @param end The second corner
	 */
	public RectangleHitbox(Vector3fc start, Vector3fc end)
	{
		corners = new Vector3f[2];
		
		corners[0] = new Vector3f(start);
		corners[1] = new Vector3f(end);
		
		triangles = new Vector3fc[6 * 2 * 3]; // 6 faces, 2 triangles per face, 3 verticies per triangle
		faces = new BlockFace[6 * 2];
		
		// TOP
		
		triangles[0] = new Vector3f(start);
		triangles[1] = new Vector3f(end.x(), start.y(), end.z());
		triangles[2] = new Vector3f(end.x(), start.y(), start.z());
		
		triangles[3] = new Vector3f(start);
		triangles[4] = new Vector3f(end.x(), start.y(), end.z());
		triangles[5] = new Vector3f(start.x(), start.y(), end.z());
		
		faces[0] = BlockFace.TOP;
		faces[1] = BlockFace.TOP;
		
		// BOTTOM
		
		triangles[6] = new Vector3f(end);
		triangles[7] = new Vector3f(start.x(), end.y(), end.z());
		triangles[8] = new Vector3f(start.x(), end.y(), start.z());
		
		triangles[9] = new Vector3f(end);
		triangles[10] = new Vector3f(end.x(), end.y(), start.z());
		triangles[11] = new Vector3f(start.x(), end.y(), end.z());
		
		faces[2] = BlockFace.BOTTOM;
		faces[3] = BlockFace.BOTTOM;
		
		// LEFT
		
		triangles[12] = new Vector3f(start);
		triangles[13] = new Vector3f(start.x(), start.y(), end.z());
		triangles[14] = new Vector3f(start.x(), end.y(), end.z());
		
		triangles[15] = new Vector3f(start);
		triangles[16] = new Vector3f(start.x(), end.y(), start.z());
		triangles[17] = new Vector3f(start.x(), end.y(), end.z());
		
		faces[4] = BlockFace.LEFT;
		faces[5] = BlockFace.LEFT;
		
		// RIGHT
		
		triangles[18] = new Vector3f(end);
		triangles[19] = new Vector3f(end.x(), start.y(), end.z());
		triangles[20] = new Vector3f(end.x(), start.y(), start.z());
		
		triangles[21] = new Vector3f(end);
		triangles[22] = new Vector3f(end.x(), end.y(), start.z());
		triangles[23] = new Vector3f(end.x(), start.y(), start.z());
		
		faces[6] = BlockFace.RIGHT;
		faces[7] = BlockFace.RIGHT;
		
		// FRONT
		
		triangles[24] = new Vector3f(start);
		triangles[25] = new Vector3f(end.x(), start.y(), start.z());
		triangles[26] = new Vector3f(end.x(), end.y(), start.z());
		
		triangles[27] = new Vector3f(start);
		triangles[28] = new Vector3f(start.x(), end.y(), start.z());
		triangles[29] = new Vector3f(end.x(), end.y(), start.z());
		
		faces[8] = BlockFace.FRONT;
		faces[9] = BlockFace.FRONT;
		
		// BACK
		
		triangles[30] = new Vector3f(end);
		triangles[31] = new Vector3f(start.x(), start.y(), end.z());
		triangles[32] = new Vector3f(start.x(), end.y(), end.z());
		
		triangles[33] = new Vector3f(end);
		triangles[34] = new Vector3f(end.x(), start.y(), end.z());
		triangles[35] = new Vector3f(start.x(), start.y(), end.z());
		
		faces[10] = BlockFace.BACK;
		faces[11] = BlockFace.BACK;
		
		dimensions = Utils.sub(end, start);
	}
	
	@Override
	public Vector3fc[] getCorners()
	{
		return corners;
	}
	
	@Override
	public Vector3fc getBoundingBox()
	{
		return getDimensions();
	}

	@Override
	public Vector3fc getDimensions()
	{
		return dimensions;
	}

	@Override
	public Vector3fc getPosition()
	{
		return corners[0];
	}

	@Override
	public Vector3fc[] getTriangles()
	{
		return triangles;
	}

	@Override
	public BlockFace[] getFaces()
	{
		return faces;
	}
}
