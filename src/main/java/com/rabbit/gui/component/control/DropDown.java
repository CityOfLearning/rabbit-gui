package com.rabbit.gui.component.control;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.rabbit.gui.component.GuiWidget;
import com.rabbit.gui.component.Shiftable;
import com.rabbit.gui.component.WidgetList;
import com.rabbit.gui.layout.LayoutComponent;
import com.rabbit.gui.render.Renderer;
import com.rabbit.gui.render.TextRenderer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@LayoutComponent
public class DropDown<T> extends GuiWidget implements WidgetList<T>, Shiftable {

	public class DropDownElement<K> {

		private final int itemIndex;
		private final K itemValue;
		private final String itemName;

		public DropDownElement(int itemIndex, K itemValue, String itemName) {
			this.itemIndex = itemIndex;
			this.itemValue = itemValue;
			this.itemName = itemName;
		}

		public int getItemIndex() {
			return this.itemIndex;
		}

		public String getItemName() {
			return this.itemName;
		}

		public K getValue() {
			return this.itemValue;
		}
	}

	@FunctionalInterface
	public interface ItemSelectedListener<T> {
		public void onItemSelected(DropDown<T> dropdown, String selected);
	}

	protected Map<String, DropDownElement<T>> content = new TreeMap<String, DropDownElement<T>>();

	private Button dropButton;

	@LayoutComponent
	protected String text;

	protected String selected;

	protected String hovered;

	protected boolean isUnrolled = false;

	@LayoutComponent
	protected boolean isVisible = true;

	@LayoutComponent
	protected boolean isEnabled = true;

	protected ResourceLocation texture = new ResourceLocation("textures/gui/widgets.png");

	protected ItemSelectedListener<T> itemSelectedListener;

	public DropDown(int xPos, int yPos, int width) {
		this(xPos, yPos, width, "");
	}

	public DropDown(int xPos, int yPos, int width, String text) {
		super(xPos, yPos, width, 12);
		this.text = text;
		this.initDropButton();
	}

	public DropDown(int xPos, int yPos, int width, T... values) {
		this(xPos, yPos, width);
		this.addAll(values);
		if (values.length > 0) {
			this.setDefaultItem(String.valueOf(values[0]));
		}
	}

	public DropDown<T> add(String key, T value) {
		this.getContent().put(key, new DropDownElement<T>(this.getContent().size(), value, key));
		return this;
	}

	@Override
	public DropDown<T> add(T value) {
		return this.add(String.valueOf(value), value);
	}

	@Override
	public DropDown<T> addAll(T... values) {
		Arrays.stream(values).forEach(this::add);
		return this;
	}

	public DropDown<T> addAndSetDefault(T value) {
		return this.addItemAndSetDefault(String.valueOf(value), value);
	}

	public DropDown<T> addItemAndSetDefault(String name, T value) {
		this.add(name, value);
		this.setDefaultItem(name);
		return this;
	}

	@Override
	public DropDown<T> clear() {
		this.getContent().clear();
		return this;
	}

	private void drawDropDownBackground() {
		Renderer.drawRect(getX() - 1, getY() - 1, getX() + getWidth() + 1, getY() + getHeight() + 1, -6250336);
		Renderer.drawRect(getX(), getY(), (getX() + getWidth()) - 13, getY() + getHeight(), -16777216);
	}

	private void drawExpandedList(int mouseX, int mouseY, float partialTicks) {
		GlStateManager.resetColor();
		List<String> keys = new ArrayList<String>(this.getContent().keySet());
		int unrollHeight = keys.size() * getHeight();
		Renderer.drawRect(getX() - 1, getY() + getHeight(), getX() + getWidth() + 1,
				getY() + getHeight() + unrollHeight + 1, -6250336);
		Renderer.drawRect(getX(), getY() + getHeight() + 1, getX() + getWidth(), getY() + getHeight() + unrollHeight,
				-16777216);
		boolean hoverUnrolledList = (mouseX >= getX()) && (mouseX <= (getX() + getWidth())) && (mouseY >= getY())
				&& (mouseY <= (getY() + getHeight() + unrollHeight + 1));
		for (int index = 0; index < keys.size(); index++) {
			String itemIdentifier = keys.get(index);
			int yPos = getY() + getHeight() + (getHeight() / 8) + (index * 12);
			boolean hoverSlot = (mouseX >= getX()) && (mouseX <= (getX() + getWidth())) && (mouseY >= yPos)
					&& (mouseY <= (yPos + 12));
			boolean selectedSlot = hoverSlot
					|| (!hoverUnrolledList && itemIdentifier.equalsIgnoreCase(this.getSelectedIdentifier()));
			this.drawSlot(itemIdentifier, getX(), yPos, getWidth(), getHeight(), selectedSlot);
		}
	}

	private void drawSlot(String item, int xPos, int yPos, int width, int height, boolean background) {
		this.drawSlot(item, xPos, yPos, width, height, background, 2);
	}

	private void drawSlot(String item, int xPos, int yPos, int width, int height, boolean background, int drawOffset) {
		String text = TextRenderer.getFontRenderer().trimStringToWidth(item, width - drawOffset);
		GlStateManager.resetColor();
		Color color = Color.white;
		if (background) {
			Renderer.drawRect(xPos, yPos, xPos + width, (yPos + height) - (height / 8), 0xFFFFFFFF);
			color = Color.black;
		}
		TextRenderer.renderString(xPos + 2, yPos + (getHeight() / 8), text, color);
	}

	private boolean expandedListUnderMouse(int mouseX, int mouseY) {
		return (mouseX >= (getX() - 1)) && (mouseX < (getX() + getWidth() + 1)) && (mouseY >= (getY() - 1))
				&& (mouseY < ((getY() + getHeight() + (this.getContent().size() * 12)) - 1));
	}

	@Override
	public Map<String, DropDownElement<T>> getContent() {
		return this.content;
	}

	public DropDownElement<T> getElement(String identifier) {
		return this.getContent().get(identifier);
	}

	public ItemSelectedListener<T> getItemSelectedListener() {
		return this.itemSelectedListener;
	}

	public DropDownElement<T> getSelectedElement() {
		return this.getElement(this.selected);
	}

	public String getSelectedIdentifier() {
		return this.selected;
	}

	private void initDropButton() {
		this.dropButton = new Button((getX() + getWidth()) - 12, getY(), 12, 12, "\u25BC");
	}

	public boolean isEmpty() {
		return this.content != null ? this.content.isEmpty() : true;
	}

	public boolean isEnabled() {
		return this.isEnabled;
	}

	public boolean isVisible() {
		return this.isVisible;
	}

	@Override
	public void onDraw(int mouseX, int mouseY, float partialTicks) {
		if (this.isEmpty()) {
			this.setIsEnabled(false);
		}
		if (this.isVisible()) {
			this.underMouse(mouseX, mouseY);
			this.drawDropDownBackground();
			if (this.isUnrolled) {
				this.drawExpandedList(mouseX, mouseY, partialTicks);
			}

			if (this.selected != null) {
				this.drawSlot(this.getSelectedIdentifier(), getX(), getY(), getWidth(), getHeight(), false, 14);
			}
		}
		super.onDraw(mouseX, mouseY, partialTicks);
	}

	@Override
	public boolean onMouseClicked(int posX, int posY, int mouseButtonIndex, boolean overlap) {
		super.onMouseClicked(posX, posY, mouseButtonIndex, overlap);
		boolean clicked = !overlap
				&& (this.isUnrolled ? this.expandedListUnderMouse(posX, posY) : this.underMouse(posX, posY));
		if (!clicked) {
			this.isUnrolled = false;
		}
		if (clicked && this.isEnabled()) {

			if (this.underMouse(posX, posY) && !this.isEmpty()) {
				this.isUnrolled = !this.isUnrolled;
			}

			if (this.isUnrolled) {
				List<String> contentKeys = new ArrayList<>(this.getContent().keySet());
				for (int index = 0; index < contentKeys.size(); index++) {
					int yPos = getY() + getHeight() + (getHeight() / 8) + (index * 12);
					boolean hoverItem = (posX >= getX()) && (posX <= (getX() + getWidth())) && (posY >= yPos)
							&& (posY <= (yPos + 12));
					if (hoverItem) {
						this.selected = contentKeys.get(index);
						if (this.getItemSelectedListener() != null) {
							this.getItemSelectedListener().onItemSelected(this, this.selected);
						}
						this.isUnrolled = false;
					}
				}
			}
		}
		return clicked;
	}

	@Override
	public DropDown<T> remove(T object) {
		this.content.remove(String.valueOf(object));
		return this;
	}

	private void setDefaultItem(String name) {
		if (this.getContent().containsKey(name)) {
			this.selected = name;
		}
	}

	@Override
	public DropDown<T> setId(String id) {
		assignId(id);
		return this;
	}

	public DropDown<T> setIsEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
		this.dropButton.setIsEnabled(isEnabled);
		return this;
	}

	public DropDown<T> setIsVisible(boolean isVisible) {
		this.isVisible = isVisible;
		this.dropButton.setIsVisible(isVisible);
		return this;
	}

	public DropDown<T> setItemSelectedListener(ItemSelectedListener<T> listener) {
		this.itemSelectedListener = listener;
		return this;
	}

	@Override
	public void setup() {
		registerComponent(this.dropButton);
	}

	@Override
	public void shiftX(int x) {
		setX(getX() + x);
	}

	@Override
	public void shiftY(int y) {
		setY(getY() + y);
	}

	private boolean underMouse(int x, int y) {
		return (x >= getX()) && (x <= (getX() + getWidth())) && (y >= getY()) && (y <= (getY() + getHeight()));
	}
}
