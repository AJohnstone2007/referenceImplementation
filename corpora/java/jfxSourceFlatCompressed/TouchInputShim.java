package com.sun.glass.ui.monocle;
public class TouchInputShim {
public static int getTouchRadius() {
return TouchInput.getInstance().getTouchRadius();
}
}
