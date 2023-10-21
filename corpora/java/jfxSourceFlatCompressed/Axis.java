package javafx.scene.chart;
import com.sun.javafx.scene.NodeHelper;
import javafx.css.Styleable;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.EnumConverter;
import javafx.css.converter.PaintConverter;
import javafx.css.converter.SizeConverter;
import java.util.*;
import javafx.animation.FadeTransition;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.*;
import javafx.beans.value.WritableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.FontCssMetaData;
import javafx.css.StyleableProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Dimension2D;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.util.Duration;
public abstract class Axis<T> extends Region {
Text measure = new Text();
private Orientation effectiveOrientation;
private double effectiveTickLabelRotation = Double.NaN;
private Label axisLabel = new Label();
private final Path tickMarkPath = new Path();
private double oldLength = 0;
boolean rangeValid = false;
boolean measureInvalid = false;
boolean tickLabelsVisibleInvalid = false;
private BitSet labelsToSkip = new BitSet();
private final ObservableList<TickMark<T>> tickMarks = FXCollections.observableArrayList();
private final ObservableList<TickMark<T>> unmodifiableTickMarks = FXCollections.unmodifiableObservableList(tickMarks);
public ObservableList<TickMark<T>> getTickMarks() { return unmodifiableTickMarks; }
private ObjectProperty<Side> side = new StyleableObjectProperty<Side>(){
@Override protected void invalidated() {
Side edge = get();
pseudoClassStateChanged(TOP_PSEUDOCLASS_STATE, edge == Side.TOP);
pseudoClassStateChanged(RIGHT_PSEUDOCLASS_STATE, edge == Side.RIGHT);
pseudoClassStateChanged(BOTTOM_PSEUDOCLASS_STATE, edge == Side.BOTTOM);
pseudoClassStateChanged(LEFT_PSEUDOCLASS_STATE, edge == Side.LEFT);
requestAxisLayout();
}
@Override
public CssMetaData<Axis<?>,Side> getCssMetaData() {
return StyleableProperties.SIDE;
}
@Override
public Object getBean() {
return Axis.this;
}
@Override
public String getName() {
return "side";
}
};
public final Side getSide() { return side.get(); }
public final void setSide(Side value) { side.set(value); }
public final ObjectProperty<Side> sideProperty() { return side; }
final void setEffectiveOrientation(Orientation orientation) {
effectiveOrientation = orientation;
}
final Side getEffectiveSide() {
final Side side = getSide();
if (side == null || (side.isVertical() && effectiveOrientation == Orientation.HORIZONTAL)
|| side.isHorizontal() && effectiveOrientation == Orientation.VERTICAL) {
return effectiveOrientation == Orientation.VERTICAL ? Side.LEFT : Side.BOTTOM;
}
return side;
}
private ObjectProperty<String> label = new ObjectPropertyBase<String>() {
@Override protected void invalidated() {
axisLabel.setText(get());
requestAxisLayout();
}
@Override
public Object getBean() {
return Axis.this;
}
@Override
public String getName() {
return "label";
}
};
public final String getLabel() { return label.get(); }
public final void setLabel(String value) { label.set(value); }
public final ObjectProperty<String> labelProperty() { return label; }
private BooleanProperty tickMarkVisible = new StyleableBooleanProperty(true) {
@Override protected void invalidated() {
tickMarkPath.setVisible(get());
requestAxisLayout();
}
@Override
public CssMetaData<Axis<?>,Boolean> getCssMetaData() {
return StyleableProperties.TICK_MARK_VISIBLE;
}
@Override
public Object getBean() {
return Axis.this;
}
@Override
public String getName() {
return "tickMarkVisible";
}
};
public final boolean isTickMarkVisible() { return tickMarkVisible.get(); }
public final void setTickMarkVisible(boolean value) { tickMarkVisible.set(value); }
public final BooleanProperty tickMarkVisibleProperty() { return tickMarkVisible; }
private BooleanProperty tickLabelsVisible = new StyleableBooleanProperty(true) {
@Override protected void invalidated() {
for (TickMark<T> tick : tickMarks) {
tick.setTextVisible(get());
}
tickLabelsVisibleInvalid = true;
requestAxisLayout();
}
@Override
public CssMetaData<Axis<?>,Boolean> getCssMetaData() {
return StyleableProperties.TICK_LABELS_VISIBLE;
}
@Override
public Object getBean() {
return Axis.this;
}
@Override
public String getName() {
return "tickLabelsVisible";
}
};
public final boolean isTickLabelsVisible() { return tickLabelsVisible.get(); }
public final void setTickLabelsVisible(boolean value) {
tickLabelsVisible.set(value); }
public final BooleanProperty tickLabelsVisibleProperty() { return tickLabelsVisible; }
private DoubleProperty tickLength = new StyleableDoubleProperty(8) {
@Override protected void invalidated() {
if (tickLength.get() < 0 && !tickLength.isBound()) {
tickLength.set(0);
}
requestAxisLayout();
}
@Override
public CssMetaData<Axis<?>,Number> getCssMetaData() {
return StyleableProperties.TICK_LENGTH;
}
@Override
public Object getBean() {
return Axis.this;
}
@Override
public String getName() {
return "tickLength";
}
};
public final double getTickLength() { return tickLength.get(); }
public final void setTickLength(double value) { tickLength.set(value); }
public final DoubleProperty tickLengthProperty() { return tickLength; }
private BooleanProperty autoRanging = new BooleanPropertyBase(true) {
@Override protected void invalidated() {
if(get()) {
requestAxisLayout();
}
}
@Override
public Object getBean() {
return Axis.this;
}
@Override
public String getName() {
return "autoRanging";
}
};
public final boolean isAutoRanging() { return autoRanging.get(); }
public final void setAutoRanging(boolean value) { autoRanging.set(value); }
public final BooleanProperty autoRangingProperty() { return autoRanging; }
private ObjectProperty<Font> tickLabelFont = new StyleableObjectProperty<Font>(Font.font("System",8)) {
@Override protected void invalidated() {
Font f = get();
measure.setFont(f);
for(TickMark<T> tm : getTickMarks()) {
tm.textNode.setFont(f);
}
measureInvalid = true;
requestAxisLayout();
}
@Override
public CssMetaData<Axis<?>,Font> getCssMetaData() {
return StyleableProperties.TICK_LABEL_FONT;
}
@Override
public Object getBean() {
return Axis.this;
}
@Override
public String getName() {
return "tickLabelFont";
}
};
public final Font getTickLabelFont() { return tickLabelFont.get(); }
public final void setTickLabelFont(Font value) { tickLabelFont.set(value); }
public final ObjectProperty<Font> tickLabelFontProperty() { return tickLabelFont; }
private ObjectProperty<Paint> tickLabelFill = new StyleableObjectProperty<Paint>(Color.BLACK) {
@Override protected void invalidated() {
for (TickMark<T> tick : tickMarks) {
tick.textNode.setFill(getTickLabelFill());
}
}
@Override
public CssMetaData<Axis<?>,Paint> getCssMetaData() {
return StyleableProperties.TICK_LABEL_FILL;
}
@Override
public Object getBean() {
return Axis.this;
}
@Override
public String getName() {
return "tickLabelFill";
}
};
public final Paint getTickLabelFill() { return tickLabelFill.get(); }
public final void setTickLabelFill(Paint value) { tickLabelFill.set(value); }
public final ObjectProperty<Paint> tickLabelFillProperty() { return tickLabelFill; }
private DoubleProperty tickLabelGap = new StyleableDoubleProperty(3) {
@Override protected void invalidated() {
requestAxisLayout();
}
@Override
public CssMetaData<Axis<?>,Number> getCssMetaData() {
return StyleableProperties.TICK_LABEL_TICK_GAP;
}
@Override
public Object getBean() {
return Axis.this;
}
@Override
public String getName() {
return "tickLabelGap";
}
};
public final double getTickLabelGap() { return tickLabelGap.get(); }
public final void setTickLabelGap(double value) { tickLabelGap.set(value); }
public final DoubleProperty tickLabelGapProperty() { return tickLabelGap; }
private BooleanProperty animated = new SimpleBooleanProperty(this, "animated", true);
public final boolean getAnimated() { return animated.get(); }
public final void setAnimated(boolean value) { animated.set(value); }
public final BooleanProperty animatedProperty() { return animated; }
private DoubleProperty tickLabelRotation = new DoublePropertyBase(0) {
@Override protected void invalidated() {
if (isAutoRanging()) {
invalidateRange();
}
requestAxisLayout();
}
@Override
public Object getBean() {
return Axis.this;
}
@Override
public String getName() {
return "tickLabelRotation";
}
};
public final double getTickLabelRotation() { return tickLabelRotation.getValue(); }
public final void setTickLabelRotation(double value) { tickLabelRotation.setValue(value); }
public final DoubleProperty tickLabelRotationProperty() { return tickLabelRotation; }
public Axis() {
getStyleClass().setAll("axis");
axisLabel.getStyleClass().add("axis-label");
axisLabel.setAlignment(Pos.CENTER);
tickMarkPath.getStyleClass().add("axis-tick-mark");
getChildren().addAll(axisLabel, tickMarkPath);
}
protected final boolean isRangeValid() { return rangeValid; }
protected final void invalidateRange() { rangeValid = false; }
protected final boolean shouldAnimate(){
return getAnimated() && NodeHelper.isTreeShowing(this);
}
@Override public void requestLayout() {}
public void requestAxisLayout() {
super.requestLayout();
}
public void invalidateRange(List<T> data) {
invalidateRange();
requestAxisLayout();
}
protected abstract Object autoRange(double length);
protected abstract void setRange(Object range, boolean animate);
protected abstract Object getRange();
public abstract double getZeroPosition();
public abstract double getDisplayPosition(T value);
public abstract T getValueForDisplay(double displayPosition);
public abstract boolean isValueOnAxis(T value);
public abstract double toNumericValue(T value);
public abstract T toRealValue(double value);
protected abstract List<T> calculateTickValues(double length, Object range);
@Override protected double computePrefHeight(double width) {
final Side side = getEffectiveSide();
if (side.isVertical()) {
return 100;
} else {
Object range = autoRange(width);
double maxLabelHeight = 0;
if (isTickLabelsVisible()) {
final List<T> newTickValues = calculateTickValues(width, range);
for (T value: newTickValues) {
maxLabelHeight = Math.max(maxLabelHeight,measureTickMarkSize(value, range).getHeight());
}
}
final double tickMarkLength = isTickMarkVisible() ? (getTickLength() > 0) ? getTickLength() : 0 : 0;
final double labelHeight =
axisLabel.getText() == null || axisLabel.getText().length() == 0 ?
0 : axisLabel.prefHeight(-1);
return maxLabelHeight + getTickLabelGap() + tickMarkLength + labelHeight;
}
}
@Override protected double computePrefWidth(double height) {
final Side side = getEffectiveSide();
if (side.isVertical()) {
Object range = autoRange(height);
double maxLabelWidth = 0;
if (isTickLabelsVisible()) {
final List<T> newTickValues = calculateTickValues(height,range);
for (T value: newTickValues) {
maxLabelWidth = Math.max(maxLabelWidth, measureTickMarkSize(value, range).getWidth());
}
}
final double tickMarkLength = isTickMarkVisible() ? (getTickLength() > 0) ? getTickLength() : 0 : 0;
final double labelHeight =
axisLabel.getText() == null || axisLabel.getText().length() == 0 ?
0 : axisLabel.prefHeight(-1);
return maxLabelWidth + getTickLabelGap() + tickMarkLength + labelHeight;
} else {
return 100;
}
}
protected void tickMarksUpdated(){}
@Override protected void layoutChildren() {
final boolean isFirstPass = oldLength == 0;
final Side side = getEffectiveSide();
final double length = side.isVertical() ? getHeight() : getWidth();
boolean rangeInvalid = !isRangeValid();
boolean lengthDiffers = oldLength != length;
if (lengthDiffers || rangeInvalid) {
Object range;
if(isAutoRanging()) {
range = autoRange(length);
setRange(range, getAnimated() && !isFirstPass && NodeHelper.isTreeShowing(this) && rangeInvalid);
} else {
range = getRange();
}
List<T> newTickValues = calculateTickValues(length, range);
Iterator<TickMark<T>> tickMarkIterator = tickMarks.iterator();
while (tickMarkIterator.hasNext()) {
TickMark<T> tick = tickMarkIterator.next();
final TickMark<T> tm = tick;
if (shouldAnimate()) {
FadeTransition ft = new FadeTransition(Duration.millis(250),tick.textNode);
ft.setToValue(0);
ft.setOnFinished(actionEvent -> {
getChildren().remove(tm.textNode);
});
ft.play();
} else {
getChildren().remove(tm.textNode);
}
tickMarkIterator.remove();
}
for(T newValue: newTickValues) {
final TickMark<T> tick = new TickMark<T>();
tick.setValue(newValue);
tick.textNode.setText(getTickMarkLabel(newValue));
tick.textNode.setFont(getTickLabelFont());
tick.textNode.setFill(getTickLabelFill());
tick.setTextVisible(isTickLabelsVisible());
if (shouldAnimate()) tick.textNode.setOpacity(0);
getChildren().add(tick.textNode);
tickMarks.add(tick);
if (shouldAnimate()) {
FadeTransition ft = new FadeTransition(Duration.millis(750),tick.textNode);
ft.setFromValue(0);
ft.setToValue(1);
ft.play();
}
}
tickMarksUpdated();
oldLength = length;
rangeValid = true;
}
if (lengthDiffers || rangeInvalid || measureInvalid || tickLabelsVisibleInvalid) {
measureInvalid = false;
tickLabelsVisibleInvalid = false;
labelsToSkip.clear();
int numLabelsToSkip = 0;
double totalLabelsSize = 0;
double maxLabelSize = 0;
for (TickMark<T> m : tickMarks) {
m.setPosition(getDisplayPosition(m.getValue()));
if (m.isTextVisible()) {
double tickSize = measureTickMarkSize(m.getValue(), side);
totalLabelsSize += tickSize;
maxLabelSize = Math.round(Math.max(maxLabelSize, tickSize));
}
}
if (maxLabelSize > 0 && length < totalLabelsSize) {
numLabelsToSkip = ((int)(tickMarks.size() * maxLabelSize / length)) + 1;
}
if (numLabelsToSkip > 0) {
int tickIndex = 0;
for (TickMark<T> m : tickMarks) {
if (m.isTextVisible()) {
m.setTextVisible((tickIndex++ % numLabelsToSkip) == 0);
}
}
}
if (tickMarks.size() > 2) {
TickMark<T> m1 = tickMarks.get(0);
TickMark<T> m2 = tickMarks.get(1);
if (isTickLabelsOverlap(side, m1, m2, getTickLabelGap())) {
m2.setTextVisible(false);
}
m1 = tickMarks.get(tickMarks.size()-2);
m2 = tickMarks.get(tickMarks.size()-1);
if (isTickLabelsOverlap(side, m1, m2, getTickLabelGap())) {
m1.setTextVisible(false);
}
}
updateTickMarks(side, length);
}
}
private void updateTickMarks(Side side, double length) {
tickMarkPath.getElements().clear();
final double width = getWidth();
final double height = getHeight();
final double tickMarkLength = (isTickMarkVisible() && getTickLength() > 0) ? getTickLength() : 0;
final double effectiveLabelRotation = getEffectiveTickLabelRotation();
if (Side.LEFT.equals(side)) {
tickMarkPath.setLayoutX(-0.5);
tickMarkPath.setLayoutY(0.5);
if (getLabel() != null) {
axisLabel.getTransforms().setAll(new Translate(0, height), new Rotate(-90, 0, 0));
axisLabel.setLayoutX(0);
axisLabel.setLayoutY(0);
axisLabel.resize(height, Math.ceil(axisLabel.prefHeight(width)));
}
for (TickMark<T> tick : tickMarks) {
positionTextNode(tick.textNode, width - getTickLabelGap() - tickMarkLength,
tick.getPosition(), effectiveLabelRotation, side);
updateTickMark(tick, length,
width - tickMarkLength, tick.getPosition(),
width, tick.getPosition());
}
} else if (Side.RIGHT.equals(side)) {
tickMarkPath.setLayoutX(0.5);
tickMarkPath.setLayoutY(0.5);
if (getLabel() != null) {
final double axisLabelWidth = Math.ceil(axisLabel.prefHeight(width));
axisLabel.getTransforms().setAll(new Translate(0, height), new Rotate(-90, 0, 0));
axisLabel.setLayoutX(width-axisLabelWidth);
axisLabel.setLayoutY(0);
axisLabel.resize(height, axisLabelWidth);
}
for (TickMark<T> tick : tickMarks) {
positionTextNode(tick.textNode, getTickLabelGap() + tickMarkLength,
tick.getPosition(), effectiveLabelRotation, side);
updateTickMark(tick, length,
0, tick.getPosition(),
tickMarkLength, tick.getPosition());
}
} else if (Side.TOP.equals(side)) {
tickMarkPath.setLayoutX(0.5);
tickMarkPath.setLayoutY(-0.5);
if (getLabel() != null) {
axisLabel.getTransforms().clear();
axisLabel.setLayoutX(0);
axisLabel.setLayoutY(0);
axisLabel.resize(width, Math.ceil(axisLabel.prefHeight(width)));
}
for (TickMark<T> tick : tickMarks) {
positionTextNode(tick.textNode, tick.getPosition(), height - tickMarkLength - getTickLabelGap(),
effectiveLabelRotation, side);
updateTickMark(tick, length,
tick.getPosition(), height,
tick.getPosition(), height - tickMarkLength);
}
} else {
tickMarkPath.setLayoutX(0.5);
tickMarkPath.setLayoutY(0.5);
if (getLabel() != null) {
axisLabel.getTransforms().clear();
final double labelHeight = Math.ceil(axisLabel.prefHeight(width));
axisLabel.setLayoutX(0);
axisLabel.setLayoutY(height - labelHeight);
axisLabel.resize(width, labelHeight);
}
for (TickMark<T> tick : tickMarks) {
positionTextNode(tick.textNode, tick.getPosition(), tickMarkLength + getTickLabelGap(),
effectiveLabelRotation, side);
updateTickMark(tick, length,
tick.getPosition(), 0,
tick.getPosition(), tickMarkLength);
}
}
}
private boolean isTickLabelsOverlap(Side side, TickMark<T> m1, TickMark<T> m2, double gap) {
if (!m1.isTextVisible() || !m2.isTextVisible()) return false;
double m1Size = measureTickMarkSize(m1.getValue(), side);
double m2Size = measureTickMarkSize(m2.getValue(), side);
double m1Start = m1.getPosition() - m1Size / 2;
double m1End = m1.getPosition() + m1Size / 2;
double m2Start = m2.getPosition() - m2Size / 2;
double m2End = m2.getPosition() + m2Size / 2;
return side.isVertical() ? (m1Start - m2End) <= gap : (m2Start - m1End) <= gap;
}
private void positionTextNode(Text node, double posX, double posY, double angle, Side side) {
node.setLayoutX(0);
node.setLayoutY(0);
node.setRotate(angle);
final Bounds bounds = node.getBoundsInParent();
if (Side.LEFT.equals(side)) {
node.setLayoutX(posX-bounds.getWidth()-bounds.getMinX());
node.setLayoutY(posY - (bounds.getHeight() / 2d) - bounds.getMinY());
} else if (Side.RIGHT.equals(side)) {
node.setLayoutX(posX-bounds.getMinX());
node.setLayoutY(posY-(bounds.getHeight()/2d)-bounds.getMinY());
} else if (Side.TOP.equals(side)) {
node.setLayoutX(posX-(bounds.getWidth()/2d)-bounds.getMinX());
node.setLayoutY(posY-bounds.getHeight()-bounds.getMinY());
} else {
node.setLayoutX(posX-(bounds.getWidth()/2d)-bounds.getMinX());
node.setLayoutY(posY-bounds.getMinY());
}
}
private void updateTickMark(TickMark<T> tick, double length,
double startX, double startY, double endX, double endY)
{
if (tick.getPosition() >= 0 && tick.getPosition() <= Math.ceil(length)) {
tick.textNode.setVisible(tick.isTextVisible());
tickMarkPath.getElements().addAll(
new MoveTo(startX, startY),
new LineTo(endX, endY)
);
} else {
tick.textNode.setVisible(false);
}
}
protected abstract String getTickMarkLabel(T value);
protected final Dimension2D measureTickMarkLabelSize(String labelText, double rotation) {
measure.setRotate(rotation);
measure.setText(labelText);
Bounds bounds = measure.getBoundsInParent();
return new Dimension2D(bounds.getWidth(), bounds.getHeight());
}
protected final Dimension2D measureTickMarkSize(T value, double rotation) {
return measureTickMarkLabelSize(getTickMarkLabel(value), rotation);
}
protected Dimension2D measureTickMarkSize(T value, Object range) {
return measureTickMarkSize(value, getEffectiveTickLabelRotation());
}
private double measureTickMarkSize(T value, Side side) {
Dimension2D size = measureTickMarkSize(value, getEffectiveTickLabelRotation());
return side.isVertical() ? size.getHeight() : size.getWidth();
}
final double getEffectiveTickLabelRotation() {
return !isAutoRanging() || Double.isNaN(effectiveTickLabelRotation) ? getTickLabelRotation() : effectiveTickLabelRotation;
}
final void setEffectiveTickLabelRotation(double rotation) {
effectiveTickLabelRotation = rotation;
}
public static final class TickMark<T> {
private StringProperty label = new StringPropertyBase() {
@Override protected void invalidated() {
textNode.setText(getValue());
}
@Override
public Object getBean() {
return TickMark.this;
}
@Override
public String getName() {
return "label";
}
};
public final String getLabel() { return label.get(); }
public final void setLabel(String value) { label.set(value); }
public final StringExpression labelProperty() { return label; }
private ObjectProperty<T> value = new SimpleObjectProperty<T>(this, "value");
public final T getValue() { return value.get(); }
public final void setValue(T v) { value.set(v); }
public final ObjectExpression<T> valueProperty() { return value; }
private DoubleProperty position = new SimpleDoubleProperty(this, "position");
public final double getPosition() { return position.get(); }
public final void setPosition(double value) { position.set(value); }
public final DoubleExpression positionProperty() { return position; }
Text textNode = new Text();
private BooleanProperty textVisible = new BooleanPropertyBase(true) {
@Override protected void invalidated() {
if(!get()) {
textNode.setVisible(false);
}
}
@Override
public Object getBean() {
return TickMark.this;
}
@Override
public String getName() {
return "textVisible";
}
};
public final boolean isTextVisible() { return textVisible.get(); }
public final void setTextVisible(boolean value) { textVisible.set(value); }
public TickMark() {
}
@Override public String toString() {
return value.get().toString();
}
}
private static class StyleableProperties {
private static final CssMetaData<Axis<?>,Side> SIDE =
new CssMetaData<Axis<?>,Side>("-fx-side",
new EnumConverter<Side>(Side.class)) {
@Override
public boolean isSettable(Axis<?> n) {
return n.side == null || !n.side.isBound();
}
@SuppressWarnings("unchecked")
@Override
public StyleableProperty<Side> getStyleableProperty(Axis<?> n) {
return (StyleableProperty<Side>)n.sideProperty();
}
};
private static final CssMetaData<Axis<?>,Number> TICK_LENGTH =
new CssMetaData<Axis<?>,Number>("-fx-tick-length",
SizeConverter.getInstance(), 8.0) {
@Override
public boolean isSettable(Axis<?> n) {
return n.tickLength == null || !n.tickLength.isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(Axis<?> n) {
return (StyleableProperty<Number>)(WritableValue<Number>)n.tickLengthProperty();
}
};
private static final CssMetaData<Axis<?>,Font> TICK_LABEL_FONT =
new FontCssMetaData<Axis<?>>("-fx-tick-label-font",
Font.font("system", 8.0)) {
@Override
public boolean isSettable(Axis<?> n) {
return n.tickLabelFont == null || !n.tickLabelFont.isBound();
}
@SuppressWarnings("unchecked")
@Override
public StyleableProperty<Font> getStyleableProperty(Axis<?> n) {
return (StyleableProperty<Font>)n.tickLabelFontProperty();
}
};
private static final CssMetaData<Axis<?>,Paint> TICK_LABEL_FILL =
new CssMetaData<Axis<?>,Paint>("-fx-tick-label-fill",
PaintConverter.getInstance(), Color.BLACK) {
@Override
public boolean isSettable(Axis<?> n) {
return n.tickLabelFill == null | !n.tickLabelFill.isBound();
}
@SuppressWarnings("unchecked")
@Override
public StyleableProperty<Paint> getStyleableProperty(Axis<?> n) {
return (StyleableProperty<Paint>)n.tickLabelFillProperty();
}
};
private static final CssMetaData<Axis<?>,Number> TICK_LABEL_TICK_GAP =
new CssMetaData<Axis<?>,Number>("-fx-tick-label-gap",
SizeConverter.getInstance(), 3.0) {
@Override
public boolean isSettable(Axis<?> n) {
return n.tickLabelGap == null || !n.tickLabelGap.isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(Axis<?> n) {
return (StyleableProperty<Number>)(WritableValue<Number>)n.tickLabelGapProperty();
}
};
private static final CssMetaData<Axis<?>,Boolean> TICK_MARK_VISIBLE =
new CssMetaData<Axis<?>,Boolean>("-fx-tick-mark-visible",
BooleanConverter.getInstance(), Boolean.TRUE) {
@Override
public boolean isSettable(Axis<?> n) {
return n.tickMarkVisible == null || !n.tickMarkVisible.isBound();
}
@Override
public StyleableProperty<Boolean> getStyleableProperty(Axis<?> n) {
return (StyleableProperty<Boolean>)(WritableValue<Boolean>)n.tickMarkVisibleProperty();
}
};
private static final CssMetaData<Axis<?>,Boolean> TICK_LABELS_VISIBLE =
new CssMetaData<Axis<?>,Boolean>("-fx-tick-labels-visible",
BooleanConverter.getInstance(), Boolean.TRUE) {
@Override
public boolean isSettable(Axis<?> n) {
return n.tickLabelsVisible == null || !n.tickLabelsVisible.isBound();
}
@Override
public StyleableProperty<Boolean> getStyleableProperty(Axis<?> n) {
return (StyleableProperty<Boolean>)(WritableValue<Boolean>)n.tickLabelsVisibleProperty();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(Region.getClassCssMetaData());
styleables.add(SIDE);
styleables.add(TICK_LENGTH);
styleables.add(TICK_LABEL_FONT);
styleables.add(TICK_LABEL_FILL);
styleables.add(TICK_LABEL_TICK_GAP);
styleables.add(TICK_MARK_VISIBLE);
styleables.add(TICK_LABELS_VISIBLE);
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
private static final PseudoClass TOP_PSEUDOCLASS_STATE =
PseudoClass.getPseudoClass("top");
private static final PseudoClass BOTTOM_PSEUDOCLASS_STATE =
PseudoClass.getPseudoClass("bottom");
private static final PseudoClass LEFT_PSEUDOCLASS_STATE =
PseudoClass.getPseudoClass("left");
private static final PseudoClass RIGHT_PSEUDOCLASS_STATE =
PseudoClass.getPseudoClass("right");
}
