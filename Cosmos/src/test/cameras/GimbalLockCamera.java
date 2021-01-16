package test.cameras;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;

import com.cornchipss.utils.Maths;

import test.Vec3;
import test.physx.PhysicalObject;

public class GimbalLockCamera extends Camera
{
	private Matrix4f matrix;
	private PhysicalObject parent;
	
	private Vec3 forward, right, up;
	private Vec3 rot;
	
	public GimbalLockCamera(PhysicalObject parent)
	{
		this.parent = parent;
		
		rot = new Vec3();
		matrix = new Matrix4f();
		
		forward = new Vec3(0, 0, -1); // opengl moment
		right = new Vec3(1, 0, 0);
		up = new Vec3(0, 1, 0);
		
		update();
	}
	
	private void update()
	{
		if(parent.initialized())
		{
			rot.x(Maths.clamp(rot.x(), -Maths.PI / 2, Maths.PI / 2));
			
			Maths.createViewMatrix(new Vec3(parent.position()).add(new Vec3(0, 0.4f, 0)), new Vec3(rot), matrix);
			
			forward.x(Maths.sin(rot.y()) * Maths.cos(rot.x()));
		    forward.y(Maths.sin(-rot.x()));
		    forward.z(-Maths.cos(rot.x()) * Maths.cos(rot.y()));
		    
		    right.x(Maths.cos(rot.y()));
		    right.z(Maths.sin(rot.y()));
		    
		    up.x(Maths.sin(rot.y()) * Maths.sin(rot.x()));
		    up.y(Maths.cos(rot.x()));
		    up.z(-Maths.sin(rot.x()) * Maths.cos(rot.y()));
		}
	}
	
	public void rotate(Vec3 delta)
	{
		rot.add(delta);
		
		update();
	}

	public void rotation(Vec3 r)
	{
		rot.set(r);
		
		update();
	}
	
	@Override
	public Matrix4fc viewMatrix()
	{
		return matrix;
	}

	@Override
	public Vec3 forward()
	{
		return forward;
	}

	@Override
	public Vec3 right()
	{
		return right;
	}

	@Override
	public Vec3 up()
	{
		return up;
	}

	public void parent(PhysicalObject transform)
	{
		this.parent = transform;
	}
	
	public PhysicalObject parent()
	{
		return parent;
	}
}
