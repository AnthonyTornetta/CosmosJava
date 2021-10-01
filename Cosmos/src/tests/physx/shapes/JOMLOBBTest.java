package tests.physx.shapes;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.joml.Vector3f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cornchipss.cosmos.physx.Orientation;
import com.cornchipss.cosmos.physx.collision.obb.OBBCollider;
import com.cornchipss.cosmos.physx.collision.obb.OBBCollisionCheckerJOML;
import com.cornchipss.cosmos.utils.Maths;

class JOMLOBBTest
{
	private OBBCollider a, b;
	private OBBCollisionCheckerJOML obbChecker;
	
	@BeforeEach
	public void before()
	{
		a = new OBBCollider(new Vector3f(0, 0, 0), new Orientation(), new Vector3f(0.5f, 0.5f, 0.5f));
		obbChecker = new OBBCollisionCheckerJOML();
	}
	
	@Test
	void test()
	{
		b = new OBBCollider(new Vector3f(0, 0.7f, 0), new Orientation(), new Vector3f(0.5f, 0.5f, 0.5f));
		assertTrue(obbChecker.testOBBOBB(a, b, null));
		
		b = new OBBCollider(new Vector3f(-20, 0, 0), new Orientation(), new Vector3f(0.5f, 0.5f, 0.5f));
		assertFalse(obbChecker.testOBBOBB(a, b, null));
		
		b = new OBBCollider(new Vector3f(20, 0, 0), new Orientation(), new Vector3f(0.5f, 0.5f, 0.5f));
		assertFalse(obbChecker.testOBBOBB(a, b, null));
		
		b = new OBBCollider(new Vector3f(0, 20, 0), new Orientation(), new Vector3f(0.5f, 0.5f, 0.5f));
		assertFalse(obbChecker.testOBBOBB(a, b, null));
		
		b = new OBBCollider(new Vector3f(0, -20, 0), new Orientation(), new Vector3f(0.5f, 0.5f, 0.5f));
		assertFalse(obbChecker.testOBBOBB(a, b, null));
		
		b = new OBBCollider(new Vector3f(0, 0, 20), new Orientation(), new Vector3f(0.5f, 0.5f, 0.5f));
		assertFalse(obbChecker.testOBBOBB(a, b, null));
		
		b = new OBBCollider(new Vector3f(0, 0, -20), new Orientation(), new Vector3f(0.5f, 0.5f, 0.5f));
		assertFalse(obbChecker.testOBBOBB(a, b, null));
		
		b = new OBBCollider(new Vector3f(0, 0, 0), new Orientation(), new Vector3f(0.5f, 0.5f, 0.5f));
		assertTrue(obbChecker.testOBBOBB(a, b, null));
		
		b = new OBBCollider(new Vector3f(0, 1.1f, 0), new Orientation(Maths.quaternionFromRotation(Maths.PI2 / 2.f, 0, 0)), new Vector3f(0.5f, 0.5f, 0.5f));
		assertTrue(obbChecker.testOBBOBB(a, b, null));
	}
}
