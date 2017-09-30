package com.rabbit.gui.component.hud.overlay;

import java.util.ArrayList;
import java.util.List;

import com.rabbit.gui.component.GuiWidget;
import com.rabbit.gui.component.hud.Hud;

import net.minecraft.client.renderer.GlStateManager;

public class Overlay extends Hud {

	private static List<GuiWidget> widgets = new ArrayList<GuiWidget>();
	public static boolean isHidden = false;

	// this techincally overwrites the super class
	public static void draw() {
		updateWindowScale();
		GlStateManager.disableDepth();
		GlStateManager.depthMask(false);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableTexture2D();
		GlStateManager.disableLighting();

		for(GuiWidget widget: widgets) {
			widget.onDraw(0, 0, 1);
		}
		
		GlStateManager.disableLighting();
		GlStateManager.depthMask(true);
		GlStateManager.enableDepth();
	}

	public static void addWidget(GuiWidget widget) {
		widgets.add(widget);
	}
	
	public static List<GuiWidget> getWidgets(){
		return widgets;
	}
	
}
