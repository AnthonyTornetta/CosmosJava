package com.cornchipss.cosmos.gui.measurement;

public class PixelMeasurement extends Measurement
{
	public static final PixelMeasurement ZERO = new PixelMeasurement(0);
	
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
