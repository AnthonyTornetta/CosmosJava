package com.cornchipss.cosmos.structures;

import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.cornchipss.cosmos.netty.action.PlayerAction;
import com.cornchipss.cosmos.physx.Movement;
import com.cornchipss.cosmos.physx.Movement.MovementType;
import com.cornchipss.cosmos.physx.PhysicalObject;
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

	private Vector3f centerBlockPosition;
	private Vector3i shipCoreBlockLocation;

	public Ship(World world, int id)
	{
		super(world, MAX_DIMENSIONS, MAX_DIMENSIONS, MAX_DIMENSIONS, id);

		movement = Movement.movement(MovementType.NONE);

		centerBlockPosition = new Vector3f();
		shipCoreBlockLocation = new Vector3i(width() / 2, height() / 2,
			length() / 2);
	}

	@Override
	public boolean update(float delta)
	{
		super.update(delta);

		if (pilot == null)
		{
			// no more drifting into space once the pilot leaves
			body().velocity(body().velocity().mul(0.99f, new Vector3f()));
			movement = Movement.movement(MovementType.NONE);
		}
		else
		{
			Vector3f where = new Vector3f(width() / 2, height() / 2,
				length() / 2);
			pilot.body().velocity(Maths.zero());
			pilot.body().position(blockCoordsToWorldCoords(where, where));

			pilot.body().orientation(body().orientation());
			movement = pilot.movement();
		}

		if (body().velocity().dot(body().velocity()) < 0.1f)
			body().velocity(Maths.zero());

		return true;
	}

	public void setPilot(Player p)
	{
		if (!Utils.equals(pilot, p))
		{
			movement(Movement.movement(MovementType.NONE));

			Player temp = pilot;

			pilot = null;

			if (temp != null)
				temp.shipPiloting(null);

			pilot = p;
			if (p != null)
				p.shipPiloting(this);
		}
	}

	/**
	 * The central block of the ship's location (ship core location) relative to
	 * the world
	 * 
	 * @return
	 */
	public Vector3fc shipCoreWorldPosition()
	{
		this.blockCoordsToWorldCoords(shipCoreBlockLocation,
			centerBlockPosition);
		return centerBlockPosition;
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

	public Vector3i shipCoreBlockPosition()
	{
		return shipCoreBlockLocation;
	}

	@Override
	public RigidBody createRigidBody(Transform trans)
	{
		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass() / 1000.0f,
			new DefaultMotionState(trans), createStructureShape(trans));

		rbInfo.restitution = 0.25f;
		rbInfo.angularDamping = 0.25f;
		rbInfo.friction = 0.25f;

		return new RigidBody(rbInfo);
	}
}
