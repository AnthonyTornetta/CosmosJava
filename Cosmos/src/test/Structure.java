package test;

import org.joml.Matrix4fc;
import org.joml.Vector3f;

import com.cornchipss.physics.Transform;
import com.cornchipss.utils.Maths;
import com.cornchipss.utils.Utils;

import test.blocks.Block;

public class Structure
{
	private Chunk[] chunks;

	private boolean initialized = false;
	
	private int width, height, length;
	
	private int cWidth, cHeight, cLength;
	
	private Transform transform;
	
	private float[][][] lightMap;
	
	public Structure(Transform trans, int width, int height, int length)
	{
		if(width <= 0 || height <= 0 || length <= 0)
			throw new IllegalArgumentException("A Structure's width/height/length cannot be <= 0");
		
		this.transform = trans;
		
		this.width = width;
		this.height = height;
		this.length = length;
		
		cLength = (int)Math.ceil((float)length / Chunk.LENGTH);
		cHeight = (int)Math.ceil((float)height / Chunk.HEIGHT);
		cWidth = (int)Math.ceil((float)width / Chunk.WIDTH);
		
		lightMap = new float[length][height][width];
		
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
					chunks[i] = new Chunk(x * Chunk.WIDTH, y * Chunk.HEIGHT, z * Chunk.LENGTH, this);
					
					chunks[i].transformMatrix(
							Maths.createTransformationMatrix(
									new Vector3f(
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
	
	public void calculateLights()
	{
		long start = System.currentTimeMillis();
		
		for(int z = 0; z < cLength; z++)
		{
			for(int y = 0; y < cHeight; y++)
			{
				for(int x = 0; x < cWidth; x++)
				{
					chunks[flatten(x, y, z)].calculateLightMap();
				}
			}
		}
		
		Utils.println(System.currentTimeMillis() - start + "ms to calculate light map");
	}
	
	public boolean within(int x, int y, int z)
	{
		return x >= 0 && x < cWidth && y >= 0 && y < cHeight && z >= 0 && z < cLength;
	}
	
	public void block(int x, int y, int z, Block b)
	{
		if(!initialized)
			init();
		
		if(x >= 0 && x < width && y >= 0 && y < height && z >= 0 && z < length)
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
	
	public Vector3f center()
	{
		return new Vector3f(transform.position().x() + width / 2.0f, 
						transform.position().y() + height / 2.0f, 
						transform.position().z() + length / 2.0f);
	}
	
	public int length() { return length; }
	public int height() { return height; }
	public int width() { return width; }

	public Matrix4fc transformMatrix()
	{
		return transform.asMatrix();
	}
	
	public Transform transform()
	{
		return transform;
	}

	public float[][][] lightMap()
	{
		return lightMap;
	}
}
