package com.cornchipss.cosmos.gui.measurement;

public class PixelMeasurement implements Measurement
{
	public static final PixelMeasurement ZERO = new PixelMeasurement(0);
	public static final PixelMeasurement ONE = new PixelMeasurement(1);
	public static final PixelMeasurement TWO = new PixelMeasurement(2);
	
	private float value;
	
	public PixelMeasurement(float v)
	{
		value = v;
	}
	
	@Override
	public float actualValue(float dimension)
	{
		return value();
	}
	
	public float value() { return value; }
	public void value(float f) { value = f; }
	
	@Override
	public String toString()
	{
		return value + "";
	}
}
