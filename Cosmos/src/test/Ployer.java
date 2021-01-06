package test;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import com.cornchipss.utils.Input;

import test.physx.Camera;
import test.physx.GimbalLockCamera;

public class Ployer
{
	private Vector3f pos;
	
	private GimbalLockCamera cam;
	
	public Ployer()
	{
		this(0, 0, 0);
	}
	
	public Ployer(int x, int y, int z)
	{
		pos = new Vector3f(x, y, z);
		cam = new GimbalLockCamera(pos);
	}
	
	public Vector3f position()
	{
		return pos;
	}

	public void update(float delta)
	{
		Vector3f dPos = new Vector3f();
	    
		if(Input.isKeyDown(GLFW.GLFW_KEY_W))
			dPos.add(cam.forward());
		if(Input.isKeyDown(GLFW.GLFW_KEY_S))
			dPos.sub(cam.forward());
		if(Input.isKeyDown(GLFW.GLFW_KEY_D))
			dPos.add(cam.right());
		if(Input.isKeyDown(GLFW.GLFW_KEY_A))
			dPos.sub(cam.right());
		if(Input.isKeyDown(GLFW.GLFW_KEY_E))
			dPos.add(cam.up());
		if(Input.isKeyDown(GLFW.GLFW_KEY_Q))
			dPos.sub(cam.up());
		
		dPos.mul(10 * delta);
		
		Vector3f dRot = new Vector3f();

		if(Input.isKeyDown(GLFW.GLFW_KEY_C))
			dRot.y += 1f;
		if(Input.isKeyDown(GLFW.GLFW_KEY_Z))
			dRot.y -= 1f;
		
		if(Input.isKeyDown(GLFW.GLFW_KEY_R))
			dRot.x += 1f;
		if(Input.isKeyDown(GLFW.GLFW_KEY_T))
			dRot.x -= 1f;
		
		dRot.mul(delta);
		
		cam.rotate(dRot);
		pos.add(dPos);
	}

	public Camera camera()
	{
		return cam;
	}
}
