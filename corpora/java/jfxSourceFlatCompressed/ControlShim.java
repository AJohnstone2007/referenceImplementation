package javafx.scene.control;
import javafx.beans.property.StringProperty;
public class ControlShim extends Control {
public static void installDefaultSkin(Control control) {
control.setSkin(control.createDefaultSkin());
}
public static StringProperty skinClassNameProperty(Control c) {
return c.skinClassNameProperty();
}
public static void layoutChildren(Control c) {
c.layoutChildren();
}
public static double computePrefWidth(Control c, double height) {
return c.computePrefWidth(height);
}
public static double computePrefHeight(Control c, double width) {
return c.computePrefHeight(width);
}
}
