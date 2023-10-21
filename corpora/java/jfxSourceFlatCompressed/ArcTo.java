package javafx.scene.shape;
import com.sun.javafx.geom.Arc2D;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.geom.PathIterator;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.shape.ArcToHelper;
import com.sun.javafx.sg.prism.NGPath;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
public class ArcTo extends PathElement {
static {
ArcToHelper.setArcToAccessor(new ArcToHelper.ArcToAccessor() {
@Override
public void doAddTo(PathElement pathElement, Path2D path) {
((ArcTo) pathElement).doAddTo(path);
}
});
}
public ArcTo() {
ArcToHelper.initHelper(this);
}
public ArcTo(double radiusX, double radiusY, double xAxisRotation,
double x, double y, boolean largeArcFlag, boolean sweepFlag)
{
setRadiusX(radiusX);
setRadiusY(radiusY);
setXAxisRotation(xAxisRotation);
setX(x);
setY(y);
setLargeArcFlag(largeArcFlag);
setSweepFlag(sweepFlag);
ArcToHelper.initHelper(this);
}
private DoubleProperty radiusX = new DoublePropertyBase() {
@Override
public void invalidated() {
u();
}
@Override
public Object getBean() {
return ArcTo.this;
}
@Override
public String getName() {
return "radiusX";
}
};
public final void setRadiusX(double value) {
radiusX.set(value);
}
public final double getRadiusX() {
return radiusX.get();
}
public final DoubleProperty radiusXProperty() {
return radiusX;
}
private DoubleProperty radiusY = new DoublePropertyBase() {
@Override
public void invalidated() {
u();
}
@Override
public Object getBean() {
return ArcTo.this;
}
@Override
public String getName() {
return "radiusY";
}
};
public final void setRadiusY(double value) {
radiusY.set(value);
}
public final double getRadiusY() {
return radiusY.get();
}
public final DoubleProperty radiusYProperty() {
return radiusY;
}
private DoubleProperty xAxisRotation;
public final void setXAxisRotation(double value) {
if (xAxisRotation != null || value != 0.0) {
XAxisRotationProperty().set(value);
}
}
public final double getXAxisRotation() {
return xAxisRotation == null ? 0.0 : xAxisRotation.get();
}
public final DoubleProperty XAxisRotationProperty() {
if (xAxisRotation == null) {
xAxisRotation = new DoublePropertyBase() {
@Override
public void invalidated() {
u();
}
@Override
public Object getBean() {
return ArcTo.this;
}
@Override
public String getName() {
return "XAxisRotation";
}
};
}
return xAxisRotation;
}
private BooleanProperty largeArcFlag;
public final void setLargeArcFlag(boolean value) {
if (largeArcFlag != null || value != false) {
largeArcFlagProperty().set(value);
}
}
public final boolean isLargeArcFlag() {
return largeArcFlag == null ? false : largeArcFlag.get();
}
public final BooleanProperty largeArcFlagProperty() {
if (largeArcFlag == null) {
largeArcFlag = new BooleanPropertyBase() {
@Override
public void invalidated() {
u();
}
@Override
public Object getBean() {
return ArcTo.this;
}
@Override
public String getName() {
return "largeArcFlag";
}
};
}
return largeArcFlag;
}
private BooleanProperty sweepFlag;
public final void setSweepFlag(boolean value) {
if (sweepFlag != null || value != false) {
sweepFlagProperty().set(value);
}
}
public final boolean isSweepFlag() {
return sweepFlag == null ? false : sweepFlag.get();
}
public final BooleanProperty sweepFlagProperty() {
if (sweepFlag == null) {
sweepFlag = new BooleanPropertyBase() {
@Override
public void invalidated() {
u();
}
@Override
public Object getBean() {
return ArcTo.this;
}
@Override
public String getName() {
return "sweepFlag";
}
};
}
return sweepFlag;
}
private DoubleProperty x;
public final void setX(double value) {
if (x != null || value != 0.0) {
xProperty().set(value);
}
}
public final double getX() {
return x == null ? 0.0 : x.get();
}
public final DoubleProperty xProperty() {
if (x == null) {
x = new DoublePropertyBase() {
@Override
public void invalidated() {
u();
}
@Override
public Object getBean() {
return ArcTo.this;
}
@Override
public String getName() {
return "x";
}
};
}
return x;
}
private DoubleProperty y;
public final void setY(double value) {
if (y != null || value != 0.0) {
yProperty().set(value);
}
}
public final double getY() {
return y == null ? 0.0 : y.get();
}
public final DoubleProperty yProperty() {
if (y == null) {
y = new DoublePropertyBase() {
@Override
public void invalidated() {
u();
}
@Override
public Object getBean() {
return ArcTo.this;
}
@Override
public String getName() {
return "y";
}
};
}
return y;
}
@Override
void addTo(NGPath pgPath) {
addArcTo(pgPath, null, pgPath.getCurrentX(), pgPath.getCurrentY());
}
private void doAddTo(Path2D path) {
addArcTo(null, path, path.getCurrentX(), path.getCurrentY());
}
private void addArcTo(NGPath pgPath, Path2D path,
final double x0, final double y0)
{
double localX = getX();
double localY = getY();
boolean localSweepFlag = isSweepFlag();
boolean localLargeArcFlag = isLargeArcFlag();
final double xto = (isAbsolute()) ? localX : localX + x0;
final double yto = (isAbsolute()) ? localY : localY + y0;
final double dx2 = (x0 - xto) / 2.0;
final double dy2 = (y0 - yto) / 2.0;
final double xAxisRotationR = Math.toRadians(getXAxisRotation());
final double cosAngle = Math.cos(xAxisRotationR);
final double sinAngle = Math.sin(xAxisRotationR);
final double x1 = ( cosAngle * dx2 + sinAngle * dy2);
final double y1 = (-sinAngle * dx2 + cosAngle * dy2);
double rx = Math.abs(getRadiusX());
double ry = Math.abs(getRadiusY());
double Prx = rx * rx;
double Pry = ry * ry;
final double Px1 = x1 * x1;
final double Py1 = y1 * y1;
final double radiiCheck = Px1/Prx + Py1/Pry;
if (radiiCheck > 1.0) {
rx = Math.sqrt(radiiCheck) * rx;
ry = Math.sqrt(radiiCheck) * ry;
if (rx == rx && ry == ry) { } else {
if (pgPath == null) {
path.lineTo((float) xto, (float) yto);
} else {
pgPath.addLineTo((float) xto, (float) yto);
}
return;
}
Prx = rx * rx;
Pry = ry * ry;
}
double sign = ((localLargeArcFlag == localSweepFlag) ? -1.0 : 1.0);
double sq = ((Prx*Pry)-(Prx*Py1)-(Pry*Px1)) / ((Prx*Py1)+(Pry*Px1));
sq = (sq < 0.0) ? 0.0 : sq;
final double coef = (sign * Math.sqrt(sq));
final double cx1 = coef * ((rx * y1) / ry);
final double cy1 = coef * -((ry * x1) / rx);
final double sx2 = (x0 + xto) / 2.0;
final double sy2 = (y0 + yto) / 2.0;
final double cx = sx2 + (cosAngle * cx1 - sinAngle * cy1);
final double cy = sy2 + (sinAngle * cx1 + cosAngle * cy1);
final double ux = (x1 - cx1) / rx;
final double uy = (y1 - cy1) / ry;
final double vx = (-x1 - cx1) / rx;
final double vy = (-y1 - cy1) / ry;
double n = Math.sqrt((ux * ux) + (uy * uy));
double p = ux;
sign = ((uy < 0.0) ? -1.0 : 1.0);
double angleStart = Math.toDegrees(sign * Math.acos(p / n));
n = Math.sqrt((ux * ux + uy * uy) * (vx * vx + vy * vy));
p = ux * vx + uy * vy;
sign = ((ux * vy - uy * vx < 0.0) ? -1.0 : 1.0);
double angleExtent = Math.toDegrees(sign * Math.acos(p / n));
if (!localSweepFlag && (angleExtent > 0)) {
angleExtent -= 360.0;
} else if (localSweepFlag && (angleExtent < 0)) {
angleExtent += 360.0;
}
angleExtent = angleExtent % 360;
angleStart = angleStart % 360;
final float arcX = (float) (cx - rx);
final float arcY = (float) (cy - ry);
final float arcW = (float) (rx * 2.0);
final float arcH = (float) (ry * 2.0);
final float arcStart = (float) -angleStart;
final float arcExtent = (float) -angleExtent;
if (pgPath == null) {
final Arc2D arc =
new Arc2D(arcX, arcY, arcW, arcH,
arcStart, arcExtent, Arc2D.OPEN);
BaseTransform xform = (xAxisRotationR == 0) ? null :
BaseTransform.getRotateInstance(xAxisRotationR, cx, cy);
PathIterator pi = arc.getPathIterator(xform);
pi.next();
path.append(pi, true);
} else {
pgPath.addArcTo(arcX, arcY, arcW, arcH,
arcStart, arcExtent, (float) xAxisRotationR);
}
}
@Override
public String toString() {
final StringBuilder sb = new StringBuilder("ArcTo[");
sb.append("x=").append(getX());
sb.append(", y=").append(getY());
sb.append(", radiusX=").append(getRadiusX());
sb.append(", radiusY=").append(getRadiusY());
sb.append(", xAxisRotation=").append(getXAxisRotation());
if (isLargeArcFlag()) {
sb.append(", lartArcFlag");
}
if (isSweepFlag()) {
sb.append(", sweepFlag");
}
return sb.append("]").toString();
}
}
