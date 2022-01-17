package com.cornchipss.cosmos.world;

import java.util.LinkedList;
import java.util.List;

import org.joml.Vector3fc;

import com.cornchipss.cosmos.physx.PhysicalObject;
import com.cornchipss.cosmos.physx.simulation.PhysicsWorld;
import com.cornchipss.cosmos.structures.Structure;
import com.cornchipss.cosmos.utils.IUpdatable;

public class World extends PhysicsWorld
{
	private List<Structure> structures;
	private List<IUpdatable> updatableObjects;

	public World()
	{
		structures = new LinkedList<>();
		updatableObjects = new LinkedList<>();
	}

	@Override
	public void update(float delta)
	{
		super.update(delta);

		for (IUpdatable o : updatableObjects)
			o.update(delta);
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

		if (obj instanceof Structure)
			structures.add((Structure) obj);

		if (obj instanceof IUpdatable)
			updatableObjects.add((IUpdatable) obj);
	}

	@Override
	protected void removeObjectDuringUnlock(PhysicalObject obj)
	{
		super.removeObjectDuringUnlock(obj);

		if (obj instanceof Structure)
			structures.remove((Structure) obj);

		if (obj instanceof IUpdatable)
			updatableObjects.remove((IUpdatable) obj);
	}

	public Structure structureFromID(int id)
	{
		for (Structure s : structures)
			if (s.id() == id)
				return s;
		return null;
	}
}
