package com.cornchipss.entities;

import org.joml.Vector3f;

import com.cornchipss.physics.collision.hitbox.Hitbox;
import com.cornchipss.utils.Maths;
import com.cornchipss.world.Universe;

public abstract class Entity
{
	private Hitbox hitbox;
	
	private float rx, ry, rz;
	private Vector3f position;
	private Vector3f velocity;
	private Universe universe;
	
	public Entity(float x, float y, float z, Hitbox hitbox)
	{
		this.position = new Vector3f(x, y, z);
		this.velocity = Maths.zero();
		this.hitbox = hitbox;
	}
	
	public abstract void onUpdate();
	
	public Hitbox getHitbox() { return hitbox; }
	protected void setHitbox(Hitbox hitbox) { this.hitbox = hitbox; }
	
	public float getRx() { return rx; }
	public void setRx(float rx) { this.rx = rx; }

	public float getRy() { return ry; }
	public void setRy(float ry) { this.ry = ry; }

	public float getRz() { return rz; }
	public void setRz(float rz) { this.rz = rz; }

	public float getX() { return position.x; }
	public void setX(float x) { this.position.x = x; }

	public float getY() { return position.y; }
	public void setY(float y) { this.position.y = y; }

	public float getZ() { return position.z; }
	public void setZ(float z) { this.position.z = z; }
	
	public Vector3f getPosition() { return position; }
	public void setPosition(Vector3f pos) { this.position = pos; }
	
	public Vector3f getVelocity() { return velocity; }
	public void setVelocity(Vector3f vel) { this.velocity = vel; }
	
	public void addVelocityX(float x) { velocity.x += x; }
	public void addVelocityY(float y) { velocity.y += y; }
	public void addVelocityZ(float z) { velocity.z += z; }
	
	public float getVelocityX() { return velocity.x; }
	public float getVelocityY() { return velocity.y; }
	public float getVelocityZ() { return velocity.z; }
	
	public Universe getUniverse() { return universe; }
	public void setUniverse(Universe universe) { this.universe = universe; }
}
