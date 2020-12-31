package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.cornchipss.physics.Transform;
import com.cornchipss.rendering.Texture;
import com.cornchipss.rendering.Window;
import com.cornchipss.utils.Input;
import com.cornchipss.utils.Maths;

import test.blocks.Blocks;
import test.utils.Logger;

public class Main
{
	private Window window;
	
	/**
	 * Loads the shaders + returns the program ID they are linked to
	 * @return the program ID they are linked to	
	 */
	private int loadShaders()
	{
		StringBuilder shaderCode = new StringBuilder();
		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new FileReader("./assets/shaders/chunk.vert"));
			
			for(String line = br.readLine(); line != null; line = br.readLine())
			{
				shaderCode.append(line + System.lineSeparator());
			}
			
			br.close();
		}
		catch(Exception ex)
		{
			throw new RuntimeException(ex);
		}
		
		int vertexShader = GL30.glCreateShader(GL30.GL_VERTEX_SHADER);
		GL30.glShaderSource(vertexShader, shaderCode.toString());
		GL30.glCompileShader(vertexShader);
		
		int success = GL30.glGetShaderi(vertexShader, GL30.GL_COMPILE_STATUS);
		if(success == 0)
		{
			String log = GL30.glGetShaderInfoLog(vertexShader);
			Logger.LOGGER.error("Vertex Shader Compilation Error!!!");
			Logger.LOGGER.error(log);
			System.exit(-1);
		}
		
		// Fragment Shader
		
		shaderCode = new StringBuilder();
		try
		{
			br = new BufferedReader(new FileReader("./assets/shaders/chunk.frag"));
			
			for(String line = br.readLine(); line != null; line = br.readLine())
			{
				shaderCode.append(line + System.lineSeparator());
			}
			
			br.close();
		}
		catch(Exception ex)
		{
			throw new RuntimeException(ex);
		}
		int fragShader = GL30.glCreateShader(GL30.GL_FRAGMENT_SHADER);
		GL30.glShaderSource(fragShader, shaderCode.toString());
		GL30.glCompileShader(fragShader);
		
		success = GL30.glGetShaderi(fragShader, GL30.GL_COMPILE_STATUS);
		if(success == 0)
		{
			String log = GL30.glGetShaderInfoLog(fragShader);
			Logger.LOGGER.error("Fragment Shader Compilation Error!!!");
			Logger.LOGGER.error(log);
			System.exit(-1);
		}
		
		
		int shaderProgram = GL30.glCreateProgram();
		GL30.glAttachShader(shaderProgram, vertexShader);
		GL30.glAttachShader(shaderProgram, fragShader);
		GL30.glLinkProgram(shaderProgram);
		GL20.glValidateProgram(shaderProgram);
		
		Logger.LOGGER.info("Shader Loader > " + GL30.glGetProgramInfoLog(shaderProgram));
		
		if(GL30.glGetProgrami(shaderProgram, GL30.GL_LINK_STATUS) == 0)
		{
			String log = GL30.glGetProgramInfoLog(shaderProgram);
			Logger.LOGGER.error("Shader Program Linking Error!!!");
			Logger.LOGGER.error(log);
			System.exit(-1);
		}
		
		// Once they are linked to the program, we do not need them anymore.
		GL30.glDeleteShader(vertexShader);
		GL30.glDeleteShader(fragShader);
		
		return shaderProgram;
	}
	
	public static void main(String[] args)
	{
		new Main().run();
	}

	private void run()
	{
		Logger.LOGGER.setLevel(Logger.LogLevel.INFO);
		
		window = new Window(1024, 720, "wack simulator 2020");
		
		int shaderProgram = loadShaders();
		
		Structure s = new Structure(new Transform(Maths.zero()), 16*3,16,16*3);//16 * 2, 16 * 2, 16 * 2);
		
		s.transform().translate(new Vector3f(-s.width() / 2, -s.height() / 2, -s.length() / 2));
		
		Random rdm = new Random();
		
		for(int z = 0; z < s.length(); z++)
		{
			for(int x = 0; x < s.width(); x++)
			{
				int h = s.height() - 5;//s.height() -4;//- rdm.nextInt(8) - 4;
				for(int y = 0; y < h; y++)
				{
					if(y == h - 1)
					{
						s.block(x, y, z, Blocks.GRASS);
						
//						if(x % 8 == 0 && z % 8 == 0)
//							s.block(x, y + 3, z, Blocks.LIGHT);
//						if(x % 16 == 0 && z % 16 == 0)
//							s.removeBlock(x, y + 3, z);
					}
					else if(h - y < 5)
						s.block(x, y, z, Blocks.DIRT);
					else
						s.block(x, y, z, Blocks.STONE);
				}
			}
		}
		
		s.calculateLights(false);
		
		for(Chunk c : s.chunks())
			c.render();
		
		for(int dx = -4; dx <= 4; dx++)
			for(int dz = -4; dz <= 4; dz++)
				s.block(8 + dx, s.height() - 1 - 1, 8 + dz, Blocks.STONE);
		
		int timeLoc = GL20.glGetUniformLocation(shaderProgram, "time");
		int camLoc = GL20.glGetUniformLocation(shaderProgram, "u_camera");
		int transLoc = GL20.glGetUniformLocation(shaderProgram, "u_transform");
		int projLoc = GL20.glGetUniformLocation(shaderProgram, "u_proj");
		
		Matrix4f cameraMatrix = new Matrix4f();
		
		Matrix4f projectionMatrix = new Matrix4f();
		projectionMatrix.perspective((float)Math.toRadians(90), 
				1024/720.0f,
				0.1f, 1000);
		
		Texture tex = Texture.loadTexture("atlas/main.png");
				
		Input.setWindow(window);
		
		Vector3f pos = new Vector3f();
		Vector3f rot = new Vector3f();
		
		float[] floatBuf = new float[16];
		
		long t = System.currentTimeMillis();
		
		final int UPS_TARGET = 70;
		
		final int MILLIS_WAIT = 1000 / UPS_TARGET;
		
		long lastSecond = t;
		
		int ups = 0;
		float variance = 0;
		
		while(!window.shouldClose())
		{
			float delta = System.currentTimeMillis() - t;
			
			if(rdm.nextFloat() < 0.05f)
			{
				int x = rdm.nextInt(s.width());
				int y = s.height() - 2;
				int z = rdm.nextInt(s.length());
				
//				for(int dz = -1; dz <= 1; dz++)
//				{
//					for(int dy = -1; dy <= 1; dy++)
//					{
//						for(int dx = -1; dx <= 1; dx++)
//						{
//							if(dx == 0 && dy == 0 && dz == 0)
								s.block(x, y, z, Blocks.LIGHT);
//							else if(s.withinBlocks(x + dx, y + dy, z + dz) && Math.abs(dx) + Math.abs(dy) + Math.abs(dz) == 1)
//								s.block(x + dx, y + dy, z + dz, Blocks.STONE);
//						}
//					}
//				}
			}
			
			if(delta < MILLIS_WAIT)
			{
				try
				{
					Thread.sleep(MILLIS_WAIT - (int)delta);
					
					delta = (System.currentTimeMillis() - t);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			
			delta /= 1000.0f;
			
			if(delta > variance)
				variance = delta;
			
			t = System.currentTimeMillis();
			
			if(lastSecond / 1000 != t / 1000)
			{
				Logger.LOGGER.info("UPS: " + ups + "; Max Variance: " + variance + "ms");
				lastSecond = t;
				ups = 0;
				variance = 0;
			}
			ups++;
			
			update(delta);
			
			GL11.glEnable(GL13.GL_TEXTURE0);
			
			render(delta);
			
			GL30.glUseProgram(shaderProgram);
			GL20.glUniform1f(timeLoc, (float)GLFW.glfwGetTime());
			tex.bind();
			
			GL20.glUniformMatrix4fv(projLoc, false, projectionMatrix.get(floatBuf));
			
			if(Input.isKeyDown(GLFW.GLFW_KEY_W))
				pos.z -= 10f * delta;
			if(Input.isKeyDown(GLFW.GLFW_KEY_S))
				pos.z += 10f * delta;
			if(Input.isKeyDown(GLFW.GLFW_KEY_D))
				pos.x += 10f * delta;
			if(Input.isKeyDown(GLFW.GLFW_KEY_A))
				pos.x -= 10f * delta;
			if(Input.isKeyDown(GLFW.GLFW_KEY_E))
				pos.y += 10f * delta;
			if(Input.isKeyDown(GLFW.GLFW_KEY_Q))
				pos.y -= 10f * delta;
			
			if(Input.isKeyDown(GLFW.GLFW_KEY_C))
				rot.y += 1f * delta;
			if(Input.isKeyDown(GLFW.GLFW_KEY_Z))
				rot.y -= 1f * delta;
			
			if(Input.isKeyDown(GLFW.GLFW_KEY_R))
				rot.x += 1f * delta;
			if(Input.isKeyDown(GLFW.GLFW_KEY_T))
				rot.x -= 1f * delta;
			
			Maths.createViewMatrix(pos, rot, cameraMatrix);
			
			GL20.glUniformMatrix4fv(camLoc, false, cameraMatrix.get(floatBuf));
			
			GL30.glEnable(GL30.GL_DEPTH_TEST);
			GL30.glDepthFunc(GL30.GL_LESS);
			
			for(Chunk chunk : s.chunks())
			{
				Matrix4f transform = new Matrix4f();
				Matrix4fc trans = s.transformMatrix();
				trans.mul(chunk.transformMatrix(), transform);
				
				GL20.glUniformMatrix4fv(transLoc, false, transform.get(floatBuf));
				
				chunk.mesh().prepare();
				chunk.mesh().draw();
				chunk.mesh().finish();
			}
			
			Texture.unbind();
			
			window.update();

		}
		
		window.destroy();
	}
	
	private void update(float delta)
	{
	}
	
	private void render(float delta)
	{
		window.clear(33 / 255.0f, 33 / 255.0f, 33 / 255.0f, 1.0f);
		
		// Render Code
	}
}
