package com.cornchipss.cosmos.server.kyros.types;

public class Login
{
	private String name;

	public Login() {}
	
	public Login(String name)
	{
		this.name = name;
	}
	
	public String name()
	{
		return name;
	}
}
