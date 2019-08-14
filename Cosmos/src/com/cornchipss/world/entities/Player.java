package com.cornchipss.world.entities;

import org.lwjgl.glfw.GLFW;

import com.cornchipss.utils.Input;
import com.cornchipss.world.Entity;

public class Player extends Entity
{
	public Player(float x, float y, float z)
	{
		super(x, y, z);
	}
	
	// TODO
//	public Vector3f getBlockLookingAt()
//	{
//		
//	}
	
	@Override
	public void onUpdate()
	{
		float vX = 0, vY = 0, vZ = 0;
		
		float speed = Input.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT) ? 3f : .2f;
		
		float sensitivity = 0.0025f;
		
		if(Input.isKeyDown(GLFW.GLFW_KEY_W))
		{
			vX += speed * Math.sin(getRy());
			vZ -= speed * Math.cos(getRy());
		}
		if(Input.isKeyDown(GLFW.GLFW_KEY_S))
		{
			vX -= speed * Math.sin(getRy());
			vZ += speed * Math.cos(getRy());
		}
		if(Input.isKeyDown(GLFW.GLFW_KEY_A))
		{
			vX -= speed * Math.cos(getRy());
			vZ -= speed * Math.sin(getRy());
		}
		if(Input.isKeyDown(GLFW.GLFW_KEY_D))
		{
			vX += speed * Math.cos(getRy());
			vZ += speed * Math.sin(getRy());
		}
		
		if(Input.isKeyDown(GLFW.GLFW_KEY_E))
		{
			vY += speed;
		}
		if(Input.isKeyDown(GLFW.GLFW_KEY_Q))
			vY -= speed;
		
		if(Input.isKeyDown(GLFW.GLFW_KEY_R))
		{
			setX(0);
			setY(0);
			setZ(0);
			setRx(0);
			setRy(0);
		}
		
		vX *= speed;
		vY *= speed;
		vZ *= speed;
		
		setX(getX() + vX);
		setY(getY() + vY);
		setZ(getZ() + vZ);
		
		setRx(getRx() + sensitivity * -Input.getMouseDeltaY());
		setRy(getRy() + sensitivity * -Input.getMouseDeltaX());
		
		if(getRx() > Math.PI / 2)
			setRx((float)Math.PI / 2);
		else if(getRx() < -Math.PI / 2)
			setRx((float)-Math.PI / 2);
	}
}
