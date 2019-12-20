package com.cornchipss.world.planet;

import com.cornchipss.utils.Maths;
import com.cornchipss.world.biospheres.Biosphere;
import com.cornchipss.world.structures.BlockStructure;

/**
 * A body that can store blocks in a sector
 * @author Cornchip
 */
public class Planet extends BlockStructure
{
	/**
	 * The {@link Biosphere} of the planet
	 */
	private Biosphere biosphere;
	
	/**
	 * Creates a body that can store blocks in a sector
	 * @param x The x position of the planet relative to the sector's chunk's positions
	 * @param y The y position of the planet relative to the sector's chunk's positions
	 * @param z The z position of the planet relative to the sector's chunk's positions
	 * @param width The width of the planet (in blocks) - MUST BE EVEN
	 * @param height The height of the planet (in blocks) - MUST BE EVEN
	 * @param length The length of the planet (in blocks) - MUST BE EVEN
	 */
	public Planet(int width, int height, int length, Biosphere biosphere)
	{
		super(width, height, length, 0, Maths.PI / 2, 0);
		this.biosphere = biosphere;
	}
	
	public Biosphere getBiosphere() { return biosphere; }
	public void setBiosphere(Biosphere b) { this.biosphere = b; }

	@Override
	public boolean createsGravity()
	{
		return true;
	}
}
