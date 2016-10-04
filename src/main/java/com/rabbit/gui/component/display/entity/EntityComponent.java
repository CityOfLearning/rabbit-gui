package com.rabbit.gui.component.display.entity;

import org.lwjgl.input.Mouse;

import com.rabbit.gui.component.GuiWidget;
import com.rabbit.gui.component.IGui;
import com.rabbit.gui.layout.LayoutComponent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityComponent extends GuiWidget {

	EntityLivingBase  entity;
	boolean dragging;

	@LayoutComponent
	float rotation = 0;

	@LayoutComponent
	float zoom = 1;

	public EntityComponent(int x, int y, int width, int height, EntityLiving entity) {
		this(x, y, width, height, entity, 0, 1);
	}

	public EntityComponent(int x, int y, int width, int height, EntityLiving entity, int rotation) {
		this(x, y, width, height, entity, rotation, 1);
	}

	public EntityComponent(int x, int y, int width, int height, EntityLiving entity, int rotation, float zoom) {
		super(x, y, width, height);
		this.entity = entity;
		this.rotation = rotation;
		this.zoom = zoom;
	}
	
	@Override
	public void onDraw(int mouseX, int mouseY, float partialTicks) {
		super.onDraw(mouseX, mouseY, partialTicks);
		GlStateManager.pushMatrix();
		GlStateManager.resetColor();
		GlStateManager.enableColorMaterial();
		GlStateManager.translate(x, y, 75.0f);
		float scale = 1.0f;
		if (entity.height > 2.4) {
			scale = 2.0f / entity.height;
		}
		GlStateManager.scale(-30.0f * scale * zoom, 30.0f * scale * zoom, 30.0f * scale * zoom);
		GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
		RenderHelper.enableStandardItemLighting();
		entity.renderYawOffset = rotation;
		entity.rotationYawHead = entity.renderYawOffset;
		Minecraft.getMinecraft().getRenderManager().renderEntityWithPosYaw(entity, 0.0, 0.0, 0.0, 0.0f, 1.0f);
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableRescaleNormal();
		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.disableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
		GlStateManager.popMatrix();
	}
	
	@Override
	public boolean onMouseClicked(int posX, int posY, int mouseButtonIndex, boolean overlap) {
		super.onMouseClicked(posX, posY, mouseButtonIndex, overlap);
		boolean clicked = !overlap;
		dragging = clicked;
		return clicked;
	}

	/**
	 * Called then mouse moved or scrolled
	 */
	@Override
	public void onMouseInput() {
		super.onMouseInput();
		if(dragging){
			rotation -= Mouse.getEventDX() *.5;
		}
	}

	@Override
	public void onMouseRelease(int mouseX, int mouseY) {
		super.onMouseRelease(mouseX, mouseY);
		dragging = false;
	}
}
