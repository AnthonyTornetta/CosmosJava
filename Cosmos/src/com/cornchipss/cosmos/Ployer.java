package com.cornchipss.cosmos;

import org.lwjgl.glfw.GLFW;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.CapsuleShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.Transform;
import com.cornchipss.cosmos.cameras.Camera;
import com.cornchipss.cosmos.cameras.GimbalLockCamera;
import com.cornchipss.cosmos.physx.PhysicalObject;
import com.cornchipss.cosmos.utils.Maths;
import com.cornchipss.cosmos.utils.io.Input;
import com.cornchipss.cosmos.world.ZaWARUDO;

public class Ployer extends PhysicalObject
{
	private GimbalLockCamera cam;
	
	public Ployer(ZaWARUDO world)
	{
		super(world);
		
		cam = new GimbalLockCamera(this);
	}
	
	@Override
	public void addToWorld(Transform transform)
	{
		RigidBodyConstructionInfo rbInfo = world().generateInfo(50, transform, new CapsuleShape(0.4f, 0.9f));
		rbInfo.restitution = 0.0f;
		body(world().createRigidBody(rbInfo));
		body().setActivationState(CollisionObject.DISABLE_DEACTIVATION); // never sleeps
	}
	
	public void update(float delta)
	{
		Vec3 dVel = new Vec3();
	    
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
		
		dVel.x(dVel.x() * (delta * 1000));
		dVel.z(dVel.z() * (delta * 1000));
		dVel.y(dVel.y() * (delta * 1000));
		
		if(Input.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL))
			dVel.mul(0.001f);
		
		Vec3 dRot = new Vec3();
		
		dRot.y(dRot.y() - Input.getMouseDeltaX() * 0.1f);
		
		dRot.x(dRot.x() - Input.getMouseDeltaY() * 0.1f);
		
		dRot.mul(delta);
		
		cam.rotate(dRot);
		
		if(Input.isKeyDown(GLFW.GLFW_KEY_R))
			cam.rotation(Maths.zero());
		
		Vec3 vel = new Vec3(body().getLinearVelocity(new javax.vecmath.Vector3f()));
		
		if(Input.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT))
			vel.mul(0.75f);

		vel.add(dVel);

		vel = Maths.safeNormalize(vel, 10.0f);
		
		if(Input.isKeyJustDown(GLFW.GLFW_KEY_SPACE))
			vel.y(vel.y() + 5);
		
		// "things just fall" - Mrs. Light, 2019
//		vel.y(vel.y() - 9.8f * delta);
		
		body().setLinearVelocity(vel.java());
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
