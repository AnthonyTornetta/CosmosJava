package test.utils;

import javax.annotation.Nonnull;

public class Logger
{
	public static final Logger LOGGER = new Logger(LogLevel.INFO);
	
	public static enum LogLevel
	{
		DEBUG(0),
		INFO(1),
		WARNING(2),
		ERROR(3),
		NOTHING(4);
		
		int val;
		
		LogLevel(int v)
		{
			val = v;
		}
	}
	
	private LogLevel level;
	
	public Logger(@Nonnull LogLevel level)
	{
		this.level = level;
	}
	
	public void setLevel(@Nonnull LogLevel lvl)
	{
		this.level = lvl;
	}
	
	private String raw(String msg, String level)
	{
		return "[" + level + "] [" + traceInfo() + "] " + msg;
	}
	
	private String traceInfo()
	{
		StackTraceElement trace = Thread.currentThread().getStackTrace()[4];
		
		String clazz = trace.getClassName();
		
		return clazz.substring(clazz.lastIndexOf(".") + 1) + ":" + trace.getLineNumber() + "";
	}
	
	public void debug(String msg)
	{
		if(level.val <= LogLevel.DEBUG.val)
			System.out.println(raw(msg, "Debug"));
	}
	
	public void info(String msg)
	{
		if(level.val <= LogLevel.INFO.val)
			System.out.println(raw(msg, "Info"));
	}
	
	public void warning(String msg)
	{
		if(level.val <= LogLevel.WARNING.val)
			System.out.println(raw(msg, "Warning"));
	}
	
	public void error(String msg)
	{
		if(level.val <= LogLevel.ERROR.val)
			System.out.println(raw(msg, "Error"));
	}
}
