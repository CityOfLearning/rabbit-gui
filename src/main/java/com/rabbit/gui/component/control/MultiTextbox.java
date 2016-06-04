package com.rabbit.gui.component.control;

import java.util.ArrayList;
import java.util.List;

import com.rabbit.gui.render.Renderer;
import com.rabbit.gui.render.TextRenderer;

import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MultiTextbox extends TextBox {

	private ScrollBar scrollBar;

	protected int maxStringLenght = 1000;

	private int listHeight;

	public MultiTextbox(int xPos, int yPos, int width, int height) {
		super(xPos, yPos, width, height);
		listHeight = height;
	}

	public MultiTextbox(int xPos, int yPos, int width, int height, String initialText) {
		super(xPos, yPos, width, height, initialText);
		listHeight = height;
	}

	@Override
	protected void drawBox() {
		if (isVisible()) {
			if (isBackgroundVisible()) {
				drawTextBoxBackground();
			}
			int color = 0xFFFFFF;
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
							TextRenderer.getFontRenderer()
									.drawString(
											line, getX()
													+ 4,
											getY() + 4 + ((lineCount - startLine)
													* TextRenderer.getFontRenderer().FONT_HEIGHT),
											color);
						}
						line = "";
						lineCount++;
					}
					if (renderCursor && (charCount == getCursorPosition()) && (lineCount >= startLine)
							&& (lineCount < maxLineAmount)) {
						int cursorX = getX() + TextRenderer.getFontRenderer().getStringWidth(line) + 3;
						int cursorY = getY() + ((lineCount - startLine) * TextRenderer.getFontRenderer().FONT_HEIGHT)
								+ 4;
						if (getText().length() == getCursorPosition()) {
							TextRenderer.getFontRenderer().drawString("_", cursorX, cursorY, color);
						} else {
							Renderer.drawRect(cursorX, cursorY, cursorX + 1, cursorY + 10, 0xFFFFFFFF);
						}
					}
					charCount++;
					line += c;
				}
				if ((lineCount >= startLine) && (lineCount < maxLineAmount)) {
					TextRenderer.getFontRenderer().drawString(line, getX() + 4,
							getY() + 4 + ((lineCount - startLine) * TextRenderer.getFontRenderer().FONT_HEIGHT), color);
					if (renderCursor && (charCount == getCursorPosition())) {
						int cursorX = getX() + TextRenderer.getFontRenderer().getStringWidth(line) + 3;
						int cursorY = getY() + ((lineCount - startLine) * TextRenderer.getFontRenderer().FONT_HEIGHT)
								+ 4;
						if (getText().length() == getCursorPosition()) {
							TextRenderer.getFontRenderer().drawString("_", cursorX, cursorY, color);
						} else {
							Renderer.drawRect(cursorX, cursorY, cursorX + 1,
									cursorY + TextRenderer.getFontRenderer().FONT_HEIGHT, color);
						}
					}
				}
				++lineCount;
				++charCount;
			}
			listHeight = lineCount * TextRenderer.getFontRenderer().FONT_HEIGHT;
			scrollBar.setVisiblie(listHeight > (height - 4));
			scrollBar.setScrollerSize((getScrollerSize()));
		}
	}

	public List<String> getLines() {
		List<String> lines = new ArrayList<String>();
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
		return maxStringLenght;
	}

	private int getScrollerSize() {
		return (int) (((1F * height) / listHeight) * (height - 4));
	}

	private int getStartLineY() {
		float scrolled = scrollBar.scrolled;
		return MathHelper.ceiling_double_int((scrolled * getHeight()) / TextRenderer.getFontRenderer().FONT_HEIGHT);
	}

	@Override
	protected void handleKey(char typedChar, int typedIndex) {
		if (!isFocused()) {
			return;
		}
		String originalText = getText();
		if ((typedChar == 13) || (typedChar == 10)) {
			setText(originalText.substring(0, getCursorPosition()) + typedChar
					+ originalText.substring(getCursorPosition()));
		}

		boolean isSpecialCharCombination = handleSpecialCharComb(typedChar, typedIndex);
		if (!isSpecialCharCombination) {
			handleInput(typedChar, typedIndex);
		}
	}

	@Override
	protected boolean handleMouseClick(int posX, int posY, int mouseButtonIndex, boolean overlap) {
		boolean clicked = !overlap && isTextBoxUnderMouse(posX, posY);
		setIsFocused(clicked);
		if (isFocused() && (mouseButtonIndex == 0)) {
			int lenght = posX - getX();
			String temp = TextRenderer.getFontRenderer().trimStringToWidth(text.substring(scrollOffset), getWidth());
			setCursorPosition(TextRenderer.getFontRenderer().trimStringToWidth(temp, lenght).length() + scrollOffset);
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
		}
		return clicked;
	}

	@Override
	public void setup() {
		registerComponent(
				scrollBar = new ScrollBar(getX() + getWidth(), getY(), 15, getHeight(), 20).setVisiblie(false));
	}

}
