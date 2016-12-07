package com.rabbit.gui.component.code;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.rabbit.gui.RabbitGui;
import com.rabbit.gui.component.GuiWidget;
import com.rabbit.gui.component.code.parser.AntlrAutoCompletionSuggester;
import com.rabbit.gui.component.code.parser.CollectorTokenSource;
import com.rabbit.gui.component.code.parser.DescriptiveErrorListener;
import com.rabbit.gui.component.code.parser.Python3Lexer;
import com.rabbit.gui.component.code.parser.Python3Parser;
import com.rabbit.gui.component.code.parser.AntlrAutoCompletionSuggester.EditorContext;
import com.rabbit.gui.component.code.parser.AntlrAutoCompletionSuggester.TokenType;
import com.rabbit.gui.component.control.Button;
import com.rabbit.gui.component.control.MultiTextbox;
import com.rabbit.gui.component.control.TextBox;
import com.rabbit.gui.component.display.Shape;
import com.rabbit.gui.component.display.ShapeType;
import com.rabbit.gui.render.Renderer;
import com.rabbit.gui.render.TextRenderer;
import com.rabbit.gui.utils.UtilityFunctions;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CodeInterface extends MultiTextbox {

	protected int maxStringLength = Integer.MAX_VALUE;
	protected Shape errorBox;
	private int errLine = -1;
	private String errCode;
	private boolean hasError = false;
	private String formattedText = "";

	protected boolean drawHoverText = true;
	protected List<String> originalHoverText = new ArrayList<String>();

	protected List<String> hoverText = new ArrayList<String>();

	protected boolean drawToLeft;

	private int suggestionCooldown = 50;

	// the variable name and its respective class
	private Map<String, String> userVariables = Maps.newHashMap();
	private Map<String, List<String>> classMembers = Maps.newHashMap();

	private final String SYMBOL = EnumChatFormatting.WHITE.toString(); // white
	private final String NUMBER = EnumChatFormatting.BLUE.toString(); // blue
	private final String STRING = EnumChatFormatting.YELLOW.toString(); // yellow
	private final String VARIABLE = EnumChatFormatting.AQUA.toString(); // cyan
	private final String FUNCTION = EnumChatFormatting.GOLD.toString(); // gold
	private final String MEMBER_VAR = EnumChatFormatting.LIGHT_PURPLE.toString(); // magenta
	private final String MEMBER_FUNCTION = EnumChatFormatting.GREEN.toString(); // light
																				// green
	private final String SYNTAX = EnumChatFormatting.RED.toString(); // light
																		// red
	private final String COMMENT = EnumChatFormatting.DARK_GRAY.toString(); // dark
																			// gray
	private final String RESET = EnumChatFormatting.RESET.toString();

	public CodeInterface(int xPos, int yPos, int width, int height) {
		super(xPos, yPos, width, height);
		errorBox = new Shape(0, 0, getWidth(), 0, ShapeType.RECT, Color.red);
	}

	public CodeInterface(int xPos, int yPos, int width, int height, String initialText) {
		super(xPos, yPos, width, height, initialText);
		formatText();
		errorBox = new Shape(0, 0, getWidth(), 0, ShapeType.RECT, Color.red);
	}

	public CodeInterface addHoverText(String text) {
		originalHoverText.add(text);
		return this;
	}

	public CodeInterface doesDrawHoverText(boolean state) {
		drawHoverText = state;
		return this;
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
					if ((charCount == getCursorPosition()) && (lineCount >= startLine) && (lineCount < maxLineAmount)) {

						int cursorX = getX() + TextRenderer.getFontRenderer().getStringWidth(line) + 3;
						int cursorY = getY() + ((lineCount - startLine) * TextRenderer.getFontRenderer().FONT_HEIGHT)
								+ 4;
						if (renderCursor) {
							if (this.suggestionCooldown > 0) {
								suggestionCooldown -= 2;
							}
							if ((getText().length() == getCursorPosition()) || (c == '\n')) {
								TextRenderer.getFontRenderer().drawString("_", cursorX, cursorY, 0xFFFFFFFF);
							} else {
								Renderer.drawRect(cursorX, cursorY, cursorX + 1,
										cursorY + TextRenderer.getFontRenderer().FONT_HEIGHT, 0xFFFFFFFF);
							}
						}
						if (this.suggestionCooldown <= 0) {
							setHoverText(getRecommendation(wholeLine));
							if (drawHoverText) {
								verifyHoverText(cursorX + 5, cursorY);
								if (drawToLeft) {
									int tlineWidth = 0;
									for (String hline : hoverText) {
										tlineWidth = TextRenderer.getFontRenderer().getStringWidth(hline) > tlineWidth
												? TextRenderer.getFontRenderer().getStringWidth(hline) : tlineWidth;
									}
									Renderer.drawHoveringText(hoverText, cursorX - tlineWidth - 20, cursorY + 12);
								} else {
									Renderer.drawHoveringText(hoverText, cursorX + 5, cursorY + 12);
								}
							}
						}
					}
					charCount++;
					line += c;
				}
				if ((lineCount >= startLine) && (lineCount < maxLineAmount)) {
					if (charCount == getCursorPosition()) {
						int cursorX = getX() + TextRenderer.getFontRenderer().getStringWidth(line) + 3;
						int cursorY = getY() + ((lineCount - startLine) * TextRenderer.getFontRenderer().FONT_HEIGHT)
								+ 4;
						if (renderCursor) {
							if (this.suggestionCooldown > 0) {
								suggestionCooldown -= 2;
							}
							if ((getText().length() == getCursorPosition()) || (getText().toCharArray()[Math
									.min(charCount, getText().toCharArray().length - 1)] == '\n')) {
								TextRenderer.getFontRenderer().drawString("_", cursorX, cursorY, 0xFFFFFFFF);
							} else {
								Renderer.drawRect(cursorX, cursorY, cursorX + 1,
										cursorY + TextRenderer.getFontRenderer().FONT_HEIGHT, 0xFFFFFFFF);
							}
						}
						if (this.suggestionCooldown <= 0) {
							setHoverText(getRecommendation(wholeLine));
							if (drawHoverText) {
								verifyHoverText(cursorX + 5, cursorY);
								if (drawToLeft) {
									int tlineWidth = 0;
									for (String hline : hoverText) {
										tlineWidth = TextRenderer.getFontRenderer().getStringWidth(hline) > tlineWidth
												? TextRenderer.getFontRenderer().getStringWidth(hline) : tlineWidth;
									}
									Renderer.drawHoveringText(hoverText, cursorX - tlineWidth - 20, cursorY + 12);
								} else {
									Renderer.drawHoveringText(hoverText, cursorX + 5, cursorY + 12);
								}
							}
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

	// for some reason this caused a null pointer... though the formatted text
	// should never be null
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

	public CodeInterface setHoverText(List<String> text) {
		originalHoverText = text;
		return this;
	}

	@Override
	public TextBox setText(String newText) {
		text = newText;
		suggestionCooldown = 50;
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

	public List<String> getRecommendation(String line) {
		/*AntlrAutoCompletionSuggester autoComplete = new AntlrAutoCompletionSuggester(Python3Parser.ruleNames,
				Python3Parser.VOCABULARY, Python3Parser._ATN);

		EditorContext context = autoComplete.new EditorContext(line);
		Set<TokenType> suggestions = autoComplete.suggestions(context);
		List<String> recommendations = Lists.newArrayList();

		try {
			// suggester adds a fake token at the end, drop it
			List<Token> tokens = UtilityFunctions.minusLast(context.preceedingTokens());
			Token curText = null;
			if (UtilityFunctions.getLastElement(UtilityFunctions.minusLast(tokens)).getType() == Python3Lexer.DOT) {
				curText = UtilityFunctions.getLastElement(tokens);
				tokens = UtilityFunctions.minusLast(tokens);
			}
			if (UtilityFunctions.getLastElement(tokens).getType() == Python3Lexer.DOT) {
				// The last element is a dot so we are probably
				// accessing member variables
				if (curText == null) {
					if (UtilityFunctions.getLastElement(UtilityFunctions.minusLast(tokens))
							.getType() == Python3Lexer.NAME) {
						// and the thing preceeding the dot is name so we
						// are accessing a classes variables
						recommendations.addAll(classMembers.get(userVariables
								.get(UtilityFunctions.getLastElement(UtilityFunctions.minusLast(tokens)).getText())));
					}
				} else {
					for (String elem : classMembers.get(userVariables
							.get(UtilityFunctions.getLastElement(UtilityFunctions.minusLast(tokens)).getText()))) {
						if (elem.contains(curText.getText())) {
							recommendations.add(elem);
						}
					}
				}
			}
		} catch (Exception e) {
			// eh we dont really care if we dont get a recommendation
			// that badly
		}
		if (recommendations.isEmpty()) {
			for (TokenType token : suggestions) {
				String proposition = Python3Lexer.VOCABULARY.getLiteralName(token.getType());
				if (proposition != null) {
					// TODO: probably best if we filter out symbols? how to give
					// smarter feedback
					if (proposition.startsWith("'") && proposition.endsWith("'")) {
						proposition = proposition.substring(1, proposition.length() - 1);
						recommendations.add(proposition);
					}
				}
			}
		}
		Collections.sort(recommendations);
		return recommendations;
	}

	public void testForErrors() {
		Python3Lexer lexer = new Python3Lexer(new ANTLRInputStream(getText()));

		CollectorTokenSource tokenSource = new CollectorTokenSource(lexer);
		CommonTokenStream tokens = new CommonTokenStream(tokenSource);
		Python3Parser parser = new Python3Parser(tokens);
		parser.removeErrorListeners();
		parser.addErrorListener(DescriptiveErrorListener.INSTANCE);
		parser.file_input();

		List<Token> res = new LinkedList<Token>();
		for (Token token : tokenSource.getCollectedTokens()) {
			if (token.getChannel() == 0 && token.getType() != Python3Parser.NEWLINE) {
				res.add(token);
			}
		}

		userVariables.clear();

		int i = 0;
		for (Token token : res) {
			if (token.getType() == Python3Parser.ASSIGN) {
				try {
					if (res.get(i - 1).getType() == Python3Parser.NAME
							&& res.get(i + 1).getType() == Python3Parser.NAME) {
						// ok we have an assignment lets remember the object map
						// now
						try {
							userVariables.put(res.get(i - 1).getText(), res.get(i + 1).getText());
						} catch (Exception e) {
							// TODO handle user variables named the same thing
						}
					}
				} catch (IndexOutOfBoundsException e) {
					// name is not assigned its ok we might be in the process of
					// writing
				}
			}
			i++;
		}*/
		return Lists.newArrayList();
	}

	public void addClassMembers(String clazz, List<String> mappings) {
		if (classMembers.containsKey(clazz)) {
			classMembers.replace(clazz, mappings);
		} else {
			classMembers.put(clazz, mappings);
		}
	}

	public List<String> getClassMembers(String clazz) {
		return this.classMembers.get(clazz);
	}

	protected void verifyHoverText(int mouseX, int mouseY) {
		int tlineWidth = 0;
		for (String line : originalHoverText) {
			tlineWidth = TextRenderer.getFontRenderer().getStringWidth(line) > tlineWidth
					? TextRenderer.getFontRenderer().getStringWidth(line) : tlineWidth;
		}
		int dWidth = RabbitGui.proxy.getCurrentStage().width;
		if (((tlineWidth + mouseX) > dWidth) && ((mouseX + 1) > (dWidth / 2))) {
			// the button is on the right half of the screen
			drawToLeft = true;
		}
		List<String> newHoverText = new ArrayList<String>();
		if (drawToLeft) {
			for (String line : originalHoverText) {
				int lineWidth = TextRenderer.getFontRenderer().getStringWidth(line) + 12;
				// if the line length is longer than the button is from the left
				// side of the screen we have to split
				if (lineWidth > mouseX) {
					// the line is too long lets split it
					String newString = "";
					for (String substring : line.split(" ")) {
						// we can fit the string, we are ok
						if ((TextRenderer.getFontRenderer().getStringWidth(newString)
								+ TextRenderer.getFontRenderer().getStringWidth(substring)) < (mouseX - 12)) {
							newString += substring + " ";
						} else {
							newHoverText.add(newString);
							newString = substring + " ";
						}
					}
					newHoverText.add(newString);
				} else {
					newHoverText.add(line);
				}
			}
		} else {
			for (String line : originalHoverText) {
				int lineWidth = TextRenderer.getFontRenderer().getStringWidth(line) + 12;
				// we just need to know what the right most side of the button
				// is
				if (lineWidth > (dWidth - mouseX)) {
					// the line is too long lets split it
					String newString = "";
					for (String substring : line.split(" ")) {
						// we can fit the string, we are ok
						if ((TextRenderer.getFontRenderer().getStringWidth(newString)
								+ TextRenderer.getFontRenderer().getStringWidth(substring)) < (dWidth - mouseX - 12)) {
							newString += substring + " ";
						} else {
							newHoverText.add(newString);
							newString = substring + " ";
						}
					}
					newHoverText.add(newString);
				} else {
					newHoverText.add(line);
				}
			}
		}
		hoverText = newHoverText;
	}
}
