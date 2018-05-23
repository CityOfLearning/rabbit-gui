Text Boxes
====================

Any kind of textual user input will likely need a text box in order to capture it. This will not only render and capture the text but will allow for formatting and interaction for powerful text based control. Text boxes can either render text in the vanilla way or in unicode if sizing and spacing is important.

MultiTextboxes
--------------

MultiTextboxes are slightly more complex Text Boxes that allow for multiple lines and more advanced text interaction. They also contain a scroll bar that shows up when the text has reached the end of the text box so that users can continue writing beyond the bounds and scroll up or down to other parts of the text.

### Constructors

```
TextBox(int xPos, int yPos, int width, int height)
TextBox(int xPos, int yPos, int width, int height, String initialText)
MultiTextbox(int xPos, int yPos, int width, int height)
MultiTextbox(int xPos, int yPos, int width, int height, String initialText)
```

### Listeners

A text boxes listener is pretty straight forward. It requires two parameters, the textbox and the previous text it held. It returns the previous text as getting the present text just requires the use of the `getText()` function on the textbox. It may also be helpful if you need to reset the textbox back to its previous state or for comparing the difference.

```
TextChangedListener( textbox, previousText -> {
//perform some action here
});
```