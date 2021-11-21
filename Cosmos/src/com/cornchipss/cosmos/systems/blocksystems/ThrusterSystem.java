package com.cornchipss.cosmos.systems.blocksystems;

import org.joml.Vector3f;

import com.cornchipss.cosmos.blocks.StructureBlock;
import com.cornchipss.cosmos.blocks.modifiers.IThrustProducer;
import com.cornchipss.cosmos.netty.NettySide;
import com.cornchipss.cosmos.structures.Ship;
import com.cornchipss.cosmos.structures.Structure;
import com.cornchipss.cosmos.systems.BlockSystem;
import com.cornchipss.cosmos.systems.BlockSystemIDs;
import com.cornchipss.cosmos.utils.Maths;

public class ThrusterSystem extends BlockSystem
{
	public ThrusterSystem(Structure s)
	{
		super(s);
	}

	private float thrustPerSecond = 0;
	private float powerPerSecond = 0;;

	@Override
	public void update(float delta)
	{
		if (NettySide.side() == NettySide.CLIENT)
			return;

		if (structure() instanceof Ship)
		{
			Ship ship = (Ship) structure();

			float energyCost = powerPerSecond * delta;

			float thrustForce = thrustPerSecond * delta;

			Vector3f dVel = new Vector3f();

			if (ship.movement().forward())
				dVel.add(ship.body().transform().forward());
			if (ship.movement().backward())
				dVel.sub(ship.body().transform().forward());
			if (ship.movement().right())
				dVel.add(ship.body().transform().right());
			if (ship.movement().left())
				dVel.sub(ship.body().transform().right());
			if (ship.movement().up())
				dVel.add(ship.body().transform().up());
			if (ship.movement().down())
				dVel.sub(ship.body().transform().up());

			if (dVel.x != 0 || dVel.y != 0 || dVel.z != 0 || ship.movement().deltaRotation().x() != 0
				|| ship.movement().deltaRotation().y() != 0 || ship.movement().deltaRotation().z() != 0
				|| ship.movement().stop() && (ship.body().velocity().dot(ship.body().velocity()) != 0))
			{
				if (!ship.useEnergy(energyCost))
					return;
			}
			else
				return; // Nothing is happening

			float accel = thrustForce / ship.mass();

			dVel.x = (dVel.x() * (accel));
			dVel.z = (dVel.z() * (accel));
			dVel.y = (dVel.y() * (accel));

			Vector3f vel = new Vector3f(ship.body().velocity());

			if (ship.movement().stop())
			{
				Vector3f r = new Vector3f(0.1f * ship.body().velocity().x(), 0.1f * ship.body().velocity().y(),
					0.1f * ship.body().velocity().z());

				if (r.dot(r) != 0)
					r.normalize(accel);

				vel.sub(r);
			}

			vel.add(dVel);

			vel = Maths.safeNormalize(vel, 100.0f);

			ship.body().velocity(vel);

			ship.body().angularVelocity(ship.movement().deltaRotation().negate(new Vector3f()));
		}
	}

	@Override
	public void addBlock(StructureBlock added)
	{
		IThrustProducer p = (IThrustProducer) added.block();
		thrustPerSecond += p.thrustGeneratedPerSecond();
		powerPerSecond += p.powerUsedPerSecond();
	}

	@Override
	public void removeBlock(StructureBlock removed)
	{
		IThrustProducer p = (IThrustProducer) removed.block();
		thrustPerSecond -= p.thrustGeneratedPerSecond();
		powerPerSecond -= p.powerUsedPerSecond();
	}

	@Override
	public String id()
	{
		return BlockSystemIDs.THRUSTER_ID;
	}
}
