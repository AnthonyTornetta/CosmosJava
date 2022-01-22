package com.cornchipss.cosmos.blocks.data;

/**
 * <p>
 * Data for a block in the world
 * </p>
 * <p>
 * Unique to each block
 * </p>
 */
public class BlockData
{
	private float damage;
	
	public void takeDamage(float dmg)
	{
		damage += dmg;
	}
	
	public float damage()
	{
		return damage;
	}
	
	public void damage(int damage)
	{
		this.damage = damage;
	}
	
	public boolean needsToBeSaved()
	{
		return damage != 0;
	}
}
