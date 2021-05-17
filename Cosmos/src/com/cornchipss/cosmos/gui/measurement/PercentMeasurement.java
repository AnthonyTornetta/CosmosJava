package com.cornchipss.cosmos.gui.measurement;

public class PercentMeasurement extends Measurement
{
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
