package com.cornchipss.cosmos.gui.guis;

import com.cornchipss.cosmos.gui.GUI;
import com.cornchipss.cosmos.gui.GUIElementHolder;
import com.cornchipss.cosmos.gui.GUIModel;
import com.cornchipss.cosmos.gui.GUITextureMultiple;
import com.cornchipss.cosmos.gui.measurement.AddedMeasurement;
import com.cornchipss.cosmos.gui.measurement.MeasurementPair;
import com.cornchipss.cosmos.gui.measurement.PercentMeasurement;
import com.cornchipss.cosmos.gui.measurement.PixelMeasurement;
import com.cornchipss.cosmos.inventory.Inventory;

public class HotbarGUI extends GUIElementHolder
{
	private final int slotDimensions = 64;

	private GUITextureMultiple[] inventorySlots;
	private GUIModel[] inventoryModels;

	private int selectedSlot = 0;

	private Inventory inv;

	public HotbarGUI(Inventory inv, MeasurementPair xy, MeasurementPair wh)
	{
		super(xy, wh);
		this.inv = inv;
	}

	public void onAdd(GUI gui)
	{
		inventorySlots = new GUITextureMultiple[10];

		PixelMeasurement slotDims = new PixelMeasurement(slotDimensions);

		int offset = -slotDimensions * (inventorySlots.length / 2);

		for (int i = 0; i < inventorySlots.length; i++)
		{
			inventorySlots[i] = new GUITextureMultiple(new MeasurementPair(
				new AddedMeasurement(new PixelMeasurement(offset + i * slotDimensions), PercentMeasurement.HALF),
				PixelMeasurement.ZERO), new MeasurementPair(slotDims, slotDims), 0.25f, 0, 0, 0.25f);

			addChild(inventorySlots[i]);
		}

		initInventoryBarModels(inv);

		inventorySlots[selectedSlot].state(1);
	}

	private void initInventoryBarModels(Inventory inventory)
	{
		inventoryModels = new GUIModel[10];

		int offset = -slotDimensions * (inventoryModels.length / 2);

		for (int i = 0; i < inventory.columns(); i++)
		{
			if (inventory.block(0, i) != null)
			{
				int margin = 4;

				inventoryModels[i] = new GUIModel(
					new MeasurementPair(new AddedMeasurement(new PixelMeasurement(offset + i * slotDimensions + margin),
						PercentMeasurement.HALF), new PixelMeasurement(margin)),
					slotDimensions - margin * 2, inventory.block(0, i).model());

				addChild(inventoryModels[i]);
			}
		}
	}

	public void select(int row)
	{
		inventorySlots[selectedSlot].state(0);
		inventorySlots[row].state(1);

		selectedSlot = row;
	}
}
