package com.cornchipss.cosmos.structures;

import org.joml.Vector3f;

import com.cornchipss.cosmos.blocks.Block;
import com.cornchipss.cosmos.netty.action.PlayerAction;
import com.cornchipss.cosmos.physx.Movement;
import com.cornchipss.cosmos.physx.PhysicalObject;
import com.cornchipss.cosmos.physx.Movement.MovementType;
import com.cornchipss.cosmos.utils.Maths;
import com.cornchipss.cosmos.utils.Utils;
import com.cornchipss.cosmos.world.World;
import com.cornchipss.cosmos.world.entities.player.Player;

/**
 * A structure representing a ship
 */
public class Ship extends Structure
{
	private final static int MAX_DIMENSIONS = 16 * 10;

	private Player pilot;

	private Movement movement;

	public Ship(World world, int id)
	{
		super(world, MAX_DIMENSIONS, MAX_DIMENSIONS, MAX_DIMENSIONS, id);

		movement = Movement.movement(MovementType.NONE);
	}

	@Override
	public void block(int x, int y, int z, Block b)
	{
		super.block(x, y, z, b);
	}

	@Override
	public void update(float delta)
	{
		super.update(delta);

		if (pilot == null)
		{
			body().velocity(body().velocity().mul(0.99f, new Vector3f())); // no more drifting into space once the pilot
																			// leaves
			movement = Movement.movement(MovementType.NONE);
		}
		else
		{
			Vector3f where = new Vector3f(width() / 2, height() / 2, length() / 2);
			pilot.body().velocity(Maths.zero());
			pilot.body().transform().position(blockCoordsToWorldCoords(where, where));

			pilot.body().transform().orientation(body().transform().orientation());
			movement = pilot.movement();
		}
	}

	@SuppressWarnings("deprecation")
	public void setPilot(Player p)
	{
		if (!Utils.equals(pilot, p))
		{
			movement(Movement.movement(MovementType.NONE));

			if (pilot != null)
				pilot.shipPiloting(null);

			pilot = p;
			if (p != null)
				p.shipPiloting(this);
		}
	}

	public Player pilot()
	{
		return pilot;
	}

	public void movement(Movement movement)
	{
		this.movement = movement;
	}

	public Movement movement()
	{
		return movement;
	}

	public void sendAction(PlayerAction action)
	{
		blockSystemManager().sendAction(action);
	}
	
	@Override
	public boolean shouldCollideWith(PhysicalObject other)
	{
		return !other.equals(pilot());
	}
}
