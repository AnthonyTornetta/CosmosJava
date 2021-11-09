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
		for (Node n : nodes)
		{
			if (!n.equals(node))
			{
				if (node.start.x == n.start.x && node.start.y == n.start.y)
				{
					if (node.start.z - node.count == n.start.z)
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

		for (Node n : nodes)
		{
			if(n.start.x == x && n.start.y == y)
			{
				if(n.start.z - n.count == z)
				{
					n.count++;
					
					for(Node n2 : nodes)
					{
						if(!n2.equals(n))
						{
							if(n2.start.x == x && n2.start.y == y)
							{
								if(n2.start.z == n.start.z - n.count)
								{
									n.count += n2.count;
									
									nodes.remove(n2);
									
									return;
								}
							}
						}
					}
					return;
				}
				
				if(n.start.z + 1 == z)
				{
					n.start.z++;
					n.count++;
					
					for(Node n2 : nodes)
					{
						if(!n2.equals(n))
						{
							if(n2.start.x == x && n2.start.y == y)
							{
								if(n2.start.z - n2.count == n.start.z)
								{
									n2.count += n.count;
									
									nodes.remove(n);
									
									return;
								}
							}
						}
					}
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

		for (Node n : nodes)
		{
			if(n.start.x == x && n.start.y == y)
			{
				if(z == n.start.z - n.count + 1)
				{
					n.count--;

					if(n.count == 0)
						nodes.remove(n);
					return;
				}
				
				if(z == n.start.z)
				{
					n.start.z--;
					n.count--;
					
					if(n.count == 0)
						nodes.remove(n);
					return;
				}
				
				if(z > n.start.z - n.count && z <= n.start.z)
				{
					Node newNode = new Node();
					newNode.start = new Vector3i(x, y, z - 1);
					
					int newCount = n.start.z - z;
					
					newNode.count = n.count - newCount - 1;
					n.count = newCount;
					
					nodes.add(newNode);
					
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
