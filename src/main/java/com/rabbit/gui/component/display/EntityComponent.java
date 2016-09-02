package com.rabbit.gui.component.display;

import com.rabbit.gui.component.GuiWidget;
import com.rabbit.gui.layout.LayoutComponent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityComponent extends GuiWidget {

	EntityLiving entity;

	@LayoutComponent
	int rotation = 0;

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
		GlStateManager.resetColor();
		GlStateManager.enableColorMaterial();
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, 75.0f);
		float scale = 1.0f;
		if (entity.height > 2.4) {
			scale = 2.0f / entity.height;
		}
		GlStateManager.scale(-30.0f * scale * zoom, 30.0f * scale * zoom, 30.0f * scale * zoom);
		GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
		RenderHelper.enableStandardItemLighting();
		float f2 = entity.renderYawOffset;
		float f3 = entity.rotationYaw;
		float f4 = entity.rotationPitch;
		float f5 = entity.rotationYawHead;
		float f6 = (x) - mouseX;
		float f7 = (y) - (50.0f * scale * zoom) - mouseY;
		GlStateManager.rotate(135.0f, 0.0f, 1.0f, 0.0f);
		GlStateManager.rotate(-135.0f, 0.0f, 1.0f, 0.0f);
		GlStateManager.rotate(-(float) Math.atan(f7 / 40.0f) * 20.0f, 1.0f, 0.0f, 0.0f);
		entity.renderYawOffset = rotation;
		entity.rotationYaw = ((float) Math.atan(f6 / 80.0f) * 40.0f) + rotation;
		entity.rotationPitch = -(float) Math.atan(f7 / 40.0f) * 20.0f;
		entity.rotationYawHead = entity.rotationYaw;
		Minecraft.getMinecraft().getRenderManager().playerViewY = 180.0f;
		Minecraft.getMinecraft().getRenderManager().renderEntityWithPosYaw(entity, 0.0, 0.0, 0.0, 0.0f, 1.0f);
		float n = f2;
		entity.renderYawOffset = n;
		entity.prevRenderYawOffset = n;
		float n2 = f3;
		entity.rotationYaw = n2;
		entity.prevRotationYaw = n2;
		float n3 = f4;
		entity.rotationPitch = n3;
		entity.prevRotationPitch = n3;
		float n4 = f5;
		entity.rotationYawHead = n4;
		entity.prevRotationYawHead = n4;
		GlStateManager.popMatrix();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableRescaleNormal();
		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.disableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}
}
