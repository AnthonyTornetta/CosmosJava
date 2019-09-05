package com.cornchipss.world.entities;

import org.lwjgl.glfw.GLFW;

import com.cornchipss.physics.collision.hitbox.RectangleHitbox;
import com.cornchipss.utils.Input;
import com.cornchipss.utils.Utils;

public class Player extends Entity
{
	private float sensitivity = 0.0025f;
	private float maxSlowdown = 1f;
	
	public Player(float x, float y, float z)
	{
		super(x, y, z, new RectangleHitbox(0.45f, 0.9f, 0.45f));
	}
	
	@Override
	public void onUpdate()
	{
		if(!Input.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT))
		{
			float speed = Input.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) ? .2f : .02f;
			
			if(Input.isKeyDown(GLFW.GLFW_KEY_W))
			{
				addVelocityX((float) (speed * Math.sin(getRy())));
				addVelocityZ((float) (-speed * Math.cos(getRy())));
			}
			if(Input.isKeyDown(GLFW.GLFW_KEY_S))
			{
				addVelocityX((float) (-speed * Math.sin(getRy())));
				addVelocityZ((float) (speed * Math.cos(getRy())));
			}
			if(Input.isKeyDown(GLFW.GLFW_KEY_A))
			{
				addVelocityX((float) (-speed * Math.cos(getRy())));
				addVelocityZ((float) (-speed * Math.sin(getRy())));
			}
			if(Input.isKeyDown(GLFW.GLFW_KEY_D))
			{
				addVelocityX((float) (speed * Math.cos(getRy())));
				addVelocityZ((float) (speed * Math.sin(getRy())));
			}
			
			if(Input.isKeyDown(GLFW.GLFW_KEY_E))
			{
				addVelocityY(speed);
			}
			if(Input.isKeyDown(GLFW.GLFW_KEY_Q))
			{
				addVelocityY(-speed);
			}
			
			if(Input.isKeyDown(GLFW.GLFW_KEY_R))
			{
				setX(0);
				setY(0);
				setZ(0);
				setRx(0);
				setRy(0);
				setVelocity(Utils.zero());
			}
		}
		else
		{
			addVelocityX(Utils.clamp(-getVelocityX(), -maxSlowdown, maxSlowdown) * 0.1f);
			addVelocityY(Utils.clamp(-getVelocityY(), -maxSlowdown, maxSlowdown) * 0.1f);
			addVelocityZ(Utils.clamp(-getVelocityZ(), -maxSlowdown, maxSlowdown) * 0.1f);
		}
		
		updatePhysics();
		
//		addVelocityY(-9.8f / 60.0f);
		
//		Vector3f cloesestBlock = getUniverse().getClosestBlock(getX(), getY(), getZ(), 0);
//		if(cloesestBlock != null)
//		{
//			Block b = getUniverse().getBlockAt(cloesestBlock);
//		}
		
		setRx(getRx() + sensitivity * -Input.getMouseDeltaY());
		setRy(getRy() + sensitivity * -Input.getMouseDeltaX());
		
		if(getRx() > Math.PI / 2)
			setRx((float)Math.PI / 2);
		else if(getRx() < -Math.PI / 2)
			setRx((float)-Math.PI / 2);
	}

	
}
