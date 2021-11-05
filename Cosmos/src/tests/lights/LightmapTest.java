package tests.lights;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.joml.Vector3f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cornchipss.cosmos.lights.LightMap;
import com.cornchipss.cosmos.lights.LightSource;

class LightmapTest
{
	private LightMap map;
	private LightSource src;

	private static final int W = 11, H = 11;

	@BeforeEach
	void before()
	{
		map = new LightMap(W, H, 1);
		src = new LightSource(5, 1, 0, 0);
	}

	@Test
	void test()
	{
		assertEquals(new Vector3f(0, 0, 0), map.lightAt(0, 0, 0));

		map.addLight(src, 0, 0, 0);
		map.updateMap();

		float[][] actualMap = new float[][] { { 1.0f, 0.8f, 0.6f, 0.4f, 0.2f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, },
			{ 0.8f, 0.6f, 0.4f, 0.2f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, },
			{ 0.6f, 0.4f, 0.2f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, },
			{ 0.4f, 0.2f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, },
			{ 0.2f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, },
			{ 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, },
			{ 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, },
			{ 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, },
			{ 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, },
			{ 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, },
			{ 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, } };

		float[][] generated = new float[H][W];

		for (int y = 0; y < H; y++)
		{
			for (int x = 0; x < W; x++)
			{
				generated[y][x] = map.lightAt(x, y, 0).x();
			}
		}

		for (int y = 0; y < H; y++)
			assertArrayEquals(actualMap[y], generated[y]);

		map.removeLight(0, 0, 0);
		map.updateMap();
		for (int z = 0; z < 1; z++)
			for (int y = 0; y < H; y++)
				for (int x = 0; x < W; x++)
					assertEquals(new Vector3f(0, 0, 0), map.lightAt(x, y, z));
	}

	@Test
	public void test2()
	{
		map.addLight(src, W / 2, H / 2, 0);
		map.updateMap();

		map.addLight(src, 0, H / 2, 0);
		map.updateMap();

//		map.printMap();

		map.removeLight(W / 2, H / 2, 0);
		map.updateMap();
//		map.printMap();

		map.setBlocking(1, H / 2, 0);
		map.updateMap();
//		map.printMap();

		map.removeBlocking(1, H / 2, 0);
		map.updateMap();
//		map.printMap();
	}

	@Test
	public void test3()
	{
		map.addLight(src, W / 2, H / 2, 0);
		map.updateMap();
//		map.printMap();

		map.addLight(src, 0, H / 2, 0);
		map.updateMap();
//		map.printMap();
	}

	@Test
	public void test4()
	{
		map.addLight(src, W / 2, H / 2, 0);
		map.updateMap();
		map.printMap();

		map.setBlocking(W / 2 - 1, H / 2, 0);
		map.setBlocking(W / 2 + 1, H / 2, 0);
		map.updateMap();

		map.printDBGCombo();
	}
}
