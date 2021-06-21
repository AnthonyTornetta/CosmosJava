package com.cornchipss.cosmos.gui.measurement;

public class MeasurementParser
{
	private MeasurementParser() {} // no
	
	private static Measurement parseInnards(String s)
	{
		String num = "";
		
		for(int i = s.length() - 1; i >= 0; i--)
		{
			if(s.charAt(i) == '*')
			{
				return new MultipliedMeasurement(parseInnards(s.substring(0, i)), parseNum(num));
			}
			else if(s.charAt(i) == '/')
			{				
				return new DividedMeasurement(parseInnards(s.substring(0, i)), parseNum(num));
			}
			else
			{
				num = s.charAt(i) + num;
			}
		}
		
		return parseNum(num);
	}
	
	private static Measurement parseNum(String s)
	{
		if(s.charAt(s.length() - 1) == '%')
			return new PercentMeasurement(Float.parseFloat(
					s.substring(0, s.length() - 1)) / 100.0f);	
		else
			return new PixelMeasurement(Float.parseFloat(s));
	}
	
	private static Measurement parseSplitInput(String[] arr, char[] tokens, int start)
	{
		if(start == 0)
		{
			return parseInnards(arr[start]);
		}
		else
		{
			switch(tokens[start - 1])
			{
			case '+':
				return new AddedMeasurement(parseSplitInput(arr, tokens, start - 1),
						parseInnards(arr[start]));
			case '-':
				return new SubtractedMeasurement(parseSplitInput(arr, tokens, start - 1),
						parseInnards(arr[start]));
			}
		}
		
		throw new IllegalStateException("oops");
	}
	
	public static Measurement parse(String m)
	{
		m = m.replace(" ", "");
		
		String[] splt = m.split("\\+|\\-");
		char[] tokenOrder = new char[splt.length - 1];
		int idx = 0;
		for(char c : m.toCharArray())
			if(c == '-')
				tokenOrder[idx++] = c;
			else if(c == '+')
				tokenOrder[idx++] = c;
		
		return parseSplitInput(splt, tokenOrder, splt.length - 1);
	}
}
