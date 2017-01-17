package com.rabbit.gui.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class SkinManager {
	// key is minecraft username and value is the skin texture
	private static Map<String, UUID> playerSkin = new HashMap<>();

	public static void addSkin(EntityPlayer player, String skin) {
		addSkin(player.getName(), skin);
	}

	public static void addSkin(String player, ResourceLocation skin) {
		UUID textureId = UUID.randomUUID();
		// incase its a url the texture helper will sort it all out
		TextureHelper.addStaticTexture(textureId, skin);
		playerSkin.put(player, textureId);
	}

	public static void addSkin(String player, String skin) {
		UUID textureId = UUID.randomUUID();
		// incase its a url the texture helper will sort it all out
		TextureHelper.addTexture(textureId, skin);
		playerSkin.put(player, textureId);
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

	public static void setSkinTexture(EntityPlayer player, String skin) {
		setSkinTexture(player.getName(), skin);
	}

	public static void setSkinTexture(String player, String skin) {
		if (playerSkin.containsKey(player)) {
			UUID textureId = UUID.randomUUID();
			// incase its a url the texture helper will sort it all out
			TextureHelper.addTexture(textureId, skin);
			playerSkin.replace(player, textureId);
		} else {
			addSkin(player, skin);
		}
	}
}
