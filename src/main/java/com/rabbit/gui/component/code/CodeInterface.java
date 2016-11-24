package com.rabbit.gui.component.code;

import java.awt.Color;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.rabbit.gui.component.GuiWidget;
import com.rabbit.gui.component.code.parser.CollectorTokenSource;
import com.rabbit.gui.component.code.parser.DescriptiveErrorListener;
import com.rabbit.gui.component.code.parser.Python3Lexer;
import com.rabbit.gui.component.code.parser.Python3Parser;
import com.rabbit.gui.component.control.MultiTextbox;
import com.rabbit.gui.component.control.TextBox;
import com.rabbit.gui.component.display.Shape;
import com.rabbit.gui.component.display.ShapeType;
import com.rabbit.gui.render.Renderer;
import com.rabbit.gui.render.TextRenderer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import scala.actors.threadpool.Arrays;

@SideOnly(Side.CLIENT)
public class CodeInterface extends MultiTextbox {

	protected int maxStringLength = Integer.MAX_VALUE;
	protected Shape errorBox;
	private int errLine = -1;
	private String errCode;
	private boolean hasError = false;
	private String formattedText = "";

	private final String SYMBOL = "\u00A7f"; // white
	private final String NUMBER = "\u00A79"; // blue
	private final String STRING = "\u00A7e"; // yellow
	private final String VARIABLE = "\u00A7b"; // cyan
	private final String FUNCTION = "\u00A76"; // gold
	private final String MEMBER_VAR = "\u00A7d"; // magenta
	private final String MEMBER_FUNCTION = "\u00A7a"; // light green
	private final String SYNTAX = "\u00A7c"; // light red
	private final String COMMENT = "\u00A78"; // dark gray
	private final String RESET = "\u00A7r";

	public CodeInterface(int xPos, int yPos, int width, int height) {
		super(xPos, yPos, width, height);
		errorBox = new Shape(0, 0, getWidth(), 0, ShapeType.RECT, Color.red);
	}

	public CodeInterface(int xPos, int yPos, int width, int height, String initialText) {
		super(xPos, yPos, width, height, initialText);
		formatText();
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
			List<String> lines = getFormattedLines();
			int charCount = 0;
			int lineCount = 0;
			int maxWidth = scrollBar.isVisible() ? width - 14 : width - 4;
			for (String wholeLine : lines) {
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
					charCount++;
					line += c;
				}
				if ((lineCount >= startLine) && (lineCount < maxLineAmount)) {
					if (hasError && (Math.abs(lineCount - errLine) <= 1)) {
						// its possible the code error is empty because the way
						// python shell reports its errors...
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
				}
				++lineCount;
				++charCount;
			}
			listHeight = (lineCount * TextRenderer.getFontRenderer().FONT_HEIGHT) + (height / 2);

			/*
			 * Find and render the cursor for some reason the formatted text
			 * doesnt render the cursor in the right place
			 */
			lines = getLines();
			charCount = 0;
			lineCount = 0;
			maxWidth = scrollBar.isVisible() ? width - 14 : width - 4;
			for (String wholeLine : lines) {
				String line = "";
				char[] chars = wholeLine.toCharArray();
				for (char c : chars) {
					if (TextRenderer.getFontRenderer().getStringWidth(line + c) > maxWidth) {
						line = "";
						lineCount++;
					}
					if (renderCursor && (charCount == getCursorPosition()) && (lineCount >= startLine)
							&& (lineCount < maxLineAmount)) {
						int cursorX = getX() + TextRenderer.getFontRenderer().getStringWidth(line) + 3;
						int cursorY = getY() + ((lineCount - startLine) * TextRenderer.getFontRenderer().FONT_HEIGHT)
								+ 4;
						if ((getText().length() == getCursorPosition()) || (c == '\n')) {
							TextRenderer.getFontRenderer().drawString("_", cursorX, cursorY, 0xFFFFFFFF);
						} else {
							Renderer.drawRect(cursorX, cursorY, cursorX + 1,
									cursorY + TextRenderer.getFontRenderer().FONT_HEIGHT, 0xFFFFFFFF);
						}
					}
					charCount++;
					line += c;
				}
				if ((lineCount >= startLine) && (lineCount < maxLineAmount)) {
					if (renderCursor && (charCount == getCursorPosition())) {
						int cursorX = getX() + TextRenderer.getFontRenderer().getStringWidth(line) + 3;
						int cursorY = getY() + ((lineCount - startLine) * TextRenderer.getFontRenderer().FONT_HEIGHT)
								+ 4;
						if ((getText().length() == getCursorPosition()) || (getText().toCharArray()[Math.min(charCount,
								getText().toCharArray().length - 1)] == '\n')) {
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
			scrollBar.setVisiblie(listHeight > (height - 4));
			scrollBar.setHandleMouseWheel((listHeight > (height - 4)) && isUnderMouse(Mouse.getX(), Mouse.getY()));
			scrollBar.setScrollerSize((super.getScrollerSize()));
			GlStateManager.resetColor();
			GlStateManager.popMatrix();
			TextRenderer.getFontRenderer().setUnicodeFlag(false);
		}
	}

	private void formatText() {
		formattedText = "";
		for (String line : getLines()) {
			Python3Lexer lexer = new Python3Lexer(new ANTLRInputStream(line));
			CollectorTokenSource tokenSource = new CollectorTokenSource(lexer);
			CommonTokenStream tokens = new CommonTokenStream(tokenSource);
			Python3Parser parser = new Python3Parser(tokens);
			parser.removeErrorListeners();
			lexer.removeErrorListeners();
			parser.file_input();

			for (int i = 0; i < tokenSource.getCollectedTokens().size(); i++) {
				Token token = tokens.get(i);
				if (token.getType() == Token.EOF) {
					break;
				} else if (token.getType() <= Python3Lexer.BREAK) {
					formattedText += SYNTAX + token.getText() + RESET;
				} else if (token.getType() == Python3Lexer.NAME) {
					// a name ends up being nearly everything so lets break it
					// down a little
					Token nextToken = null;
					Token prevToken = null;
					if (i < (tokenSource.getCollectedTokens().size() - 1)) {
						nextToken = tokens.get(i + 1);
					}
					if (i > 0) {
						prevToken = tokens.get(i - 1);
					}
					// if the next token is an open paren its a function
					// if it follows a dot its a member variable
					// if that has a paren its a member function
					if ((prevToken != null) && (prevToken.getType() == Python3Lexer.DOT)) {
						if ((nextToken != null) && (nextToken.getType() == Python3Lexer.OPEN_PAREN)) {
							// its a member function
							formattedText += MEMBER_FUNCTION + token.getText() + RESET;
						} else {
							// its a member of some sort
							formattedText += MEMBER_VAR + token.getText() + RESET;
						}
					} else if ((nextToken != null) && (nextToken.getType() == Python3Lexer.OPEN_PAREN)) {
						// a function
						formattedText += FUNCTION + token.getText() + RESET;
					} else {
						formattedText += VARIABLE + token.getText() + RESET;
					}
				} else if (token.getType() == Python3Lexer.STRING_LITERAL) {
					formattedText += STRING + token.getText() + RESET;
				} else if ((token.getType() >= Python3Lexer.BYTES_LITERAL)
						&& (token.getType() <= Python3Lexer.IMAG_NUMBER)) {
					formattedText += NUMBER + token.getText() + RESET;
				} else if ((token.getType() >= Python3Lexer.DOT) && (token.getType() <= Python3Lexer.IDIV_ASSIGN)) {
					formattedText += SYMBOL + token.getText() + RESET;
				} else if (token.getType() == Python3Parser.COMMENT) {
					formattedText += COMMENT + token.getText() + RESET;
				} else if (token.getType() == Python3Parser.SPACES) {
					formattedText += token.getText();
				} else if (token.getType() == Python3Parser.INDENT) {
					formattedText += token.getText();
				}
				// else {
				// System.out.println("Attempting to format token: " +
				// token.getType() + ", " + token.getText());
				// }
				// dedents dont seem to matter in this context
				/*
				 * else if (token.getType() == Python3Lexer.NEWLINE) {
				 * formattedText += "\n"; }
				 *
				 * else if( token.getType() == Python3Parser.DEDENT){
				 * formattedText += "\n"; }
				 */
			}
			formattedText += "\n";
		}
	}

	public List<String> getFormattedLines() {
		return Arrays.asList(formattedText.split("\n"));
	}

	@Override
	public int getStartLineY() {
		if (scrollBar != null) {
			return MathHelper.ceiling_double_int((scrollBar.getScrolledAmt() * (listHeight - getHeight()))
					/ TextRenderer.getFontRenderer().FONT_HEIGHT);
		}
		return 0;
	}

	@Override
	protected boolean handleInput(char typedChar, int typedKeyIndex) {
		if (hasError && (typedKeyIndex == Keyboard.KEY_RETURN)) {
			errLine++;
		}
		boolean status = super.handleInput(typedChar, typedKeyIndex);
		formatText();
		return status;
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
	public TextBox setText(String newText) {
		text = newText;
		testForErrors();
		formatText();
		return this;
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

	public void testForErrors() {
		Python3Lexer lexer = new Python3Lexer(new ANTLRInputStream(getText()));

		CollectorTokenSource tokenSource = new CollectorTokenSource(lexer);
		CommonTokenStream tokens = new CommonTokenStream(tokenSource);
		Python3Parser parser = new Python3Parser(tokens);
		parser.removeErrorListeners();
		parser.addErrorListener(DescriptiveErrorListener.INSTANCE);
		parser.file_input();
	}
}
