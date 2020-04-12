package com.cornchipss.world.objects;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.physics.Transform;
import com.cornchipss.physics.collision.hitbox.Hitbox;
import com.cornchipss.utils.Constants;
import com.cornchipss.world.Universe;

public abstract class PhysicalObject
{
	private Transform transform;
	private Hitbox hitbox;
	
	private Universe universe;
	
	public PhysicalObject(Vector3fc position, Hitbox hitbox)
	{
		transform = new Transform(new Vector3f(position.x(), position.y(), position.z()));
		this.hitbox = hitbox;
	}
	
	public PhysicalObject(float x, float y, float z, Hitbox hitbox)
	{
		transform = new Transform(new Vector3f(x, y, z));
		this.hitbox = hitbox;
	}
	
	public PhysicalObject()
	{
		transform = new Transform();
		hitbox = null;
	}
	
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
		
		return Constants.GRAVITATIONAL_CONSTANT * getMass() * obj.getMass() / getTransform().distanceSqrd(obj.getTransform());
	}
	
	/**
	 * <p>Whether or not this object should create gravity</p>
	 * Useful for stopping ships from being as gravitational as planets
	 * @return Whether or not this object should create gravity
	 */
	public abstract boolean createsGravity();

	public Transform getTransform() { return transform; }
	public void setTransform(Transform transform) { this.transform = transform; }

	public Hitbox getHitbox() { return hitbox; }
	public void setHitbox(Hitbox hitbox) { this.hitbox = hitbox; }

	public Universe getUniverse() { return universe; }
	public void setUniverse(Universe universe) { this.universe = universe; }
}
