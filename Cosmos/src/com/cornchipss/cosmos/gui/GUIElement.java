package com.cornchipss.cosmos.gui;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;

import com.cornchipss.cosmos.gui.measurement.MeasurementPair;
import com.cornchipss.cosmos.material.TexturedMaterial;
import com.cornchipss.cosmos.material.Materials;
import com.cornchipss.cosmos.rendering.Mesh;
import com.cornchipss.cosmos.rendering.Window;
import com.cornchipss.cosmos.utils.Maths;
import com.cornchipss.cosmos.utils.io.Input;

public abstract class GUIElement
{
	protected Matrix4f transform;
	private MeasurementPair position;
	private MeasurementPair dimensions;
	private Vector3f rotation;
	private float scale;

	private Vector3f positionVector;

	protected void createMatrix()
	{
		positionVector.set(
			position.x().actualValue(Window.instance().getWidth()),
			position.y().actualValue(Window.instance().getHeight()), 0);

		Maths.createTransformationMatrix(positionVector, rotation.x, rotation.y,
			rotation.z, scale, transform);
	}

	public GUIElement(MeasurementPair position, MeasurementPair dimensions,
		float rx, float ry, float rz, float scale)
	{
		this.position = position;
		this.rotation = new Vector3f(rx, ry, rz);
		this.dimensions = dimensions;
		this.scale = scale;
		transform = new Matrix4f();
		positionVector = new Vector3f();

		createMatrix();
	}

	public GUIElement(MeasurementPair position, MeasurementPair dimensions)
	{
		this(position, dimensions, 0, 0, 0, 1);
	}

	public boolean hovered()
	{
		float width = Window.instance().getWidth();
		float height = Window.instance().getHeight();

		float minX = position().x().actualValue(width);
		float minY = position().y().actualValue(height);
		float maxX = position().x().actualValue(width)
			+ this.dimensions.x().actualValue(width);
		float maxY = position().y().actualValue(height)
			+ this.dimensions.y().actualValue(height);

		float mouseX = Input.getRelativeMouseX();
		float mouseY = Input.getRelativeMouseY();

		return mouseX >= minX && mouseY >= minY && mouseX <= maxX
			&& mouseY <= maxY;
	}

	public void onResize(float w, float h)
	{
		createMatrix();
	}

	public GUIElement(MeasurementPair position, MeasurementPair dimensions,
		float scale)
	{
		this(position, dimensions, 0, 0, 0, scale);
	}

	/**
	 * If this is true, no attempt to draw this element or its children will be
	 * made.
	 * 
	 * @return
	 */
	public boolean hidden()
	{
		return false;
	}

	/**
	 * If this is false, no attempt to draw this element will be made. Its
	 * children will go through the normal drawing steps
	 * 
	 * @return
	 */
	public boolean canBeDrawn()
	{
		return true;
	}

	public void fullDraw(GUI gui, Matrix4fc projectionMatrix,
		Matrix4fc cameraMatrix)
	{
		material().use();

		material().initUniforms(projectionMatrix, cameraMatrix, transform(),
			true);

		prepare(gui);
		draw(gui);
		finish(gui);

		material().stop();
	}

	public void prepare(GUI gui)
	{
		guiMesh().prepare();
	}

	public void draw(GUI gui)
	{
		guiMesh().draw();
	}

	public void finish(GUI gui)
	{
		guiMesh().finish();
	}

	public abstract Mesh guiMesh();

	public Matrix4fc transform()
	{
		return transform;
	}

	public void delete()
	{
		guiMesh().delete();
	}

	public TexturedMaterial material()
	{
		return Materials.GUI_MATERIAL;
	}

	public MeasurementPair position()
	{
		return position;
	}

	public void position(MeasurementPair position)
	{
		this.position = position;
		createMatrix();
	}

	public MeasurementPair dimensions()
	{
		return dimensions;
	}

	public void dimensions(MeasurementPair p)
	{
		this.dimensions = p;
	}

	public void updateTransform()
	{
		createMatrix();
	}
}
