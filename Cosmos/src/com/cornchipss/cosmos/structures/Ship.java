package com.cornchipss.cosmos.structures;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.glfw.GLFW;

import com.cornchipss.cosmos.Player;
import com.cornchipss.cosmos.utils.Maths;
import com.cornchipss.cosmos.utils.Utils;
import com.cornchipss.cosmos.utils.io.Input;
import com.cornchipss.cosmos.world.ZaWARUDO;

/**
 * A structure representing a ship
 */
public class Ship extends Structure
{
	private final static int MAX_DIMENSIONS = 16 * 9;
	
	private Player pilot;
	
	private Vector3f corePos = new Vector3f();
	
	public Ship(ZaWARUDO world)
	{
		super(world, MAX_DIMENSIONS, MAX_DIMENSIONS, MAX_DIMENSIONS);
	}
	
	public Vector3fc corePosition()
	{
		corePos.set(MAX_DIMENSIONS / 2.f, MAX_DIMENSIONS / 2.f, MAX_DIMENSIONS / 2.f);
		return corePos;
	}
	
	int temp = 0;
	
	@Override
	public void update(float delta)
	{
		if(pilot != null)
		{			
			pilot.body().velocity(Maths.zero());
			pilot.body().transform().position(localCoordsToWorldCoords(width()/2, height()/2, length()/2));
			pilot.camera().zeroRotation();
			
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
			
			dVel.x = (dVel.x() * (delta * 20));
			dVel.z = (dVel.z() * (delta * 20));
			dVel.y = (dVel.y() * (delta * 20));
			
			Vector3f dRot = new Vector3f();
			
			Vector3f vel = body().velocity();
			
			if(Input.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT))
				vel.mul(0.75f);
	
			vel.add(dVel);
	
			vel = Maths.safeNormalize(vel, 100.0f);
			
			body().velocity(vel);
			
			if(Input.isKeyDown(GLFW.GLFW_KEY_KP_9))
				dRot.z += 1;
			if(Input.isKeyDown(GLFW.GLFW_KEY_KP_7))
				dRot.z -= 1;
			if(Input.isKeyDown(GLFW.GLFW_KEY_KP_4))
				dRot.y += 1;
			if(Input.isKeyDown(GLFW.GLFW_KEY_KP_6))
				dRot.y -= 1;
			if(Input.isKeyDown(GLFW.GLFW_KEY_KP_8))
				dRot.x += 1;
			if(Input.isKeyDown(GLFW.GLFW_KEY_KP_2))
				dRot.x -= 1;
			
			dRot.mul(0.01f);

			Quaternionf temp = new Quaternionf();
			temp.set(body().transform().rotation());
			temp.rotateAxis(dRot.z, body().transform().forward());
			
			temp.rotateAxis(dRot.y, body().transform().up());
			temp.rotateAxis(-dRot.x, body().transform().right());
			
			body().transform().rotation(temp);
			
			pilot.body().transform().rotation(body().transform().rotation());
		}
		else
			body().velocity(body().velocity().mul(0.99f)); // no more drifting into space once the pilot leaves
	}

	public void setPilot(Player p)
	{
		if(!Utils.equals(pilot, p))
		{
			if(pilot != null)
				pilot.pilotingShip(null);
			
			pilot = p;
			if(p != null)
				p.pilotingShip(this);
		}
	}
	
	public Player pilot()
	{
		return pilot;
	}
}
