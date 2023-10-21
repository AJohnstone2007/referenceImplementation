package javafx.animation;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.util.Duration;
public final class FadeTransition extends Transition {
private static final double EPSILON = 1e-12;
private double start;
private double delta;
private ObjectProperty<Node> node;
private static final Node DEFAULT_NODE = null;
public final void setNode(Node value) {
if ((node != null) || (value != null )) {
nodeProperty().set(value);
}
}
public final Node getNode() {
return (node == null)? DEFAULT_NODE : node.get();
}
public final ObjectProperty<Node> nodeProperty() {
if (node == null) {
node = new SimpleObjectProperty<Node>(this, "node", DEFAULT_NODE);
}
return node;
}
private Node cachedNode;
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
return FadeTransition.this;
}
@Override
public String getName() {
return "duration";
}
};
}
return duration;
}
private DoubleProperty fromValue;
private static final double DEFAULT_FROM_VALUE = Double.NaN;
public final void setFromValue(double value) {
if ((fromValue != null) || (!Double.isNaN(value) )) {
fromValueProperty().set(value);
}
}
public final double getFromValue() {
return (fromValue == null)? DEFAULT_FROM_VALUE : fromValue.get();
}
public final DoubleProperty fromValueProperty() {
if (fromValue == null) {
fromValue = new SimpleDoubleProperty(this, "fromValue", DEFAULT_FROM_VALUE);
}
return fromValue;
}
private DoubleProperty toValue;
private static final double DEFAULT_TO_VALUE = Double.NaN;
public final void setToValue(double value) {
if ((toValue != null) || (!Double.isNaN(value))) {
toValueProperty().set(value);
}
}
public final double getToValue() {
return (toValue == null)? DEFAULT_TO_VALUE : toValue.get();
}
public final DoubleProperty toValueProperty() {
if (toValue == null) {
toValue = new SimpleDoubleProperty(this, "toValue", DEFAULT_TO_VALUE);
}
return toValue;
}
private DoubleProperty byValue;
private static final double DEFAULT_BY_VALUE = 0.0;
public final void setByValue(double value) {
if ((byValue != null) || (Math.abs(value - DEFAULT_BY_VALUE) > EPSILON)) {
byValueProperty().set(value);
}
}
public final double getByValue() {
return (byValue == null)? DEFAULT_BY_VALUE : byValue.get();
}
public final DoubleProperty byValueProperty() {
if (byValue == null) {
byValue = new SimpleDoubleProperty(this, "byValue", DEFAULT_BY_VALUE);
}
return byValue;
}
public FadeTransition(Duration duration, Node node) {
setDuration(duration);
setNode(node);
setCycleDuration(duration);
}
public FadeTransition(Duration duration) {
this(duration, null);
}
public FadeTransition() {
this(DEFAULT_DURATION, null);
}
@Override
protected void interpolate(double frac) {
final double newOpacity = Math.max(0.0,
Math.min(start + frac * delta, 1.0));
cachedNode.setOpacity(newOpacity);
}
private Node getTargetNode() {
final Node node = getNode();
return (node != null) ? node : getParentTargetNode();
}
@Override
boolean startable(boolean forceSync) {
return super.startable(forceSync)
&& ((getTargetNode() != null) || (!forceSync && (cachedNode != null)));
}
@Override
void sync(boolean forceSync) {
super.sync(forceSync);
if (forceSync || (cachedNode == null)) {
cachedNode = getTargetNode();
final double _fromValue = getFromValue();
final double _toValue = getToValue();
start = (!Double.isNaN(_fromValue)) ? Math.max(0,
Math.min(_fromValue, 1)) : cachedNode.getOpacity();
delta = (!Double.isNaN(_toValue)) ? _toValue - start : getByValue();
if (start + delta > 1.0) {
delta = 1.0 - start;
} else if (start + delta < 0.0) {
delta = -start;
}
}
}
}
