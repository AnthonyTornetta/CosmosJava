package com.cornchipss.cosmos.models;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.joml.Vector2i;

public class ModelLoader
{
	public static void toFile(String file,
			float[] vertices, float[] uvs, int[] indices,
			Map<String, Integer> groups,
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
			for(String g : groups.keySet())
			{
				if(groups.get(g) == i)
				{
					str.append(g + ": ");
					break;
				}
			}
			
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
			float[] vertices, float[] uvs, int[] indices,
			Map<String, Integer> groups) throws IOException
	{
		toFile(file, vertices, uvs, indices, groups, false);
	}
	
	public static LoadedModel fromFile(String file) throws IOException
	{
		// used for the auto close on the reader
		try(BufferedReader br = new BufferedReader(new FileReader(new File(file + ".model"))))
		{
			List<Float> verts = new LinkedList<>();
			List<Integer> indices = new LinkedList<>();
			List<Float> uvs = new LinkedList<>();
			Map<String, Vector2i> groups = new HashMap<>();
			
			char mode = 0;
			
			String prevGroup = null;
			int prevIndex = -1;
			
			for(String line = br.readLine(); line != null; line = br.readLine())
			{
				line = line.trim();
				
				if(line.length() == 0)
					continue;
				
				if(mode == 0)
				{
					mode = Character.toLowerCase(line.charAt(0));
					if(mode != 'u' && mode != 'v' && mode != 'i')
						throw new IOException("File must begin with the mode (v/u/i) - began with " + mode);
					
					line = line.substring(1);
				}
				
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
						
						if(s.charAt(s.length() - 1) == ':')
						{
							String group = s.substring(0, s.length() - 1);
							if(prevGroup == null)
							{
								prevGroup = group;
								prevIndex = indices.size();
							}
							else
							{
								groups.put(prevGroup, new Vector2i(prevIndex, indices.size() - 1));
								
								prevGroup = group;
								prevIndex = indices.size();
							}
						}
						else
						{
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
			
			if(prevGroup != null)
			{
				groups.put(prevGroup, new Vector2i(prevIndex, indices.size() - 1));
			}
			else
			{
				groups.put("main", new Vector2i(0, indices.size() - 1));
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
			
			return new LoadedModel(vertsArr, uvsArr, indicesArr, groups);
		}
	}
}
