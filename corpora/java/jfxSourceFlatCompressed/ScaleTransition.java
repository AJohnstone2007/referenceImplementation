package javafx.animation;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.util.Duration;
public final class ScaleTransition extends Transition {
private static final double EPSILON = 1e-12;
private double startX;
private double startY;
private double startZ;
private double deltaX;
private double deltaY;
private double deltaZ;
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
return ScaleTransition.this;
}
@Override
public String getName() {
return "duration";
}
};
}
return duration;
}
private DoubleProperty fromX;
private static final double DEFAULT_FROM_X = Double.NaN;
public final void setFromX(double value) {
if ((fromX != null) || (!Double.isNaN(value))) {
fromXProperty().set(value);
}
}
public final double getFromX() {
return (fromX == null) ? DEFAULT_FROM_X : fromX.get();
}
public final DoubleProperty fromXProperty() {
if (fromX == null) {
fromX = new SimpleDoubleProperty(this, "fromX", DEFAULT_FROM_X);
}
return fromX;
}
private DoubleProperty fromY;
private static final double DEFAULT_FROM_Y = Double.NaN;
public final void setFromY(double value) {
if ((fromY != null) || (!Double.isNaN(value))) {
fromYProperty().set(value);
}
}
public final double getFromY() {
return (fromY == null)? DEFAULT_FROM_Y : fromY.get();
}
public final DoubleProperty fromYProperty() {
if (fromY == null) {
fromY = new SimpleDoubleProperty(this, "fromY", DEFAULT_FROM_Y);
}
return fromY;
}
private DoubleProperty fromZ;
private static final double DEFAULT_FROM_Z = Double.NaN;
public final void setFromZ(double value) {
if ((fromZ != null) || (!Double.isNaN(value))) {
fromZProperty().set(value);
}
}
public final double getFromZ() {
return (fromZ == null)? DEFAULT_FROM_Z : fromZ.get();
}
public final DoubleProperty fromZProperty() {
if (fromZ == null) {
fromZ = new SimpleDoubleProperty(this, "fromZ", DEFAULT_FROM_Z);
}
return fromZ;
}
private DoubleProperty toX;
private static final double DEFAULT_TO_X = Double.NaN;
public final void setToX(double value) {
if ((toX != null) || (!Double.isNaN(value))) {
toXProperty().set(value);
}
}
public final double getToX() {
return (toX == null)? DEFAULT_TO_X : toX.get();
}
public final DoubleProperty toXProperty() {
if (toX == null) {
toX = new SimpleDoubleProperty(this, "toX", DEFAULT_TO_X);
}
return toX;
}
private DoubleProperty toY;
private static final double DEFAULT_TO_Y = Double.NaN;
public final void setToY(double value) {
if ((toY != null) || (!Double.isNaN(value))) {
toYProperty().set(value);
}
}
public final double getToY() {
return (toY == null)? DEFAULT_TO_Y : toY.get();
}
public final DoubleProperty toYProperty() {
if (toY == null) {
toY = new SimpleDoubleProperty(this, "toY", DEFAULT_TO_Y);
}
return toY;
}
private DoubleProperty toZ;
private static final double DEFAULT_TO_Z = Double.NaN;
public final void setToZ(double value) {
if ((toZ != null) || (!Double.isNaN(value))) {
toZProperty().set(value);
}
}
public final double getToZ() {
return (toZ == null)? DEFAULT_TO_Z : toZ.get();
}
public final DoubleProperty toZProperty() {
if (toZ == null) {
toZ = new SimpleDoubleProperty(this, "toZ", DEFAULT_TO_Z);
}
return toZ;
}
private DoubleProperty byX;
private static final double DEFAULT_BY_X = 0.0;
public final void setByX(double value) {
if ((byX != null) || (Math.abs(value - DEFAULT_BY_X) > EPSILON)) {
byXProperty().set(value);
}
}
public final double getByX() {
return (byX == null)? DEFAULT_BY_X : byX.get();
}
public final DoubleProperty byXProperty() {
if (byX == null) {
byX = new SimpleDoubleProperty(this, "byX", DEFAULT_BY_X);
}
return byX;
}
private DoubleProperty byY;
private static final double DEFAULT_BY_Y = 0.0;
public final void setByY(double value) {
if ((byY != null) || (Math.abs(value - DEFAULT_BY_Y) > EPSILON)) {
byYProperty().set(value);
}
}
public final double getByY() {
return (byY == null)? DEFAULT_BY_Y : byY.get();
}
public final DoubleProperty byYProperty() {
if (byY == null) {
byY = new SimpleDoubleProperty(this, "byY", DEFAULT_BY_Y);
}
return byY;
}
private DoubleProperty byZ;
private static final double DEFAULT_BY_Z = 0.0;
public final void setByZ(double value) {
if ((byZ != null) || (Math.abs(value - DEFAULT_BY_Z) > EPSILON)) {
byZProperty().set(value);
}
}
public final double getByZ() {
return (byZ == null)? DEFAULT_BY_Z : byZ.get();
}
public final DoubleProperty byZProperty() {
if (byZ == null) {
byZ = new SimpleDoubleProperty(this, "byZ", DEFAULT_BY_Z);
}
return byZ;
}
public ScaleTransition(Duration duration, Node node) {
setDuration(duration);
setNode(node);
setCycleDuration(duration);
}
public ScaleTransition(Duration duration) {
this(duration, null);
}
public ScaleTransition() {
this(DEFAULT_DURATION, null);
}
@Override
public void interpolate(double frac) {
if (!Double.isNaN(startX)) {
cachedNode.setScaleX(startX + frac * deltaX);
}
if (!Double.isNaN(startY)) {
cachedNode.setScaleY(startY + frac * deltaY);
}
if (!Double.isNaN(startZ)) {
cachedNode.setScaleZ(startZ + frac * deltaZ);
}
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
final double _fromX = getFromX();
final double _fromY = getFromY();
final double _fromZ = getFromZ();
final double _toX = getToX();
final double _toY = getToY();
final double _toZ = getToZ();
final double _byX = getByX();
final double _byY = getByY();
final double _byZ = getByZ();
if (Double.isNaN(_fromX) && Double.isNaN(_toX) && (Math.abs(_byX) < EPSILON)) {
startX = Double.NaN;
} else {
startX = (!Double.isNaN(_fromX)) ? _fromX : cachedNode.getScaleX();
deltaX = (!Double.isNaN(_toX)) ? _toX - startX : getByX();
}
if (Double.isNaN(_fromY) && Double.isNaN(_toY) && (Math.abs(_byY) < EPSILON)) {
startY = Double.NaN;
} else {
startY = (!Double.isNaN(_fromY)) ? _fromY : cachedNode.getScaleY();
deltaY = (!Double.isNaN(_toY)) ? _toY - startY : getByY();
}
if (Double.isNaN(_fromZ) && Double.isNaN(_toZ) && (Math.abs(_byZ) < EPSILON)) {
startZ = Double.NaN;
} else {
startZ = (!Double.isNaN(_fromZ)) ? _fromZ : cachedNode.getScaleZ();
deltaZ = (!Double.isNaN(_toZ)) ? _toZ - startZ : getByZ();
}
}
}
}
