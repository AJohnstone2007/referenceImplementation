package com.sun.javafx.embed;
public interface EmbeddedStageInterface {
public void setLocation(int x, int y);
public void setSize(int width, int height);
public void setFocused(boolean focused, int focusCause);
public void focusUngrab();
}
