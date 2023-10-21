package com.sun.javafx.scene.control.behavior;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.PopupControl;
import javafx.scene.paint.Color;
public class ColorPickerBehavior extends ComboBoxBaseBehavior<Color> {
public ColorPickerBehavior(final ColorPicker colorPicker) {
super(colorPicker);
}
@Override public void onAutoHide(PopupControl popup) {
if (!popup.isShowing() && getNode().isShowing()) {
getNode().hide();
}
if (!getNode().isShowing()) {
super.onAutoHide(popup);
}
}
}
