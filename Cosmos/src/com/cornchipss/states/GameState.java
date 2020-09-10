package com.cornchipss.states;

import org.joml.Vector3f;
import org.newdawn.slick.Color;

import com.cornchipss.rendering.BlockStructureRenderer;
import com.cornchipss.rendering.Window;
import com.cornchipss.rendering.debug.DebugRenderer;
import com.cornchipss.world.Universe;
import com.cornchipss.world.entities.Player;
import com.cornchipss.world.sector.Sector;

public class GameState implements State
{
	public static Player player;
	private Universe universe;
	private Sector sector;
	
	private BlockStructureRenderer renderer;
	
	public static final Color CLEAR_COLOR = new Color(25.0f / 255, 29.0f / 255, 30.0f / 255);
	
	@Override
	public void start()
	{
		universe = new Universe();
		sector = new Sector();
		universe.setSector(0, 0, 0, sector);
		
		sector.generate();
		
		player = new Player(0, 128, 0);
		player.setUniverse(universe);
		
		renderer = new BlockStructureRenderer();
	}

	@Override
	public void update()
	{
		sector.update(player);
		player.onUpdate();
	}

	@Override
	public void end()
	{
		
	}

	@Override
	public void render(Window window)
	{		
		window.clear(CLEAR_COLOR.r, CLEAR_COLOR.g, CLEAR_COLOR.b, 1f);
		
		sector.renderPlanetsWithin(1, renderer, player);
		
		DebugRenderer.draw(player);
	}
}
