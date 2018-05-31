Dropdown
====================

A dropdown is what you would expect it to be. Click it to open a menu with a list of options available. These choices will show a string value but contain whatever generic is declared when creating the dropdown. This allows for readability and for utility. Consider a dropdown of recipes that when selected returns all the items needed and the pattern into an extended crafting table or something similar. You could use the generic template to store the recipe in a dropdown menu.

Dropdown Element
----------------

What is described above is due to the dropdown element class. Whatever element is stored should have a valid toString() function because that is how it is indexed and retrieved. The dropdown class acts the same as a list and can add elements using common list functions `add` and `addAll`. When adding to the dropdown a dropdown element is dynamically created so whatever generic is given is all that has to be passed to an `add` function. 

#### Element Functions

```
int getItemIndex()
String getItemName()
<Type Generic> getValue()
```

### Constructors

```
DropDown(int xPos, int yPos, int width, int height)
DropDown(int xPos, int yPos, int width, int height, String text)
DropDown(int xPos, int yPos, int width, int height, String text, T... values)
DropDown(int xPos, int yPos, int width, int height, T... values)
```

### Listeners

A dropdown listener is slightly more complex since it doesn't return the element but rather the dropbox affected and the index (string) that was selected. If you want the element you can use the `getSelectedElement()` function from the returned dropdown and use its member functions to access the value.

```
ItemSelectedListener( dropbox, index -> {
//perform some action here
});
```