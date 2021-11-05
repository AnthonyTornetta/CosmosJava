package tests.structure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.joml.Vector3f;
import org.joml.Vector3fc;
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

	private static final float EPSILON = 1e-5f;

	private static void assertVectorEquals(Vector3fc expected, Vector3fc actual)
	{
		if (expected.x() - actual.x() > EPSILON || expected.y() - actual.y() > EPSILON
			|| expected.z() - actual.z() > EPSILON)
		{
			assertEquals(expected, actual); // this will be false but give nice printout
		}
		else
			assertTrue(true); // it passed
	}

	@Test
	void blocks()
	{
		final int W = 16, H = 16, L = 16;

		init(W, H, L);

		OBBCollider c = a.obbForBlock(a.chunk(0, 0, 0), 0, 0, 0);

		assertEquals(new OBBCollider(new Vector3f(0, 0, 0), new Orientation(), new Vector3f(8, 8, 8)),
			a.obbForChunk(a.chunk(0, 0, 0)));

		assertEquals(
			new OBBCollider(new Vector3f(-7.5f, -7.5f, -7.5f), new Orientation(), new Vector3f(0.5f, 0.5f, 0.5f)), c);

		init(W + W, H + H, L + L);

		c = a.obbForBlock(a.chunk(0, 0, 0), 0, 0, 0);

		assertEquals(
			new OBBCollider(new Vector3f(-15.5f, -15.5f, -15.5f), new Orientation(), new Vector3f(0.5f, 0.5f, 0.5f)),
			c);

		a.body().transform().orientation().rotateRelative(new Vector3f(0, Maths.PI, 0));
		a.body().transform().position(new Vector3f(100, 0, 0));
		c = a.obbForBlock(a.chunk(0, 0, 0), 15, 0, 0);

		assertEquals(new OBBCollider(new Vector3f(100.5f, -15.5f, 15.5f), a.body().transform().orientation(),
			new Vector3f(0.5f, 0.5f, 0.5f)), c);
	}

	@Test
	void structureEven()
	{
		init(32, 32, 32);

		a.body().transform().position(new Vector3f(100, 150, 200));

		assertVectorEquals(new Vector3f(-8, -8, -8), a.chunk(0, 0, 0).relativePosition());
		assertVectorEquals(new Vector3f(-8, -8, -8), a.chunkRelativePosCentered(a.chunk(0, 0, 0), new Vector3f()));

		assertVectorEquals(new Vector3f(100 - 8, 150 - 8, 200 - 8),
			a.chunkWorldPosCentered(a.chunk(0, 0, 0), new Vector3f()));

		assertVectorEquals(new Vector3f(8, 8, 8), a.chunk(1, 1, 1).relativePosition());
		assertVectorEquals(new Vector3f(8, 8, 8), a.chunkRelativePosCentered(a.chunk(1, 1, 1), new Vector3f()));

		assertVectorEquals(new Vector3f(100 + 8, 150 + 8, 200 + 8),
			a.chunkWorldPosCentered(a.chunk(1, 1, 1), new Vector3f()));
	}

	@Test
	void structureOdd()
	{
		init(48, 48, 48);

		a.body().transform().position(new Vector3f(100, 150, 200));

		assertVectorEquals(new Vector3f(-16, -16, -16), a.chunk(0, 0, 0).relativePosition());
		assertVectorEquals(new Vector3f(-16, -16, -16), a.chunkRelativePosCentered(a.chunk(0, 0, 0), new Vector3f()));

		assertVectorEquals(new Vector3f(100 - 16, 150 - 16, 200 - 16),
			a.chunkWorldPosCentered(a.chunk(0, 0, 0), new Vector3f()));

		assertVectorEquals(new Vector3f(16, 16, 16), a.chunk(2, 2, 2).relativePosition());
		assertVectorEquals(new Vector3f(16, 16, 16), a.chunkRelativePosCentered(a.chunk(2, 2, 2), new Vector3f()));

		assertVectorEquals(new Vector3f(100 + 16, 150 + 16, 200 + 16),
			a.chunkWorldPosCentered(a.chunk(2, 2, 2), new Vector3f()));

		Vector3f pos = new Vector3f(a.body().transform().position());
		a.body().transform().position(new Vector3f());
		a.body().transform().orientation().rotateRelative(new Vector3f(0, Maths.PI, 0));
		a.body().transform().position(pos);

		assertVectorEquals(new Vector3f(100 - 16, 150 + 16, 200 - 16),
			a.chunkWorldPosCentered(a.chunk(2, 2, 2), new Vector3f()));
	}
}
