package com.cornchipss.world;

public abstract class Entity
{
	private float x, y, z;
	private float rx, ry, rz;
	
	public Entity(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public abstract void onUpdate();
	
	public float getRx() {
		return rx;
	}

	public void setRx(float rx) {
		this.rx = rx;
	}

	public float getRy() {
		return ry;
	}

	public void setRy(float ry) {
		this.ry = ry;
	}

	public float getRz() {
		return rz;
	}

	public void setRz(float rz) {
		this.rz = rz;
	}

	public float getX() { return x; }
	public void setX(float x) { this.x = x; }

	public float getY() { return y; }
	public void setY(float y) { this.y = y; }

	public float getZ() { return z; }
	public void setZ(float z) { this.z = z; }
}
