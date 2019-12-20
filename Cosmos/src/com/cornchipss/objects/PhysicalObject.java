package com.cornchipss.objects;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.physics.Transform;
import com.cornchipss.physics.collision.hitbox.Hitbox;
import com.cornchipss.utils.Constants;
import com.cornchipss.utils.Maths;
import com.cornchipss.world.Universe;

public abstract class PhysicalObject
{
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
	
	public float getX() { return transform.getPosition().x(); }
	public void setX(float x) { transform.setX(x); }

	public float getY() { return transform.getPosition().y(); }
	public void setY(float y) { transform.setY(y); }

	public float getZ() { return transform.getPosition().z(); }
	public void setZ(float z) { transform.setZ(z); }

	public float getRx() { return transform.getRotationX(); }
	public void setRx(float x) { transform.setRotationX(x); }

	public float getRy() { return transform.getRotationY(); }
	public void setRy(float y) { transform.setRotationY(y); }

	public float getRz() { return transform.getRotationZ(); }
	public void setRz(float z) { transform.setRotationZ(z); }
	
	public Vector3fc getRotation() { return transform.getRotation(); }
	public void setRotation(Vector3fc rot) { transform.setRotation(rot); }
	public void setRotation(float x, float y, float z) { transform.setRotation(x, y, z); }
	
	public Vector3f getPosition() { return transform.getPosition(); }
	public void setPosition(Vector3fc pos) { transform.setPosition(pos); }
	
	public void addVelocityX(float x) { velocity.x += x; }
	public void addVelocityY(float y) { velocity.y += y; }
	public void addVelocityZ(float z) { velocity.z += z; }
	
	public float getVelocityX() { return velocity.x; }
	public float getVelocityY() { return velocity.y; }
	public float getVelocityZ() { return velocity.z; }
	
	public Transform getTransform() { return transform; }
	public void setTransform(Transform transform) { this.transform = transform; }

	public Vector3f getVelocity() { return velocity; }
	public void setVelocity(Vector3f velocity) { this.velocity = velocity; }

	public Hitbox getHitbox() { return hitbox; }
	public void setHitbox(Hitbox hitbox) { this.hitbox = hitbox; }
	
	public Universe getUniverse() { return universe; }
	public void setUniverse(Universe universe) { this.universe = universe; }
}
