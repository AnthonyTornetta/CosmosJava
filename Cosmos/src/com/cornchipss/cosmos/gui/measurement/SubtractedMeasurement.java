package com.cornchipss.cosmos.gui.measurement;

public class SubtractedMeasurement implements Measurement
{
	private Measurement a, b;
	
	public SubtractedMeasurement(Measurement a, Measurement b)
	{
		this.a = a;
		this.b = b;
	}
	
	@Override
	public float actualValue(float dimension)
	{
		return a.actualValue(dimension) - b.actualValue(dimension);
	}

	public Measurement a() { return a; }
	public void a(Measurement a) { this.a = a; }

	public Measurement b() { return b; }
	public void b(Measurement b) { this.b = b; }
	
	@Override
	public String toString()
	{
		return a + " - " + b;
	}
}
