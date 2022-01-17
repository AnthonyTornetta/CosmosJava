package com.cornchipss.cosmos.world.entities.player;

import java.util.ConcurrentModificationException;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.cosmos.blocks.Blocks;
import com.cornchipss.cosmos.cameras.Camera;
import com.cornchipss.cosmos.inventory.Inventory;
import com.cornchipss.cosmos.physx.Movement;
import com.cornchipss.cosmos.physx.Movement.MovementType;
import com.cornchipss.cosmos.physx.PhysicalObject;
import com.cornchipss.cosmos.physx.RigidBody;
import com.cornchipss.cosmos.physx.Transform;
import com.cornchipss.cosmos.physx.collision.obb.OBBCollider;
import com.cornchipss.cosmos.physx.collision.obb.OBBCollisionCheckerJOML;
import com.cornchipss.cosmos.structures.Ship;
import com.cornchipss.cosmos.structures.Structure;
import com.cornchipss.cosmos.utils.Utils;
import com.cornchipss.cosmos.world.World;

public abstract class Player extends PhysicalObject
{
	public static final float WIDTH = 0.8f, LENGTH = 0.6f, HEIGHT = 1.8f;
	private static final Vector3fc HALF_WIDTHS = new Vector3f(WIDTH / 2,
		HEIGHT / 2, LENGTH / 2);

	private Ship pilotingShip;

	private Inventory inventory;
	private int selectedInventoryCol;

	private String name;

	public String name()
	{
		return name;
	}

	private Movement movement;

	private OBBCollisionCheckerJOML jomlChecker;

	public Player(World world, String name)
	{
		super(world);

		this.name = name;

		inventory = new Inventory(4, 10);

		inventory.block(0, 0, Blocks.GRASS);
		inventory.block(0, 1, Blocks.DIRT);
		inventory.block(0, 2, Blocks.STONE);
		inventory.block(0, 3, Blocks.LIGHT);
		inventory.block(0, 4, Blocks.SHIP_HULL);
		inventory.block(0, 5, Blocks.THRUSTER);
		inventory.block(0, 6, Blocks.REACTOR);
		inventory.block(0, 7, Blocks.ENERGY_STORAGE);
		inventory.block(0, 8, Blocks.LASER_CANNON);
		inventory.block(0, 9, Blocks.LEAF);

		movement = Movement.movement(MovementType.NONE);

		jomlChecker = new OBBCollisionCheckerJOML();
	}

	public abstract void update(float delta);

	@Override
	public void addToWorld(Transform transform)
	{
		body(new RigidBody(transform));
		world().addObject(this);
	}

	@Override
	public OBBCollider OBB()
	{
		return new OBBCollider(this.position(),
			this.body().transform().orientation(), HALF_WIDTHS);
	}

	public Structure.RayRes calculateLookingAt()
	{
		Structure.RayRes closestHit = null;
		float closestDist = -1;

		try
		{
			for (Structure s : world()
				.structuresNear(body().transform().position()))
			{
				Structure.RayRes hit = s.raycast(camera().position(),
					camera().forward().mul(50.0f, new Vector3f()), jomlChecker);
				if (hit != null)
				{
					float distSqrd = hit.distance();

					if (closestHit == null)
					{
						closestHit = hit;
						closestDist = distSqrd;
					}
					else if (closestDist > distSqrd)
					{
						closestHit = hit;
						closestDist = distSqrd;
					}
				}
			}
		}
		catch (ConcurrentModificationException ex)
		{
			return null;
		}

//		Utils.println(closestHit);

		return closestHit;
	}

	public boolean isPilotingShip()
	{
		return pilotingShip != null;
	}

	/**
	 * Use {@link Ship#setPilot(Player)}
	 * 
	 * @param s
	 */
	public @Deprecated void shipPiloting(Ship s)
	{
		if (Utils.equals(pilotingShip, s))
			return;

		if (pilotingShip != null)
			pilotingShip.setPilot(null);
		pilotingShip = null;

		this.body().transform().position(
			this.position().add(new Vector3f(0, -5, 0), new Vector3f()));

		if (s != null)
		{
			pilotingShip = s;
			pilotingShip.setPilot(this);
		}
	}

	public Ship shipPiloting()
	{
		return pilotingShip;
	}

	public abstract Camera camera();

	public void selectedInventoryColumn(int c)
	{
		selectedInventoryCol = c;
	}

	public int selectedInventoryColumn()
	{
		return selectedInventoryCol;
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof Player)
		{
			final Player o = (Player) other;
			return name().equals(o.name());
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return name.hashCode();
	}

	public Inventory inventory()
	{
		return inventory;
	}

	public void inventory(Inventory i)
	{
		inventory = i;
	}

	public void movement(Movement movement)
	{
		this.movement = movement;
	}

	public Movement movement()
	{
		return movement;
	}

	@Override
	public boolean shouldCollideWith(PhysicalObject o)
	{
		return !isPilotingShip();
	}
}
