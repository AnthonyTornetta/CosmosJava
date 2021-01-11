package test.physx;

import javax.annotation.Nonnull;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.utils.Maths;

public class Rotation
{
	private Quaternionf rotation;
	private Matrix4f matrix;
	
	private Vector3f forward, right, up;
	
	public Rotation(@Nonnull Quaternionfc rotation)
	{
		this.rotation = new Quaternionf().set(rotation);
		
		updateAxis();
	}
	
	public Rotation()
	{
		this(Maths.blankQuaternion());
	}
	
	private void updateAxis()
	{
		//  Quaternion * Vector
		forward = rotation.transform(new Vector3f(0, 0, -1));
		right = rotation.transform(new Vector3f(1, 0, 0));
		up = rotation.transform(new Vector3f(0, 1, 0));
		
		matrix = Maths.createRotationMatrix(rotation);
	}
	
	public @Nonnull Vector3fc forward()
	{
		return forward;
	}
	public @Nonnull Vector3fc right()
	{
		return right;
	}
	public @Nonnull Vector3fc up()
	{
		return up;
	}
	
	public @Nonnull Quaternionfc rotation()
	{
		return rotation;
	}
	
	public void rotation(Quaternionfc rot)
	{
		rotation.set(rot);
		updateAxis();
	}
	
	public @Nonnull Matrix4fc matrix()
	{
		return matrix;
	}
}
