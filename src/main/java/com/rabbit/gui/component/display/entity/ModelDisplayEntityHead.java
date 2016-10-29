package com.rabbit.gui.component.display.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

public class ModelDisplayEntityHead extends ModelBase {

	public ModelRenderer head;
	public ModelRenderer headwear;

	public ModelDisplayEntityHead() {
		textureWidth = 64;
		textureHeight = 64;
		head = new ModelRenderer(this, 0, 0);
		head.addBox(0F, 0F, 0F, 8, 8, 8);
		head.setRotationPoint(0.0F, 0.0F, 0.0F);
		head.setTextureSize(64, 64);
		headwear = new ModelRenderer(this, 32, 0);
		headwear.addBox(0F, 0F, 0F, 8, 8, 8, 0.5F);
		headwear.setRotationPoint(0.0F, 0.0F, 0.0F);
		headwear.setTextureSize(64, 64);
	}

	/**
	 * Sets the models various rotation angles then renders the model.
	 */
	@Override
	public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_,
			float p_78088_6_, float scale) {
		setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, scale, entityIn);

		GlStateManager.pushMatrix();
		GlStateManager.translate(0.0F, 1.0F, 0.0F);
		head.render(scale);
		headwear.render(scale);
		GlStateManager.popMatrix();

	}

	@Override
	public void setModelAttributes(ModelBase model) {
		super.setModelAttributes(model);
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch, float scaleFactor, Entity entityIn) {
		head.rotateAngleY = netHeadYaw / (180F / (float) Math.PI);
		head.rotateAngleX = headPitch / (180F / (float) Math.PI);
		head.rotationPointY = 0.0F;

		copyModelAngles(head, headwear);
	}
}
