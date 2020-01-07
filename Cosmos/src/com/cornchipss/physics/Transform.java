package com.cornchipss.physics;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.utils.Maths;

public class Transform
{
	private Vector3f position;
	private Quaternionf rotation;
	private Vector3f velocity = Maths.zero();
	
	public Transform()
	{
		this(Maths.zero());
	}
	
	public Transform(Vector3fc position)
	{
		this(position, new Quaternionf());
	}
	
	public Transform(Vector3fc position, Quaternionf rotation)
	{
		setPosition(new Vector3f(position));
		setRotation(rotation);
	}
	
	/**
	 * Combines two other transforms without modifying either one<br>
	 * Equivilant to <code>return this + other</code> - make sure it's parent + child - not the other way around
	 * @param child The transform to combine this with
	 * @return Combination of two other transforms without modifying either one
	 */
	public Transform combine(Transform child)
	{
		Transform t = new Transform(
				Maths.add(getPosition(), Maths.rotatePoint(rotation, child.getPosition())), 
				Maths.mul(child.rotation, rotation));
		t.setVelocity(Maths.add(child.velocity, velocity));
		return t;
	}
	
	/**
	 * Separates two other transforms without modifying either one<br>
	 * The inverse of {@link Transform#combine(Transform)}<br>
	 * Equivilant to <code>return this - other</code> - make sure it's parent - child - not the other way around
	 * @param child The transform to seperate this from
	 * @return Separation of two other transforms without modifying either one
	 */
	public Transform separate(Transform child)
	{
		Transform t = new Transform(
				Maths.sub(Maths.rotatePoint(Maths.invert(rotation), child.getPosition()), getPosition()), 
				Maths.div(child.getRotation(), getRotation())); // correct rotation
		t.setVelocity(Maths.sub(child.velocity, velocity));
		return t;
	}
	
	public Vector3f getPosition() { return position; }
	public void setPosition(Vector3fc position)
	{
		this.position = new Vector3f(position);
	}

	public void setX(float x) { position.x = x; }
	public void setY(float y) { position.y = y; }
	public void setZ(float z) { position.z = z; }
	
	public float getX() { return position.x; }
	public float getY() { return position.y; }
	public float getZ() { return position.z; }
	
	public void translate(Vector3fc amt)
	{
		setX(amt.x() + getX());
		setY(amt.y() + getY());
		setZ(amt.z() + getZ());
	}
	
	public Quaternionf getRotation() { return rotation; }
	public void setRotation(Quaternionf rotation)
	{
		this.rotation = rotation;
	}
	
	public void setRotation(float rx, float ry, float rz)
	{
		rotation = Maths.quaternionFromRotation(rx, ry, rz);
	}
	
	/**
	 * Rotates the object in the order of X, Y, then Z
	 * @param rx rotation x
	 * @param ry rotation y
	 * @param rz rotation z
	 */
	public void rotateXYZ(float rx, float ry, float rz)
	{
		rotation.rotateXYZ(rx, ry, rz);
	}
	
	/**
	 * Rotates the object in the order of Y, X, then Z
	 * @param rx rotation x
	 * @param ry rotation y
	 * @param rz rotation z
	 */
	public void rotateYXZ(float rx, float ry, float rz)
	{
		rotation.rotateYXZ(rx, ry, rz);
	}
	
	/**
	 * Rotates the object in the order of Z, Y, then X
	 * @param rx rotation x
	 * @param ry rotation y
	 * @param rz rotation z
	 */
	public void rotateZYX(float rx, float ry, float rz)
	{
		rotation.rotateZYX(rx, ry, rz);
	}
	
	/**
	 * Rotates about the x axis
	 * @param rx the radians to rotate
	 */
	public void rotateX(float rx)
	{
		rotation.rotateX(rx);
	}
	
	/**
	 * Rotates about the y axis
	 * @param ry the radians to rotate
	 */
	public void rotateY(float ry)
	{
		rotation.rotateY(ry);
	}
	
	/**
	 * Rotates about the z axis
	 * @param rz the radians to rotate
	 */
	public void rotateZ(float rz)
	{
		rotation.rotateZ(rz);
	}
	
	/**
	 * Rotates X, Y then Z
	 * @param delta The amount to rotate
	 */
	public void rotateXYZ(Vector3fc delta)
	{
		rotateXYZ(delta.x(), delta.y(), delta.z());
	}
	
	/**
	 * Take caution when using this
	 * Sets the rotation X
	 * @param rx The rotation x
	 */
	@Deprecated
	public void setRotationX(float rx)
	{
		Vector3f eulers = rotation.getEulerAnglesXYZ(new Vector3f());
		setRotation(rx, eulers.y(), eulers.z());
	}
	
	/**
	 * Take caution when using this
	 * Sets the rotation X
	 * @param ry The rotation x
	 */
	@Deprecated
	public void setRotationY(float ry)
	{
		Vector3f eulers = rotation.getEulerAnglesXYZ(new Vector3f());
		setRotation(eulers.x(), ry, eulers.z());
	}
	
	/**
	 * Take caution when using this
	 * Sets the rotation Z
	 * @param rz The rotation z
	 */
	@Deprecated
	public void setRotationZ(float rz)
	{
		Vector3f eulers = rotation.getEulerAnglesXYZ(new Vector3f());
		setRotation(eulers.x(), eulers.y(), rz);
	}
	
	public void resetRotation()
	{
		rotation = Maths.blankQuaternion();
	}
	
	/**
	 * Gets the velocity
	 * @return the velocity
	 */
	public Vector3f getVelocity() { return velocity; }
	
	/**
	 * Stores velocity - does not change the position
	 * @param v the velocity value to store
	 */
	public void setVelocity(Vector3f v) { velocity = v; }
	
	/**
	 * Adds the the velocity
	 * @param amt The amount to add to the velocity
	 */
	public void accelerate(Vector3fc amt)
	{
		velocity.x += amt.x();
		velocity.y += amt.y();
		velocity.z += amt.z();
	}
}
