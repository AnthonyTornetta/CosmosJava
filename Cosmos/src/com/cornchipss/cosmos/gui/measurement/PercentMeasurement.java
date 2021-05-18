package com.cornchipss.cosmos.gui.measurement;

public class PercentMeasurement extends Measurement
{
	public static final PercentMeasurement ONE = new PercentMeasurement(1);
	public static final PercentMeasurement HALF = new PercentMeasurement(0.5f);
	
	public PercentMeasurement(float v)
	{
		super(v);
	}

	@Override
	public float actualValue(float dimension)
	{
		return value() * dimension;
	}
}
