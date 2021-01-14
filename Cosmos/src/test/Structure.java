package test;

import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3i;

import com.bulletphysics.dynamics.RigidBody;
import com.cornchipss.utils.Maths;

import test.blocks.Block;
import test.lights.LightMap;
import test.physx.PhysicalObject;
import test.utils.Logger;

public class Structure extends PhysicalObject
{
	private Chunk[] chunks;

	private boolean initialized = false;
	
	private int width, height, length;
	
	private int cWidth, cHeight, cLength;
	
	private LightMap lightMap;
	
	public Structure(RigidBody body, int width, int height, int length)
	{
		super(body);
		
		if(width <= 0 || height <= 0 || length <= 0)
			throw new IllegalArgumentException("A Structure's width/height/length cannot be <= 0");
		
		this.width = width;
		this.height = height;
		this.length = length;
		
		cLength = (int)Math.ceil((float)length / Chunk.LENGTH);
		cHeight = (int)Math.ceil((float)height / Chunk.HEIGHT);
		cWidth = (int)Math.ceil((float)width / Chunk.WIDTH);
		
		lightMap = new LightMap(width + 2, height + 2, length + 2);
		
		chunks = new Chunk[cLength * cHeight * cWidth];
	}
	
	public int chunksLength()
	{
		return cLength;
	}
	public int chunksHeight()
	{
		return cHeight;
	}
	public int chunksWidth()
	{
		return cWidth;
	}
	
	public Chunk[] chunks()
	{
		return chunks;
	}
	
	private int flatten(int x, int y, int z)
	{
		if(!within(x, y, z))
			throw new IndexOutOfBoundsException(x + ", " + y + ", " + z + " is out of bounds.");
		return x + cWidth * (y + cHeight * z);
	}
	
	private void chunkAt(int x, int y, int z, Chunk c)
	{
		chunks[flatten(x, y, z)] = c;
	}
	
	private Chunk chunkAt(int x, int y, int z)
	{
		return chunks[flatten(x, y, z)];
	}
	
	public Chunk chunk(int x, int y, int z)
	{
		return chunkAt(x / Chunk.WIDTH, y / Chunk.HEIGHT, z / Chunk.LENGTH);
	}
	
	public void chunk(int x, int y, int z, Chunk c)
	{
		chunkAt(x / Chunk.WIDTH, y / Chunk.HEIGHT, z / Chunk.LENGTH, c);
	}
	
	public void init()
	{
		initialized = true;
		
		for(int z = 0; z < chunksLength(); z++)
		{
			for(int y = 0; y < chunksHeight(); y++)
			{
				for(int x = 0; x < chunksWidth(); x++)
				{
					int i = flatten(x, y, z);
					chunks[i] = new Chunk(x * Chunk.WIDTH + 1, y * Chunk.HEIGHT + 1, z * Chunk.LENGTH + 1, this);
					
					chunks[i].transformMatrix(
							Maths.createTransformationMatrix(
									new Vec3(
											x * Chunk.WIDTH, 
											y * Chunk.HEIGHT, 
											z * Chunk.LENGTH), 
									Maths.blankQuaternion()));

				}
			}
		}
		
		for(int z = 0; z < chunksLength(); z++)
		{
			for(int y = 0; y < chunksHeight(); y++)
			{
				for(int x = 0; x < chunksWidth(); x++)
				{
					int i = flatten(x, y, z);
					
					chunks[i].leftNeighbor(
							within(x - 1, y, z) ? chunks[flatten(x - 1, y, z)] : null);
					chunks[i].rightNeighbor(
							within(x + 1, y, z) ? chunks[flatten(x + 1, y, z)] : null);
					chunks[i].topNeighbor(
							within(x, y + 1, z) ? chunks[flatten(x, y + 1, z)] : null);
					chunks[i].bottomNeighbor(
							within(x, y - 1, z) ? chunks[flatten(x, y - 1, z)] : null);
					chunks[i].frontNeighbor(
							within(x, y, z + 1) ? chunks[flatten(x, y, z + 1)] : null);
					chunks[i].backNeighbor(
							within(x, y, z - 1) ? chunks[flatten(x, y, z - 1)] : null);
					
				}
			}
		}
	}
	
	public void calculateLights(boolean render)
	{
		long start = System.currentTimeMillis();
		
		Vector3i[] changedArea = lightMap.calculateLightMap();
		
		long end = System.currentTimeMillis();
		
		Logger.LOGGER.debug(end - start + "ms to calculate light map");
		
		if(render)
		{
			Vector3i extremeNeg = changedArea[0];
			Vector3i extremePos = changedArea[1];
			
			int updates = 0;
			
			if(extremeNeg.x() != -1) // if it isn't -1, then none of them are negative 1
			{
				// Account for the +2 size of the light map
				extremeNeg.x += 1;
				extremeNeg.y += 1;
				extremeNeg.z += 1;
				
				extremePos.x -= 1;
				extremePos.y -= 1;
				extremePos.z -= 1;
				
				for(int cz = extremeNeg.z() / 16; cz < Math.ceil(extremePos.z() / 16.0f); cz++)
				{
					for(int cy = extremeNeg.y() / 16; cy < Math.ceil(extremePos.y() / 16.0f); cy++)
					{
						for(int cx = extremeNeg.x() / 16; cx < Math.ceil(extremePos.x() / 16.0f); cx++)
						{
							chunks[flatten(cx, cy, cz)].render();
							updates++;
						}
					}
				}
			}
			
			Logger.LOGGER.debug("Chunk Re-Renders: " + updates);
		}
	}
	
	public boolean within(int x, int y, int z)
	{
		return x >= 0 && x < cWidth && y >= 0 && y < cHeight && z >= 0 && z < cLength;
	}
	
	public boolean withinBlocks(int x, int y, int z)
	{
		return x >= 0 && x < width && y >= 0 && y < height && z >= 0 && z < length;
	}
	
	public void block(int x, int y, int z, Block b)
	{
		if(!initialized)
			init();
		
		if(withinBlocks(x, y, z))
		{
			Chunk c = chunk(x, y, z);
			
			c.block(x % Chunk.WIDTH, y % Chunk.HEIGHT, z % Chunk.LENGTH, b);
		}
		else
			throw new IndexOutOfBoundsException(x + ", " + y + ", " + z + " was out of bounds for " + width + "x" + height + "x" + length);
	}
	
	public Block block(int x, int y, int z)
	{
		if(!initialized)
			init();
		
		if(x >= 0 && x < width && y >= 0 && y < height && z >= 0 && z < length)
		{
			Chunk c = chunkAt(x, y, z);
			
			return c.block(x % Chunk.WIDTH, y % Chunk.HEIGHT, z % Chunk.LENGTH);
		}
		else
			throw new IndexOutOfBoundsException(x + ", " + y + ", " + z + " was out of bounds for " + width + "x" + height + "x" + length);
	}
	
	public Vec3 center()
	{
		Vec3 pos = new Vec3(body().getCenterOfMassPosition(new javax.vecmath.Vector3f()));
		return pos;
	}
	
	public int length() { return length; }
	public int height() { return height; }
	public int width() { return width; }

	public Matrix4fc transformMatrix()
	{
		return Maths.createTransformationMatrix(
				new Vec3(body().getCenterOfMassPosition(new javax.vecmath.Vector3f())).sub(new Vec3(width() / 2.0f, height / 2.0f, length / 2.0f)), 
				Maths.blankQuaternion()); // TODO: put rotation here
	}
	
	public LightMap lightMap()
	{
		return lightMap;
	}

	public void removeBlock(int x, int y, int z)
	{
		block(x, y, z, null);
	}
}
