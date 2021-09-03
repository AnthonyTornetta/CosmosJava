package tests.physx.shapes;

import static org.junit.jupiter.api.Assertions.*;

import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

import com.cornchipss.cosmos.physx.Orientation;
import com.cornchipss.cosmos.physx.shapes.RectangleShape;
import com.cornchipss.cosmos.utils.Maths;

class RectangleShapeTest
{
	@Test
	void test()
	{
		// pointIntersects
		
		RectangleShape rs = new RectangleShape(0.5f, 1.0f, 0.5f);
		
		assertTrue(rs.pointIntersects(new Vector3f(), new Vector3f(), new Orientation()));
		
		assertFalse(rs.pointIntersects(new Vector3f(0, 0.50001f, 0), new Vector3f(), new Orientation()));
		
		assertTrue(rs.pointIntersects(new Vector3f(0, 0.5f, 0), new Vector3f(), new Orientation()));
		
		Orientation halfpi = new Orientation(Maths.quaternionFromRotation(0, 0, Maths.PI2));
		
		assertTrue(rs.pointIntersects(new Vector3f(0.5f, 0.25f, 0.25f), new Vector3f(), halfpi));
		
		assertTrue(rs.pointIntersects(new Vector3f(1.5f, 0.25f, 1.25f), new Vector3f(1, 0, 1), halfpi));
		
		assertFalse(rs.pointIntersects(new Vector3f(1.50001f, 0.25f, 1.25f), new Vector3f(1, 0, 1), halfpi));
		
		
		// lineIntersects
		// There should be more tests
		Vector3f res = new Vector3f();
		
		assertTrue(rs.lineIntersects(new Vector3f(-10, 0, 0), new Vector3f(10, 0, 0), new Vector3f(), new Orientation(), res));
		assertEquals(res, new Vector3f(-0.25f, 0, 0));
		
		assertFalse(rs.lineIntersects(new Vector3f(-10, 10, 0), new Vector3f(10, 0, 0), new Vector3f(), new Orientation(), res));
		assertEquals(res, new Vector3f(-0.25f, 0, 0));
		
		assertTrue(rs.lineIntersects(new Vector3f(0, -10, 0), new Vector3f(0, 10, 0), new Vector3f(), new Orientation(), res));
		assertEquals(res, new Vector3f(0, -0.5f, 0));
	}
}
