package tests.physx.shapes;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.joml.Vector3f;
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
	@Test
	void test()
	{
		// pointIntersects
		
		World w = new World();
		
		Structure s = new Planet(w, 16, 16, 16, 0);
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
		
		StructureShape rs = new StructureShape(s);
		
		assertTrue(rs.pointIntersects(new Vector3f(), new Vector3f(), new Orientation()));
		
		assertFalse(rs.pointIntersects(new Vector3f(0, 8, 0), new Vector3f(), new Orientation()));
		assertTrue(rs.pointIntersects(new Vector3f(0, 7.9f, 0), new Vector3f(), new Orientation()));
		
		Orientation o = new Orientation(Maths.quaternionFromRotation(0, Maths.PI / 4f, 0));
		assertFalse(rs.pointIntersects(new Vector3f(0, 8, 0), new Vector3f(), o));
		assertTrue(rs.pointIntersects(new Vector3f(0, 7.9f, 0), new Vector3f(), o));
		
	}
}
