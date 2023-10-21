package javafx.animation;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.geom.PathIterator;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.shape.ShapeHelper;
import java.util.ArrayList;
public final class PathTransition extends Transition {
private ObjectProperty<Node> node;
private double totalLength = 0;
private final ArrayList<Segment> segments = new ArrayList<>();
private static final Node DEFAULT_NODE = null;
private static final int SMOOTH_ZONE = 10;
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
return PathTransition.this;
}
@Override
public String getName() {
return "duration";
}
};
}
return duration;
}
private ObjectProperty<Shape> path;
private static final Shape DEFAULT_PATH = null;
public final void setPath(Shape value) {
if ((path != null) || (value != null )) {
pathProperty().set(value);
}
}
public final Shape getPath() {
return (path == null)? DEFAULT_PATH : path.get();
}
public final ObjectProperty<Shape> pathProperty() {
if (path == null) {
path = new SimpleObjectProperty<Shape>(this, "path", DEFAULT_PATH);
}
return path;
}
public static enum OrientationType {
NONE,
ORTHOGONAL_TO_TANGENT
}
private ObjectProperty<OrientationType> orientation;
private static final OrientationType DEFAULT_ORIENTATION = OrientationType.NONE;
public final void setOrientation(OrientationType value) {
if ((orientation != null) || (!DEFAULT_ORIENTATION.equals(value))) {
orientationProperty().set(value);
}
}
public final OrientationType getOrientation() {
return (orientation == null)? OrientationType.NONE : orientation.get();
}
public final ObjectProperty<OrientationType> orientationProperty() {
if (orientation == null) {
orientation = new SimpleObjectProperty<OrientationType>(this, "orientation", DEFAULT_ORIENTATION);
}
return orientation;
}
private boolean cachedIsNormalRequired;
public PathTransition(Duration duration, Shape path, Node node) {
setDuration(duration);
setPath(path);
setNode(node);
setCycleDuration(duration);
}
public PathTransition(Duration duration, Shape path) {
this(duration, path, null);
}
public PathTransition() {
this(DEFAULT_DURATION, null, null);
}
@Override
public void interpolate(double frac) {
double part = totalLength * Math.min(1, Math.max(0, frac));
int segIdx = findSegment(0, segments.size() - 1, part);
Segment seg = segments.get(segIdx);
double lengthBefore = seg.accumLength - seg.length;
double partLength = part - lengthBefore;
double ratio = partLength / seg.length;
Segment prevSeg = seg.prevSeg;
double x = prevSeg.toX + (seg.toX - prevSeg.toX) * ratio;
double y = prevSeg.toY + (seg.toY - prevSeg.toY) * ratio;
double rotateAngle = seg.rotateAngle;
double z = Math.min(SMOOTH_ZONE, seg.length / 2);
if (partLength < z && !prevSeg.isMoveTo) {
rotateAngle = interpolate(
prevSeg.rotateAngle, seg.rotateAngle,
partLength / z / 2 + 0.5F);
} else {
double dist = seg.length - partLength;
Segment nextSeg = seg.nextSeg;
if (dist < z && nextSeg != null) {
if (!nextSeg.isMoveTo) {
rotateAngle = interpolate(
seg.rotateAngle, nextSeg.rotateAngle,
(z - dist) / z / 2);
}
}
}
cachedNode.setTranslateX(x - NodeHelper.getPivotX(cachedNode));
cachedNode.setTranslateY(y - NodeHelper.getPivotY(cachedNode));
if (cachedIsNormalRequired) {
cachedNode.setRotate(rotateAngle);
}
}
private Node getTargetNode() {
final Node node = getNode();
return (node != null) ? node : getParentTargetNode();
}
@Override
boolean startable(boolean forceSync) {
return super.startable(forceSync)
&& (((getTargetNode() != null) && (getPath() != null) && !getPath().getLayoutBounds().isEmpty()) || (!forceSync
&& (cachedNode != null)));
}
@Override
void sync(boolean forceSync) {
super.sync(forceSync);
if (forceSync || (cachedNode == null)) {
cachedNode = getTargetNode();
recomputeSegments();
cachedIsNormalRequired = getOrientation() == OrientationType.ORTHOGONAL_TO_TANGENT;
}
}
private void recomputeSegments() {
segments.clear();
final Shape p = getPath();
Segment moveToSeg = Segment.getZeroSegment();
Segment lastSeg = Segment.getZeroSegment();
float[] coords = new float[6];
for (PathIterator i = ShapeHelper.configShape(p).getPathIterator(NodeHelper.getLeafTransform(p), 1.0f); !i.isDone(); i.next()) {
Segment newSeg = null;
int segType = i.currentSegment(coords);
double x = coords[0];
double y = coords[1];
switch (segType) {
case PathIterator.SEG_MOVETO:
moveToSeg = Segment.newMoveTo(x, y, lastSeg.accumLength);
newSeg = moveToSeg;
break;
case PathIterator.SEG_CLOSE:
newSeg = Segment.newClosePath(lastSeg, moveToSeg);
if (newSeg == null) {
lastSeg.convertToClosePath(moveToSeg);
}
break;
case PathIterator.SEG_LINETO:
newSeg = Segment.newLineTo(lastSeg, x, y);
break;
}
if (newSeg != null) {
segments.add(newSeg);
lastSeg = newSeg;
}
}
totalLength = lastSeg.accumLength;
}
private int findSegment(int begin, int end, double length) {
if (begin == end) {
return segments.get(begin).isMoveTo && begin > 0
? findSegment(begin - 1, begin - 1, length)
: begin;
}
int middle = begin + (end - begin) / 2;
return segments.get(middle).accumLength > length
? findSegment(begin, middle, length)
: findSegment(middle + 1, end, length);
}
private static double interpolate(double fromAngle, double toAngle, double ratio) {
double delta = toAngle - fromAngle;
if (Math.abs(delta) > 180) {
toAngle += delta > 0 ? -360 : 360;
}
return normalize(fromAngle + ratio * (toAngle - fromAngle));
}
private static double normalize(double angle) {
while (angle > 360) {
angle -= 360;
}
while (angle < 0) {
angle += 360;
}
return angle;
}
private static class Segment {
private static final Segment zeroSegment = new Segment(true, 0, 0, 0, 0, 0);
boolean isMoveTo;
double length;
double accumLength;
double toX;
double toY;
double rotateAngle;
Segment prevSeg;
Segment nextSeg;
private Segment(boolean isMoveTo, double toX, double toY,
double length, double lengthBefore, double rotateAngle) {
this.isMoveTo = isMoveTo;
this.toX = toX;
this.toY = toY;
this.length = length;
this.accumLength = lengthBefore + length;
this.rotateAngle = rotateAngle;
}
public static Segment getZeroSegment() {
return zeroSegment;
}
public static Segment newMoveTo(double toX, double toY,
double accumLength) {
return new Segment(true, toX, toY, 0, accumLength, 0);
}
public static Segment newLineTo(Segment fromSeg, double toX, double toY) {
double deltaX = toX - fromSeg.toX;
double deltaY = toY - fromSeg.toY;
double length = Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
if ((length >= 1) || fromSeg.isMoveTo) {
double sign = Math.signum(deltaY == 0 ? deltaX : deltaY);
double angle = (sign * Math.acos(deltaX / length));
angle = normalize(angle / Math.PI * 180);
Segment newSeg = new Segment(false, toX, toY,
length, fromSeg.accumLength, angle);
fromSeg.nextSeg = newSeg;
newSeg.prevSeg = fromSeg;
return newSeg;
}
return null;
}
public static Segment newClosePath(Segment fromSeg, Segment moveToSeg) {
Segment newSeg = newLineTo(fromSeg, moveToSeg.toX, moveToSeg.toY);
if (newSeg != null) {
newSeg.convertToClosePath(moveToSeg);
}
return newSeg;
}
public void convertToClosePath(Segment moveToSeg) {
Segment firstLineToSeg = moveToSeg.nextSeg;
nextSeg = firstLineToSeg;
firstLineToSeg.prevSeg = this;
}
}
}
