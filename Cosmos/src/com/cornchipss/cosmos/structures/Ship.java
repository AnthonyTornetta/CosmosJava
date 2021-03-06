package com.cornchipss.cosmos.structures;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import com.cornchipss.cosmos.Player;
import com.cornchipss.cosmos.cameras.GimbalLockCamera;
import com.cornchipss.cosmos.utils.Maths;
import com.cornchipss.cosmos.utils.Utils;
import com.cornchipss.cosmos.utils.io.Input;
import com.cornchipss.cosmos.world.ZaWARUDO;

/**
 * A structure representing a ship
 */
public class Ship extends Structure
{
	private final static int MAX_DIMENSIONS = 16 * 10;
	
	private Player pilot;
	
	public Ship(ZaWARUDO world)
	{
		super(world, MAX_DIMENSIONS, MAX_DIMENSIONS, MAX_DIMENSIONS);
	}
	
	@Override
	public void update(float delta)
	{
		if(pilot != null)
		{
			pilot.body().velocity(Maths.zero());
			pilot.body().transform().position(localCoordsToWorldCoords(width()/2, height()/2, length()/2));
			((GimbalLockCamera)pilot.camera()).rotation(new Vector3f(0, 0, 0)); //  TODO: not do this
			
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
			
			dVel.x = (dVel.x() * (delta * 1000));
			dVel.z = (dVel.z() * (delta * 1000));
			dVel.y = (dVel.y() * (delta * 1000));
			
			if(Input.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL))
				dVel.mul(0.001f);
			
			Vector3f dRot = new Vector3f();
			
			dRot.y = (dRot.y() - Input.getMouseDeltaX() * 0.1f);
			
			dRot.x = (dRot.x() - Input.getMouseDeltaY() * 0.1f);
			
			dRot.mul(delta);
			
			Vector3f vel = body().velocity();
			
			if(Input.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT))
				vel.mul(0.75f);
	
			vel.add(dVel);
	
			vel = Maths.safeNormalize(vel, 10.0f);
			
			body().velocity(vel);
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
