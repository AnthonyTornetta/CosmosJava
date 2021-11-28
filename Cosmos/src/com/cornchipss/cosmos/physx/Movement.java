package com.cornchipss.cosmos.physx;

import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Movement
{
	public static enum MovementType
	{
		NONE(0), FORWARD(0b1), BACKWARD(0b10), LEFT(0b100), RIGHT(0b1000), UP(0b10000), DOWN(0b100000), STOP(0b1000000);

		private final int code;

		private MovementType(int n)
		{
			code = n;
		}

		public int code()
		{
			return code;
		}
	}

	private int code;
	private Vector3f deltaRot;

	public int code()
	{
		return code;
	}

	private Movement(int code)
	{
		this.code = code;
		deltaRot = new Vector3f();
	}

	public static Movement movement(MovementType... movements)
	{
		Movement mov = new Movement(0);

		for (MovementType m : movements)
			mov.add(m);

		return mov;
	}

	public void add(MovementType type)
	{
		code |= type.code();
	}

	@Override
	public boolean equals(Object o)
	{
		return ((o instanceof Movement) && ((Movement) o).code == code);
	}

	@Override
	public int hashCode()
	{
		return code;
	}
	
	public static Movement movementFromCode(int code)
	{
		return new Movement(code);
	}

	public boolean forward()
	{
		return (code & MovementType.FORWARD.code) != 0;
	}

	public boolean backward()
	{
		return (code & MovementType.BACKWARD.code) != 0;
	}

	public boolean left()
	{
		return (code & MovementType.LEFT.code) != 0;
	}

	public boolean right()
	{
		return (code & MovementType.RIGHT.code) != 0;
	}

	public boolean up()
	{
		return (code & MovementType.UP.code) != 0;
	}

	public boolean down()
	{
		return (code & MovementType.DOWN.code) != 0;
	}

	public boolean stop()
	{
		return (code & MovementType.STOP.code) != 0;
	}

	public Vector3f movementDirection(Vector3f v)
	{
		v.set(right() ? 1 : 0, up() ? 1 : 0, forward() ? 1 : 0);
		v.sub(left() ? 1 : 0, down() ? 1 : 0, backward() ? 1 : 0);
		return v;
	}

	public void addDeltaRotation(Vector3fc delta)
	{
		deltaRot.add(delta);
	}

	public Vector3fc deltaRotation()
	{
		return deltaRot;
	}
}
