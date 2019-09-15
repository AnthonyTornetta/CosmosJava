import org.joml.Vector3d;
import org.joml.Vector3f;

import com.cornchipss.utils.Utils;

public class asdf
{
	public static void main(String[] args)
	{
		Vector3f position = new Vector3f(0, 0, 0);
		final float d = 10;
		
		float rx = 0;
		float ry = 30;
		
		Vector3d endPoint = new Vector3d();
		
		final double j = d * Math.cos(Math.toRadians(rx));
		
		endPoint.x = position.x + j * Math.cos(Math.toRadians(ry));
		endPoint.y = position.y + d * Math.sin(Math.toRadians(rx));
		endPoint.z = position.z + j * Math.sin(Math.toRadians(ry));
		
		System.out.println(Utils.toString(endPoint));
	}
}
