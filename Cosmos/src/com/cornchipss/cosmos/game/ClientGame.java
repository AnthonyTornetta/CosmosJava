package com.cornchipss.cosmos.game;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import com.cornchipss.cosmos.client.Client;
import com.cornchipss.cosmos.client.CosmosNettyClient;
import com.cornchipss.cosmos.gui.GUI;
import com.cornchipss.cosmos.gui.GUITexture;
import com.cornchipss.cosmos.gui.guis.HotbarGUI;
import com.cornchipss.cosmos.gui.guis.PauseMenuGUI;
import com.cornchipss.cosmos.gui.guis.ShipGUI;
import com.cornchipss.cosmos.gui.measurement.MeasurementPair;
import com.cornchipss.cosmos.gui.measurement.MeasurementParser;
import com.cornchipss.cosmos.gui.measurement.PixelMeasurement;
import com.cornchipss.cosmos.gui.text.Fonts;
import com.cornchipss.cosmos.gui.text.GUIText;
import com.cornchipss.cosmos.gui.text.OpenGLFont;
import com.cornchipss.cosmos.material.Material;
import com.cornchipss.cosmos.material.Materials;
import com.cornchipss.cosmos.material.RawImageMaterial;
import com.cornchipss.cosmos.models.entities.PlayerModel;
import com.cornchipss.cosmos.physx.Transform;
import com.cornchipss.cosmos.rendering.MaterialMesh;
import com.cornchipss.cosmos.rendering.Mesh;
import com.cornchipss.cosmos.rendering.Window;
import com.cornchipss.cosmos.structures.Structure;
import com.cornchipss.cosmos.utils.DebugMonitor;
import com.cornchipss.cosmos.utils.Utils;
import com.cornchipss.cosmos.utils.io.Input;
import com.cornchipss.cosmos.world.Chunk;
import com.cornchipss.cosmos.world.entities.player.ClientPlayer;
import com.cornchipss.cosmos.world.entities.player.Player;

public class ClientGame extends Game
{
	private Matrix4f projectionMatrix;
	private ClientPlayer player;
	private GUI gui;
	private GUIText fpsText;
	private CosmosNettyClient nettyClient;
	public CosmosNettyClient nettyClient() { return nettyClient; }
	
	private HotbarGUI hotbarGUI;
	
	private volatile boolean running = true;
	
	private boolean drawGUI = true;
	
	private static ClientGame instance;
	public static ClientGame instance() { return instance; }
	
	private Mesh playerMesh;
	private Material playerMaterial;
	
	private PauseMenuGUI pauseMenu;
	
	private ShipGUI shipGUI;
	
	private void initPauseMenu()
	{
		pauseMenu = new PauseMenuGUI(MeasurementPair.ZERO, MeasurementPair.HUNDRED_PERCENT);
		
		gui.addElement(pauseMenu);
	}
	
	private void initGraphics()
	{
		gui = new GUI(Materials.GUI_MATERIAL);
		gui.init(0, 0, Window.instance().getWidth(), Window.instance().getHeight());
		
		initPauseMenu();
		
		hotbarGUI = new HotbarGUI(player().inventory(), MeasurementPair.ZERO, MeasurementPair.HUNDRED_PERCENT);
		gui.addElement(hotbarGUI);
		
		shipGUI = new ShipGUI(MeasurementPair.ZERO, MeasurementPair.HUNDRED_PERCENT);
		gui.addElement(shipGUI);

		GUITexture crosshair = new GUITexture(
				new MeasurementPair(
						MeasurementParser.parse("50% - 16"),
						MeasurementParser.parse("50% - 16")),
				new MeasurementPair(
						new PixelMeasurement(32),
						new PixelMeasurement(32)), 
				0, 0);
		
		gui.addElement(crosshair);
		
		OpenGLFont font = Fonts.ARIAL_28;
		
		
		fpsText = new GUIText("-- --ms", font, 
				new MeasurementPair(PixelMeasurement.ZERO, PixelMeasurement.ZERO));
		gui.addElement(fpsText);
		
		playerMaterial = new RawImageMaterial("assets/images/atlas/player");
		playerMaterial.init();
		
		playerMesh = new PlayerModel(playerMaterial).createMesh(0, 0, 0, 1);
	}
	
	public ClientGame(CosmosNettyClient nettyClient)
	{
		if(instance != null)
			throw new IllegalStateException("A game is already running.");
		
		instance = this;
		
		this.nettyClient = nettyClient;

		projectionMatrix = new Matrix4f();
		projectionMatrix.perspective((float)Math.toRadians(90), 
				Window.instance().getWidth()/(float)Window.instance().getHeight(),
				0.1f, 1000);
	}
	
	public void onResize(int w, int h)
	{
		projectionMatrix.identity();
		projectionMatrix.perspective((float)Math.toRadians(90), 
				w/(float)h,
				0.1f, 1000);
		
		gui.onResize(w, h);
		pauseMenu.onResize(w, h);
	}
	
	public void render(float delta)
	{
		if(player() == null)
			return;
		if(gui == null)
			initGraphics();

		GL11.glEnable(GL13.GL_TEXTURE0);
		
		GL30.glEnable(GL30.GL_DEPTH_TEST);
		GL30.glDepthFunc(GL30.GL_LESS);
		
		//GL30.glPolygonMode(GL30.GL_FRONT_AND_BACK, GL30.GL_LINE);
		
		world().lock();
		for(Structure s : world().structures())
		{			
//			if(!s.hasBeenRendered())
//			{
//				s.calculateLights();
//				s.render();
//			}
			
			drawStructure(s, projectionMatrix, player);
		}
		world().unlock();
		
		for(Player p : nettyClient.players().players())
		{
			if(!p.equals(player))
			{
				playerMaterial.use();
				
				Matrix4fc camera = player.shipPiloting() == null ? 
						player.camera().viewMatrix() : 
							player.shipPiloting().body().transform().invertedMatrix();
						
				playerMaterial.initUniforms(projectionMatrix, camera, 
						p.body().transform().matrix(), false);
				
				playerMesh.prepare();
				playerMesh.draw();
				playerMesh.finish();
				
				playerMaterial.stop();
			}
		}
		
		gui.update(delta);
		
		if(drawGUI)
			gui.draw();
	}
	
	@Override
	public void postUpdate()
	{
		if(nettyClient.ready())
		{
			for(Structure s : world().structures())
				updateStructureGraphics(s);
		}
	}
	
	private void togglePause()
	{
		pauseMenu.active(!pauseMenu.active());
		
		Input.hideCursor(!pauseMenu.active());
	}
	
	@Override
	public void update(float delta)
	{
		if(shipGUI != null)
		{
			if(!Utils.equals(player.shipPiloting(), shipGUI.ship()))
			{
				shipGUI.ship(player.shipPiloting());
			}
		}
		
		if(Input.isKeyJustDown(GLFW.GLFW_KEY_P))
		{
			togglePause();
		}
		
		if((pauseMenu == null || !pauseMenu.active()) && nettyClient.ready())
		{
			super.update(delta);
			
			if(player() == null)
				return;
			if(gui == null)
				initGraphics();
			
			if(Input.isKeyJustDown(GLFW.GLFW_KEY_F3))
				drawGUI = !drawGUI;
			
			fpsText.text(DebugMonitor.get("ups") + " " + (int)((Float)DebugMonitor.get("ups-variance")*1000) + "ms");
			
			int prevRow = player.selectedInventoryColumn();
			
			player.update(delta);
			
			int row = player.selectedInventoryColumn();
			
			if(prevRow != row)
			{
				hotbarGUI.select(row);
			}
		}
	}
	
	private static void updateStructureGraphics(Structure s)
	{
		if(s.lightMap().needsCalculated())
			s.calculateLightsAndApply();
		
		for(Chunk c : s.chunks())
			if(c.needsRendered())
				c.render();
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

	public ClientPlayer player()
	{
		return player;
	}
	
	public void player(ClientPlayer p)
	{
		this.player = p;
		p.addToWorld(new Transform());
	}

	public void running(boolean r)
	{
		running = r;
	}
	
	public boolean running()
	{
		return Client.instance().running() && running;
	}
}
