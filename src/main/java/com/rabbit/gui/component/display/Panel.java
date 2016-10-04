package com.rabbit.gui.component.display;

import java.util.ArrayList;
import java.util.List;

import com.rabbit.gui.component.GuiWidget;
import com.rabbit.gui.component.IGui;
import com.rabbit.gui.component.control.Button;
import com.rabbit.gui.utils.Geometry;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Panel extends GuiWidget {

	List<GuiWidget> panelComponents = new ArrayList<>();
	private boolean isDragging;
	private boolean isResizing;
	private int dragXDelta, dragYDelta;
	private int resizeXPos;

	public Panel(int xPos, int yPos, int width, int height) {
		super(xPos, yPos, width, height);
		isDragging = false;
		isResizing = false;
	}

	public void movePanel(int newX, int newY) {
		float xDelta = x - newX;
		float yDelta = y - newY;
		if ((xDelta == 0) && (yDelta == 0)) {
			return;
		}
		panelComponents.forEach(com -> {
			com.setX((int) (com.getX() - xDelta));
			com.setY((int) (com.getY() - yDelta));
		});
		x = newX;
		y = newY;
	}

	@Override
	public void onDraw(int xMouse, int yMouse, float partialTicks) {
		if (isDragging) {
			movePanel(xMouse - dragXDelta, yMouse - dragYDelta);
		}
		if (isResizing) {
			movePanel(xMouse - dragXDelta, y);
			resize(width + (resizeXPos - xMouse), height);
			resizeXPos = xMouse;
			// movePanel(xMouse - dragXDelta, yMouse - dragYDelta);
			// setSize(width + (resizeXPos-xMouse), height + (resizeYPos -
			// yMouse));
		}
		panelComponents.forEach(com -> com.onDraw(xMouse, yMouse, partialTicks));
	}

	@Override
	public void onKeyTyped(char typedChar, int typedIndex) {
		panelComponents.forEach(com -> com.onKeyTyped(typedChar, typedIndex));
	}

	@Override
	public boolean onMouseClicked(int posX, int posY, int mouseButtonIndex, boolean overlap) {
		super.onMouseClicked(posX, posY, mouseButtonIndex, overlap);
		// is it in the upper left corner
		isDragging = !overlap && Geometry.isDotInArea(x + 5, y, width - 5, 10, posX, posY);
		// isResizing = !overlap && Geometry.isDotInArea(x, y + 10, 5, height -
		// 10, posX, posY);
		if (isDragging) {
			dragXDelta = posX - x;
			dragYDelta = posY - y;
		}
		if (isResizing) {
			dragXDelta = posX - x;
			dragYDelta = posY - y;
			resizeXPos = posX;
		}

		panelComponents.forEach(com -> {
			if (com.onMouseClicked(posX, posY, mouseButtonIndex, overlap)) {
				isDragging = isResizing = false;
			}
		});

		return isDragging | isResizing;
	}

	@Override
	public void onMouseInput() {
		panelComponents.forEach(com -> com.onMouseInput());
	}

	@Override
	public void onMouseRelease(int mouseX, int mouseY) {
		super.onMouseRelease(mouseX, mouseY);
		panelComponents.forEach(com -> com.onMouseRelease(mouseX, mouseY));
		if (isDragging) {
			isDragging = false;
		}
		if (isResizing) {
			isResizing = false;
		}
	}

	@Override
	public void onUpdate() {
		panelComponents.forEach(com -> com.onUpdate());
	}

	/**
	 * Registers component as a part of this panel
	 *
	 * @param component
	 */
	@Override
	public void registerComponent(IGui component) {
		GuiWidget widget = (GuiWidget) component;
		// set the component in relative location to panel
		widget.setX(widget.getX() + x);
		widget.setY(widget.getY() + y);
		panelComponents.add(widget);
	}

	public void resize(int newWidth, int newHeight) {
		float widthDelta = newWidth / ((float) width);
		float heightDelta = newHeight / ((float) height);
		panelComponents.forEach(com -> {
			if (!(com instanceof Button)) {
				com.setWidth((int) (com.getWidth() * widthDelta));
				com.setHeight((int) (com.getHeight() * heightDelta));
			}
		});
		setSize(newWidth, newHeight);
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public void setup() {
		panelComponents.forEach(com -> com.setup());
	}
}
