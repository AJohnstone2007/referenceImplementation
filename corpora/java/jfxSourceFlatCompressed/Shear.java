package javafx.scene.transform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.geom.transform.BaseTransform;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
public class Shear extends Transform {
public Shear() {
}
public Shear(double x, double y) {
setX(x);
setY(y);
}
public Shear(double x, double y, double pivotX, double pivotY) {
setX(x);
setY(y);
setPivotX(pivotX);
setPivotY(pivotY);
}
private DoubleProperty x;
public final void setX(double value) {
xProperty().set(value);
}
public final double getX() {
return x == null ? 0.0 : x.get();
}
public final DoubleProperty xProperty() {
if (x == null) {
x = new DoublePropertyBase() {
@Override
public void invalidated() {
transformChanged();
}
@Override
public Object getBean() {
return Shear.this;
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
yProperty().set(value);
}
public final double getY() {
return y == null ? 0.0 : y.get();
}
public final DoubleProperty yProperty() {
if (y == null) {
y = new DoublePropertyBase() {
@Override
public void invalidated() {
transformChanged();
}
@Override
public Object getBean() {
return Shear.this;
}
@Override
public String getName() {
return "y";
}
};
}
return y;
}
private DoubleProperty pivotX;
public final void setPivotX(double value) {
pivotXProperty().set(value);
}
public final double getPivotX() {
return pivotX == null ? 0.0 : pivotX.get();
}
public final DoubleProperty pivotXProperty() {
if (pivotX == null) {
pivotX = new DoublePropertyBase() {
@Override
public void invalidated() {
transformChanged();
}
@Override
public Object getBean() {
return Shear.this;
}
@Override
public String getName() {
return "pivotX";
}
};
}
return pivotX;
}
private DoubleProperty pivotY;
public final void setPivotY(double value) {
pivotYProperty().set(value);
}
public final double getPivotY() {
return pivotY == null ? 0.0 : pivotY.get();
}
public final DoubleProperty pivotYProperty() {
if (pivotY == null) {
pivotY = new DoublePropertyBase() {
@Override
public void invalidated() {
transformChanged();
}
@Override
public Object getBean() {
return Shear.this;
}
@Override
public String getName() {
return "pivotY";
}
};
}
return pivotY;
}
@Override
public double getMxy() {
return getX();
}
@Override
public double getMyx() {
return getY();
}
@Override
public double getTx() {
return -getX() * getPivotY();
}
@Override
public double getTy() {
return -getY() * getPivotX();
}
@Override
boolean computeIs2D() {
return true;
}
@Override
boolean computeIsIdentity() {
return getX() == 0.0 && getY() == 0.0;
}
@Override
void fill2DArray(double[] array) {
final double sx = getX();
final double sy = getY();
array[0] = 1.0;
array[1] = sx;
array[2] = -sx * getPivotY();
array[3] = sy;
array[4] = 1.0;
array[5] = -sy * getPivotX();
}
@Override
void fill3DArray(double[] array) {
final double sx = getX();
final double sy = getY();
array[0] = 1.0;
array[1] = sx;
array[2] = 0.0;
array[3] = -sx * getPivotY();
array[4] = sy;
array[5] = 1.0;
array[6] = 0.0;
array[7] = -sy * getPivotX();
array[8] = 0.0;
array[9] = 0.0;
array[10] = 1.0;
array[11] = 0.0;
}
@Override
public Transform createConcatenation(Transform transform) {
if (transform instanceof Affine) {
Affine a = (Affine) transform.clone();
a.prepend(this);
return a;
}
final double sx = getX();
final double sy = getY();
final double txx = transform.getMxx();
final double txy = transform.getMxy();
final double txz = transform.getMxz();
final double ttx = transform.getTx();
final double tyx = transform.getMyx();
final double tyy = transform.getMyy();
final double tyz = transform.getMyz();
final double tty = transform.getTy();
return new Affine(
txx + sx * tyx,
txy + sx * tyy,
txz + sx * tyz,
ttx + sx * tty - sx * getPivotY(),
sy * txx + tyx,
sy * txy + tyy,
sy * txz + tyz,
sy * ttx + tty - sy * getPivotX(),
transform.getMzx(),
transform.getMzy(),
transform.getMzz(),
transform.getTz());
}
@Override
public Transform createInverse() {
final double sx = getX();
final double sy = getY();
if (sy == 0.0) {
return new Shear(-sx, 0.0, 0.0, getPivotY());
}
if (sx == 0.0) {
return new Shear(0.0, -sy, getPivotX(), 0.0);
}
final double px = getPivotX();
final double py = getPivotY();
final double coef = 1.0 / (1.0 - sx * sy);
return new Affine(
coef, -sx * coef, 0, sx * (py - sy * px) * coef,
-sy * coef, 1 + sx * sy * coef, 0, sy * px + sy * (sx * sy * px - sx * py) * coef,
0, 0, 1, 0);
}
@Override
public Shear clone() {
return new Shear(getX(), getY(), getPivotX(), getPivotY());
}
@Override
public Point2D transform(double x, double y) {
final double mxy = getX();
final double myx = getY();
return new Point2D(
x + mxy * y - mxy * getPivotY(),
myx * x + y - myx * getPivotX());
}
@Override
public Point3D transform(double x, double y, double z) {
final double mxy = getX();
final double myx = getY();
return new Point3D(
x + mxy * y - mxy * getPivotY(),
myx * x + y - myx * getPivotX(),
z);
}
@Override
void transform2DPointsImpl(double[] srcPts, int srcOff,
double[] dstPts, int dstOff, int numPts) {
final double xy = getX();
final double yx = getY();
final double px = getPivotX();
final double py = getPivotY();
while (--numPts >= 0) {
final double x = srcPts[srcOff++];
final double y = srcPts[srcOff++];
dstPts[dstOff++] = x + xy * y - xy * py;
dstPts[dstOff++] = yx * x + y - yx * px;
}
}
@Override
void transform3DPointsImpl(double[] srcPts, int srcOff,
double[] dstPts, int dstOff, int numPts) {
final double xy = getX();
final double yx = getY();
final double px = getPivotX();
final double py = getPivotY();
while (--numPts >= 0) {
final double x = srcPts[srcOff++];
final double y = srcPts[srcOff++];
dstPts[dstOff++] = x + xy * y - xy * py;
dstPts[dstOff++] = yx * x + y - yx * px;
dstPts[dstOff++] = srcPts[srcOff++];
}
}
@Override
public Point2D deltaTransform(double x, double y) {
return new Point2D(
x + getX() * y,
getY() * x + y);
}
@Override
public Point3D deltaTransform(double x, double y, double z) {
return new Point3D(
x + getX() * y,
getY() * x + y,
z);
}
@Override
public Point2D inverseTransform(double x, double y)
throws NonInvertibleTransformException {
final double sx = getX();
final double sy = getY();
if (sy == 0.0) {
final double mxy = -getX();
return new Point2D(
x + mxy * y - mxy * getPivotY(),
y);
}
if (sx == 0.0) {
final double myx = -getY();
return new Point2D(
x,
myx * x + y - myx * getPivotX());
}
return super.inverseTransform(x, y);
}
@Override
public Point3D inverseTransform(double x, double y, double z)
throws NonInvertibleTransformException {
final double sx = getX();
final double sy = getY();
if (sy == 0.0) {
final double mxy = -getX();
return new Point3D(
x + mxy * y - mxy * getPivotY(),
y,
z);
}
if (sx == 0.0) {
final double myx = -getY();
return new Point3D(
x,
myx * x + y - myx * getPivotX(),
z);
}
return super.inverseTransform(x, y, z);
}
@Override
void inverseTransform2DPointsImpl(double[] srcPts, int srcOff,
double[] dstPts, int dstOff, int numPts)
throws NonInvertibleTransformException {
final double px = getPivotX();
final double py = getPivotY();
final double sx = getX();
final double sy = getY();
if (sy == 0.0) {
final double xy = -sx;
while (--numPts >= 0) {
final double x = srcPts[srcOff++];
final double y = srcPts[srcOff++];
dstPts[dstOff++] = x + xy * y - xy * py;
dstPts[dstOff++] = y;
}
return;
}
if (sx == 0.0) {
final double yx = -sy;
while (--numPts >= 0) {
final double x = srcPts[srcOff++];
final double y = srcPts[srcOff++];
dstPts[dstOff++] = x;
dstPts[dstOff++] = yx * x + y - yx * px;
}
return;
}
super.inverseTransform2DPointsImpl(srcPts, srcOff, dstPts, dstOff, numPts);
}
@Override
void inverseTransform3DPointsImpl(double[] srcPts, int srcOff,
double[] dstPts, int dstOff, int numPts)
throws NonInvertibleTransformException{
final double px = getPivotX();
final double py = getPivotY();
final double sx = getX();
final double sy = getY();
if (sy == 0.0) {
final double xy = -sx;
while (--numPts >= 0) {
final double x = srcPts[srcOff++];
final double y = srcPts[srcOff++];
dstPts[dstOff++] = x + xy * y - xy * py;
dstPts[dstOff++] = y;
dstPts[dstOff++] = srcPts[srcOff++];
}
return;
}
if (sx == 0.0) {
final double yx = -sy;
while (--numPts >= 0) {
final double x = srcPts[srcOff++];
final double y = srcPts[srcOff++];
dstPts[dstOff++] = x;
dstPts[dstOff++] = yx * x + y - yx * px;
dstPts[dstOff++] = srcPts[srcOff++];
}
return;
}
super.inverseTransform3DPointsImpl(srcPts, srcOff, dstPts, dstOff, numPts);
}
@Override
public Point2D inverseDeltaTransform(double x, double y)
throws NonInvertibleTransformException {
final double sx = getX();
final double sy = getY();
if (sy == 0.0) {
return new Point2D(
x - getX() * y,
y);
}
if (sx == 0.0) {
return new Point2D(
x,
-getY() * x + y);
}
return super.inverseDeltaTransform(x, y);
}
@Override
public Point3D inverseDeltaTransform(double x, double y, double z)
throws NonInvertibleTransformException {
final double sx = getX();
final double sy = getY();
if (sy == 0.0) {
return new Point3D(
x - getX() * y,
y,
z);
}
if (sx == 0.0) {
return new Point3D(
x,
-getY() * x + y,
z);
}
return super.inverseDeltaTransform(x, y, z);
}
@Override
public String toString() {
final StringBuilder sb = new StringBuilder("Shear [");
sb.append("x=").append(getX());
sb.append(", y=").append(getY());
sb.append(", pivotX=").append(getPivotX());
sb.append(", pivotY=").append(getPivotY());
return sb.append("]").toString();
}
@Override
void apply(final Affine3D trans) {
if (getPivotX() != 0 || getPivotY() != 0) {
trans.translate(getPivotX(), getPivotY());
trans.shear(getX(), getY());
trans.translate(-getPivotX(), -getPivotY());
} else {
trans.shear(getX(), getY());
}
}
@Override
BaseTransform derive(final BaseTransform trans) {
return trans.deriveWithConcatenation(
1.0, getY(),
getX(), 1.0,
getTx(), getTy());
}
@Override
void validate() {
getX(); getPivotX();
getY(); getPivotY();
}
@Override
void appendTo(Affine a) {
a.appendShear(getX(), getY(), getPivotX(), getPivotY());
}
@Override
void prependTo(Affine a) {
a.prependShear(getX(), getY(), getPivotX(), getPivotY());
}
}
