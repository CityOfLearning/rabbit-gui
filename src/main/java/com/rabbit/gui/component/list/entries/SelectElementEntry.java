package com.rabbit.gui.component.list.entries;

import java.awt.Color;

import com.rabbit.gui.component.list.DisplayList;
import com.rabbit.gui.layout.LayoutComponent;
import com.rabbit.gui.render.TextAlignment;
import com.rabbit.gui.render.TextRenderer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SelectElementEntry<T> extends SelectListEntry {

	public static interface OnClickListener {
		void onClick(SelectElementEntry entry, DisplayList list, int mouseX, int mouseY);
	}

	@LayoutComponent
	protected boolean isEnabled = true;

	/**
	 * String which would be drawn in the center of the entry <br>
	 * If it doesn't fits into slot width it would be trimmed
	 */
	private final String title;
	private final T entryValue;

	@LayoutComponent
	private Color color;

	/**
	 * Listener which would be called when user click the entry
	 */
	private OnClickListener listener;

	public SelectElementEntry(T value, String title) {
		this(value, title, Color.WHITE, null);
	}

	public SelectElementEntry(T value, String title, Color color) {
		this(value, title, color, null);
	}

	public SelectElementEntry(T value, String title, Color color, OnClickListener listener) {
		this.entryValue = value;
		this.title = title;
		this.listener = listener;
		this.color = color;
	}

	public SelectElementEntry(T value, String title, OnClickListener listener) {
		this(value, title, Color.WHITE, listener);
	}

	public String getTitle() {
		return title;
	}

	public T getValue() {
		return this.entryValue;
	}

	/**
	 * @return <code> true</code> if button can be clicked
	 */
	@Override
	public boolean isEnabled() {
		return isEnabled;
	}

	@Override
	public void onClick(DisplayList list, int mouseX, int mouseY) {
		if (isEnabled) {
			super.onClick(list, mouseX, mouseY);
			if (listener != null) {
				listener.onClick(this, list, mouseX, mouseY);
			}
		} else {
			setSelected(false);
		}

	}

	@Override
	public void onDraw(DisplayList list, int posX, int posY, int width, int height, int mouseX, int mouseY) {
		super.onDraw(list, posX, posY, width, height, mouseX, mouseY);
		if (isEnabled) {
			TextRenderer.renderString(posX + (width / 2), (posY + (height / 2)) - 5,
					TextRenderer.getFontRenderer().trimStringToWidth(title, width), color, TextAlignment.CENTER);
		} else {
			TextRenderer.renderString(posX + (width / 2), (posY + (height / 2)) - 5,
					TextRenderer.getFontRenderer().trimStringToWidth(title, width), Color.GRAY, TextAlignment.CENTER);
		}
	}

	@Override
	public SelectElementEntry<T> setIsEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
		return this;
	}
}
