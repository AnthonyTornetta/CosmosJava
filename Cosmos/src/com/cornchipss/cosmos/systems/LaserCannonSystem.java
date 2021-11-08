package com.cornchipss.cosmos.systems;

import java.util.LinkedList;
import java.util.List;

import org.joml.Vector3i;

import com.cornchipss.cosmos.blocks.StructureBlock;
import com.cornchipss.cosmos.structures.Structure;
import com.cornchipss.cosmos.utils.Utils;

public class LaserCannonSystem extends BlockSystem
{
	private static class Node
	{
		int count;
		Vector3i start;
	}
	
	private List<Node> nodes;
	
	public LaserCannonSystem(Structure s)
	{
		super(s);
		
		nodes = new LinkedList<>();
	}
	
	private void update(Node node)
	{
		for(Node n : nodes)
		{
			if(!n.equals(node))
			{
				if(node.start.x == n.start.x && node.start.y == n.start.y)
				{
					if(node.count + node.start.z == n.start.z)
					{
						node.count += n.count;
						nodes.remove(n);
						
						return;
					}
				}
			}
		}
	}
	
	@Override
	public void addBlock(StructureBlock added)
	{
		int x = added.structureX();
		int y = added.structureY();
		int z = added.structureZ();
		
		for(Node n : nodes)
		{
			if(n.start.x == x && n.start.y == y)
			{
				if(n.start.z + n.count == z)
				{
					n.count++;
					update(n);
					return;
				}
			}
		}
		
		Node n = new Node();
		n.count = 1;
		n.start = new Vector3i(x, y, z);
		nodes.add(n);
	}

	@Override
	public void removeBlock(StructureBlock removed)
	{
		int x = removed.structureX();
		int y = removed.structureY();
		int z = removed.structureZ();
		
		for(Node n : nodes)
		{
			if(n.start.x == x && n.start.y == y)
			{				
				if(n.start.z == z)
				{
					// easy way out
					n.start.z++;
					n.count--;
					if(n.count == 0)
						nodes.remove(n);
					return;
				}
				
				if(n.start.z + n.count + 1 == z)
				{
					// easy way out
					n.count--;
					return;
				}
				
				if(n.start.z <= z && z < n.start.z + n.count)
				{
					Node newN = new Node();
					newN.start = new Vector3i(x, y, z + 1);
					newN.count = n.count - z - n.start.z - 1;
					nodes.add(newN);
					
					n.count = z - n.start.z;
					
					return;
				}
			}
		}
	}

	@Override
	public void update(float delta)
	{
		Utils.println(nodes.size());
	}

	@Override
	public String id()
	{
		return BlockSystemIDs.LASER_CANNON_ID;
	}
}
