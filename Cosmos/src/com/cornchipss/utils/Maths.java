package com.cornchipss.utils;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.lwjgl.BufferUtils;

import test.Vec3;

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
	 * How the equals function handles floats
	 */
	public static final float EQUALS_PRECISION = 0.0001f;
	
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
	
	public static void createViewMatrix(Vec3 position, Quaternionfc rotation, Matrix4f dest)
	{
		dest.identity();
		
		dest.rotate(rotation);
		
		dest.translate(-position.x(), -position.y(), -position.z());
	}
	
	/**
	 * Creates a view matrix based on coordinates + rotations
	 * @param pos Position
	 * @param rot Rotation
	 * @param dest The destiantion matrix
	 */
	public static void createViewMatrix(Vec3 pos, Vec3 rot, Matrix4f dest)
	{
		createViewMatrix(pos.x(), pos.y(), pos.z(), rot.x(), rot.y(), rot.z(), dest);
	}
	
	public static Matrix4f createTransformationMatrix(Vec3 position, float rx, float ry, float rz)
	{
		return createTransformationMatrix(position, rx, ry, rz, 1);
	}
	
	public static final Vector3fc 
		RIGHT = new Vector3f(1,0,0), 
		UP = new Vector3f(0,1,0), 
		FORWARD = new Vector3f(0,0,1);
	
	public static Matrix4f createTransformationMatrix(Vec3 pos, float rx, float ry, float rz, float scale)
	{
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.translate(pos.joml());
        matrix.rotate(rx, RIGHT);
        matrix.rotate(ry, UP);
        matrix.rotate(rz, FORWARD);
        matrix.scale(new Vector3f(scale, scale, scale));
        return matrix;
    }
	
	public static Matrix4f createTransformationMatrix(Vec3 pos, Quaternionfc rot)
	{
		Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.rotate(rot);
        matrix.translate(pos.joml());
        return matrix;
	}
	
	public static Matrix4f createRotationMatrix(Quaternionfc q)
	{
		FloatBuffer buf = BufferUtils.createFloatBuffer(16);
		q.getAsMatrix4f(buf);
		return new Matrix4f(buf);
	}
	
	public static Matrix4f createRotationMatrix(Vec3 axis, float angle)
	{
		return createRotationMatrix(axis.joml(), angle);
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
	
	public static Matrix4f createCombinedRotationMatrix(Vec3 rotation)
	{
		return createRotationMatrix(Utils.x(), rotation.x()).mul(createRotationMatrix(Utils.y(), rotation.y()).mul(createRotationMatrix(Utils.z(), rotation.z())));
	}
	
	@Deprecated
	/**
	 * idk if this works 
	 * @param pos
	 * @param rotations
	 * @return
	 */
	public static Vec3 getPositionActual(Vec3 pos, Matrix4fc... rotations)
	{
		Matrix4f rotationFinal = new Matrix4f();
		rotationFinal.identity();
		
		for(Matrix4fc rot : rotations)
			rotationFinal.mul(rot);
		
		Vector4f vec = new Vector4f(pos.x(), pos.y(), pos.z(), 0).mul(rotationFinal);
		
		return new Vec3(vec.x, vec.y, vec.z);
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
	
	public static Quaternionf quaternionFromRotation(float z, float y, float x)
	{
		float sx = Maths.sin(x / 2);
		float cx = Maths.cos(x / 2);
		float sy = Maths.sin(y / 2);
		float cy = Maths.cos(y / 2);
		float sz = Maths.sin(z / 2);
		float cz = Maths.cos(z / 2);
		
		float qx = sz * cy * cx - cz * sy * sx;
		float qy = cz * sy * cx + sz * cy * sx;
		float qz = cz * cy * sx - sz * sy * cx;
		float qw = cz * cy * cx + sz * sy * sx;
		
		return new Quaternionf(qx, qy, qz, qw);
	}
	
	public static Quaternionf quaternionFromRotation(Vec3 rot)
	{
		return quaternionFromRotation(rot.x(), rot.y(), rot.z());
	}
	
	public static Vec3 rotatePoint(Vec3 point, Vec3 rotation)
	{
		Quaternionf transQuat = blankQuaternion();
		
		Vec3 punto = new Vec3(point.x(), point.y(), point.z());
		rotation = mod(rotation, Maths.TAU);
		
		Quaternionf rotationQuat = blankQuaternion();
		
		rotationQuat.rotateXYZ(rotation.x(), rotation.y(), rotation.z(), transQuat);
		
		transQuat.transform(punto.joml());
		
		return punto;
	}
	
	public static Vec3 rotatePoint(Matrix4fc rotationMatrixX, Matrix4fc rotationMatrixY, Matrix4fc rotationMatrixZ, Vec3 point)
	{
		return rotatePoint(rotationMatrixX, rotationMatrixX, rotationMatrixX, new Vector4f(point.x(), point.y(), point.z(), 0));
	}
	
	public static Vec3 rotatePoint(Matrix4fc rotationMatrixX, Matrix4fc rotationMatrixY, Matrix4fc rotationMatrixZ, Vector4fc point)
	{
		return rotatePoint(Maths.mul(rotationMatrixX, rotationMatrixY).mul(rotationMatrixZ), point);
	}
	
	public static Vec3 rotatePoint(Matrix4fc combinedRotation, Vec3 point)
	{
		return rotatePoint(combinedRotation, new Vector4f(point.x(), point.y(), point.z(), 0));
	}
	
	public static Vec3 rotatePoint(Matrix4fc combinedRotation, Vector4fc point)
	{
		Vector4f vec = new Vector4f(point).mul(combinedRotation);
		return new Vec3(vec.x, vec.y, vec.z);
	}
	
	/**
	 * Calculates the ending point based off the starting position, rotation values, and the total distance
	 * @param start The starting point
	 * @param v The rotation (z is ignored)
	 * @param dist The total distance travelable
	 * @return The ending point
	 */
	public static Vec3 pointAt(Vec3 start, Vec3 v, float dist)
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
	public static Vec3 pointAt(Vec3 start, float rx, float ry, float dist)
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
	public static Vec3 toComponents(float rx, float ry, float velMagnitude)
	{
		Vec3 components = new Vec3();
		
		final double j = velMagnitude * Math.cos(rx);
		
		components.x((float) (j * Math.sin(ry)));
		components.y((float) (-velMagnitude * Math.sin(rx)));
		components.z((float) (-j * Math.cos(ry)));
		
		return components;
	}
	
	/**
	 * Adds two vectors without modifying either one
	 * @param a The first vector
	 * @param b The second vector
	 * @return A new vector of the two vectors added
	 */
	public static Vec3 add(Vec3 a, Vec3 b)
	{
		return new Vec3(a.x() + b.x(), a.y() + b.y(), a.z() + b.z());
	}
	
	/**
	 * Adds vectors without modifying them
	 * @param vecs The vectors
	 * @return A new vector of two vectors added
	 */
	public static Vec3 add(Vec3... vecs)
	{
		Vec3 v = Maths.zero();
		
		for(Vec3 c : vecs)
			v.add(c);
		
		return v;
	}
	
	public static Vec3 add(Vec3 v, float x, float y, float z)
	{
		return new Vec3(v.x() + x, v.y() + y, v.z() + z);
	}
	
	/**
	 * Subtracts two vectors without modifying either one
	 * @param a The first vector
	 * @param b The second vector
	 * @return A new vector of the two vectors subtracted
	 */
	public static Vec3 sub(Vec3 a, Vec3 b)
	{
		return new Vec3(a.x() - b.x(), a.y() - b.y(), a.z() - b.z());
	}
	
	/**
	 * Subtracts a vector without modifying it
	 * @param a The first vector
	 * @param b The second scalor
	 * @return A new vector of the vector - scalor
	 */
	public static Vec3 sub(Vec3 a, float s)
	{
		return new Vec3(a.x() - s, a.y() - s, a.z() - s);
	}
	
	/**
	 * Subtracts vectors without modifying them
	 * @param vecs The vectors
	 * @return A new vector of two vectors subtracted
	 */
	public static Vec3 sub(Vec3... vecs)
	{
		Vec3 v = Maths.zero();
		
		for(Vec3 c : vecs)
			v.sub(c);
		
		return v;
	}
	
	/**
	 * Multiplies two vectors without modifying either one
	 * @param a The first vector
	 * @param b The second vector
	 * @return A new vector of the two vectors multiplied
	 */
	public static Vec3 mul(Vec3 a, Vec3 b)
	{
		return new Vec3(a.x() * b.x(), a.y() * b.y(), a.z() * b.z());
	}
	
	/**
	 * Multiplies vectors without modifying them
	 * @param vecs The vectors
	 * @return A new vector of two vectors multiplid
	 */
	public static Vec3 mul(Vec3... vecs)
	{
		if(vecs.length == 0)
			return Maths.zero();
		
		Vec3 v = Maths.one();
		
		for(Vec3 c : vecs)
			v.mul(c);
		
		return v;
	}
	
	/**
	 * Multiplies two vectors without modifying either one
	 * @param x The first vector (<code>new Vec3(x, x, x)</code>)
	 * @param b The second vector
	 * @return A new vector of the two vectors multiplied
	 */
	public static Vec3 mul(float x, Vec3 a)
	{
		return mul(a, new Vec3(x));
	}
	
	/**
	 * Divides two vectors without modifying either one
	 * @param a The first vector
	 * @param b The second vector
	 * @return A new vector of the two vectors divided
	 */
	public static Vec3 div(Vec3 a, Vec3 b)
	{
		return new Vec3(a.x() / b.x(), a.y() / b.y(), a.z() / b.z());
	}
	
	/**
	 * Divides two vectors without modifying either one
	 * @param a The first vector
	 * @param b The second vector
	 * @return A new vector of the two vectors divided
	 */
	public static Vec3 div(Vec3 a, float d)
	{
		return new Vec3(a.x() / d, a.y() / d, a.z() / d);
	}
	
	/**
	 * Divides vectors without modifying them
	 * @param vecs The vectors
	 * @return A new vector of two vectors divided
	 */
	public static Vec3 div(Vec3... vecs)
	{
		if(vecs.length == 0)
			return Maths.zero();
		
		Vec3 v = Maths.one();
		
		for(Vec3 c : vecs)
			v.div(c);
		
		return v;
	}
	
	/**
	 * Takes the modulus two vectors without modifying either one
	 * @param a The first vector
	 * @param b The second vector
	 * @return A new vector of the two vectors modulus'ed
	 */
	public static Vec3 mod(Vec3 a, Vec3 b)
	{
		return new Vec3(a.x() % b.x(), a.y() % b.y(), a.z() % b.z());
	}
	
	/**
	 * Takes the modulus two vectors without modifying either one
	 * @param a The first vector
	 * @param b The scalar
	 * @return A new vector of the two vectors modulus'ed
	 */
	public static Vec3 mod(Vec3 a, float b)
	{
		return new Vec3(a.x() % b, a.y() % b, a.z() % b);
	}
	
	/**
	 * A Vec3 with all values being 0
	 * @return a Vec3 with all values being 0
	 */
	public static Vec3 zero()
	{
		return new Vec3(0, 0, 0);
	}
	
	public static Vec3 one()
	{
		return new Vec3(1, 1, 1);
	}

	public static Vec3 negative()
	{
		return new Vec3(-1, -1, -1);
	}

	public static float toRads(float degs)
	{
		return Maths.PI * degs / 180f;
	}
	
	public static float toDegs(float rads)
	{
		return rads * 180f / Maths.PI;
	}
	
	public static Vec3 toDegs(Vec3 rads)
	{
		return new Vec3(toDegs(rads.x()), toDegs(rads.y()), toDegs(rads.z()));
	}

	public static Matrix4f identity()
	{
		return new Matrix4f().identity();
	}

	public static Matrix4f mul(Matrix4fc a, Matrix4fc b) 
	{
		return new Matrix4f().identity().mul(a).mul(b);
	}

	public static Vec3 invert(Vec3 v)
	{
		return new Vec3(-v.x(), -v.y(), -v.z());
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

	public static Vec3 rotatePoint(Quaternionfc rotation, Vec3 position)
	{
		return new Vec3(rotation.transform(position.joml(), new Vector3f()));
	}

	public static Quaternionfc invert(Quaternionfc q)
	{
		return new Quaternionf().invert();
	}

	public static float clamp(float x, float min, float max)
	{
		return x > max ? max : x < min ? min : x;
	}

	public static Vec3 x(float x)
	{
		return new Vec3(x, 0, 0);
	}
	
	public static Vec3 y(float y)
	{
		return new Vec3(0, y, 0);
	}
	
	public static Vec3 z(float z)
	{
		return new Vec3(0, 0, z);
	}

	public static Quaternionf clone(Quaternionfc rotation)
	{
		return new Quaternionf(rotation.x(), rotation.y(), rotation.z(), rotation.w());
	}
	
	/**
	 * Normalizes a vector ({@link Vec3#normalize(float)}), but keeps it 0,0,0 if every value is 0
	 * @param vec The vector to normalize
	 * @param max The amount to normalize it to (generally 1)
	 * @return A normalized version of the vector
	 */
	public static Vec3 safeNormalize(Vec3 vec, float max)
	{
		return safeNormalize(vec.x(), vec.y(), vec.z(), max);
	}
	
	public static Vec3 safeNormalize(float x, float y, float z, float max)
	{
		if(x * x + y * y + z * z <= max * max)
			return new Vec3(x, y, z);
		return new Vec3(new Vec3(x, y, z).normalize(max));
	}
	
	public static Vec3 safeNormalizeXZ(Vec3 v, float max)
	{
		Vec3 xzVec = new Vec3(v.x(), 0, v.z());
		xzVec = safeNormalize(xzVec, max);
		return new Vec3(xzVec.x(), v.y(), xzVec.z());
	}

	public static Vec3 mul(Vec3 v, float s)
	{
		return new Vec3(v.x() * s, v.y() * s, v.z() * s);
	}
	
	public static float sqrt(float x)
	{
		return (float)Math.sqrt(x);
	}
	
	public static float magnitude(Vec3 v)
	{
		return Maths.sqrt(v.x() * v.x() + v.y() * v.y() + v.z() * v.z());
	}
	
	public static Vec3 normalClamp(Vec3 v, float max)
	{
		if(magnitude(v) > max)
			return safeNormalize(v, max);
		else
			return v;
	}

	public static float magnitudeXZ(Vec3 v)
	{
		return Maths.sqrt(v.x() * v.x() + v.z() * v.z());
	}

	public static Vec3 normalClampXZ(Vec3 v, float max)
	{
		if(magnitudeXZ(v) > max)
			return safeNormalizeXZ(v, max);
		else
			return v;
	}

	public static Matrix4fc invert(Matrix4fc mat)
	{
		return new Matrix4f(mat).invert();
	}

	public static boolean equals(float a, float b)
	{
		float amb = a - b;
		return amb <= EQUALS_PRECISION && amb >= -EQUALS_PRECISION;
	}
	
	public static boolean equals(Quaternionfc a, Quaternionfc b)
	{
		return equals(a.x(), b.x()) && equals(a.y(), b.y()) && equals(a.z(), b.z()) && equals(a.w(), b.w());
	}

	public static float distSqrd(Vec3 a, Vec3 b)
	{
		float x = a.x() - b.x();
		float y = a.y() - b.y();
		float z = a.z() - b.z();
		
		return x * x + y * y + z * z;
	}

	public static int floor(float x)
	{
		return (int)Math.floor(x);
	}

	public static float min(float a, float b)
	{
		return a < b ? a : b;
	}
	
	public static float max(float a, float b)
	{
		return a > b ? a : b;
	}
}
