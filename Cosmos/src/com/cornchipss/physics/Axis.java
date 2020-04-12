package com.cornchipss.physics;

import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.utils.Maths;
import com.cornchipss.utils.Utils;

/**
 * Immutable class that represents an axis along a quaternion<br>
 * Useful for easy vector direction math
 */
public final class Axis
{
	private final Vector3fc xEndpoint, yEndpoint, zEndpoint; // assuming axis center is 0, 0, 0
	
	/**
	 * The "absolute" axis that all absolute things are based on
	 */
	public static final Axis ABSOLUTE = new Axis();
	
	/**
	 * Immutable class that represents an axis along a quaternion
	 * Creates the default axis
	 */
	public Axis()
	{
		xEndpoint = new Vector3f(1, 0, 0);
		yEndpoint = new Vector3f(0, 1, 0);
		zEndpoint = new Vector3f(0, 0, 1);
	}
	
	/**
	 * Immutable class that represents an axis along a quaternion
	 * @param q the quaternion to rotate the axis by
	 */
	public Axis(Quaternionfc q)
	{
		this(Maths.createRotationMatrix(q));
	}
	
	public Axis(Matrix4fc rot)
	{
		this.xEndpoint = new Vector3f(rot.m00(), rot.m10(), rot.m20());
		this.yEndpoint = new Vector3f(rot.m01(), rot.m11(), rot.m21());
		this.zEndpoint = new Vector3f(rot.m02(), rot.m12(), rot.m22());
	}
	
	/**
	 * An axis for the specified rotation (this sorta kinda works)
	 * @param rotationMatrix the rotation to rotate the axis by
	 */
//	public Axis(float xTheta, float yTheta, float zTheta)
//	{
//		this(new Vector3f(xTheta, yTheta, zTheta));
//		
//		xTheta = xTheta % Maths.TAU;
//		yTheta = yTheta % Maths.TAU;
//		zTheta = zTheta % Maths.TAU;
		
//		Utils.println(xTheta + ", " + yTheta + ", " + zTheta);
		
//		xEndpoint = new Vector3f(
//				Maths.cos(yTheta) + Maths.cos(zTheta), 
//				-Maths.sin(zTheta), 
//				Maths.sin(yTheta)).normalize();
//		
//		yEndpoint = new Vector3f(
//				Maths.sin(zTheta) + Maths.sin(yTheta), 
//				Maths.cos(xTheta) + Maths.cos(zTheta), 
//				-Maths.cos(yTheta) + Maths.sin(xTheta)).normalize();
//		
//		zEndpoint = new Vector3f(
//				-Maths.sin(yTheta), 
//				Maths.sin(xTheta), 
//				Maths.cos(xTheta) + Maths.cos(yTheta)).normalize();
		// things are wierd and im going to blame it on opengl, not me.
//		this.xEndpoint = Maths.rotatePoint(rotationMatrix, new Vector3f(0, 0, 1));
//		this.yEndpoint = Maths.rotatePoint(rotationMatrix, new Vector3f(0, 1, 0));
//		this.zEndpoint = Maths.rotatePoint(rotationMatrix, new Vector3f(-1, 0, 0));
//	}
	
	/**
	 * Converts a vector from {@link Axis#ABSOLUTE} to whatever this axis is
	 * @param origAxisVector The vector on the {@link Axis#ABSOLUTE} axis
	 * @return The vector translated to this new axis
	 */
	public Vector3f vectorInDirection(Vector3fc origAxisVector)
	{
		return new Vector3f(
				origAxisVector.x() * xEndpoint().x(),
				origAxisVector.x() * xEndpoint().y(),
				origAxisVector.x() * xEndpoint().z())
				.add(
				origAxisVector.y() * yEndpoint().x(), 
				origAxisVector.y() * yEndpoint().y(),
				origAxisVector.y() * yEndpoint().z())
				.add(
				origAxisVector.z() * zEndpoint().x(), 
				origAxisVector.z() * zEndpoint().y(),
				origAxisVector.z() * zEndpoint().z());
	}

	public Vector3fc xEndpoint() { return xEndpoint; }
	public Vector3fc yEndpoint() { return yEndpoint; }
	public Vector3fc zEndpoint() { return zEndpoint; }
	
	@Override
	public String toString()
	{
		return "AXIS (x: " + Utils.toEasyString(xEndpoint) + " y: " + Utils.toEasyString(yEndpoint) + " z: " + Utils.toEasyString(zEndpoint) + ")";
	}
}
