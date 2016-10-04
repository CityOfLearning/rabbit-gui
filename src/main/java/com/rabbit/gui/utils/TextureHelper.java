package com.rabbit.gui.utils;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Maps;
import com.rabbit.gui.RabbitGui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

public class TextureHelper {
	private static Map<UUID, Pair<String, DynamicTexture>> dynamicImages = Maps.newHashMap();
	private static Map<UUID, Pair<Integer, Integer>> dynamicImageInfo = Maps.newHashMap();
	private static Map<UUID, ResourceLocation> staticImages = Maps.newHashMap();
	private static Map<UUID, Pair<Integer, Integer>> ImageSizes = Maps.newHashMap();
	private static ResourceLocation defaultTexture = new ResourceLocation("Minecraft", "textures/items/barrier.png");

	public static void addDynamicTexture(UUID textureId, URL textureLocation) throws IOException {
		// this should cache the image
		BufferedImage bufImg = ImageCacheHelper.fetchImage(textureLocation);
		if (bufImg == null) {
			throw new IOException();
		}
		bufImg = trim(bufImg);
		if ((bufImg.getWidth() > 128) || (bufImg.getHeight() > 128)) {
			if (bufImg.getWidth() != bufImg.getHeight()) {
				bufImg = resize(bufImg, bufImg.getWidth(), bufImg.getHeight());
			} else {
				// resize a square image
				bufImg = ImageCacheHelper.resizeImage(bufImg);
			}
		}
		dynamicImageInfo.put(textureId, new ImmutablePair<Integer, Integer>(bufImg.getWidth(), bufImg.getHeight()));
		dynamicImages.put(textureId,
				new ImmutablePair<String, DynamicTexture>(textureLocation.getPath(), new DynamicTexture(bufImg)));
	}

	public static void addStaticTexture(UUID textureId, ResourceLocation texture) {
		staticImages.put(textureId, texture);
	}

	public static void addTexture(UUID textureId, String textureLocation) {
		if ((textureLocation != null) && !textureLocation.isEmpty()) {
			try {
				addDynamicTexture(textureId, new URL(textureLocation));
				RabbitGui.logger.info("Added Texture " + textureLocation + " to Dynamic Textures");
			} catch (MalformedURLException e) {
				RabbitGui.logger.info("Adding Texture " + textureLocation + " to Static Textures");
				staticImages.put(textureId, new ResourceLocation(textureLocation));
			} catch (IOException e) {
				RabbitGui.logger.info("Adding Texture " + textureLocation + " to Static Textures 2");
				staticImages.put(textureId, new ResourceLocation(textureLocation));
			}
		}
	}

	public static void bindTexture(UUID textureId) {
		if (dynamicImages.containsKey(textureId)) {
			GlStateManager.bindTexture(dynamicImages.get(textureId).getRight().getGlTextureId());
		} else if (staticImages.containsKey(textureId)) {
			Minecraft.getMinecraft().renderEngine.bindTexture(staticImages.get(textureId));
		} else {
			Minecraft.getMinecraft().renderEngine.bindTexture(defaultTexture);
		}
	}

	public static DynamicTexture getDynamicTexture(UUID textureId) {
		if (isTextureDynamic(textureId)) {
			return dynamicImages.get(textureId).getRight();
		}
		return null;
	}

	public static Pair<Integer, Integer> getDynamicTextureInfo(UUID textureId) {
		return dynamicImageInfo.get(textureId);
	}

	public static String getDynamicTextureLocation(UUID textureId) {
		if (isTextureDynamic(textureId)) {
			return dynamicImages.get(textureId).getLeft();
		}
		return null;
	}

	public static ResourceLocation getStaticTexture(UUID textureId) {
		return staticImages.get(textureId);
	}

	public static Pair<Integer, Integer> getTextureWidthAndHeight(UUID textureId) {
		int width = 0, height = 0;
		if (isTextureStatic(textureId)) {
			try {
				BufferedImage image = ImageIO.read(Minecraft.getMinecraft().getResourceManager()
						.getResource(getStaticTexture(textureId)).getInputStream());
				width = image.getWidth();
				height = image.getHeight();
			} catch (IOException ioex) {
				throw new RuntimeException("Can't get resource", ioex);
			}
		} else if (isTextureDynamic(textureId)) {
			try {
				BufferedImage image = ImageIO.read(new URL(dynamicImages.get(textureId).getLeft()));
				width = image.getWidth();
				height = image.getHeight();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return new ImmutablePair<Integer, Integer>(width, height);
	}

	private static int getTrimmedHeight(BufferedImage img) {
		int width = img.getWidth();
		int height = img.getHeight();
		int trimmedHeight = 0;

		for (int i = 0; i < width; i++) {
			for (int j = height - 1; j >= 0; j--) {
				if (((img.getRGB(i, j) >> 24) != 0x00) && (j > trimmedHeight)) {
					trimmedHeight = j;
					break;
				}
			}
		}

		return trimmedHeight;
	}

	private static int getTrimmedWidth(BufferedImage img) {
		int height = img.getHeight();
		int width = img.getWidth();
		int trimmedWidth = 0;

		for (int i = 0; i < height; i++) {
			for (int j = width - 1; j >= 0; j--) {
				if (((img.getRGB(j, i) >> 24) != 0x00) && (j > trimmedWidth)) {
					trimmedWidth = j;
					break;
				}
			}
		}

		return trimmedWidth;
	}

	private static int getXMostPos(BufferedImage img) {
		int height = img.getHeight();
		int width = img.getWidth();
		int x = width;

		for (int i = 0; i < height; i++) {
			for (int j = width - 1; j >= 0; j--) {
				if (((img.getRGB(j, i) >> 24) != 0x00) && (j < x)) {
					x = j;
					break;
				}
			}
		}

		return x;
	}

	private static int getYMostPos(BufferedImage img) {
		int width = img.getWidth();
		int height = img.getHeight();
		int y = height;

		for (int i = 0; i < width; i++) {
			for (int j = height - 1; j >= 0; j--) {
				if (((img.getRGB(i, j) >> 24) != 0x00) && (j < y)) {
					y = j;
					break;
				}
			}
		}

		return y;
	}

	public static boolean isTextureDynamic(UUID textureId) {
		return dynamicImages.containsKey(textureId);
	}

	public static boolean isTextureStatic(UUID textureId) {
		return staticImages.containsKey(textureId);
	}

	public static BufferedImage resize(BufferedImage img, int width, int height) {
		int newW = 0, newH = 0;
		if (width > 128) {
			// image is too big lets just scale from width
			float scale = 128.0f / width;
			newW = (int) (width * scale);
			newH = (int) (height * scale);
		} else {
			// image is tall but not wide thought we still need to scale
			float scale = 128.0f / height;
			newW = (int) (width * scale);
			newH = (int) (height * scale);
		}
		Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
		BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = dimg.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();

		return dimg;
	}

	public static boolean textureExists(UUID textureId) {
		return staticImages.containsKey(textureId) || dynamicImages.containsKey(textureId);
	}

	public static BufferedImage trim(BufferedImage img) {
		int width = getTrimmedWidth(img);
		int height = getTrimmedHeight(img);
		int xpos = getXMostPos(img);
		int ypos = getYMostPos(img);

		return img.getSubimage(xpos, ypos, width - xpos, height - ypos);
	}
}
