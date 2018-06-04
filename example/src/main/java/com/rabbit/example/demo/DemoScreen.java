package com.rabbit.gui.demo;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.rabbit.gui.component.control.Button;
import com.rabbit.gui.component.control.CheckBoxButton;
import com.rabbit.gui.component.control.CheckBoxPictureButton;
import com.rabbit.gui.component.control.NumberPicker;
import com.rabbit.gui.component.control.Slider;
import com.rabbit.gui.component.control.TextBox;
import com.rabbit.gui.component.display.Panel;
import com.rabbit.gui.component.display.Picture;
import com.rabbit.gui.component.display.TextLabel;
import com.rabbit.gui.component.list.ScrollableDisplayList;
import com.rabbit.gui.component.list.entries.ListEntry;
import com.rabbit.gui.component.list.entries.StringEntry;
import com.rabbit.gui.reference.Reference;
import com.rabbit.gui.show.Show;
import com.rabbit.gui.utils.DefaultTextures;

import net.minecraft.util.ResourceLocation;

public class DemoScreen extends Show {

	public DemoScreen() {

	}

	@Override
	public void setup() {

		Panel panel = new Panel((int) (width * .2), (int) (height * .2), (int) (width * .4), (int) (height * .4))
				.setVisible(true).setFocused(true).setCanDrag(true);

		panel.registerComponent(new TextLabel((int) (panel.getWidth() * .05), (int) (panel.getHeight() * .05),
				(int) (panel.getWidth() * .8), 20, Color.black, "Im a Panel"));

		panel.registerComponent(new TextBox((int) (panel.getWidth() * .1), (int) (panel.getHeight() * .3),
				(int) (panel.getWidth() * .8), 25, "I'm Placeholder Text"));

		panel.registerComponent(new Button((int) (panel.getWidth() * .6), (int) (panel.getHeight() * .6),
				(int) (panel.getWidth() * .33), 20, "I'm a Button"));

		panel.registerComponent(new Picture(0, 0, panel.getWidth(), panel.getHeight(), DefaultTextures.BACKGROUND1));

		// this is my lazy way of not having to rearrange things
		panel.reverseComponents();

		registerComponent(panel);
		
		registerComponent(new CheckBoxPictureButton((int) (width * .67), (int) (height * .37), 50, 35,
				new ResourceLocation("rabbit", "textures/gui/exit.png"), false));
		
		// The students on the Roster List for this class
		List<ListEntry> rlist = new ArrayList<>();

		// View roster list
		for (int i = 0; i < 20; i++) {
			rlist.add(new StringEntry("I am string entry #" + i));
		}

		registerComponent(
				new ScrollableDisplayList((int) (width * .22), (int) (height * .35), width / 3, 105, 15, rlist));
		
registerComponent(new NumberPicker((int) (width * .58), (int) (height * .37), 20, 60));

		registerComponent(new CheckBoxButton((int) (width * .58), (int) (height * .65), 100, 20,
				"Toggle Me", false));

		registerComponent(new TextLabel((int) (width * .25), (int) (height * .25), (int) (width * .25), 20, Color.black,
				"Im a Text Label"));

		registerComponent(new Slider((int) (width * .53) + 15, (int) (height * .23), 120, 20, 10));

		registerComponent(new Picture((int) (width * .2), (int) (height * .2), (int) (width * .6), (int) (height * .6),
				DefaultTextures.BACKGROUND1));
	}
}
