package test;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3i;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import com.bulletphysics.linearmath.Transform;
import com.cornchipss.rendering.Texture;
import com.cornchipss.rendering.Window;
import com.cornchipss.utils.Input;
import com.cornchipss.utils.Maths;
import com.cornchipss.world.blocks.BlockFace;

import test.blocks.Blocks;
import test.gui.GUIElement;
import test.physx.RayResult;
import test.shaders.Shader;
import test.utils.Logger;
import test.world.ZaWARUDO;

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
		
		Shader guiShader = new Shader("assets/shaders/gui");
		guiShader.init();
		
		ZaWARUDO world = new ZaWARUDO();
		
		final int structW = 16*20,
				structH = 256,
				structL = 16*20;
		
		GUIElement crosshair = new GUIElement(new Vec3(), 0.1f, 0.1f, 0, 0);
		
		Structure s = new Structure(world, structW, structH, structL);
		
		for(int z = 0; z < s.length(); z++)
		{
			for(int x = 0; x < s.width(); x++)
			{
				int h = s.height() - 16;
				for(int y = 0; y < h; y++)
				{
//					if(Math.random() < 0.01)
//						s.block(x, y, z, Blocks.LIGHT);
					if(y == h - 1)
						s.block(x, y, z, Blocks.GRASS);
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
		
		Transform sT = new Transform();
		sT.setIdentity();
		sT.origin.set(0, 0, 0);
		
		s.addToWorld(sT);
		
		Ployer p = new Ployer(world);
		Transform playerTransform = new Transform();
		playerTransform.origin.set(0, s.height() / 2 + 3, 0);
		p.addToWorld(playerTransform);

		
		int timeLoc = defaultShader.uniformLocation("time");
		int camLoc = defaultShader.uniformLocation("u_camera");
		int transLoc = defaultShader.uniformLocation("u_transform");
		int projLoc = defaultShader.uniformLocation("u_proj");
		
		int guiTransLoc = guiShader.uniformLocation("u_transform");
		int guiProjLoc = guiShader.uniformLocation("u_xd");
		
		Matrix4f projectionMatrix = new Matrix4f();
		projectionMatrix.perspective((float)Math.toRadians(90), 
				1024/720.0f,
				0.1f, 1000);
		
		Texture tex = Texture.loadTexture("atlas/main.png");
		
		Texture guiTex = Texture.loadTexture("atlas/gui.png");
		
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
				Logger.LOGGER.info("UPS: " + ups + "; Max Variance: " + variance*1000 + "ms");
				lastSecond = t;
				ups = 0;
				variance = 0;
			}
			ups++;
			
			if(Input.isKeyJustDown(GLFW.GLFW_KEY_F1))
				Input.toggleCursor();
			
			if(Input.isKeyDown(GLFW.GLFW_KEY_ESCAPE))
				running = false;
			
			world.update(delta);
			
			p.update(delta);
			
			if(Input.isMouseBtnJustDown(GLFW.GLFW_MOUSE_BUTTON_1) || Input.isMouseBtnJustDown(GLFW.GLFW_MOUSE_BUTTON_2) || Input.isMouseBtnJustDown(GLFW.GLFW_MOUSE_BUTTON_3))
			{
				Vec3 from = p.camera().position();
				Vec3 dLook = Maths.mul(p.camera().forward(), 50.f);
				Vec3 to = Maths.add(from, dLook);
				
				RayResult hits = s.shape().raycast(from.joml(), to.joml());
				
				if(hits.closestHit() != null)
				{
					Vector3i pos = new Vector3i(Maths.round(hits.closestHit().x()), 
							Maths.round(hits.closestHit().y()), 
							Maths.round(hits.closestHit().z()));
					
					if(Input.isMouseBtnJustDown(GLFW.GLFW_MOUSE_BUTTON_1))
					{
						s.block(pos.x, pos.y, pos.z, null);
					}
					else if(Input.isMouseBtnJustDown(GLFW.GLFW_MOUSE_BUTTON_2))
					{
						BlockFace face = hits.closestFace();
						
						int xx = Maths.floor(pos.x + 0.5f + (face.getRelativePosition().x * 2)), 
							yy = Maths.floor(pos.y + 0.5f + (face.getRelativePosition().y * 2)), 
							zz = Maths.floor(pos.z + 0.5f + (face.getRelativePosition().z * 2));
						
						if(s.withinBlocks(xx, yy, zz))
						{
							s.block(xx, yy, zz, Blocks.LIGHT);
						}
					}
					else if(Input.isMouseBtnJustDown(GLFW.GLFW_MOUSE_BUTTON_3))
					{
						s.beginBulkUpdate();
						
						final int radius = 20;
						
						Vec3 temp = new Vec3();
						Vec3 tempPos = new Vec3(pos.x, pos.y, pos.z);
						
						for(int dz = -radius; dz <= radius; dz++)
						{
							for(int dy = -radius; dy <= radius; dy++)
							{
								for(int dx = -radius; dx <= radius; dx++)
								{									
									int xx = pos.x + dx,
										yy = pos.y + dy, 
										zz = pos.z + dz;
									
									temp.x(xx);
									temp.y(yy);
									temp.z(zz);

									if(Maths.distSqrd(temp, tempPos) < radius * radius)
									{
										
										if(s.withinBlocks(xx, yy, zz))
										{
											s.block(xx, yy, zz, null);
										}
									}
								}
							}
						}
						
						s.endBulkUpdate();
					}
				}
			}
			
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
			
			guiShader.use();
			GL30.glDisable(GL30.GL_DEPTH_TEST);

			guiTex.bind();
			
			guiShader.setUniformMatrix(guiTransLoc, crosshair.transform());
			guiShader.setUniformMatrix(guiProjLoc, projectionMatrix);
			
			crosshair.guiMesh().prepare();
			crosshair.guiMesh().draw();
			crosshair.guiMesh().finish();
//			// GUI shader code here
//			
			Texture.unbind();
			
			guiShader.stop();
			
			window.update();
		}
		
		window.destroy();
		
		Logger.LOGGER.info("Successfully closed.");
	}
	
	private void render(float delta)
	{
		window.clear(33 / 255.0f, 33 / 255.0f, 33 / 255.0f, 1.0f);
		
		// Render Code
	}
}
