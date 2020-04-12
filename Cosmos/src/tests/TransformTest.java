package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.junit.jupiter.api.Test;

import com.cornchipss.physics.Transform;
import com.cornchipss.utils.Maths;

class TransformTest
{
	@Test
	void test()
	{
		Transform trans = new Transform();
		
		Transform child = new Transform();
		
		assertEquals(Maths.zero(), trans.localPosition());
		
		assertEquals(Maths.blankQuaternion(), trans.rotation());
		
		trans.rotateX(Maths.PI2);
		
		assertEquals(new Quaternionf(new AxisAngle4f(Maths.PI2, 1, 0, 0)), trans.rotation());
		
		child.parent(trans);
		
		child.rotateX(Maths.PI);
		
		assertTrue(Maths.equals(new Quaternionf(new AxisAngle4f(Maths.PI2, 1, 0, 0)), child.localRotation()));
//		assertEquals(new Quaternionf(new AxisAngle4f(Maths.PI, 1, 0, 0)), child.rotation());
		
	}
}
