package com.cornchipss.cosmos.models;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ModelLoader
{
	public static void toFile(String file,
			float[] vertices, float[] uvs, int[] indices,
			boolean pretty) throws IOException
	{
		StringBuilder str = new StringBuilder();	
		
		str.append('v');
		if(pretty)
			str.append('\n');
		else
			str.append(' ');
		
		for(int i = 0; i < vertices.length; i++)
		{
			str.append(vertices[i] + " ");
			if(pretty && (i + 1) % 3 == 0)
			{
				str.append('\n');
			}
		}
		
		if(pretty && str.charAt(str.length() - 1) != '\n')
			str.append('\n');
		
		str.append('u');
		if(pretty)
			str.append('\n');
		else
			str.append(' ');
		
		for(int i = 0; i < uvs.length; i++)
		{
			str.append(uvs[i] + " ");
			if(pretty && (i + 1) % 2 == 0)
			{
				str.append('\n');
			}
		}
		
		if(pretty && str.charAt(str.length() - 1) != '\n')
			str.append('\n');
		
		str.append('i');
		if(pretty)
			str.append('\n');
		else
			str.append(' ');
		
		for(int i = 0; i < indices.length; i++)
		{
			str.append(indices[i] + " ");
			if(pretty && (i + 1) % 3 == 0)
			{
				str.append('\n');
			}
		}
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(file)));
		bw.write(str.toString());
		bw.close();
	}
	
	public static void toFile(String file,
			float[] vertices, float[] uvs, int[] indices) throws IOException
	{
		toFile(file, vertices, uvs, indices, false);
	}
	
	public static LoadedModel fromFile(String file) throws IOException
	{
		// used for the auto close on the reader
		try(BufferedReader br = new BufferedReader(new FileReader(new File(file + ".model"))))
		{
			List<Float> verts = new LinkedList<>();
			List<Integer> indices = new LinkedList<>();
			List<Float> uvs = new LinkedList<>();
			
			char mode = 0;
			
			for(String line = br.readLine(); line != null; line = br.readLine())
			{
				line = line.trim();
				
				if(mode == 0)
				{
					if(line.length() > 1)
						throw new IOException("File must begin with the mode (v/u/i)");
					
					mode = line.charAt(0);
				}
				else
				{
					if(line.length() == 0 || line.charAt(0) == '#')
						continue;
					
					String[] split = line.split(" ");
					
					for(String s : split)
					{
						if(s.length() != 0)
						{							
							// Checks for mode updates
							if(s.length() == 1)
							{
								char c = Character.toLowerCase(s.charAt(0));
								if(c == 'u' || c == 'v' || c == 'i')
								{
									mode = c;
									continue;
								}
							}
							
							switch(mode)
							{
							case 'v':
								verts.add(Float.parseFloat(s));
								break;
							case 'u':
								uvs.add(Float.parseFloat(s));
								break;
							case 'i':
								indices.add(Integer.parseInt(s));
								break;
							}
						}
					}
				}
			}
			
			float[] vertsArr = new float[verts.size()];
			float[] uvsArr = new float[uvs.size()];
			int[] indicesArr = new int[indices.size()];
			
			int i = 0;
			for(float f : verts)
				vertsArr[i++] = f;
			
			i = 0;
			for(float f : uvs)
				uvsArr[i++] = f;
			
			i = 0;
			for(int idx : indices)
				indicesArr[i++] = idx;
			
			return new LoadedModel(vertsArr, uvsArr, indicesArr);
		}
	}
}
