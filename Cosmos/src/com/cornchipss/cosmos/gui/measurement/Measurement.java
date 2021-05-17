package com.cornchipss.cosmos.gui.measurement;

public abstract class Measurement
{
	private float value;
	
	public Measurement(float v)
	{
		this.value = v;
	}
	
	public abstract float actualValue(float dimension);
	
	public float value()
	{
		return value;
	}
	
	public void value(float f)
	{
		value = f;
	}
}
