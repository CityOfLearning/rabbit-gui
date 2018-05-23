Check Boxes
====================

A checkbox is really just a simple button but the information it conveys can be quite different. Need to pack a GUI with lots of toggleable elements? Maybe the checkbox is what you want.

### Constructors

```
CheckBox(int xPos, int yPos, int width, int height, Color checkColor, Color textColor, String title, boolean checked)
CheckBox(int xPos, int yPos, int width, int height, String title, boolean checked)
CheckBox(int xPos, int yPos, String title, boolean checked)
CheckBox(int xPos, int yPos, String title, Color textColor, boolean checked)
CheckBox(int xPos, int yPos, String title, Color checkColor, Color textColor, boolean checked)
```

### Listeners

Similar to the button a checkbox also has its own listener which takes one parameter, the checkbox that was just checked/unchecked

```
CheckBoxStatusChangedListener( chkbox -> {
//perform some action here
});
```