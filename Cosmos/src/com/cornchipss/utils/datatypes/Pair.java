package com.cornchipss.utils.datatypes;

/**
 * Holds two seperate datatypes
 * @param <A> The first datatype
 * @param <B> The second datatype
 */
public class Pair <A, B>
{
	private A a;
	private B b;
	
	public Pair()
	{
		this(null, null);
	}
	
	public Pair(A a, B b)
	{
		this.a = a;
		this.b = b;
	}

	public A getA() 
	{
		return a;
	}
	public void setA(A a)
	{
		this.a = a;
	}

	public B getB()
	{
		return b;
	}

	public void setB(B b)
	{
		this.b = b;
	}
}
