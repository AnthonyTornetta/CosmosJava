package com.cornchipss.cosmos.server.kyros.types;

public class StatusResponse
{
	private int code;
	private String msg;
	
	public StatusResponse () {}
	
	public StatusResponse(int code)
	{
		this(code, null);
	}
	
	public StatusResponse(int code, String msg)
	{
		this.code = code;
		this.msg = msg;
	}
	
	@Override
	public String toString()
	{
		return "Status: " + code + " - " + (msg != null ? msg : "");
	}
	
	public int code()
	{
		return code;
	}
	
	public String message()
	{
		return msg;
	}
}
