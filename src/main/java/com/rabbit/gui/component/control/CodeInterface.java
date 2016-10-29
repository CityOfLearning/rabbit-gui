package com.rabbit.gui.component.control;

import java.awt.Color;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.rabbit.gui.component.GuiWidget;
import com.rabbit.gui.component.display.Shape;
import com.rabbit.gui.component.display.ShapeType;
import com.rabbit.gui.render.Renderer;
import com.rabbit.gui.render.TextRenderer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CodeInterface extends MultiTextbox {

	protected int maxStringLength = 100000;
	protected Shape errorBox;
	private int errLine = -1;
	private String errCode;
	private boolean hasError = false;

	public CodeInterface(int xPos, int yPos, int width, int height) {
		super(xPos, yPos, width, height);
		errorBox = new Shape(0, 0, getWidth(), 0, ShapeType.RECT, Color.red);
	}

	public CodeInterface(int xPos, int yPos, int width, int height, String initialText) {
		super(xPos, yPos, width, height, initialText);
		errorBox = new Shape(0, 0, getWidth(), 0, ShapeType.RECT, Color.red);
	}

	public void clearError() {
		errLine = -1;
		errCode = "";
		errorBox.setHeight(0);
		hasError = false;
	}

	@Override
	protected void drawBox() {
		if (isVisible()) {
			GlStateManager.pushMatrix();
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
						if (hasError && (Math.abs(lineCount - errLine) <= 1)) {
							if (!errCode.isEmpty() && wholeLine.contains(errCode) && (lineCount != errLine)) {
								errLine = lineCount;
								errorBox.setY(getY() + 4
										+ ((lineCount - startLine) * TextRenderer.getFontRenderer().FONT_HEIGHT));
								errorBox.setHeight(TextRenderer.getFontRenderer().FONT_HEIGHT * 2);
							} else if (lineCount == errLine) {
								errorBox.setY(getY() + 4
										+ ((lineCount - startLine) * TextRenderer.getFontRenderer().FONT_HEIGHT));
								errorBox.setHeight(TextRenderer.getFontRenderer().FONT_HEIGHT * 2);
							}
						}
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
					if (hasError && (Math.abs(lineCount - errLine) <= 1)) {
						// its possible the code error is empty because the way
						// python shell
						// reports its errors...
						if (!errCode.isEmpty() && wholeLine.contains(errCode) && (lineCount != errLine)) {
							errLine = lineCount;
							errorBox.setY(getY() + 4
									+ ((lineCount - startLine) * TextRenderer.getFontRenderer().FONT_HEIGHT));
							errorBox.setHeight(TextRenderer.getFontRenderer().FONT_HEIGHT);
						} else if (lineCount == errLine) {
							errorBox.setY(getY() + 4
									+ ((lineCount - startLine) * TextRenderer.getFontRenderer().FONT_HEIGHT));
							errorBox.setHeight(TextRenderer.getFontRenderer().FONT_HEIGHT);
						}
					}
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
			scrollBar.setHandleMouseWheel((listHeight > (height - 4)) && isUnderMouse(Mouse.getX(), Mouse.getY()));
			scrollBar.setScrollerSize((super.getScrollerSize()));
			GlStateManager.resetColor();
			GlStateManager.popMatrix();
			TextRenderer.getFontRenderer().setUnicodeFlag(false);
		}
	}

	@Override
	protected boolean handleInput(char typedChar, int typedKeyIndex) {
		getText();
		if (hasError && (typedKeyIndex == Keyboard.KEY_RETURN)) {
			errLine++;
		}
		return super.handleInput(typedChar, typedKeyIndex);
	}

	public void notifyError(int line, String code, String error) {
		errLine = line;
		// code can be empty
		if ((code != null) && !code.isEmpty()) {
			errCode = code.trim();
		} else if (error.contains("NameError")) {
			try {
				errCode = getLines().get(errLine);
			} catch (Exception e) {
				// index out of bounds
			}
		}
		if (errLine >= 0) {
			hasError = true;
		}
	}

	@Override
	public void setup() {
		super.setup();
		registerComponent(errorBox);
	}

	@Override
	public GuiWidget setX(int x) {
		super.setX(x);
		errorBox.setX(x);
		return this;
	}

	@Override
	public GuiWidget setY(int y) {
		super.setY(y);
		return this;
	}
}
