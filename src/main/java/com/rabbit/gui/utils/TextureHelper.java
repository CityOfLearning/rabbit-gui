package com.rabbit.gui.utils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

public class TextureHelper {
	private static Map<UUID, Pair<String, DynamicTexture>> dynamicImages = Maps.newHashMap();
	private static Map<UUID, ResourceLocation> staticImages = Maps.newHashMap();
	private static ResourceLocation defaultTexture = new ResourceLocation("Minecraft", "textures/items/barrier.png");

	public static void addTexture(UUID textureId, String textureLocation) {
		if(textureLocation != null && !textureLocation.isEmpty()){
			try {
				dynamicImages.put(textureId, new ImmutablePair<String, DynamicTexture>(textureLocation, new DynamicTexture(ImageIO.read(new URL(textureLocation)))));
				System.out.println("Added Texture "+textureLocation + " to Dynamic Textures");
			} catch (MalformedURLException e) {
				System.out.println("Adding Texture "+textureLocation + " to Static Textures");
				staticImages.put(textureId, new ResourceLocation(textureLocation));
			} catch (IOException e) {
				System.out.println("Adding Texture "+textureLocation + " to Static Textures 2");
				staticImages.put(textureId, new ResourceLocation(textureLocation));
			}
		}
	}

	public static void addStaticTexture(UUID textureId, ResourceLocation texture) {
		staticImages.put(textureId, texture);
	}

	public static void addDynamicTexture(UUID textureId, URL textureLocation) {
		try {
			dynamicImages.put(textureId, new ImmutablePair<String, DynamicTexture>(textureLocation.getPath(), new DynamicTexture(ImageIO.read(textureLocation))));
		} catch (Exception e) {
			// must not be a valid image
		}
	}
	
	public static boolean isTextureStatic(UUID textureId){
		return staticImages.containsKey(textureId);
	}
	
	public static boolean isTextureDynamic(UUID textureId){
		return dynamicImages.containsKey(textureId);
	}
	
	public static boolean textureExists(UUID textureId){
		return staticImages.containsKey(textureId) || dynamicImages.containsKey(textureId);
	}
	
	public static ResourceLocation getStaticTexture(UUID textureId){
		return staticImages.get(textureId);
	}
	
	public static DynamicTexture getDynamicTexture(UUID textureId){
		if(isTextureDynamic(textureId)){
			return dynamicImages.get(textureId).getRight();
		}
		return null;
	}
	
	public static String getDynamicTextureLocation(UUID textureId){
		if(isTextureDynamic(textureId)){
			return dynamicImages.get(textureId).getLeft();
		}
		return null;
	}
	
	public static Pair<Integer, Integer> getTextureWidthAndHeight(UUID textureId){
		int width = 0, height =0;
		if(isTextureStatic(textureId)){
			try {
				BufferedImage image = ImageIO
						.read(Minecraft.getMinecraft().getResourceManager().getResource(getStaticTexture(textureId)).getInputStream());
				width = image.getWidth();
				height = image.getHeight();
			} catch (IOException ioex) {
				throw new RuntimeException("Can't get resource", ioex);
			}
		} else if(isTextureDynamic(textureId)) {
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

	public static void bindTexture(UUID textureId) {
		if (dynamicImages.containsKey(textureId)) {
			GlStateManager.bindTexture(dynamicImages.get(textureId).getRight().getGlTextureId());
//			GL11.glBindTexture(GL11.GL_TEXTURE_2D, dynamicImages.get(textureId).getRight().getGlTextureId());
		} else if (staticImages.containsKey(textureId)) {
			Minecraft.getMinecraft().renderEngine.bindTexture(staticImages.get(textureId));
		} else {
			Minecraft.getMinecraft().renderEngine.bindTexture(defaultTexture);
		}
	}

}
