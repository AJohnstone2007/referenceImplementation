package javafx.animation;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.util.Duration;
public final class RotateTransition extends Transition {
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
return RotateTransition.this;
}
@Override
public String getName() {
return "duration";
}
};
}
return duration;
}
private ObjectProperty<Point3D> axis;
private static final Point3D DEFAULT_AXIS = null;
public final void setAxis(Point3D value) {
if ((axis != null) || (value != null )) {
axisProperty().set(value);
}
}
public final Point3D getAxis() {
return (axis == null)? DEFAULT_AXIS : axis.get();
}
public final ObjectProperty<Point3D> axisProperty() {
if (axis == null) {
axis = new SimpleObjectProperty<Point3D>(this, "axis", DEFAULT_AXIS);
}
return axis;
}
private DoubleProperty fromAngle;
private static final double DEFAULT_FROM_ANGLE = Double.NaN;
public final void setFromAngle(double value) {
if ((fromAngle != null) || (!Double.isNaN(value))) {
fromAngleProperty().set(value);
}
}
public final double getFromAngle() {
return (fromAngle == null)? DEFAULT_FROM_ANGLE : fromAngle.get();
}
public final DoubleProperty fromAngleProperty() {
if (fromAngle == null) {
fromAngle = new SimpleDoubleProperty(this, "fromAngle", DEFAULT_FROM_ANGLE);
}
return fromAngle;
}
private DoubleProperty toAngle;
private static final double DEFAULT_TO_ANGLE = Double.NaN;
public final void setToAngle(double value) {
if ((toAngle != null) || (!Double.isNaN(value))) {
toAngleProperty().set(value);
}
}
public final double getToAngle() {
return (toAngle == null)? DEFAULT_TO_ANGLE : toAngle.get();
}
public final DoubleProperty toAngleProperty() {
if (toAngle == null) {
toAngle = new SimpleDoubleProperty(this, "toAngle", DEFAULT_TO_ANGLE);
}
return toAngle;
}
private DoubleProperty byAngle;
private static final double DEFAULT_BY_ANGLE = 0.0;
public final void setByAngle(double value) {
if ((byAngle != null) || (Math.abs(value - DEFAULT_BY_ANGLE) > EPSILON)) {
byAngleProperty().set(value);
}
}
public final double getByAngle() {
return (byAngle == null)? DEFAULT_BY_ANGLE : byAngle.get();
}
public final DoubleProperty byAngleProperty() {
if (byAngle == null) {
byAngle = new SimpleDoubleProperty(this, "byAngle", DEFAULT_BY_ANGLE);
}
return byAngle;
}
public RotateTransition(Duration duration, Node node) {
setDuration(duration);
setNode(node);
setCycleDuration(duration);
}
public RotateTransition(Duration duration) {
this(duration, null);
}
public RotateTransition() {
this(DEFAULT_DURATION, null);
}
@Override
protected void interpolate(double frac) {
cachedNode.setRotate(start + frac * delta);
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
final double _fromAngle = getFromAngle();
final double _toAngle = getToAngle();
start = (!Double.isNaN(_fromAngle)) ? _fromAngle : cachedNode
.getRotate();
delta = (!Double.isNaN(_toAngle)) ? _toAngle - start : getByAngle();
final Point3D _axis = getAxis();
if (_axis != null) {
node.get().setRotationAxis(_axis);
}
}
}
}
