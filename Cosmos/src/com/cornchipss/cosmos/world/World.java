package com.cornchipss.cosmos.world;

import java.util.LinkedList;
import java.util.List;

import org.joml.Vector3fc;

import com.cornchipss.cosmos.physx.PhysicalObject;
import com.cornchipss.cosmos.physx.simulation.PhysicsWorld;
import com.cornchipss.cosmos.structures.Structure;

public class World extends PhysicsWorld
{
	private List<Structure> structures;
	
	public World()
	{
		structures = new LinkedList<>();
	}

	@Override
	public void update(float delta)
	{
		super.update(delta);

		for (Structure s : structures)
			s.update(delta);
	}

	public List<Structure> structures()
	{
		return structures;
	}

	/**
	 * TODO: this
	 * 
	 * @param location The location to select nearby players for
	 * @return The structures that are near that location (not yet implemented -
	 *         just returns them all)
	 */
	public List<Structure> structuresNear(Vector3fc location)
	{
		return structures;
	}
	
	@Override
	protected void addObjectDuringUnlock(PhysicalObject obj)
	{
		super.addObjectDuringUnlock(obj);
		
		if(obj instanceof Structure)
			structures.add((Structure)obj);
	}
	
	@Override
	protected void removeObjectDuringUnlock(PhysicalObject obj)
	{
		super.removeObjectDuringUnlock(obj);
		
		if(obj instanceof Structure)
			structures.remove((Structure)obj);
	}

	public Structure structureFromID(int id)
	{
		for (Structure s : structures)
			if (s.id() == id)
				return s;
		return null;
	}
}
