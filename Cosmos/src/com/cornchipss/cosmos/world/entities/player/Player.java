package com.cornchipss.cosmos.world.entities.player;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.cosmos.blocks.Blocks;
import com.cornchipss.cosmos.cameras.Camera;
import com.cornchipss.cosmos.inventory.Inventory;
import com.cornchipss.cosmos.physx.Movement;
import com.cornchipss.cosmos.physx.PhysicalObject;
import com.cornchipss.cosmos.physx.RayResult;
import com.cornchipss.cosmos.physx.RigidBody;
import com.cornchipss.cosmos.physx.Transform;
import com.cornchipss.cosmos.physx.Movement.MovementType;
import com.cornchipss.cosmos.structures.Ship;
import com.cornchipss.cosmos.structures.Structure;
import com.cornchipss.cosmos.utils.Maths;
import com.cornchipss.cosmos.utils.Utils;
import com.cornchipss.cosmos.world.World;

public abstract class Player extends PhysicalObject
{
	private Ship pilotingShip;
	
	private Inventory inventory;
	private int selectedInventoryCol;
	
	private String name;
	public String name() { return name; }
	
	private Movement movement;
	
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
		inventory.block(0, 8, Blocks.LOG);
		inventory.block(0, 9, Blocks.LEAF);
		
		movement = Movement.movement(MovementType.NONE);
	}
	
	public abstract void update(float delta);

	@Override
	public void addToWorld(Transform transform)
	{
		body(new RigidBody(transform));
		world().addRigidBody(body());
	}
	
	public Structure calculateLookingAt()
	{
		Vector3fc from = camera().position();
		Vector3f dLook = Maths.mul(camera().forward(), 50.0f);
		Vector3f to = Maths.add(from, dLook);
		
		Structure closestHit = null;
		float closestDistSqrd = -1;
		
		for(Structure s : world().structuresNear(body().transform().position()))
		{
			RayResult hits = s.shape().raycast(from, to);
			if(hits.closestHit() != null)
			{
				float distSqrd = Maths.distSqrd(from, hits.closestHitWorldCoords());
				
				if(closestHit == null)
				{
					closestHit = s;
					closestDistSqrd = distSqrd;
				}
				else if(closestDistSqrd > distSqrd)
				{
					closestHit = s;
					closestDistSqrd = distSqrd;
				}
			}
		}
		
		return closestHit;
	}

	public boolean isPilotingShip()
	{
		return pilotingShip != null;
	}
	
	public void shipPiloting(Ship s)
	{
		if(Utils.equals(pilotingShip, s))
			return;
		
		Ship temp = pilotingShip;
		pilotingShip = null;
		
		if(temp != null)
			temp.setPilot(null);
		
		if(s != null)
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
		if(other instanceof Player)
		{
			Player o = (Player)other;
			return name().equals(o.name());
		}
		
		return false;
	}
	
	@Override
	public int hashCode()
	{
		return name.hashCode();
	}
	
	public Inventory inventory() { return inventory; }
	public void inventory(Inventory i) { inventory = i; }

	public void movement(Movement movement)
	{
		this.movement = movement;
	}
	
	public Movement movement()
	{
		return movement;
	}
}
