package com.cornchipss.world.entities;

import java.util.List;

import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.glfw.GLFW;

import com.cornchipss.Cosmos;
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
	
	// All in KMS units
	private float maxSpeedXZ = 5f;
//	private float maxSpeedY = 10;
	private float accelRate = 100f;
	
	public static final float FRICTION = 0;
	
	public static final float GRAVITY_ACCEL = 0;//-9.8f;
	
	private int blockSelected;
	private Block[] inventory = new Block[] 
			{ Blocks.stone, Blocks.glass, Blocks.grass, Blocks.dirt, Blocks.sand, Blocks.snow, Blocks.sandstone, Blocks.snowstone };
	
	private final int LOOK_DISTANCE = 10;
	
	private Transform camera;
	private float rx = 0, ry = 0;
	
	public Player(float x, float y, float z)
	{
		super(x, y, z, new RectangleHitbox(0.45f, 0.9f, 0.45f), 0.5f);
		
		// TODO: figure out why it works like this
//		setTransform(new Transform(new Vector3f(x, y, z))
//				{
//					@Override
//					public Quaternionfc rotation()
//					{
//						if(hasParent())
//						{
//							Quaternionfc parentRot = parent().rotation();
//							return localRotation().mul(parentRot.invert(new Quaternionf()), new Quaternionf());
//						}
//						
//						return localRotation();
//					}
//				});
//		
		camera = new Transform(transform());
		
//		camera = transform();
	}
	
	private void handleNewMovement()
	{
		Axis axis = camera().axis();
		
		Vector3f newVecX = axis.vectorInDirection(new Vector3f(1, 0, 0));
		Vector3f newVecY = axis.vectorInDirection(new Vector3f(0, 1, 0));
		Vector3f newVecZ = axis.vectorInDirection(new Vector3f(0, 0, 1));
		
		Vector3f accel = new Vector3f();
		
		if(Input.isKeyDown(GLFW.GLFW_KEY_W))
		{
			accel.x -= newVecZ.x;
			accel.y -= newVecZ.y;
			accel.z -= newVecZ.z;
		}
		if(Input.isKeyDown(GLFW.GLFW_KEY_S))
		{
			accel.x += newVecZ.x;
			accel.y += newVecZ.y;
			accel.z += newVecZ.z;
		}
		if(Input.isKeyDown(GLFW.GLFW_KEY_A))
		{
			accel.x -= newVecX.x;
			accel.y -= newVecX.y;
			accel.z -= newVecX.z;
		}
		if(Input.isKeyDown(GLFW.GLFW_KEY_D))
		{
			accel.x += newVecX.x;
			accel.y += newVecX.y;
			accel.z += newVecX.z;
		}
		if(Input.isKeyDown(GLFW.GLFW_KEY_Q))
		{
			accel.x -= newVecY.x;
			accel.y -= newVecY.y;
			accel.z -= newVecY.z;
		}
		if(Input.isKeyDown(GLFW.GLFW_KEY_E))
		{
			accel.x += newVecY.x;
			accel.y += newVecY.y;
			accel.z += newVecY.z;
		}
		
		if(Input.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT))
		{
			Vector3fc vel = transform().velocity();
			accel.x -= vel.x() * .1f;
			accel.y -= vel.y() * .1f;
			accel.z -= vel.z() * .1f;
		}
		
		accel.mul(accelRate * Cosmos.deltaTime());
		
		transform().accelerate(accel);
//		getTransform().localVelocity(Maths.normalClamp(getTransform().localVelocity(), maxSpeedXZ));
	}
	
	private void handleResets()
	{
		if(Input.isKeyDown(GLFW.GLFW_KEY_V))
		{
			camera().localRotation(0, 0, 0);
			rx = 0;
			ry = 0;
		}
		if(Input.isKeyDown(GLFW.GLFW_KEY_R))
		{
			transform().localPosition(new Vector3f(0, 0, 0));
			transform().localRotation(0, 0, 0);
			camera().localRotation(0, 0, 0);
			//getTransform().removeParent();
			transform().localVelocity(Maths.zero());
		}
		if(Input.isKeyJustDown(GLFW.GLFW_KEY_SPACE))
		{
			if(transform().hasParent())
				transform().removeParent();
			else
				transform().parent(
						getUniverse().getPlanet(
								transform().position()).transform());
				//transform().parent(getUniverse().getSector(0, 0, 0).sa().transform());
		}
	}
	
	private void handleCamera()
	{
		rx += -Input.getMouseDeltaY() * sensitivity;
		ry += -Input.getMouseDeltaX() * sensitivity;
		
		camera.localRotation(new Vector3f(rx, ry, 0));		
		/*Axis axis = camera().axis();
		
		float rz = Input.isKeyDown(GLFW.GLFW_KEY_Z) ? 1 : 0;
		rz -= Input.isKeyDown(GLFW.GLFW_KEY_C) ? 1 : 0;
		rz *= 0.01f;
		
		camera().rotate(new AxisAngle4f(rz, axis.zEndpoint()));
		
		float ry = -Input.getMouseDeltaX() * sensitivity;
		camera().rotate(new AxisAngle4f(ry, axis.yEndpoint()));
		
		float rx = -Input.getMouseDeltaY() * sensitivity;
		camera().rotate(new AxisAngle4f(rx, axis.xEndpoint()));*/
//		Utils.println(axis);
//		
//		Quaternionf quat = Maths.clone(camera.rotation());
//		
//		new Quaternionf(new AxisAngle4f(-Input.getMouseDeltaX() * sensitivity, axis.yEndpoint())).mul(quat, quat);
//		quat.normalize();
//		new Quaternionf(new AxisAngle4f(-Input.getMouseDeltaY() * sensitivity, axis.xEndpoint())).mul(quat, quat);
//		quat.normalize();
//		
//		float rotZ = 0;
//		if(Input.isKeyDown(GLFW.GLFW_KEY_Z))
//		{
//			rotZ -= 1;
//		}
//		if(Input.isKeyDown(GLFW.GLFW_KEY_C))
//		{
//			rotZ += 1;
//		}
//		
//		new Quaternionf(new AxisAngle4f(rotZ * 0.01f, axis.zEndpoint())).mul(quat, quat);
//		quat.normalize();
//		
//		for(int i = 0; i < inventory.length; i++)
//		{
//			if(Input.isKeyJustDown(GLFW.GLFW_KEY_1 + i))
//			{
//				blockSelected = i;
//			}
//		}
//		
//		quat.normalize();
//		
//		camera.rotation(quat);
	}
	
	private void handleBlockInteractions()
	{
		if(Input.isMouseBtnJustDown(GLFW.GLFW_MOUSE_BUTTON_RIGHT))
		{
			Pair<Location, BlockFace> lookingAt = getBlockLookingAt(10);
			
			if(lookingAt.getA() != null && lookingAt.getB() != null)
			{
				BlockFace face = lookingAt.getB();
				Vector3f dir = face.getRelativePosition();
				
				getUniverse().setBlockAt(Maths.add(lookingAt.getA().getPosition(), Maths.mul(2, dir)), inventory[blockSelected]);
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
	
	private void handleOtherForces()
	{
		List<BlockFace> hits = updatePhysics();
		
		if(Utils.contains(hits, BlockFace.TOP))
		{
			if(Input.isKeyDown(GLFW.GLFW_KEY_SPACE)) // hits.size() == 1 prevents wall jumping
			{
				transform().accelerate(new Vector3f(0, 10, 0));
			}
		}
	}
	
	@Override
	public void onUpdate()
	{
		handleCamera();
		
		handleNewMovement();
		
		handleResets();
				
		handleBlockInteractions();
		
		handleOtherForces();
	}
	
	public Pair<Location, BlockFace> getBlockLookingAt(float lookDist)
	{
		RaycastOptions settings = new RaycastOptions();
		
		settings.setBlacklist(Blocks.air);
		
		Raycast ray = Raycast.fire(transform().position(), getUniverse(), transform().eulers(), lookDist, settings);
		
		int closest = -1;
		float closestDist = 0;
		
		for(int i = 0; i < ray.size(); i++)
		{
			Location l = ray.getNthHit(i);
			
			float dist = l.getPosition().distanceSquared(transform().position());
			
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
		return Maths.add(transform().position(), new Vector3f(0, getHitbox().getBoundingBox().y(), 0));
	}

	@Override
	public float getMass()
	{
		return 80;
	}
	
	public Transform camera()
	{
		return camera;
	}
}
