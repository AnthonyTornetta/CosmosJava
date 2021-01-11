package test.physx;

import java.util.LinkedList;
import java.util.List;

public class World
{
	private List<Transform> transforms;
	
	public World()
	{
		transforms = new LinkedList<>();
	}
	
	public void addTransform(Transform t)
	{
		this.transforms.add(t);
	}
	
	public void removeTransform(Transform t)
	{
		this.transforms.remove(t);
	}
}
