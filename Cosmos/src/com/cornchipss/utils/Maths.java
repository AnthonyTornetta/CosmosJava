package com.cornchipss.utils;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

public class Maths
{
	/**
	 * A float of Math.PI
	 */
	public static final float PI = (float)Math.PI;
	
	/**
	 * Maths.PI * 2
	 */
	public static final float TAU = PI * 2;
	
	/**
	 * Maths.PI / 2
	 */
	public static final float PI2 = PI / 2;	
	
	/**
	 * Creates a view matrix based on coordinates + rotations
	 * @param x X
	 * @param y Y
	 * @param z Z
	 * @param rx Rotation X
	 * @param ry Rotation Y
	 * @param rz Rotation Z
	 * @param dest The destiantion matrix
	 */
	public static void createViewMatrix(float x, float y, float z, float rx, float ry, float rz, Matrix4f dest)
	{
		dest.identity();
		
		dest.rotate(rx, 1, 0, 0);
		dest.rotate(ry, 0, 1, 0);
		dest.rotate(rz, 0, 0, 1);
		
		dest.translate(-x, -y, -z);
	}
	
	/**
	 * Creates a view matrix based on coordinates + rotations
	 * @param pos Position
	 * @param rot Rotation
	 * @param dest The destiantion matrix
	 */
	public static void createViewMatrix(Vector3fc pos, Vector3fc rot, Matrix4f dest)
	{
		createViewMatrix(pos.x(), pos.y(), pos.z(), rot.x(), rot.y(), rot.z(), dest);
	}
	
	public static Matrix4f createTransformationMatrix(Vector3f pos, float rx, float ry, float rz)
	{
		return createTransformationMatrix(pos, rx, ry, rz, 1);
	}
	
	public static Matrix4f createTransformationMatrix(Vector3f pos, float rx, float ry, float rz, float scale)
	{
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.translate(pos);
        matrix.rotate(rx, new Vector3f(1,0,0));
        matrix.rotate(ry, new Vector3f(0,1,0));
        matrix.rotate(rz, new Vector3f(0,0,1));
        matrix.scale(new Vector3f(scale, scale, scale));
        return matrix;
    }
	
	public static Matrix4f createRotationMatrix(Quaternionfc q)
	{
		return Maths.createCombinedRotationMatrix(q.getEulerAnglesXYZ(new Vector3f()));
	}
	
	public static Matrix4f createRotationMatrix(Vector3fc axis, float angle)
	{
		/*
		 * https://open.gl/transformations
		 */
		
		if(axis.y() != 0)
			angle -= Maths.PI / 2; // OpenGL is funny for whatever reason
		
	    float s = sin(angle);
	    float c = cos(angle);
	    float oc = 1.0f - c;
	    
	    return new Matrix4f(oc * axis.x() * axis.x() + c,           oc * axis.x() * axis.y() - axis.z() * s,  oc * axis.z() * axis.x() + axis.y() * s,  0.0f,
	                oc * axis.x() * axis.y() + axis.z() * s,  oc * axis.y() * axis.y() + c,           oc * axis.y() * axis.z() - axis.x() * s,  0.0f,
	                oc * axis.z() * axis.x() - axis.y() * s,  oc * axis.y() * axis.z() + axis.x() * s,  oc * axis.z() * axis.z() + c,           0.0f,
	                0.0f,                                0.0f,                                0.0f,                                1.0f);
	}
	
	public static Matrix4f createCombinedRotationMatrix(Vector3fc rotation)
	{
		return createRotationMatrix(Utils.x(), rotation.x()).mul(createRotationMatrix(Utils.y(), rotation.y()).mul(createRotationMatrix(Utils.z(), rotation.z())));
	}
	
	public static Vector3f getPositionActual(Vector3f pos, Matrix4f... rotations)
	{
		Matrix4f rotationFinal = new Matrix4f();
		rotationFinal.identity();
		
		for(Matrix4f rot : rotations)
			rotationFinal.mul(rot);
		
		Vector4f vec = new Vector4f(pos.x, pos.y, pos.z, 0).mul(rotationFinal);
		
		return new Vector3f(vec.x, vec.y, vec.z);
	}
	
	public static float cos(float theta)
	{
		return (float)Math.cos(theta);
	}
	
	public static float sin(float theta)
	{
		return (float)Math.sin(theta);
	}
	
	public static float tan(float theta)
	{
		return (float)Math.tan(theta);
	}
	
	public static Quaternionf blankQuaternion()
	{
		return new Quaternionf(0, 0, 0, 1);
	}
	
	public static Quaternionf quaternionFromRotation(float rx, float ry, float rz)
	{
		return new Quaternionf().rotateXYZ(rx, ry, rz);
	}
	
	public static Quaternionf quaternionFromRotation(Vector3fc rot)
	{
		return quaternionFromRotation(rot.x(), rot.y(), rot.z());
	}
	
	public static Vector3f rotatePoint(Vector3fc point, Vector3fc rotation)
	{
		Quaternionf transQuat = blankQuaternion();
		
		Vector3f punto = new Vector3f(point.x(), point.y(), point.z());
		rotation = mod(rotation, Maths.TAU);
		
		Quaternionf rotationQuat = blankQuaternion();
		
		rotationQuat.rotateXYZ(rotation.x(), rotation.y(), rotation.z(), transQuat);
		
		transQuat.transform(punto);
		
		return punto;
	}
	
	public static Vector3f rotatePoint(Matrix4f rotationMatrixX, Matrix4f rotationMatrixY, Matrix4f rotationMatrixZ, Vector3fc point)
	{
		return rotatePoint(rotationMatrixX, rotationMatrixX, rotationMatrixX, new Vector4f(point.x(), point.y(), point.z(), 0));
	}
	
	public static Vector3f rotatePoint(Matrix4f rotationMatrixX, Matrix4f rotationMatrixY, Matrix4f rotationMatrixZ, Vector4fc point)
	{
		return rotatePoint(Maths.mul(rotationMatrixX, rotationMatrixY).mul(rotationMatrixZ), point);
	}
	
	public static Vector3f rotatePoint(Matrix4f combinedRotation, Vector3fc point)
	{
		return rotatePoint(combinedRotation, new Vector4f(point.x(), point.y(), point.z(), 0));
	}
	
	public static Vector3f rotatePoint(Matrix4f combinedRotation, Vector4fc point)
	{
		Vector4f vec = new Vector4f(point).mul(combinedRotation);
		return new Vector3f(vec.x, vec.y, vec.z);
	}
	
	/**
	 * Calculates the ending point based off the starting position, rotation values, and the total distance
	 * @param start The starting point
	 * @param v The rotation (z is ignored)
	 * @param dist The total distance travelable
	 * @return The ending point
	 */
	public static Vector3f pointAt(Vector3fc start, Vector3fc v, float dist)
	{
		return add(toComponents(v.x(), v.y(), dist), start);
	}
	
	/**
	 * Calculates the ending point based off the starting position, rotation values, and the total distance
	 * @param start The starting point
	 * @param rx The x rotation
	 * @param ry The y rotation
	 * @param dist The total distance travelable
	 * @return The ending point
	 */
	public static Vector3f pointAt(Vector3fc start, float rx, float ry, float dist)
	{
		return add(toComponents(rx, ry, dist), start);
	}
	
	/**
	 * Calculates the ending point based off the starting position, rotation values, and the total distance
	 * @param rx The x rotation
	 * @param ry The y rotation
	 * @param dist The total distance travelable
	 * @return The ending point
	 */
	public static Vector3fc toComponents(float rx, float ry, float velMagnitude)
	{
		Vector3f components = new Vector3f();
		
		final double j = velMagnitude * Math.cos(rx);
		
		components.x = (float) (j * Math.sin(ry));
		components.y = (float) (-velMagnitude * Math.sin(rx));
		components.z = (float) (-j * Math.cos(ry));
		
		return components;
	}
	
	/**
	 * Adds two vectors without modifying either one
	 * @param a The first vector
	 * @param b The second vector
	 * @return A new vector of the two vectors added
	 */
	public static Vector3f add(Vector3fc a, Vector3fc b)
	{
		return new Vector3f(a.x() + b.x(), a.y() + b.y(), a.z() + b.z());
	}
	
	/**
	 * Adds vectors without modifying them
	 * @param vecs The vectors
	 * @return A new vector of two vectors added
	 */
	public static Vector3f add(Vector3fc... vecs)
	{
		Vector3f v = Maths.zero();
		
		for(Vector3fc c : vecs)
			v.add(c);
		
		return v;
	}
	
	public static Vector3f add(Vector3fc v, float x, float y, float z)
	{
		return new Vector3f(v.x() + x, v.y() + y, v.z() + z);
	}
	
	/**
	 * Subtracts two vectors without modifying either one
	 * @param a The first vector
	 * @param b The second vector
	 * @return A new vector of the two vectors subtracted
	 */
	public static Vector3f sub(Vector3fc a, Vector3fc b)
	{
		return new Vector3f(a.x() - b.x(), a.y() - b.y(), a.z() - b.z());
	}
	
	/**
	 * Subtracts a vector without modifying it
	 * @param a The first vector
	 * @param b The second scalor
	 * @return A new vector of the vector - scalor
	 */
	public static Vector3f sub(Vector3fc a, float s)
	{
		return new Vector3f(a.x() - s, a.y() - s, a.z() - s);
	}
	
	/**
	 * Subtracts vectors without modifying them
	 * @param vecs The vectors
	 * @return A new vector of two vectors subtracted
	 */
	public static Vector3f sub(Vector3fc... vecs)
	{
		Vector3f v = Maths.zero();
		
		for(Vector3fc c : vecs)
			v.sub(c);
		
		return v;
	}
	
	/**
	 * Multiplies two vectors without modifying either one
	 * @param a The first vector
	 * @param b The second vector
	 * @return A new vector of the two vectors multiplied
	 */
	public static Vector3f mul(Vector3fc a, Vector3fc b)
	{
		return new Vector3f(a.x() * b.x(), a.y() * b.y(), a.z() * b.z());
	}
	
	/**
	 * Multiplies vectors without modifying them
	 * @param vecs The vectors
	 * @return A new vector of two vectors multiplid
	 */
	public static Vector3f mul(Vector3fc... vecs)
	{
		if(vecs.length == 0)
			return Maths.zero();
		
		Vector3f v = Maths.one();
		
		for(Vector3fc c : vecs)
			v.mul(c);
		
		return v;
	}
	
	/**
	 * Multiplies two vectors without modifying either one
	 * @param x The first vector (<code>new Vector3f(x, x, x)</code>)
	 * @param b The second vector
	 * @return A new vector of the two vectors multiplied
	 */
	public static Vector3f mul(float x, Vector3fc a)
	{
		return mul(a, new Vector3f(x));
	}
	
	/**
	 * Divides two vectors without modifying either one
	 * @param a The first vector
	 * @param b The second vector
	 * @return A new vector of the two vectors divided
	 */
	public static Vector3f div(Vector3fc a, Vector3fc b)
	{
		return new Vector3f(a.x() / b.x(), a.y() / b.y(), a.z() / b.z());
	}
	
	/**
	 * Divides two vectors without modifying either one
	 * @param a The first vector
	 * @param b The second vector
	 * @return A new vector of the two vectors divided
	 */
	public static Vector3f div(Vector3fc a, float d)
	{
		return new Vector3f(a.x() / d, a.y() / d, a.z() / d);
	}
	
	/**
	 * Divides vectors without modifying them
	 * @param vecs The vectors
	 * @return A new vector of two vectors divided
	 */
	public static Vector3f div(Vector3fc... vecs)
	{
		if(vecs.length == 0)
			return Maths.zero();
		
		Vector3f v = Maths.one();
		
		for(Vector3fc c : vecs)
			v.div(c);
		
		return v;
	}
	
	/**
	 * Takes the modulus two vectors without modifying either one
	 * @param a The first vector
	 * @param b The second vector
	 * @return A new vector of the two vectors modulus'ed
	 */
	public static Vector3f mod(Vector3fc a, Vector3fc b)
	{
		return new Vector3f(a.x() % b.x(), a.y() % b.y(), a.z() % b.z());
	}
	
	/**
	 * Takes the modulus two vectors without modifying either one
	 * @param a The first vector
	 * @param b The scalar
	 * @return A new vector of the two vectors modulus'ed
	 */
	public static Vector3f mod(Vector3fc a, float b)
	{
		return new Vector3f(a.x() % b, a.y() % b, a.z() % b);
	}
	
	/**
	 * A Vector3f with all values being 0
	 * @return a Vector3f with all values being 0
	 */
	public static Vector3f zero()
	{
		return new Vector3f(0, 0, 0);
	}
	
	public static Vector3f one()
	{
		return new Vector3f(1, 1, 1);
	}

	public static Vector3fc negative()
	{
		return new Vector3f(-1, -1, -1);
	}

	public static float toRads(float degs)
	{
		return Maths.PI * degs / 180f;
	}
	
	public static float toDegs(float rads)
	{
		return rads * 180f / Maths.PI;
	}
	
	public static Vector3f toDegs(Vector3fc rads)
	{
		return new Vector3f(toDegs(rads.x()), toDegs(rads.y()), toDegs(rads.z()));
	}

	public static Matrix4f identity()
	{
		return new Matrix4f().identity();
	}

	public static Matrix4f mul(Matrix4f a, Matrix4f b) 
	{
		return new Matrix4f().identity().mul(a).mul(b);
	}

	public static Vector3f invert(Vector3fc v)
	{
		return new Vector3f(-v.x(), -v.y(), -v.z());
	}

	/**
	 * Same as rotate a by b
	 * @param a Thing to rotate
	 * @param b Thing to be rotated by
	 * @return The rotated vector
	 */
	public static Quaternionf mul(Quaternionfc a, Quaternionfc b)
	{
		return a.mul(b, new Quaternionf());
	}

	/**
	 * Same as un-rotate a by b
	 * @param a Thing to un-rotate
	 * @param b Thing to be un-rotate by
	 * @return The un-rotate vector
	 */
	public static Quaternionf div(Quaternionfc a, Quaternionfc b)
	{
		return a.div(b, new Quaternionf());
	}

	public static Vector3f rotatePoint(Quaternionfc rotation, Vector3fc position)
	{
		return rotation.transform(position, new Vector3f());
	}

	public static Quaternionfc invert(Quaternionfc q)
	{
		return new Quaternionf().invert();
	}
}
