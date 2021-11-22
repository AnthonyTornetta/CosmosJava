package com.cornchipss.cosmos.world.entities;

import java.awt.Color;
import java.io.IOException;

import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;

import com.cornchipss.cosmos.blocks.Blocks;
import com.cornchipss.cosmos.client.world.entities.ClientPlayer;
import com.cornchipss.cosmos.material.TexturedMaterial;
import com.cornchipss.cosmos.material.types.RawImageMaterial;
import com.cornchipss.cosmos.models.ModelLoader;
import com.cornchipss.cosmos.netty.NettySide;
import com.cornchipss.cosmos.physx.PhysicalObject;
import com.cornchipss.cosmos.physx.RigidBody;
import com.cornchipss.cosmos.physx.Transform;
import com.cornchipss.cosmos.physx.collision.CollisionInfo;
import com.cornchipss.cosmos.physx.collision.IHasCollisionEvent;
import com.cornchipss.cosmos.physx.collision.obb.OBBCollider;
import com.cornchipss.cosmos.rendering.IRenderable;
import com.cornchipss.cosmos.rendering.Mesh;
import com.cornchipss.cosmos.rendering.debug.DebugRenderer;
import com.cornchipss.cosmos.rendering.debug.DebugRenderer.DrawMode;
import com.cornchipss.cosmos.structures.Structure;
import com.cornchipss.cosmos.utils.IUpdatable;
import com.cornchipss.cosmos.world.World;

public class Laser extends PhysicalObject
	implements IHasCollisionEvent, IRenderable, IUpdatable
{
	private Vector3f halfwidths = new Vector3f(0.5f, 0.5f, 0.5f);

	private float speed;

	private Structure sender;

	private static Mesh mesh;
	private static TexturedMaterial material;

	private Vector3fc origin;

	public Laser(World world, float speed, Structure sender)
	{
		super(world);
		this.speed = 2;
		this.sender = sender;
	}

	@Override
	public OBBCollider OBB()
	{
		return new OBBCollider(this.position(),
			this.body().transform().orientation(), halfwidths);
	}

	@Override
	public void addToWorld(Transform transform)
	{
		body(new RigidBody(transform));
		world().addObject(this);

		origin = new Vector3f(transform.position());

		this.body().velocity(this.body().transform().orientation().forward()
			.mul(speed, new Vector3f()));
	}

	@Override
	public boolean shouldCollideWith(PhysicalObject obj)
	{
		return !obj.equals(sender) && !(obj instanceof Laser);
	}

	@Override
	public boolean onCollide(PhysicalObject obj, CollisionInfo info)
	{
		if (obj instanceof Structure)
		{
			Structure s = (Structure) obj;
			Vector3i point = s.worldCoordsToBlockCoords(info.collisionPoint,
				new Vector3i());

			s.block(point.x, point.y, point.z, Blocks.LIGHT);
		}
		this.world().removeObject(this);

		return true;
	}

	@Override
	public void updateGraphics()
	{
		if (material == null)
		{
			try
			{
				mesh = ModelLoader.fromFile("assets/models/laser").createMesh(0,
					0, 0, halfwidths.x, halfwidths.y, halfwidths.z);

				material = new RawImageMaterial("assets/images/atlas/laser");
				material.init();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void draw(Matrix4fc projectionMatrix, Matrix4fc camera,
		ClientPlayer p)
	{
		material.use();

		material.initUniforms(projectionMatrix, camera,
			body().transform().matrix(), false);

		mesh.prepare();
		mesh.draw();
		mesh.finish();

		material.stop();
	}

	@Override
	public boolean shouldBeDrawn()
	{
		return material != null;
	}

	@Override
	public boolean update(float delta)
	{
		if (NettySide.side() == NettySide.CLIENT)
		{
			DebugRenderer.instance().drawRectangle(
				body().transform().matrix(), new Vector3f(halfwidths.x + 0.01f,
					halfwidths.y + 0.01f, halfwidths.z + 0.01f),
				Color.red, DrawMode.LINES);
		}

		float dSqrd = origin.distanceSquared(body().transform().position());

		if (dSqrd > 10_000)
		{
			this.world().removeObject(this);
		}

		return true;
	}
}
