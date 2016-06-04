package com.rabbit.gui.component.control;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.rabbit.gui.component.GuiWidget;
import com.rabbit.gui.component.Shiftable;
import com.rabbit.gui.layout.LayoutComponent;
import com.rabbit.gui.render.Renderer;
import com.rabbit.gui.render.TextRenderer;
import com.rabbit.gui.utils.ControlCharacters;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@LayoutComponent
public class TextBox extends GuiWidget implements Shiftable {

	@FunctionalInterface
	public static interface TextChangedListener {
		void onTextChanged(TextBox textbox, String previousText);
	}

	public static final int BACKGROUND_GRAY_COLOR = -6250336;
	public static final int BACKGROUND_DARK_COLOR = -0xFFFFFF - 1;
	public static final int CURSOR_COLOR = -3092272;

	@LayoutComponent
	protected boolean visibleBackground = true;

	@LayoutComponent
	protected boolean isVisible = true;

	@LayoutComponent
	protected boolean isEnabled = true;

	@LayoutComponent
	protected boolean isFocused = false;
	@LayoutComponent
	protected String text;
	protected String initailText;
	protected int cursorPos;

	protected int scrollOffset;

	@LayoutComponent
	protected int maxStringLength = 100;

	protected int selectionEnd = -2;

	@LayoutComponent
	protected int enabledColor = 14737632;

	@LayoutComponent
	protected int disabledColor = 7368816;

	protected long cursorCounter = 0L;

	protected TextChangedListener textChangedListener;

	public TextBox(int xPos, int yPos, int width, int height) {
		this(xPos, yPos, width, height, "");
	}

	public TextBox(int xPos, int yPos, int width, int height, String initialText) {
		super(xPos, yPos, width, height);
		text = initialText;
		initailText = initialText;
		setCursorPosition(text.length());
		Keyboard.enableRepeatEvents(true);
	}

	public void deleteTextFromCursor(int amount) {
		if (getText().length() != 0) {
			if (selectionEnd != getCursorPosition()) {
				pushText("");
			} else {
				try {
					boolean negative = amount < 0;
					int j = negative ? getCursorPosition() + amount : getCursorPosition();
					int k = negative ? getCursorPosition() : getCursorPosition() + amount;
					String result = "";
					if (j >= 0) {
						result = getText().substring(0, j);
					}

					if (k < getText().length()) {
						result += getText().substring(k);
					}

					setTextWithEvent(result);

					if (negative) {
						moveCursorBy(amount);
					}
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}
	}

	public void deleteWordsFromCursor(int amount) {
		if (getText().length() != 0) {
			if (selectionEnd != getCursorPosition()) {
				pushText("");
			} else {
				deleteTextFromCursor(
						this.getAmountOfWordsFromPos(amount, getCursorPosition(), true) - getCursorPosition());
			}
		}
	}

	protected void drawBox() {
		if (isVisible()) {
			if (isBackgroundVisible()) {
				drawTextBoxBackground();
			}
			int textColor = isEnabled() ? getEnabledColor() : getDisabledColor();
			int cursorPosWithOffset = getCursorPosition() - scrollOffset;
			int selEnd = selectionEnd - scrollOffset;
			String text = TextRenderer.getFontRenderer().trimStringToWidth(this.text.substring(scrollOffset),
					isBackgroundVisible() ? getWidth() - 8 : getWidth());
			boolean isCursorVisible = (cursorPosWithOffset >= 0) && (cursorPosWithOffset <= text.length());
			boolean shouldRenderCursor = isFocused() && (((cursorCounter / 6) % 2) == 0) && isCursorVisible;
			int firstTextX = isBackgroundVisible() ? getX() + 4 : getX();
			int textY = isBackgroundVisible() ? getY() + ((getHeight() - 8) / 2) : getY();
			int secondTextX = firstTextX;

			if (selEnd > text.length()) {
				selEnd = text.length();
			}

			if (text.length() > 0) {
				String firstText = isCursorVisible ? text.substring(0, cursorPosWithOffset) : text;
				secondTextX = TextRenderer.getFontRenderer().drawStringWithShadow(firstText, firstTextX, textY,
						textColor);
			}

			boolean isCursorInText = (getCursorPosition() < getText().length())
					|| (getText().length() >= getMaxLength());
			int cursorX = secondTextX;

			if (!isCursorVisible) {
				cursorX = cursorPosWithOffset > 0 ? firstTextX + getWidth() : firstTextX;
			} else if (isCursorInText) {
				cursorX = --secondTextX;
			}

			if ((text.length() > 0) && isCursorVisible && (cursorPosWithOffset < text.length())) {
				TextRenderer.getFontRenderer().drawStringWithShadow(text.substring(cursorPosWithOffset), secondTextX,
						textY, textColor);
			}

			if (shouldRenderCursor) {
				if (isCursorInText) {
					Renderer.drawRect(cursorX, textY - 1, cursorX + 1,
							textY + 1 + TextRenderer.getFontRenderer().FONT_HEIGHT, CURSOR_COLOR);
				} else {
					TextRenderer.getFontRenderer().drawStringWithShadow("_", cursorX, textY, textColor);
				}
			}

			if (selEnd != cursorPosWithOffset) {
				int finishX = firstTextX + TextRenderer.getFontRenderer().getStringWidth(text.substring(0, selEnd));
				renderSelectionRect(cursorX, textY - 1, finishX - 1,
						textY + 1 + TextRenderer.getFontRenderer().FONT_HEIGHT);
			}

		}
	}

	protected void drawTextBoxBackground() {
		Renderer.drawRect(getX() - 1, getY() - 1, getX() + getWidth() + 1, getY() + getHeight() + 1,
				BACKGROUND_GRAY_COLOR);
		Renderer.drawRect(getX(), getY(), getX() + getWidth(), getY() + getHeight(), BACKGROUND_DARK_COLOR);
	}

	public int getAmountOfWordsFromPos(boolean negative, int absolute, int pos, boolean flag) {
		int result = pos;
		for (int i = 0; i < absolute; ++i) {
			if (negative) {
				while (flag && (result > 0) && (getText().charAt(result - 1) == 32)) {
					--result;
				}
				while ((result > 0) && (getText().charAt(result - 1) != 32)) {
					--result;
				}
			} else {
				result = getText().indexOf(32, result);
				if (result == -1) {
					result = getText().length();
				} else {
					while (flag && (result < getText().length()) && (getText().charAt(result) == 32)) {
						++result;
					}
				}
			}
		}
		return result;
	}

	public int getAmountOfWordsFromPos(int n, int pos, boolean flag) {
		return this.getAmountOfWordsFromPos(n < 0, Math.abs(n), pos, flag);
	}

	public int getCursorPosition() {
		return cursorPos;
	}

	public int getDisabledColor() {
		return disabledColor;
	}

	public int getEnabledColor() {
		return enabledColor;
	}

	public int getMaxLength() {
		return maxStringLength;
	}

	public String getSelectedText() {
		int from = Math.min(getCursorPosition(), selectionEnd);
		int to = Math.max(getCursorPosition(), selectionEnd);
		return getText().substring(from, to);
	}

	public String getText() {
		return text;
	}

	public TextChangedListener getTextChangedListener() {
		return textChangedListener;
	}

	protected boolean handleInput(char typedChar, int typedKeyIndex) {
		switch (typedKeyIndex) {
		case Keyboard.KEY_BACK:
			if (isEnabled()) {
				if (GuiScreen.isCtrlKeyDown()) {
					deleteWordsFromCursor(-1);
				} else {
					deleteTextFromCursor(-1);
				}
			}
			return true;
		case Keyboard.KEY_HOME:
			if (GuiScreen.isShiftKeyDown()) {
				setSelectionPos(0);
			} else {
				setCursorPosition(0);
			}
			return true;
		case Keyboard.KEY_LEFT:
			handleKeyboardArrow(-1);
			return true;
		case Keyboard.KEY_RIGHT:
			handleKeyboardArrow(1);
			return true;
		case Keyboard.KEY_END:
			if (GuiScreen.isShiftKeyDown()) {
				setSelectionPos(getText().length());
			} else {
				setCursorPosition(getText().length());
			}
			return true;
		case Keyboard.KEY_DELETE:
			if (isEnabled()) {
				if (GuiScreen.isCtrlKeyDown()) {
					deleteWordsFromCursor(1);
				} else {
					deleteTextFromCursor(1);
				}
			}
			return true;
		default:
			if (isEnabled() && ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
				pushText(Character.toString(typedChar));
				return true;
			}
		}

		return false;
	}

	protected void handleKey(char typedChar, int typedIndex) {
		if (!isFocused()) {
			return;
		}
		boolean isSpecialCharCombination = handleSpecialCharComb(typedChar, typedIndex);
		if (!isSpecialCharCombination) {
			handleInput(typedChar, typedIndex);
		}
	}

	private void handleKeyboardArrow(int n) {
		if (GuiScreen.isShiftKeyDown()) {
			if (GuiScreen.isCtrlKeyDown()) {
				setSelectionPos(this.getAmountOfWordsFromPos(n, selectionEnd, true));
			} else {
				setSelectionPos(selectionEnd + n);
			}
		} else if (GuiScreen.isCtrlKeyDown()) {
			setCursorPosition(this.getAmountOfWordsFromPos(n, getCursorPosition(), true));
		} else {
			setCursorPosition(selectionEnd + (n));
		}
	}

	protected boolean handleMouseClick(int posX, int posY, int mouseButtonIndex, boolean overlap) {
		boolean clicked = isTextBoxUnderMouse(posX, posY) && !overlap;
		setIsFocused(clicked);
		if (isFocused() && (mouseButtonIndex == 0)) {
			int lenght = posX - getX();
			String temp = TextRenderer.getFontRenderer().trimStringToWidth(text.substring(scrollOffset), getWidth());
			setCursorPosition(TextRenderer.getFontRenderer().trimStringToWidth(temp, lenght).length() + scrollOffset);
		}
		return clicked;
	}

	protected boolean handleSpecialCharComb(char typedChar, int typedIndex) {
		switch (typedChar) {
		case 1:
			setCursorPosition(getText().length());
			setSelectionPos(0);
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

	public boolean isBackgroundVisible() {
		return visibleBackground;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public boolean isFocused() {
		return isFocused;
	}

	public boolean isTextBoxUnderMouse(int mouseX, int mouseY) {
		return (mouseX >= getX()) && (mouseX <= (getX() + getWidth())) && (mouseY >= getY())
				&& (mouseY <= (getY() + getHeight()));
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void moveCursorBy(int amount) {
		setCursorPosition(selectionEnd + amount);
	}

	@Override
	public void onClose() {
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public void onDraw(int mouseX, int mouseY, float partialTicks) {
		super.onDraw(mouseX, mouseY, partialTicks);
		drawBox();
	}

	@Override
	public void onKeyTyped(char typedChar, int typedIndex) {
		super.onKeyTyped(typedChar, typedIndex);
		handleKey(typedChar, typedIndex);
	}

	@Override
	public boolean onMouseClicked(int posX, int posY, int mouseButtonIndex, boolean overlap) {
		super.onMouseClicked(posX, posY, mouseButtonIndex, overlap);
		return handleMouseClick(posX, posY, mouseButtonIndex, overlap);
	}

	@Override
	public void onUpdate() {
		cursorCounter++;
	}

	public void pushText(String text) {
		String result = "";
		String filtered = ChatAllowedCharacters.filterAllowedCharacters(text);
		int i = getCursorPosition() < selectionEnd ? getCursorPosition() : selectionEnd;
		int j = getCursorPosition() < selectionEnd ? selectionEnd : getCursorPosition();
		int k = getMaxLength() - getText().length() - (i - selectionEnd);

		if (getText().length() > 0) {
			result += getText().substring(0, i);
		}
		int end = 0;
		if (k < filtered.length()) {
			result = result + filtered.substring(0, k);
			end = k;
		} else {
			result = result + filtered;
			end = filtered.length();
		}
		if ((getText().length() > 0) && (j < getText().length())) {
			result = result + getText().substring(j);
		}
		setTextWithEvent(result);
		moveCursorBy((i - selectionEnd) + end);
	}

	protected void renderSelectionRect(int xTop, int yTop, int xBot, int yBot) {
		Renderer.drawRectWithSpecialGL(xTop, yTop, xBot, yBot, -0x5555FF, () -> {
			GL11.glColor4f(0.0F, 0.0F, 255.0F, 255.0F);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_COLOR_LOGIC_OP);
			GL11.glLogicOp(GL11.GL_OR_REVERSE);
		});
		GL11.glDisable(GL11.GL_COLOR_LOGIC_OP);
	}

	public TextBox setBackgroundVisibility(boolean visibleBackground) {
		this.visibleBackground = visibleBackground;
		return this;
	}

	public TextBox setCursorPosition(int pos) {
		cursorPos = pos;
		if (getCursorPosition() < 0) {
			cursorPos = 0;
		}
		if (getCursorPosition() > getText().length()) {
			cursorPos = getText().length();
		}

		setSelectionPos(getCursorPosition());
		return this;
	}

	public TextBox setDisabledColor(int color) {
		disabledColor = color;
		return this;
	}

	public TextBox setEnabledColor(int color) {
		enabledColor = color;
		return this;
	}

	@Override
	public TextBox setId(String id) {
		assignId(id);
		return this;
	}

	public TextBox setIsEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
		return this;
	}

	public TextBox setIsFocused(boolean isFocused) {
		if (isFocused && (text == initailText)) {
			setText("");
		} else if (!isFocused && text.isEmpty()) {
			setText(initailText);
		}
		this.isFocused = isFocused;
		return this;
	}

	public TextBox setIsVisible(boolean isVisible) {
		this.isVisible = isVisible;
		return this;
	}

	public TextBox setMaxLength(int max) {
		maxStringLength = max;
		return this;
	}

	public TextBox setSelectionPos(int pos) {
		if (pos < 0) {
			pos = 0;
		}

		if (pos > getText().length()) {
			pos = getText().length();
		}

		selectionEnd = pos;

		if (scrollOffset > getText().length()) {
			scrollOffset = getText().length();
		}

		String trimmed = TextRenderer.getFontRenderer().trimStringToWidth(getText().substring(scrollOffset),
				getWidth());
		int length = trimmed.length() + scrollOffset;

		if (pos == scrollOffset) {
			scrollOffset -= TextRenderer.getFontRenderer().trimStringToWidth(getText(), getWidth(), true).length();
		}

		if (pos > length) {
			scrollOffset += pos - length;
		} else if (pos <= scrollOffset) {
			scrollOffset -= scrollOffset - pos;
		}

		if (scrollOffset < 0) {
			scrollOffset = 0;
		}

		if (scrollOffset > getText().length()) {
			scrollOffset = getText().length();
		}

		return this;
	}

	public TextBox setText(String newText) {
		text = newText;
		return this;
	}

	public TextBox setTextChangedListener(TextChangedListener listener) {
		textChangedListener = listener;
		return this;
	}

	/**
	 * Will set text of textbox and execute TextChangedListener
	 *
	 * @param newText
	 * @return this
	 */
	public TextBox setTextWithEvent(String newText) {
		setText(newText);
		if (getTextChangedListener() != null) {
			getTextChangedListener().onTextChanged(this, newText);
		}
		return this;
	}

	@Override
	public void shiftX(int x) {
		setX(getX() + x);
	}

	@Override
	public void shiftY(int y) {
		setY(getY() + y);
	}
}
