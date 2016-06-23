package com.rabbit.gui.component.control;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import com.rabbit.gui.layout.LayoutComponent;
import com.rabbit.gui.render.Renderer;
import com.rabbit.gui.render.TextRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@LayoutComponent
public class PictureToggleButton extends Button {
	
	private boolean toggle;

	private ResourceLocation onPictureTexture;
	private ResourceLocation offPictureTexture;

	public PictureToggleButton(int xPos, int yPos, int width, int height, ResourceLocation ontexture, ResourceLocation offtexture, boolean toggled) {
		super(xPos, yPos, width, height, "");
		this.toggle = toggled;
		try {
			BufferedImage image = ImageIO
					.read(Minecraft.getMinecraft().getResourceManager().getResource(ontexture).getInputStream());
			image = ImageIO
					.read(Minecraft.getMinecraft().getResourceManager().getResource(offtexture).getInputStream());
		} catch (IOException ioex) {
			throw new RuntimeException("Can't get resource", ioex);
		}
		setOnPictureTexture(ontexture);
		setOffPictureTexture(offtexture);
	}

	/** Dummy constructor. Used in layout */
	public PictureToggleButton() {
		super();
	}

	@Override
	public void onDraw(int mouseX, int mouseY, float partialTicks) {
		if (isVisible()) {
			GL11.glPushMatrix();
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
						Renderer.drawHoveringText(hoverText, mouseX - tlineWidth - 20, mouseY + 12);
					} else {
						Renderer.drawHoveringText(hoverText, mouseX, mouseY + 12);
					}
				}
			} else {
				drawButton(IDLE_STATE);
				renderPicture();
			}
			endRender();
			GL11.glPopMatrix();
		}
	}

	private void renderPicture() {
		GL11.glPushMatrix();
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		if(toggle){
			Minecraft.getMinecraft().renderEngine.bindTexture(onPictureTexture);
		} else {
			Minecraft.getMinecraft().renderEngine.bindTexture(offPictureTexture);
		}
		Renderer.drawScaledTexturedRect(getX() + 1, getY() + 1, getWidth()-2, getHeight()-2);
		GL11.glPopMatrix();
	}

	public boolean isToggled() {
		return toggle;
	}

	public void setToggle(boolean toggle) {
		this.toggle = toggle;
	}
	
	@Override
	public boolean onMouseClicked(int posX, int posY, int mouseButtonIndex, boolean overlap) {
		boolean clicked = isButtonUnderMouse(posX, posY) && isEnabled() && !overlap;
		if (clicked) {
			if (getClickListener() != null) {
				getClickListener().onClick(this);
			}
			toggle = !toggle;
			playClickSound();
		}
		return clicked;
	}

	public ResourceLocation getOnPictureTexture() {
		return onPictureTexture;
	}

	public void setOnPictureTexture(ResourceLocation onPictureTexture) {
		this.onPictureTexture = onPictureTexture;
	}

	public ResourceLocation getOffPictureTexture() {
		return offPictureTexture;
	}

	public void setOffPictureTexture(ResourceLocation offPictureTexture) {
		this.offPictureTexture = offPictureTexture;
	}

}
