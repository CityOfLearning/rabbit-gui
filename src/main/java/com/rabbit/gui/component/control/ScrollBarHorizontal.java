package com.rabbit.gui.component.control;

import org.lwjgl.input.Mouse;

import com.rabbit.gui.component.GuiWidget;
import com.rabbit.gui.render.Renderer;
import com.rabbit.gui.utils.Geometry;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ScrollBarHorizontal extends GuiWidget {

	public static interface OnProgressChanged {
		void onProgressChanged(ScrollBarHorizontal bar, float modifier);
	}

	protected float scrolled = 0;

	protected int scrollerSize;

	protected boolean isScrolling = false;

	protected boolean visible = true;

	protected OnProgressChanged progressChangedListener = (bar, mod) -> {
	};

	protected boolean handleMouseWheel;

	public ScrollBarHorizontal(int xPos, int yPos, int width, int height, int scrollerSize) {
		super(xPos, yPos, width, height);
		this.scrollerSize = scrollerSize;
	}

	/**
	 * Calculates scroller progress based on mouse x pos
	 *
	 * @param mouseX
	 */
	private void calculateScroller(int mouseX) {
		if (isScrolling) {
			float magic = (((mouseX - getX()) + 2) - 10F) / ((getX() + width) - (getX() + 2) - 15.0F);
			updateProgress(magic - scrolled);
		}
	}

	private void drawScroller(int xPos, int yPos, int width, int height) {
		Minecraft.getMinecraft().renderEngine
				.bindTexture(new ResourceLocation("textures/gui/container/creative_inventory/tabs.png"));
		Renderer.drawContinuousTexturedBox(xPos, yPos, isScrolling() ? 244 : 232, 0, width, height, 12, 15, 1, 2, 2, 2);
	}

	/**
	 * Returns a float value between 0 and 1,
	 */
	public float getProgress() {
		return scrolled;
	}

	public OnProgressChanged getProgressChangedListener() {
		return progressChangedListener;
	}

	public boolean isScrolling() {
		return isScrolling;
	}

	public boolean isVisible() {
		return visible;
	}

	@Override
	public void onDraw(int mouseX, int mouseY, float partialTicks) {
		super.onDraw(mouseX, mouseY, partialTicks);
		if (isVisible()) {
			calculateScroller(mouseX);
			Minecraft.getMinecraft().renderEngine
					.bindTexture(new ResourceLocation("textures/gui/container/creative_inventory/tab_items.png"));
			Renderer.drawContinuousTexturedBox(getX(), getY(), 174 - 1, 17 - 1, width, height, 14 + 2, 112 + 2, 2, 2, 2,
					2);
			int scrollerWidth = (int) (getX() + 2 + (scrolled * (width - 4 - scrollerSize)));
			drawScroller(scrollerWidth, getY() + 2, scrollerSize, height - 4);
		}
	}

	@Override
	public boolean onMouseClicked(int posX, int posY, int mouseButtonIndex, boolean overlap) {
		super.onMouseClicked(posX, posY, mouseButtonIndex, overlap);
		isScrolling = !overlap && Geometry.isDotInArea((int) (getX() + 2 + (scrolled * (width - scrollerSize))), getY() + 2, scrollerSize, height - 4, posX, posY);
		return isScrolling;
	}

	@Override
	public void onMouseInput() {
		super.onMouseInput();
		if (shouldHandleMouseWheel()) {
			double delta = Mouse.getDWheel();
			if (delta < 0) {
				updateProgress(0.10F);
			}
			if (delta > 0) {
				updateProgress(-0.10F);
			}
		}
	}

	@Override
	public void onMouseRelease(int mouseX, int mouseY) {
		super.onMouseRelease(mouseX, mouseY);
		isScrolling = false;
	}

	private void revalidateScroller() {
		if (scrolled < 0) {
			scrolled = 0;
		}
		if (scrolled > 1) {
			scrolled = 1;
		}
	}

	public ScrollBarHorizontal setHandleMouseWheel(boolean status) {
		handleMouseWheel = status;
		return this;
	}

	public ScrollBarHorizontal setProgress(float scroll) {
		scrolled = scroll;
		revalidateScroller();
		return this;
	}

	public ScrollBarHorizontal setProgressChangedListener(OnProgressChanged progressChangedListener) {
		this.progressChangedListener = progressChangedListener;
		return this;
	}

	public ScrollBarHorizontal setScrollerSize(int size) {
		scrollerSize = size;
		return this;
	}

	public ScrollBarHorizontal setVisiblie(boolean visible) {
		this.visible = visible;
		return this;
	}

	public boolean shouldHandleMouseWheel() {
		return handleMouseWheel;
	}

	public void updateProgress(float modifier) {
		setProgress(scrolled + modifier);
		getProgressChangedListener().onProgressChanged(this, modifier);
	}

}
