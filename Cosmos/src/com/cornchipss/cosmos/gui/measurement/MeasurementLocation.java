package com.cornchipss.cosmos.gui.measurement;

public class MeasurementLocation 
{
	private Measurement x, y, z;
	
	public MeasurementLocation(Measurement x, Measurement y, Measurement z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void x(Measurement a)
	{
		x = a;
	}
	public void y(Measurement a)
	{
		y = a;
	}
	public void z(Measurement a)
	{
		z = a;
	}
		
	public Measurement x()
	{
		return x;
	}
	public Measurement y()
	{
		return y;
	}
	public Measurement z()
	{
		return z;
	}
}
