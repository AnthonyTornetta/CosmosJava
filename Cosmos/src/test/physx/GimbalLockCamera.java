package test.physx;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.utils.Maths;
import com.cornchipss.utils.Utils;

public class GimbalLockCamera extends Camera
{
	private Matrix4f matrix;
	private Vector3fc parentPosition;
	
	private Vector3f forward, right, up;
	private Vector3f rot;
	
	public GimbalLockCamera(Vector3fc parentPosition)
	{
		this.parentPosition = parentPosition;
		
		rot = new Vector3f();
		matrix = new Matrix4f();
		
		forward = new Vector3f(0, 0, -1); // opengl moment
		right = new Vector3f(1, 0, 0);
		up = new Vector3f(0, 1, 0);
		
		update();
	}
	
	private void update()
	{
		Maths.createViewMatrix(parentPosition, rot, matrix);
		
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
		Utils.println(parentPosition);
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
