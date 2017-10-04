package com.rabbit.gui.component.notification.types;

import net.minecraft.util.ResourceLocation;

public interface INotification {
	public static final ResourceLocation guiTexture = new ResourceLocation(
			"textures/gui/achievement/achievement_background.png");

	public void drawNotification();

	public void updateWindowScale();
}
