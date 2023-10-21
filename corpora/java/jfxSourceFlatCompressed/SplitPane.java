package javafx.scene.control;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.WeakHashMap;
import javafx.beans.DefaultProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.WritableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.css.StyleableObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.converter.EnumConverter;
import javafx.scene.control.skin.SplitPaneSkin;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
@DefaultProperty("items")
public class SplitPane extends Control {
private static final String RESIZABLE_WITH_PARENT = "resizable-with-parent";
public static void setResizableWithParent(Node node, Boolean value) {
if (value == null) {
node.getProperties().remove(RESIZABLE_WITH_PARENT);
} else {
node.getProperties().put(RESIZABLE_WITH_PARENT, value);
}
}
public static Boolean isResizableWithParent(Node node) {
if (node.hasProperties()) {
Object value = node.getProperties().get(RESIZABLE_WITH_PARENT);
if (value != null) {
return (Boolean)value;
}
}
return true;
}
public SplitPane() {
this((Node[])null);
}
public SplitPane(Node... items) {
getStyleClass().setAll(DEFAULT_STYLE_CLASS);
((StyleableProperty<Boolean>)(WritableValue<Boolean>)focusTraversableProperty()).applyStyle(null, Boolean.FALSE);
getItems().addListener(new ListChangeListener<Node>() {
@Override public void onChanged(Change<? extends Node> c) {
while (c.next()) {
int from = c.getFrom();
int index = from;
for (int i = 0; i < c.getRemovedSize(); i++) {
if (index < dividers.size()) {
dividerCache.put(index, Double.MAX_VALUE);
} else if (index == dividers.size()) {
if (!dividers.isEmpty()) {
if (c.wasReplaced()) {
dividerCache.put(index - 1, dividers.get(index - 1).getPosition());
} else {
dividerCache.put(index - 1, Double.MAX_VALUE);
}
}
}
index++;
}
for (int i = 0; i < dividers.size(); i++) {
if (dividerCache.get(i) == null) {
dividerCache.put(i, dividers.get(i).getPosition());
}
}
}
dividers.clear();
for (int i = 0; i < getItems().size() - 1; i++) {
if (dividerCache.containsKey(i) && dividerCache.get(i) != Double.MAX_VALUE) {
Divider d = new Divider();
d.setPosition(dividerCache.get(i));
dividers.add(d);
} else {
dividers.add(new Divider());
}
dividerCache.remove(i);
}
}
});
if (items != null) {
getItems().addAll(items);
}
pseudoClassStateChanged(HORIZONTAL_PSEUDOCLASS_STATE, true);
}
private ObjectProperty<Orientation> orientation;
public final void setOrientation(Orientation value) {
orientationProperty().set(value);
};
public final Orientation getOrientation() {
return orientation == null ? Orientation.HORIZONTAL : orientation.get();
}
public final ObjectProperty<Orientation> orientationProperty() {
if (orientation == null) {
orientation = new StyleableObjectProperty<Orientation>(Orientation.HORIZONTAL) {
@Override public void invalidated() {
final boolean isVertical = (get() == Orientation.VERTICAL);
pseudoClassStateChanged(VERTICAL_PSEUDOCLASS_STATE, isVertical);
pseudoClassStateChanged(HORIZONTAL_PSEUDOCLASS_STATE, !isVertical);
}
@Override public CssMetaData<SplitPane,Orientation> getCssMetaData() {
return StyleableProperties.ORIENTATION;
}
@Override
public Object getBean() {
return SplitPane.this;
}
@Override
public String getName() {
return "orientation";
}
};
}
return orientation;
}
private final ObservableList<Node> items = FXCollections.observableArrayList();
private final ObservableList<Divider> dividers = FXCollections.observableArrayList();
private final ObservableList<Divider> unmodifiableDividers = FXCollections.unmodifiableObservableList(dividers);
private final WeakHashMap<Integer, Double> dividerCache = new WeakHashMap<Integer, Double>();
public ObservableList<Node> getItems() {
return items;
}
public ObservableList<Divider> getDividers() {
return unmodifiableDividers;
}
public void setDividerPosition(int dividerIndex, double position) {
if (getDividers().size() <= dividerIndex) {
dividerCache.put(dividerIndex, position);
return;
}
if (dividerIndex >= 0) {
getDividers().get(dividerIndex).setPosition(position);
}
}
public void setDividerPositions(double... positions) {
if (dividers.isEmpty()) {
for (int i = 0; i < positions.length; i++) {
dividerCache.put(i, positions[i]);
}
return;
}
for (int i = 0; i < positions.length && i < dividers.size(); i++) {
dividers.get(i).setPosition(positions[i]);
}
}
public double[] getDividerPositions() {
double[] positions = new double[dividers.size()];
for (int i = 0; i < dividers.size(); i++) {
positions[i] = dividers.get(i).getPosition();
}
return positions;
}
@Override protected Skin<?> createDefaultSkin() {
return new SplitPaneSkin(this);
}
private static final String DEFAULT_STYLE_CLASS = "split-pane";
private static class StyleableProperties {
private static final CssMetaData<SplitPane,Orientation> ORIENTATION =
new CssMetaData<SplitPane,Orientation>("-fx-orientation",
new EnumConverter<Orientation>(Orientation.class),
Orientation.HORIZONTAL) {
@Override
public Orientation getInitialValue(SplitPane node) {
return node.getOrientation();
}
@Override
public boolean isSettable(SplitPane n) {
return n.orientation == null || !n.orientation.isBound();
}
@Override
public StyleableProperty<Orientation> getStyleableProperty(SplitPane n) {
return (StyleableProperty<Orientation>)(WritableValue<Orientation>)n.orientationProperty();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
styleables.add(ORIENTATION);
STYLEABLES = Collections.unmodifiableList(styleables);
}
}
public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
return StyleableProperties.STYLEABLES;
}
@Override
public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
return getClassCssMetaData();
}
private static final PseudoClass VERTICAL_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("vertical");
private static final PseudoClass HORIZONTAL_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("horizontal");
@Override protected Boolean getInitialFocusTraversable() {
return Boolean.FALSE;
}
public static class Divider {
public Divider() {
}
private DoubleProperty position;
public final void setPosition(double value) {
positionProperty().set(value);
}
public final double getPosition() {
return position == null ? 0.5F : position.get();
}
public final DoubleProperty positionProperty() {
if (position == null) {
position = new SimpleDoubleProperty(this, "position", 0.5F);
}
return position;
}
}
}
