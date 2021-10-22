package tests.physx.collision;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cornchipss.cosmos.blocks.Block;
import com.cornchipss.cosmos.blocks.Blocks;
import com.cornchipss.cosmos.physx.Transform;
import com.cornchipss.cosmos.physx.collision.CollisionInfo;
import com.cornchipss.cosmos.physx.collision.DefaultCollisionChecker;
import com.cornchipss.cosmos.structures.Planet;
import com.cornchipss.cosmos.structures.Structure;
import com.cornchipss.cosmos.utils.Utils;
import com.cornchipss.cosmos.world.World;

class DefaultCollisionCheckerTest
{
	private static void fill(Structure s, Block b)
	{
		for(int z = 0; z < s.length(); z++)
		{
			for(int y = 0; y < s.height(); y++)
			{
				for(int x = 0; x < s.width(); x++)
				{
					s.block(x, y, z, b);
				}
			}
		}
	}
	
	Structure a, b;
	DefaultCollisionChecker dcc;
	CollisionInfo info;

	@BeforeEach
	void before()
	{
		World w = new World();
		
		a = new Planet(w, 16*4, 16*4, 16*4, 1);
		b = new Planet(w, 16*4, 16*4, 16*4, 2);
		
		fill(a, Blocks.STONE);
		fill(b, Blocks.STONE);
		
		a.addToWorld(new Transform());
		b.addToWorld(new Transform());
		
		dcc = new DefaultCollisionChecker();
		
		info = new CollisionInfo();
	}
	
	@AfterEach
	void after()
	{
		a = b = null;
		dcc = null;
		info = null;
	}
	
	private static final float EPSILON = 1e-5f;
	
	private static void assertVectorEquals(Vector3fc expected, Vector3fc actual)
	{
		if(expected.x() - actual.x() > EPSILON 
				|| expected.y() - actual.y() > EPSILON
				|| expected.z() - actual.z() > EPSILON)
		{
			assertEquals(expected, actual); // this will be false but give nice printout
		}
		else
			assertTrue(true); // it passed
	}
	
//	@Test
	void emptyStructure()
	{
		fill(a, null);
		
		a.body().transform().position(new Vector3f(-20, 0, 0));
		assertFalse(dcc.colliding(a, b, new Vector3f(20, 0, 0), null));
	}
	
//	@Test
	void structurePosX()
	{
		a.body().transform().position(new Vector3f(-20, 0, 0));
		assertTrue(dcc.colliding(a, b, new Vector3f(20, 0, 0), info));
		
		assertVectorEquals(new Vector3f(-1, 0, 0), info.normal);
	}
	
//	@Test
	void structureNegX()
	{
		a.body().transform().position(new Vector3f(20, 0, 0));
		assertTrue(dcc.colliding(a, b, new Vector3f(-20, 0, 0), info));
		
		assertVectorEquals(new Vector3f(1, 0, 0), info.normal);
	}
	
//	@Test
	void structureFarAwayX()
	{
		a.body().transform().position(new Vector3f(200, 0, 0));
		b.body().transform().position(new Vector3f(190, 0, 0));
		
		assertTrue(dcc.colliding(a, b, new Vector3f(-20, 0, 0), info));
		
		assertVectorEquals(new Vector3f(-1, 0, 0), info.normal);
	}
	
	@Test
	void structureFarAway()
	{	
		float dx = 0.1f;
		float dy = 0.1f;
		float dz = 0.1f;
		
		a.body().transform().position(new Vector3f(100+a.width() / 2.f + dx, 100 + a.height() / 2.f + dy, 100 + a.length() / 2.f + dz));
		b.body().transform().position(new Vector3f(100, 100, 100));
		
		assertTrue(dcc.colliding(a, b, new Vector3f(-2 * dx, -2 * dy, -2 * dz), info));
		
		Utils.println(info.normal);
	}
}
