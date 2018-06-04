package com.rabbit.gui.demo;

import java.util.ArrayList;
import java.util.List;

import com.rabbit.gui.component.display.Panel;
import com.rabbit.gui.component.display.Picture;
import com.rabbit.gui.component.grid.ScrollableGrid;
import com.rabbit.gui.component.grid.entries.GridEntry;
import com.rabbit.gui.component.grid.entries.PictureButtonGridEntry;
import com.rabbit.gui.show.Show;
import com.rabbit.gui.utils.DefaultTextures;

import net.minecraft.advancements.Advancement;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class DemoScreen2 extends Show {

	public DemoScreen2() {

	}

	@Override
	public void setup() {

		Panel panel = (Panel) new Panel(20, 20, (int) (width * .6), (int) (height * .6))
				.setVisible(true).setFocused(true).setCanDrag(true).setId("num1");

		List<GridEntry> entries = new ArrayList<>();

		for (Advancement a : Minecraft.getMinecraft().player.connection.getAdvancementManager().getAdvancementList()
				.getAdvancements()) {
			List<String> hoverText = new ArrayList<>();
			if(a.getDisplay() != null) {
				hoverText.add(a.getDisplay().getTitle().getFormattedText());
				hoverText.add(a.getDisplay().getDescription().getFormattedText());
			
			entries.add(new PictureButtonGridEntry(25, 25,
					new ResourceLocation("minecraft", "textures/items/experience_bottle.png"))
							.setDoesDrawHoverText(true).setHoverText(hoverText));
			entries.add(new PictureButtonGridEntry(25, 25,
					new ResourceLocation("minecraft", "textures/items/experience_bottle.png"))
							.setDoesDrawHoverText(true).setHoverText(hoverText));
			}
		}

		panel.registerComponent(new ScrollableGrid(10, 10, panel.getWidth()-20,
				panel.getHeight() -20, 55, 55, entries).setVisibleBackground(false));
		// The background
		panel.registerComponent(new Picture(0, 0, panel.getWidth(), panel.getHeight(), DefaultTextures.BACKGROUND3));
		
		panel.reverseComponents();
		
		registerComponent(panel);
		
		Panel panel2 = (Panel) new Panel((int) (width * .5), (int) (height * .5), (int) (width * .6), (int) (height * .6))
				.setVisible(true).setFocused(false).setCanDrag(true).setId("num2");

		List<GridEntry> entries2 = new ArrayList<>();

		for (Advancement a : Minecraft.getMinecraft().player.connection.getAdvancementManager().getAdvancementList()
				.getAdvancements()) {
			List<String> hoverText = new ArrayList<>();
			if(a.getDisplay() != null) {
				hoverText.add(a.getDisplay().getTitle().getFormattedText());
				hoverText.add(a.getDisplay().getDescription().getFormattedText());
			
			entries2.add(new PictureButtonGridEntry(25, 25,
					new ResourceLocation("minecraft", "textures/items/book_enchanted.png"))
							.setDoesDrawHoverText(true).setHoverText(hoverText));
			}
		}

		panel2.registerComponent(new ScrollableGrid(10, 10, panel2.getWidth()-20,
				panel2.getHeight() -20, 55, 55, entries2).setVisibleBackground(false));
		// The background
		panel2.registerComponent(new Picture(0, 0, panel2.getWidth(), panel2.getHeight(), DefaultTextures.BACKGROUND2));
		
		panel2.reverseComponents();
		
		registerComponent(panel2);
	}
}
