package javafx.scene.control;
import com.sun.javafx.scene.control.Properties;
import com.sun.javafx.util.Utils;
import javafx.css.converter.EnumConverter;
import javafx.css.converter.SizeConverter;
import javafx.scene.control.skin.ScrollBarSkin;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.WritableValue;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.geometry.Orientation;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class ScrollBar extends Control {
public ScrollBar() {
setWidth(Properties.DEFAULT_WIDTH);
setHeight(Properties.DEFAULT_LENGTH);
getStyleClass().setAll(DEFAULT_STYLE_CLASS);
setAccessibleRole(AccessibleRole.SCROLL_BAR);
((StyleableProperty<Boolean>)(WritableValue<Boolean>)focusTraversableProperty()).applyStyle(null,Boolean.FALSE);
pseudoClassStateChanged(HORIZONTAL_PSEUDOCLASS_STATE, true);
}
private DoubleProperty min;
public final void setMin(double value) {
minProperty().set(value);
}
public final double getMin() {
return min == null ? 0 : min.get();
}
public final DoubleProperty minProperty() {
if (min == null) {
min = new SimpleDoubleProperty(this, "min");
}
return min;
}
private DoubleProperty max;
public final void setMax(double value) {
maxProperty().set(value);
}
public final double getMax() {
return max == null ? 100 : max.get();
}
public final DoubleProperty maxProperty() {
if (max == null) {
max = new SimpleDoubleProperty(this, "max", 100);
}
return max;
}
private DoubleProperty value;
public final void setValue(double value) {
valueProperty().set(value);
}
public final double getValue() {
return value == null ? 0 : value.get();
}
public final DoubleProperty valueProperty() {
if (value == null) {
value = new SimpleDoubleProperty(this, "value");
}
return value;
}
private ObjectProperty<Orientation> orientation;
public final void setOrientation(Orientation value) {
orientationProperty().set(value);
}
public final Orientation getOrientation() {
return orientation == null ? Orientation.HORIZONTAL : orientation.get();
}
public final ObjectProperty<Orientation> orientationProperty() {
if (orientation == null) {
orientation = new StyleableObjectProperty<Orientation>(Orientation.HORIZONTAL) {
@Override protected void invalidated() {
final boolean vertical = (get() == Orientation.VERTICAL);
pseudoClassStateChanged(VERTICAL_PSEUDOCLASS_STATE, vertical);
pseudoClassStateChanged(HORIZONTAL_PSEUDOCLASS_STATE, !vertical);
}
@Override
public CssMetaData<ScrollBar,Orientation> getCssMetaData() {
return StyleableProperties.ORIENTATION;
}
@Override
public Object getBean() {
return ScrollBar.this;
}
@Override
public String getName() {
return "orientation";
}
};
}
return orientation;
}
private DoubleProperty unitIncrement;
public final void setUnitIncrement(double value) {
unitIncrementProperty().set(value);
}
public final double getUnitIncrement() {
return unitIncrement == null ? 1 : unitIncrement.get();
}
public final DoubleProperty unitIncrementProperty() {
if (unitIncrement == null) {
unitIncrement = new StyleableDoubleProperty(1) {
@Override
public CssMetaData<ScrollBar,Number> getCssMetaData() {
return StyleableProperties.UNIT_INCREMENT;
}
@Override
public Object getBean() {
return ScrollBar.this;
}
@Override
public String getName() {
return "unitIncrement";
}
};
}
return unitIncrement;
}
private DoubleProperty blockIncrement;
public final void setBlockIncrement(double value) {
blockIncrementProperty().set(value);
}
public final double getBlockIncrement() {
return blockIncrement == null ? 10 : blockIncrement.get();
}
public final DoubleProperty blockIncrementProperty() {
if (blockIncrement == null) {
blockIncrement = new StyleableDoubleProperty(10) {
@Override
public CssMetaData<ScrollBar,Number> getCssMetaData() {
return StyleableProperties.BLOCK_INCREMENT;
}
@Override
public Object getBean() {
return ScrollBar.this;
}
@Override
public String getName() {
return "blockIncrement";
}
};
}
return blockIncrement;
}
private DoubleProperty visibleAmount;
public final void setVisibleAmount(double value) {
visibleAmountProperty().set(value);
}
public final double getVisibleAmount() {
return visibleAmount == null ? 15 : visibleAmount.get();
}
public final DoubleProperty visibleAmountProperty() {
if (visibleAmount == null) {
visibleAmount = new SimpleDoubleProperty(this, "visibleAmount");
}
return visibleAmount;
}
public void adjustValue(double position) {
double posValue = ((getMax() - getMin()) * Utils.clamp(0, position, 1))+getMin();
double newValue;
if (Double.compare(posValue, getValue()) != 0) {
if (posValue > getValue()) {
newValue = getValue() + getBlockIncrement();
}
else {
newValue = getValue() - getBlockIncrement();
}
setValue(Utils.clamp(getMin(), newValue, getMax()));
}
}
public void increment() {
setValue(Utils.clamp(getMin(), getValue() + getUnitIncrement(), getMax()));
}
public void decrement() {
setValue(Utils.clamp(getMin(), getValue() - getUnitIncrement(), getMax()));
}
private void blockIncrement() {
adjustValue(getValue() + getBlockIncrement());
}
private void blockDecrement() {
adjustValue(getValue() - getBlockIncrement());
}
@Override protected Skin<?> createDefaultSkin() {
return new ScrollBarSkin(this);
}
private static final String DEFAULT_STYLE_CLASS = "scroll-bar";
private static class StyleableProperties {
private static final CssMetaData<ScrollBar,Orientation> ORIENTATION =
new CssMetaData<ScrollBar,Orientation>("-fx-orientation",
new EnumConverter<Orientation>(Orientation.class),
Orientation.HORIZONTAL) {
@Override
public Orientation getInitialValue(ScrollBar node) {
return node.getOrientation();
}
@Override
public boolean isSettable(ScrollBar n) {
return n.orientation == null || !n.orientation.isBound();
}
@Override
public StyleableProperty<Orientation> getStyleableProperty(ScrollBar n) {
return (StyleableProperty<Orientation>)(WritableValue<Orientation>)n.orientationProperty();
}
};
private static final CssMetaData<ScrollBar,Number> UNIT_INCREMENT =
new CssMetaData<ScrollBar,Number>("-fx-unit-increment",
SizeConverter.getInstance(), 1.0) {
@Override
public boolean isSettable(ScrollBar n) {
return n.unitIncrement == null || !n.unitIncrement.isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(ScrollBar n) {
return (StyleableProperty<Number>)(WritableValue<Number>)n.unitIncrementProperty();
}
};
private static final CssMetaData<ScrollBar,Number> BLOCK_INCREMENT =
new CssMetaData<ScrollBar,Number>("-fx-block-increment",
SizeConverter.getInstance(), 10.0) {
@Override
public boolean isSettable(ScrollBar n) {
return n.blockIncrement == null || !n.blockIncrement.isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(ScrollBar n) {
return (StyleableProperty<Number>)(WritableValue<Number>)n.blockIncrementProperty();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
styleables.add(ORIENTATION);
styleables.add(UNIT_INCREMENT);
styleables.add(BLOCK_INCREMENT);
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
private static final PseudoClass VERTICAL_PSEUDOCLASS_STATE =
PseudoClass.getPseudoClass("vertical");
private static final PseudoClass HORIZONTAL_PSEUDOCLASS_STATE =
PseudoClass.getPseudoClass("horizontal");
@Override protected Boolean getInitialFocusTraversable() {
return Boolean.FALSE;
}
@Override
public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch (attribute) {
case VALUE: return getValue();
case MAX_VALUE: return getMax();
case MIN_VALUE: return getMin();
case ORIENTATION: return getOrientation();
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
@Override
public void executeAccessibleAction(AccessibleAction action, Object... parameters) {
switch (action) {
case INCREMENT: increment(); break;
case DECREMENT: decrement(); break;
case BLOCK_INCREMENT: blockIncrement(); break;
case BLOCK_DECREMENT: blockDecrement(); break;
case SET_VALUE: {
Double value = (Double) parameters[0];
if (value != null) setValue(value);
break;
}
default: super.executeAccessibleAction(action, parameters);
}
}
}
