package com.rabbit.gui.component.control;

import org.lwjgl.opengl.GL11;

import com.rabbit.gui.component.display.Picture;
import com.rabbit.gui.layout.LayoutComponent;
import com.rabbit.gui.render.Renderer;
import com.rabbit.gui.render.TextRenderer;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@LayoutComponent
public class PictureButton extends Button {

	private Picture picture;

	/** Dummy constructor. Used in layout */
	public PictureButton() {
		super();
	}

	public PictureButton(int xPos, int yPos, int width, int height, ResourceLocation texture) {
		super(xPos, yPos, width, height, "");
		picture = new Picture(xPos + 1, yPos + 1, width - 2, height - 2, texture);
	}

	public PictureButton(int xPos, int yPos, int width, int height, String texture) {
		super(xPos, yPos, width, height, "");
		picture = new Picture(xPos + 1, yPos + 1, width - 2, height - 2, texture);
	}

	@Override
	public void onDraw(int mouseX, int mouseY, float partialTicks) {
		if (isVisible()) {
			GL11.glPushMatrix();
			prepareRender();
			if (!isEnabled()) {
				drawButton(DISABLED_STATE);
				picture.onDraw(mouseX, mouseY, partialTicks);
			} else if (isButtonUnderMouse(mouseX, mouseY)) {
				drawButton(HOVER_STATE);
				picture.onDraw(mouseX, mouseY, partialTicks);
				if (drawHoverText) {
					verifyHoverText(mouseX, mouseY);
					if (drawToLeft) {
						int tlineWidth = 0;
						for (String line : hoverText) {
							tlineWidth = TextRenderer.getFontRenderer().getStringWidth(line) > tlineWidth
									? TextRenderer.getFontRenderer().getStringWidth(line) : tlineWidth;
						}
						Renderer.drawHoveringText(hoverText, mouseX - tlineWidth - 20, mouseY + 12);
					} else {
						Renderer.drawHoveringText(hoverText, mouseX, mouseY + 12);
					}
				}
			} else {
				drawButton(IDLE_STATE);
				picture.onDraw(mouseX, mouseY, partialTicks);
			}
			endRender();
			GL11.glPopMatrix();
		}
	}

	public PictureButton setPictureTexture(ResourceLocation res) {
		picture.setImageTexture(res);
		;
		return this;
	}

	public PictureButton setPictureTexture(String res) {
		picture.setImageTexture(res);
		;
		return this;
	}

}
