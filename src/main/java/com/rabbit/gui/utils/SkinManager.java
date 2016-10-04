package com.rabbit.gui.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class SkinManager {
	// key is minecraft username and value is the skin texture
	private static Map<String, UUID> playerSkin = new HashMap<String, UUID>();

	public static void addSkin(EntityPlayer player, String skin) {
		UUID textureId = UUID.randomUUID();
		// incase its a url the texture helper will sort it all out
		TextureHelper.addTexture(textureId, skin);
		playerSkin.put(player.getDisplayNameString(), textureId);
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
		if (playerSkin.containsKey(player.getDisplayNameString())) {
			TextureHelper.bindTexture(playerSkin.get(player.getDisplayNameString()));
			return true;
		}
		return false;
	}

	public static boolean bindSkinTexture(String player) {
		if (playerSkin.containsKey(player)) {
			TextureHelper.bindTexture(playerSkin.get(player));
			return true;
		}
		return false;
	}

	public static String getSkinTexture(EntityPlayer player) {
		if (playerSkin.containsKey(player.getName())) {
			// not sure we can do anything about dynamic textures currently
			if (TextureHelper.getDynamicTexture(playerSkin.get(player.getName())) == null) {
				return TextureHelper.getStaticTexture(playerSkin.get(player.getName())).toString();
			}
		}
		return null;
	}

	public static String getSkinTexture(String player) {
		if (playerSkin.containsKey(player)) {
			// not sure we can do anything about dynamic textures currently
			if (TextureHelper.getDynamicTexture(playerSkin.get(player)) == null) {
				return TextureHelper.getStaticTexture(playerSkin.get(player)).toString();
			}
		}
		return null;
	}

	public static boolean hasSkinTexture(EntityPlayer player) {
		return playerSkin.containsKey(player.getDisplayNameString());
	}

	public static boolean hasSkinTexture(String player) {
		return playerSkin.containsKey(player);
	}

	public static void removeSkinTexture(EntityPlayer player) {
		playerSkin.remove(player.getName());
	}

	public static void removeSkinTexture(String player) {
		playerSkin.remove(player);
	}

	public static void setSkinTexture(EntityPlayer player, String skin) {
		if (playerSkin.containsKey(player.getDisplayNameString())) {
			UUID textureId = UUID.randomUUID();
			// incase its a url the texture helper will sort it all out
			TextureHelper.addTexture(textureId, skin);
			playerSkin.replace(player.getDisplayNameString(), textureId);
		} else {
			addSkin(player, skin);
		}
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
