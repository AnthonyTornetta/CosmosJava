package com.cornchipss.cosmos.gui.measurement;

/**
 * <p>
 * Used to neatly write measurements without writing horrible code
 * </p>
 * <p>
 * <code>"50% + 200"</code><br>
 * Gets converted into<br>
 * <code>new AddedMeasurement(new PercentMeasurement(0.5f), new PixelMeasurement(200));</code>
 * </p>
 */
public class MeasurementParser
{
	private MeasurementParser()
	{
	} // no

	/**
	 * Parses the * and / part of the input - input should only contain * and /
	 * tokens at this point
	 * 
	 * @param s The string to parse
	 * @return The measurement it produces
	 */
	private static Measurement parseInnards(String s)
	{
		String num = "";

		for (int i = s.length() - 1; i >= 0; i--)
		{
			if (s.charAt(i) == '*')
			{
				return new MultipliedMeasurement(
					parseInnards(s.substring(0, i)), parseNum(num));
			}
			else if (s.charAt(i) == '/')
			{
				return new DividedMeasurement(parseInnards(s.substring(0, i)),
					parseNum(num));
			}
			else
			{
				num = s.charAt(i) + num;
			}
		}

		return parseNum(num);
	}

	/**
	 * Converts a number to its measurement counterpart
	 * 
	 * @param s The number
	 * @return The measurement counterpart
	 */
	private static Measurement parseNum(String s)
	{
		if (s.charAt(s.length() - 1) == '%')
			return new PercentMeasurement(
				Float.parseFloat(s.substring(0, s.length() - 1)) / 100.0f);
		else
			return new PixelMeasurement(Float.parseFloat(s));
	}

	/**
	 * Parses input split around '+' and '-' tokens. The token order should be
	 * provided in tokens
	 * 
	 * @param arr    The split string
	 * @param tokens The token order
	 * @param start  The position to start the parsing at
	 * @return The measurement generated from this parsing
	 */
	private static Measurement parseSplitInput(String[] arr, char[] tokens,
		int start)
	{
		if (start == 0)
		{
			return parseInnards(arr[start]);
		}
		else
		{
			switch (tokens[start - 1])
			{
				case '+':
					return new AddedMeasurement(
						parseSplitInput(arr, tokens, start - 1),
						parseInnards(arr[start]));
				case '-':
					return new SubtractedMeasurement(
						parseSplitInput(arr, tokens, start - 1),
						parseInnards(arr[start]));
			}
		}

		throw new IllegalStateException("Something wasn't formatted properly");
	}

	/**
	 * <p>
	 * Converts a String of measurement code into an actual {@link Measurement}
	 * object
	 * </p>
	 * <p>
	 * Used to neatly write measurements without writing horrible code
	 * </p>
	 * <p>
	 * <code>"50% + 200"</code><br>
	 * Gets converted into<br>
	 * <code>new AddedMeasurement(new PercentMeasurement(0.5f), new PixelMeasurement(200));</code>
	 * </p>
	 * 
	 * @param str The string to convert
	 * @return The measurement generated from this string
	 */
	public static Measurement parse(String str)
	{
		str = str.replace(" ", "");

		String[] splt = str.split("\\+|\\-");
		char[] tokenOrder = new char[splt.length - 1];
		int idx = 0;
		for (char c : str.toCharArray())
			if (c == '-')
				tokenOrder[idx++] = c;
			else if (c == '+')
				tokenOrder[idx++] = c;

		return parseSplitInput(splt, tokenOrder, splt.length - 1);
	}
}
