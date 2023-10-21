package javafx.scene.chart;
import javafx.css.CssMetaData;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableIntegerProperty;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.SizeConverter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.property.*;
import javafx.beans.value.WritableValue;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.geometry.Side;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.StringConverter;
public abstract class ValueAxis<T extends Number> extends Axis<T> {
private final Path minorTickPath = new Path();
private double offset;
double dataMinValue;
double dataMaxValue;
private List<T> minorTickMarkValues = null;
private boolean minorTickMarksDirty = true;
protected final DoubleProperty currentLowerBound = new SimpleDoubleProperty(this, "currentLowerBound");
private BooleanProperty minorTickVisible = new StyleableBooleanProperty(true) {
@Override protected void invalidated() {
minorTickPath.setVisible(get());
requestAxisLayout();
}
@Override
public Object getBean() {
return ValueAxis.this;
}
@Override
public String getName() {
return "minorTickVisible";
}
@Override
public CssMetaData<ValueAxis<? extends Number>,Boolean> getCssMetaData() {
return StyleableProperties.MINOR_TICK_VISIBLE;
}
};
public final boolean isMinorTickVisible() { return minorTickVisible.get(); }
public final void setMinorTickVisible(boolean value) { minorTickVisible.set(value); }
public final BooleanProperty minorTickVisibleProperty() { return minorTickVisible; }
private ReadOnlyDoubleWrapper scale = new ReadOnlyDoubleWrapper(this, "scale", 0) {
@Override
protected void invalidated() {
requestAxisLayout();
measureInvalid = true;
}
};
public final double getScale() { return scale.get(); }
protected final void setScale(double scale) { this.scale.set(scale); }
public final ReadOnlyDoubleProperty scaleProperty() { return scale.getReadOnlyProperty(); }
ReadOnlyDoubleWrapper scalePropertyImpl() { return scale; }
private DoubleProperty upperBound = new DoublePropertyBase(100) {
@Override protected void invalidated() {
if(!isAutoRanging()) {
invalidateRange();
requestAxisLayout();
}
}
@Override
public Object getBean() {
return ValueAxis.this;
}
@Override
public String getName() {
return "upperBound";
}
};
public final double getUpperBound() { return upperBound.get(); }
public final void setUpperBound(double value) { upperBound.set(value); }
public final DoubleProperty upperBoundProperty() { return upperBound; }
private DoubleProperty lowerBound = new DoublePropertyBase(0) {
@Override protected void invalidated() {
if(!isAutoRanging()) {
invalidateRange();
requestAxisLayout();
}
}
@Override
public Object getBean() {
return ValueAxis.this;
}
@Override
public String getName() {
return "lowerBound";
}
};
public final double getLowerBound() { return lowerBound.get(); }
public final void setLowerBound(double value) { lowerBound.set(value); }
public final DoubleProperty lowerBoundProperty() { return lowerBound; }
private final ObjectProperty<StringConverter<T>> tickLabelFormatter = new ObjectPropertyBase<StringConverter<T>>(null){
@Override protected void invalidated() {
invalidateRange();
requestAxisLayout();
}
@Override
public Object getBean() {
return ValueAxis.this;
}
@Override
public String getName() {
return "tickLabelFormatter";
}
};
public final StringConverter<T> getTickLabelFormatter() { return tickLabelFormatter.getValue(); }
public final void setTickLabelFormatter(StringConverter<T> value) { tickLabelFormatter.setValue(value); }
public final ObjectProperty<StringConverter<T>> tickLabelFormatterProperty() { return tickLabelFormatter; }
private DoubleProperty minorTickLength = new StyleableDoubleProperty(5) {
@Override protected void invalidated() {
requestAxisLayout();
}
@Override
public Object getBean() {
return ValueAxis.this;
}
@Override
public String getName() {
return "minorTickLength";
}
@Override
public CssMetaData<ValueAxis<? extends Number>,Number> getCssMetaData() {
return StyleableProperties.MINOR_TICK_LENGTH;
}
};
public final double getMinorTickLength() { return minorTickLength.get(); }
public final void setMinorTickLength(double value) { minorTickLength.set(value); }
public final DoubleProperty minorTickLengthProperty() { return minorTickLength; }
private IntegerProperty minorTickCount = new StyleableIntegerProperty(5) {
@Override protected void invalidated() {
invalidateRange();
requestAxisLayout();
}
@Override
public Object getBean() {
return ValueAxis.this;
}
@Override
public String getName() {
return "minorTickCount";
}
@Override
public CssMetaData<ValueAxis<? extends Number>,Number> getCssMetaData() {
return StyleableProperties.MINOR_TICK_COUNT;
}
};
public final int getMinorTickCount() { return minorTickCount.get(); }
public final void setMinorTickCount(int value) { minorTickCount.set(value); }
public final IntegerProperty minorTickCountProperty() { return minorTickCount; }
public ValueAxis() {
minorTickPath.getStyleClass().add("axis-minor-tick-mark");
getChildren().add(minorTickPath);
}
public ValueAxis(double lowerBound, double upperBound) {
this();
setAutoRanging(false);
setLowerBound(lowerBound);
setUpperBound(upperBound);
}
@Override protected final Object autoRange(double length) {
if (isAutoRanging()) {
double labelSize = getTickLabelFont().getSize() * 2;
return autoRange(dataMinValue,dataMaxValue,length,labelSize);
} else {
return getRange();
}
}
protected final double calculateNewScale(double length, double lowerBound, double upperBound) {
double newScale = 1;
final Side side = getEffectiveSide();
if (side.isVertical()) {
offset = length;
newScale = ((upperBound-lowerBound) == 0) ? -length : -(length / (upperBound - lowerBound));
} else {
offset = 0;
newScale = ((upperBound-lowerBound) == 0) ? length : length / (upperBound - lowerBound);
}
return newScale;
}
protected Object autoRange(double minValue, double maxValue, double length, double labelSize) {
return null;
}
protected abstract List<T> calculateMinorTickMarks();
@Override protected void tickMarksUpdated() {
super.tickMarksUpdated();
minorTickMarkValues = calculateMinorTickMarks();
minorTickMarksDirty = true;
}
@Override protected void layoutChildren() {
final Side side = getEffectiveSide();
final double length = side.isVertical() ? getHeight() :getWidth() ;
if(!isAutoRanging()) {
setScale(calculateNewScale(length, getLowerBound(), getUpperBound()));
currentLowerBound.set(getLowerBound());
}
super.layoutChildren();
if (minorTickMarksDirty) {
minorTickMarksDirty = false;
updateMinorTickPath(side, length);
}
}
private void updateMinorTickPath(Side side, double length) {
int numMinorTicks = (getTickMarks().size() - 1)*(Math.max(1, getMinorTickCount()) - 1);
double neededLength = (getTickMarks().size()+numMinorTicks)*2;
minorTickPath.getElements().clear();
double minorTickLength = Math.max(0, getMinorTickLength());
if (minorTickLength > 0 && length > neededLength) {
if (Side.LEFT.equals(side)) {
minorTickPath.setLayoutX(-0.5);
minorTickPath.setLayoutY(0.5);
for (T value : minorTickMarkValues) {
double y = getDisplayPosition(value);
if (y >= 0 && y <= length) {
minorTickPath.getElements().addAll(
new MoveTo(getWidth() - minorTickLength, y),
new LineTo(getWidth() - 1, y));
}
}
} else if (Side.RIGHT.equals(side)) {
minorTickPath.setLayoutX(0.5);
minorTickPath.setLayoutY(0.5);
for (T value : minorTickMarkValues) {
double y = getDisplayPosition(value);
if (y >= 0 && y <= length) {
minorTickPath.getElements().addAll(
new MoveTo(1, y),
new LineTo(minorTickLength, y));
}
}
} else if (Side.TOP.equals(side)) {
minorTickPath.setLayoutX(0.5);
minorTickPath.setLayoutY(-0.5);
for (T value : minorTickMarkValues) {
double x = getDisplayPosition(value);
if (x >= 0 && x <= length) {
minorTickPath.getElements().addAll(
new MoveTo(x, getHeight() - 1),
new LineTo(x, getHeight() - minorTickLength));
}
}
} else {
minorTickPath.setLayoutX(0.5);
minorTickPath.setLayoutY(0.5);
for (T value : minorTickMarkValues) {
double x = getDisplayPosition(value);
if (x >= 0 && x <= length) {
minorTickPath.getElements().addAll(
new MoveTo(x, 1.0F),
new LineTo(x, minorTickLength));
}
}
}
}
}
@Override public void invalidateRange(List<T> data) {
if (data.isEmpty()) {
dataMaxValue = getUpperBound();
dataMinValue = getLowerBound();
} else {
dataMinValue = Double.MAX_VALUE;
dataMaxValue = -Double.MAX_VALUE;
}
for(T dataValue: data) {
dataMinValue = Math.min(dataMinValue, dataValue.doubleValue());
dataMaxValue = Math.max(dataMaxValue, dataValue.doubleValue());
}
super.invalidateRange(data);
}
@Override public double getDisplayPosition(T value) {
return offset + ((value.doubleValue() - currentLowerBound.get()) * getScale());
}
@Override public T getValueForDisplay(double displayPosition) {
return toRealValue(((displayPosition-offset) / getScale()) + currentLowerBound.get());
}
@Override public double getZeroPosition() {
if (0 < getLowerBound() || 0 > getUpperBound()) return Double.NaN;
return getDisplayPosition((T)Double.valueOf(0));
}
@Override public boolean isValueOnAxis(T value) {
final double num = value.doubleValue();
return num >= getLowerBound() && num <= getUpperBound();
}
@Override public double toNumericValue(T value) {
return (value == null) ? Double.NaN : value.doubleValue();
}
@Override public T toRealValue(double value) {
return (T)Double.valueOf(value);
}
private static class StyleableProperties {
private static final CssMetaData<ValueAxis<? extends Number>,Number> MINOR_TICK_LENGTH =
new CssMetaData<ValueAxis<? extends Number>,Number>("-fx-minor-tick-length",
SizeConverter.getInstance(), 5.0) {
@Override
public boolean isSettable(ValueAxis<? extends Number> n) {
return n.minorTickLength == null || !n.minorTickLength.isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(ValueAxis<? extends Number> n) {
return (StyleableProperty<Number>)(WritableValue<Number>)n.minorTickLengthProperty();
}
};
private static final CssMetaData<ValueAxis<? extends Number>,Number> MINOR_TICK_COUNT =
new CssMetaData<ValueAxis<? extends Number>,Number>("-fx-minor-tick-count",
SizeConverter.getInstance(), 5) {
@Override
public boolean isSettable(ValueAxis<? extends Number> n) {
return n.minorTickCount == null || !n.minorTickCount.isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(ValueAxis<? extends Number> n) {
return (StyleableProperty<Number>)(WritableValue<Number>)n.minorTickCountProperty();
}
};
private static final CssMetaData<ValueAxis<? extends Number>,Boolean> MINOR_TICK_VISIBLE =
new CssMetaData<ValueAxis<? extends Number>,Boolean>("-fx-minor-tick-visible",
BooleanConverter.getInstance(), Boolean.TRUE) {
@Override
public boolean isSettable(ValueAxis<? extends Number> n) {
return n.minorTickVisible == null || !n.minorTickVisible.isBound();
}
@Override
public StyleableProperty<Boolean> getStyleableProperty(ValueAxis<? extends Number> n) {
return (StyleableProperty<Boolean>)(WritableValue<Boolean>)n.minorTickVisibleProperty();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(Axis.getClassCssMetaData());
styleables.add(MINOR_TICK_COUNT);
styleables.add(MINOR_TICK_LENGTH);
styleables.add(MINOR_TICK_COUNT);
styleables.add(MINOR_TICK_VISIBLE);
STYLEABLES = Collections.unmodifiableList(styleables);
}
}
public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
return StyleableProperties.STYLEABLES;
}
@Override
public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
return getClassCssMetaData();
}
}
