package com.cornchipss.cosmos.models;

import java.util.LinkedList;
import java.util.List;

import com.cornchipss.cosmos.blocks.BlockFace;
import com.cornchipss.cosmos.material.Materials;
import com.cornchipss.cosmos.rendering.Mesh;
import com.cornchipss.cosmos.utils.Utils;

public abstract class AnimatedCubeModel extends CubeModel
{
	@Override
	public Mesh createMesh(float offX, float offY, float offZ, float scale, BlockFace... sides)
	{
		Mesh mesh = super.createMesh(false, offX, offY, offZ, scale, sides);
		
		List<Float> animationInfo = new LinkedList<>();
		
		for(BlockFace face : sides)
		{
			for(int i = 0; i < 4; i++)
			{
				animationInfo.add((float)maxAnimationStage(face));
				animationInfo.add(animationDelay(face) * 1000);
			}
		}
		
		float[] animationData = Utils.toArray(animationInfo);
		
		mesh.storeData(Mesh.ANIMATION_INDEX, 2, animationData);
		
		mesh.unbind();
		
		return mesh;
	}
	
	/**
	 * Used by the {@link Materials#ANIMATED_DEFAULT_MATERIAL}
	 * @param side The side of the block
	 * @return The number of animation frames this face has
	 */
	public abstract int maxAnimationStage(BlockFace side);
	
	public abstract float animationDelay(BlockFace side);
}
