package com.cornchipss.entities;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.physics.Transform;
import com.cornchipss.physics.collision.hitbox.Hitbox;
import com.cornchipss.utils.Maths;
import com.cornchipss.world.Universe;

public abstract class Entity
{
	private Hitbox hitbox;
	
	private Transform transform;
	
	private Vector3f velocity;
	private Universe universe;
	
	public Entity(float x, float y, float z, Hitbox hitbox)
	{
		transform = new Transform(new Vector3f(x, y, z));
		this.velocity = Maths.zero();
		this.hitbox = hitbox;
	}
	
	public abstract void onUpdate();
	
	public Hitbox getHitbox() { return hitbox; }
	protected void setHitbox(Hitbox hitbox) { this.hitbox = hitbox; }
	
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
	
	public Vector3f getPosition() { return transform.getPosition(); }
	public void setPosition(Vector3fc pos) { transform.setPosition(pos); }
	
	public Vector3f getVelocity() { return velocity; }
	public void setVelocity(Vector3f vel) { this.velocity = vel; }
	
	public void addVelocityX(float x) { velocity.x += x; }
	public void addVelocityY(float y) { velocity.y += y; }
	public void addVelocityZ(float z) { velocity.z += z; }
	
	public float getVelocityX() { return velocity.x; }
	public float getVelocityY() { return velocity.y; }
	public float getVelocityZ() { return velocity.z; }
	
	public Transform getTransform() { return transform; }
	public void setTransform(Transform t) { this.transform = t; }
	
	public Universe getUniverse() { return universe; }
	public void setUniverse(Universe universe) { this.universe = universe; }
}
