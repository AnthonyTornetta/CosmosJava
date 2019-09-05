package com.cornchipss;

import org.lwjgl.glfw.GLFW;

import com.cornchipss.registry.Biospheres;
import com.cornchipss.registry.Blocks;
import com.cornchipss.rendering.PlanetRenderer;
import com.cornchipss.rendering.Window;
import com.cornchipss.utils.Input;
import com.cornchipss.utils.Timer;
import com.cornchipss.world.Universe;
import com.cornchipss.world.biospheres.Biosphere;
import com.cornchipss.world.entities.Player;
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
			throw new IllegalStateException("Cannot instantiate Minecraft Ripoff twice!");
		instance = this;
	}
	
	public synchronized void start()
	{
		gameThread = new Thread(this, "game");
		gameThread.start();
	}
	
	@Override
	public void run()
	{
		window = new Window(startWidth, startHeight, "end my eternal suffering");
		Input.setWindow(window);
		
		Biospheres.registerBiospheres("com.cornchipss.world.biospheres");
		
		Blocks.registerBlocks();
		
		Universe universe = new Universe();
		Sector sector = new Sector();
		universe.setSector(0, 0, 0, sector);
		
		sector.generate();
		
		PlanetRenderer renderer = new PlanetRenderer();
		
		Player player = new Player(0, 0, 0);
		player.setUniverse(universe);
		
		Timer updateTimer = new Timer();
		Timer secondsTimer = new Timer();
		
		int fps = 0;
		int ups = 0;
		
		Input.hideCursor(true);
		
		while(!window.shouldClose() && !Input.isKeyDown(GLFW.GLFW_KEY_ESCAPE))
		{			
			while(updateTimer.getDeltaMillis() >= 1000 / FPS_CAP)
			{
				Input.update();
				player.onUpdate();
				sector.update(player);
				
				updateTimer.subtractTimeMilli(1000 / FPS_CAP);
				ups++;
			}
			
			while(secondsTimer.getDeltaMillis() >= 1000)
			{
				System.out.println("FPS/UPS: " + fps + " / " + ups);
				fps = ups = 0;
				secondsTimer.subtractTimeMilli(1000);
			}
			
			try
			{
				long sleep = 1000 / FPS_CAP - updateTimer.getDeltaMillis();
				if(sleep > 0)
					Thread.sleep(sleep);
			}
			catch (InterruptedException e)
			{}
			
			window.clear(0.5f, 0.8f, 1f, 1f);
			
			sector.renderPlanetsWithin(1, renderer, player);
			
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