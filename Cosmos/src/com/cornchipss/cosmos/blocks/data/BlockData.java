package com.cornchipss.cosmos.blocks.data;

import java.util.HashMap;
import java.util.Map;

public class BlockData
{
	private Map<String, Object> data;
	
	public BlockData()
	{
		data = new HashMap<>();
	}
	
	public Object data(String key)
	{
		return data.get(key);
	}
	
	public void data(String key, Object value)
	{
		data.put(key, value);
	}
}
