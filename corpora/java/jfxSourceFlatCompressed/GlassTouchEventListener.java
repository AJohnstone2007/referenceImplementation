package com.sun.javafx.tk.quantum;
interface GlassTouchEventListener {
public void notifyBeginTouchEvent(long time, int modifiers, boolean isDirect,
int touchEventCount);
public void notifyNextTouchEvent(long time, int type, long touchId,
int x, int y, int xAbs, int yAbs);
public void notifyEndTouchEvent(long time);
}
