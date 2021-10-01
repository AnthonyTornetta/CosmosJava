package tests.physx.shapes;

import static org.junit.jupiter.api.Assertions.fail;

import org.joml.Vector3f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cornchipss.cosmos.physx.Orientation;
import com.cornchipss.cosmos.physx.shapes.OBB;
import com.cornchipss.cosmos.utils.Maths;
import com.cornchipss.cosmos.utils.Utils;

class OBBTest
{
	private OBB a, b;
	
	@BeforeEach
	public void before()
	{
		a = new OBB(new Vector3f(0, 0, 0), new Orientation(), new Vector3f(0.5f, 0.5f, 0.5f));
	}
	
	@Test
	void test()
	{
//		b = new OBB(new Vector3f(0, 0.7f, 0), new Orientation(), new Vector3f(0.5f, 0.5f, 0.5f));
//		Utils.println(OBB.testOBBOBB(a, b));
		
		b = new OBB(new Vector3f(-20, 0, 0), new Orientation(), new Vector3f(0.5f, 0.5f, 0.5f));
		Utils.println(OBB.testOBBOBB(a, b, null));
		
		b = new OBB(new Vector3f(20, 0, 0), new Orientation(), new Vector3f(0.5f, 0.5f, 0.5f));
		Utils.println(OBB.testOBBOBB(a, b, null));
		
		b = new OBB(new Vector3f(0, 20, 0), new Orientation(), new Vector3f(0.5f, 0.5f, 0.5f));
		Utils.println(OBB.testOBBOBB(a, b, null));
		
		b = new OBB(new Vector3f(0, -20, 0), new Orientation(), new Vector3f(0.5f, 0.5f, 0.5f));
		Utils.println(OBB.testOBBOBB(a, b, null));
		
		b = new OBB(new Vector3f(0, 0, 20), new Orientation(), new Vector3f(0.5f, 0.5f, 0.5f));
		Utils.println(OBB.testOBBOBB(a, b, null));
		
		b = new OBB(new Vector3f(0, 0, -20), new Orientation(), new Vector3f(0.5f, 0.5f, 0.5f));
		Utils.println(OBB.testOBBOBB(a, b, null));
		
		b = new OBB(new Vector3f(0, 0, 0), new Orientation(), new Vector3f(0.5f, 0.5f, 0.5f));
		Utils.println(OBB.testOBBOBB(a, b, null));
		
		b = new OBB(new Vector3f(0, 1.1f, 0), new Orientation(Maths.quaternionFromRotation(Maths.PI2 / 2.f, 0, 0)), new Vector3f(0.5f, 0.5f, 0.5f));
		Utils.println(OBB.testOBBOBB(a, b, null));
	}
}
