package com.cornchipss.rendering;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL33;

import com.cornchipss.rendering.shaders.PlanetShader;
import com.cornchipss.utils.datatypes.Vector3fList;
import com.cornchipss.world.entities.Player;
import com.cornchipss.world.structures.BlockStructure;

public class BlockStructureRenderer extends Renderer
{
	private int timeLocation;
	
	private int matrixLocation;
	
	private Texture atlas;
	
	// Used for mass rendering models
	private int positionsVBO;
	
	public BlockStructureRenderer()
	{
		super(new PlanetShader());
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		// For use w/ storing positions of where models are
		// This is initialized here just to minimize interactions w/ the GPU to save some performace (i think that's how it works)
		positionsVBO = GL15.glGenBuffers();
	}
	
	@Override
	public void loadTextures()
	{
		atlas = Texture.loadTexture("atlas/main.png");
	}
	
	@Override
	public void loadUniformLocations()
	{
		timeLocation = getShader().getUniformLocation("u_time");
		
		matrixLocation = getShader().getUniformLocation("u_transformation_matrix");
	}
	
	@Override
	public void loadUniforms()
	{
		getShader().setUniformF(timeLocation, (float)GLFW.glfwGetTime());
	}
	
	@Override
	public void bindTextures()
	{
		atlas.bind();
	}
	
	public void render(BlockStructure structure, Player player)
	{
		if(structure == null)
			throw new IllegalArgumentException("Cannot render a null planet!");
		
		if(structure.isRenderable())
		{
			Matrix4f openglMatrix = new Matrix4f();
			openglMatrix.identity();
			openglMatrix.translate(structure.transform().position());
			openglMatrix.rotate(structure.transform().rotation());

			getShader().loadUniformMatrix(matrixLocation, openglMatrix);
			
			Map<Model, Vector3fList> modelsAndPositions = structure.getModelsAndPositions();
			
			List<Model> transparentModels = new LinkedList<>();
			
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glCullFace(GL11.GL_BACK);
			
			for(Model m : modelsAndPositions.keySet())
			{
				if(m.isTransparent())
					transparentModels.add(m);
				else
				{
					renderModel(m, modelsAndPositions.get(m));
				}
			}
			
//			GL11.glEnable(GL11.GL_BLEND);
//			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			
			for(Model m : transparentModels)
			{
				// TODO:
				
//				Vector3fc[] vecs = modelsAndPositions.get(m).asVecs();
//				
//				final Vector3fc pos = new Vector3f(player.getPosition());
//				
//				Arrays.sort(vecs, new Comparator<Vector3fc>()
//				{
//					@Override
//					public int compare(Vector3fc o1, Vector3fc o2)
//					{
////						Utils.println(Utils.toString(o1) + ", " + Utils.toString(o2));
//						
//						float d1 = o1.distanceSquared(pos);
//						float d2 = o2.distanceSquared(pos);
//						
//						if(d1 < d2)
//							return 1;
//						if(d1 > d2)
//							return -1;
//						return 0;
//					}
//				});
//				
//				float[] floats = new float[vecs.length * 3];
//				for(int i = 0; i < vecs.length; i++)
//				{
//					floats[i * 3] = vecs[i].x();
//					floats[i * 3 + 1] = vecs[i].y();
//					floats[i * 3 + 2] = vecs[i].z();
//				}
				
				float[] floats = modelsAndPositions.get(m).asFloats();
				
				GL11.glCullFace(GL11.GL_FRONT);
				
				renderModel(m, floats);
				
				GL11.glCullFace(GL11.GL_BACK);
				
				renderModel(m, floats);
			}
			
			GL11.glDisable(GL11.GL_BLEND);
		}
	}
	
	private void renderModel(Model m, Vector3fList posList)
	{
		renderModel(m, posList.asFloats());
	}
	
	private void renderModel(Model m, float[] positions)
	{
		GL30.glBindVertexArray(m.getVao());
		
		// Update every position in the model
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, positionsVBO);
		
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, positions, GL15.GL_STATIC_DRAW);
		GL30.glVertexAttribPointer(3, 3, GL11.GL_FLOAT, false, 0, 0);
		GL33.glVertexAttribDivisor(3, 1);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		// Draw it all!
		GL30.glEnableVertexAttribArray(0);
		GL30.glEnableVertexAttribArray(1);
		GL30.glEnableVertexAttribArray(2);
		GL30.glEnableVertexAttribArray(3);
		
		GL31.glDrawElementsInstanced(GL30.GL_TRIANGLES, m.getVertexCount(), GL11.GL_UNSIGNED_INT, 0, positions.length / 3);
		
		GL33.glDisableVertexAttribArray(3);
		GL30.glDisableVertexAttribArray(2);
		GL30.glDisableVertexAttribArray(1);
		GL30.glDisableVertexAttribArray(0);
		
		GL30.glBindVertexArray(0);
	}
}
