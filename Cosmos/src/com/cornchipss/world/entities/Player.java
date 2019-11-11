package com.cornchipss.world.entities;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import com.cornchipss.physics.collision.hitbox.RectangleHitbox;
import com.cornchipss.physics.raycast.Raycast;
import com.cornchipss.physics.raycast.RaycastOptions;
import com.cornchipss.registry.Blocks;
import com.cornchipss.utils.Input;
import com.cornchipss.utils.Utils;
import com.cornchipss.utils.datatypes.Pair;
import com.cornchipss.world.Location;
import com.cornchipss.world.blocks.Block;
import com.cornchipss.world.blocks.BlockFace;

public class Player extends Entity
{
	private float sensitivity = 0.0025f;
	private float maxSlowdown = 1f;
	
	private int blockSelected;
	private Block[] blocks = new Block[] 
			{ Blocks.stone, Blocks.glass, Blocks.grass, Blocks.dirt, Blocks.sand, Blocks.snow, Blocks.sandstone, Blocks.snowstone };
	
	private final int LOOK_DISTANCE = 10;

	public Player(float x, float y, float z)
	{
		super(x, y, z, new RectangleHitbox(0.45f, 0.9f, 0.45f));
	}
	
	@Override
	public void onUpdate()
	{
		setVelocity(Utils.zero());

		if(!Input.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT))
		{
			float speed = Input.isKeyDown(GLFW.GLFW_KEY_LEFT_ALT) ? 2f : Input.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) ? .2f : .02f;
			
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

		for(int i = 0; i < blocks.length; i++)
		{
			if(Input.isKeyJustDown(GLFW.GLFW_KEY_1 + i))
			{
				blockSelected = i;
			}
		}
		
		if(Input.isMouseBtnJustDown(GLFW.GLFW_MOUSE_BUTTON_RIGHT))
		{
			Pair<Location, BlockFace> lookingAt = getBlockLookingAt(10);
			
			if(lookingAt.getA() != null && lookingAt.getB() != null)
			{
				BlockFace face = lookingAt.getB();
				Vector3f dir = face.getDirection();
				
				getUniverse().setBlockAt(Utils.add(lookingAt.getA().getPosition(), Utils.mul(2, dir)), blocks[blockSelected]);
			}
		}
		if(Input.isMouseBtnJustDown(GLFW.GLFW_MOUSE_BUTTON_LEFT))
		{
			Pair<Location, BlockFace> lookingAt = getBlockLookingAt(LOOK_DISTANCE);
			
			if(lookingAt.getA() != null && lookingAt.getB() != null)
			{
				getUniverse().setBlockAt(lookingAt.getA().getPosition(), Blocks.air);
			}
		}
		if(Input.isMouseBtnJustDown(GLFW.GLFW_MOUSE_BUTTON_MIDDLE))
		{
			Pair<Location, BlockFace> lookingAt = getBlockLookingAt(LOOK_DISTANCE * 10);
			
			if(lookingAt.getA() != null)
			{
				getUniverse().explode(lookingAt.getA(), 10);
			}
		}
	}
	
	public Pair<Location, BlockFace> getBlockLookingAt(float lookDist)
	{
		RaycastOptions settings = new RaycastOptions();
		
		settings.setBlacklist(Blocks.air);
		
		Raycast ray = Raycast.fire(getPosition(), getUniverse(), getRx(), getRy(), lookDist, settings);

		int closest = -1;
		float closestDist = 0;
		
		for(int i = 0; i < ray.size(); i++)
		{
			Location l = ray.getNthHit(i);
			
			float dist = l.getPosition().distanceSquared(getPosition());
			
			if(closest == -1 || dist < closestDist)
			{
				closest = i;
				closestDist = dist;
			}
		}
		
		if(closest != -1)
		{
			return new Pair<Location, BlockFace>(ray.getNthHit(closest), ray.getNthFace(closest));
		}
		else
			return new Pair<Location, BlockFace>();
	}
	
	public Vector3f getHeadPosition()
	{
		return Utils.add(getPosition(), new Vector3f(0, getHitbox().getBoundingBox().y(), 0));
	}
}
