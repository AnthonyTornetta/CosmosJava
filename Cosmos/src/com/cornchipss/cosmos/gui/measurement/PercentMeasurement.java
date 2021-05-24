package com.cornchipss.cosmos.gui.measurement;

public class PercentMeasurement implements Measurement
{
	public static final PercentMeasurement ONE = new PercentMeasurement(1);
	public static final PercentMeasurement HALF = new PercentMeasurement(0.5f);
	
	private float value;
	
	public PercentMeasurement(float v)
	{
		value = v;
	}

	@Override
	public float actualValue(float dimension)
	{
		return value() * dimension;
	}
	
	public float value() { return value; }
	public void value(float f) { value = f; }
	
	@Override
	public String toString()
	{
		return value * 100 + "%";
	}
}
