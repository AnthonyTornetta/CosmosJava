package com.cornchipss.cosmos.world;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

public class ZaWARUDO
{
	private DynamicsWorld world;
	
	public ZaWARUDO()
	{
		BroadphaseInterface broadphase = new DbvtBroadphase();
		CollisionConfiguration cfg = new DefaultCollisionConfiguration();
		CollisionDispatcher dispatcher = new CollisionDispatcher(cfg);
		ConstraintSolver solver = new SequentialImpulseConstraintSolver();
		
		world = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, cfg);
		world.setGravity(new Vector3f());
	}
	
	public void addRigidBody(RigidBody bdy)
	{
		world.addRigidBody(bdy);
	}
	
	public RigidBodyConstructionInfo generateInfo(float mass, Transform transform, CollisionShape shape)
	{
		Vector3f localIntertia = new Vector3f();
		if(mass != 0)
			shape.calculateLocalInertia(mass, localIntertia);
		
		MotionState motionState = new DefaultMotionState(transform);
		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass, motionState, shape);
		
		return rbInfo;
	}
	
	public RigidBody createRigidBody(RigidBodyConstructionInfo rbInfo)
	{
		RigidBody bdy = new RigidBody(rbInfo);
		world.addRigidBody(bdy);
		return bdy;
	}
	
	public void update(float delta)
	{
		world.stepSimulation(delta);
	}
	
	public DynamicsWorld world() { return world; }
}
