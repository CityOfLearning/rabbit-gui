package com.rabbit.gui.component.control;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.rabbit.gui.component.GuiWidget;
import com.rabbit.gui.render.Renderer;
import com.rabbit.gui.render.TextRenderer;
import com.rabbit.gui.utils.ControlCharacters;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MultiTextbox extends TextBox {

	protected ScrollBar scrollBar;

	protected int maxStringLength = 1000;

	protected int listHeight;

	public MultiTextbox(int xPos, int yPos, int width, int height) {
		super(xPos, yPos, width, height);
		listHeight = height;
		scrollBar = new ScrollBar((getX() + getWidth()) - 5, getY(), 10, getHeight(), 20).setVisiblie(false);
	}

	public MultiTextbox(int xPos, int yPos, int width, int height, String initialText) {
		super(xPos, yPos, width, height, initialText);
		listHeight = height;
		scrollBar = new ScrollBar((getX() + getWidth()) - 5, getY(), 10, getHeight(), 20).setVisiblie(false);
	}

	@Override
	protected void drawBox() {
		if (isVisible()) {
			GlStateManager.pushMatrix();
			{
				if (isBackgroundVisible()) {
					drawTextBoxBackground();
				}
				TextRenderer.getFontRenderer().setUnicodeFlag(drawUnicode);
				int color = isEnabled ? getEnabledColor() : getDisabledColor();
				boolean renderCursor = isFocused() && (((cursorCounter / 6) % 2) == 0);
				int startLine = getStartLineY();
				int maxLineAmount = (height / TextRenderer.getFontRenderer().FONT_HEIGHT) + startLine;
				List<String> lines = getLines();
				int charCount = 0;
				int lineCount = 0;
				int maxWidth = width - 4;
				for (int i = 0; i < lines.size(); ++i) {
					String wholeLine = lines.get(i);
					String line = "";
					char[] chars = wholeLine.toCharArray();
					for (char c : chars) {
						if (TextRenderer.getFontRenderer().getStringWidth(line + c) > maxWidth) {
							if ((lineCount >= startLine) && (lineCount < maxLineAmount)) {
								TextRenderer.getFontRenderer().drawString(line, getX() + 4, getY() + 4
										+ ((lineCount - startLine) * TextRenderer.getFontRenderer().FONT_HEIGHT),
										color);
							}
							line = "";
							lineCount++;
						}
						if (renderCursor && (charCount == getCursorPosition()) && (lineCount >= startLine)
								&& (lineCount < maxLineAmount)) {
							int cursorX = getX() + TextRenderer.getFontRenderer().getStringWidth(line) + 3;
							int cursorY = getY()
									+ ((lineCount - startLine) * TextRenderer.getFontRenderer().FONT_HEIGHT) + 4;
							if ((getText().length() == getCursorPosition()) || (c == '\n')) {
								TextRenderer.getFontRenderer().drawString("_", cursorX, cursorY, 0xFFFFFFFF);
							} else {
								Renderer.drawRect(cursorX, cursorY, cursorX + 1, cursorY + 10, 0xFFFFFFFF);
							}
						}
						charCount++;
						line += c;
					}
					if ((lineCount >= startLine) && (lineCount < maxLineAmount)) {
						TextRenderer.getFontRenderer().drawString(line, getX() + 4,
								getY() + 4 + ((lineCount - startLine) * TextRenderer.getFontRenderer().FONT_HEIGHT),
								color);
						if (renderCursor && (charCount == getCursorPosition())) {
							int cursorX = getX() + TextRenderer.getFontRenderer().getStringWidth(line) + 3;
							int cursorY = getY()
									+ ((lineCount - startLine) * TextRenderer.getFontRenderer().FONT_HEIGHT) + 4;
							if ((getText().length() == getCursorPosition()) || (getText().toCharArray()[Math
									.min(charCount, getText().toCharArray().length - 1)] == '\n')) {
								TextRenderer.getFontRenderer().drawString("_", cursorX, cursorY, 0xFFFFFFFF);
							} else {
								Renderer.drawRect(cursorX, cursorY, cursorX + 1,
										cursorY + TextRenderer.getFontRenderer().FONT_HEIGHT, 0xFFFFFFFF);
							}
						}
					}
					++lineCount;
					++charCount;
				}
				listHeight = lineCount * TextRenderer.getFontRenderer().FONT_HEIGHT;
				scrollBar.setVisiblie(listHeight > (height - 4));
				scrollBar.setHandleMouseWheel((listHeight > (height - 4)) && isUnderMouse(Mouse.getX(), Mouse.getY()));
				scrollBar.setScrollerSize((getScrollerSize()));
				GlStateManager.resetColor();
			}
			GlStateManager.popMatrix();
			TextRenderer.getFontRenderer().setUnicodeFlag(false);
		}
	}

	public List<String> getLines() {
		List<String> lines = new ArrayList<>();
		StringBuffer currentLine = new StringBuffer();
		char[] chars = getText().toCharArray();
		for (char symbol : chars) {
			if ((symbol == '\r') || (symbol == '\n')) {
				lines.add(currentLine.toString());
				currentLine.delete(0, currentLine.length());
			} else {
				currentLine.append(symbol);
			}
		}
		lines.add(currentLine.toString());
		return lines;
	}

	@Override
	public int getMaxLength() {
		return maxStringLength;
	}

	protected int getScrollerSize() {
		return (int) (((1F * height) / listHeight) * (height - 4));
	}

	public int getStartLineY() {
		if (scrollBar != null) {
			float scrolled = scrollBar.getScrolledAmt();
			return MathHelper.ceil((scrolled * getHeight()) / TextRenderer.getFontRenderer().FONT_HEIGHT);
		}
		return 0;
	}

	@Override
	protected boolean handleInput(char typedChar, int typedKeyIndex) {
		String originalText = getText();
		switch (typedKeyIndex) {
		case Keyboard.KEY_RETURN:
			setTextWithEvent(originalText.substring(0, getCursorPosition()) + "\n"
					+ originalText.substring(getCursorPosition()));
			setCursorPosition(getCursorPosition() + 1);
			return true;
		case Keyboard.KEY_UP: {
			List<String> lines = getLines();
			int charCount = 0;
			int lineCount = 0;
			int startLine = getStartLineY();
			int maxWidth = width - 4;
			int prevLineLength = getText().length();
			for (int i = 0; i < lines.size(); ++i) {
				String wholeLine = lines.get(i);
				String line = "";
				char[] chars = wholeLine.toCharArray();
				for (char c : chars) {
					if (TextRenderer.getFontRenderer().getStringWidth(line + c) > maxWidth) {
						line = "";
						lineCount++;
					}
					if ((charCount == getCursorPosition())) {
						setCursorPosition(Math.max(0,
								charCount - (prevLineLength < line.length() ? line.length() + 1 : prevLineLength + 1)));
						return true;
					}
					charCount++;
					line += c;
				}
				if ((lineCount >= startLine)) {
					if ((charCount == getCursorPosition())) {
						setCursorPosition(Math.max(0,
								charCount - (prevLineLength < line.length() ? line.length() + 1 : prevLineLength + 1)));
						return true;
					}
				}
				prevLineLength = wholeLine.length();
				++lineCount;
				++charCount;
			}
			return true;
		}
		case Keyboard.KEY_DOWN: {
			List<String> lines = getLines();
			int charCount = 0;
			int startLine = getStartLineY();
			int maxWidth = width - 4;
			for (int i = startLine; i < lines.size(); ++i) {
				String wholeLine = lines.get(i);
				String line = "";
				char[] chars = wholeLine.toCharArray();
				for (char c : chars) {
					if (TextRenderer.getFontRenderer().getStringWidth(line + c) > maxWidth) {
						line = "";
					}
					if ((charCount == getCursorPosition())) {
						if ((i + 1) >= lines.size()) {
							setCursorPosition(getText().length());
							return true;
						} else {
							String nextLine = lines.get(i + 1);
							if (nextLine.length() >= wholeLine.length()) {
								setCursorPosition(Math.min(getText().length(), charCount + wholeLine.length() + 1));
								return true;
							} else {
								setCursorPosition(Math.min(getText().length(),
										charCount + (wholeLine.length() - line.length()) + nextLine.length() + 1));
								return true;
							}
						}
					}
					charCount++;
					line += c;
				}
				if ((charCount == getCursorPosition())) {
					if ((i + 1) >= lines.size()) {
						setCursorPosition(getText().length());
						return true;
					} else {
						String nextLine = lines.get(i + 1);
						if (nextLine.length() >= wholeLine.length()) {
							setCursorPosition(Math.min(getText().length(), charCount + wholeLine.length() + 1));
							return true;
						} else {
							setCursorPosition(Math.min(getText().length(),
									charCount + (wholeLine.length() - line.length()) + nextLine.length() + 1));
							return true;
						}
					}
				}
				++charCount;
			}
			return true;
		}
		default:
			return super.handleInput(typedChar, typedKeyIndex);
		}
	}

	@Override
	protected void handleKey(char typedChar, int typedIndex) {
		if (!isFocused()) {
			return;
		}

		boolean isSpecialCharCombination = handleSpecialCharComb(typedChar, typedIndex);
		if (!isSpecialCharCombination) {
			handleInput(typedChar, typedIndex);
		}
	}

	@Override
	protected boolean handleMouseClick(int posX, int posY, int mouseButtonIndex, boolean overlap) {
		boolean clicked = isEnabled() && !overlap && isTextBoxUnderMouse(posX, posY);
		setIsFocused(clicked);
		if (isFocused() && (mouseButtonIndex == 0)) {
			TextRenderer.getFontRenderer().setUnicodeFlag(drawUnicode);
			int lenght = posX - getX();
			String temp = TextRenderer.getFontRenderer()
					.trimStringToWidth(text.substring(Math.max(0, Math.min(scrollOffset, text.length()))), getWidth());
			setCursorPosition(TextRenderer.getFontRenderer().trimStringToWidth(temp, lenght).length()
					+ Math.max(0, Math.min(scrollOffset, text.length())));
			int x = posX - getX();
			int y = ((posY - getY() - 4) / TextRenderer.getFontRenderer().FONT_HEIGHT) + getStartLineY();
			cursorPos = 0;
			List<String> lines = getLines();
			int charCount = 0;
			int lineCount = 0;
			int maxWidth = getWidth() - 4;
			for (int i = 0; i < lines.size(); ++i) {
				String wholeLine = lines.get(i);
				String line = "";
				char[] chars = wholeLine.toCharArray();
				for (char c : chars) {
					setCursorPosition(charCount);
					if (TextRenderer.getFontRenderer().getStringWidth(line + c) > maxWidth) {
						lineCount++;
						line = "";
						if (y < lineCount) {
							break;
						}
					}
					if ((lineCount == y) && (x <= TextRenderer.getFontRenderer().getStringWidth(line + c))) {
						return clicked;
					}
					charCount++;
					line += c;
				}
				setCursorPosition(charCount);
				charCount++;
				lineCount++;
				if (y < lineCount) {
					break;
				}

			}
			if (y >= lineCount) {
				setCursorPosition(getText().length());
			}
			TextRenderer.getFontRenderer().setUnicodeFlag(false);
		}
		return clicked;
	}

	@Override
	protected boolean handleSpecialCharComb(char typedChar, int typedIndex) {
		switch (typedChar) {
		case ControlCharacters.CtrlA:
			setCursorPosition(getText().length());
			setSelectionPos(0);
			System.out.println("Selected Text now set to: " + getSelectedText());
			return true;
		case ControlCharacters.CtrlC:
			GuiScreen.setClipboardString(getSelectedText());
			return true;
		case ControlCharacters.CtrlV:
			if (isEnabled()) {
				pushText(GuiScreen.getClipboardString());
			}
			return true;
		case ControlCharacters.CtrlX:
			GuiScreen.setClipboardString(getSelectedText());
			if (isEnabled()) {
				pushText("");
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean isUnderMouse(int mouseX, int mouseY) {
		return (mouseX >= getX()) && (mouseX <= (getX() + getWidth())) && (mouseY >= getY())
				&& (mouseY <= (getY() + getHeight()));
	}

	@Override
	public void pushText(String text) {
		String result = "";
		int i = getCursorPosition() < selectionEnd ? getCursorPosition() : selectionEnd;
		int j = getCursorPosition() < selectionEnd ? selectionEnd : getCursorPosition();
		int k = getMaxLength() - getText().length() - (i - selectionEnd);

		if (getText().length() > 0) {
			result += getText().substring(0, i);
		}
		int end = 0;
		if (k < text.length()) {
			result = result + text.substring(0, k);
			end = k;
		} else {
			result = result + text;
			end = text.length();
		}
		if ((getText().length() > 0) && (j < getText().length())) {
			result = result + getText().substring(j);
		}
		setTextWithEvent(result);
		moveCursorBy((i - selectionEnd) + end);
	}

	@Override
	public void setup() {
		scrollBar.setX((getX() + getWidth()) - (scrollBar.getWidth() / 2));
		registerComponent(scrollBar);

	}

	@Override
	public GuiWidget setX(int x) {
		super.setX(x);
		if (scrollBar != null) {
			scrollBar.setX((x + getWidth()) - (scrollBar.getWidth() / 2));
		}
		return this;
	}

	@Override
	public GuiWidget setY(int y) {
		super.setY(y);
		if (scrollBar != null) {
			scrollBar.setY(y);
		}
		return this;
	}
}
