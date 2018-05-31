Progress Bar
====================

Sometimes things take awhile to process and without any indication a player might think the GUI is frozen or unresponsive. Show them that something is happening when a process takes more than a second to happen. 

The Double Listener
-------------------

A progress bar works with a change listener that when modified notifies the progress bar to update. This listener takes a value between 0 and 1 inclusive with a 1 filling the progress bar and a 0 means no progress. 

Here is an asycronous example 

```
progBar = new ProgressBar((int) (width * .15), (int) (height * .7), (int) (width / 3.3), 20);
progBar.getProgressChangedListener().setValue(0);

Runnable task = () -> {
	// this blocks and so we gotta thread it

	int progress = 0;
	for (EntityPlayer player : ServerRoster) {
		progBar.getProgressChangedListener()
				.setValue(((float) progress++) / ServerRoster.size());
		player.grabMissingData();
	}
	progBar.setVisible(false);
	textLabel.setText("Filled Missing Player Data");
};
progBar.setVisible(true);
new Thread(task).start();

```

Alternatively the progress bar can just have its progress set directly but using the listener is safer and allows for asyncronus control. 

### Constructors

```
ProgressBar(int x, int y, int width, int height)
```