package com.cornchipss.cosmos.gui.measurement;

public class MeasurementPair 
{
	private Measurement x, y;
	
	public MeasurementPair(Measurement x, Measurement y)
	{
		this.x = x;
		this.y = y;
	}
	
	public void x(Measurement m)
	{
		x = m;
	}
	public void y(Measurement m)
	{
		y = m;
	}
		
	public Measurement x()
	{
		return x;
	}
	public Measurement y()
	{
		return y;
	}
}
