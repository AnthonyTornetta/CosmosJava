package com.cornchipss.cosmos.netty.action;

public class PlayerAction
{
	private final int code;

	/**
	 * Use {@link Builder} unless read in from raw data
	 * 
	 * @param c the raw data to read action in from
	 */
	public PlayerAction(int c)
	{
		code = c;
	}

	private static final int NONE = 0;
	
	/**
	 * Stops ongoing action
	 */
	private static final int STOP = 0b01;
	
	private static final int FIRE = 0b10;

	/**
	 * Used for sending over packet
	 * 
	 * @return
	 */
	public int code()
	{
		return code;
	}
	
	public boolean isNone()
	{
		return code == 0;
	}

	public boolean isFiring()
	{
		return (code & FIRE) != 0;
	}
	
	public boolean isStopping()
	{
		return (code & STOP) != 0;
	}

	/**
	 * Creates a player action
	 */
	public static class Builder
	{
		private int code = NONE;

		public Builder setFiring(boolean f)
		{
			if (f)
				code |= FIRE;
			else
				code &= ~FIRE;

			return this;
		}
		
		public Builder setStopping(boolean s)
		{
			if (s)
				code |= STOP;
			else
				code &= ~STOP;
			
			return this;
		}

		public PlayerAction create()
		{
			return new PlayerAction(code);
		}
	}
}
