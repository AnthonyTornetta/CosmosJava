package com.cornchipss.entities;

import java.util.List;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import com.cornchipss.physics.collision.hitbox.RectangleHitbox;
import com.cornchipss.physics.raycast.Raycast;
import com.cornchipss.physics.raycast.RaycastOptions;
import com.cornchipss.registry.Blocks;
import com.cornchipss.utils.Input;
import com.cornchipss.utils.Maths;
import com.cornchipss.utils.Utils;
import com.cornchipss.utils.datatypes.Pair;
import com.cornchipss.world.Location;
import com.cornchipss.world.blocks.Block;
import com.cornchipss.world.blocks.BlockFace;

public class Player extends PhysicalEntity
{
	private float sensitivity = 0.0025f;
	private float maxSlowdown = 1f;
	private float maxSpeed = 0.25f;
	private float maxSpeedY = 0.5f;
	
	public static final float FRICTION = .1f;
	
	private int blockSelected;
	private Block[] blocks = new Block[] 
			{ Blocks.stone, Blocks.glass, Blocks.grass, Blocks.dirt, Blocks.sand, Blocks.snow, Blocks.sandstone, Blocks.snowstone };
	
	private final int LOOK_DISTANCE = 10;

	public Player(float x, float y, float z)
	{
		super(x, y, z, new RectangleHitbox(0.45f, 0.9f, 0.45f), 0.5f);
	}
	
	@Override
	public void onUpdate()
	{
		if(!Input.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT))
		{
			float speed = Input.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) ? .015f : .007f;
			
			float velX = 0, velY = 0, velZ = 0;
			
			if(Input.isKeyDown(GLFW.GLFW_KEY_W))
			{
				velX = (float) (speed * Math.sin(getRy()));
				velZ = (float) (-speed * Math.cos(getRy()));
			}
			if(Input.isKeyDown(GLFW.GLFW_KEY_S))
			{
				velX = (float) (-speed * Math.sin(getRy()));
				velZ = (float) (speed * Math.cos(getRy()));
			}
			if(Input.isKeyDown(GLFW.GLFW_KEY_A))
			{
				velX = (float) (-speed * Math.cos(getRy()));
				velZ = (float) (-speed * Math.sin(getRy()));
			}
			if(Input.isKeyDown(GLFW.GLFW_KEY_D))
			{
				velX = (float) (speed * Math.cos(getRy()));
				velZ = (float) (speed * Math.sin(getRy()));
			}
			
			if(Input.isKeyDown(GLFW.GLFW_KEY_E))
			{
				velY = speed;
			}
			if(Input.isKeyDown(GLFW.GLFW_KEY_Q))
			{
				velY = -speed;
			}
			
			if(Math.abs(getVelocityX() + velX) < Math.abs(getVelocityX()) || Math.abs(getVelocityX()) <= maxSpeed)
			{
				getVelocity().x = Utils.clamp(getVelocityX() + velX, -maxSpeed, maxSpeed);
			}
			if(Math.abs(getVelocityY() + velY) < Math.abs(getVelocityY()) || Math.abs(getVelocityY()) <= maxSpeedY)
			{
				getVelocity().y = Utils.clamp(getVelocityY() + velY, -maxSpeedY, maxSpeedY);
			}
			if(Math.abs(getVelocityZ() + velZ) < Math.abs(getVelocityZ()) || Math.abs(getVelocityZ()) <= maxSpeed)
			{
				getVelocity().z = Utils.clamp(getVelocityZ() + velZ, -maxSpeed, maxSpeed);
			}
			
			if(Input.isKeyDown(GLFW.GLFW_KEY_R))
			{
				setX(0);
				setY(128);
				setZ(0);
				setRx(0);
				setRy(0);
				setVelocity(Maths.zero());
			}
		}
		else
		{
			addVelocityX(Utils.clamp(-getVelocityX(), -maxSlowdown, maxSlowdown) * 0.1f);
			addVelocityY(Utils.clamp(-getVelocityY(), -maxSlowdown, maxSlowdown) * 0.1f);
			addVelocityZ(Utils.clamp(-getVelocityZ(), -maxSlowdown, maxSlowdown) * 0.1f);
		}
		
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
				Vector3f dir = face.getRelativePosition();
				
				getUniverse().setBlockAt(Maths.add(lookingAt.getA().getPosition(), Maths.mul(2, dir)), blocks[blockSelected]);
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
		
		addVelocityY(-.01f);
		
		List<BlockFace> hits = updatePhysics();
		
		if(Utils.contains(hits, BlockFace.TOP))
		{
			addVelocityX(Utils.clamp(-getVelocityX(), -maxSlowdown, maxSlowdown) * FRICTION);
			addVelocityZ(Utils.clamp(-getVelocityZ(), -maxSlowdown, maxSlowdown) * FRICTION);
			
			if(hits.size() == 1 && Input.isKeyDown(GLFW.GLFW_KEY_SPACE)) // hits.size() == 1 prevents wall jumping
			{
				addVelocityY(.2f);
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
		return Maths.add(getPosition(), new Vector3f(0, getHitbox().getBoundingBox().y(), 0));
	}
}
