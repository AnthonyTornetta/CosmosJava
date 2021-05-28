package com.cornchipss.modelinator;

import java.awt.Color;
import java.io.IOException;

import org.joml.Intersectionf;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import com.cornchipss.cosmos.cameras.Camera;
import com.cornchipss.cosmos.cameras.GimbalLockCamera;
import com.cornchipss.cosmos.gui.GUI;
import com.cornchipss.cosmos.gui.GUIRectangle;
import com.cornchipss.cosmos.gui.interactable.GUITextBox;
import com.cornchipss.cosmos.gui.interactable.GUIScrollBox;
import com.cornchipss.cosmos.gui.measurement.MeasurementPair;
import com.cornchipss.cosmos.gui.measurement.PercentMeasurement;
import com.cornchipss.cosmos.gui.measurement.PixelMeasurement;
import com.cornchipss.cosmos.gui.measurement.SubtractedMeasurement;
import com.cornchipss.cosmos.gui.text.Fonts;
import com.cornchipss.cosmos.gui.text.GUIText;
import com.cornchipss.cosmos.material.Material;
import com.cornchipss.cosmos.material.Materials;
import com.cornchipss.cosmos.material.RawImageMaterial;
import com.cornchipss.cosmos.models.LoadedModel;
import com.cornchipss.cosmos.models.ModelLoader;
import com.cornchipss.cosmos.physx.Transform;
import com.cornchipss.cosmos.rendering.Mesh;
import com.cornchipss.cosmos.rendering.Window;
import com.cornchipss.cosmos.utils.GameLoop;
import com.cornchipss.cosmos.utils.Maths;
import com.cornchipss.cosmos.utils.io.Input;

public class ModelCreator
{
	private int renderMode = 0;
	
	private GUIText textSelectedName;
	
	private GUITextBox txtBox;
	
	public ModelCreator() throws IOException
	{
		Window window = new Window(1024, 720, "Model-Intator");
		Input.setWindow(window);
		Input.update();
		
		Materials.initMaterials();
		Fonts.init();
		
		LoadedModel playerModel = ModelLoader.fromFile("assets/models/player");
		
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
		
		final Color GREY = new Color(43+20, 43+20, 43+20);
		final Color LIGHTER_GREY = new Color(43+40, 43+40, 43+40);
		
		GUI gui = new GUI(Materials.GUI_MATERIAL);
		gui.init(0, 0, window.getWidth(), window.getHeight());
		
		GUIRectangle rect = new GUIRectangle(
					new MeasurementPair(
				PixelMeasurement.ZERO, 
				PixelMeasurement.ZERO), 
					new MeasurementPair(
				PercentMeasurement.ONE, 
				new PixelMeasurement(100)), GREY);
		
		GUIRectangle rect2 = new GUIRectangle(
				new MeasurementPair(
					PixelMeasurement.ZERO, 
					PixelMeasurement.ZERO), 
				new MeasurementPair(
						new PixelMeasurement(205), 
						PercentMeasurement.ONE), 
				LIGHTER_GREY);
		
		GUIRectangle rect3 = new GUIRectangle(
				new MeasurementPair(
					PixelMeasurement.ZERO, 
					PixelMeasurement.ZERO), 
				new MeasurementPair(
					PercentMeasurement.ONE, 
					new PixelMeasurement(105)), 
				LIGHTER_GREY);
		
		textSelectedName = new GUIText("", Fonts.ARIAL_28, 
				new MeasurementPair( 
					new PixelMeasurement(10),
					new SubtractedMeasurement(
							PercentMeasurement.ONE,
							new PixelMeasurement(Fonts.ARIAL_28.height() + 10))
					));
		
		txtBox = new GUITextBox(
				new MeasurementPair(
					PixelMeasurement.ZERO, 
					PixelMeasurement.ZERO), 
				new MeasurementPair(
					new PixelMeasurement(30), 
					new PixelMeasurement(Fonts.ARIAL_8.height())),
				Fonts.ARIAL_8);
		
		GUIScrollBox box = new GUIScrollBox(new MeasurementPair
				(PixelMeasurement.ZERO, PixelMeasurement.ZERO),
				new MeasurementPair(
						new PixelMeasurement(200), 
						PercentMeasurement.ONE),
				GREY);
		
		box.addChild(textSelectedName);
		
		gui.addElement(rect2, rect3, rect, txtBox, box);
		
		GameLoop loop = new GameLoop((float delta) ->
		{
			window.clear(0.17f, 0.17f, 0.17f, 1);
			
			if(window.wasWindowResized())
			{
				projMatrix.identity();
				projMatrix.perspective((float)Math.toRadians(90), 
						window.getWidth() / (float)window.getHeight(),
						0.1f, 1000);
				
				gui.onResize(window.getWidth(), window.getHeight());
			}
			
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
			
			update(delta, playerModel, cam, projMatrix, window, trans);
			gui.update(delta);
			
			Input.update();
			
			mat.use();
			
			cam.update();
			mat.initUniforms(projMatrix, cam.viewMatrix(), identityMatrix, true);
			
			m.prepare();
			m.draw();
			m.finish();
			
			mat.stop();
			
			GL30.glPolygonMode(GL30.GL_FRONT_AND_BACK, GL30.GL_FILL);
			gui.draw();
			
			window.update();
			
			if(Input.isKeyDown(GLFW.GLFW_KEY_ESCAPE))
				return false;
			
			return !window.shouldClose();
		}, 1000 / 60);
		
		loop.run();
		
		m.delete();
		
		window.destroy();
	}
	
	private int processMouse(float delta, LoadedModel m, Camera cam, Matrix4fc projection,
			Window window)
	{
		Matrix4f projTotal = projection.mul(cam.viewMatrix(), new Matrix4f());
		
		int[] viewBounds = new int[]
				{
					window.viewportOffsetX(), window.viewportOffsetY(), 
					window.viewportWidth(), window.viewportHeight()
				};
		
		Vector4f from4 = projTotal.unproject(
				Input.getRelativeMouseX(), Input.getRelativeMouseY(), 0, viewBounds, new Vector4f());
		
		Vector4f to4 = projTotal.unproject(
				Input.getRelativeMouseX(), Input.getRelativeMouseY(), 1, viewBounds, new Vector4f());
		
		Vector3f from, to;
		
		from = new Vector3f(from4.x, from4.y, from4.z);
		to = new Vector3f(to4.x, to4.y, to4.z);
		
		float closestDist = 0;
		int startingIndex = -1;
		
		Vector3f pos1 = new Vector3f(), pos2 = new Vector3f(), pos3 = new Vector3f();
		Vector3f collisionAt = new Vector3f();
		
		for(int i = 0; i < m.indices().length; i += 3)
		{
			verticesAtIndex(i, pos1, pos2, pos3, m);
			
			if(Intersectionf.intersectLineSegmentTriangle(from, to,
					pos1, pos2, pos3, (float) 1E-9, collisionAt))
			{
				if(startingIndex == -1)
				{
					closestDist = Maths.distSqrd(collisionAt, from);
					startingIndex = i;
				}
				else
				{
					float distSqrd = Maths.distSqrd(collisionAt, from);
					if(distSqrd < closestDist)
					{
						closestDist = distSqrd;
						startingIndex = i;
					}
				}
			}
		}
		
		return startingIndex;
	}
	
	private void select(int[] indices)
	{
		txtBox.text(indices[0] + " - " + indices[indices.length - 1]);
	}
	
	private void verticesAtIndex(int i, Vector3f a, Vector3f b, Vector3f c, LoadedModel m)
	{
		a.set(m.vertices()[m.indices()[i]*3], 
				m.vertices()[m.indices()[i]*3 + 1], m.vertices()[m.indices()[i]*3 + 2]);
		
		b.set(m.vertices()[m.indices()[i+1]*3], 
				m.vertices()[m.indices()[i+1]*3 + 1], m.vertices()[m.indices()[i+1]*3 + 2]);
		
		c.set(m.vertices()[m.indices()[i+2]*3], 
				m.vertices()[m.indices()[i+2]*3 + 1], m.vertices()[m.indices()[i+2]*3 + 2]);
	}
	
	private void update(float delta, LoadedModel m, Camera cam, Matrix4fc projection,
			Window window, Transform transform)
	{
		if(Input.isMouseBtnJustDown(GLFW.GLFW_MOUSE_BUTTON_LEFT))
		{
			int res = processMouse(delta, m, cam, projection, window);
			
			if(res != -1)
			{
				String group = m.groupContaining(res);
				textSelectedName.text(group);
				
				int[] indices = m.indicesForGroup(group);
				
				select(indices);
			}
		}
		
		handleMovements(cam, transform, delta);
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
