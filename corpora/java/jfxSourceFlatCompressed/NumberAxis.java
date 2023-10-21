package javafx.scene.chart;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WritableValue;
import javafx.geometry.Dimension2D;
import javafx.geometry.Side;
import javafx.util.Duration;
import javafx.util.StringConverter;
import com.sun.javafx.charts.ChartLayoutAnimator;
import javafx.css.StyleableDoubleProperty;
import javafx.css.CssMetaData;
import javafx.css.converter.SizeConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
public final class NumberAxis extends ValueAxis<Number> {
private Object currentAnimationID;
private final ChartLayoutAnimator animator = new ChartLayoutAnimator(this);
private final StringProperty currentFormatterProperty = new SimpleStringProperty(this, "currentFormatter", "");
private final DefaultFormatter defaultFormatter = new DefaultFormatter(this);
private BooleanProperty forceZeroInRange = new BooleanPropertyBase(true) {
@Override protected void invalidated() {
if(isAutoRanging()) {
requestAxisLayout();
invalidateRange();
}
}
@Override
public Object getBean() {
return NumberAxis.this;
}
@Override
public String getName() {
return "forceZeroInRange";
}
};
public final boolean isForceZeroInRange() { return forceZeroInRange.getValue(); }
public final void setForceZeroInRange(boolean value) { forceZeroInRange.setValue(value); }
public final BooleanProperty forceZeroInRangeProperty() { return forceZeroInRange; }
private DoubleProperty tickUnit = new StyleableDoubleProperty(5) {
@Override protected void invalidated() {
if(!isAutoRanging()) {
invalidateRange();
requestAxisLayout();
}
}
@Override
public CssMetaData<NumberAxis,Number> getCssMetaData() {
return StyleableProperties.TICK_UNIT;
}
@Override
public Object getBean() {
return NumberAxis.this;
}
@Override
public String getName() {
return "tickUnit";
}
};
public final double getTickUnit() { return tickUnit.get(); }
public final void setTickUnit(double value) { tickUnit.set(value); }
public final DoubleProperty tickUnitProperty() { return tickUnit; }
public NumberAxis() {}
public NumberAxis(double lowerBound, double upperBound, double tickUnit) {
super(lowerBound, upperBound);
setTickUnit(tickUnit);
}
public NumberAxis(String axisLabel, double lowerBound, double upperBound, double tickUnit) {
super(lowerBound, upperBound);
setTickUnit(tickUnit);
setLabel(axisLabel);
}
@Override protected String getTickMarkLabel(Number value) {
StringConverter<Number> formatter = getTickLabelFormatter();
if (formatter == null) formatter = defaultFormatter;
return formatter.toString(value);
}
@Override protected Object getRange() {
return new Object[]{
getLowerBound(),
getUpperBound(),
getTickUnit(),
getScale(),
currentFormatterProperty.get()
};
}
@Override protected void setRange(Object range, boolean animate) {
final Object[] rangeProps = (Object[]) range;
final double lowerBound = (Double)rangeProps[0];
final double upperBound = (Double)rangeProps[1];
final double tickUnit = (Double)rangeProps[2];
final double scale = (Double)rangeProps[3];
final String formatter = (String)rangeProps[4];
currentFormatterProperty.set(formatter);
final double oldLowerBound = getLowerBound();
setLowerBound(lowerBound);
setUpperBound(upperBound);
setTickUnit(tickUnit);
if(animate) {
animator.stop(currentAnimationID);
currentAnimationID = animator.animate(
new KeyFrame(Duration.ZERO,
new KeyValue(currentLowerBound, oldLowerBound),
new KeyValue(scalePropertyImpl(), getScale())
),
new KeyFrame(Duration.millis(700),
new KeyValue(currentLowerBound, lowerBound),
new KeyValue(scalePropertyImpl(), scale)
)
);
} else {
currentLowerBound.set(lowerBound);
setScale(scale);
}
}
@Override protected List<Number> calculateTickValues(double length, Object range) {
final Object[] rangeProps = (Object[]) range;
final double lowerBound = (Double)rangeProps[0];
final double upperBound = (Double)rangeProps[1];
final double tickUnit = (Double)rangeProps[2];
List<Number> tickValues = new ArrayList<>();
if (lowerBound == upperBound) {
tickValues.add(lowerBound);
} else if (tickUnit <= 0) {
tickValues.add(lowerBound);
tickValues.add(upperBound);
} else if (tickUnit > 0) {
tickValues.add(lowerBound);
if (((upperBound - lowerBound) / tickUnit) > 2000) {
System.err.println("Warning we tried to create more than 2000 major tick marks on a NumberAxis. " +
"Lower Bound=" + lowerBound + ", Upper Bound=" + upperBound + ", Tick Unit=" + tickUnit);
} else {
if (lowerBound + tickUnit < upperBound) {
double major = Math.rint(tickUnit) == tickUnit ? Math.ceil(lowerBound) : lowerBound + tickUnit;
int count = (int)Math.ceil((upperBound - major)/tickUnit);
for (int i = 0; major < upperBound && i < count; major += tickUnit, i++) {
if (!tickValues.contains(major)) {
tickValues.add(major);
}
}
}
}
tickValues.add(upperBound);
}
return tickValues;
}
protected List<Number> calculateMinorTickMarks() {
final List<Number> minorTickMarks = new ArrayList<>();
final double lowerBound = getLowerBound();
final double upperBound = getUpperBound();
final double tickUnit = getTickUnit();
final double minorUnit = tickUnit/Math.max(1, getMinorTickCount());
if (tickUnit > 0) {
if(((upperBound - lowerBound) / minorUnit) > 10000) {
System.err.println("Warning we tried to create more than 10000 minor tick marks on a NumberAxis. " +
"Lower Bound=" + getLowerBound() + ", Upper Bound=" + getUpperBound() + ", Tick Unit=" + tickUnit);
return minorTickMarks;
}
final boolean tickUnitIsInteger = Math.rint(tickUnit) == tickUnit;
if (tickUnitIsInteger) {
double minor = Math.floor(lowerBound) + minorUnit;
int count = (int)Math.ceil((Math.ceil(lowerBound) - minor)/minorUnit);
for (int i = 0; minor < Math.ceil(lowerBound) && i < count; minor += minorUnit, i++) {
if (minor > lowerBound) {
minorTickMarks.add(minor);
}
}
}
double major = tickUnitIsInteger ? Math.ceil(lowerBound) : lowerBound;
int count = (int)Math.ceil((upperBound - major)/tickUnit);
for (int i = 0; major < upperBound && i < count; major += tickUnit, i++) {
final double next = Math.min(major + tickUnit, upperBound);
double minor = major + minorUnit;
int minorCount = (int)Math.ceil((next - minor)/minorUnit);
for (int j = 0; minor < next && j < minorCount; minor += minorUnit, j++) {
minorTickMarks.add(minor);
}
}
}
return minorTickMarks;
}
@Override protected Dimension2D measureTickMarkSize(Number value, Object range) {
final Object[] rangeProps = (Object[]) range;
final String formatter = (String)rangeProps[4];
return measureTickMarkSize(value, getTickLabelRotation(), formatter);
}
private Dimension2D measureTickMarkSize(Number value, double rotation, String numFormatter) {
String labelText;
StringConverter<Number> formatter = getTickLabelFormatter();
if (formatter == null) formatter = defaultFormatter;
if(formatter instanceof DefaultFormatter) {
labelText = ((DefaultFormatter)formatter).toString(value, numFormatter);
} else {
labelText = formatter.toString(value);
}
return measureTickMarkLabelSize(labelText, rotation);
}
@Override protected Object autoRange(double minValue, double maxValue, double length, double labelSize) {
final Side side = getEffectiveSide();
if (isForceZeroInRange()) {
if (maxValue < 0) {
maxValue = 0;
} else if (minValue > 0) {
minValue = 0;
}
}
int numOfTickMarks = (int)Math.floor(length/labelSize);
numOfTickMarks = Math.max(numOfTickMarks, 2);
int minorTickCount = Math.max(getMinorTickCount(), 1);
double range = maxValue-minValue;
if (range != 0 && range/(numOfTickMarks*minorTickCount) <= Math.ulp(minValue)) {
range = 0;
}
final double paddedRange = (range == 0)
? minValue == 0 ? 2 : Math.abs(minValue)*0.02
: Math.abs(range)*1.02;
final double padding = (paddedRange - range) / 2;
double paddedMin = minValue - padding;
double paddedMax = maxValue + padding;
if ((paddedMin < 0 && minValue >= 0) || (paddedMin > 0 && minValue <= 0)) {
paddedMin = 0;
}
if ((paddedMax < 0 && maxValue >= 0) || (paddedMax > 0 && maxValue <= 0)) {
paddedMax = 0;
}
double tickUnit = paddedRange/(double)numOfTickMarks;
double tickUnitRounded = 0;
double minRounded = 0;
double maxRounded = 0;
int count = 0;
double reqLength = Double.MAX_VALUE;
String formatter = "0.00000000";
while (reqLength > length || count > 20) {
int exp = (int)Math.floor(Math.log10(tickUnit));
final double mant = tickUnit / Math.pow(10, exp);
double ratio = mant;
if (mant > 5d) {
exp++;
ratio = 1;
} else if (mant > 1d) {
ratio = mant > 2.5 ? 5 : 2.5;
}
if (exp > 1) {
formatter = "#,##0";
} else if (exp == 1) {
formatter = "0";
} else {
final boolean ratioHasFrac = Math.rint(ratio) != ratio;
final StringBuilder formatterB = new StringBuilder("0");
int n = ratioHasFrac ? Math.abs(exp) + 1 : Math.abs(exp);
if (n > 0) formatterB.append(".");
for (int i = 0; i < n; ++i) {
formatterB.append("0");
}
formatter = formatterB.toString();
}
tickUnitRounded = ratio * Math.pow(10, exp);
minRounded = Math.floor(paddedMin / tickUnitRounded) * tickUnitRounded;
maxRounded = Math.ceil(paddedMax / tickUnitRounded) * tickUnitRounded;
double maxReqTickGap = 0;
double last = 0;
count = (int)Math.ceil((maxRounded - minRounded)/tickUnitRounded);
double major = minRounded;
for (int i = 0; major <= maxRounded && i < count; major += tickUnitRounded, i++) {
Dimension2D markSize = measureTickMarkSize(major, getTickLabelRotation(), formatter);
double size = side.isVertical() ? markSize.getHeight() : markSize.getWidth();
if (i == 0) {
last = size/2;
} else {
maxReqTickGap = Math.max(maxReqTickGap, last + 6 + (size/2) );
}
}
reqLength = (count-1) * maxReqTickGap;
tickUnit = tickUnitRounded;
if (numOfTickMarks == 2 && reqLength > length) {
break;
}
if (reqLength > length || count > 20) tickUnit *= 2;
}
final double newScale = calculateNewScale(length, minRounded, maxRounded);
return new Object[]{minRounded, maxRounded, tickUnitRounded, newScale, formatter};
}
private static class StyleableProperties {
private static final CssMetaData<NumberAxis,Number> TICK_UNIT =
new CssMetaData<NumberAxis,Number>("-fx-tick-unit",
SizeConverter.getInstance(), 5.0) {
@Override
public boolean isSettable(NumberAxis n) {
return n.tickUnit == null || !n.tickUnit.isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(NumberAxis n) {
return (StyleableProperty<Number>)(WritableValue<Number>)n.tickUnitProperty();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(ValueAxis.getClassCssMetaData());
styleables.add(TICK_UNIT);
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
public static class DefaultFormatter extends StringConverter<Number> {
private DecimalFormat formatter;
private String prefix = null;
private String suffix = null;
public DefaultFormatter(final NumberAxis axis) {
formatter = axis.isAutoRanging()? new DecimalFormat(axis.currentFormatterProperty.get()) : new DecimalFormat();
final ChangeListener<Object> axisListener = (observable, oldValue, newValue) -> {
formatter = axis.isAutoRanging()? new DecimalFormat(axis.currentFormatterProperty.get()) : new DecimalFormat();
};
axis.currentFormatterProperty.addListener(axisListener);
axis.autoRangingProperty().addListener(axisListener);
}
public DefaultFormatter(NumberAxis axis, String prefix, String suffix) {
this(axis);
this.prefix = prefix;
this.suffix = suffix;
}
@Override public String toString(Number object) {
return toString(object, formatter);
}
private String toString(Number object, String numFormatter) {
if (numFormatter == null || numFormatter.isEmpty()) {
return toString(object, formatter);
} else {
return toString(object, new DecimalFormat(numFormatter));
}
}
private String toString(Number object, DecimalFormat formatter) {
if (prefix != null && suffix != null) {
return prefix + formatter.format(object) + suffix;
} else if (prefix != null) {
return prefix + formatter.format(object);
} else if (suffix != null) {
return formatter.format(object) + suffix;
} else {
return formatter.format(object);
}
}
@Override public Number fromString(String string) {
try {
int prefixLength = (prefix == null)? 0: prefix.length();
int suffixLength = (suffix == null)? 0: suffix.length();
return formatter.parse(string.substring(prefixLength, string.length() - suffixLength));
} catch (ParseException e) {
return null;
}
}
}
}
