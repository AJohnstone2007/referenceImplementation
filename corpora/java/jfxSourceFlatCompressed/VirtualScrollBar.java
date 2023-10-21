package com.sun.javafx.scene.control;
import com.sun.javafx.util.Utils;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.skin.VirtualFlow;
public class VirtualScrollBar extends ScrollBar {
private final VirtualFlow flow;
private boolean adjusting;
public VirtualScrollBar(final VirtualFlow flow) {
this.flow = flow;
super.valueProperty().addListener(valueModel -> {
if (isVirtual() ) {
if (adjusting) {
} else {
flow.setPosition(getValue());
}
}
});
}
private BooleanProperty virtual = new SimpleBooleanProperty(this, "virtual");
public final void setVirtual(boolean value) {
virtual.set(value);
}
public final boolean isVirtual() {
return virtual.get();
}
public final BooleanProperty virtualProperty() {
return virtual;
}
@Override public void decrement() {
if (isVirtual()) {
flow.scrollPixels(-10);
} else {
super.decrement();
}
}
@Override public void increment() {
if (isVirtual()) {
flow.scrollPixels(10);
} else {
super.increment();
}
}
@Override public void adjustValue(double pos) {
if (isVirtual()) {
adjusting = true;
double oldValue = flow.getPosition();
double newValue = ((getMax() - getMin()) * Utils.clamp(0, pos, 1))+getMin();
if (newValue < oldValue) {
IndexedCell cell = flow.getFirstVisibleCell();
if (cell == null) return;
flow.scrollToBottom(cell);
} else if (newValue > oldValue) {
IndexedCell cell = flow.getLastVisibleCell();
if (cell == null) return;
flow.scrollToTop(cell);
}
adjusting = false;
} else {
super.adjustValue(pos);
}
}
}
