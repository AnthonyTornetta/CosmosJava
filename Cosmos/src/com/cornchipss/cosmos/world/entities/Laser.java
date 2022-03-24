package com.cornchipss.cosmos.world.entities;

import java.awt.Color;
import java.io.IOException;

import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import com.cornchipss.cosmos.blocks.StructureBlock;
import com.cornchipss.cosmos.client.world.entities.ClientPlayer;
import com.cornchipss.cosmos.material.TexturedMaterial;
import com.cornchipss.cosmos.material.types.RawImageMaterial;
import com.cornchipss.cosmos.memory.MemoryPool;
import com.cornchipss.cosmos.models.ModelLoader;
import com.cornchipss.cosmos.netty.NettySide;
import com.cornchipss.cosmos.physx.PhysicalObject;
import com.cornchipss.cosmos.physx.RigidBodyProxy;
import com.cornchipss.cosmos.physx.collision.CollisionInfo;
import com.cornchipss.cosmos.physx.collision.IHasCollisionEvent;
import com.cornchipss.cosmos.physx.collision.obb.LaserOBBCollider;
import com.cornchipss.cosmos.rendering.IRenderable;
import com.cornchipss.cosmos.rendering.Mesh;
import com.cornchipss.cosmos.rendering.debug.DebugRenderer;
import com.cornchipss.cosmos.rendering.debug.DebugRenderer.DrawMode;
import com.cornchipss.cosmos.structures.Structure;
import com.cornchipss.cosmos.utils.IUpdatable;
import com.cornchipss.cosmos.utils.VecUtils;
import com.cornchipss.cosmos.world.World;

public class Laser extends PhysicalObject implements IHasCollisionEvent, IRenderable, IUpdatable
{
	private Vector3f halfwidths = new Vector3f(0.1f, 0.1f, 10f);

	private float speed, damageMultiplier;

	private Structure sender;

	private static Mesh mesh;
	private static TexturedMaterial material;

	public static final float BASE_DAMAGE = 50;

	private Vector3fc origin;

	public Laser(World world, float speed, Structure sender, float damageMultiplier)
	{
		super(world);
		this.speed = speed;
		this.sender = sender;
		this.damageMultiplier = damageMultiplier;
	}

	@Override
	public LaserOBBCollider OBB()
	{
		return MemoryPool.getInstanceOrCreate(LaserOBBCollider.class).set(this.position(),
			this.body().orientation());
	}

	@Override
	public void addToWorld(RigidBodyProxy body)
	{
		super.addToWorld(body);

		this.body().velocity(this.body().orientation().forward().mul(speed, new Vector3f()));
	}

	@Override
	public boolean shouldCollideWith(PhysicalObject obj)
	{
		return !obj.equals(sender) && !(obj instanceof Laser);
	}

	@Override
	public boolean onCollide(PhysicalObject obj, CollisionInfo info)
	{
		if (NettySide.side() == NettySide.SERVER)
		{
			if (obj instanceof Structure)
			{
				Structure s = (Structure) obj;

				info.collisionPoint.sub(info.normal.x / 2.f, info.normal.y / 2.f, info.normal.z / 2.f);

				Vector3i point = s.worldCoordsToBlockCoords(info.collisionPoint, new Vector3i());

				if (s.hasBlock(point.x, point.y, point.z))
					s.block(point.x, point.y, point.z).takeDamage(new StructureBlock(s, point.x, point.y, point.z),
						damageMultiplier * BASE_DAMAGE);
			}
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
				mesh = ModelLoader.fromFile("assets/models/laser").createMesh(0, 0, 0, halfwidths.x, halfwidths.y,
					halfwidths.z);

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
	public void draw(Matrix4fc projectionMatrix, Matrix4fc camera, ClientPlayer p)
	{
		material.use();

		material.initUniforms(projectionMatrix, camera, body().matrix(), false);

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
			DebugRenderer.instance().drawRectangle(body().matrix(),
				new Vector3f(halfwidths.x + 0.01f, halfwidths.y + 0.01f, halfwidths.z + 0.01f), Color.red,
				DrawMode.LINES);
		}

		float dSqrd = origin.distanceSquared(body().position());

		if (dSqrd > 10_000)
		{
			this.world().removeObject(this);
		}

		return true;
	}

	@Override
	protected RigidBody createRigidBody(Transform transform)
	{
		BoxShape cshape = new BoxShape(VecUtils.convert(halfwidths));

		MotionState state = new DefaultMotionState(transform);

		RigidBodyConstructionInfo info = new RigidBodyConstructionInfo(0.1f, state, cshape);

		return new RigidBody(info);
	}
}
