package javafx.scene.control;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.WritableValue;
import javafx.geometry.Orientation;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.util.StringConverter;
import com.sun.javafx.util.Utils;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableIntegerProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.EnumConverter;
import javafx.css.converter.SizeConverter;
import javafx.scene.control.skin.SliderSkin;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
public class Slider extends Control {
public Slider() {
initialize();
}
public Slider(double min, double max, double value) {
setMax(max);
setMin(min);
setValue(value);
adjustValues();
initialize();
}
private void initialize() {
getStyleClass().setAll(DEFAULT_STYLE_CLASS);
setAccessibleRole(AccessibleRole.SLIDER);
pseudoClassStateChanged(HORIZONTAL_PSEUDOCLASS_STATE, true);
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
max = new DoublePropertyBase(100) {
@Override protected void invalidated() {
if (get() < getMin()) {
setMin(get());
}
adjustValues();
notifyAccessibleAttributeChanged(AccessibleAttribute.MAX_VALUE);
}
@Override
public Object getBean() {
return Slider.this;
}
@Override
public String getName() {
return "max";
}
};
}
return max;
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
min = new DoublePropertyBase(0) {
@Override protected void invalidated() {
if (get() > getMax()) {
setMax(get());
}
adjustValues();
notifyAccessibleAttributeChanged(AccessibleAttribute.MIN_VALUE);
}
@Override
public Object getBean() {
return Slider.this;
}
@Override
public String getName() {
return "min";
}
};
}
return min;
}
private DoubleProperty value;
public final void setValue(double value) {
if (!valueProperty().isBound()) valueProperty().set(value);
}
public final double getValue() {
return value == null ? 0 : value.get();
}
public final DoubleProperty valueProperty() {
if (value == null) {
value = new DoublePropertyBase(0) {
@Override protected void invalidated() {
adjustValues();
notifyAccessibleAttributeChanged(AccessibleAttribute.VALUE);
}
@Override
public Object getBean() {
return Slider.this;
}
@Override
public String getName() {
return "value";
}
};
}
return value;
}
private BooleanProperty valueChanging;
public final void setValueChanging(boolean value) {
valueChangingProperty().set(value);
}
public final boolean isValueChanging() {
return valueChanging == null ? false : valueChanging.get();
}
public final BooleanProperty valueChangingProperty() {
if (valueChanging == null) {
valueChanging = new SimpleBooleanProperty(this, "valueChanging", false);
}
return valueChanging;
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
public CssMetaData<Slider,Orientation> getCssMetaData() {
return StyleableProperties.ORIENTATION;
}
@Override
public Object getBean() {
return Slider.this;
}
@Override
public String getName() {
return "orientation";
}
};
}
return orientation;
}
private BooleanProperty showTickLabels;
public final void setShowTickLabels(boolean value) {
showTickLabelsProperty().set(value);
}
public final boolean isShowTickLabels() {
return showTickLabels == null ? false : showTickLabels.get();
}
public final BooleanProperty showTickLabelsProperty() {
if (showTickLabels == null) {
showTickLabels = new StyleableBooleanProperty(false) {
@Override
public CssMetaData<Slider,Boolean> getCssMetaData() {
return StyleableProperties.SHOW_TICK_LABELS;
}
@Override
public Object getBean() {
return Slider.this;
}
@Override
public String getName() {
return "showTickLabels";
}
};
}
return showTickLabels;
}
private BooleanProperty showTickMarks;
public final void setShowTickMarks(boolean value) {
showTickMarksProperty().set(value);
}
public final boolean isShowTickMarks() {
return showTickMarks == null ? false : showTickMarks.get();
}
public final BooleanProperty showTickMarksProperty() {
if (showTickMarks == null) {
showTickMarks = new StyleableBooleanProperty(false) {
@Override
public CssMetaData<Slider,Boolean> getCssMetaData() {
return StyleableProperties.SHOW_TICK_MARKS;
}
@Override
public Object getBean() {
return Slider.this;
}
@Override
public String getName() {
return "showTickMarks";
}
};
}
return showTickMarks;
}
private DoubleProperty majorTickUnit;
public final void setMajorTickUnit(double value) {
if (value <= 0) {
throw new IllegalArgumentException("MajorTickUnit cannot be less than or equal to 0.");
}
majorTickUnitProperty().set(value);
}
public final double getMajorTickUnit() {
return majorTickUnit == null ? 25 : majorTickUnit.get();
}
public final DoubleProperty majorTickUnitProperty() {
if (majorTickUnit == null) {
majorTickUnit = new StyleableDoubleProperty(25) {
@Override
public void invalidated() {
if (get() <= 0) {
throw new IllegalArgumentException("MajorTickUnit cannot be less than or equal to 0.");
}
}
@Override
public CssMetaData<Slider,Number> getCssMetaData() {
return StyleableProperties.MAJOR_TICK_UNIT;
}
@Override
public Object getBean() {
return Slider.this;
}
@Override
public String getName() {
return "majorTickUnit";
}
};
}
return majorTickUnit;
}
private IntegerProperty minorTickCount;
public final void setMinorTickCount(int value) {
minorTickCountProperty().set(value);
}
public final int getMinorTickCount() {
return minorTickCount == null ? 3 : minorTickCount.get();
}
public final IntegerProperty minorTickCountProperty() {
if (minorTickCount == null) {
minorTickCount = new StyleableIntegerProperty(3) {
@Override
public CssMetaData<Slider,Number> getCssMetaData() {
return StyleableProperties.MINOR_TICK_COUNT;
}
@Override
public Object getBean() {
return Slider.this;
}
@Override
public String getName() {
return "minorTickCount";
}
};
}
return minorTickCount;
}
private BooleanProperty snapToTicks;
public final void setSnapToTicks(boolean value) {
snapToTicksProperty().set(value);
}
public final boolean isSnapToTicks() {
return snapToTicks == null ? false : snapToTicks.get();
}
public final BooleanProperty snapToTicksProperty() {
if (snapToTicks == null) {
snapToTicks = new StyleableBooleanProperty(false) {
@Override
public CssMetaData<Slider,Boolean> getCssMetaData() {
return StyleableProperties.SNAP_TO_TICKS;
}
@Override
public Object getBean() {
return Slider.this;
}
@Override
public String getName() {
return "snapToTicks";
}
};
}
return snapToTicks;
}
private ObjectProperty<StringConverter<Double>> labelFormatter;
public final void setLabelFormatter(StringConverter<Double> value) {
labelFormatterProperty().set(value);
}
public final StringConverter<Double> getLabelFormatter() {
return labelFormatter == null ? null : labelFormatter.get();
}
public final ObjectProperty<StringConverter<Double>> labelFormatterProperty() {
if (labelFormatter == null) {
labelFormatter = new SimpleObjectProperty<StringConverter<Double>>(this, "labelFormatter");
}
return labelFormatter;
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
public CssMetaData<Slider,Number> getCssMetaData() {
return StyleableProperties.BLOCK_INCREMENT;
}
@Override
public Object getBean() {
return Slider.this;
}
@Override
public String getName() {
return "blockIncrement";
}
};
}
return blockIncrement;
}
public void adjustValue(double newValue) {
final double _min = getMin();
final double _max = getMax();
if (_max <= _min) return;
newValue = newValue < _min ? _min : newValue;
newValue = newValue > _max ? _max : newValue;
setValue(snapValueToTicks(newValue));
}
public void increment() {
adjustValue(getValue() + getBlockIncrement());
}
public void decrement() {
adjustValue(getValue() - getBlockIncrement());
}
private void adjustValues() {
if ((getValue() < getMin() || getValue() > getMax()) )
setValue(Utils.clamp(getMin(), getValue(), getMax()));
}
private double snapValueToTicks(double val) {
double v = val;
if (isSnapToTicks()) {
double tickSpacing = 0;
if (getMinorTickCount() != 0) {
tickSpacing = getMajorTickUnit() / (Math.max(getMinorTickCount(),0)+1);
} else {
tickSpacing = getMajorTickUnit();
}
int prevTick = (int)((v - getMin())/ tickSpacing);
double prevTickValue = (prevTick) * tickSpacing + getMin();
double nextTickValue = (prevTick + 1) * tickSpacing + getMin();
v = Utils.nearest(prevTickValue, v, nextTickValue);
}
return Utils.clamp(getMin(), v, getMax());
}
@Override protected Skin<?> createDefaultSkin() {
return new SliderSkin(this);
}
private static final String DEFAULT_STYLE_CLASS = "slider";
private static class StyleableProperties {
private static final CssMetaData<Slider,Number> BLOCK_INCREMENT =
new CssMetaData<Slider,Number>("-fx-block-increment",
SizeConverter.getInstance(), 10.0) {
@Override
public boolean isSettable(Slider n) {
return n.blockIncrement == null || !n.blockIncrement.isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(Slider n) {
return (StyleableProperty<Number>)(WritableValue<Number>)n.blockIncrementProperty();
}
};
private static final CssMetaData<Slider,Boolean> SHOW_TICK_LABELS =
new CssMetaData<Slider,Boolean>("-fx-show-tick-labels",
BooleanConverter.getInstance(), Boolean.FALSE) {
@Override
public boolean isSettable(Slider n) {
return n.showTickLabels == null || !n.showTickLabels.isBound();
}
@Override
public StyleableProperty<Boolean> getStyleableProperty(Slider n) {
return (StyleableProperty<Boolean>)(WritableValue<Boolean>)n.showTickLabelsProperty();
}
};
private static final CssMetaData<Slider,Boolean> SHOW_TICK_MARKS =
new CssMetaData<Slider,Boolean>("-fx-show-tick-marks",
BooleanConverter.getInstance(), Boolean.FALSE) {
@Override
public boolean isSettable(Slider n) {
return n.showTickMarks == null || !n.showTickMarks.isBound();
}
@Override
public StyleableProperty<Boolean> getStyleableProperty(Slider n) {
return (StyleableProperty<Boolean>)(WritableValue<Boolean>)n.showTickMarksProperty();
}
};
private static final CssMetaData<Slider,Boolean> SNAP_TO_TICKS =
new CssMetaData<Slider,Boolean>("-fx-snap-to-ticks",
BooleanConverter.getInstance(), Boolean.FALSE) {
@Override
public boolean isSettable(Slider n) {
return n.snapToTicks == null || !n.snapToTicks.isBound();
}
@Override
public StyleableProperty<Boolean> getStyleableProperty(Slider n) {
return (StyleableProperty<Boolean>)(WritableValue<Boolean>)n.snapToTicksProperty();
}
};
private static final CssMetaData<Slider,Number> MAJOR_TICK_UNIT =
new CssMetaData<Slider,Number>("-fx-major-tick-unit",
SizeConverter.getInstance(), 25.0) {
@Override
public boolean isSettable(Slider n) {
return n.majorTickUnit == null || !n.majorTickUnit.isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(Slider n) {
return (StyleableProperty<Number>)(WritableValue<Number>)n.majorTickUnitProperty();
}
};
private static final CssMetaData<Slider,Number> MINOR_TICK_COUNT =
new CssMetaData<Slider,Number>("-fx-minor-tick-count",
SizeConverter.getInstance(), 3.0) {
@Override
public boolean isSettable(Slider n) {
return n.minorTickCount == null || !n.minorTickCount.isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(Slider n) {
return (StyleableProperty<Number>)(WritableValue<Number>)n.minorTickCountProperty();
}
};
private static final CssMetaData<Slider,Orientation> ORIENTATION =
new CssMetaData<Slider,Orientation>("-fx-orientation",
new EnumConverter<Orientation>(Orientation.class),
Orientation.HORIZONTAL) {
@Override
public Orientation getInitialValue(Slider node) {
return node.getOrientation();
}
@Override
public boolean isSettable(Slider n) {
return n.orientation == null || !n.orientation.isBound();
}
@Override
public StyleableProperty<Orientation> getStyleableProperty(Slider n) {
return (StyleableProperty<Orientation>)(WritableValue<Orientation>)n.orientationProperty();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
styleables.add(BLOCK_INCREMENT);
styleables.add(SHOW_TICK_LABELS);
styleables.add(SHOW_TICK_MARKS);
styleables.add(SNAP_TO_TICKS);
styleables.add(MAJOR_TICK_UNIT);
styleables.add(MINOR_TICK_COUNT);
styleables.add(ORIENTATION);
STYLEABLES = Collections.unmodifiableList(styleables);
}
}
public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
return StyleableProperties.STYLEABLES;
}
@Override protected List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
return getClassCssMetaData();
}
private static final PseudoClass VERTICAL_PSEUDOCLASS_STATE =
PseudoClass.getPseudoClass("vertical");
private static final PseudoClass HORIZONTAL_PSEUDOCLASS_STATE =
PseudoClass.getPseudoClass("horizontal");
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
case SET_VALUE: {
Double value = (Double) parameters[0];
if (value != null) setValue(value);
break;
}
default: super.executeAccessibleAction(action, parameters);
}
}
}
