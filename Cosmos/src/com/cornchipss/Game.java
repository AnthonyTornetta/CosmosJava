package com.cornchipss;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import com.cornchipss.entities.Player;
import com.cornchipss.registry.Biospheres;
import com.cornchipss.registry.Blocks;
import com.cornchipss.registry.Options;
import com.cornchipss.rendering.DebugRenderer;
import com.cornchipss.rendering.PlanetRenderer;
import com.cornchipss.rendering.Window;
import com.cornchipss.utils.Input;
import com.cornchipss.utils.Maths;
import com.cornchipss.utils.Timer;
import com.cornchipss.utils.Utils;
import com.cornchipss.world.Universe;
import com.cornchipss.world.sector.Sector;

/*
 * Started August 5th, 2019
 */
public class Game implements Runnable
{
	private int startWidth = 1024, startHeight = 720;
	private Thread gameThread;
	private Window window;
	
	private static Game instance;
	
	public static final int FPS_CAP = 60;
	
	public Game()
	{
		if(instance != null)
			throw new IllegalStateException("Cannot instantiate Cosmos twice!");
		instance = this;
	}
	
	public synchronized void start()
	{
		gameThread = new Thread(this, "game");
		gameThread.start();
	}
	
	private static DebugRenderer debug;
	
	public static void renderLine(Vector3f begin, Vector3f end, int r, int g, int b)
	{
		debug.addLine(begin, end, r, g, b);
	}
	
	@Override
	public void run()
	{		
		window = new Window(startWidth, startHeight, "Cosmos");
		Input.setWindow(window);
		
		Options.registerDefaults();
		
		Biospheres.registerBiospheres("com.cornchipss.world.biospheres");

		Blocks.registerBlocks();
		
		Universe universe = new Universe();
		Sector sector = new Sector();
		universe.setSector(0, 0, 0, sector);
		
		sector.generate();
		
		Player player = new Player(0, 128, 0);
		player.setUniverse(universe);
		
		PlanetRenderer renderer = new PlanetRenderer();
		debug = new DebugRenderer();
		
		Timer updateTimer = new Timer();
		Timer secondsTimer = new Timer();
		
		int fps = 0;
		int ups = 0;
		
		Input.hideCursor(true);
		
		Input.update();
		
		while(!window.shouldClose() && !Input.isKeyDown(GLFW.GLFW_KEY_ESCAPE))
		{
			if(Input.isKeyJustDown(GLFW.GLFW_KEY_F1))
				Input.toggleCursor();
			
			while(updateTimer.getDeltaMillis() >= 1000 / FPS_CAP)
			{
				float r = (float)GLFW.glfwGetTime() / 1000000f;
				sector.getPlanet(0, 0, 0).rotate(r, r, r);
				player.onUpdate();
				sector.update(player);
				
				updateTimer.subtractTimeMilli(1000 / FPS_CAP);
				ups++;
				
				Input.update();
			}
			
			while(secondsTimer.getDeltaMillis() >= 1000)
			{
				Utils.println("FPS/UPS: " + fps + " / " + ups);
				fps = ups = 0;
				secondsTimer.subtractTimeMilli(1000);
			}
			
			long sleep = 1000 / FPS_CAP - updateTimer.getDeltaMillis();
			Timer.sleep(sleep);
			
			window.clear(0.5f, 0.8f, 1f, 1f);
			
			sector.renderPlanetsWithin(1, renderer, player);
			
			debug.setupRender(player);
			debug.render();
			
			debug.clear();
			
			window.update();
			
			fps++;
		}
		
		window.destroy();
		
		System.exit(0);
	}
	
	public Window getWindow()
	{
		return window;
	}
	
	public static void main(String[] args)
	{
		new Game().start();
	}

	public static Game getInstance()
	{
		return instance;
	}
}