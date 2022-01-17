package tests.structure;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.joml.Vector3f;
import org.joml.Vector3i;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cornchipss.cosmos.blocks.Block;
import com.cornchipss.cosmos.blocks.Blocks;
import com.cornchipss.cosmos.physx.Orientation;
import com.cornchipss.cosmos.physx.Transform;
import com.cornchipss.cosmos.physx.collision.obb.OBBCollider;
import com.cornchipss.cosmos.structures.Planet;
import com.cornchipss.cosmos.structures.Structure;
import com.cornchipss.cosmos.utils.Maths;
import com.cornchipss.cosmos.world.World;

class TestStructure
{
	private static void fill(Structure s, Block b)
	{
		for (int z = 0; z < s.length(); z++)
		{
			for (int y = 0; y < s.height(); y++)
			{
				for (int x = 0; x < s.width(); x++)
				{
					s.block(x, y, z, b);
				}
			}
		}
	}

	Structure a;

	private void init(int w, int h, int l)
	{
		World wd = new World();

		a = new Planet(wd, w, h, l, 1);

		fill(a, Blocks.STONE);

		a.addToWorld(new Transform());
	}

	@BeforeEach
	void before()
	{

	}

	@AfterEach
	void after()
	{
		a = null;
	}

//	private static final float EPSILON = 1e-5f;

//	private static void assertVectorEquals(Vector3fc expected, Vector3fc actual)
//	{
//		if (expected.x() - actual.x() > EPSILON || expected.y() - actual.y() > EPSILON
//			|| expected.z() - actual.z() > EPSILON)
//		{
//			assertEquals(expected, actual); // this will be false but give nice printout
//		}
//		else
//			assertTrue(true); // it passed
//	}

	@Test
	void blocks()
	{
		final int W = 16, H = 16, L = 16;

		init(W, H, L);

		Vector3i temp = new Vector3i(), out = new Vector3i();
		
		a.chunkCoordsToBlockCoords(a.chunk(0, 0, 0), temp, out);
		
		assertEquals(new Vector3i(), out);
		
		OBBCollider c = a.obbForBlock(out);

		assertEquals(new OBBCollider(new Vector3f(0, 0, 0), new Orientation(), new Vector3f(8, 8, 8)),
			a.obbForChunk(a.chunk(0, 0, 0)));

		assertEquals(
			new OBBCollider(new Vector3f(-7.5f, -7.5f, -7.5f), new Orientation(), new Vector3f(0.5f, 0.5f, 0.5f)), c);

		init(W + W, H + H, L + L);
		
		a.chunkCoordsToBlockCoords(a.chunk(0, 0, 0), temp, out);
		
		c = a.obbForBlock(out);

		assertEquals(
			new OBBCollider(new Vector3f(-15.5f, -15.5f, -15.5f), new Orientation(), new Vector3f(0.5f, 0.5f, 0.5f)),
			c);

		a.body().transform().orientation().rotateRelative(new Vector3f(0, Maths.PI, 0));
		a.body().transform().position(new Vector3f(100, 0, 0));
		
		temp.x = 15;
		a.chunkCoordsToBlockCoords(a.chunk(0, 0, 0), temp, out);
		
		c = a.obbForBlock(out);

		assertEquals(new OBBCollider(new Vector3f(100.5f, -15.5f, 15.5f), a.body().transform().orientation(),
			new Vector3f(0.5f, 0.5f, 0.5f)), c);
	}
}
