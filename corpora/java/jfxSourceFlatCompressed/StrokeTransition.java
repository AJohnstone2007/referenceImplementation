package javafx.animation;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
public final class StrokeTransition extends Transition {
private Color start;
private Color end;
private ObjectProperty<Shape> shape;
private static final Shape DEFAULT_SHAPE = null;
public final void setShape(Shape value) {
if ((shape != null) || (value != null )) {
shapeProperty().set(value);
}
}
public final Shape getShape() {
return (shape == null)? DEFAULT_SHAPE : shape.get();
}
public final ObjectProperty<Shape> shapeProperty() {
if (shape == null) {
shape = new SimpleObjectProperty<Shape>(this, "shape", DEFAULT_SHAPE);
}
return shape;
}
private Shape cachedShape;
private ObjectProperty<Duration> duration;
private static final Duration DEFAULT_DURATION = Duration.millis(400);
public final void setDuration(Duration value) {
if ((duration != null) || (!DEFAULT_DURATION.equals(value))) {
durationProperty().set(value);
}
}
public final Duration getDuration() {
return (duration == null)? DEFAULT_DURATION : duration.get();
}
public final ObjectProperty<Duration> durationProperty() {
if (duration == null) {
duration = new ObjectPropertyBase<Duration>(DEFAULT_DURATION) {
@Override
public void invalidated() {
try {
setCycleDuration(getDuration());
} catch (IllegalArgumentException e) {
if (isBound()) {
unbind();
}
set(getCycleDuration());
throw e;
}
}
@Override
public Object getBean() {
return StrokeTransition.this;
}
@Override
public String getName() {
return "duration";
}
};
}
return duration;
}
private ObjectProperty<Color> fromValue;
private static final Color DEFAULT_FROM_VALUE = null;
public final void setFromValue(Color value) {
if ((fromValue != null) || (value != null )) {
fromValueProperty().set(value);
}
}
public final Color getFromValue() {
return (fromValue == null) ? DEFAULT_FROM_VALUE : fromValue.get();
}
public final ObjectProperty<Color> fromValueProperty() {
if (fromValue == null) {
fromValue = new SimpleObjectProperty<Color>(this, "fromValue", DEFAULT_FROM_VALUE);
}
return fromValue;
}
private ObjectProperty<Color> toValue;
private static final Color DEFAULT_TO_VALUE = null;
public final void setToValue(Color value) {
if ((toValue != null) || (value != null )) {
toValueProperty().set(value);
}
}
public final Color getToValue() {
return (toValue == null)? DEFAULT_TO_VALUE : toValue.get();
}
public final ObjectProperty<Color> toValueProperty() {
if (toValue == null) {
toValue = new SimpleObjectProperty<Color>(this, "toValue", DEFAULT_TO_VALUE);
}
return toValue;
}
public StrokeTransition(Duration duration, Shape shape, Color fromValue,
Color toValue) {
setDuration(duration);
setShape(shape);
setFromValue(fromValue);
setToValue(toValue);
setCycleDuration(duration);
}
public StrokeTransition(Duration duration, Color fromValue, Color toValue) {
this(duration, null, fromValue, toValue);
}
public StrokeTransition(Duration duration, Shape shape) {
this(duration, shape, null, null);
}
public StrokeTransition(Duration duration) {
this(duration, null);
}
public StrokeTransition() {
this(DEFAULT_DURATION, null);
}
@Override
protected void interpolate(double frac) {
final Color newColor = start.interpolate(end, frac);
cachedShape.setStroke(newColor);
}
private Shape getTargetShape() {
Shape shape = getShape();
if (shape == null) {
final Node node = getParentTargetNode();
if (node instanceof Shape) {
shape = (Shape) node;
}
}
return shape;
}
@Override
boolean startable(boolean forceSync) {
if (!super.startable(forceSync)) {
return false;
}
if (!forceSync && (cachedShape != null)) {
return true;
}
final Shape shape = getTargetShape();
return ((shape != null)
&& ((getFromValue() != null) || (shape.getStroke() instanceof Color))
&& (getToValue() != null));
}
@Override
void sync(boolean forceSync) {
super.sync(forceSync);
if (forceSync || (cachedShape == null)) {
cachedShape = getTargetShape();
final Color _fromValue = getFromValue();
start = (_fromValue != null) ? _fromValue : (Color) cachedShape
.getStroke();
end = getToValue();
}
}
}
