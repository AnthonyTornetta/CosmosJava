package com.cornchipss.cosmos.rendering;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class Window
{
	private static long window;
	private int width, height;
	
	private boolean wasWindowResized = false;
	
	private static Window instance;
	
	public boolean wasWindowResized()
	{
		boolean ret = wasWindowResized;
		wasWindowResized = false;
		return ret;
	}
	
	public Window(int w, int h, String title)
	{
		instance = this;
		
		this.width = w;
		this.height = h;
		
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();
		
		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");
		
		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

		// Create the window
		window = glfwCreateWindow(w, h, title, NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");
		
		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		
		GLFW.glfwSetWindowSizeCallback(window, new GLFWWindowSizeCallbackI()
		{
			@Override
			public void invoke(long window, int width, int height)
			{
				wasWindowResized = true;
				
//				if(width / (float)height > ogAspectRatio)
//				{
//					float desiredHeight = (width / ogAspectRatio);
//					viewportOffY = -Math.round((desiredHeight - height) / 2) / 2;
//				}
//				else
//				{
//					float desiredWidth = (height * ogAspectRatio);
//					viewportOffX = -Math.round((desiredWidth - width) / 2) / 2;
//				}
				
//				GL20.glViewport(viewportOffX, viewportOffY, width - viewportOffX, height - viewportOffY);
				
//				Utils.println(width + ", " + height);
//				Utils.println(getWidth() + ", " + getHeight());
				
				instance().width = width;
				instance().height = height;
				
				GL20.glViewport(0, 0, width, height);
			}
		});
		
		// Make the window visible
		glfwShowWindow(window);
		
		GL.createCapabilities();
	}
	
	private int viewportOffX, viewportOffY;
	
	public int viewportOffsetY()
	{
		return viewportOffY;
	}
	
	public int viewportOffsetX()
	{
		return viewportOffX;
	}
	
	public int viewportWidth()
	{
		return getWidth() - viewportOffsetX();
	}
	
	public int viewportHeight()
	{
		return getHeight() - viewportOffsetY();
	}
	
	public void update()
	{
		glfwPollEvents();
		glfwSwapBuffers(window);
	}

	public boolean shouldClose()
	{
		return glfwWindowShouldClose(window);
	}
	
	public void destroy()
	{
		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	public void clear(float r, float g, float b, float a)
	{
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(r, g, b, a);
	}
	
	public long getId() { return window; }
	
	public int getWidth() { return width; }
	public int getHeight() { return height; }

	public static Window instance()
	{
		return instance;
	}
}
