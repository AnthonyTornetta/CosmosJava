package com.cornchipss.cosmos;

import java.awt.Font;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import com.cornchipss.cosmos.biospheres.Biosphere;
import com.cornchipss.cosmos.biospheres.DesertBiosphere;
import com.cornchipss.cosmos.biospheres.GrassBiosphere;
import com.cornchipss.cosmos.blocks.Blocks;
import com.cornchipss.cosmos.gui.GUI;
import com.cornchipss.cosmos.gui.GUIModel;
import com.cornchipss.cosmos.gui.GUITexture;
import com.cornchipss.cosmos.gui.GUITextureMultiple;
import com.cornchipss.cosmos.gui.text.GUIText;
import com.cornchipss.cosmos.gui.text.OpenGLFont;
import com.cornchipss.cosmos.material.Materials;
import com.cornchipss.cosmos.physx.Transform;
import com.cornchipss.cosmos.registry.Biospheres;
import com.cornchipss.cosmos.rendering.MaterialMesh;
import com.cornchipss.cosmos.rendering.Window;
import com.cornchipss.cosmos.structures.Planet;
import com.cornchipss.cosmos.structures.Ship;
import com.cornchipss.cosmos.structures.Structure;
import com.cornchipss.cosmos.utils.Logger;
import com.cornchipss.cosmos.utils.io.Input;
import com.cornchipss.cosmos.world.World;

public class Main
{
	private Window window;
	
	public static void main(String[] args)
	{
		new Main().run();
	}
	
	private Planet mainPlanet;
	private Ship ship;
	private Matrix4f projectionMatrix;
	private ClientPlayer p;
	private GUI gui;
	private World world;
	private int selectedSlot;
	private GUITextureMultiple[] inventorySlots;
	
	private void run()
	{
		Logger.LOGGER.setLevel(Logger.LogLevel.DEBUG);
		
		Blocks.init();
		
		Biospheres.registerBiosphere(GrassBiosphere.class, "cosmos:grass");
		Biospheres.registerBiosphere(DesertBiosphere.class, "cosmos:desert");
		
		window = new Window(1024, 720, "Cosmos");
		
		Materials.initMaterials();
		
		world = new World();
		
		gui = new GUI(Materials.GUI_MATERIAL);
		gui.init(window.getWidth(), window.getHeight());
		
		GUITexture crosshair = new GUITexture(new Vector3f(window.getWidth() / 2.f - 16, window.getHeight() / 2.f - 16, 0), 32, 32, 0, 0);
		gui.addElement(crosshair);
		
		OpenGLFont font = new OpenGLFont(new Font("Arial", Font.PLAIN, 28));
		font.init();
		
		inventorySlots = new GUITextureMultiple[10];
		
		GUIModel[] models = new GUIModel[10];
		
		selectedSlot = 0;
		
		int slotDimensions = 64;
		
		int startX = (int)(1024 / 2.0f - (inventorySlots.length / 2.0f) * slotDimensions);
		
		for(int i = 0; i < models.length; i++)
		{
			inventorySlots[i] =  new GUITextureMultiple(
					new Vector3f(startX + i * slotDimensions, 0, 0), slotDimensions, slotDimensions, 
					0.5f, 0,
					0, 0.5f);
			
			gui.addElement(inventorySlots[i]);
			
			if(i < Blocks.all().size())
			{
				int margin = 4;
				
				models[i] = new GUIModel(new Vector3f(startX + i * slotDimensions + margin, margin, 0), 
						slotDimensions - margin * 2, Blocks.all().get(i).model());
				
				gui.addElement(models[i]);
			}
		}
		
		inventorySlots[selectedSlot].state(1);
		
		GUIText fpsText = new GUIText("-- --ms", font, 0, 0);
		gui.addElement(fpsText);
		
		mainPlanet = new Planet(world, 16*10, 16*6, 16*10);
		mainPlanet.init();
		Biosphere def = Biospheres.newInstance("cosmos:desert");
		def.generatePlanet(mainPlanet);
		world.addStructure(mainPlanet);
		
		ship = new Ship(world);
		ship.init();
		world.addStructure(ship);
		
		try(DataInputStream shipStr = new DataInputStream(new FileInputStream(new File("assets/structures/ships/test.struct"))))
		{
			ship.read(shipStr);
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
			ship.block(ship.width() / 2, ship.height() / 2, ship.length() / 2, Blocks.SHIP_CORE);
		}
		
		ship.addToWorld(new Transform());
		
		ship.calculateLights(false);
		
		for(Chunk c : ship.chunks())
			c.render();
		
		mainPlanet.calculateLights(false);
		
		for(Chunk c : mainPlanet.chunks())
			c.render();
		
		mainPlanet.addToWorld(new Transform(0, -mainPlanet.height(), 0));
		
		p = new ClientPlayer(world);
		p.addToWorld(new Transform(0, 0, 0));
		
		projectionMatrix = new Matrix4f();
		projectionMatrix.perspective((float)Math.toRadians(90), 
				1024/720.0f,
				0.1f, 1000);
		
		Matrix4f guiProjMatrix = new Matrix4f();
		guiProjMatrix.perspective((float)Math.toRadians(90), 
				1024/720.0f,
				0.1f, 1000);
		
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
			if(window.wasWindowResized())
			{
				projectionMatrix.identity();
				projectionMatrix.perspective((float)Math.toRadians(90), 
						window.getWidth()/(float)window.getHeight(),
						0.1f, 1000);
				
				gui.updateProjection(window.getWidth(), window.getHeight());
			}
			
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
				fpsText.text(ups + " " + (int)(variance*1000) + "ms");
				
				lastSecond = t;
				ups = 0;
				variance = 0;
			}
			ups++;
			
			if(Input.isKeyJustDown(GLFW.GLFW_KEY_F1))
				Input.toggleCursor();
			
			if(Input.isKeyDown(GLFW.GLFW_KEY_ESCAPE))
				running = false;
			
			update(delta);
			
			Input.update();
			
			render(delta);
		}
		
		window.destroy();
		
		Logger.LOGGER.info("Successfully closed.");
	}
	
	private static void drawStructure(Structure s, Matrix4fc projectionMatrix, ClientPlayer p)
	{
		for(Chunk chunk : s.chunks())
		{
			Matrix4f transform = new Matrix4f();
			Matrix4fc trans = s.openGLMatrix();
			trans.mul(chunk.transformMatrix(), transform);
			
			for(MaterialMesh m : chunk.model().materialMeshes())
			{
				m.material().use();
				
				Matrix4fc camera = p.shipPiloting() == null ? 
						p.camera().viewMatrix() : 
							p.shipPiloting().body().transform().invertedMatrix();
				
				m.material().initUniforms(projectionMatrix, camera, transform, false);
				
				m.mesh().prepare();
				m.mesh().draw();
				m.mesh().finish();
				
				m.material().stop();
			}
		}
	}
	
	private void update(float delta)
	{
		world.update(delta);
		
		int prevRow = p.selectedInventoryRow();
		
		p.update(delta);
		
		int row = p.selectedInventoryRow();
		
		if(prevRow != row)
		{
			inventorySlots[prevRow].state(0);
			inventorySlots[row].state(1);
		}
		
		if(Input.isKeyJustDown(GLFW.GLFW_KEY_ENTER))
		{
			try(DataOutputStream str = new DataOutputStream(new FileOutputStream(new File("assets/structures/ships/test.struct"))))
			{
				ship.write(str);
			}
			catch(IOException ex)
			{
				ex.printStackTrace();
			}
		}
		
		for(int key = GLFW.GLFW_KEY_1; key <= GLFW.GLFW_KEY_9 + 1; key++)
		{
			if(key > GLFW.GLFW_KEY_9)
			{
				if(Input.isKeyJustDown(GLFW.GLFW_KEY_0))
				{
					inventorySlots[selectedSlot].state(0);
					selectedSlot = 9;
					inventorySlots[selectedSlot].state(1);
				}
			}
			else if(Input.isKeyJustDown(key))
			{
				inventorySlots[selectedSlot].state(0);
				selectedSlot = key - GLFW.GLFW_KEY_1;
				inventorySlots[selectedSlot].state(1);
				break;
			}
		}
		
		if(Input.isKeyJustDown(GLFW.GLFW_KEY_ENTER))
		{
			try(DataOutputStream str = new DataOutputStream(new FileOutputStream(new File("assets/structures/ships/test.struct"))))
			{
				ship.write(str);
			}
			catch(IOException ex)
			{
				ex.printStackTrace();
			}
		}
	}
	
	private void render(float delta)
	{
		window.clear(33 / 255.0f, 33 / 255.0f, 33 / 255.0f, 1.0f);
		
		GL11.glEnable(GL13.GL_TEXTURE0);
		
		GL30.glEnable(GL30.GL_DEPTH_TEST);
		GL30.glDepthFunc(GL30.GL_LESS);
		
		//GL30.glPolygonMode(GL30.GL_FRONT_AND_BACK, GL30.GL_LINE);
		
//		drawStructure(mainPlanet, projectionMatrix, p);
		
		for(Structure s : world.structures())
		{
			drawStructure(s, projectionMatrix, p);
		}
		
		gui.draw();
		
		window.update();
	}
}
