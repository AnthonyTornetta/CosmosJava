package com.cornchipss.cosmos;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import com.cornchipss.cosmos.cameras.Camera;
import com.cornchipss.cosmos.cameras.GimbalLockCamera;
import com.cornchipss.cosmos.physx.PhysicalObject;
import com.cornchipss.cosmos.physx.RigidBody;
import com.cornchipss.cosmos.physx.Transform;
import com.cornchipss.cosmos.utils.Maths;
import com.cornchipss.cosmos.utils.io.Input;
import com.cornchipss.cosmos.world.ZaWARUDO;

public class Player extends PhysicalObject
{
	private GimbalLockCamera cam;
	
	public Player(ZaWARUDO world)
	{
		super(world);
		
		cam = new GimbalLockCamera(this);
	}
	
	@Override
	public void addToWorld(Transform transform)
	{
		body(new RigidBody(transform));
		world().addRigidBody(body());
	}
	
	public void update(float delta)
	{
		Vector3f dVel = new Vector3f();
	    
		if(Input.isKeyDown(GLFW.GLFW_KEY_W))
			dVel.add(cam.forward());
		if(Input.isKeyDown(GLFW.GLFW_KEY_S))
			dVel.sub(cam.forward());
		if(Input.isKeyDown(GLFW.GLFW_KEY_D))
			dVel.add(cam.right());
		if(Input.isKeyDown(GLFW.GLFW_KEY_A))
			dVel.sub(cam.right());
		if(Input.isKeyDown(GLFW.GLFW_KEY_E))
			dVel.add(cam.up());
		if(Input.isKeyDown(GLFW.GLFW_KEY_Q))
			dVel.sub(cam.up());
		
		dVel.x = (dVel.x() * (delta * 1000));
		dVel.z = (dVel.z() * (delta * 1000));
		dVel.y = (dVel.y() * (delta * 1000));
		
		if(Input.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL))
			dVel.mul(0.001f);
		
		Vector3f dRot = new Vector3f();
		
		dRot.y = (dRot.y() - Input.getMouseDeltaX() * 0.1f);
		
		dRot.x = (dRot.x() - Input.getMouseDeltaY() * 0.1f);
		
		dRot.mul(delta);
		
		cam.rotate(dRot);
		
		if(Input.isKeyDown(GLFW.GLFW_KEY_R))
			cam.rotation(Maths.zero());
		
		Vector3f vel = body().velocity();
		
		if(Input.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT))
			vel.mul(0.75f);

		vel.add(dVel);

		vel = Maths.safeNormalize(vel, 10.0f);
		
		if(Input.isKeyJustDown(GLFW.GLFW_KEY_SPACE))
			vel.y = (vel.y() + 5);
		
		// "things just fall" - Mrs. Light, 2019
//		vel.y(vel.y() - 9.8f * delta);
		
		body().velocity(vel);
	}

	public Camera camera()
	{
		return cam;
	}
	
	@Override
	public void body(RigidBody b)
	{
		super.body(b);
		
		cam.parent(this);
	}
}
