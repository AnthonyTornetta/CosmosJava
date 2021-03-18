package com.cornchipss.cosmos.utils;

public class GameLoop implements Runnable
{
	private IUpdatable logic;
	private final int MILLIS_WAIT;
	
	public GameLoop(IUpdatable logic, int millisWait)
	{
		this.logic = logic;
		
		MILLIS_WAIT = millisWait;
	}
	
	@Override
	public void run()
	{
		boolean running = true;
		
		DebugMonitor.set("ups", 0);
		DebugMonitor.set("ups-variance", 0.0f);
		
		int ups = 0;
		float variance = 0;
		
		long t = System.currentTimeMillis();
		
		long lastSecond = t;

		while(running)
		{
			float delta = System.currentTimeMillis() - t; 
			
			if(delta < MILLIS_WAIT)
			{
				try
				{
					Thread.sleep(MILLIS_WAIT - (int)delta);
					
					delta = (System.currentTimeMillis() - t);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			
			delta /= 1000.0f;
			
			if(delta > variance)
				variance = delta;
			
			t = System.currentTimeMillis();
			
			if(lastSecond / 1000 != t / 1000)
			{
				DebugMonitor.set("ups", ups);
				DebugMonitor.set("ups-variance", variance);
				
				lastSecond = t;
				ups = 0;
				variance = 0;
			}
			ups++;
			
			running = logic.update(delta);
		}
	}
}
