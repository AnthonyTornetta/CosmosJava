package tests.physx.collision;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.joml.Vector3f;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cornchipss.cosmos.blocks.Block;
import com.cornchipss.cosmos.blocks.Blocks;
import com.cornchipss.cosmos.physx.Transform;
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
	Vector3f normal;

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
		
		normal = new Vector3f();
	}
	
	@AfterEach
	void after()
	{
		a = b = null;
		dcc = null;
		normal = null;
	}
	
	@Test
	void twoStructures()
	{
		// 0 //
		
		a.body().transform().position(new Vector3f(0, 0, 0));
		assertTrue(dcc.colliding(a, b, normal));
		
		// X //		
		
		a.body().transform().position(new Vector3f(8.0f, 0, 0));
		assertTrue(dcc.colliding(a, b, normal));
		
		a.body().transform().position(new Vector3f(15.95f, 0, 0));
		assertTrue(dcc.colliding(a, b, normal));
		
		a.body().transform().position(new Vector3f(0, 16.05f, 0));
		assertFalse(dcc.colliding(a, b, normal));
		
		// Y //
		
		a.body().transform().position(new Vector3f(0, 8.0f, 0));
		assertTrue(dcc.colliding(a, b, normal));
		
		a.body().transform().position(new Vector3f(0, 15.95f, 0));
		assertTrue(dcc.colliding(a, b, normal));
		
		a.body().transform().position(new Vector3f(0, 16.05f, 0));
		assertFalse(dcc.colliding(a, b, normal));
		
		// Z //
		
		a.body().transform().position(new Vector3f(0, 0, 8.0f));
		assertTrue(dcc.colliding(a, b, normal));
		
		a.body().transform().position(new Vector3f(0, 0, 15.95f));
		assertTrue(dcc.colliding(a, b, normal));
		
		a.body().transform().position(new Vector3f(0, 0, 16.05f));
		assertFalse(dcc.colliding(a, b, normal));
	}
}
