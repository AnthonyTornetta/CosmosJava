package test;

import java.io.BufferedReader;
import java.io.FileReader;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.cornchipss.rendering.Window;
import com.cornchipss.utils.Input;
import com.cornchipss.utils.Maths;
import com.cornchipss.utils.Utils;

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
			br = new BufferedReader(new FileReader("./assets/shaders/test.vert"));
			
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
			br = new BufferedReader(new FileReader("./assets/shaders/test.frag"));
			
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
		window = new Window(1024, 720, "mgay");
		
		int shaderProgram = loadShaders();
				
//		float[] vertices = {// first triangle
//			     0.5f,  0.5f, 0.0f,  // top right
//			     0.5f, -0.5f, 0.0f,  // bottom right
//			    -0.5f,  0.5f, 0.0f,  // top left 
//			    -0.5f, -0.5f, 0.0f,  // bottom left
//		};
		
		float[] vertices = {// first triangle
				// front
			    -0.5f, -0.5f,  0.5f,
			     0.5f, -0.5f,  0.5f,
			     0.5f,  0.5f,  0.5f,
			    -0.5f,  0.5f,  0.5f,
			    // back
			    -0.5f, -0.5f, -0.5f,
			     0.5f, -0.5f, -0.5f,
			     0.5f,  0.5f, -0.5f,
			    -0.5f,  0.5f, -0.5f
		};
		
		int[] indices = 
			{
					// front
					0, 1, 2,
					2, 3, 0,
					// right
					1, 5, 6,
					6, 2, 1,
					// back
					7, 6, 5,
					5, 4, 7,
					// left
					4, 0, 3,
					3, 7, 4,
					// bottom
					4, 5, 1,
					1, 0, 4,
					// top
					3, 2, 6,
					6, 7, 3
			};
		
		float[] cols = 
			{
				1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f,
				1.0f, 0.0f, 0.0f,
				0.0f, 1.0f, 0.0f,
				0.0f, 0.0f, 1.0f,
				1.0f, 1.0f, 0,0f
			};
		BulkModel beeg = new BulkModel("XXXXXXXXXXXXX".length(), 5, 5);
		
		beeg.parse("XXXXXXXXXXXXX\n"
				+  "X           X\n"
				+  "X XXXXXXXXX X\n"
				+  "X           X\n"
				+  "XXXXXXXXXXXXX\n", 0);
//		
		
		beeg.parse("XXXXXXXXXXXXX\n"
				+  "X           X\n"
				+  "X XXXXXXXXX X\n"
				+  "X           X\n"
				+  "XXXXXXXXXXXXX\n", 1);
		
		beeg.parse("XXXXXXXXXXXXX\n"
				+  "X           X\n"
				+  "X XXXXXXXXX X\n"
				+  "X           X\n"
				+  "XXXXXXXXXXXXX\n", 2);
		
		beeg.parse("XXXXXXXXXXXXX\n"
				+  "X           X\n"
				+  "X XXXXXXXXX X\n"
				+  "X           X\n"
				+  "XXXXXXXXXXXXX\n", 3);
		
		beeg.parse("XXXXXXXXXXXXX\n"
				+  "X           X\n"
				+  "X XXXXXXXXX X\n"
				+  "X           X\n"
				+  "XXXXXXXXXXXXX\n", 4);
//		
//		beeg.parse("XXXXXXXXXXXXX\n"
//				+  "XXXXXXXXXXXXX\n"
//				+  " XXXX XXXX XX\n"
//				+  " XX      X XX\n"
//				+  " XXXX X     X\n", 2);
//		
//		beeg.parse("XXXXXXXXXXXXX\n"
//				+  "XXXXXXXXXXXXX\n"
//				+  " XXXX XXXX XX\n"
//				+  " XX      X XX\n"
//				+  " XXXX X     X\n", 3);
//		
//		beeg.parse("XXXXXXXXXXXXX\n"
//				+  "XXXXXXXXXXXXX\n"
//				+  " XXXX XXXX XX\n"
//				+  " XX      X XX\n"
//				+  " XXXX X     X\n", 4);
		
		
		beeg.render();
		
		
		int timeLoc = GL20.glGetUniformLocation(shaderProgram, "time");
		int camLoc = GL20.glGetUniformLocation(shaderProgram, "u_camera");
		int transLoc = GL20.glGetUniformLocation(shaderProgram, "u_transform");
		int projLoc = GL20.glGetUniformLocation(shaderProgram, "u_proj");
		
		Matrix4f cameraMatrix = new Matrix4f();
		
		Matrix4f meshMatrix = new Matrix4f();
		
		meshMatrix.translate(new Vector3f(0, 0, 2f));
		
		Matrix4f projectionMatrix = new Matrix4f();
		projectionMatrix.perspective((float)Math.toRadians(90), 
				1024/720.0f,
				0.1f, 1000);

		Utils.println(projectionMatrix);
		
		Input.setWindow(window);
		
		Vector3f pos = new Vector3f();
		
		while(!window.shouldClose())
		{
			update();

			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			render();
								
			GL30.glUseProgram(shaderProgram);
			GL20.glUniform1f(timeLoc, (float)GLFW.glfwGetTime());
			
			GL20.glUniformMatrix4fv(projLoc, false, projectionMatrix.get(new float[16]));
			
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
			
			Maths.createViewMatrix(pos, new Vector3f(0, 0, 0), cameraMatrix);
			
			GL20.glUniformMatrix4fv(camLoc, false, cameraMatrix.get(new float[16]));
			
			GL20.glUniformMatrix4fv(transLoc, false, meshMatrix.get(new float[16]));
			
			GL30.glEnable(GL30.GL_DEPTH_TEST);
			GL30.glDepthFunc(GL30.GL_LESS);
			
			for(Mesh mesh : beeg.meshes)
			{
				GL30.glBindVertexArray(mesh.vao());
				GL20.glEnableVertexAttribArray(0);
				GL20.glEnableVertexAttribArray(1);
				GL11.glDrawElements(GL20.GL_TRIANGLES, mesh.verticies(), GL11.GL_UNSIGNED_INT, 0);
				GL20.glDisableVertexAttribArray(1);
				GL20.glDisableVertexAttribArray(0);
				GL30.glBindVertexArray(0);
			}
			
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
