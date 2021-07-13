package com.cornchipss.cosmos.structures;

import java.util.LinkedList;
import java.util.List;

import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;
import org.lwjgl.glfw.GLFW;

import com.cornchipss.cosmos.blocks.Block;
import com.cornchipss.cosmos.blocks.modifiers.IThrustProducer;
import com.cornchipss.cosmos.structures.types.IEnergyHolder;
import com.cornchipss.cosmos.utils.Maths;
import com.cornchipss.cosmos.utils.Utils;
import com.cornchipss.cosmos.utils.io.Input;
import com.cornchipss.cosmos.world.World;
import com.cornchipss.cosmos.world.entities.player.Player;

/**
 * A structure representing a ship
 */
public class Ship extends Structure implements IEnergyHolder
{
	private final static int MAX_DIMENSIONS = 16 * 10;
	
	private Player pilot;
	
	private Vector3f corePos = new Vector3f();
	
	private float totalMass = 0;
	private float thrustForce = 0;
	
	private float energy = 0;
	
	private List<Vector3i> thrusterPositions;
	
	public Ship(World world, int id)
	{
		super(world, MAX_DIMENSIONS, MAX_DIMENSIONS, MAX_DIMENSIONS, id);
		
		thrusterPositions = new LinkedList<>();
	}
	
	public Vector3fc corePosition()
	{
		corePos.set(MAX_DIMENSIONS / 2.f, MAX_DIMENSIONS / 2.f, MAX_DIMENSIONS / 2.f);
		return corePos;
	}
	
	@Override
	public void block(int x, int y, int z, Block b)
	{
		Block previous = block(x, y, z);
		
		if(previous != null)
		{
			totalMass -= previous.mass();
		}
		
		if(b != null)
		{
			totalMass += b.mass();
		}
		
		if(!Utils.equals(previous, b))
		{
			if(previous instanceof IThrustProducer)
			{
				thrustForce -= ((IThrustProducer)previous).thrustGeneratedPerSecond();
				energy -= ((IThrustProducer)previous).powerUsedPerSecond();
			}
			
			if(b instanceof IThrustProducer)
			{
				thrustForce += ((IThrustProducer)b).thrustGeneratedPerSecond();
				energy += ((IThrustProducer)b).powerUsedPerSecond();
			}
			
			if(previous instanceof IThrustProducer
					&& !(b instanceof IThrustProducer))
			{				
				thrusterPositions.remove(new Vector3i(x, y, z));
			}
			else if(b instanceof IThrustProducer)
			{
				thrusterPositions.add(new Vector3i(x, y, z));
			}
		}
		
		super.block(x, y, z, b);
	}
	
	@Override
	public void update(float delta)
	{
		if(pilot != null)
		{			
			pilot.body().velocity(Maths.zero());
			pilot.body().transform().position(localCoordsToWorldCoords(width()/2, height()/2, length()/2));
			
			Vector3f dVel = new Vector3f();
		    
			if(Input.isKeyDown(GLFW.GLFW_KEY_W))
				dVel.add(body().transform().forward());
			if(Input.isKeyDown(GLFW.GLFW_KEY_S))
				dVel.sub(body().transform().forward());
			if(Input.isKeyDown(GLFW.GLFW_KEY_D))
				dVel.add(body().transform().right());
			if(Input.isKeyDown(GLFW.GLFW_KEY_A))
				dVel.sub(body().transform().right());
			if(Input.isKeyDown(GLFW.GLFW_KEY_E))
				dVel.add(body().transform().up());
			if(Input.isKeyDown(GLFW.GLFW_KEY_Q))
				dVel.sub(body().transform().up());
			
			float accel = thrustForce / mass();
			
			dVel.x = (dVel.x() * (delta * accel));
			dVel.z = (dVel.z() * (delta * accel));
			dVel.y = (dVel.y() * (delta * accel));
			
			Vector3f dRot = new Vector3f();
			
			Vector3f vel = body().velocity();
			
			if(Input.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT))
				vel.mul(0.75f);
	
			vel.add(dVel);
	
			vel = Maths.safeNormalize(vel, 100.0f);
			
			body().velocity(vel);
			
			if(Input.isKeyDown(GLFW.GLFW_KEY_C))
				dRot.z += 2;
			if(Input.isKeyDown(GLFW.GLFW_KEY_Z))
				dRot.z -= 2;
			
			dRot.y = Input.getMouseDeltaX() * 0.1f;
			
			dRot.x = Input.getMouseDeltaY() * 0.1f;
			
			dRot.mul(0.01f);
			
			body().transform().rotateRelative(dRot);
			
			pilot.body().transform().orientation(body().transform().orientation());
		}
		else
			body().velocity(body().velocity().mul(0.99f)); // no more drifting into space once the pilot leaves
	}

	public void setPilot(Player p)
	{
		if(!Utils.equals(pilot, p))
		{
			if(pilot != null)
				pilot.shipPiloting(null);
			
			pilot = p;
			if(p != null)
				p.shipPiloting(this);
		}
	}
	
	public Player pilot()
	{
		return pilot;
	}
	
	public int thrusterCount()
	{
		return thrusterPositions.size();
	}
	
	public float mass()
	{
		return totalMass;
	}

	@Override
	public float energy()
	{
		return energy;
	}

	@Override
	public float maxEnergy()
	{
		return 1000;
	}

	@Override
	public void useEnergy(float amount)
	{
		energy -= amount;
		if(energy < 0)
			energy = 0;
	}

	@Override
	public void addEnergy(float amount)
	{
		energy += amount;
		if(energy > maxEnergy())
			energy = maxEnergy();
	}
}
