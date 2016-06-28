package com.rabbit.gui.component.display;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import com.rabbit.gui.component.GuiWidget;
import com.rabbit.gui.render.Renderer;
import com.rabbit.gui.utils.TextureHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Picture extends GuiWidget {

	private String pictureUUID = UUID.randomUUID().toString();
	private int imageWidth;
	private int imageHeight;

	public Picture(int xPos, int yPos, int width, int height, ResourceLocation texture) {
		super(xPos, yPos, width, height);
		TextureHelper.addStaticTexture(pictureUUID, texture);
		try {
			BufferedImage image = ImageIO
					.read(Minecraft.getMinecraft().getResourceManager().getResource(texture).getInputStream());
			setImageWidth(image.getWidth());
			setImageHeight(image.getHeight());
		} catch (IOException ioex) {
			throw new RuntimeException("Can't get resource", ioex);
		}
	}
	
	public Picture(int xPos, int yPos, int width, int height, String textureLocation) {
		super(xPos, yPos, width, height);
		TextureHelper.addTexture(pictureUUID, textureLocation);
		if(TextureHelper.isTextureStatic(pictureUUID)){
			try {
				BufferedImage image = ImageIO
						.read(Minecraft.getMinecraft().getResourceManager().getResource(TextureHelper.getStaticTexture(pictureUUID)).getInputStream());
				setImageWidth(image.getWidth());
				setImageHeight(image.getHeight());
			} catch (IOException ioex) {
				throw new RuntimeException("Can't get resource", ioex);
			}
		} else if(TextureHelper.isTextureDynamic(pictureUUID)) {
			try {
				BufferedImage image = ImageIO.read(new URL(textureLocation));
				setImageWidth(image.getWidth());
				setImageHeight(image.getHeight());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public int getImageWidth() {
		return imageWidth;
	}

	@Override
	public void onDraw(int xMouse, int yMouse, float partialTicks) {
		super.onDraw(xMouse, yMouse, partialTicks);
		renderPicture();
	}

	private void renderPicture() {
		GL11.glPushMatrix();
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		TextureHelper.bindTexture(pictureUUID);
		Renderer.drawScaledTexturedRect(getX(), getY(), getWidth(), getHeight());
		GL11.glPopMatrix();
	}

	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}

	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}
}
