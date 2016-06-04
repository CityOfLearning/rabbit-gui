package com.rabbit.gui.component.list.entries;

import com.rabbit.gui.component.list.DisplayList;
import com.rabbit.gui.render.TextAlignment;
import com.rabbit.gui.render.TextRenderer;

/**
 * Implementation of the ListEntry witch draws the given string in the center of
 * entry slot
 */
public class StringEntry implements ListEntry {

	public static interface OnClickListener {
		void onClick(DisplayList list, int mouseX, int mouseY);
	}

	/**
	 * String which would be drawn in the center of the entry <br>
	 * If it doesn't fits into slot width it would be trimmed
	 */
	private final String title;

	/**
	 * Listener which would be called when user click the entry
	 */
	private OnClickListener listener;

	public StringEntry(String title) {
		this(title, null);
	}

	public StringEntry(String title, OnClickListener listener) {
		this.title = title;
		this.listener = listener;
	}

	@Override
	public void onClick(DisplayList list, int mouseX, int mouseY) {
		if (listener != null) {
			listener.onClick(list, mouseX, mouseY);
		}
	}

	@Override
	public void onDraw(DisplayList list, int posX, int posY, int width, int height, int mouseX, int mouseY) {
		TextRenderer.renderString(posX + (width / 2), (posY + (height / 2)) - 5,
				TextRenderer.getFontRenderer().trimStringToWidth(title, width), TextAlignment.CENTER);
	}
}