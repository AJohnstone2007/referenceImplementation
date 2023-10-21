package com.sun.javafx.webkit.prism;
import com.sun.javafx.geom.Arc2D;
import com.sun.javafx.geom.Ellipse2D;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.geom.PathIterator;
import com.sun.javafx.geom.Point2D;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.RoundRectangle2D;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.webkit.graphics.WCPath;
import com.sun.webkit.graphics.WCPathIterator;
import com.sun.webkit.graphics.WCRectangle;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.logging.PlatformLogger.Level;
import com.sun.prism.BasicStroke;
import java.util.Arrays;
final class WCPathImpl extends WCPath<Path2D> {
private final Path2D path;
private boolean hasCP = false;
private final static PlatformLogger log =
PlatformLogger.getLogger(WCPathImpl.class.getName());
WCPathImpl() {
if (log.isLoggable(Level.FINE)) {
log.fine("Create empty WCPathImpl({0})", getID());
}
path = new Path2D();
}
WCPathImpl(WCPathImpl wcp) {
if (log.isLoggable(Level.FINE)) {
log.fine("Create WCPathImpl({0}) from WCPathImpl({1})",
new Object[] { getID(), wcp.getID()});
}
path = new Path2D(wcp.path);
hasCP = wcp.hasCP;
}
public void addRect(double x, double y, double w, double h) {
if (log.isLoggable(Level.FINE)) {
log.fine("WCPathImpl({0}).addRect({1},{2},{3},{4})",
new Object[] {getID(), x, y, w, h});
}
hasCP = true;
path.append(new RoundRectangle2D(
(float)x, (float)y, (float)w, (int)h, 0.0f, 0.0f), false);
}
public void addEllipse(double x, double y, double w, double h)
{
if (log.isLoggable(Level.FINE)) {
log.fine("WCPathImpl({0}).addEllipse({1},{2},{3},{4})",
new Object[] {getID(), x, y, w, h});
}
hasCP = true;
path.append(new Ellipse2D((float)x, (float)y, (float)w, (float)h), false);
}
public void addArcTo(double x1, double y1, double x2, double y2, double r)
{
if (log.isLoggable(Level.FINE)) {
log.fine("WCPathImpl({0}).addArcTo({1},{2},{3},{4})",
new Object[] {getID(), x1, y1, x2, y2});
}
Arc2D arc = new Arc2D();
arc.setArcByTangent(
path.getCurrentPoint(),
new Point2D((float) x1, (float) y1),
new Point2D((float) x2, (float) y2),
(float) r);
hasCP = true;
path.append(arc, true);
}
public void addArc(double x, double y, double r, double sa,
double ea, boolean aclockwise)
{
final float TWO_PI = 2.0f * (float) Math.PI;
float startAngle = (float) sa;
float endAngle = (float) ea;
if (log.isLoggable(Level.FINE)) {
log.fine("WCPathImpl({0}).addArc(x={1},y={2},r={3},sa=|{4}|,ea=|{5}|,aclock={6})",
new Object[] {getID(), x, y, r, startAngle, endAngle, aclockwise});
}
hasCP = true;
float newEndAngle = endAngle;
if (!aclockwise && startAngle > endAngle) {
newEndAngle = startAngle + (TWO_PI - ((startAngle - endAngle) % TWO_PI));
} else if (aclockwise && startAngle < endAngle) {
newEndAngle = startAngle - (TWO_PI - ((endAngle - startAngle) % TWO_PI));
}
path.append(new Arc2D((float) (x - r), (float) (y - r),
(float) (2 * r), (float) (2 * r),
(float) Math.toDegrees(-startAngle),
(float) Math.toDegrees(startAngle - newEndAngle), Arc2D.OPEN), true);
}
public boolean contains(int rule, double x, double y) {
if (log.isLoggable(Level.FINE)) {
log.fine("WCPathImpl({0}).contains({1},{2},{3})",
new Object[] {getID(), rule, x, y});
}
final int savedRule = path.getWindingRule();
path.setWindingRule(rule);
final boolean res = path.contains((float)x, (float)y);
path.setWindingRule(savedRule);
return res;
}
@Override
public WCRectangle getBounds() {
RectBounds b = path.getBounds();
return new WCRectangle(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
}
public void clear() {
if (log.isLoggable(Level.FINE)) {
log.fine("WCPathImpl({0}).clear()", getID());
}
hasCP = false;
path.reset();
}
public void moveTo(double x, double y) {
if (log.isLoggable(Level.FINE)) {
log.fine("WCPathImpl({0}).moveTo({1},{2})",
new Object[] {getID(), x, y});
}
hasCP = true;
path.moveTo((float)x, (float)y);
}
public void addLineTo(double x, double y) {
if (log.isLoggable(Level.FINE)) {
log.fine("WCPathImpl({0}).addLineTo({1},{2})",
new Object[] {getID(), x, y});
}
hasCP = true;
path.lineTo((float)x, (float)y);
}
public void addQuadCurveTo(double x0, double y0, double x1, double y1) {
if (log.isLoggable(Level.FINE)) {
log.fine("WCPathImpl({0}).addQuadCurveTo({1},{2},{3},{4})",
new Object[] {getID(), x0, y0, x1, y1});
}
hasCP = true;
path.quadTo((float)x0, (float)y0, (float)x1, (float)y1);
}
public void addBezierCurveTo(double x0, double y0, double x1, double y1,
double x2, double y2) {
if (log.isLoggable(Level.FINE)) {
log.fine("WCPathImpl({0}).addBezierCurveTo({1},{2},{3},{4},{5},{6})",
new Object[] {getID(), x0, y0, x1, y1, x2, y2});
}
hasCP = true;
path.curveTo((float)x0, (float)y0, (float)x1, (float)y1,
(float)x2, (float)y2);
}
public void addPath(WCPath p) {
if (log.isLoggable(Level.FINE)) {
log.fine("WCPathImpl({0}).addPath({1})",
new Object[] {getID(), p.getID()});
}
hasCP = hasCP || ((WCPathImpl)p).hasCP;
path.append(((WCPathImpl)p).path, false);
}
public void closeSubpath() {
if (log.isLoggable(Level.FINE)) {
log.fine("WCPathImpl({0}).closeSubpath()", getID());
}
path.closePath();
}
public boolean isEmpty() {
return !hasCP;
}
public int getWindingRule() {
return 1 - this.path.getWindingRule();
}
public void setWindingRule(int rule) {
this.path.setWindingRule(1 - rule);
}
public Path2D getPlatformPath() {
if (log.isLoggable(Level.FINE)) {
log.fine("WCPathImpl({0}).getPath() BEGIN=====", getID());
PathIterator pi = path.getPathIterator(null);
float [] coords = new float[6];
while(!pi.isDone()) {
switch(pi.currentSegment(coords)) {
case PathIterator.SEG_MOVETO:
log.fine("SEG_MOVETO ({0},{1})",
new Object[] {coords[0], coords[1]});
break;
case PathIterator.SEG_LINETO:
log.fine("SEG_LINETO ({0},{1})",
new Object[] {coords[0], coords[1]});
break;
case PathIterator.SEG_QUADTO:
log.fine("SEG_QUADTO ({0},{1},{2},{3})",
new Object[] {coords[0], coords[1], coords[2], coords[3]});
break;
case PathIterator.SEG_CUBICTO:
log.fine("SEG_CUBICTO ({0},{1},{2},{3},{4},{5})",
new Object[] {coords[0], coords[1], coords[2], coords[3],
coords[4], coords[5]});
break;
case PathIterator.SEG_CLOSE:
log.fine("SEG_CLOSE");
break;
}
pi.next();
}
log.fine("========getPath() END=====");
}
return path;
}
public WCPathIterator getPathIterator() {
final PathIterator pi = path.getPathIterator(null);
return new WCPathIterator() {
@Override public int getWindingRule() {
return pi.getWindingRule();
}
@Override public boolean isDone() {
return pi.isDone();
}
@Override public void next() {
pi.next();
}
@Override public int currentSegment(double[] coords) {
float [] _coords = new float[6];
int segmentType = pi.currentSegment(_coords);
for (int i = 0; i < coords.length; i++) {
coords[i] = _coords[i];
}
return segmentType;
}
};
}
public void translate(double x, double y)
{
if (log.isLoggable(Level.FINE)) {
log.fine("WCPathImpl({0}).translate({1}, {2})",
new Object[] {getID(), x, y});
}
path.transform(BaseTransform.getTranslateInstance(x, y));
}
public void transform(double mxx, double myx,
double mxy, double myy,
double mxt, double myt)
{
if (log.isLoggable(Level.FINE)) {
log.fine("WCPathImpl({0}).transform({1},{2},{3},{4},{5},{6})",
new Object[] {getID(), mxx, myx, mxy, myy, mxt, myt});
}
path.transform(BaseTransform.getInstance(mxx, myx, mxy, myy, mxt, myt));
}
@Override
public boolean strokeContains(double x, double y,
double thickness, double miterLimit,
int cap, int join, double dashOffset,
double[] dashArray) {
BasicStroke stroke = new BasicStroke(
(float) thickness, cap, join, (float) miterLimit);
if (dashArray.length > 0) {
stroke.set(dashArray, (float) dashOffset);
}
boolean result = stroke
.createCenteredStrokedShape(path)
.contains((float) x, (float) y);
if (log.isLoggable(Level.FINE)) {
log.fine(
"WCPathImpl({0}).strokeContains({1},{2},{3},{4},{5},{6},{7},{8}) = {9}",
new Object[]{getID(), x, y, thickness, miterLimit, cap, join,
dashOffset, Arrays.toString(dashArray), result});
}
return result;
}
}
