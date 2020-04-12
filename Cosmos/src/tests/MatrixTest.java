package tests;

import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.cornchipss.utils.Maths;
import com.cornchipss.utils.Utils;

public class MatrixTest
{
	public static void main(String[] args)
	{
		Matrix4f mat = new Matrix4f();
		
		mat.identity();
		
		mat.rotate(new AxisAngle4f(Maths.PI, 1, 0, 0));
		
		Utils.println(mat.getEulerAnglesZYX(new Vector3f()));
	}
}
