# Tips #

  * Access localhost from Android emulator
    * Use 10.0.2.2 instead of 127.0.0.1
  * Changing fonts in an Android app
| Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Black.ttf");<br>TextView tv = (TextView) findViewById(R.id.FontTextView);<br>tv.setTypeface(tf); 