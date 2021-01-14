package test;

import javax.vecmath.Vector3f;

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
		
		dVel.x *= delta * 1000;
		dVel.y *= delta * 1000;
		dVel.z *= delta * 1000;
		
		Vector3f dRot = new Vector3f();
		
		dRot.y -= Input.getMouseDeltaX() * 0.1f;
		
		dRot.x -= Input.getMouseDeltaY() * 0.1f;
		
		dRot.x *= delta;
		dRot.y *= delta;
		dRot.z *= delta;
		
		cam.rotate(dRot);
		
		if(Input.isKeyDown(GLFW.GLFW_KEY_R))
			cam.rotation(Maths.zero());
		
		Vector3 vel = body().getLinearVelocity(new javax.vecmath.Vector3f());
		
		vel.add(dVel);
		
		if(Input.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT))
			vel.mul(0.75f);

		vel = Maths.safeNormalize((Vector3f)vel, 2.0f);
		
		body().setLinearVelocity(vel);
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
