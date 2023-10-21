package com.sun.javafx.scene.text;
public interface TextLayoutFactory {
public TextLayout createLayout();
public TextLayout getLayout();
public void disposeLayout(TextLayout layout);
}
