package test;

import org.lwjgl.glfw.GLFW;

import com.bulletphysics.dynamics.RigidBody;
import com.cornchipss.utils.Input;
import com.cornchipss.utils.Maths;

import test.cameras.Camera;
import test.cameras.GimbalLockCamera;
import test.physx.PhysicalObject;

public class Ployer extends PhysicalObject
{
	private GimbalLockCamera cam;
	
	public Ployer(RigidBody body)
	{
		super(body);
		
		cam = new GimbalLockCamera(this);
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
		dVel.y(dVel.y() * (delta * 20.0f));
		
		Vec3 dRot = new Vec3();
		
		dRot.y(dRot.y() - Input.getMouseDeltaX() * 0.1f);
		
		dRot.x(dRot.x() - Input.getMouseDeltaY() * 0.1f);
		
		dRot.mul(delta);
		
		cam.rotate(dRot);
		
		if(Input.isKeyDown(GLFW.GLFW_KEY_R))
			cam.rotation(Maths.zero());
		
		Vec3 vel = new Vec3(body().getLinearVelocity(new javax.vecmath.Vector3f()));
		
		vel.add(dVel);
		
		if(Input.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT))
			vel.mul(0.75f);
		
		vel = Maths.safeNormalizeXZ(vel, 5.0f);
		
		if(Input.isKeyJustDown(GLFW.GLFW_KEY_SPACE))
			vel.y(vel.y() + 5);
		
		vel.y(vel.y() - 9.8f * delta);
		
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
