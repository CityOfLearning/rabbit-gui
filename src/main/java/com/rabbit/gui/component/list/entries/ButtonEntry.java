package com.rabbit.gui.component.list.entries;

import com.rabbit.gui.component.control.Button;
import com.rabbit.gui.component.list.DisplayList;
import com.rabbit.gui.render.Renderer;
import com.rabbit.gui.render.TextAlignment;
import com.rabbit.gui.render.TextRenderer;

import net.minecraft.client.renderer.GlStateManager;

/**
 * Implementation of the ListEntry witch draws the given string in the center of
 * entry slot
 */
public class ButtonEntry extends Button implements ListEntry {
	
	
	public ButtonEntry(String text) {
		super(0, 0, 0, 0, text);
	}
	
	public ButtonEntry(String text, ButtonClickListener listener) {
		super(0, 0, 0, 0, text);
		super.setClickListener(listener);
	}

	@Override
	public void onDraw(DisplayList list, int posX, int posY, int width, int height, int mouseX, int mouseY) {
		if (getX() != posX) {
			setX(posX);
		}
		if (getY() != posY) {
			setY(posY);
		}
		if (getWidth() != width) {
			setWidth(width);
		}
		if (getHeight() != height) {
			setHeight(height);
		}
		if (isVisible()) {
			GlStateManager.pushMatrix();
			prepareRender();
			if (!isEnabled()) {
				drawButton(DISABLED_STATE);
			} else if (isButtonUnderMouse(mouseX, mouseY)) {
				drawButton(HOVER_STATE);
				if (drawHoverText) {
					verifyHoverText(mouseX, mouseY);
					if (drawToLeft) {
						int tlineWidth = 0;
						for (String line : hoverText) {
							tlineWidth = TextRenderer.getFontRenderer().getStringWidth(line) > tlineWidth
									? TextRenderer.getFontRenderer().getStringWidth(line) : tlineWidth;
						}
						Renderer.drawHoveringTextInScissoredArea(hoverText, mouseX - tlineWidth - 20, mouseY);
					} else {
						Renderer.drawHoveringTextInScissoredArea(hoverText, mouseX, mouseY);
					}
				}
			} else {
				drawButton(IDLE_STATE);
			}
			TextRenderer.renderString(getX() + (getWidth() / 2), (getY() + (getHeight() / 2)) - 4, getText(),
					TextAlignment.CENTER);
			endRender();
			GlStateManager.popMatrix();
		}
	}

	@Override
	public ButtonEntry setIsEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
		return this;
	}
	
	@Override
	public void onClick(DisplayList list, int mouseX, int mouseY) {
		if (isEnabled) {
			if (super.getClickListener() != null) {
				super.onMouseClicked(mouseX, mouseY, 1, false);
			}
		} else {
			setSelected(false);
		}

	}
}