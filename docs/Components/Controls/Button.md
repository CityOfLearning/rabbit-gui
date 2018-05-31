Buttons
====================

One of the most basic GUI elements is a button and Rabbit GUI gives you options for the style that suits you. The types of buttons are `Button`, `ToggleButton`, `CheckBoxButton`, `CheckBoxPictureButton`, `PictureButton`, and `PictureToggleButton`. Hopefully the names give away the style of each and extending the class to suit your needs is also quite easy.

### Constructors

```
Button(int xPos, int yPos, int width, int height, String title)
CheckBoxButton(int xPos, int yPos, int width, int height, String text, boolean checked)
CheckBoxPictureButton(int xPos, int yPos, int width, int height, ResourceLocation texture, boolean checked)
CheckBoxPictureButton(int xPos, int yPos, int width, int height, String texture, boolean checked)
PictureButton(int xPos, int yPos, int width, int height, ResourceLocation texture)
PictureButton(int xPos, int yPos, int width, int height, String texture)
PictureToggleButton(int xPos, int yPos, int width, int height, ResourceLocation ontexture, ResourceLocation offtexture, boolean toggled)
PictureToggleButton(int xPos, int yPos, int width, int height, ResourceLocation ontexture, String offtexture, boolean toggled)
PictureToggleButton(int xPos, int yPos, int width, int height, String ontexture, ResourceLocation offtexture, boolean toggled)
PictureToggleButton(int xPos, int yPos, int width, int height, String ontexture, String offtexture, boolean toggled)
ToggleButton(int xPos, int yPos, int width, int height, String title, boolean toggle)
```

### Listeners

Buttons wouldn't be much use without a listener for when the button is clicked. Buttons are probably the easiest listener to create as a simple lamba or function where the only parameter is the type of button is passed is required.

For example:

```
setClickListener( btn -> {
//perform some action here
});
```

Picture Buttons
---------------

Some say a picture is worth a thousand words. If you need to use a thousand words to describe what your button does you might want to use a picture instead. Pictures can grab from resources paked with a mod or from a path given in the form of a string. This allows textures to be dynamic if needed. 

Checkbox Buttons
----------------

The checkbox button and its variations are like a normal button only that when they are toggled a box that is drawn with the button will be checked. Optionally instead of text one might elect to use a Picture much like described regarding picture buttons only this will also have a checkbox overlaying a corner of the button. 

Toggle Buttons
--------------

If you want your button to have an obvious state when active you might want to use a toggle button or one of the variants. A toggle state is the same as the checkbox only there is no checkbox overlayed on the button and different resources can be used to show its current state.
