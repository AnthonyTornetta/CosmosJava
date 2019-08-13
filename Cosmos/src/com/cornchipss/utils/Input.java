package com.cornchipss.utils;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;

import com.cornchipss.rendering.Window;

public class Input implements GLFWKeyCallbackI, GLFWMouseButtonCallbackI
{
	private static Window window;
	
	private static Input instance;
	
	private static boolean[] keysDown = new boolean[GLFW.GLFW_KEY_LAST + 1];
	private static boolean[] keysJustDown = new boolean[GLFW.GLFW_KEY_LAST + 1];
	
	private static boolean[] mouseButtonsDown = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST + 1];
	private static boolean[] mouseBtnsJustDown = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST + 1];
	
	private static List<Integer> keysPressed = new LinkedList<>();
	private static List<Integer> mouseButtonsJustPressed = new LinkedList<>();
	
	private static final Mouse mouse = new Mouse();
	
	private static class Mouse // No getters/setters because it's a struct
	{
		float x, y, deltaX, deltaY;
	}
	
	private Input()
	{
		
	}
	
	public static void update()
	{
		for(int i : keysPressed)
			keysJustDown[i] = false;
		keysPressed.clear();
		
		for(int i : mouseButtonsJustPressed)
			mouseBtnsJustDown[i] = false;
		mouseButtonsJustPressed.clear();
		
		double[] mouseX = new double[1];
		double[] mouseY = new double[1];
		GLFW.glfwGetCursorPos(window.getId(), mouseX, mouseY);
		mouse.deltaX = (float) (mouse.x - mouseX[0]);
		mouse.deltaY = (float) (mouse.y - mouseY[0]);
		
		mouse.x = (float)mouseX[0];
		mouse.y = (float)mouseY[0];
	}
	
	public static void setWindow(Window window)
	{
		if(Input.window != null)
			throw new IllegalStateException("Input handler already initialized to a window!");
		
		Input.window = window;
		
		instance = new Input();
		
		GLFW.glfwSetKeyCallback(window.getId(), instance);
		GLFW.glfwSetMouseButtonCallback(window.getId(), instance);
	}

	@Override
	public void invoke(long window, int key, int arg2, int action, int arg4)
	{
		if(key == GLFW.GLFW_KEY_UNKNOWN)
			return;
		
		if(action == GLFW.GLFW_PRESS)
		{
			keysDown[key] = true;
			keysJustDown[key] = true;
			keysPressed.add(key);
		}
		else if(action == GLFW.GLFW_RELEASE)
			keysDown[key] = false;
		else if(action == GLFW.GLFW_REPEAT)
		{
			
		}
	}
	
	@Override
	public void invoke(long window, int mouseBtn, int action, int arg3)
	{
		if(mouseBtn == GLFW.GLFW_KEY_UNKNOWN)
			return;
		
		if(action == GLFW.GLFW_PRESS)
		{
			mouseButtonsDown[mouseBtn] = true;
			mouseBtnsJustDown[mouseBtn] = true;
			mouseButtonsJustPressed.add(mouseBtn);
		}
		else
			mouseButtonsDown[mouseBtn] = false;
	}
	
	@Override
	public String getSignature()
	{
		return GLFWKeyCallbackI.super.getSignature();
	}

	@Override
	public void callback(long window)
	{
		GLFWKeyCallbackI.super.callback(window);
	}

	public static boolean isKeyDown(int key)
	{
		return keysDown[key];
	}
	
	public static boolean isMouseBtnDown(int mouseBtn)
	{
		return mouseButtonsDown[mouseBtn];
	}
	
	public static void hideCursor(boolean hide)
	{
		if(hide)
		{
			GLFW.glfwSetInputMode(window.getId(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
		}
		else
		{
			GLFW.glfwSetInputMode(window.getId(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
		}
	}
	
	public static float getMouseX() { return mouse.x; }
	public static float getMouseY() { return mouse.y; }
	public static float getMouseDeltaX() { return mouse.deltaX; }
	public static float getMouseDeltaY() { return mouse.deltaY; }
}
