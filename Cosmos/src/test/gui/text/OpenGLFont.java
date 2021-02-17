package test.gui.text;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import com.cornchipss.rendering.Texture;

public class OpenGLFont
{
	public static final int CHAR_MIN = 32, CHAR_MAX = 256; // 0-31 are control codes
	
	private Texture fontTexture;
	private Font font;
	private FontMetrics metrics;
	
	private int[] offsets = new int[CHAR_MAX - CHAR_MIN + 1];
	
	private int fontHeight;
	
	private FontMetrics getMetrics(Font f)
	{
		BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		
		g.setFont(font);
		FontMetrics metrics = g.getFontMetrics();
		g.dispose();
		
		return metrics;
	}
	
	public OpenGLFont(Font f)
	{
		font = f;
		metrics = getMetrics(f);
	}
	
	public void init()
	{
		int imgWidth = 0;
		
		fontHeight = metrics.getHeight();
		
		for(int i = CHAR_MIN; i <= CHAR_MAX; i++)
		{
			if(i == 127)
				continue; // DEL character
			
			char c = (char)i;
			
			offsets[i - CHAR_MIN] = imgWidth;
			
			imgWidth += metrics.charWidth(c);
		}
		
		BufferedImage image = new BufferedImage(imgWidth, fontHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		
		offsets[CHAR_MAX - CHAR_MIN] = imgWidth;
		
		g.setPaint(Color.WHITE);
		
		g.setFont(font);

		for(int i = CHAR_MIN; i <= CHAR_MAX; i++)
		{
			if(i == 127)
				continue; // DEL character
			
			char c = (char)i;
			
			g.drawString(String.valueOf(c), getOffset(c), metrics.getAscent());
		}
		
		g.dispose();
		
		fontTexture = Texture.loadTexture(image);
	}

	public static void unbind()
	{
		Texture.unbind();
	}

	public void bind()
	{
		fontTexture.bind();
	}

	public float uBegin(char c)
	{
		return getOffset(c) / (float)offsets[offsets.length - 1];
	}
	
	public float uEnd(char c)
	{
		return getOffset((char)((int)c + 1)) / (float)offsets[offsets.length - 1];
	}
	
	public int charWidth(char c)
	{
		return getOffset((char)((int)c + 1)) - getOffset(c);
	}
	
	private int getOffset(char c)
	{
		return offsets[(int)c - CHAR_MIN];
	}
	
	public Font font() { return font; }
	
	public int height() { return fontHeight; }
}
