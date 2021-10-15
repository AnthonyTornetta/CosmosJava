package tests.physx.shapes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cornchipss.cosmos.physx.Orientation;
import com.cornchipss.cosmos.physx.collision.obb.OBBCollider;
import com.cornchipss.cosmos.physx.collision.obb.OBBCollisionCheckerJOML;
import com.cornchipss.cosmos.utils.Maths;
import com.cornchipss.cosmos.utils.Utils;

class JOMLOBBTest
{
	private OBBCollider a, b;
	private OBBCollisionCheckerJOML obbChecker;
	private Vector3f normal;
	
	@BeforeEach
	public void before()
	{
		a = new OBBCollider(new Vector3f(0, 0, 0), new Orientation(), new Vector3f(0.5f, 0.5f, 0.5f));
		obbChecker = new OBBCollisionCheckerJOML();
		normal = new Vector3f();
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
	void testMoving()
	{
		// y
		
		b = new OBBCollider(new Vector3f(0, 2.0f, 0), new Orientation(), new Vector3f(0.5f, 0.5f, 0.5f));
		assertTrue(obbChecker.testMovingOBBOBB(new Vector3f(0, 10.0f, 0), a, b, normal));
		
		assertVecEquals(new Vector3f(0, -1.0f, 0), normal);
		
		b = new OBBCollider(new Vector3f(0, -2.0f, 0), new Orientation(), new Vector3f(0.5f, 0.5f, 0.5f));
		assertTrue(obbChecker.testMovingOBBOBB(new Vector3f(0, -10.0f, 0), a, b, normal));
		
		assertVecEquals(new Vector3f(0, 1.0f, 0), normal);
		
		// x
		
		b = new OBBCollider(new Vector3f(2.0f, 0, 0), new Orientation(), new Vector3f(0.5f, 0.5f, 0.5f));
		assertTrue(obbChecker.testMovingOBBOBB(new Vector3f(10.0f, 0, 0), a, b, normal));
		
		assertVecEquals(new Vector3f(-1.0f, 0, 0), normal);
		
		b = new OBBCollider(new Vector3f(-2.0f, 0, 0), new Orientation(), new Vector3f(0.5f, 0.5f, 0.5f));
		assertTrue(obbChecker.testMovingOBBOBB(new Vector3f(-10.0f, 0, 0), a, b, normal));
		
		assertVecEquals(new Vector3f(1.0f, 0, 0), normal);
		
		// z
		
		b = new OBBCollider(new Vector3f(0, 0, 2.0f), new Orientation(), new Vector3f(0.5f, 0.5f, 0.5f));
		assertTrue(obbChecker.testMovingOBBOBB(new Vector3f(0, 0, 10.0f), a, b, normal));
		
		assertVecEquals(new Vector3f(0, 0, -1.0f), normal);
		
		b = new OBBCollider(new Vector3f(0, 0, -2.0f), new Orientation(), new Vector3f(0.5f, 0.5f, 0.5f));
		assertTrue(obbChecker.testMovingOBBOBB(new Vector3f(0, 0, -10.0f), a, b, normal));
		
		assertVecEquals(new Vector3f(0, 0, 1.0f), normal);
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