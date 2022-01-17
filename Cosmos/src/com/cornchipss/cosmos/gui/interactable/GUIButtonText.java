package com.cornchipss.cosmos.gui.interactable;

import java.util.ArrayList;
import java.util.List;

import com.cornchipss.cosmos.gui.GUIElement;
import com.cornchipss.cosmos.gui.IGUIContainer;
import com.cornchipss.cosmos.gui.measurement.AddedMeasurement;
import com.cornchipss.cosmos.gui.measurement.DividedMeasurement;
import com.cornchipss.cosmos.gui.measurement.MeasurementPair;
import com.cornchipss.cosmos.gui.measurement.PixelMeasurement;
import com.cornchipss.cosmos.gui.measurement.SubtractedMeasurement;
import com.cornchipss.cosmos.gui.text.Fonts;
import com.cornchipss.cosmos.gui.text.GUIText;
import com.cornchipss.cosmos.gui.text.OpenGLFont;

public class GUIButtonText extends GUIButton implements IGUIContainer
{
	private List<GUIElement> textArr = new ArrayList<>(1);

	public GUIButtonText(String text, MeasurementPair position,
		MeasurementPair dim, Runnable onclick)
	{
		super(position, dim, onclick);

		// x = btnPos + btnWidth / 2 - stringWidth / 2
		// y = btnPos + btnHeight / 2 + fontHeight / 2

		OpenGLFont font = Fonts.ARIAL_28;

		textArr
			.add(new GUIText(text, Fonts.ARIAL_28,
				new MeasurementPair(
					new SubtractedMeasurement(
						new AddedMeasurement(position.x(),
							new DividedMeasurement(dim.x(),
								PixelMeasurement.TWO)),
						new PixelMeasurement(font.stringWidth(text) / 2.f)),
					new SubtractedMeasurement(
						new AddedMeasurement(position.y(),
							new DividedMeasurement(dim.y(),
								PixelMeasurement.TWO)),
						new PixelMeasurement(font.height() / 2.f)))));
	}

	@Override
	public List<GUIElement> children()
	{
		return textArr;
	}

	@Override
	public void addChild(GUIElement elem)
	{
		throw new IllegalStateException("Cannot add a child from this button!");
	}

	@Override
	public void removeChild(GUIElement elem)
	{
		throw new IllegalStateException(
			"Cannot remove the text from this button!");
	}
}
