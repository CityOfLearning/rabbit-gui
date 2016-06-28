package com.rabbit.gui.utils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

public class TextureHelper {
	private static Map<String, Pair<String, DynamicTexture>> dynamicImages = Maps.newHashMap();
	private static Map<String, ResourceLocation> staticImages = Maps.newHashMap();
	private static ResourceLocation defaultTexture = new ResourceLocation("Minecraft", "textures/items/barrier.png");

	public static void addTexture(String name, String textureLocation) {
		try {
			dynamicImages.put(name, new ImmutablePair<String, DynamicTexture>(textureLocation, new DynamicTexture(ImageIO.read(new URL(textureLocation)))));
		} catch (MalformedURLException e) {
			staticImages.put(name, new ResourceLocation(textureLocation));
		} catch (IOException e) {
			staticImages.put(name, new ResourceLocation(textureLocation));
		}
	}

	public static void addStaticTexture(String name, ResourceLocation texture) {
		staticImages.put(name, texture);
	}

	public static void addDynamicTexture(String name, URL textureLocation) {
		try {
			dynamicImages.put(name, new ImmutablePair<String, DynamicTexture>(textureLocation.getPath(), new DynamicTexture(ImageIO.read(textureLocation))));
		} catch (Exception e) {
			// must not be a valid image
		}
	}
	
	public static boolean isTextureStatic(String name){
		return staticImages.containsKey(name);
	}
	
	public static boolean isTextureDynamic(String name){
		return dynamicImages.containsKey(name);
	}
	
	public static ResourceLocation getStaticTexture(String name){
		return staticImages.get(name);
	}
	
	public static DynamicTexture getDynamicTexture(String name){
		if(isTextureDynamic(name)){
			return dynamicImages.get(name).getRight();
		}
		return null;
	}
	
	public static Pair<Integer, Integer> getTextureWidthAndHeight(String name){
		int width = 0, height =0;
		if(isTextureStatic(name)){
			try {
				BufferedImage image = ImageIO
						.read(Minecraft.getMinecraft().getResourceManager().getResource(getStaticTexture(name)).getInputStream());
				width = image.getWidth();
				height = image.getHeight();
			} catch (IOException ioex) {
				throw new RuntimeException("Can't get resource", ioex);
			}
		} else if(isTextureDynamic(name)) {
			try {
				BufferedImage image = ImageIO.read(new URL(dynamicImages.get(name).getLeft()));
				width = image.getWidth();
				height = image.getHeight();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return new ImmutablePair<Integer, Integer>(width, height);
	}

	public static void bindTexture(String name) {
		if (dynamicImages.containsKey(name)) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, dynamicImages.get(name).getRight().getGlTextureId());
		} else if (staticImages.containsKey(name)) {
			Minecraft.getMinecraft().renderEngine.bindTexture(staticImages.get(name));
		} else {
			Minecraft.getMinecraft().renderEngine.bindTexture(defaultTexture);
		}
	}

}
