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

public class Main
{
	private Window window;
	
	/**
	 * Loads the shaders + returns the program ID they are linked to
	 * @return
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
			System.err.println("Vertex Shader Compilation Error!!!");
			System.err.print(log);
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
			System.err.println("Fragment Shader Compilation Error!!!");
			System.err.print(log);
			System.exit(-1);
		}
		
		
		int shaderProgram = GL30.glCreateProgram();
		GL30.glAttachShader(shaderProgram, vertexShader);
		GL30.glAttachShader(shaderProgram, fragShader);
		GL30.glLinkProgram(shaderProgram);
		GL20.glValidateProgram(shaderProgram);
		
		System.out.println("Shader Loader > " + GL30.glGetProgramInfoLog(shaderProgram));
		
		if(GL30.glGetProgrami(shaderProgram, GL30.GL_LINK_STATUS) == 0)
		{
			String log = GL30.glGetProgramInfoLog(shaderProgram);
			System.err.println("Shader Program Linking Error!!!");
			System.err.print(log);
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
		window = new Window(1024, 720, "wack simulator 2020");
		
		int shaderProgram = loadShaders();
		
		Structure s = new Structure(new Transform(Maths.zero()), 16 * 2, 16 * 2, 16 * 2);
		
		s.transform().translate(new Vector3f(-s.width() / 2, -s.height() / 2, -s.length() / 2));
		
		Random rdm = new Random();
		
		for(int z = 0; z < s.length(); z++)
		{
			for(int x = 0; x < s.width(); x++)
			{
				int h = s.height() - rdm.nextInt(2);
				for(int y = 0; y < h; y++)
				{
					if(y == h - 1)
						s.block(x, y, z, Blocks.GRASS);
					else if(h - y < 5)
						s.block(x, y, z, Blocks.DIRT);
					else
						s.block(x, y, z, Blocks.STONE);
				}
			}
		}
		
		for(Chunk c : s.chunks())
			c.render();
		
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
		
		while(!window.shouldClose())
		{
			update();

			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			GL11.glEnable(GL13.GL_TEXTURE0);
			
			render();
			
			GL30.glUseProgram(shaderProgram);
			GL20.glUniform1f(timeLoc, (float)GLFW.glfwGetTime());
			tex.bind();
			
			GL20.glUniformMatrix4fv(projLoc, false, projectionMatrix.get(floatBuf));
			
			if(Input.isKeyDown(GLFW.GLFW_KEY_W))
				pos.z -= 0.1f;
			if(Input.isKeyDown(GLFW.GLFW_KEY_S))
				pos.z += 0.1f;
			if(Input.isKeyDown(GLFW.GLFW_KEY_D))
				pos.x += 0.1f;
			if(Input.isKeyDown(GLFW.GLFW_KEY_A))
				pos.x -= 0.1f;
			if(Input.isKeyDown(GLFW.GLFW_KEY_E))
				pos.y += 0.1f;
			if(Input.isKeyDown(GLFW.GLFW_KEY_Q))
				pos.y -= 0.1f;
			
			if(Input.isKeyDown(GLFW.GLFW_KEY_C))
				rot.y += 0.01f;
			if(Input.isKeyDown(GLFW.GLFW_KEY_Z))
				rot.y -= 0.01f;
			
			if(Input.isKeyDown(GLFW.GLFW_KEY_R))
				rot.x += 0.01f;
			if(Input.isKeyDown(GLFW.GLFW_KEY_T))
				rot.x -= 0.01f;
			
			Maths.createViewMatrix(pos, rot, cameraMatrix);
			
			GL20.glUniformMatrix4fv(camLoc, false, cameraMatrix.get(floatBuf));
			
			GL30.glEnable(GL30.GL_DEPTH_TEST);
			GL30.glDepthFunc(GL30.GL_LESS);
			
			for(Chunk chunk : s.chunks())
			{
				Matrix4f transform = new Matrix4f();
				Matrix4fc t = s.transformMatrix();
				t.mul(chunk.transformMatrix(), transform);
				
				GL20.glUniformMatrix4fv(transLoc, false, transform.get(floatBuf));
				
				GL30.glBindVertexArray(chunk.mesh().vao());
				GL20.glEnableVertexAttribArray(0);
				GL20.glEnableVertexAttribArray(1);
				GL20.glEnableVertexAttribArray(2);
				GL11.glDrawElements(GL20.GL_TRIANGLES, chunk.mesh().verticies(), GL11.GL_UNSIGNED_INT, 0);
				GL20.glDisableVertexAttribArray(2);
				GL20.glDisableVertexAttribArray(1);
				GL20.glDisableVertexAttribArray(0);
				GL30.glBindVertexArray(0);
			}
			
			Texture.unbind();
			
			window.update();

		}
		
		window.destroy();
	}
	
	private void update()
	{
	}
	
	private void render()
	{
		window.clear(33 / 255.0f, 33 / 255.0f, 33 / 255.0f, 1.0f);
		
		// Render Code
	}
}
