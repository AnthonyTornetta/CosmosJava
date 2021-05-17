package com.cornchipss.cosmos.gui.measurement;

public class PixelMeasurement extends Measurement
{
	public PixelMeasurement(float v)
	{
		super(v);
	}

	@Override
	public float actualValue(float dimension)
	{
		return value();
	}
}
