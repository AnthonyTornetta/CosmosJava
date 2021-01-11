package test.physx;

import java.util.Arrays;
import java.util.List;

import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.utils.Maths;

public class Transform
{
	private Vector3f position;
	private Vector3f velocity;
	private Rotation rotation;
	
	private Vector3f dimensions;
	
	private Transform parent;
	private List<Transform> children;
	
	public Transform(Vector3fc position, Rotation rotation, Vector3fc dimensions, Transform parent, Transform... children)
	{
		this.position = new Vector3f().set(position);
		this.rotation = rotation;
		this.velocity = new Vector3f();
		this.dimensions = new Vector3f().set(dimensions);
		
		this.parent = parent;
		this.children = Arrays.asList(children);
	}
	
	public Transform(Vector3fc position, Rotation rotation, Vector3fc dimensions)
	{
		this(position, rotation, dimensions, null);
	}
	
	public Transform(Vector3fc position, Vector3fc dimensions)
	{
		this(position, new Rotation(), dimensions);
	}
	
	public Transform()
	{
		this(Maths.zero(), Maths.zero());
	}
	
	public void update(float delta, Transform... transforms)
	{
		position.add(Maths.mul(velocity, delta));
	}

	public void translate(Vector3f dPos)
	{
		position.add(dPos);
	}
	
	public Matrix4fc matrix()
	{
		return Maths.createTransformationMatrix(position, rotation.rotation());
	}
	
	// Auto generated getters & setters below //
		
	public Vector3f position()
	{
		return position;
	}

	public Vector3f getVelocity()
	{
		return velocity;
	}

	public void setVelocity(Vector3f velocity)
	{
		this.velocity = velocity;
	}

	public void setPosition(Vector3f position)
	{
		this.position = position;
	}

	public Rotation rotation()
	{
		return rotation;
	}

	public void rotation(Rotation rotation)
	{
		this.rotation = rotation;
	}

	public Vector3f dimensions()
	{
		return dimensions;
	}

	public void dimensions(Vector3f dimensions)
	{
		this.dimensions = dimensions;
	}

	public Transform parent()
	{
		return parent;
	}

	public void parent(Transform parent)
	{
		this.parent = parent;
	}

	public List<Transform> children()
	{
		return children;
	}

	public void children(List<Transform> children)
	{
		this.children = children;
	}
}
