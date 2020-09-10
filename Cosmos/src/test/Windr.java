package test;

import java.awt.Color;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

public class Windr
{
	private long id;
	private int width, height;
	
	private Color clearColor;
	
	public Windr(int w, int h)
	{
		this.width = w;
		this.height = h;
		
		clearColor = new Color(33, 33, 33);
	}
	
	void create()
	{
		if(!GLFW.glfwInit())
		{
			System.err.println("Unable to create GLFW initialization");
			return;
		}
		
		GLFW.glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));
		
		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
		
		id = GLFW.glfwCreateWindow(width, height, "Hello, World!", 0, 0);
		
		if(id == 0)
		{
			System.err.println("Unable to create window!");
			return;
		}
		
		GLFWVidMode mode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
		
		GLFW.glfwSetWindowPos(id, mode.width() / 2 - width / 2, mode.height() / 2 - height / 2);
		
		GLFW.glfwMakeContextCurrent(id);
		
		GLFW.glfwSwapInterval(1); // Disables V-Sync
		
		GLFW.glfwShowWindow(id);
	}
	
	/**
	 * Initializes opengl on the thread this is called on
	 */
	void init()
	{
		GLFW.glfwInit();
		
		GL.createCapabilities();
		
		GL11.glClearColor(
				clearColor.getRed() / 255.0f, 
				clearColor.getGreen() / 255.0f, 
				clearColor.getBlue() / 255.0f, 
				clearColor.getAlpha() / 255.0f);
	}
	
	void clearWindow()
	{
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}
	
	void update()
	{
		GLFW.glfwSwapBuffers(id);
		GLFW.glfwPollEvents();
	}
	
	boolean shouldClose()
	{
		return GLFW.glfwWindowShouldClose(id);
	}
	
	public void close(boolean close)
	{
		GLFW.glfwSetWindowShouldClose(id, close);

		if(close)
		{
			GLFW.glfwDestroyWindow(id);
			GLFW.glfwTerminate();
			GLFW.glfwSetErrorCallback(null).free();
		}
	}
}
