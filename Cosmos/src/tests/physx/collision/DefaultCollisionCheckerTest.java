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
		
		a = new Planet(w, 16, 16, 16, 1);
		b = new Planet(w, 16, 16, 16, 2);
		
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
	
	// 0 //

	@Test
	void twoStructures()
	{
		
		a.body().transform().position(new Vector3f(0, 0, 0));
		assertTrue(dcc.colliding(a, b, new Vector3f(), null));
	}
	
	// X //		
	
	@Test
	void twoStructuresX2Neg()
	{
		a.body().transform().position(new Vector3f(-8.0f, 0, 0));
		assertTrue(dcc.colliding(a, b, new Vector3f(), null));
	}
	
	@Test
	void twoStructuresX3Neg()
	{
		a.body().transform().position(new Vector3f(-15.45f, 0, 0));
		assertTrue(dcc.colliding(a, b, new Vector3f(), null));
	}
	
	@Test 
	void twoStructuresX1()		
	{
		a.body().transform().position(new Vector3f(8.0f, 0, 0));
		assertTrue(dcc.colliding(a, b, new Vector3f(), null));
	}
	
	@Test 
	void twoStructuresX2()		
	{
		a.body().transform().position(new Vector3f(15.95f, 0, 0));
		assertTrue(dcc.colliding(a, b, new Vector3f(), null));
	}
	
	@Test 
	void twoStructuresX3()		
	{
		a.body().transform().position(new Vector3f(0, 16.05f, 0));
		assertFalse(dcc.colliding(a, b, new Vector3f(), null));
	}
	
//	@Test
	void testPosXNormal()
	{
		a.body().transform().position(new Vector3f(15.95f, -.01f, -0.01f));
		dcc.colliding(a, b, new Vector3f(), info);
		
		assertVectorEquals(new Vector3f(1, 0, 0), info.normal);
	}
	
//	@Test
	void testNegXNormal()
	{
		a.body().transform().position(new Vector3f(-15.45f, -.01f, -0.01f));
		dcc.colliding(a, b, new Vector3f(), info);
		
		assertVectorEquals(new Vector3f(-1, 0, 0), info.normal);
	}
	
	// Y //
	
	@Test 
	void twoStructuresY1()		
	{
		a.body().transform().position(new Vector3f(0, 8.0f, 0));
		assertTrue(dcc.colliding(a, b, new Vector3f(), null));
	}
	
	@Test 
	void twoStructuresY2()		
	{
		a.body().transform().position(new Vector3f(0, 15.95f, 0));
		assertTrue(dcc.colliding(a, b, new Vector3f(), null));
	}
	
	@Test 
	void twoStructuresY3()		
	{
		a.body().transform().position(new Vector3f(0, 16.05f, 0));
		assertFalse(dcc.colliding(a, b, new Vector3f(), null));
	}
	
	@Test
	void testPosYNormal()
	{
		a.body().transform().position(new Vector3f(-0.01f, 15.95f, -0.01f));
		dcc.colliding(a, b, new Vector3f(), info);
		
		assertVectorEquals(new Vector3f(0, 1, 0), info.normal);
	}
	
//	@Test
	void testNegYNormal()
	{
		a.body().transform().position(new Vector3f(-0.01f, -15.45f, -0.01f));
		dcc.colliding(a, b, new Vector3f(), info);
		
		assertVectorEquals(new Vector3f(0, -1, 0), info.normal);
	}
	
	// Z //
	
	@Test 
	void twoStructuresZ1()		
	{
		a.body().transform().position(new Vector3f(0, 0, 8.0f));
		assertTrue(dcc.colliding(a, b, new Vector3f(), null));
	}
	
	@Test 
	void twoStructuresZ2()		
	{
		a.body().transform().position(new Vector3f(0, 0, 15.95f));
		assertTrue(dcc.colliding(a, b, new Vector3f(), null));
	}
	
	@Test 
	void twoStructuresZ3()		
	{
		a.body().transform().position(new Vector3f(0, 0, 16.05f));
		assertFalse(dcc.colliding(a, b, new Vector3f(), null));
	}
	
//	@Test
	void testPosZNormal()
	{
		a.body().transform().position(new Vector3f(-0.01f, -0.01f, 15.95f));
		dcc.colliding(a, b, new Vector3f(), info);
		
		assertVectorEquals(new Vector3f(0, 0, 1), info.normal);
	}
	
//	@Test
	void testNegZNormal()
	{
		a.body().transform().position(new Vector3f(-0.01f, -0.01f, -15.45f));
		dcc.colliding(a, b, new Vector3f(), info);
		
		assertVectorEquals(new Vector3f(0, 0, -1), info.normal);
	}
}
