Panels
====================

Panels are an extremely useful part of the Rabbit GUI framework. It allows you to have multiple interactable windows on one screen without having to switch between screens. You can use it as a popup to alert a player of an action they performed, show multiple states, or really whatever you need. Creating and adding elements to a panel is the same as you would add an element to your gui.

Creating a Panel
----------------

Like any other component they should be created in the setup function of a class that subclasses show. The position and size elements work the same as other elements.

### Constructors
```
Panel(int xPos, int yPos, int width, int height)
Panel(int xPos, int yPos, int width, int height, boolean visible)
```

Panels also have the ability to be toggled on and off with their visibility. Invisible panels are not be interactable but retain their state so long as the screen is active. Panels can also be dragged around the screen when clicked and held on the left side of the panel. Optionally the panels can be set to dim to 50% transparency when not hovered over.

### Adding Elements

Once you register a panel you can add components to it using the same function call but as a member function of the panel `registerComponent()`. If you need the component to dynamically resize with the panel set its location and size from the panels parameters using the `getHeight()` and `getWidth()`

Note that elements position is relative to the panels position so a componenet added to the panel with a position of X:0 and Y:0 will be in the top left of the panel, not the screen itself. If you want elements to be on the left or top of the panel you will need to use negative position values.