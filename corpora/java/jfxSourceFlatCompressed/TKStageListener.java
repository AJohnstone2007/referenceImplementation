package com.sun.javafx.tk;
public interface TKStageListener {
public void changedLocation(float x, float y);
public void changedSize(float width, float height);
public void changedScale(float xScale, float yScale);
public void changedFocused(boolean focused, FocusCause cause);
public void changedIconified(boolean iconified);
public void changedMaximized(boolean maximized);
public void changedAlwaysOnTop(boolean alwaysOnTop);
public void changedResizable(boolean resizable);
public void changedFullscreen(boolean fs);
public void changedScreen(Object from, Object to);
public void closing();
public void closed();
public void focusUngrab();
}
