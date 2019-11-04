package com.cornchipss.testing;

import static org.junit.jupiter.api.Assertions.*;

import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

import com.cornchipss.utils.Utils;
import com.cornchipss.utils.datatypes.Vector3fList;

class TestingMain
{
	@Test
	void test()
	{
		Utils.println("-- Datatypes --");
		
		Utils.println("Vector3fList");
		Vector3fList vecList = new Vector3fList(300);
		vecList.addVector(new Vector3f(0, 0, 0));
		vecList.addVector(new Vector3f(1, 1, 1));
		vecList.addVector(new Vector3f(2, 2, 2));
		
		assertArrayEquals(vecList.asFloats(), new float[] { 0, 0, 0, 1, 1, 1, 2, 2, 2 });
		
		vecList.removeVector(new Vector3f(1, 1, 1));
		
		assertArrayEquals(vecList.asFloats(), new float[] { 0, 0, 0, 2, 2, 2 });
		
		vecList.removeVector(new Vector3f(2, 2, 2));
		
		assertArrayEquals(vecList.asFloats(), new float[] { 0, 0, 0 });
		
		vecList.addVector(new Vector3f(1, 1, 1));
		vecList.addVector(new Vector3f(2, 2, 2));
		vecList.addVector(new Vector3f(3, 3, 3));
		
		assertArrayEquals(vecList.asFloats(), new float[] { 0, 0, 0, 1, 1, 1, 2, 2, 2, 3, 3, 3 });
		assertTrue(vecList.containsVector(new Vector3f(3, 3, 3)));
		assertFalse(vecList.containsVector(new Vector3f(4, 4, 4)));
		
		vecList.removeVector(new Vector3f(3, 3, 3));
		vecList.addVector(new Vector3f(3, 3, 3));
		vecList.removeVector(new Vector3f(3, 3, 3));
		vecList.addVector(new Vector3f(3, 3, 3));
		vecList.removeVector(new Vector3f(3, 3, 3));
		vecList.addVector(new Vector3f(3, 3, 3));
		vecList.removeVector(new Vector3f(3, 3, 3));
		vecList.addVector(new Vector3f(3, 3, 3));
		
		assertArrayEquals(vecList.asFloats(), new float[] { 0, 0, 0, 1, 1, 1, 2, 2, 2, 3, 3, 3 });
		
		vecList.removeVector(new Vector3f(2, 2, 2));
		vecList.addVector(new Vector3f(2, 2, 2));
		
		assertArrayEquals(vecList.asFloats(), new float[] { 0, 0, 0, 1, 1, 1, 3, 3, 3, 2, 2, 2 });
		
		assertEquals(vecList.size(), 12);
		
		Utils.println("All test cases passed!");
	}
}
