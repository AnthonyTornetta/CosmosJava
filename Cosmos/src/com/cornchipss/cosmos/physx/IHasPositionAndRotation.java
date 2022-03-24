package com.cornchipss.cosmos.physx;

import org.joml.Vector3fc;

/**
 * Please find a better name for me
 */
public interface IHasPositionAndRotation
{
	public Vector3fc position();
	public Orientation orientation();
}
