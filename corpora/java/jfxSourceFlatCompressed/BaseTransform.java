package com.sun.javafx.geom.transform;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.Point2D;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.Shape;
import com.sun.javafx.geom.Vec3d;
public abstract class BaseTransform implements CanTransformVec3d{
public static final BaseTransform IDENTITY_TRANSFORM = new Identity();
public static enum Degree {
IDENTITY,
TRANSLATE_2D,
AFFINE_2D,
TRANSLATE_3D,
AFFINE_3D,
}
protected static final int TYPE_UNKNOWN = -1;
public static final int TYPE_IDENTITY = 0;
public static final int TYPE_TRANSLATION = 1;
public static final int TYPE_UNIFORM_SCALE = 2;
public static final int TYPE_GENERAL_SCALE = 4;
public static final int TYPE_MASK_SCALE = (TYPE_UNIFORM_SCALE |
TYPE_GENERAL_SCALE);
public static final int TYPE_FLIP = 64;
public static final int TYPE_QUADRANT_ROTATION = 8;
public static final int TYPE_GENERAL_ROTATION = 16;
public static final int TYPE_MASK_ROTATION = (TYPE_QUADRANT_ROTATION |
TYPE_GENERAL_ROTATION);
public static final int TYPE_GENERAL_TRANSFORM = 32;
public static final int TYPE_AFFINE2D_MASK =
(TYPE_TRANSLATION |
TYPE_UNIFORM_SCALE |
TYPE_GENERAL_SCALE |
TYPE_QUADRANT_ROTATION |
TYPE_GENERAL_ROTATION |
TYPE_GENERAL_TRANSFORM |
TYPE_FLIP);
public static final int TYPE_AFFINE_3D = 128;
static void degreeError(Degree maxSupported) {
throw new InternalError("does not support higher than "+
maxSupported+" operations");
}
public static BaseTransform getInstance(BaseTransform tx) {
if (tx.isIdentity()) {
return IDENTITY_TRANSFORM;
} else if (tx.isTranslateOrIdentity()) {
return new Translate2D(tx);
} else if (tx.is2D()) {
return new Affine2D(tx);
}
return new Affine3D(tx);
}
public static BaseTransform getInstance(double mxx, double mxy, double mxz, double mxt,
double myx, double myy, double myz, double myt,
double mzx, double mzy, double mzz, double mzt)
{
if (mxz == 0.0 && myz == 0.0 &&
mzx == 0.0 && mzy == 0.0 && mzz == 1.0 && mzt == 0.0)
{
return getInstance(mxx, myx, mxy, myy, mxt, myt);
} else {
return new Affine3D(mxx, mxy, mxz, mxt,
myx, myy, myz, myt,
mzx, mzy, mzz, mzt);
}
}
public static BaseTransform getInstance(double mxx, double myx,
double mxy, double myy,
double mxt, double myt)
{
if (mxx == 1.0 && myx == 0.0 && mxy == 0.0 && myy == 1.0) {
return getTranslateInstance(mxt, myt);
} else {
return new Affine2D(mxx, myx, mxy, myy, mxt, myt);
}
}
public static BaseTransform getTranslateInstance(double mxt, double myt) {
if (mxt == 0.0 && myt == 0.0) {
return IDENTITY_TRANSFORM;
} else {
return new Translate2D(mxt, myt);
}
}
public static BaseTransform getScaleInstance(double mxx, double myy) {
return getInstance(mxx, 0, 0, myy, 0, 0);
}
public static BaseTransform getRotateInstance(double theta, double x, double y) {
Affine2D a = new Affine2D();
a.setToRotation(theta, x, y);
return a;
}
public abstract Degree getDegree();
public abstract int getType();
public abstract boolean isIdentity();
public abstract boolean isTranslateOrIdentity();
public abstract boolean is2D();
public abstract double getDeterminant();
public double getMxx() { return 1.0; }
public double getMxy() { return 0.0; }
public double getMxz() { return 0.0; }
public double getMxt() { return 0.0; }
public double getMyx() { return 0.0; }
public double getMyy() { return 1.0; }
public double getMyz() { return 0.0; }
public double getMyt() { return 0.0; }
public double getMzx() { return 0.0; }
public double getMzy() { return 0.0; }
public double getMzz() { return 1.0; }
public double getMzt() { return 0.0; }
public abstract Point2D transform(Point2D src, Point2D dst);
public abstract Point2D inverseTransform(Point2D src, Point2D dst)
throws NoninvertibleTransformException;
public abstract Vec3d transform(Vec3d src, Vec3d dst);
public abstract Vec3d deltaTransform(Vec3d src, Vec3d dst);
public abstract Vec3d inverseTransform(Vec3d src, Vec3d dst)
throws NoninvertibleTransformException;
public abstract Vec3d inverseDeltaTransform(Vec3d src, Vec3d dst)
throws NoninvertibleTransformException;
public abstract void transform(float[] srcPts, int srcOff,
float[] dstPts, int dstOff,
int numPts);
public abstract void transform(double[] srcPts, int srcOff,
double[] dstPts, int dstOff,
int numPts);
public abstract void transform(float[] srcPts, int srcOff,
double[] dstPts, int dstOff,
int numPts);
public abstract void transform(double[] srcPts, int srcOff,
float[] dstPts, int dstOff,
int numPts);
public abstract void deltaTransform(float[] srcPts, int srcOff,
float[] dstPts, int dstOff,
int numPts);
public abstract void deltaTransform(double[] srcPts, int srcOff,
double[] dstPts, int dstOff,
int numPts);
public abstract void inverseTransform(float[] srcPts, int srcOff,
float[] dstPts, int dstOff,
int numPts)
throws NoninvertibleTransformException;
public abstract void inverseDeltaTransform(float[] srcPts, int srcOff,
float[] dstPts, int dstOff,
int numPts)
throws NoninvertibleTransformException;
public abstract void inverseTransform(double[] srcPts, int srcOff,
double[] dstPts, int dstOff,
int numPts)
throws NoninvertibleTransformException;
public abstract BaseBounds transform(BaseBounds bounds, BaseBounds result);
public abstract void transform(Rectangle rect, Rectangle result);
public abstract BaseBounds inverseTransform(BaseBounds bounds, BaseBounds result)
throws NoninvertibleTransformException;
public abstract void inverseTransform(Rectangle rect, Rectangle result)
throws NoninvertibleTransformException;
public abstract Shape createTransformedShape(Shape s);
public abstract void setToIdentity();
public abstract void setTransform(BaseTransform xform);
public abstract void invert() throws NoninvertibleTransformException;
public abstract void restoreTransform(double mxx, double myx,
double mxy, double myy,
double mxt, double myt);
public abstract void restoreTransform(double mxx, double mxy, double mxz, double mxt,
double myx, double myy, double myz, double myt,
double mzx, double mzy, double mzz, double mzt);
public abstract BaseTransform deriveWithTranslation(double mxt, double myt);
public abstract BaseTransform deriveWithTranslation(double mxt, double myt, double mzt);
public abstract BaseTransform deriveWithScale(double mxx, double myy, double mzz);
public abstract BaseTransform deriveWithRotation(double theta, double axisX, double axisY, double axisZ);
public abstract BaseTransform deriveWithPreTranslation(double mxt, double myt);
public abstract BaseTransform deriveWithConcatenation(double mxx, double myx,
double mxy, double myy,
double mxt, double myt);
public abstract BaseTransform deriveWithConcatenation(
double mxx, double mxy, double mxz, double mxt,
double myx, double myy, double myz, double myt,
double mzx, double mzy, double mzz, double mzt);
public abstract BaseTransform deriveWithPreConcatenation(BaseTransform transform);
public abstract BaseTransform deriveWithConcatenation(BaseTransform tx);
public abstract BaseTransform deriveWithNewTransform(BaseTransform tx);
public abstract BaseTransform createInverse()
throws NoninvertibleTransformException;
public abstract BaseTransform copy();
@Override
public int hashCode() {
if (isIdentity()) return 0;
long bits = 0;
bits = bits * 31 + Double.doubleToLongBits(getMzz());
bits = bits * 31 + Double.doubleToLongBits(getMzy());
bits = bits * 31 + Double.doubleToLongBits(getMzx());
bits = bits * 31 + Double.doubleToLongBits(getMyz());
bits = bits * 31 + Double.doubleToLongBits(getMxz());
bits = bits * 31 + Double.doubleToLongBits(getMyy());
bits = bits * 31 + Double.doubleToLongBits(getMyx());
bits = bits * 31 + Double.doubleToLongBits(getMxy());
bits = bits * 31 + Double.doubleToLongBits(getMxx());
bits = bits * 31 + Double.doubleToLongBits(getMzt());
bits = bits * 31 + Double.doubleToLongBits(getMyt());
bits = bits * 31 + Double.doubleToLongBits(getMxt());
return (((int) bits) ^ ((int) (bits >> 32)));
}
@Override
public boolean equals(Object obj) {
if (!(obj instanceof BaseTransform)) {
return false;
}
BaseTransform a = (BaseTransform) obj;
return (getMxx() == a.getMxx() &&
getMxy() == a.getMxy() &&
getMxz() == a.getMxz() &&
getMxt() == a.getMxt() &&
getMyx() == a.getMyx() &&
getMyy() == a.getMyy() &&
getMyz() == a.getMyz() &&
getMyt() == a.getMyt() &&
getMzx() == a.getMzx() &&
getMzy() == a.getMzy() &&
getMzz() == a.getMzz() &&
getMzt() == a.getMzt());
}
static Point2D makePoint(Point2D src, Point2D dst) {
if (dst == null) {
dst = new Point2D();
}
return dst;
}
static final double EPSILON_ABSOLUTE = 1.0e-5;
public static boolean almostZero(double a) {
return ((a < EPSILON_ABSOLUTE) && (a > -EPSILON_ABSOLUTE));
}
@Override
public String toString() {
return "Matrix: degree " + getDegree() + "\n" +
getMxx() + ", " + getMxy() + ", " + getMxz() + ", " + getMxt() + "\n" +
getMyx() + ", " + getMyy() + ", " + getMyz() + ", " + getMyt() + "\n" +
getMzx() + ", " + getMzy() + ", " + getMzz() + ", " + getMzt() + "\n";
}
}
