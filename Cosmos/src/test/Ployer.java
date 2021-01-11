package test;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import com.cornchipss.utils.Input;
import com.cornchipss.utils.Maths;

import test.cameras.Camera;
import test.cameras.GimbalLockCamera;
import test.physx.Transform;

public class Ployer
{
	private Transform transform;
	
	private GimbalLockCamera cam;
	
	public Ployer()
	{
		this(0, 0, 0);
	}
	
	public Ployer(int x, int y, int z)
	{
		transform = new Transform(new Vector3f(x, y, z), new Vector3f(0.4f, .95f, 0.4f));
		cam = new GimbalLockCamera(transform);
	}
	
	public Transform transform()
	{
		return transform;
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
		
		dVel.mul(delta * 1000);
		
		Vector3f dRot = new Vector3f();
		
		dRot.y -= Input.getMouseDeltaX() * 0.1f;
		
		dRot.x -= Input.getMouseDeltaY() * 0.1f;
		
		dRot.mul(delta);
		
		cam.rotate(dRot);
		
		if(Input.isKeyDown(GLFW.GLFW_KEY_R))
			cam.rotation(Maths.zero());
		
		Vector3f vel = transform.getVelocity();
		
		vel.add(dVel);
		Maths.safeNormalize(vel, 2.0f);
		
		transform.setVelocity(vel);
	}

	public Camera camera()
	{
		return cam;
	}
}
