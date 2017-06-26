package com.rabbit.gui.utils;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.rabbit.gui.RabbitGui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class SkinManager {
	// key is minecraft username and value is the skin texture
	private static Map<String, UUID> playerSkin = new HashMap<>();
	private static Map<String, Integer> textureSize = new HashMap<>();

	public static void addSkin(EntityPlayer player, String skin) {
		addSkin(player.getName(), skin);
	}

	public static void addSkin(String player, ResourceLocation skin) {
		if (skin != null) {
			UUID textureId = UUID.randomUUID();
			// incase its a url the texture helper will sort it all out
			TextureHelper.addStaticTexture(textureId, skin);
			playerSkin.put(player, textureId);
		}
	}

	public static void addSkin(String player, String skin) {
		if (skin != null && !skin.isEmpty()) {
			UUID textureId = UUID.randomUUID();
			// incase its a url the texture helper will sort it all out
			TextureHelper.addTexture(textureId, skin);
			playerSkin.put(player, textureId);
		}
	}

	public static boolean bindSkinTexture(EntityPlayer player) {
		return bindSkinTexture(player.getName());
	}

	public static boolean bindSkinTexture(String player) {
		if (playerSkin.containsKey(player)) {
			TextureHelper.bindTexture(playerSkin.get(player));
			return true;
		}
		return false;
	}

	public static String getSkinTexture(EntityPlayer player) {
		return getSkinTexture(player.getName());
	}

	public static String getSkinTexture(String player) {
		if (playerSkin.containsKey(player)) {
			// not sure we can do anything about dynamic textures currently
			if (TextureHelper.getDynamicTexture(playerSkin.get(player)) == null) {
				ResourceLocation tex = TextureHelper.getStaticTexture(playerSkin.get(player));
				if (tex != null) {
					return tex.toString();
				}
			}
		}
		return null;
	}

	public static boolean hasSkinTexture(EntityPlayer player) {
		return hasSkinTexture(player.getName());
	}

	public static boolean hasSkinTexture(String player) {
		return playerSkin.containsKey(player);
	}

	public static void removeSkinTexture(EntityPlayer player) {
		removeSkinTexture(player.getName());
	}

	public static void removeSkinTexture(String player) {
		playerSkin.remove(player);
	}

	public static void setSkinTexture(EntityPlayer player, ResourceLocation skin) {
		setSkinTexture(player.getName(), skin);
	}

	public static void setSkinTexture(EntityPlayer player, String skin) {
		setSkinTexture(player.getName(), skin);
	}

	public static void setSkinTexture(String player, ResourceLocation skin) {
		if (skin != null) {
			if (playerSkin.containsKey(player)) {
				UUID textureId = UUID.randomUUID();
				// incase its a url the texture helper will sort it all out
				TextureHelper.addStaticTexture(textureId, skin);
				playerSkin.replace(player, textureId);
			} else {
				addSkin(player, skin);
			}
			setTextureDimension(player, skin);
		}
	}

	public static void setSkinTexture(String player, String skin) {
		if (skin != null && !skin.isEmpty()) {
			if (playerSkin.containsKey(player)) {
				UUID textureId = UUID.randomUUID();
				// incase its a url the texture helper will sort it all out
				TextureHelper.addTexture(textureId, skin);
				playerSkin.replace(player, textureId);
			} else {
				addSkin(player, skin);
			}
			setTextureDimension(player, skin);
		}
	}
	
	public static int getSkinTextureHeight(EntityPlayer player){
		return getSkinTextureHeight(player.getName());
	}
	
	public static int getSkinTextureHeight(String player){
		if(textureSize.containsKey(player)){
		return textureSize.get(player);
		}
		else {
			return 64;
		}
	}

	private static void setTextureDimension(String player, ResourceLocation skin) {
		InputStream inputstream = null;
		try {
			IResource iresource = Minecraft.getMinecraft().getResourceManager().getResource(skin);
			inputstream = iresource.getInputStream();
			BufferedImage bufferedimage = TextureUtil.readBufferedImage(inputstream);

			if (textureSize.containsKey(player)) {
				textureSize.replace(player, bufferedimage.getHeight());
			} else {
				textureSize.put(player, bufferedimage.getHeight());
			}
		} catch (FileNotFoundException fnfe) {
			RabbitGui.logger.error("Could not determine texture size", fnfe);
		} catch (IOException e) {
			RabbitGui.logger.error("Could not determine texture size", e);
		} finally {
			if (inputstream != null) {
				try {
					inputstream.close();
				} catch (IOException e) {
					RabbitGui.logger.error("Failed during player render, could not close inputstream",
							e);
				}
			}
		}
	}

	private static void setTextureDimension(String player, String skin) {
		try {
			BufferedImage bufferedimage = ImageCacheHelper.fetchImage(new URL(skin));

			if (textureSize.containsKey(player)) {
				textureSize.replace(player, bufferedimage.getHeight());
			} else {
				textureSize.put(player, bufferedimage.getHeight());
			}
		} catch (MalformedURLException e) {
			RabbitGui.logger.error(e.getLocalizedMessage() + "\nCould not determine texture size from url, attempting resource location method");
			setTextureDimension(player, new ResourceLocation(skin));
		}
	}
}
