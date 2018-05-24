﻿Getting Started
====================

Creating a GUI with Rabbit
--------------------------

Currently Rabbit GUI is a fully client side GUI framework. That means no inventory screens as those work off of a client and server side connection.

Getting started is easy there is no registering of GUIs required, just create a class that extends `com.rabbit.gui.show.Show`

Then all you have to do is override the setup function and that's it

```
public class MyFirstGUI extends Show {

    public MyFirstGUI(){

    }

    @Override
    public void setup() {
        super.setup();

    }
```

Now that GUI won't do much but that is all you have to do to make a valid GUI. In order to open that GUI all you have to do is set the current stage through Rabbit. This can be done like so: `RabbitGui.proxy.display(new MyFirstGUI());`


Adding Components
-----------------

Adding elements to the GUI is also pretty easy. One just has to register each component within the `setup()` function using `registerComponent().`

This should look something like this:

```
    @Override
    public void setup() {

        registerComponent(new TextLabel((int) (width * .05), (int) (height * .05),
                (int) (width * .8), 20, Color.black, "Some Display Text"));

        registerComponent(new TextBox((int) (width * .1), (int) (height * .3),
                (int) (width * .8), 25, "Placeholder text")
                        .setTextChangedListener((TextBox textbox, String previousText) -> {
                            String somevariable = previousText;
                        }));

        registerComponent(new Button((int) (width * .6), (int) (height * .6),
                (int) (width * .25), 20, "Close Window").setClickListener(btn -> {
                    getStage().close();
                }));

        registerComponent(new Picture((int) (width * .2), (int) (height * .2), 
                (int) (width * .6), (int) (height * .6)), DefaultTextures.BACKGROUND1));
    }
```

Note the order of how things are registered. Things registered last are rendered in the background and things registered first are in the foreground. The GUI will scale dynamically with the window size but only if the X, Y and Width/Height are computed from the width and height. **Static values will not scale**

Some elements have callbacks that can be used for whatever purpose you see fit. In our example clicking the close window button will close the GUI, though hitting escape will also do that. The example also shows that a text box can update some variable from the previous text which was in the textbox.
