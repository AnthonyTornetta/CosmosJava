package test.physx;

import org.joml.Vector3fc;

import com.cornchipss.world.blocks.BlockFace;

public interface PhysicsShape
{
	public BlockFace[] faces();
	public Vector3fc[] sides();
}
