package com.cornchipss.rendering.shaders;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.cornchipss.utils.Utils;

public class Shader
{
	private int id;
	
	private int vertS, fragS;
	
	public Shader(String vertex, String frag)
	{
		id = GL20.glCreateProgram();
		
		vertS = loadShader("assets/shaders/" + vertex, GL20.GL_VERTEX_SHADER);
		fragS = loadShader("assets/shaders/" + frag, GL20.GL_FRAGMENT_SHADER);
		
		GL20.glAttachShader(id, vertS);
		GL20.glAttachShader(id, fragS);
		
		GL20.glLinkProgram(id);
		GL20.glValidateProgram(id);
	}
	
	public int getUniformLocation(String name)
	{
		return GL30.glGetUniformLocation(id, name);
	}
	
	public void setUniformF(int location, float value)
	{
		GL30.glUniform1f(location, value);
	}
	
	public void setUniformVector(int location, float x, float y, float z)
	{
		GL30.glUniform3f(location, x, y, z);
	}
	
	public void loadUniformMatrix(int location, Matrix4f mat)
	{
		GL30.glUniformMatrix4fv(location, false, Utils.toFloatBuffer(mat));
	}
	
	public void start()
	{
		GL20.glUseProgram(id);
	}
	
	public static void stop()
	{
		GL20.glUseProgram(0);
	}
	
	public void cleanUp()
	{
		stop();
		GL20.glDetachShader(id, vertS);
		GL20.glDetachShader(id, fragS);
		GL20.glDeleteShader(vertS);
		GL20.glDeleteShader(fragS);
		GL20.glDeleteProgram(id);
	}
	
	private static int loadShader(String file, int type)
	{
        StringBuilder shaderSource = new StringBuilder();
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while((line = reader.readLine())!=null)
            {
                shaderSource.append(line).append("//\n");
            }
            reader.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
        
        int shaderID = GL30.glCreateShader(type);
        GL30.glShaderSource(shaderID, shaderSource);
        GL30.glCompileShader(shaderID);
        
        if(GL30.glGetShaderi(shaderID, GL30.GL_COMPILE_STATUS )== GL11.GL_FALSE)
        {
            System.out.println(GL30.glGetShaderInfoLog(shaderID, 500));
            System.err.println("Could not compile shader!");
            System.exit(-1);
        }
        
        return shaderID;
    }
}
