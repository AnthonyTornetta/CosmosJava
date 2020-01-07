package com.cornchipss.world.entities;

import java.util.List;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import com.cornchipss.physics.Axis;
import com.cornchipss.physics.Transform;
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
	private float maxSlowdown = .1f;
	private float maxSpeed = 2f;
	private float maxSpeedY = 5f;
	
	public static final float FRICTION = 1f;
	
	public static final float GRAVITY_ACCEL = 0;//-0.01f;
	
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
		maxSlowdown = .1f;
		maxSpeed = 100f;
		maxSpeedY = 100f;
		
		float speed = 50 * (Input.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) ? .025f : .015f);
//		float ySpeed = 0.015f * 50;
		
		float velX = 0, velY = 0, velZ = 0;
				
		Axis axis = new Axis(getAbsoluteRotation());
		
		Vector3f newVecX = axis.vectorInDirection(new Vector3f(speed, 0, 0));
		Vector3f newVecY = axis.vectorInDirection(new Vector3f(0, speed, 0));
		Vector3f newVecZ = axis.vectorInDirection(new Vector3f(0, 0, speed));
		
		if(Input.isKeyDown(GLFW.GLFW_KEY_W))
		{
			velX -= newVecZ.x;
			velY -= newVecZ.y;
			velZ -= newVecZ.z;
		}
		if(Input.isKeyDown(GLFW.GLFW_KEY_S))
		{
			velX += newVecZ.x;
			velY += newVecZ.y;
			velZ += newVecZ.z;
		}
		if(Input.isKeyDown(GLFW.GLFW_KEY_A))
		{
			velX -= newVecX.x;
			velY -= newVecX.y;
			velZ -= newVecX.z;
		}
		if(Input.isKeyDown(GLFW.GLFW_KEY_D))
		{
			velX += newVecX.x;
			velY += newVecX.y;
			velZ += newVecX.z;
		}
		if(Input.isKeyDown(GLFW.GLFW_KEY_Q))
		{
			velX -= newVecY.x;
			velY -= newVecY.y;
			velZ -= newVecY.z;
		}
		if(Input.isKeyDown(GLFW.GLFW_KEY_E))
		{
			velX += newVecY.x;
			velY += newVecY.y;
			velZ += newVecY.z;
		}
		
		// normalizes the speed so you dont get a giga boost if you go in 3 directions at once
		// I didn't use the normalize() method because it divides by 0 if velocity is 0, 0, 0
		
		float k = (velX * velX + velY * velY + velZ * velZ) / (speed * speed);
		if(k != 0)
		{
			velX /= k;
			velY /= k;
			velZ /= k;
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
		
		if(Input.isKeyDown(GLFW.GLFW_KEY_V))
			getTransform().resetRotation();
		if(Input.isKeyDown(GLFW.GLFW_KEY_R))
		{			
			setX(0);
			setY(0);
			setZ(0);
			getTransform().resetRotation();
			setVelocity(Maths.zero());
		}
		if(Input.isKeyJustDown(GLFW.GLFW_KEY_SPACE))
		{
			if(hasParent())
				removeParent();
			else
				setParent(getUniverse().getPlanet(getAbsolutePosition()));
		}
		
		getTransform().rotateX(sensitivity * -Input.getMouseDeltaY());
		getTransform().rotateY(sensitivity * -Input.getMouseDeltaX());
		
		if(Input.isKeyDown(GLFW.GLFW_KEY_C))
		{
			getTransform().rotateZ(-Maths.PI / 180f);
		}
		if(Input.isKeyDown(GLFW.GLFW_KEY_Z))
		{
			getTransform().rotateZ(Maths.PI / 180f);
		}
		
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
		
		accelerateY(GRAVITY_ACCEL);
		
		if(Input.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT))
		{
			accelerateX(Utils.clamp(-getVelocityX(), -maxSlowdown, maxSlowdown) * 0.1f);
			accelerateY(Utils.clamp(-(getVelocityY() - GRAVITY_ACCEL), -maxSlowdown, maxSlowdown) * 0.1f - GRAVITY_ACCEL);
			accelerateZ(Utils.clamp(-getVelocityZ(), -maxSlowdown, maxSlowdown) * 0.1f);
		}
		
		List<BlockFace> hits = updatePhysics();
		
		accelerateX(Utils.clamp(-getVelocityX(), -maxSlowdown, maxSlowdown) * (maxSpeed / FRICTION));
		accelerateZ(Utils.clamp(-getVelocityZ(), -maxSlowdown, maxSlowdown) * (maxSpeed / FRICTION));
		
		if(Utils.contains(hits, BlockFace.TOP))
		{
			if(Input.isKeyDown(GLFW.GLFW_KEY_SPACE)) // hits.size() == 1 prevents wall jumping
			{
				accelerateY(.2f);
			}
		}
	}
	
	public Pair<Location, BlockFace> getBlockLookingAt(float lookDist)
	{
		RaycastOptions settings = new RaycastOptions();
		
		settings.setBlacklist(Blocks.air);
		
		Transform absTransform = getAbsoluteTransform();
		
		Raycast ray = Raycast.fire(absTransform.getPosition(), getUniverse(), absTransform.getRotation(), lookDist, settings);
		
		int closest = -1;
		float closestDist = 0;
		
		for(int i = 0; i < ray.size(); i++)
		{
			Location l = ray.getNthHit(i);
			
			float dist = l.getPosition().distanceSquared(absTransform.getPosition());
			
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
	
	/**
	 * returns the relative head position of the player
	 * @return the relative head position of the player
	 */
	public Vector3f getHeadPosition()
	{
		return Maths.add(getPosition(), new Vector3f(0, getHitbox().getBoundingBox().y(), 0));
	}

	@Override
	public float getMass()
	{
		return 80;
	}
}
