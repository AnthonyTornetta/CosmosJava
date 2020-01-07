package com.cornchipss.world.objects;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.physics.Transform;
import com.cornchipss.physics.collision.hitbox.Hitbox;
import com.cornchipss.utils.Constants;
import com.cornchipss.utils.Maths;
import com.cornchipss.utils.Utils;
import com.cornchipss.world.Universe;

public abstract class PhysicalObject
{
	private PhysicalObject parent;
	private List<PhysicalObject> children = new ArrayList<>();
	
	private Transform transform;
	private Vector3f velocity;
	private Hitbox hitbox;
	
	private Universe universe;
	
	public PhysicalObject(Vector3fc position, Hitbox hitbox)
	{
		transform = new Transform(new Vector3f(position.x(), position.y(), position.z()));
		this.velocity = Maths.zero();
		this.hitbox = hitbox;
	}
	
	public PhysicalObject(float x, float y, float z, Hitbox hitbox)
	{
		transform = new Transform(new Vector3f(x, y, z));
		this.velocity = Maths.zero();
		this.hitbox = hitbox;
	}
	
	public PhysicalObject()
	{
		transform = new Transform();
		velocity = Maths.zero();
		hitbox = null;
	}
	
	public void setParent(PhysicalObject newParent)
	{
		/*
		 * If they have a parent, make their relative transform to their absolute 
		 * because they are getting a new parent so their absolute position has to be transformed relative
		 * to the new parent
		 * 
		 * If the new parent is null, just dont bother setting anything relative because its not relative to anything
		 * If the new parent isnt null everything has to be made relative to that
		 */
		if(hasParent())
		{
			setRelativeTransform(getAbsoluteTransform());
		}
		if(newParent != null)
		{
			setRelativeTransform(newParent.getAbsoluteTransform().separate(getAbsoluteTransform()));
		}
		
		this.parent = newParent;
	}
	
	public void removeParent()
	{
		setParent(null);
	}
	
	public boolean hasParent() { return getParent() != null; }
	
	/**
	 * The transform relative to the center of the game world (0, 0, 0)
	 * @return The transform relative to the center of the game world (0, 0, 0) - modifying this may or may not effect the actual transform, so don't modify this unless you know what you're doing
	 */
	public Transform getAbsoluteTransform()
	{
		if(parent == null)
			return getTransform();
		
		return parent.getAbsoluteTransform().combine(getTransform());
	}

	public void addChild(PhysicalObject child)
	{
		children.add(child);
	}
	
	public void removeChild(PhysicalObject child)
	{
		children.remove(child);
	}
	
	public PhysicalObject getParent() { return parent; }
	public List<PhysicalObject> getChildren() { return children; }
	
	/**
	 * <p>Returns the mass of a physical object in Kilograms</p>
	 * <p>This will be used in calculations of gravity, collisions, etc</p>
	 * @return The mass of a physical object in Kilograms
	 */
	public abstract float getMass();
	
	/**
	 * <p>Gets the gravitational pull this object has on another</p>
	 * <p>returns 0 if THIS object doesn't create gravity</p>
	 * <p>For this reason call the function for both objects rather than assuming they are equal like in real life</p>
	 * @param obj The object to pull
	 * @return The gravitational pull this object has on another
	 */
	public float getGravitationalForce(PhysicalObject obj)
	{
		if(!createsGravity())
			return 0;
		
		return Constants.GRAVITATIONAL_CONSTANT * getMass() * obj.getMass() / (getPosition().distance(obj.getPosition()));
	}
	
	/**
	 * <p>Whether or not this object should create gravity</p>
	 * Useful for stopping ships from being as gravitational as planets
	 * @return Whether or not this object should create gravity
	 */
	public abstract boolean createsGravity();
	
	/**
	 * Gets the relative X of an object to its parent
	 * @return The relative X of an object to its parent
	 */
	public float getX() { return transform.getPosition().x(); }
	
	/**
	 * Sets the relative X of an object to its parent
	 */
	public void setX(float x) { transform.setX(x); }

	/**
	 * Gets the relative Y of an object to its parent
	 * @return The relative Y of an object to its parent
	 */
	public float getY() { return transform.getPosition().y(); }
	
	/**
	 * Sets the relative Y of an object to its parent
	 */
	public void setY(float y) { transform.setY(y); }

	/**
	 * Gets the relative Z of an object to its parent
	 * @return The relative Z of an object to its parent
	 */
	public float getZ() { return transform.getPosition().z(); }
	
	/**
	 * Sets the relative Z of an object to its parent
	 */
	public void setZ(float z) { transform.setZ(z); }
	
	/**
	 * Gets the relative Rotation X of an object to its parent
	 * @return The relative Rotation X of an object to its parent
	 */
	public float getRotationX() { return transform.getRotationX(); }
	
	/**
	 * Sets the relative Rotation X of an object to its parent
	 */
	public void setRotationX(float x) { transform.setRotationX(x); }

	/**
	 * Gets the relative Rotation Y of an object to its parent
	 * @return The relative Rotation Y of an object to its parent
	 */
	public float getRotationY() { return transform.getRotationY(); }
	
	/**
	 * Sets the relative Rotation Z of an object to its parent
	 */
	public void setRotationY(float y) { transform.setRotationY(y); }

	/**
	 * Gets the relative Rotation Z of an object to its parent
	 * @return The relative Rotation Z of an object to its parent
	 */
	public float getRotationZ() { return transform.getRotationZ(); }
	
	/**
	 * Sets the relative Rotation Z of an object to its parent
	 */
	public void setRotationZ(float z) { transform.setRotationZ(z); }
	
	/**
	 * Gets the relative Rotation of an object to its parent
	 * @return The relative Rotation of an object to its parent
	 */
	public Quaternionf getRotation() { return transform.getRotation(); }
	
	/**
	 * Sets the relative Rotation of an object to its parent
	 */
	public void setRotation(Quaternionf rot) { transform.setRotation(rot); }
	
	/**
	 * Sets the relative Rotation of an object to its parent
	 * @param x rotation X
	 * @param y rotation Y
	 * @param z rotation Z
	 */
	public void setRotation(float x, float y, float z) { transform.setRotation(x, y, z); }
	
	/**
	 * Rotates the object's relative rotation by a given amount
	 * @param amt The amount to rotate the planet by
	 */
	public void rotate(Vector3fc amt)
	{
		transform.rotate(amt);
	}
	
	/**
	 * Rotates the planet's relative rotation by a given amount
	 * @param rx The amount to rotate the planet by
	 * @param ry The amount to rotate the planet by
	 * @param rz The amount to rotate the planet by
	 */
	public void rotate(float rx, float ry, float rz)
	{
		transform.rotate(rx, ry, rz);
	}
	
	/**
	 * Gets the relative Position of an object to its parent
	 * @return The relative Position of an object to its parent
	 */
	public Vector3f getPosition() { return transform.getPosition(); }
	
	/**
	 * Sets the relative Position of an object to its parent
	 */
	public void setPosition(Vector3fc pos) { transform.setPosition(pos); }
	
	/**
	 * Adds relative x acceleration
	 * @param x The relative x acceleration to add
	 */
	public void accelerateX(float x) { velocity.x += x; }
	
	/**
	 * Adds relative y acceleration
	 * @param y The relative y acceleration to add
	 */
	public void accelerateY(float y) { velocity.y += y; }
	
	/**
	 * Adds relative z acceleration
	 * @param z The relative z acceleration to add
	 */
	public void accelerateZ(float z) { velocity.z += z; }
	
	/**
	 * Gets the relative velocity in the X direction
	 * @return the relative velocity in the X direction
	 */
	public float getVelocityX() { return velocity.x; }
	
	/**
	 * Gets the relative velocity in the Y direction
	 * @return the relative velocity in the Y direction
	 */
	public float getVelocityY() { return velocity.y; }
	
	/**
	 * Gets the relative velocity in the Z direction
	 * @return the relative velocity in the Z direction
	 */
	public float getVelocityZ() { return velocity.z; }
	
	/**
	 * The object's transform relative to whatever its parent is
	 * @return The object's transform relative to whatever its parent is
	 */
	public Transform getTransform() { return transform; }
	
	/**
	 * Sets the object's transform relative to whatever its parent is
	 * @param transform The object's transform relative to whatever its parent is
	 */
	public void setRelativeTransform(Transform transform) { this.transform = transform; }
	
	/**
	 * Gets the relative velocity to its parent
	 * @return The relative velocity to its parent
	 */
	public Vector3f getVelocity() { return getTransform().getVelocity(); }
	
	/**
	 * Sets the relative velocity to its parent
	 */
	public void setVelocity(Vector3f velocity) { this.velocity = velocity; }
	
	public Hitbox getHitbox() { return hitbox; }
	public void setHitbox(Hitbox hitbox) { this.hitbox = hitbox; }
	
	public Universe getUniverse() { return universe; }
	public void setUniverse(Universe universe) { this.universe = universe; }
	
	public Vector3fc getAbsolutePosition()
	{
		return getAbsoluteTransform().getPosition();
	}
	
	public Quaternionf getAbsoluteRotation()
	{
		return getAbsoluteTransform().getRotation();
	}
	
	public Vector3f getAbsoluteVelocity()
	{
		return getAbsoluteTransform().getVelocity();
	}
	
	/**
	 * Gets the ABSOLUTE combined rotation of the object
	 * @return The ABSOLUTE combined rotation of the object
	 */
	public Matrix4f getCombinedRotation()
	{
		return Maths.createCombinedRotationMatrix(getAbsoluteEulers());
	}
	
	/**
	 * Gets the RELATIVE combined rotation of the object
	 * @return The RELATIVE combined rotation of the object
	 */
	public Matrix4f getRelativeCombinedRotation()
	{
		return Maths.createCombinedRotationMatrix(getEulers());
	}
	
	public Matrix4f getRotationXMatrix()
	{
		return Maths.createRotationMatrix(Utils.x(), getAbsoluteRotation().x());
	}
	
	public Matrix4f getRotationYMatrix()
	{
		return Maths.createRotationMatrix(Utils.y(), getAbsoluteRotation().y());
	}
	
	public Matrix4f getRotationZMatrix()
	{
		return Maths.createRotationMatrix(Utils.z(), getAbsoluteRotation().z());
	}
	
	public Vector3f getAbsoluteEulers()
	{
		return getAbsoluteRotation().getEulerAnglesXYZ(new Vector3f());
	}
	
	public Vector3fc getEulers()
	{
		return transform.getEulers();
	}
}
