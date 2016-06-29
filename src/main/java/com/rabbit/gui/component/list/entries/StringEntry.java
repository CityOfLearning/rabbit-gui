package com.rabbit.gui.component.list.entries;

import java.awt.Color;

import com.rabbit.gui.component.list.DisplayList;
import com.rabbit.gui.layout.LayoutComponent;
import com.rabbit.gui.render.TextAlignment;
import com.rabbit.gui.render.TextRenderer;

/**
 * Implementation of the ListEntry witch draws the given string in the center of
 * entry slot
 */
public class StringEntry implements ListEntry {

	@LayoutComponent
	protected boolean isEnabled = true;

	/**
	 * String which would be drawn in the center of the entry <br>
	 * If it doesn't fits into slot width it would be trimmed
	 */
	private final String title;

	public StringEntry(String title) {
		this.title = title;
	}


	@Override
	public void onClick(DisplayList list, int mouseX, int mouseY) {
		//nothing happens its not a selectable component
	}

	@Override
	public void onDraw(DisplayList list, int posX, int posY, int width, int height, int mouseX, int mouseY) {
		if(isEnabled()){
			TextRenderer.renderString(posX + (width / 2), (posY + (height / 2)) - 5,
					TextRenderer.getFontRenderer().trimStringToWidth(title, width), TextAlignment.CENTER);	
		} else {
			TextRenderer.renderString(posX + (width / 2), (posY + (height / 2)) - 5,
					TextRenderer.getFontRenderer().trimStringToWidth(title, width), Color.gray, TextAlignment.CENTER);
		}	
	}


	@Override
	public boolean isEnabled() {
		return isEnabled;
	}


	@Override
	public StringEntry setIsEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
		return this;
	}
}