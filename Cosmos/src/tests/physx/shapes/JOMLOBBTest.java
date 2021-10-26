package tests.physx.shapes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cornchipss.cosmos.physx.Orientation;
import com.cornchipss.cosmos.physx.collision.CollisionInfo;
import com.cornchipss.cosmos.physx.collision.obb.OBBCollider;
import com.cornchipss.cosmos.physx.collision.obb.OBBCollisionCheckerJOML;
import com.cornchipss.cosmos.utils.Maths;

class JOMLOBBTest
{
	private OBBCollider a, b;
	private OBBCollisionCheckerJOML obbChecker;
	private CollisionInfo info;
	
	@BeforeEach
	public void before()
	{
		a = new OBBCollider(new Vector3f(0, 0, 0), new Orientation(), new Vector3f(0.5f, 0.5f, 0.5f));
		obbChecker = new OBBCollisionCheckerJOML();
		info = new CollisionInfo();
	}
	
	private static final float EPSILON = 1e-5f;
	
	private void assertVecEquals(Vector3fc a, Vector3fc b)
	{
		if(Math.abs(a.x() - b.x()) < EPSILON && Math.abs(a.y() - b.y()) < EPSILON && Math.abs(a.z() - b.z()) < EPSILON)
			assertTrue(true);
		else
			assertEquals(a, b);
	}
	
	@Test
	void testMoving2()
	{
		OBBCollider a = new OBBCollider(new Vector3f(-22.5f, -21.1f, -25.1f), new Orientation(), new Vector3f(80, 80, 80));
		OBBCollider b = new OBBCollider(new Vector3f(0.0f, -80.0f, 0.0f), new Orientation(), new Vector3f(80.0f, 40.0f, 80.0f));
		
		assertTrue(obbChecker.testMovingOBBOBB(new Vector3f(0.1f, 0, 0), a, b, info));
	}
	
	@Test
	void testMoving()
	{
		// y
		
		b = new OBBCollider(new Vector3f(0, 2.0f, 0), new Orientation(), new Vector3f(0.5f, 0.5f, 0.5f));
		assertTrue(obbChecker.testMovingOBBOBB(new Vector3f(0, 10.0f, 0), a, b, info));
		
		assertVecEquals(new Vector3f(0, -1.0f, 0), info.normal);
		
		b = new OBBCollider(new Vector3f(0, -2.0f, 0), new Orientation(), new Vector3f(0.5f, 0.5f, 0.5f));
		assertTrue(obbChecker.testMovingOBBOBB(new Vector3f(0, -10.0f, 0), a, b, info));
		
		assertVecEquals(new Vector3f(0, 1.0f, 0), info.normal);
		
		// x
		
		b = new OBBCollider(new Vector3f(2.0f, 0, 0), new Orientation(), new Vector3f(0.5f, 0.5f, 0.5f));
		assertTrue(obbChecker.testMovingOBBOBB(new Vector3f(10.0f, 0, 0), a, b, info));
		
		assertVecEquals(new Vector3f(-1.0f, 0, 0), info.normal);
		
		b = new OBBCollider(new Vector3f(-2.0f, 0, 0), new Orientation(), new Vector3f(0.5f, 0.5f, 0.5f));
		assertTrue(obbChecker.testMovingOBBOBB(new Vector3f(-10.0f, 0, 0), a, b, info));
		
		assertVecEquals(new Vector3f(1.0f, 0, 0), info.normal);
		
		// z
		
		b = new OBBCollider(new Vector3f(0, 0, 2.0f), new Orientation(), new Vector3f(0.5f, 0.5f, 0.5f));
		assertTrue(obbChecker.testMovingOBBOBB(new Vector3f(0, 0, 10.0f), a, b, info));
		
		assertVecEquals(new Vector3f(0, 0, -1.0f), info.normal);
		
		b = new OBBCollider(new Vector3f(0, 0, -2.0f), new Orientation(), new Vector3f(0.5f, 0.5f, 0.5f));
		assertTrue(obbChecker.testMovingOBBOBB(new Vector3f(0, 0, -10.0f), a, b, info));
		
		assertVecEquals(new Vector3f(0, 0, 1.0f), info.normal);
	}
	
	@Test
	void test()
	{
		b = new OBBCollider(new Vector3f(0, 0.7f, 0), new Orientation(), new Vector3f(0.5f, 0.5f, 0.5f));
		assertTrue(obbChecker.testOBBOBB(a, b));
		
		b = new OBBCollider(new Vector3f(-20, 0, 0), new Orientation(), new Vector3f(0.5f, 0.5f, 0.5f));
		assertFalse(obbChecker.testOBBOBB(a, b));
		
		b = new OBBCollider(new Vector3f(20, 0, 0), new Orientation(), new Vector3f(0.5f, 0.5f, 0.5f));
		assertFalse(obbChecker.testOBBOBB(a, b));
		
		b = new OBBCollider(new Vector3f(0, 20, 0), new Orientation(), new Vector3f(0.5f, 0.5f, 0.5f));
		assertFalse(obbChecker.testOBBOBB(a, b));
		
		b = new OBBCollider(new Vector3f(0, -20, 0), new Orientation(), new Vector3f(0.5f, 0.5f, 0.5f));
		assertFalse(obbChecker.testOBBOBB(a, b));
		
		b = new OBBCollider(new Vector3f(0, 0, 20), new Orientation(), new Vector3f(0.5f, 0.5f, 0.5f));
		assertFalse(obbChecker.testOBBOBB(a, b));
		
		b = new OBBCollider(new Vector3f(0, 0, -20), new Orientation(), new Vector3f(0.5f, 0.5f, 0.5f));
		assertFalse(obbChecker.testOBBOBB(a, b));
		
		b = new OBBCollider(new Vector3f(0, 0, 0), new Orientation(), new Vector3f(0.5f, 0.5f, 0.5f));
		assertTrue(obbChecker.testOBBOBB(a, b));
		
		b = new OBBCollider(new Vector3f(0, 1.1f, 0), new Orientation(Maths.quaternionFromRotation(Maths.PI2 / 2.f, 0, 0)), new Vector3f(0.5f, 0.5f, 0.5f));
		assertTrue(obbChecker.testOBBOBB(a, b));
	}
}
