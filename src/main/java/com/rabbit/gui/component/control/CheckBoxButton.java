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
public class CheckBoxButton extends Button {

	@FunctionalInterface
	public static interface ButtonClickListener {
		void onClick(CheckBoxButton button);
	}

	private CheckBox checkbox;
	private ResourceLocation pictureTexture;

	/** Dummy constructor. Used in layout */
	protected CheckBoxButton() {
		super();
	}

	public CheckBoxButton(int xPos, int yPos, int width, int height, ResourceLocation texture, boolean checked) {
		super(xPos, yPos, width, height, "");
		pictureTexture = texture;
		try {
			BufferedImage image = ImageIO
					.read(Minecraft.getMinecraft().getResourceManager().getResource(texture).getInputStream());
		} catch (IOException ioex) {
			throw new RuntimeException("Can't get resource", ioex);
		}
		if(width <= height){
			checkbox = new CheckBox(xPos + 2, (int)(yPos + height*.66 - 2), height /3, height /3, "", checked);
		} else {
			checkbox = new CheckBox(xPos + 2, yPos + height/2 - 2, height /2, height /2, "", checked);
		}
		checkbox.setIsEnabled(false);
	}

	@Override
	public void onDraw(int mouseX, int mouseY, float partialTicks) {
		if (isVisible()) {
			prepareRender();
			if (!isEnabled()) {
				drawButton(DISABLED_STATE);
				renderPicture();
				checkbox.onDraw(mouseX, mouseY, partialTicks);
			} else if (isButtonUnderMouse(mouseX, mouseY)) {
				drawButton(HOVER_STATE);
				renderPicture();
				checkbox.onDraw(mouseX, mouseY, partialTicks);
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
				if(checkbox.isChecked()){
					drawButton(DISABLED_STATE);
				} else {
					drawButton(IDLE_STATE);
				}
				renderPicture();
				checkbox.onDraw(mouseX, mouseY, partialTicks);
			}	
		}
	}

	@Override
	public boolean onMouseClicked(int posX, int posY, int mouseButtonIndex, boolean overlap) {
		boolean clicked = isButtonUnderMouse(posX, posY) && isEnabled() && !overlap;
		if (clicked) {
			if (getClickListener() != null) {
				getClickListener().onClick(this);
			}
			checkbox.setIsChecked(!checkbox.isChecked());
			playClickSound();
		}
		return clicked;
	}

	private void renderPicture() {
		GL11.glPushMatrix();
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		Minecraft.getMinecraft().renderEngine.bindTexture(pictureTexture);
		Renderer.drawScaledTexturedRect(getX() + getWidth()/5, getY() + 1, (int)(getWidth()*.8)-1, (int)(getHeight()*.8)-1);
		GL11.glPopMatrix();
	}
}
