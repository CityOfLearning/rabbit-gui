Number Picker
====================

A number picker is really just two buttons that increment/decrement a value. The number can be set to change by a certain value inbetween the min and max range of values inclusive [min-max]

### Constructors

```
NumberPicker(int x, int y, int width, int height)
NumberPicker(int x, int y, int width, int height, int value)
```

### Listeners

A number picker listener will be sent the picker that was interacted with and its value

```
setListener(picker, value -> {
//perform some action here
});
```