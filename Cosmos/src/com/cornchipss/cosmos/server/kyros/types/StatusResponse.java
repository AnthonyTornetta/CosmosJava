package com.cornchipss.cosmos.server.kyros.types;

public class StatusResponse
{
	private int code;
	private String msg;
	
	public StatusResponse(int code)
	{
		this(code, null);
	}
	
	public StatusResponse(int code, String msg)
	{
		this.code = code;
		this.msg = msg;
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
