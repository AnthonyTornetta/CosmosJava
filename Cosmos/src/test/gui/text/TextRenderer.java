package test.gui.text;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.annotation.Nonnull;

import com.cornchipss.rendering.Texture;
import com.cornchipss.utils.Utils;

import test.Mesh;

public class TextRenderer
{
	private final Font font;
	
	public static final int CHAR_MIN = 32, CHAR_MAX = 256; // 0-31 are control codes
	
	private int[] offsets = new int[CHAR_MAX - CHAR_MIN + 1];
	
	private Texture fontTexture;
	private int fontHeight;
	
	private FontMetrics getMetrics(Font f, boolean antiAlias)
	{
		// Get the metrics:
		BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		
		if (antiAlias)
		    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g.setFont(font);
		FontMetrics metrics = g.getFontMetrics();
		g.dispose();
		
		return metrics;
	}
	
	public TextRenderer(@Nonnull Font f)
	{
		font = f;
		
		int imgWidth = 0;
		
		FontMetrics metrics = getMetrics(f, false);
		
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
	
	int getOffset(char c)
	{
		return offsets[(int)c - CHAR_MIN];
	}
	
	public void renderText(String text, float x, float y)
	{
		float xOff = 0;
		float yOff = 0;
		
		int[] indicies = new int[text.length() * 6];
		float[] uvs = new float[text.length() * 8];
		float[] vertices = new float[text.length() * 12];
		
		float mul = (float)offsets[offsets.length - 1];
		
		for(int i = 0; i < text.length(); i++)
		{
			char c = text.charAt(i);
			
			if(c == '\n')
			{
				yOff += fontHeight;
				xOff = 0;
			}
			
			float charLoc = getOffset(c) / (float)offsets[offsets.length - 1];
			float nextCharLoc = getOffset((char)((int)c + 1)) / (float)offsets[offsets.length - 1];
			float width = (nextCharLoc - charLoc) * mul;
			
			indicies[i * 6 + 0] = i * 4;
			indicies[i * 6 + 1] = i * 4 + 1;
			indicies[i * 6 + 2] = i * 4 + 3;
			
			indicies[i * 6 + 3] = i * 4 + 1;
			indicies[i * 6 + 4] = i * 4 + 2;
			indicies[i * 6 + 5] = i * 4 + 3;
			
			uvs[i * 8 + 0] = nextCharLoc;
			uvs[i * 8 + 1] = 0;
			
			uvs[i * 8 + 2] = nextCharLoc;
			uvs[i * 8 + 3] = 1;
			
			uvs[i * 8 + 4] = charLoc;
			uvs[i * 8 + 5] = 1;
			
			uvs[i * 8 + 6] = charLoc;
			uvs[i * 8 + 7] = 0;
			
			vertices[i * 12 + 0] = xOff + x + width;
			vertices[i * 12 + 1] = yOff + y + fontHeight;
			vertices[i * 12 + 2] = 0;
			
			vertices[i * 12 + 3] = xOff + x + width;
			vertices[i * 12 + 4] = yOff + y;
			vertices[i * 12 + 5] = 0;
			
			vertices[i * 12 + 6] = xOff + x;
			vertices[i * 12 + 7] = yOff + y;
			vertices[i * 12 + 8] = 0;
			
			vertices[i * 12 + 9] = xOff + x;
			vertices[i * 12 + 10]= yOff + y + fontHeight;
			vertices[i * 12 + 11]= 0;
			
			xOff += width;
		}
		
		fontTexture.bind();
		
		Mesh m = Mesh.createMesh(vertices, indicies, uvs);
		
		m.prepare();
		m.draw();
		m.finish();
		
		Texture.unbind();
	}
}
