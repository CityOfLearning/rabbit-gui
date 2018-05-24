Text Labels
====================

Another one of the most basic GUI elements is text. Where the text box allows for user interaction with text, the text label is a not interactable GUI element. That doesn't make it any less powerful as text helps convey all sorts of information which is why it allows for many different ways to be displayed. Text can be single line or allow for multiline with word wrapping and with or without a background.

There is also scrolling text labels in case there is too much text for the area it is being displayed in. This allows the user to scroll down and continue reading at their leisure and for you to save on space in your GUI.

Text Alignment
--------------

Currently Rabbit allows for text to be aligned `RIGHT`, `LEFT`, or `CENTER`

### Constructors

```
TextLabel(int xPos, int yPos, int width, int height, Color color, String text)
TextLabel(int xPos, int yPos, int width, int height, Color color, String text, TextAlignment align)
TextLabel(int xPos, int yPos, int width, int height, String text)
TextLabel(int xPos, int yPos, int width, int height, String text, TextAlignment align)TextLabel(int xPos, int yPos, int width, String text)

ScrollTextLabel(int xPos, int yPos, int width, int height, String text)
ScrollTextLabel(int xPos, int yPos, int width, int height, String text, TextAlignment align)
ScrollTextLabel(int xPos, int yPos, int width, String text)
```
