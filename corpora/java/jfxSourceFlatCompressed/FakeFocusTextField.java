package com.sun.javafx.scene.control;
import javafx.scene.AccessibleAttribute;
import javafx.scene.control.TextField;
public final class FakeFocusTextField extends TextField {
@Override public void requestFocus() {
if (getParent() != null) {
getParent().requestFocus();
}
}
public void setFakeFocus(boolean b) {
setFocused(b);
}
@Override
public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch (attribute) {
case FOCUS_ITEM:
return getParent();
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
}
