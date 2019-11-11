package com.cornchipss.utils.datatypes;

import org.joml.Vector3fc;

public class DistanceBinaryTree
{
	private Vector3fc origin;
	
	private static class Node
	{
		Node less, greator;
		
		Vector3fc point;
		float distSqrd;
	}
	
	private Node parentNode;
	
	public DistanceBinaryTree(Vector3fc origin)
	{
		this.origin = origin;
	}
	
	public void add(Vector3fc point)
	{
		float distSqrd = point.distanceSquared(origin);
		
		if(parentNode == null)
		{
			parentNode = new Node();
			parentNode.point = point;
			parentNode.distSqrd = distSqrd;
		}
		else
		{
			add(point, distSqrd, parentNode);
		}
	}
	
	private void add(Vector3fc point, float dist, Node start)
	{
		if(dist < start.distSqrd)
		{
			if(start.less == null)
			{
				start.less = new Node();
				start.less.point = point;
				start.less.distSqrd = dist;
			}
			else
			{
				add(point, dist, start.less);
			}
		}
		else if(dist > start.distSqrd)
		{
			if(start.greator == null)
			{
				start.greator = new Node();
				start.greator.point = point;
				start.greator.distSqrd = dist;
			}
			else
			{
				add(point, dist, start.greator);
			}
		}
	}
	
	public Vector3fc getOrigin() { return origin; }
}
