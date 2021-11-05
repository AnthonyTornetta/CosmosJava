package com.cornchipss.cosmos.structures.types;

public interface IEnergyHolder
{
	public float energy();

	public float maxEnergy();

	public boolean hasEnoughEnergyToUse(float amount);

	public boolean useEnergy(float amount);

	public void addEnergy(float amount);
}
