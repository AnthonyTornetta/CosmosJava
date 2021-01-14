package test;

import java.util.Random;

import javax.vecmath.Vector3f;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CapsuleShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.IDebugDraw;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import com.cornchipss.rendering.Texture;
import com.cornchipss.rendering.Window;
import com.cornchipss.utils.Input;
import com.cornchipss.utils.Utils;

import test.blocks.Blocks;
import test.shaders.Shader;
import test.utils.Logger;

public class Main
{
	private Window window;
	
	public static void main(String[] args)
	{
		new Main().run();
	}
	
	private void run()
	{
		Logger.LOGGER.setLevel(Logger.LogLevel.DEBUG);
		
		window = new Window(1024, 720, "wack simulator 2021");
		
		Shader defaultShader = new Shader("assets/shaders/chunk");
		defaultShader.init();
		
		BroadphaseInterface broadphase = new DbvtBroadphase();
		CollisionConfiguration cfg = new DefaultCollisionConfiguration();
		CollisionDispatcher dispatcher = new CollisionDispatcher(cfg);
		ConstraintSolver solver = new SequentialImpulseConstraintSolver();
		
		DynamicsWorld world = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, cfg);
		world.setGravity(new Vector3f());
		
		final int structW = 16 * 3,
				structH = 16,
				structL = 16 * 3;
		
		CollisionShape structShape = new BoxShape(new Vector3f(structW / 2.0f, structH / 2.0f, structL / 2.0f));
		Transform structTransform = new Transform();
		
		RigidBody structBody;
		
		{
			float mass = 10000000f;

			// rigidbody is dynamic if and only if mass is non zero, otherwise static
			boolean isDynamic = (mass != 0f);

			Vector3f localInertia = new Vector3f(0, 0, 0);
			if (isDynamic)
				structShape.calculateLocalInertia(mass, localInertia);
			
			// using motionstate is recommended, it provides interpolation capabilities, and only synchronizes 'active' objects
			DefaultMotionState myMotionState = new DefaultMotionState(structTransform);
			RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass, myMotionState, structShape, localInertia);
			rbInfo.friction = 15.0f;
			rbInfo.restitution = 0.0f;
			structBody = new RigidBody(rbInfo);
//			structBody.setCollisionFlags(structBody.getCollisionFlags() | CollisionFlags.STATIC_OBJECT);
			
			// add the body to the dynamics world
			world.addRigidBody(structBody);
		}
		Utils.println(structBody.getCenterOfMassPosition(new Vector3f()));
		
		Transform playerTransform = new Transform();
		playerTransform.origin.set(0, structH / 2.0f + 2, 0);
		RigidBody playerBody;
		
		CollisionShape playerShape = new CapsuleShape(0.4f, 0.9f);
		
		{
			float mass = 50.0f;
			
			Vector3f localIntertia = new Vector3f();
			playerShape.calculateLocalInertia(mass, localIntertia);
			
			MotionState motionState = new DefaultMotionState(playerTransform);
			RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass, motionState, playerShape);
			
			rbInfo.restitution = 0.0f;
			playerBody = new RigidBody(rbInfo);
			
//			playerBody.setCollisionFlags(playerBody.getCollisionFlags());
			world.addRigidBody(playerBody);
		}
		
		Ployer p = new Ployer(playerBody);
		
		Structure s = new Structure(structBody, structW, structH, structL);
		
		Random rdm = new Random();
		
		for(int z = 0; z < s.length(); z++)
		{
			for(int x = 0; x < s.width(); x++)
			{
				int h = s.height();// - 5;//s.height() -4;//- rdm.nextInt(8) - 4;
				for(int y = 0; y < h; y++)
				{
					if(y == h - 1)
						s.block(x, y, z, rdm.nextFloat() < 0.01f ? Blocks.LIGHT : Blocks.GRASS);
					else if(h - y < 5)
						s.block(x, y, z, Blocks.DIRT);
					else
						s.block(x, y, z, Blocks.STONE);
				}
			}
		}
		
		s.calculateLights(false);
		
		for(Chunk c : s.chunks())
			c.render();
		
		int timeLoc = defaultShader.uniformLocation("time");
		int camLoc = defaultShader.uniformLocation("u_camera");
		int transLoc = defaultShader.uniformLocation("u_transform");
		int projLoc = defaultShader.uniformLocation("u_proj");
		
		Matrix4f projectionMatrix = new Matrix4f();
		projectionMatrix.perspective((float)Math.toRadians(90), 
				1024/720.0f,
				0.1f, 1000);
		
		Texture tex = Texture.loadTexture("atlas/main.png");
				
		Input.setWindow(window);
		
		long t = System.currentTimeMillis();
		
		final int UPS_TARGET = 70;
		
		final int MILLIS_WAIT = 1000 / UPS_TARGET;
		
		long lastSecond = t;
		
		int ups = 0;
		float variance = 0;
		
		Input.hideCursor(true);
		
		Input.update();
		
		boolean running = true;
		
		while(!window.shouldClose() && running)
		{
			float delta = System.currentTimeMillis() - t;
			
			if(delta < MILLIS_WAIT)
			{
				try
				{
					Thread.sleep(MILLIS_WAIT - (int)delta);
					
					delta = (System.currentTimeMillis() - t);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			
			delta /= 1000.0f;
			
			if(delta > variance)
				variance = delta;
			
			t = System.currentTimeMillis();
			
			if(lastSecond / 1000 != t / 1000)
			{
				Logger.LOGGER.info("UPS: " + ups + "; Max Variance: " + variance + "ms");
				lastSecond = t;
				ups = 0;
				variance = 0;
			}
			ups++;
			
			if(Input.isKeyJustDown(GLFW.GLFW_KEY_F1))
				Input.toggleCursor();
			
			if(Input.isKeyDown(GLFW.GLFW_KEY_ESCAPE))
				running = false;
			
//			world.update(delta);
			
			p.body().setActivationState(RigidBody.ACTIVE_TAG);
			
			world.stepSimulation(delta);
			world.debugDrawWorld();
			
//			p.body().getTransform().setOrientation(new Quaternion().identity());
			
			p.update(delta);
			
			GL11.glEnable(GL13.GL_TEXTURE0);
			
			render(delta);
			
			Input.update();

			defaultShader.use();
			defaultShader.setUniformF(timeLoc, (float)GLFW.glfwGetTime());
			tex.bind();
			
			defaultShader.setUniformMatrix(projLoc, projectionMatrix);
			
			defaultShader.setUniformMatrix(camLoc, p.camera().viewMatrix());
			
			GL30.glEnable(GL30.GL_DEPTH_TEST);
			GL30.glDepthFunc(GL30.GL_LESS);
			
			for(Chunk chunk : s.chunks())
			{
				Matrix4f transform = new Matrix4f();
				Matrix4fc trans = s.transformMatrix();
				trans.mul(chunk.transformMatrix(), transform);
				
				defaultShader.setUniformMatrix(transLoc, transform);
				
				chunk.mesh().prepare();
				chunk.mesh().draw();
				chunk.mesh().finish();
			}
			
			Texture.unbind();
			
			defaultShader.stop();
			
			window.update();
		}
		
//		world.stop();
		
		window.destroy();
		
		Logger.LOGGER.info("Successfully closed.");
	}
	
	private void render(float delta)
	{
		window.clear(33 / 255.0f, 33 / 255.0f, 33 / 255.0f, 1.0f);
		
		// Render Code
	}
}
