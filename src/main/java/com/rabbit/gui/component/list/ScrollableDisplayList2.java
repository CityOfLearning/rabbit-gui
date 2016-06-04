package com.rabbit.gui.component.list;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.rabbit.gui.component.control.ScrollBar;
import com.rabbit.gui.component.control.ScrollBarHorizontal;
import com.rabbit.gui.component.list.entries.ListEntry;
import com.rabbit.gui.layout.LayoutComponent;
import com.rabbit.gui.utils.Geometry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@LayoutComponent
public class ScrollableDisplayList2 extends DisplayList {

	protected ScrollBar scrollBar;
	protected ScrollBarHorizontal scrollBarH;

	public ScrollableDisplayList2(int xPos, int yPos, int width, int height, int slotHeight, List<ListEntry> content) {
		super(xPos, yPos, width, height, slotHeight, content);
	}

	/**
	 * Returns true if content height of list is not more that list actual
	 * height
	 */
	private boolean canFit() {
		return (content.size() * slotHeight) < height;
	}

	@Override
	protected void drawListContent(int mouseX, int mouseY) {
		scrollBar.setVisiblie(!canFit());
		scrollBar.setHandleMouseWheel(!canFit());
		scrollBar.setScrollerSize(getScrollerSize());
		int scale = Geometry.computeScaleFactor();
		for (int i = 0; i < content.size(); i++) {
			ListEntry entry = content.get(i);
			int slotPosX = ((getX() + (i * (width/6))) - (int) (((width/6) * scrollBarH.getProgress())));
			int slotPosY = ((getY() + (i * slotHeight)) - (int) ((slotHeight * scrollBar.getProgress() * content.size())
					- (((height - slotHeight) * (scrollBar.getProgress())) / 1)));
			int slotWidth = width + (width/6);
			int slotHeight = this.slotHeight;
			if ((slotPosY < (getY() + height)) && ((slotPosY + slotHeight) > getY())) {
				GL11.glPushMatrix();
				GL11.glEnable(GL11.GL_SCISSOR_TEST);
				Minecraft mc = Minecraft.getMinecraft();
				GL11.glScissor(getX() * scale, mc.displayHeight - ((getY() + getHeight()) * scale), getWidth() * scale,
						getHeight() * scale);
				GlStateManager.resetColor();
				entry.onDraw(this, slotPosX, slotPosY, slotWidth, slotHeight, mouseX, mouseY);
				GL11.glDisable(GL11.GL_SCISSOR_TEST);
				GL11.glPopMatrix();
			}
		}
	}

	private int getScrollerSize() {
		return (int) Math.min(Math.max((int) (((1F * height) / (content.size() * slotHeight)) * (height - 4)) * 2, 15),
				height * .8);
	}

	@Override
	protected void handleMouseClickList(int mouseX, int mouseY) {
		for (int i = 0; i < content.size(); i++) {
			ListEntry entry = content.get(i);
			entry.setSelected(false);
			int slotPosX = ((getX() + (i * (width/6))) - (int) (((width/6) * scrollBarH.getProgress())));
			int slotPosY = ((getY() + (i * slotHeight)) - (int) ((slotHeight * scrollBar.getProgress() * content.size())
					- (((height - slotHeight) * (scrollBar.getProgress())) / 1)));
			int slotWidth = width + (width/6);
			int slotHeight = this.slotHeight;
			boolean scrollbarActive = scrollBar.isScrolling() && scrollBar.isVisible();
			if (((slotPosY + slotHeight) <= (getY() + height)) && (slotPosY >= getY()) && !scrollbarActive) {
				boolean clickedOnEntry = Geometry.isDotInArea(slotPosX, slotPosY, slotWidth, slotHeight, mouseX,
						mouseY);
				if (clickedOnEntry) {
					entry.onClick(this, mouseX, mouseY);
				}
			}
		}
	}

	@Override
	public void onDraw(int mouseX, int mouseY, float partialTicks) {
		super.onDraw(mouseX, mouseY, partialTicks);

	}

	@Override
	public void setup() {
		super.setup();
		int scrollerSize = height / (content.isEmpty() ? 1 : content.size());
		if (scrollerSize < 10) {
			scrollerSize = 10;
		}
		if (content.size() < (height / slotHeight)) {
			scrollerSize = height - 4;
		}
		scrollBar = new ScrollBar((getX() + width) - 10, getY(), 10, height, scrollerSize);
		registerComponent(scrollBar);
		
		scrollBarH = new ScrollBarHorizontal(getX(), (getY() + height) - 10, width, 10, 10);
		registerComponent(scrollBarH);
	}

}
