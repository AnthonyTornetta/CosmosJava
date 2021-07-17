package com.cornchipss.cosmos.systems;

import java.util.List;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import com.cornchipss.cosmos.blocks.StructureBlock;
import com.cornchipss.cosmos.blocks.modifiers.IThrustProducer;
import com.cornchipss.cosmos.structures.Ship;
import com.cornchipss.cosmos.structures.Structure;
import com.cornchipss.cosmos.utils.Maths;
import com.cornchipss.cosmos.utils.io.Input;

public class ThrusterSystem extends BlockSystem
{
	@Override
	public void update(Structure s, List<StructureBlock> blocks, float delta)
	{
		if(s instanceof Ship)
		{
			Ship ship = (Ship)s;
			float energyCost = calculateEnergyUsedPerSecond(blocks) * delta;
			
			if(ship.pilot() != null && ship.useEnergy(energyCost))
			{
				float thrustForce = calculateThrustForcePerSecond(blocks) * delta;
				
				Vector3f dVel = new Vector3f();
			    
				if(Input.isKeyDown(GLFW.GLFW_KEY_W))
					dVel.add(ship.body().transform().forward());
				if(Input.isKeyDown(GLFW.GLFW_KEY_S))
					dVel.sub(ship.body().transform().forward());
				if(Input.isKeyDown(GLFW.GLFW_KEY_D))
					dVel.add(ship.body().transform().right());
				if(Input.isKeyDown(GLFW.GLFW_KEY_A))
					dVel.sub(ship.body().transform().right());
				if(Input.isKeyDown(GLFW.GLFW_KEY_E))
					dVel.add(ship.body().transform().up());
				if(Input.isKeyDown(GLFW.GLFW_KEY_Q))
					dVel.sub(ship.body().transform().up());
				
				float accel = thrustForce / ship.mass();
				
				dVel.x = (dVel.x() * (accel));
				dVel.z = (dVel.z() * (accel));
				dVel.y = (dVel.y() * (accel));
				
				Vector3f dRot = new Vector3f();
				
				Vector3f vel = ship.body().velocity();
				
				if(Input.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT))
					vel.mul(0.75f);
		
				vel.add(dVel);
		
				vel = Maths.safeNormalize(vel, 100.0f);
				
				ship.body().velocity(vel);
				
				if(Input.isKeyDown(GLFW.GLFW_KEY_C))
					dRot.z += 2;
				if(Input.isKeyDown(GLFW.GLFW_KEY_Z))
					dRot.z -= 2;
				
				dRot.y = Input.getMouseDeltaX() * 0.1f;
				
				dRot.x = Input.getMouseDeltaY() * 0.1f;
				
				dRot.mul(0.01f);
				
				ship.body().transform().rotateRelative(dRot);
			}
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
