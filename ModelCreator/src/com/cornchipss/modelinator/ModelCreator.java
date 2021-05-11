package com.cornchipss.modelinator;

import java.io.IOException;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import com.cornchipss.cosmos.cameras.Camera;
import com.cornchipss.cosmos.cameras.GimbalLockCamera;
import com.cornchipss.cosmos.material.Material;
import com.cornchipss.cosmos.material.RawImageMaterial;
import com.cornchipss.cosmos.models.Model;
import com.cornchipss.cosmos.models.ModelLoader;
import com.cornchipss.cosmos.physx.Transform;
import com.cornchipss.cosmos.rendering.Mesh;
import com.cornchipss.cosmos.rendering.Window;
import com.cornchipss.cosmos.utils.GameLoop;
import com.cornchipss.cosmos.utils.io.Input;

public class ModelCreator
{
	private int renderMode = 0;
	
	public ModelCreator() throws IOException
	{
		Window window = new Window(1024, 720, "Model-Intator");
		Input.setWindow(window);
		Input.update();
		
		Model playerModel = ModelLoader.fromFile("assets/models/player");
		
		Mesh m = playerModel.createMesh(0, 0, 0, 1);
		
		Material mat = new RawImageMaterial("assets/images/atlas/player");
		mat.init();
		
		Matrix4fc identityMatrix = new Matrix4f().identity();
		
		Transform trans = new Transform();
		
		Matrix4f projMatrix = new Matrix4f();
		projMatrix.perspective((float)Math.toRadians(90), 
				window.getWidth() / (float)window.getHeight(),
				0.1f, 1000);
				
		GimbalLockCamera cam = new GimbalLockCamera(trans);
		
		GameLoop loop = new GameLoop((float delta) ->
		{
			window.clear(0, 0.2f, 0.5f, 1);
			
			handleMovements(cam, trans, delta);
			
			GL11.glEnable(GL13.GL_TEXTURE0);
			
			GL30.glEnable(GL30.GL_DEPTH_TEST);
			GL30.glDepthFunc(GL30.GL_LESS);
			
			if(Input.isKeyJustDown(GLFW.GLFW_KEY_F3))
				renderMode = (renderMode + 1) % 3;
			
			if(renderMode == 1)
				GL30.glPolygonMode(GL30.GL_FRONT_AND_BACK, GL30.GL_LINE);
			else if(renderMode == 0)
				GL30.glPolygonMode(GL30.GL_FRONT_AND_BACK, GL30.GL_FILL);
			else
				GL30.glPolygonMode(GL30.GL_FRONT_AND_BACK, GL30.GL_POINT);
			
			Input.update();
			
			mat.use();
			
			cam.update();
			mat.initUniforms(projMatrix, cam.viewMatrix(), identityMatrix, true);
			
			m.prepare();
			m.draw();
			m.finish();
			
			mat.stop();
			
			window.update();
			
			if(Input.isKeyDown(GLFW.GLFW_KEY_ESCAPE))
				return false;
			
			return !window.shouldClose();
		}, 1000 / 60);
		
		loop.run();
		
		m.delete();
		
		window.destroy();
	}
	
	public static void main(String[] args) throws IOException
	{
		new ModelCreator();
	}
	
	private static void handleMovements(Camera camera, Transform trans, float delta)
	{
		Vector3f dVel = new Vector3f();
	    
		if(Input.isKeyDown(GLFW.GLFW_KEY_W))
			dVel.add(camera.forward());
		if(Input.isKeyDown(GLFW.GLFW_KEY_S))
			dVel.sub(camera.forward());
		if(Input.isKeyDown(GLFW.GLFW_KEY_D))
			dVel.add(camera.right());
		if(Input.isKeyDown(GLFW.GLFW_KEY_A))
			dVel.sub(camera.right());
		if(Input.isKeyDown(GLFW.GLFW_KEY_E))
			dVel.add(camera.up());
		if(Input.isKeyDown(GLFW.GLFW_KEY_Q))
			dVel.sub(camera.up());
		
		dVel.x = dVel.x() * delta;
		dVel.z = dVel.z() * delta;
		dVel.y = dVel.y() * delta;
		
		if(Input.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT))
			dVel.mul(0.1f);
		
		if(Input.isMouseBtnDown(GLFW.GLFW_MOUSE_BUTTON_RIGHT))
		{
			Input.hideCursor(true);
			Vector3f dRot = new Vector3f();
			
			dRot.y = (dRot.y() - Input.getMouseDeltaX() * 0.001f);
			
			dRot.x = (dRot.x() - Input.getMouseDeltaY() * 0.001f);
			
			camera.rotate(dRot);
		}
		else
			Input.hideCursor(false);
//		vel = Maths.safeNormalize(dVel, 5.0f);
		
		trans.position(trans.position().add(dVel, dVel));
	}
}
