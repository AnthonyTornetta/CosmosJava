package com.cornchipss.physics;

import java.util.ArrayList;
import java.util.List;

import org.joml.AxisAngle4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.utils.Maths;

public class Transform
{
	private Vector3f position;
	private Vector3f localPosition;
	
	private Vector3f velocity;
	private Vector3f localVelocity;
	
	private Quaternionf rotation;
	private Quaternionf localRotation;
	
	/**
	 * The axis this object sees the world with
	 */
	private Axis axis;
	
	private Transform parent;
	private List<Transform> children = new ArrayList<>(0); // most transforms wont have children
	
	public Transform()
	{
		this(Maths.zero());
	}
	
	public Transform(Vector3fc loc)
	{
		this(loc, Maths.blankQuaternion());
	}
	
	public Transform(Vector3fc loc, Vector3fc rot)
	{
		this(loc, Maths.quaternionFromRotation(rot));
		
		updateAxis();
	}
	
	public Transform(Vector3fc loc, Quaternionfc quat)
	{
		position = new Vector3f(localPosition = new Vector3f(loc));
		rotation = new Quaternionf(localRotation = Maths.clone(quat));
		velocity = new Vector3f(localVelocity = Maths.zero());
		
		updateAxis();
	}
	
	public Transform(Transform parent)
	{
		position = new Vector3f(parent.position());
		rotation = Maths.clone(parent.rotation());
		velocity = new Vector3f(parent.velocity());
		
		this.parent = parent;
		parent.children.add(this);
		
		this.localPosition = new Vector3f();
		this.localRotation = new Quaternionf();
		this.localVelocity = new Vector3f();
		
		updateAxis();
	}
	
	public boolean hasParent()
	{
		return parent != null;
	}
	
	public void removeParent()
	{
		if(hasParent())
		{
			parent.children.remove(this);
			this.parent = null;
			
			// These now represent the same things
			localPosition = new Vector3f(position);
			localRotation = new Quaternionf(rotation);
			localVelocity = new Vector3f(velocity);
		}
	}
	
	public void parent(Transform p)
	{
		removeParent();
		
		this.parent = p;
		parent.children.add(this);
		
		// These now represent differing things
		localPosition = new Vector3f(position);
		localRotation = new Quaternionf(rotation);
		localVelocity = new Vector3f(velocity);
		
		localRotation.div(parent.rotation());
		localPosition.sub(parent.position());
		localVelocity.sub(parent.velocity());
	}
	
	public Transform parent()
	{
		return parent;
	}
	
	public Vector3fc velocity()
	{
		return velocity;
	}
	
	public void velocity(Vector3fc vel)
	{
		accelerate(Maths.sub(vel, velocity));
	}
	
	public void accelerate(Vector3fc accel)
	{
		localVelocity.add(accel);
		
		applyAcceleration(accel);
	}
	
	public Quaternionfc localRotation()
	{
		return localRotation;
	}
	
	public void localRotation(float x, float y, float z)
	{
		localRotation(Maths.quaternionFromRotation(x, y, z));
	}
	
	public void localRotation(Vector3fc rot)
	{
		localRotation(Maths.quaternionFromRotation(rot));
	}
	
	public void localRotation(Quaternionfc rot)
	{
		Quaternionf delta = Maths.div(rot, localRotation);
		
		delta.normalize();
		localRotation.mul(delta);
		localRotation.normalize();
		
		applyRotation(delta);
	}
	
	private void applyRotation(Quaternionfc delta)
	{
		delta = delta.normalize(new Quaternionf());
		
		for(Transform child : children)
		{
			child.applyRotation(delta);
		}
		
		rotation.mul(delta);
		rotation.normalize();
		updateAxis();
	}
	
	public Quaternionfc rotation()
	{
		return rotation;
	}
	
	public void rotation(Vector3fc rot)
	{
		rotation(Maths.quaternionFromRotation(rot));
	}
	
	public void rotation(float x, float y, float z)
	{
		rotation(Maths.quaternionFromRotation(x, y, z));
	}
	
	/**
	 * Sets the rotation of this transform
	 * @param rot The rotation to set it to
	 */
	public void rotation(Quaternionfc rot)
	{
		Quaternionf delta = Maths.div(rot, rotation);
		delta.normalize();
		localRotation.mul(delta);
		
		applyRotation(delta);
	}
	
	public void position(float x, float y, float z)
	{
		position(new Vector3f(x, y, z));
	}
	
	public void position(Vector3fc pos)
	{
		Vector3f delta = Maths.sub(pos, position);
		
		position.add(delta);
		localPosition.add(delta);
		
		applyPosition(delta);
	}
	
	public Vector3fc position()
	{
		return position;
	}
	
	public void localPosition(float x, float y, float z)
	{
		localPosition(new Vector3f(x, y, z));
	}
	
	public void localPosition(Vector3fc pos)
	{
		Vector3f delta = Maths.sub(pos, localPosition);
		localPosition.add(delta);
		
		applyPosition(delta);
	}
	
	private void applyPosition(Vector3fc delta)
	{
		for(Transform child : children)
			child.applyPosition(delta);
		
		position.add(delta);
	}
	
	public Vector3fc localPosition()
	{
		return localPosition;
	}
	
	public void localVelocity(Vector3fc vel)
	{
		accelerate(Maths.sub(vel, localVelocity));
	}
	
	public Vector3fc localVelocity()
	{
		return localVelocity;
	}
	
	public void translate(Vector3fc amt)
	{
//		Utils.println(amt);
		
		localPosition.add(amt);
		
		applyTranslation(amt); // Applys this change to each child's transform's absolute position - not their local
		
//		Utils.println(position);
	}
	
	private void applyAcceleration(Vector3fc a)
	{
		for(Transform child : children)
			child.applyAcceleration(a);
		
		velocity.add(a);
	}
	
	private void applyTranslation(Vector3fc delta)
	{
		for(Transform child : children)
			child.applyTranslation(delta);
		
		position.add(delta);
	}
	
	public void rotate(Quaternionfc rotation)
	{
		applyRotation(rotation);
	}
	
	/**
	 * Gets the euler angles from this transform - if you want to apply these to this or another matrix, use {@link Transform#rotateXYZ(Vector3fc)}
	 * @return The euler angles of this transform
	 */
	public Vector3fc eulers()
	{
		return rotation.getEulerAnglesXYZ(new Vector3f());
	}
	
	public Vector3fc localEulers()
	{
		return localRotation.getEulerAnglesXYZ(new Vector3f());
	}
	
	/**
	 * Rotates the transform in the order of Z -> Y -> X
	 * @param zyx The rotation in the order of zyx (Vector should still have it as x, y, z)
	 */
	public void rotateZYX(Vector3fc zyx)
	{
		rotation.rotateZYX(zyx.z(), zyx.y(), zyx.x());
		localRotation.rotateZYX(zyx.z(), zyx.y(), zyx.x());
		
		for(Transform child : children)
			child.rotation.rotateZYX(zyx.z(), zyx.y(), zyx.x());
	}
	
	public void rotateXYZ(Vector3fc xyz)
	{
		this.localRotation.rotateXYZ(xyz.x(), xyz.y(), xyz.z());
		this.rotation.rotateXYZ(xyz.x(), xyz.y(), xyz.z());
		
		updateAxis();
		
		for(Transform child : children)
		{
			child.rotation.rotateXYZ(xyz.x(), xyz.y(), xyz.z());
			child.updateAxis();
		}
	}
	
	public void rotateX(float x)
	{
		this.localRotation.rotateX(x);
		rotation.rotateX(x);
		
		updateAxis();
		
		for(Transform child : children)
		{
			child.rotation.rotateX(x);
			child.updateAxis();
		}
	}
	
	public void rotateY(float y)
	{
		this.localRotation.rotateY(y);
		rotation.rotateY(y);
		
		updateAxis();
		
		for(Transform child : children)
		{
			child.rotation.rotateY(y);
			child.updateAxis();
		}
	}
	
	public void rotateZ(float z)
	{
		this.localRotation.rotateZ(z);
		rotation.rotateZ(z);
		
		updateAxis();
		
		for(Transform child : children)
		{
			child.rotation.rotateZ(z);
			child.updateAxis();
		}
	}
	
	private void updateAxis()
	{
		axis = new Axis(rotation);
	}

	public float x()
	{
		return position().x();
	}

	public float y()
	{
		return position().y();
	}

	public float z()
	{
		return position().z();
	}

	public Axis axis()
	{
		return axis;
	}

	public float distanceSqrd(Transform transform)
	{
		return Maths.dist(rotation().transform(position(), new Vector3f()), transform.rotation().transform(position(), new Vector3f()));
	}

	public Matrix4fc rotationMatrix()
	{
		return Maths.createRotationMatrix(rotation);
	}

	public Quaternionf rotate(AxisAngle4f axisAngle)
	{
		return null;
	}
}
