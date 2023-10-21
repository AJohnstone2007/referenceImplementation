package javafx.scene.control;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import com.sun.javafx.collections.TrackableObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.skin.AccordionSkin;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.value.WritableValue;
import javafx.css.StyleableProperty;
import java.util.List;
public class Accordion extends Control {
private boolean biasDirty = true;
private Orientation bias;
public Accordion() {
this((TitledPane[])null);
}
public Accordion(TitledPane... titledPanes) {
getStyleClass().setAll(DEFAULT_STYLE_CLASS);
if (titledPanes != null) {
getPanes().addAll(titledPanes);
}
((StyleableProperty<Boolean>)(WritableValue<Boolean>)focusTraversableProperty()).applyStyle(null, Boolean.FALSE);
}
private final ObservableList<TitledPane> panes = new TrackableObservableList<TitledPane>() {
@Override protected void onChanged(ListChangeListener.Change<TitledPane> c) {
while (c.next()) {
if (c.wasRemoved() && !expandedPane.isBound()) {
for (TitledPane pane : c.getRemoved()) {
if (!c.getAddedSubList().contains(pane) && getExpandedPane() == pane) {
setExpandedPane(null);
break;
}
}
}
}
}
};
private ObjectProperty<TitledPane> expandedPane = new ObjectPropertyBase<TitledPane>() {
private TitledPane oldValue;
@Override
protected void invalidated() {
final TitledPane value = get();
if (value != null) {
value.setExpanded(true);
} else {
if (oldValue != null) {
oldValue.setExpanded(false);
}
}
oldValue = value;
}
@Override
public String getName() {
return "expandedPane";
}
@Override
public Object getBean() {
return Accordion.this;
}
};
public final void setExpandedPane(TitledPane value) { expandedPaneProperty().set(value); }
public final TitledPane getExpandedPane() { return expandedPane.get(); }
public final ObjectProperty<TitledPane> expandedPaneProperty() { return expandedPane; }
public final ObservableList<TitledPane> getPanes() { return panes; }
@Override protected Skin<?> createDefaultSkin() {
return new AccordionSkin(this);
}
@Override public void requestLayout() {
biasDirty = true;
bias = null;
super.requestLayout();
}
@Override public Orientation getContentBias() {
if (biasDirty) {
bias = null;
final List<Node> children = getManagedChildren();
for (Node child : children) {
Orientation contentBias = child.getContentBias();
if (contentBias != null) {
bias = contentBias;
if (contentBias == Orientation.HORIZONTAL) {
break;
}
}
}
biasDirty = false;
}
return bias;
}
private static final String DEFAULT_STYLE_CLASS = "accordion";
@Override protected Boolean getInitialFocusTraversable() {
return Boolean.FALSE;
}
}
