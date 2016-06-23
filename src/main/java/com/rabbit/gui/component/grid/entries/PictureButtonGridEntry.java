package com.rabbit.gui.component.grid.entries;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import com.rabbit.gui.component.control.Button;
import com.rabbit.gui.component.grid.Grid;
import com.rabbit.gui.render.Renderer;
import com.rabbit.gui.render.TextRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Implementation of the ListEntry witch draws the given string in the center of
 * entry slot
 */
@SideOnly(Side.CLIENT)
public class PictureButtonGridEntry extends Button implements GridEntry {

	public static interface OnClickListener {
		void onClick(PictureButtonGridEntry entry, Grid grid, int mouseX, int mouseY);
	}

	/**
	 * Listener which would be called when user click the entry
	 */
	private OnClickListener listener;
	private ResourceLocation pictureTexture;
	private int imageWidth;

	private int imageHeight;

	public PictureButtonGridEntry(int width, int height, ResourceLocation texture) {
		this(width, height, texture, null);
	}

	public PictureButtonGridEntry(int width, int height, ResourceLocation texture, OnClickListener listener) {
		super(0, 0, width, height, "");
		pictureTexture = texture;
		try {
			BufferedImage image = ImageIO
					.read(Minecraft.getMinecraft().getResourceManager().getResource(texture).getInputStream());
			setImageWidth(image.getWidth());
			setImageHeight(image.getHeight());
		} catch (IOException ioex) {
			throw new RuntimeException("Can't get resource", ioex);
		}
		this.listener = listener;
	}

	@Override
	public PictureButtonGridEntry addHoverText(String text) {
		originalHoverText.add(text);
		return this;
	}

	@Override
	public PictureButtonGridEntry doesDrawHoverText(boolean state) {
		drawHoverText = state;
		return this;
	}

	@Override
	public List<String> getHoverText() {
		return originalHoverText;
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public ResourceLocation getPictureTexture() {
		return pictureTexture;
	}

	@Override
	public void onClick(Grid grid, int mouseX, int mouseY) {
		if (listener != null) {
			listener.onClick(this, grid, mouseX, mouseY);
		}
	}

	@Override
	public void onDraw(Grid grid, int posX, int posY, int width, int height, int mouseX, int mouseY) {
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
			prepareRender();
			if (!isEnabled()) {
				drawButton(DISABLED_STATE);
				renderPicture();
			} else if (isButtonUnderMouse(mouseX, mouseY)) {
				drawButton(HOVER_STATE);
				renderPicture();
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
				renderPicture();
			}
		}
	}

	private void renderPicture() {
		GL11.glPushMatrix();
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		Minecraft.getMinecraft().renderEngine.bindTexture(pictureTexture);
		Renderer.drawScaledTexturedRect(getX() + 1, getY() + 1, getWidth()-2, getHeight()-2);
		GL11.glPopMatrix();
	}

	public PictureButtonGridEntry setClickListener(OnClickListener onClicked) {
		listener = onClicked;
		return this;
	}

	@Override
	public PictureButtonGridEntry setHoverText(List<String> text) {
		originalHoverText = text;
		return this;
	}

	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}

	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}

	public PictureButtonGridEntry setPictureTexture(ResourceLocation res) {
		pictureTexture = res;
		return this;
	}
}
