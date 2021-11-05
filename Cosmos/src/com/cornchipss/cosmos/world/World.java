package com.cornchipss.cosmos.world;

import java.util.LinkedList;
import java.util.List;

import org.joml.Vector3fc;

import com.cornchipss.cosmos.physx.simulation.PhysicsWorld;
import com.cornchipss.cosmos.structures.Structure;
import com.cornchipss.cosmos.utils.Logger;

public class World extends PhysicsWorld
{
	private List<Structure> structures;

	private List<Structure> structuresToAdd;

	public World()
	{
		structures = new LinkedList<>();

		structuresToAdd = new LinkedList<>();
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

	public void addStructure(Structure s)
	{
		if (!locked())
		{
			if (!structures.contains(s))
				structures.add(s);
			else
				Logger.LOGGER.error("Duplicate structure attempted to be added");
		}
		else
			structuresToAdd.add(s);
	}

	public Structure structureFromID(int id)
	{
		for (Structure s : structures)
			if (s.id() == id)
				return s;
		return null;
	}

	@Override
	public void unlock()
	{
		super.unlock();

		while (structuresToAdd.size() != 0)
			addStructure(structuresToAdd.remove(0));
	}
}
