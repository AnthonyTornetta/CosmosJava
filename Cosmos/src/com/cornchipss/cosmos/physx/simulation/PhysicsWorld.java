package com.cornchipss.cosmos.physx.simulation;

import java.util.LinkedList;
import java.util.List;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.extras.gimpact.GImpactCollisionAlgorithm;
import com.cornchipss.cosmos.memory.MemoryPool;
import com.cornchipss.cosmos.physx.PhysicalObject;
import com.cornchipss.cosmos.physx.collision.CollisionInfo;
import com.cornchipss.cosmos.physx.collision.DefaultCollisionChecker;
import com.cornchipss.cosmos.physx.collision.ICollisionChecker;
import com.cornchipss.cosmos.physx.collision.IHasCollisionEvent;
import com.cornchipss.cosmos.utils.Maths;

public class PhysicsWorld
{
	protected DynamicsWorld world;
	
	/**
	 * Sector dimensions in meters
	 */
	public static final int SECTOR_DIMENSIONS = 10_000;

	private List<PhysicalObject> bodies;

	private boolean locked = false;

	private List<PhysicalObject> bodiesToAdd;
	private List<PhysicalObject> bodiesToRemove;

	private ICollisionChecker strategy;

	private RigidBody ballRb;
	private RigidBody planeRb;

	public static final byte[] float2Byte(float[] inData)
	{
		int j = 0;
		int length = inData.length;
		byte[] outData = new byte[length * 4];
		for (int i = 0; i < length; i++)
		{
			int data = Float.floatToIntBits(inData[i]);
			outData[j++] = (byte) (data >>> 24);
			outData[j++] = (byte) (data >>> 16);
			outData[j++] = (byte) (data >>> 8);
			outData[j++] = (byte) (data >>> 0);
		}
		return outData;
	}
	
	public static final byte[] int2Byte(int[] inData)
	{
		int j = 0;
		int length = inData.length;
		byte[] outData = new byte[length * 4];
		for (int i = 0; i < length; i++)
		{
			outData[j++] = (byte) (inData[i] >>> 24);
			outData[j++] = (byte) (inData[i] >>> 16);
			outData[j++] = (byte) (inData[i] >>> 8);
			outData[j++] = (byte) (inData[i] >>> 0);
		}
		return outData;
	}

	public PhysicsWorld()
	{
		bodies = new LinkedList<>();
		bodiesToAdd = new LinkedList<>();
		bodiesToRemove = new LinkedList<>();

		strategy = new DefaultCollisionChecker();

		BroadphaseInterface broadphase = new DbvtBroadphase();

		CollisionConfiguration cfg = new DefaultCollisionConfiguration();

		CollisionDispatcher dispatcher = new CollisionDispatcher(cfg);
		GImpactCollisionAlgorithm.registerAlgorithm(dispatcher);
		
		ConstraintSolver solver = new SequentialImpulseConstraintSolver();

		world = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, cfg);
		world.setGravity(new javax.vecmath.Vector3f(0, 0, 0));

//		CollisionShape groundShape = new StaticPlaneShape(
//			new javax.vecmath.Vector3f(0, 1, 0), 0.25f);
//
//		RigidBodyConstructionInfo cinfo = new RigidBodyConstructionInfo(0,
//			new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1),
//				new javax.vecmath.Vector3f(0, 0, 0), 1.0f))),
//			groundShape);
//		cinfo.restitution = 0.25f;
//
//		planeRb = new RigidBody(cinfo);
//
//		world.addRigidBody(planeRb);
//		
//		IndexedMesh im = new IndexedMesh();
//		/*
//		 *
//		public int numTriangles;
//		public ByteBuffer triangleIndexBase;
//		public int triangleIndexStride;
//		public int numVertices;
//		public ByteBuffer vertexBase;
//		public int vertexStride;
//		 */
//		
//		int[] indices = new int[]
//			{
//				0, 1, 2, 2, 3, 0,
//				4, 5, 6, 6, 7, 4,
//				8, 9, 10, 10, 11, 8,
//				12, 13, 14, 14, 15, 12,
//				16, 17, 18, 18, 19, 16,
//				20, 21, 22, 22, 23, 20
//			};
//		
//		float[] vertices = new float[]
//			{
//				0.5f, -0.5f, 0.5f,
//				0.5f, -0.5f, 0.5f,
//				0.5f, 0.5f, 0.5f,
//				0.5f, 0.5f, 0.5f,
//				
//				0.5f, 0.5f, -0.5f,
//				0.5f, 0.5f, -0.5f,
//				0.5f, -0.5f, -0.5f,
//				0.5f, -0.5f, -0.5f,
//				
//				0.5f, -0.5f, -0.5f,
//				0.5f, 0.5f, -0.5f,
//				0.5f, 0.5f, 0.5f,
//				0.5f, -0.5f, 0.5f,
//				
//				0.5f, -0.5f, 0.5f,
//				0.5f, 0.5f, 0.5f,
//				0.5f, 0.5f, -0.5f,
//				0.5f, -0.5f, -0.5f,
//				
//				0.5f, 0.5f, -0.5f,
//				0.5f, 0.5f, -0.5f,
//				0.5f, 0.5f, 0.5f,
//				0.5f, 0.5f, 0.5f,
//				
//				0.5f, -0.5f, 0.5f,
//				0.5f, -0.5f, 0.5f,
//				0.5f, -0.5f, -0.5f,
//				0.5f, -0.5f, -0.5f
//			};
//		
//		int triCount = indices.length / 3;
//		
//		im.numTriangles = triCount;
//		im.triangleIndexBase = ByteBuffer.allocate(indices.length * 4);
//		
//		for (int i = 0; i < indices.length; i++) {
//	        im.triangleIndexBase.putInt(indices[i]);
//	    }
//		
//		im.triangleIndexBase.rewind();
//		
//		im.triangleIndexStride = 3 * Integer.BYTES;
//		
//		im.numVertices = vertices.length / 3;
//		im.vertexBase = ByteBuffer.allocate(vertices.length * 4);
//		
//		for (int i = 0; i < vertices.length; i++) {
//	        im.vertexBase.putFloat(vertices[i]);
//	    }
//		
//		im.vertexBase.rewind();
//
//		im.vertexStride = 3 * Float.BYTES; // OK
//		
//		TriangleIndexVertexArray smi = new TriangleIndexVertexArray();
//		smi.addIndexedMesh(im);
//		
//		GImpactMeshShape fancyShape = new GImpactMeshShape(smi);//new SphereShape(2.5f);
//		
//		fancyShape.setLocalScaling(new javax.vecmath.Vector3f(1, 1, 1));
//		fancyShape.updateBound();
//		fancyShape.setMargin(0);
//		
//		RigidBodyConstructionInfo cinfoball = new RigidBodyConstructionInfo(
//			10.0f, new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1),
//				new javax.vecmath.Vector3f(0, 10, 0), 1.0f))), fancyShape);
//
////		javax.vecmath.Vector3f inertia = new javax.vecmath.Vector3f();
////		fancyShape.calculateLocalInertia(10, inertia);
////		cinfoball.localInertia.set(inertia);
//
//		cinfoball.restitution = 0.25f;
//		cinfoball.angularDamping = 0.25f;
//		cinfoball.friction = 0.25f;
//
//		ballRb = new RigidBody(cinfoball);
//		world.addRigidBody(ballRb);
	}

	protected void addObjectDuringUnlock(PhysicalObject bdy)
	{
		bodies.add(bdy);
		
		world.addRigidBody(bdy.body().jbulletRigidBody());
	}

	protected void removeObjectDuringUnlock(PhysicalObject bdy)
	{
		bodies.remove(bdy);
	}

	public void addObject(PhysicalObject bdy)
	{
		if (!locked)
			addObjectDuringUnlock(bdy);
		else
			bodiesToAdd.add(bdy);
	}

	public void removeObject(PhysicalObject obj)
	{
		if (!locked)
			removeObjectDuringUnlock(obj);
		else
			bodiesToRemove.add(obj);
	}

	public void update(float delta)
	{
		world.stepSimulation(delta);
		
//		for (PhysicalObject a : bodies)
//		{			
//			Vector3f deltaA = a.body().velocity().mul(delta, new Vector3f());
//
//			if (deltaA.x != 0 || deltaA.y != 0 || deltaA.z != 0)
//			{
//				for (PhysicalObject b : bodies)
//				{
//					if (!b.equals(a) && b.shouldCollideWith(a)
//						&& a.shouldCollideWith(b))
//					{
//						handlePotentialCollision(a, b, deltaA);
//					}
//				}
//			}
//
//			a.body().velocity().mul(delta, deltaA);
//
//			a.body().position(a.body().position().add(deltaA, deltaA));
//
//			Vector3f deltaR = a.body().angularVelocity().mul(delta,
//				new Vector3f());
//
//			a.body().rotateRelative(deltaR);
//		}
	}

	private void handlePotentialCollision(PhysicalObject a, PhysicalObject b,
		Vector3fc deltaA)
	{
		CollisionInfo info;

		// TODO: make this work

		for (int i = 0; i < 1 && strategy.colliding(a, b, deltaA,
			info = MemoryPool.getInstanceOrCreate(CollisionInfo.class)); i++)
		{
			if (a instanceof IHasCollisionEvent)
			{
				if (!((IHasCollisionEvent) a).onCollide(b, info))
					return;
			}

			Vector3f mulBy = MemoryPool.getInstanceOrCreate(Vector3f.class);

			mulBy.x = Math.signum(a.body().velocity().x()) == Math
				.signum(info.normal.x) ? 1 : -1;
			mulBy.y = Math.signum(a.body().velocity().y()) == Math
				.signum(info.normal.y) ? 1 : -1;
			mulBy.z = Math.signum(a.body().velocity().z()) == Math
				.signum(info.normal.z) ? 1 : -1;

			info.normal.x = Math.abs(info.normal.x) * mulBy.x;
			info.normal.y = Math.abs(info.normal.y) * mulBy.y;
			info.normal.z = Math.abs(info.normal.z) * mulBy.z;

			info.normal.mul(0.5f);

			if (info.normal.x == 0)
				info.normal.x = 1;
			if (info.normal.y == 0)
				info.normal.y = 1;
			if (info.normal.z == 0)
				info.normal.z = 1;

//			Utils.println(info.normal);
//			Utils.println(a.body().velocity().y());
//			a.body().velocity(a.body().velocity().mul(info.normal, mulBy));
//			Utils.println("-> " + a.body().velocity().y());

			MemoryPool.addToPool(mulBy);
			MemoryPool.addToPool(info);
			a.body().velocity(Maths.zero());
		}
	}

	public boolean locked()
	{
		return locked;
	}

	public void lock()
	{
		locked = true;
	}

	public void unlock()
	{
		locked = false;

		while (bodiesToAdd.size() != 0)
		{
			if (!bodiesToRemove.remove(bodiesToAdd.get(0)))
			{
				addObjectDuringUnlock(bodiesToAdd.remove(0));
			}
			else
			{
				bodiesToAdd.remove(0);
			}
		}

		while (bodiesToRemove.size() != 0)
			removeObjectDuringUnlock(bodiesToRemove.remove(0));
	}
}
