Slider
====================

Sliders allow for a range of values to be selected from. What you trade off for in accuracy you gain in simplicity. The slider returns its value as a floating point between 0 and 1 inclusive. That range can then be mapped to whatever values you need to slider to output.

### Constructors

```
Slider(int xPos, int yPos, int width, int height, int scrollerSize)
```

!! Scroller size is the size of the tab to draw

### Listeners

Sliders actually have 2 listeners. One for when the slider has stopped moving, and the other for while it is being moved. This can allow something to react or change while the user is sliding it to see the effect and then lock in the change once they have released the mouse. A listener is sent the slider which performed the action and the current value between [0-1] that the slider was changed to, not the amount that the slider changed.

```
setProgressChangedListener( slider, modifier -> {
//perform some action here
});

setMouseReleasedListener( slider, modifier -> {
//perform some action here
});

```