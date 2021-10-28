package com.cornchipss.cosmos.systems;

import java.util.List;

import org.joml.Vector3f;

import com.cornchipss.cosmos.blocks.StructureBlock;
import com.cornchipss.cosmos.blocks.modifiers.IThrustProducer;
import com.cornchipss.cosmos.netty.NettySide;
import com.cornchipss.cosmos.structures.Ship;
import com.cornchipss.cosmos.structures.Structure;
import com.cornchipss.cosmos.utils.Maths;

public class ThrusterSystem extends BlockSystem
{
	@Override
	public void update(Structure s, List<StructureBlock> blocks, float delta)
	{
		if(NettySide.side() == NettySide.CLIENT || blocks.size() == 0)
			return;
		
		if(s instanceof Ship)
		{
			Ship ship = (Ship)s;
			
			float energyCost = calculateEnergyUsedPerSecond(blocks) * delta;
			
			float thrustForce = calculateThrustForcePerSecond(blocks) * delta;
			
			Vector3f dVel = new Vector3f();
		    
			if(ship.movement().forward())
				dVel.add(ship.body().transform().forward());
			if(ship.movement().backward())
				dVel.sub(ship.body().transform().forward());
			if(ship.movement().right())
				dVel.add(ship.body().transform().right());
			if(ship.movement().left())
				dVel.sub(ship.body().transform().right());
			if(ship.movement().up())
				dVel.add(ship.body().transform().up());
			if(ship.movement().down())
				dVel.sub(ship.body().transform().up());
			
			if(dVel.x != 0 || dVel.y != 0 || dVel.z != 0 || 
					ship.movement().deltaRotation().x() != 0 || 
					ship.movement().deltaRotation().y() != 0 || 
					ship.movement().deltaRotation().z() != 0)
			{
				if(!ship.useEnergy(energyCost))
					return;
			}
			
			float accel = thrustForce / ship.mass();
			
			dVel.x = (dVel.x() * (accel));
			dVel.z = (dVel.z() * (accel));
			dVel.y = (dVel.y() * (accel));
			
			Vector3f vel = new Vector3f(ship.body().velocity());

			vel.add(dVel);
			
			vel = Maths.safeNormalize(vel, 100.0f);
			
			ship.body().velocity(vel);
			
			ship.body().angularVelocity(ship.movement().deltaRotation().negate(new Vector3f()));
		}
	}

	@Override
	public void addBlock(StructureBlock added, List<StructureBlock> otherBlocks)
	{
		
	}

	@Override
	public void removeBlock(StructureBlock removed, List<StructureBlock> otherBlocks)
	{
		
	}
	
	private float calculateThrustForcePerSecond(List<StructureBlock> thrusterBlocks)
	{
		if(thrusterBlocks.size() == 0)
			return 0;
		
		return thrusterBlocks.size() * ((IThrustProducer)thrusterBlocks.get(0).block()).thrustGeneratedPerSecond();
	}
	
	private float calculateEnergyUsedPerSecond(List<StructureBlock> thrusterBlocks)
	{
		if(thrusterBlocks.size() == 0)
			return 0;
		
		return thrusterBlocks.size() * ((IThrustProducer)thrusterBlocks.get(0).block()).powerUsedPerSecond();
	}
}
