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
public class ScrollBar extends GuiWidget {

	public static interface OnProgressChanged {
		void onProgressChanged(ScrollBar bar, float modifier);
	}

	protected float scrolled = 0;

	protected int scrollerSize;

	protected boolean isScrolling = false;

	protected boolean visible = true;

	protected OnProgressChanged progressChangedListener = (bar, mod) -> {
	};

	protected boolean handleMouseWheel;

	public ScrollBar(int xPos, int yPos, int width, int height, int scrollerSize) {
		super(xPos, yPos, width, height);
		this.scrollerSize = scrollerSize;
	}

	/**
	 * Calculates scroller progress based on mouse y pos
	 *
	 * @param mouseY
	 */
	private void calculateScroller(int mouseY) {
		if (isScrolling) {
			float magic = (((mouseY - getY()) + 2) - 10F) / ((getY() + height) - (getY() + 2) - 15.0F);
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
			calculateScroller(mouseY);
			Minecraft.getMinecraft().renderEngine
					.bindTexture(new ResourceLocation("textures/gui/container/creative_inventory/tab_items.png"));
			Renderer.drawContinuousTexturedBox(getX(), getY(), 174 - 1, 17 - 1, width, height, 14 + 2, 112 + 2, 2, 2, 2,
					2);
			int scrollerHeight = (int) (getY() + 2 + (scrolled * (height - 4 - scrollerSize)));
			drawScroller(getX() + 2, scrollerHeight, width - 4, scrollerSize);
		}
	}

	@Override
	public boolean onMouseClicked(int posX, int posY, int mouseButtonIndex, boolean overlap) {
		super.onMouseClicked(posX, posY, mouseButtonIndex, overlap);
		isScrolling = !overlap && Geometry.isDotInArea(getX() + 2,
				(int) (getY() + 2 + (scrolled * (height - scrollerSize))), width - 4, scrollerSize, posX, posY);
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

	public ScrollBar setHandleMouseWheel(boolean status) {
		handleMouseWheel = status;
		return this;
	}

	public ScrollBar setProgress(float scroll) {
		scrolled = scroll;
		revalidateScroller();
		return this;
	}

	public ScrollBar setProgressChangedListener(OnProgressChanged progressChangedListener) {
		this.progressChangedListener = progressChangedListener;
		return this;
	}

	public ScrollBar setScrollerSize(int size) {
		scrollerSize = size;
		return this;
	}

	public ScrollBar setVisiblie(boolean visible) {
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
