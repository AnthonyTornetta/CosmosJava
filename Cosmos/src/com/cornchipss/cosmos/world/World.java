package com.cornchipss.cosmos.world;

import java.util.LinkedList;
import java.util.List;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.cosmos.physx.RigidBody;
import com.cornchipss.cosmos.structures.Structure;
import com.cornchipss.cosmos.utils.Logger;

public class World
{
	private List<RigidBody> bodies;
	
	private List<Structure> structures;
	
	private boolean locked = false;
	private List<RigidBody> bodiesToAdd;
	private List<Structure> structuresToAdd;
//	private List<RigidBody> bodiesToRemove;
//	private List<Structure> structuresToRemove;
	
	public World()
	{
		bodies = new LinkedList<>();
		structures = new LinkedList<>();
		
		bodiesToAdd = new LinkedList<>();
		structuresToAdd = new LinkedList<>();
//		bodiesToRemove = new LinkedList<>();
//		structuresToRemove = new LinkedList<>();
	}
	
	public void addRigidBody(RigidBody bdy)
	{
		if(!locked)
			bodies.add(bdy);
		else
			bodiesToAdd.add(bdy);
	}
	
	public void update(float delta)
	{
		Vector3f temp = new Vector3f();
		
		for(RigidBody b : bodies)
		{
			temp.set(b.velocity()).mul(delta);
			
			b.transform().position(temp.add(b.transform().position()));
		}
		
		for(Structure s : structures)
			s.update(delta);
	}

	public List<Structure> structures()
	{
		return structures;
	}
	
	/**
	 * TODO: this
	 * @param location The location to select nearby players for
	 * @return The structures that are near that location (not yet implemented - just returns them all)
	 */
	public List<Structure> structuresNear(Vector3fc location)
	{
		return structures;
	}

	public void addStructure(Structure s)
	{
		if(!locked)
		{
			if(!structures.contains(s))
				structures.add(s);
			else
				Logger.LOGGER.error("Duplicate structure attempted to be added");
		}
		else
			structuresToAdd.add(s);
	}

	public Structure structureFromID(int id)
	{
		for(Structure s : structures)
			if(s.id() == id)
				return s;
		return null;
	}

	public void lock()
	{
		locked = true;
	}
	
	public void unlock()
	{
		locked = false;
		
		while(bodiesToAdd.size() != 0)
			addRigidBody(bodiesToAdd.remove(0));
		
		while(structuresToAdd.size() != 0)
			addStructure(structuresToAdd.remove(0));
	}
}
