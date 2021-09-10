package tests.physx.shapes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.joml.Vector3f;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cornchipss.cosmos.blocks.Blocks;
import com.cornchipss.cosmos.physx.Orientation;
import com.cornchipss.cosmos.physx.Transform;
import com.cornchipss.cosmos.physx.shapes.StructureShape;
import com.cornchipss.cosmos.structures.Planet;
import com.cornchipss.cosmos.structures.Structure;
import com.cornchipss.cosmos.utils.Maths;
import com.cornchipss.cosmos.world.World;

class StructureShapeTest
{
	private StructureShape rs;
	private Structure s;
	
	@BeforeEach
	public void setup()
	{
		World w = new World();
		
		s = new Planet(w, 16, 16, 16, 0);
		s.init();

		for(int z = 0; z < s.length(); z++)
		{
			for(int y = 0; y < s.height(); y++)
			{
				for(int x = 0; x < s.width(); x++)
				{
					s.block(x, y, z, Blocks.STONE);
				}
			}
		}
		s.addToWorld(new Transform());
		
		rs = new StructureShape(s);
	}
	
	@AfterEach
	public void tearDown()
	{
		rs = null;
		s = null;
	}
	
	@Test
	void testPointAtZero()
	{
		assertTrue(rs.pointIntersects(new Vector3f(), new Vector3f(), new Orientation()));
	}
	
	@Test
	void testSmallLineAtHalf()
	{
		assertTrue(rs.lineIntersects(new Vector3f(0.5f, 0.5f, 0.5f), new Vector3f(0.6f, 0.6f, 0.6f), new Vector3f(), new Orientation(), new Vector3f()));
	}
	
	@Test
	public void testPointAtOnEdge()
	{
		assertFalse(rs.pointIntersects(new Vector3f(0, 8, 0), new Vector3f(), new Orientation()));
	}
	
	@Test
	public void testPointInEdge()
	{
		assertTrue(rs.pointIntersects(new Vector3f(0, 7.9f, 0), new Vector3f(), new Orientation()));
	}
	
	@Test
	public void testPointRotated()
	{
		Orientation o = new Orientation(Maths.quaternionFromRotation(0, Maths.PI / 4f, 0));
		assertFalse(rs.pointIntersects(new Vector3f(0, 8, 0), new Vector3f(), o));
	}
	
	@Test
	public void testPointRotated2()
	{
		Orientation o = new Orientation(Maths.quaternionFromRotation(0, Maths.PI / 4f, 0));
		assertTrue(rs.pointIntersects(new Vector3f(0, 7.9f, 0), new Vector3f(), o));
	}
	
	@Test
	public void testLineInside()
	{
		assertTrue(rs.lineIntersects(new Vector3f(0, 0, 0), new Vector3f(1.0f, 0, 0), new Vector3f(), new Orientation(), new Vector3f()));
	}
	
	@Test
	public void testLineOutsideToEdge()
	{
		Vector3f out = new Vector3f();
		assertTrue(rs.lineIntersects(new Vector3f(-9, 0, 0), new Vector3f(-8f, 0, 0), new Vector3f(), new Orientation(), out));
		assertEquals(new Vector3f(-8, 0, 0), out);
	}
	
	@Test
	public void testLineInsideEdgeToOut()
	{
		Vector3f out = new Vector3f();
		assertTrue(rs.lineIntersects(new Vector3f(-8, 0, 0), new Vector3f(-9f, 0, 0), new Vector3f(), new Orientation(), out));
		assertEquals(new Vector3f(-8, 0, 0), out);
	}
	
	@Test
	public void testRotated()
	{
		Orientation o = new Orientation(Maths.quaternionFromRotation(0, Maths.PI / 4f, 0));
		assertTrue(rs.pointIntersects(new Vector3f(0, 7.9f, 0), new Vector3f(), o));
		
		s.body().transform().position(new Vector3f(0, 20, 0));
		s.body().transform().orientation(o);
		
		assertTrue(rs.pointIntersects(new Vector3f(0, 27.9f, 0), s.position(), s.body().transform().orientation()));
		
		Vector3f out = new Vector3f();
		
		assertTrue(rs.lineIntersects(new Vector3f(-20, 17, 0), new Vector3f(20, 23, 0), s.position(), s.body().transform().orientation(), out));
	}
}
