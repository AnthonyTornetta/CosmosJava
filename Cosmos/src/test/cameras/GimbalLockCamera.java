package test.cameras;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.utils.Maths;

import test.physx.Transform;

public class GimbalLockCamera extends Camera
{
	private Matrix4f matrix;
	private Transform parent;
	
	private Vector3f forward, right, up;
	private Vector3f rot;
	
	public GimbalLockCamera(Transform parent)
	{
		this.parent = parent;
		
		rot = new Vector3f();
		matrix = new Matrix4f();
		
		forward = new Vector3f(0, 0, -1); // opengl moment
		right = new Vector3f(1, 0, 0);
		up = new Vector3f(0, 1, 0);
		
		update();
	}
	
	private void update()
	{
		rot.x = Maths.clamp(rot.x, -Maths.PI / 2, Maths.PI / 2);
		
		Maths.createViewMatrix(parent.position(), rot, matrix);
		
		forward.x = Maths.sin(rot.y) * Maths.cos(rot.x);
	    forward.y = Maths.sin(-rot.x);
	    forward.z = -Maths.cos(rot.x) * Maths.cos(rot.y);
	    
	    right.x = Maths.cos(rot.y);
	    right.z = Maths.sin(rot.y);
	    
	    up.x = Maths.sin(rot.y) * Maths.sin(rot.x);
	    up.y = Maths.cos(rot.x);
	    up.z = -Maths.sin(rot.x) * Maths.cos(rot.y);
	}
	
	public void rotate(Vector3fc delta)
	{
		rot.add(delta);
		
		update();
	}

	public void rotation(Vector3fc r)
	{
		rot.x = r.x();
		rot.y = r.y();
		rot.z = r.z();
		
		update();
	}
	
	@Override
	public Matrix4fc viewMatrix()
	{
		return matrix;
	}

	@Override
	public Vector3fc forward()
	{
		return forward;
	}

	@Override
	public Vector3fc right()
	{
		return right;
	}

	@Override
	public Vector3fc up()
	{
		return up;
	}
}
