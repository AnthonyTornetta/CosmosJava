package com.cornchipss.cosmos.physx.simulation;

import java.nio.ByteBuffer;
import java.util.Set;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.IndexedMesh;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.collision.shapes.TriangleIndexVertexArray;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.cornchipss.cosmos.blocks.BlockFace;
import com.cornchipss.cosmos.models.blocks.GrassModel;
import com.cornchipss.cosmos.utils.Utils;

public class Test
{
	private RigidBody ballRb;
	private RigidBody planeRb;
	
	private DynamicsWorld world;
	
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
	
	public static void main(String[] args)
	{
		new Test().run();
	}
	
	public void run()
	{
		BroadphaseInterface broadphase = new DbvtBroadphase();

		CollisionConfiguration cfg = new DefaultCollisionConfiguration();

		CollisionDispatcher dispatcher = new CollisionDispatcher(cfg);

		ConstraintSolver solver = new SequentialImpulseConstraintSolver();

		world = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, cfg);
		world.setGravity(new javax.vecmath.Vector3f(0, -9.81f, 0));

		CollisionShape groundShape = new StaticPlaneShape(
			new javax.vecmath.Vector3f(0, 1, 0), 0.25f);

		RigidBodyConstructionInfo cinfo = new RigidBodyConstructionInfo(0,
			new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1),
				new javax.vecmath.Vector3f(0, 0, 0), 1.0f))),
			groundShape);
		cinfo.restitution = 0.25f;

		planeRb = new RigidBody(cinfo);

		world.addRigidBody(planeRb);
		
		IndexedMesh im = new IndexedMesh();
		/*
		 *
		public int numTriangles;
		public ByteBuffer triangleIndexBase;
		public int triangleIndexStride;
		public int numVertices;
		public ByteBuffer vertexBase;
		public int vertexStride;
		 */
		
		GrassModel gm = new GrassModel();
		int[] indices = gm.indicies(BlockFace.FRONT);
		float[] vertices = gm.vertices(BlockFace.FRONT, 0, 0, 0);
		
		im.numTriangles = indices.length / 3;
		im.triangleIndexBase = ByteBuffer.wrap(int2Byte(indices));
		im.triangleIndexStride = 0;
		
		im.numVertices = vertices.length;
		im.vertexBase = ByteBuffer.wrap(float2Byte(vertices));
		im.vertexStride = 0;
		
		TriangleIndexVertexArray smi = new TriangleIndexVertexArray();
		smi.addIndexedMesh(im);
		
		CollisionShape sphereShape = new BvhTriangleMeshShape(smi, true);//new SphereShape(2.5f);

		RigidBodyConstructionInfo cinfoball = new RigidBodyConstructionInfo(
			10.0f, new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1),
				new javax.vecmath.Vector3f(0, 10, 0), 1.0f))), sphereShape);

		cinfoball.localInertia.set(new javax.vecmath.Vector3f(1, 1, 1));

		cinfoball.restitution = 0.25f;
		cinfoball.angularDamping = 0.25f;
		cinfoball.friction = 0.25f;

		ballRb = new RigidBody(cinfoball);
		world.addRigidBody(ballRb);
		
		while(true)
		{
			world.stepSimulation(1.0f / 60.0f);
			
			Transform out = new Transform();
			Utils.println("BALL : " + ballRb.getWorldTransform(out).origin);

			Utils.println("PLANE: " + planeRb.getWorldTransform(out).origin);
		}
	}
}
