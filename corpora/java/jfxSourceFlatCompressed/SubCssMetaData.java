package com.sun.javafx.css;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.StyleableProperty;
import javafx.scene.Node;
public class SubCssMetaData<T> extends CssMetaData<Node,T> {
public SubCssMetaData(String property, StyleConverter converter, T initialValue) {
super(property, converter, initialValue);
}
public SubCssMetaData(String property, StyleConverter converter) {
super(property, converter);
}
@Override public boolean isSettable(Node node) { return false; }
@Override public StyleableProperty<T> getStyleableProperty(Node node) { return null; }
}
