package javafx.scene.transform;
import java.util.Iterator;
import com.sun.javafx.geometry.BoundsUtils;
import javafx.event.EventDispatchChain;
import javafx.scene.Node;
import com.sun.javafx.util.WeakReferenceQueue;
import com.sun.javafx.binding.ExpressionHelper;
import com.sun.javafx.event.EventHandlerManager;
import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.transform.TransformHelper;
import com.sun.javafx.scene.transform.TransformUtils;
import java.lang.ref.SoftReference;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
public abstract class Transform implements Cloneable, EventTarget {
static {
TransformHelper.setTransformAccessor(new TransformHelper.TransformAccessor() {
@Override
public void add(Transform transform, Node node) {
transform.add(node);
}
@Override
public void remove(Transform transform, Node node) {
transform.remove(node);
}
@Override
public void apply(Transform transform, Affine3D affine3D) {
transform.apply(affine3D);
}
@Override
public BaseTransform derive(Transform transform, BaseTransform baseTransform) {
return transform.derive(baseTransform);
}
@Override
public Transform createImmutableTransform() {
return Transform.createImmutableTransform();
}
@Override
public Transform createImmutableTransform(
double mxx, double mxy, double mxz, double tx,
double myx, double myy, double myz, double ty,
double mzx, double mzy, double mzz, double tz) {
return Transform.createImmutableTransform(mxx, mxy, mxz, tx,
myx, myy, myz, ty, mzx, mzy, mzz, tz);
}
@Override
public Transform createImmutableTransform(Transform transform,
double mxx, double mxy, double mxz, double tx,
double myx, double myy, double myz, double ty,
double mzx, double mzy, double mzz, double tz) {
return Transform.createImmutableTransform(transform,
mxx, mxy, mxz, tx, myx, myy, myz, ty, mzx, mzy, mzz, tz);
}
@Override
public Transform createImmutableTransform(Transform transform,
Transform left, Transform right) {
return Transform.createImmutableTransform(transform, left, right);
}
});
}
public Transform() {
}
public static Affine affine(
double mxx, double myx, double mxy, double myy, double tx, double ty) {
final Affine affine = new Affine();
affine.setMxx(mxx);
affine.setMxy(mxy);
affine.setTx(tx);
affine.setMyx(myx);
affine.setMyy(myy);
affine.setTy(ty);
return affine;
}
public static Affine affine(
double mxx, double mxy, double mxz, double tx,
double myx, double myy, double myz, double ty,
double mzx, double mzy, double mzz, double tz) {
final Affine affine = new Affine();
affine.setMxx(mxx);
affine.setMxy(mxy);
affine.setMxz(mxz);
affine.setTx(tx);
affine.setMyx(myx);
affine.setMyy(myy);
affine.setMyz(myz);
affine.setTy(ty);
affine.setMzx(mzx);
affine.setMzy(mzy);
affine.setMzz(mzz);
affine.setTz(tz);
return affine;
}
public static Translate translate(double x, double y) {
final Translate translate = new Translate();
translate.setX(x);
translate.setY(y);
return translate;
}
public static Rotate rotate(double angle, double pivotX, double pivotY) {
final Rotate rotate = new Rotate();
rotate.setAngle(angle);
rotate.setPivotX(pivotX);
rotate.setPivotY(pivotY);
return rotate;
}
public static Scale scale(double x, double y) {
final Scale scale = new Scale();
scale.setX(x);
scale.setY(y);
return scale;
}
public static Scale scale(double x, double y, double pivotX, double pivotY) {
final Scale scale = new Scale();
scale.setX(x);
scale.setY(y);
scale.setPivotX(pivotX);
scale.setPivotY(pivotY);
return scale;
}
public static Shear shear(double x, double y) {
final Shear shear = new Shear();
shear.setX(x);
shear.setY(y);
return shear;
}
public static Shear shear(double x, double y, double pivotX, double pivotY) {
final Shear shear = new Shear();
shear.setX(x);
shear.setY(y);
shear.setPivotX(pivotX);
shear.setPivotY(pivotY);
return shear;
}
private SoftReference<Transform> inverseCache = null;
private WeakReferenceQueue nodes = new WeakReferenceQueue();
public double getMxx() {
return 1.0;
}
public double getMxy() {
return 0.0;
}
public double getMxz() {
return 0.0;
}
public double getTx() {
return 0.0;
}
public double getMyx() {
return 0.0;
}
public double getMyy() {
return 1.0;
}
public double getMyz() {
return 0.0;
}
public double getTy() {
return 0.0;
}
public double getMzx() {
return 0.0;
}
public double getMzy() {
return 0.0;
}
public double getMzz() {
return 1.0;
}
public double getTz() {
return 0.0;
}
public double getElement(MatrixType type, int row, int column) {
if (row < 0 || row >= type.rows() || column < 0 || column >= type.columns()) {
throw new IndexOutOfBoundsException("Index outside of affine "
+ "matrix " + type + ": [" + row + ", " + column + "]");
}
switch(type) {
case MT_2D_2x3:
case MT_2D_3x3:
if (!isType2D()) {
throw new IllegalArgumentException("Cannot access 2D matrix "
+ "of a 3D transform");
}
switch(row) {
case 0:
switch(column) {
case 0: return getMxx();
case 1: return getMxy();
case 2: return getTx();
}
case 1:
switch(column) {
case 0: return getMyx();
case 1: return getMyy();
case 2: return getTy();
}
case 2:
switch(column) {
case 0: return 0.0;
case 1: return 0.0;
case 2: return 1.0;
}
}
break;
case MT_3D_3x4:
case MT_3D_4x4:
switch(row) {
case 0:
switch(column) {
case 0: return getMxx();
case 1: return getMxy();
case 2: return getMxz();
case 3: return getTx();
}
case 1:
switch(column) {
case 0: return getMyx();
case 1: return getMyy();
case 2: return getMyz();
case 3: return getTy();
}
case 2:
switch(column) {
case 0: return getMzx();
case 1: return getMzy();
case 2: return getMzz();
case 3: return getTz();
}
case 3:
switch(column) {
case 0: return 0.0;
case 1: return 0.0;
case 2: return 0.0;
case 3: return 1.0;
}
}
break;
}
throw new InternalError("Unsupported matrix type " + type);
}
boolean computeIs2D() {
return getMxz() == 0.0 && getMzx() == 0.0 && getMzy() == 0.0 &&
getMzz() == 1.0 && getTz() == 0.0;
}
boolean computeIsIdentity() {
return
getMxx() == 1.0 && getMxy() == 0.0 && getMxz() == 0.0 && getTx() == 0.0 &&
getMyx() == 0.0 && getMyy() == 1.0 && getMyz() == 0.0 && getTy() == 0.0 &&
getMzx() == 0.0 && getMzy() == 0.0 && getMzz() == 1.0 && getTz() == 0.0;
}
public double determinant() {
final double myx = getMyx();
final double myy = getMyy();
final double myz = getMyz();
final double mzx = getMzx();
final double mzy = getMzy();
final double mzz = getMzz();
return (getMxx() * (myy * mzz - mzy * myz) +
getMxy() * (myz * mzx - mzz * myx) +
getMxz() * (myx * mzy - mzx * myy));
}
private LazyBooleanProperty type2D;
public final boolean isType2D() {
return type2D == null ? computeIs2D() : type2D.get();
}
public final ReadOnlyBooleanProperty type2DProperty() {
if (type2D == null) {
type2D = new LazyBooleanProperty() {
@Override
protected boolean computeValue() {
return computeIs2D();
}
@Override
public Object getBean() {
return Transform.this;
}
@Override
public String getName() {
return "type2D";
}
};
}
return type2D;
}
private LazyBooleanProperty identity;
public final boolean isIdentity() {
return identity == null ? computeIsIdentity() : identity.get();
}
public final ReadOnlyBooleanProperty identityProperty() {
if (identity == null) {
identity = new LazyBooleanProperty() {
@Override
protected boolean computeValue() {
return computeIsIdentity();
}
@Override
public Object getBean() {
return Transform.this;
}
@Override
public String getName() {
return "identity";
}
};
}
return identity;
}
private static abstract class LazyBooleanProperty
extends ReadOnlyBooleanProperty {
private ExpressionHelper<Boolean> helper;
private boolean valid;
private boolean value;
@Override
public void addListener(InvalidationListener listener) {
helper = ExpressionHelper.addListener(helper, this, listener);
}
@Override
public void removeListener(InvalidationListener listener) {
helper = ExpressionHelper.removeListener(helper, listener);
}
@Override
public void addListener(ChangeListener<? super Boolean> listener) {
helper = ExpressionHelper.addListener(helper, this, listener);
}
@Override
public void removeListener(ChangeListener<? super Boolean> listener) {
helper = ExpressionHelper.removeListener(helper, listener);
}
@Override
public boolean get() {
if (!valid) {
value = computeValue();
valid = true;
}
return value;
}
public void invalidate() {
if (valid) {
valid = false;
ExpressionHelper.fireValueChangedEvent(helper);
}
}
protected abstract boolean computeValue();
}
private double transformDiff(Transform t, double x, double y) {
final Point2D byThis = transform(x, y);
final Point2D byOther = t.transform(x, y);
return byThis.distance(byOther);
}
private double transformDiff(Transform t, double x, double y, double z) {
final Point3D byThis = transform(x, y, z);
final Point3D byOther = t.transform(x, y, z);
return byThis.distance(byOther);
}
public boolean similarTo(Transform transform, Bounds range, double maxDelta) {
double cornerX, cornerY, cornerZ;
if (isType2D() && transform.isType2D()) {
cornerX = range.getMinX();
cornerY = range.getMinY();
if (transformDiff(transform, cornerX, cornerY) > maxDelta) {
return false;
}
cornerY = range.getMaxY();
if (transformDiff(transform, cornerX, cornerY) > maxDelta) {
return false;
}
cornerX = range.getMaxX();
cornerY = range.getMinY();
if (transformDiff(transform, cornerX, cornerY) > maxDelta) {
return false;
}
cornerY = range.getMaxY();
if (transformDiff(transform, cornerX, cornerY) > maxDelta) {
return false;
}
return true;
}
cornerX = range.getMinX();
cornerY = range.getMinY();
cornerZ = range.getMinZ();
if (transformDiff(transform, cornerX, cornerY, cornerZ) > maxDelta) {
return false;
}
cornerY = range.getMaxY();
if (transformDiff(transform, cornerX, cornerY, cornerZ) > maxDelta) {
return false;
}
cornerX = range.getMaxX();
cornerY = range.getMinY();
if (transformDiff(transform, cornerX, cornerY, cornerZ) > maxDelta) {
return false;
}
cornerY = range.getMaxY();
if (transformDiff(transform, cornerX, cornerY, cornerZ) > maxDelta) {
return false;
}
if (range.getDepth() != 0.0) {
cornerX = range.getMinX();
cornerY = range.getMinY();
cornerZ = range.getMaxZ();
if (transformDiff(transform, cornerX, cornerY, cornerZ) > maxDelta) {
return false;
}
cornerY = range.getMaxY();
if (transformDiff(transform, cornerX, cornerY, cornerZ) > maxDelta) {
return false;
}
cornerX = range.getMaxX();
cornerY = range.getMinY();
if (transformDiff(transform, cornerX, cornerY, cornerZ) > maxDelta) {
return false;
}
cornerY = range.getMaxY();
if (transformDiff(transform, cornerX, cornerY, cornerZ) > maxDelta) {
return false;
}
}
return true;
}
void fill2DArray(double[] array) {
array[0] = getMxx();
array[1] = getMxy();
array[2] = getTx();
array[3] = getMyx();
array[4] = getMyy();
array[5] = getTy();
}
void fill3DArray(double[] array) {
array[0] = getMxx();
array[1] = getMxy();
array[2] = getMxz();
array[3] = getTx();
array[4] = getMyx();
array[5] = getMyy();
array[6] = getMyz();
array[7] = getTy();
array[8] = getMzx();
array[9] = getMzy();
array[10] = getMzz();
array[11] = getTz();
}
public double[] toArray(MatrixType type, double[] array) {
checkRequestedMAT(type);
if (array == null || array.length < type.elements()) {
array = new double[type.elements()];
}
switch (type) {
case MT_2D_3x3:
array[6] = 0.0;
array[7] = 0.0;
array[8] = 1.0;
case MT_2D_2x3:
fill2DArray(array);
break;
case MT_3D_4x4:
array[12] = 0.0;
array[13] = 0.0;
array[14] = 0.0;
array[15] = 1.0;
case MT_3D_3x4:
fill3DArray(array);
break;
default:
throw new InternalError("Unsupported matrix type " + type);
}
return array;
}
public double[] toArray(MatrixType type) {
return toArray(type, null);
}
public double[] row(MatrixType type, int row, double[] array) {
checkRequestedMAT(type);
if (row < 0 || row >= type.rows()) {
throw new IndexOutOfBoundsException(
"Cannot get row " + row + " from " + type);
}
if (array == null || array.length < type.columns()) {
array = new double[type.columns()];
}
switch(type) {
case MT_2D_2x3:
case MT_2D_3x3:
switch (row) {
case 0:
array[0] = getMxx();
array[1] = getMxy();
array[2] = getTx();
break;
case 1:
array[0] = getMyx();
array[1] = getMyy();
array[2] = getTy();
break;
case 2:
array[0] = 0.0;
array[1] = 0.0;
array[2] = 1.0;
break;
}
break;
case MT_3D_3x4:
case MT_3D_4x4:
switch (row) {
case 0:
array[0] = getMxx();
array[1] = getMxy();
array[2] = getMxz();
array[3] = getTx();
break;
case 1:
array[0] = getMyx();
array[1] = getMyy();
array[2] = getMyz();
array[3] = getTy();
break;
case 2:
array[0] = getMzx();
array[1] = getMzy();
array[2] = getMzz();
array[3] = getTz();
break;
case 3:
array[0] = 0.0;
array[1] = 0.0;
array[2] = 0.0;
array[3] = 1.0;
break;
}
break;
default:
throw new InternalError("Unsupported row " + row + " of " + type);
}
return array;
}
public double[] row(MatrixType type, int row) {
return row(type, row, null);
}
public double[] column(MatrixType type, int column, double[] array) {
checkRequestedMAT(type);
if (column < 0 || column >= type.columns()) {
throw new IndexOutOfBoundsException(
"Cannot get row " + column + " from " + type);
}
if (array == null || array.length < type.rows()) {
array = new double[type.rows()];
}
switch(type) {
case MT_2D_2x3:
switch (column) {
case 0:
array[0] = getMxx();
array[1] = getMyx();
break;
case 1:
array[0] = getMxy();
array[1] = getMyy();
break;
case 2:
array[0] = getTx();
array[1] = getTy();
break;
}
break;
case MT_2D_3x3:
switch (column) {
case 0:
array[0] = getMxx();
array[1] = getMyx();
array[2] = 0.0;
break;
case 1:
array[0] = getMxy();
array[1] = getMyy();
array[2] = 0.0;
break;
case 2:
array[0] = getTx();
array[1] = getTy();
array[2] = 1.0;
break;
}
break;
case MT_3D_3x4:
switch (column) {
case 0:
array[0] = getMxx();
array[1] = getMyx();
array[2] = getMzx();
break;
case 1:
array[0] = getMxy();
array[1] = getMyy();
array[2] = getMzy();
break;
case 2:
array[0] = getMxz();
array[1] = getMyz();
array[2] = getMzz();
break;
case 3:
array[0] = getTx();
array[1] = getTy();
array[2] = getTz();
break;
}
break;
case MT_3D_4x4:
switch (column) {
case 0:
array[0] = getMxx();
array[1] = getMyx();
array[2] = getMzx();
array[3] = 0.0;
break;
case 1:
array[0] = getMxy();
array[1] = getMyy();
array[2] = getMzy();
array[3] = 0.0;
break;
case 2:
array[0] = getMxz();
array[1] = getMyz();
array[2] = getMzz();
array[3] = 0.0;
break;
case 3:
array[0] = getTx();
array[1] = getTy();
array[2] = getTz();
array[3] = 1.0;
break;
}
break;
default:
throw new InternalError("Unsupported column " + column + " of "
+ type);
}
return array;
}
public double[] column(MatrixType type, int column) {
return column(type, column, null);
}
public Transform createConcatenation(Transform transform) {
final double txx = transform.getMxx();
final double txy = transform.getMxy();
final double txz = transform.getMxz();
final double ttx = transform.getTx();
final double tyx = transform.getMyx();
final double tyy = transform.getMyy();
final double tyz = transform.getMyz();
final double tty = transform.getTy();
final double tzx = transform.getMzx();
final double tzy = transform.getMzy();
final double tzz = transform.getMzz();
final double ttz = transform.getTz();
return new Affine(
(getMxx() * txx + getMxy() * tyx + getMxz() * tzx),
(getMxx() * txy + getMxy() * tyy + getMxz() * tzy),
(getMxx() * txz + getMxy() * tyz + getMxz() * tzz),
(getMxx() * ttx + getMxy() * tty + getMxz() * ttz + getTx()),
(getMyx() * txx + getMyy() * tyx + getMyz() * tzx),
(getMyx() * txy + getMyy() * tyy + getMyz() * tzy),
(getMyx() * txz + getMyy() * tyz + getMyz() * tzz),
(getMyx() * ttx + getMyy() * tty + getMyz() * ttz + getTy()),
(getMzx() * txx + getMzy() * tyx + getMzz() * tzx),
(getMzx() * txy + getMzy() * tyy + getMzz() * tzy),
(getMzx() * txz + getMzy() * tyz + getMzz() * tzz),
(getMzx() * ttx + getMzy() * tty + getMzz() * ttz + getTz()));
}
public Transform createInverse() throws NonInvertibleTransformException {
return getInverseCache().clone();
}
@Override
public Transform clone() {
return TransformUtils.immutableTransform(this);
}
public Point2D transform(double x, double y) {
ensureCanTransform2DPoint();
return new Point2D(
getMxx() * x + getMxy() * y + getTx(),
getMyx() * x + getMyy() * y + getTy());
}
public Point2D transform(Point2D point) {
return transform(point.getX(), point.getY());
}
public Point3D transform(double x, double y, double z) {
return new Point3D(
getMxx() * x + getMxy() * y + getMxz() * z + getTx(),
getMyx() * x + getMyy() * y + getMyz() * z + getTy(),
getMzx() * x + getMzy() * y + getMzz() * z + getTz());
}
public Point3D transform(Point3D point) {
return transform(point.getX(), point.getY(), point.getZ());
}
public Bounds transform(Bounds bounds) {
if (isType2D() && (bounds.getMinZ() == 0) && (bounds.getMaxZ() == 0)) {
Point2D p1 = transform(bounds.getMinX(), bounds.getMinY());
Point2D p2 = transform(bounds.getMaxX(), bounds.getMinY());
Point2D p3 = transform(bounds.getMaxX(), bounds.getMaxY());
Point2D p4 = transform(bounds.getMinX(), bounds.getMaxY());
return BoundsUtils.createBoundingBox(p1, p2, p3, p4);
}
Point3D p1 = transform(bounds.getMinX(), bounds.getMinY(), bounds.getMinZ());
Point3D p2 = transform(bounds.getMinX(), bounds.getMinY(), bounds.getMaxZ());
Point3D p3 = transform(bounds.getMinX(), bounds.getMaxY(), bounds.getMinZ());
Point3D p4 = transform(bounds.getMinX(), bounds.getMaxY(), bounds.getMaxZ());
Point3D p5 = transform(bounds.getMaxX(), bounds.getMaxY(), bounds.getMinZ());
Point3D p6 = transform(bounds.getMaxX(), bounds.getMaxY(), bounds.getMaxZ());
Point3D p7 = transform(bounds.getMaxX(), bounds.getMinY(), bounds.getMinZ());
Point3D p8 = transform(bounds.getMaxX(), bounds.getMinY(), bounds.getMaxZ());
return BoundsUtils.createBoundingBox(p1, p2, p3, p4, p5, p6, p7, p8);
}
void transform2DPointsImpl(double[] srcPts, int srcOff,
double[] dstPts, int dstOff, int numPts) {
final double xx = getMxx();
final double xy = getMxy();
final double tx = getTx();
final double yx = getMyx();
final double yy = getMyy();
final double ty = getTy();
while (--numPts >= 0) {
final double x = srcPts[srcOff++];
final double y = srcPts[srcOff++];
dstPts[dstOff++] = xx * x + xy * y + tx;
dstPts[dstOff++] = yx * x + yy * y + ty;
}
}
void transform3DPointsImpl(double[] srcPts, int srcOff,
double[] dstPts, int dstOff, int numPts) {
final double xx = getMxx();
final double xy = getMxy();
final double xz = getMxz();
final double tx = getTx();
final double yx = getMyx();
final double yy = getMyy();
final double yz = getMyz();
final double ty = getTy();
final double zx = getMzx();
final double zy = getMzy();
final double zz = getMzz();
final double tz = getTz();
while (--numPts >= 0) {
final double x = srcPts[srcOff++];
final double y = srcPts[srcOff++];
final double z = srcPts[srcOff++];
dstPts[dstOff++] = xx * x + xy * y + xz * z + tx;
dstPts[dstOff++] = yx * x + yy * y + yz * z + ty;
dstPts[dstOff++] = zx * x + zy * y + zz * z + tz;
}
}
public void transform2DPoints(double[] srcPts, int srcOff,
double[] dstPts, int dstOff,
int numPts) {
if (srcPts == null || dstPts == null) {
throw new NullPointerException();
}
if (!isType2D()) {
throw new IllegalStateException("Cannot transform 2D points "
+ "with a 3D transform");
}
srcOff = getFixedSrcOffset(srcPts, srcOff, dstPts, dstOff, numPts, 2);
transform2DPointsImpl(srcPts, srcOff, dstPts, dstOff, numPts);
}
public void transform3DPoints(double[] srcPts, int srcOff,
double[] dstPts, int dstOff,
int numPts) {
if (srcPts == null || dstPts == null) {
throw new NullPointerException();
}
srcOff = getFixedSrcOffset(srcPts, srcOff, dstPts, dstOff, numPts, 3);
transform3DPointsImpl(srcPts, srcOff, dstPts, dstOff, numPts);
}
public Point2D deltaTransform(double x, double y) {
ensureCanTransform2DPoint();
return new Point2D(
getMxx() * x + getMxy() * y,
getMyx() * x + getMyy() * y);
}
public Point2D deltaTransform(Point2D point) {
return deltaTransform(point.getX(), point.getY());
}
public Point3D deltaTransform(double x, double y, double z) {
return new Point3D(
getMxx() * x + getMxy() * y + getMxz() * z,
getMyx() * x + getMyy() * y + getMyz() * z,
getMzx() * x + getMzy() * y + getMzz() * z);
}
public Point3D deltaTransform(Point3D point) {
return deltaTransform(point.getX(), point.getY(), point.getZ());
}
public Point2D inverseTransform(double x, double y)
throws NonInvertibleTransformException {
ensureCanTransform2DPoint();
return getInverseCache().transform(x, y);
}
public Point2D inverseTransform(Point2D point)
throws NonInvertibleTransformException {
return inverseTransform(point.getX(), point.getY());
}
public Point3D inverseTransform(double x, double y, double z)
throws NonInvertibleTransformException {
return getInverseCache().transform(x, y, z);
}
public Point3D inverseTransform(Point3D point)
throws NonInvertibleTransformException {
return inverseTransform(point.getX(), point.getY(), point.getZ());
}
public Bounds inverseTransform(Bounds bounds)
throws NonInvertibleTransformException {
if (isType2D() && (bounds.getMinZ() == 0) && (bounds.getMaxZ() == 0)) {
Point2D p1 = inverseTransform(bounds.getMinX(), bounds.getMinY());
Point2D p2 = inverseTransform(bounds.getMaxX(), bounds.getMinY());
Point2D p3 = inverseTransform(bounds.getMaxX(), bounds.getMaxY());
Point2D p4 = inverseTransform(bounds.getMinX(), bounds.getMaxY());
return BoundsUtils.createBoundingBox(p1, p2, p3, p4);
}
Point3D p1 = inverseTransform(bounds.getMinX(), bounds.getMinY(), bounds.getMinZ());
Point3D p2 = inverseTransform(bounds.getMinX(), bounds.getMinY(), bounds.getMaxZ());
Point3D p3 = inverseTransform(bounds.getMinX(), bounds.getMaxY(), bounds.getMinZ());
Point3D p4 = inverseTransform(bounds.getMinX(), bounds.getMaxY(), bounds.getMaxZ());
Point3D p5 = inverseTransform(bounds.getMaxX(), bounds.getMaxY(), bounds.getMinZ());
Point3D p6 = inverseTransform(bounds.getMaxX(), bounds.getMaxY(), bounds.getMaxZ());
Point3D p7 = inverseTransform(bounds.getMaxX(), bounds.getMinY(), bounds.getMinZ());
Point3D p8 = inverseTransform(bounds.getMaxX(), bounds.getMinY(), bounds.getMaxZ());
return BoundsUtils.createBoundingBox(p1, p2, p3, p4, p5, p6, p7, p8);
}
void inverseTransform2DPointsImpl(double[] srcPts, int srcOff,
double[] dstPts, int dstOff, int numPts)
throws NonInvertibleTransformException {
getInverseCache().transform2DPointsImpl(srcPts, srcOff,
dstPts, dstOff, numPts);
}
void inverseTransform3DPointsImpl(double[] srcPts, int srcOff,
double[] dstPts, int dstOff, int numPts)
throws NonInvertibleTransformException {
getInverseCache().transform3DPointsImpl(srcPts, srcOff,
dstPts, dstOff, numPts);
}
public void inverseTransform2DPoints(double[] srcPts, int srcOff,
double[] dstPts, int dstOff,
int numPts) throws NonInvertibleTransformException{
if (srcPts == null || dstPts == null) {
throw new NullPointerException();
}
if (!isType2D()) {
throw new IllegalStateException("Cannot transform 2D points "
+ "with a 3D transform");
}
srcOff = getFixedSrcOffset(srcPts, srcOff, dstPts, dstOff, numPts, 2);
inverseTransform2DPointsImpl(srcPts, srcOff, dstPts, dstOff, numPts);
}
public void inverseTransform3DPoints(double[] srcPts, int srcOff,
double[] dstPts, int dstOff,
int numPts) throws NonInvertibleTransformException {
if (srcPts == null || dstPts == null) {
throw new NullPointerException();
}
srcOff = getFixedSrcOffset(srcPts, srcOff, dstPts, dstOff, numPts, 3);
inverseTransform3DPointsImpl(srcPts, srcOff, dstPts, dstOff, numPts);
}
public Point2D inverseDeltaTransform(double x, double y)
throws NonInvertibleTransformException {
ensureCanTransform2DPoint();
return getInverseCache().deltaTransform(x, y);
}
public Point2D inverseDeltaTransform(Point2D point)
throws NonInvertibleTransformException {
return inverseDeltaTransform(point.getX(), point.getY());
}
public Point3D inverseDeltaTransform(double x, double y, double z)
throws NonInvertibleTransformException {
return getInverseCache().deltaTransform(x, y, z);
}
public Point3D inverseDeltaTransform(Point3D point)
throws NonInvertibleTransformException {
return inverseDeltaTransform(point.getX(), point.getY(), point.getZ());
}
private int getFixedSrcOffset(double[] srcPts, int srcOff,
double[] dstPts, int dstOff,
int numPts, int dimensions) {
if (dstPts == srcPts &&
dstOff > srcOff && dstOff < srcOff + numPts * dimensions)
{
System.arraycopy(srcPts, srcOff, dstPts, dstOff, numPts * dimensions);
return dstOff;
}
return srcOff;
}
private EventHandlerManager internalEventDispatcher;
private EventHandlerManager getInternalEventDispatcher() {
if (internalEventDispatcher == null) {
internalEventDispatcher = new EventHandlerManager(this);
}
return internalEventDispatcher;
}
private ObjectProperty<EventHandler<? super TransformChangedEvent>>
onTransformChanged;
@Override
public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
return internalEventDispatcher == null
? tail : tail.append(getInternalEventDispatcher());
}
public final <T extends Event> void addEventHandler(
final EventType<T> eventType,
final EventHandler<? super T> eventHandler) {
getInternalEventDispatcher()
.addEventHandler(eventType, eventHandler);
validate();
}
public final <T extends Event> void removeEventHandler(
final EventType<T> eventType,
final EventHandler<? super T> eventHandler) {
getInternalEventDispatcher()
.removeEventHandler(eventType, eventHandler);
}
public final <T extends Event> void addEventFilter(
final EventType<T> eventType,
final EventHandler<? super T> eventFilter) {
getInternalEventDispatcher()
.addEventFilter(eventType, eventFilter);
validate();
}
public final <T extends Event> void removeEventFilter(
final EventType<T> eventType,
final EventHandler<? super T> eventFilter) {
getInternalEventDispatcher()
.removeEventFilter(eventType, eventFilter);
}
public final void setOnTransformChanged(
EventHandler<? super TransformChangedEvent> value) {
onTransformChangedProperty().set(value);
validate();
}
public final EventHandler<? super TransformChangedEvent> getOnTransformChanged() {
return (onTransformChanged == null) ? null : onTransformChanged.get();
}
public final ObjectProperty<EventHandler<? super TransformChangedEvent>>
onTransformChangedProperty() {
if (onTransformChanged == null) {
onTransformChanged = new SimpleObjectProperty<EventHandler
<? super TransformChangedEvent>>(this, "onTransformChanged") {
@Override protected void invalidated() {
getInternalEventDispatcher().setEventHandler(
TransformChangedEvent.TRANSFORM_CHANGED, get());
}
};
}
return onTransformChanged;
}
void checkRequestedMAT(MatrixType type) throws IllegalArgumentException{
if (type.is2D() && !isType2D()) {
throw new IllegalArgumentException("Cannot access 2D matrix "
+ "for a 3D transform");
}
}
void ensureCanTransform2DPoint() throws IllegalStateException {
if (!isType2D()) {
throw new IllegalStateException("Cannot transform 2D point "
+ "with a 3D transform");
}
}
void validate() {
getMxx(); getMxy(); getMxz(); getTx();
getMyx(); getMyy(); getMyz(); getTy();
getMzx(); getMzy(); getMzz(); getTz();
}
abstract void apply(Affine3D t);
abstract BaseTransform derive(BaseTransform t);
void add(final Node node) {
nodes.add(node);
}
void remove(final Node node) {
nodes.remove(node);
}
protected void transformChanged() {
inverseCache = null;
final Iterator iterator = nodes.iterator();
while (iterator.hasNext()) {
NodeHelper.transformsChanged(((Node) iterator.next()));
}
if (type2D != null) {
type2D.invalidate();
}
if (identity != null) {
identity.invalidate();
}
if (internalEventDispatcher != null) {
validate();
Event.fireEvent(this, new TransformChangedEvent(this, this));
}
}
void appendTo(Affine a) {
a.append(getMxx(), getMxy(), getMxz(), getTx(),
getMyx(), getMyy(), getMyz(), getTy(),
getMzx(), getMzy(), getMzz(), getTz());
}
void prependTo(Affine a) {
a.prepend(getMxx(), getMxy(), getMxz(), getTx(),
getMyx(), getMyy(), getMyz(), getTy(),
getMzx(), getMzy(), getMzz(), getTz());
}
private Transform getInverseCache() throws NonInvertibleTransformException {
if (inverseCache == null || inverseCache.get() == null) {
Affine inv = new Affine(
getMxx(), getMxy(), getMxz(), getTx(),
getMyx(), getMyy(), getMyz(), getTy(),
getMzx(), getMzy(), getMzz(), getTz());
inv.invert();
inverseCache = new SoftReference<Transform>(inv);
return inv;
}
return inverseCache.get();
}
void clearInverseCache() {
if (inverseCache != null) {
inverseCache.clear();
}
}
static Transform createImmutableTransform() {
return new ImmutableTransform();
}
static Transform createImmutableTransform(
double mxx, double mxy, double mxz, double tx,
double myx, double myy, double myz, double ty,
double mzx, double mzy, double mzz, double tz) {
return new ImmutableTransform(
mxx, mxy, mxz, tx,
myx, myy, myz, ty,
mzx, mzy, mzz, tz);
}
static Transform createImmutableTransform(Transform transform,
double mxx, double mxy, double mxz, double tx,
double myx, double myy, double myz, double ty,
double mzx, double mzy, double mzz, double tz) {
if (transform == null) {
return new ImmutableTransform(
mxx, mxy, mxz, tx,
myx, myy, myz, ty,
mzx, mzy, mzz, tz);
}
((Transform.ImmutableTransform) transform).setToTransform(
mxx, mxy, mxz, tx,
myx, myy, myz, ty,
mzx, mzy, mzz, tz);
return transform;
}
static Transform createImmutableTransform(Transform transform,
Transform left, Transform right) {
if (transform == null) {
transform = new ImmutableTransform();
}
((Transform.ImmutableTransform) transform).setToConcatenation(
(ImmutableTransform) left, (ImmutableTransform) right);
return transform;
}
static class ImmutableTransform extends Transform {
private static final int APPLY_IDENTITY = 0;
private static final int APPLY_TRANSLATE = 1;
private static final int APPLY_SCALE = 2;
private static final int APPLY_SHEAR = 4;
private static final int APPLY_NON_3D = 0;
private static final int APPLY_3D_COMPLEX = 4;
private transient int state2d;
private transient int state3d;
private double xx;
private double xy;
private double xz;
private double yx;
private double yy;
private double yz;
private double zx;
private double zy;
private double zz;
private double xt;
private double yt;
private double zt;
ImmutableTransform() {
xx = yy = zz = 1.0;
}
ImmutableTransform(Transform transform) {
this(transform.getMxx(), transform.getMxy(), transform.getMxz(),
transform.getTx(),
transform.getMyx(), transform.getMyy(), transform.getMyz(),
transform.getTy(),
transform.getMzx(), transform.getMzy(), transform.getMzz(),
transform.getTz());
}
ImmutableTransform(double mxx, double mxy, double mxz, double tx,
double myx, double myy, double myz, double ty,
double mzx, double mzy, double mzz, double tz) {
xx = mxx;
xy = mxy;
xz = mxz;
xt = tx;
yx = myx;
yy = myy;
yz = myz;
yt = ty;
zx = mzx;
zy = mzy;
zz = mzz;
zt = tz;
updateState();
}
private void setToTransform(double mxx, double mxy, double mxz, double tx,
double myx, double myy, double myz, double ty,
double mzx, double mzy, double mzz, double tz)
{
xx = mxx;
xy = mxy;
xz = mxz;
xt = tx;
yx = myx;
yy = myy;
yz = myz;
yt = ty;
zx = mzx;
zy = mzy;
zz = mzz;
zt = tz;
updateState();
}
private void setToConcatenation(ImmutableTransform left, ImmutableTransform right) {
if (left.state3d == APPLY_NON_3D && right.state3d == APPLY_NON_3D) {
xx = left.xx * right.xx + left.xy * right.yx;
xy = left.xx * right.xy + left.xy * right.yy;
xt = left.xx * right.xt + left.xy * right.yt + left.xt;
yx = left.yx * right.xx + left.yy * right.yx;
yy = left.yx * right.xy + left.yy * right.yy;
yt = left.yx * right.xt + left.yy * right.yt + left.yt;
if (state3d != APPLY_NON_3D) {
xz = yz = zx = zy = zt = 0.0;
zz = 1.0;
state3d = APPLY_NON_3D;
}
updateState2D();
} else {
xx = left.xx * right.xx + left.xy * right.yx + left.xz * right.zx;
xy = left.xx * right.xy + left.xy * right.yy + left.xz * right.zy;
xz = left.xx * right.xz + left.xy * right.yz + left.xz * right.zz;
xt = left.xx * right.xt + left.xy * right.yt + left.xz * right.zt + left.xt;
yx = left.yx * right.xx + left.yy * right.yx + left.yz * right.zx;
yy = left.yx * right.xy + left.yy * right.yy + left.yz * right.zy;
yz = left.yx * right.xz + left.yy * right.yz + left.yz * right.zz;
yt = left.yx * right.xt + left.yy * right.yt + left.yz * right.zt + left.yt;
zx = left.zx * right.xx + left.zy * right.yx + left.zz * right.zx;
zy = left.zx * right.xy + left.zy * right.yy + left.zz * right.zy;
zz = left.zx * right.xz + left.zy * right.yz + left.zz * right.zz;
zt = left.zx * right.xt + left.zy * right.yt + left.zz * right.zt + left.zt;
updateState();
}
}
@Override
public double getMxx() {
return xx;
}
@Override
public double getMxy() {
return xy;
}
@Override
public double getMxz() {
return xz;
}
@Override
public double getTx() {
return xt;
}
@Override
public double getMyx() {
return yx;
}
@Override
public double getMyy() {
return yy;
}
@Override
public double getMyz() {
return yz;
}
@Override
public double getTy() {
return yt;
}
@Override
public double getMzx() {
return zx;
}
@Override
public double getMzy() {
return zy;
}
@Override
public double getMzz() {
return zz;
}
@Override
public double getTz() {
return zt;
}
@Override
public double determinant() {
switch(state3d) {
default:
stateError();
case APPLY_NON_3D:
switch (state2d) {
default:
stateError();
case APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE:
case APPLY_SHEAR | APPLY_SCALE:
return xx * yy - xy * yx;
case APPLY_SHEAR | APPLY_TRANSLATE:
case APPLY_SHEAR:
return -(xy* yx);
case APPLY_SCALE | APPLY_TRANSLATE:
case APPLY_SCALE:
return xx * yy;
case APPLY_TRANSLATE:
case APPLY_IDENTITY:
return 1.0;
}
case APPLY_TRANSLATE:
return 1.0;
case APPLY_SCALE:
case APPLY_SCALE | APPLY_TRANSLATE:
return xx * yy * zz;
case APPLY_3D_COMPLEX:
return (xx* (yy * zz - zy * yz) +
xy* (yz * zx - zz * yx) +
xz* (yx * zy - zx * yy));
}
}
@Override
public Transform createConcatenation(Transform transform) {
javafx.scene.transform.Affine a = new Affine(this);
a.append(transform);
return a;
}
@Override
public javafx.scene.transform.Affine createInverse() throws NonInvertibleTransformException {
javafx.scene.transform.Affine t = new Affine(this);
t.invert();
return t;
}
@Override
public Transform clone() {
return new ImmutableTransform(this);
}
@Override
public Point2D transform(double x, double y) {
ensureCanTransform2DPoint();
switch (state2d) {
default:
stateError();
case APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE:
return new Point2D(
xx * x + xy * y + xt,
yx * x + yy * y + yt);
case APPLY_SHEAR | APPLY_SCALE:
return new Point2D(
xx * x + xy * y,
yx * x + yy * y);
case APPLY_SHEAR | APPLY_TRANSLATE:
return new Point2D(
xy * y + xt,
yx * x + yt);
case APPLY_SHEAR:
return new Point2D(xy * y, yx * x);
case APPLY_SCALE | APPLY_TRANSLATE:
return new Point2D(
xx * x + xt,
yy * y + yt);
case APPLY_SCALE:
return new Point2D(xx * x, yy * y);
case APPLY_TRANSLATE:
return new Point2D(x + xt, y + yt);
case APPLY_IDENTITY:
return new Point2D(x, y);
}
}
@Override
public Point3D transform(double x, double y, double z) {
switch (state3d) {
default:
stateError();
case APPLY_NON_3D:
switch (state2d) {
default:
stateError();
case APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE:
return new Point3D(
xx * x + xy * y + xt,
yx * x + yy * y + yt, z);
case APPLY_SHEAR | APPLY_SCALE:
return new Point3D(
xx * x + xy * y,
yx * x + yy * y, z);
case APPLY_SHEAR | APPLY_TRANSLATE:
return new Point3D(
xy * y + xt, yx * x + yt,
z);
case APPLY_SHEAR:
return new Point3D(xy * y, yx * x, z);
case APPLY_SCALE | APPLY_TRANSLATE:
return new Point3D(
xx * x + xt, yy * y + yt,
z);
case APPLY_SCALE:
return new Point3D(xx * x, yy * y, z);
case APPLY_TRANSLATE:
return new Point3D(x + xt, y + yt, z);
case APPLY_IDENTITY:
return new Point3D(x, y, z);
}
case APPLY_TRANSLATE:
return new Point3D(x + xt, y + yt, z + zt);
case APPLY_SCALE:
return new Point3D(xx * x, yy * y, zz * z);
case APPLY_SCALE | APPLY_TRANSLATE:
return new Point3D(
xx * x + xt,
yy * y + yt,
zz * z + zt);
case APPLY_3D_COMPLEX:
return new Point3D(
xx * x + xy * y + xz * z + xt,
yx * x + yy * y + yz * z + yt,
zx * x + zy * y + zz * z + zt);
}
}
@Override
public Point2D deltaTransform(double x, double y) {
ensureCanTransform2DPoint();
switch (state2d) {
default:
stateError();
case APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE:
case APPLY_SHEAR | APPLY_SCALE:
return new Point2D(
xx * x + xy * y,
yx * x + yy * y);
case APPLY_SHEAR | APPLY_TRANSLATE:
case APPLY_SHEAR:
return new Point2D(xy * y, yx * x);
case APPLY_SCALE | APPLY_TRANSLATE:
case APPLY_SCALE:
return new Point2D(xx * x, yy * y);
case APPLY_TRANSLATE:
case APPLY_IDENTITY:
return new Point2D(x, y);
}
}
@Override
public Point3D deltaTransform(double x, double y, double z) {
switch (state3d) {
default:
stateError();
case APPLY_NON_3D:
switch (state2d) {
default:
stateError();
case APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE:
case APPLY_SHEAR | APPLY_SCALE:
return new Point3D(
xx * x + xy * y,
yx * x + yy * y, z);
case APPLY_SHEAR | APPLY_TRANSLATE:
case APPLY_SHEAR:
return new Point3D(xy * y, yx * x, z);
case APPLY_SCALE | APPLY_TRANSLATE:
case APPLY_SCALE:
return new Point3D(xx * x, yy * y, z);
case APPLY_TRANSLATE:
case APPLY_IDENTITY:
return new Point3D(x, y, z);
}
case APPLY_TRANSLATE:
return new Point3D(x, y, z);
case APPLY_SCALE:
case APPLY_SCALE | APPLY_TRANSLATE:
return new Point3D(xx * x, yy * y, zz * z);
case APPLY_3D_COMPLEX:
return new Point3D(
xx * x + xy * y + xz * z,
yx * x + yy * y + yz * z,
zx * x + zy * y + zz * z);
}
}
@Override
public Point2D inverseTransform(double x, double y)
throws NonInvertibleTransformException {
ensureCanTransform2DPoint();
switch (state2d) {
default:
return super.inverseTransform(x, y);
case APPLY_SHEAR | APPLY_TRANSLATE:
if (xy == 0.0 || yx == 0.0) {
throw new NonInvertibleTransformException("Determinant is 0");
}
return new Point2D(
(1.0 / yx) * y - yt / yx,
(1.0 / xy) * x - xt / xy);
case APPLY_SHEAR:
if (xy == 0.0 || yx == 0.0) {
throw new NonInvertibleTransformException("Determinant is 0");
}
return new Point2D((1.0 / yx) * y, (1.0 / xy) * x);
case APPLY_SCALE | APPLY_TRANSLATE:
if (xx == 0.0 || yy == 0.0) {
throw new NonInvertibleTransformException("Determinant is 0");
}
return new Point2D(
(1.0 / xx) * x - xt / xx,
(1.0 / yy) * y - yt / yy);
case APPLY_SCALE:
if (xx == 0.0 || yy == 0.0) {
throw new NonInvertibleTransformException("Determinant is 0");
}
return new Point2D((1.0 / xx) * x, (1.0 / yy) * y);
case APPLY_TRANSLATE:
return new Point2D(x - xt, y - yt);
case APPLY_IDENTITY:
return new Point2D(x, y);
}
}
@Override
public Point3D inverseTransform(double x, double y, double z)
throws NonInvertibleTransformException {
switch(state3d) {
default:
stateError();
case APPLY_NON_3D:
switch (state2d) {
default:
return super.inverseTransform(x, y, z);
case APPLY_SHEAR | APPLY_TRANSLATE:
if (xy == 0.0 || yx == 0.0) {
throw new NonInvertibleTransformException(
"Determinant is 0");
}
return new Point3D(
(1.0 / yx) * y - yt / yx,
(1.0 / xy) * x - xt / xy, z);
case APPLY_SHEAR:
if (xy == 0.0 || yx == 0.0) {
throw new NonInvertibleTransformException(
"Determinant is 0");
}
return new Point3D(
(1.0 / yx) * y,
(1.0 / xy) * x, z);
case APPLY_SCALE | APPLY_TRANSLATE:
if (xx == 0.0 || yy == 0.0) {
throw new NonInvertibleTransformException(
"Determinant is 0");
}
return new Point3D(
(1.0 / xx) * x - xt / xx,
(1.0 / yy) * y - yt / yy, z);
case APPLY_SCALE:
if (xx == 0.0 || yy == 0.0) {
throw new NonInvertibleTransformException(
"Determinant is 0");
}
return new Point3D((1.0 / xx) * x, (1.0 / yy) * y, z);
case APPLY_TRANSLATE:
return new Point3D(x - xt, y - yt, z);
case APPLY_IDENTITY:
return new Point3D(x, y, z);
}
case APPLY_TRANSLATE:
return new Point3D(x - xt, y - yt, z - zt);
case APPLY_SCALE:
if (xx == 0.0 || yy == 0.0 || zz == 0.0) {
throw new NonInvertibleTransformException("Determinant is 0");
}
return new Point3D(
(1.0 / xx) * x,
(1.0 / yy) * y,
(1.0 / zz) * z);
case APPLY_SCALE | APPLY_TRANSLATE:
if (xx == 0.0 || yy == 0.0 || zz == 0.0) {
throw new NonInvertibleTransformException("Determinant is 0");
}
return new Point3D(
(1.0 / xx) * x - xt / xx,
(1.0 / yy) * y - yt / yy,
(1.0 / zz) * z - zt / zz);
case APPLY_3D_COMPLEX:
return super.inverseTransform(x, y, z);
}
}
@Override
public Point2D inverseDeltaTransform(double x, double y)
throws NonInvertibleTransformException {
ensureCanTransform2DPoint();
switch (state2d) {
default:
return super.inverseDeltaTransform(x, y);
case APPLY_SHEAR | APPLY_TRANSLATE:
case APPLY_SHEAR:
if (xy == 0.0 || yx == 0.0) {
throw new NonInvertibleTransformException("Determinant is 0");
}
return new Point2D((1.0 / yx) * y, (1.0 / xy) * x);
case APPLY_SCALE | APPLY_TRANSLATE:
case APPLY_SCALE:
if (xx == 0.0 || yy == 0.0) {
throw new NonInvertibleTransformException("Determinant is 0");
}
return new Point2D((1.0 / xx) * x, (1.0 / yy) * y);
case APPLY_TRANSLATE:
case APPLY_IDENTITY:
return new Point2D(x, y);
}
}
@Override
public Point3D inverseDeltaTransform(double x, double y, double z)
throws NonInvertibleTransformException {
switch(state3d) {
default:
stateError();
case APPLY_NON_3D:
switch (state2d) {
default:
return super.inverseDeltaTransform(x, y, z);
case APPLY_SHEAR | APPLY_TRANSLATE:
case APPLY_SHEAR:
if (xy == 0.0 || yx == 0.0) {
throw new NonInvertibleTransformException(
"Determinant is 0");
}
return new Point3D(
(1.0 / yx) * y,
(1.0 / xy) * x, z);
case APPLY_SCALE | APPLY_TRANSLATE:
case APPLY_SCALE:
if (xx == 0.0 || yy == 0.0) {
throw new NonInvertibleTransformException(
"Determinant is 0");
}
return new Point3D(
(1.0 / xx) * x,
(1.0 / yy) * y, z);
case APPLY_TRANSLATE:
case APPLY_IDENTITY:
return new Point3D(x, y, z);
}
case APPLY_TRANSLATE:
return new Point3D(x, y, z);
case APPLY_SCALE | APPLY_TRANSLATE:
case APPLY_SCALE:
if (xx == 0.0 || yy == 0.0 || zz == 0.0) {
throw new NonInvertibleTransformException("Determinant is 0");
}
return new Point3D(
(1.0 / xx) * x,
(1.0 / yy) * y,
(1.0 / zz) * z);
case APPLY_3D_COMPLEX:
return super.inverseDeltaTransform(x, y, z);
}
}
@Override
public String toString() {
final StringBuilder sb = new StringBuilder("Transform [\n");
sb.append("\t").append(xx);
sb.append(", ").append(xy);
sb.append(", ").append(xz);
sb.append(", ").append(xt);
sb.append('\n');
sb.append("\t").append(yx);
sb.append(", ").append(yy);
sb.append(", ").append(yz);
sb.append(", ").append(yt);
sb.append('\n');
sb.append("\t").append(zx);
sb.append(", ").append(zy);
sb.append(", ").append(zz);
sb.append(", ").append(zt);
return sb.append("\n]").toString();
}
private void updateState() {
updateState2D();
state3d = APPLY_NON_3D;
if (xz != 0.0 ||
yz != 0.0 ||
zx != 0.0 ||
zy != 0.0)
{
state3d = APPLY_3D_COMPLEX;
} else {
if ((state2d & APPLY_SHEAR) == 0) {
if (zt != 0.0) {
state3d |= APPLY_TRANSLATE;
}
if (zz != 1.0) {
state3d |= APPLY_SCALE;
}
if (state3d != APPLY_NON_3D) {
state3d |= (state2d & (APPLY_SCALE | APPLY_TRANSLATE));
}
} else {
if (zz != 1.0 || zt != 0.0) {
state3d = APPLY_3D_COMPLEX;
}
}
}
}
private void updateState2D() {
if (xy == 0.0 && yx == 0.0) {
if (xx == 1.0 && yy == 1.0) {
if (xt == 0.0 && yt == 0.0) {
state2d = APPLY_IDENTITY;
} else {
state2d = APPLY_TRANSLATE;
}
} else {
if (xt == 0.0 && yt == 0.0) {
state2d = APPLY_SCALE;
} else {
state2d = (APPLY_SCALE | APPLY_TRANSLATE);
}
}
} else {
if (xx == 0.0 && yy == 0.0) {
if (xt == 0.0 && yt == 0.0) {
state2d = APPLY_SHEAR;
} else {
state2d = (APPLY_SHEAR | APPLY_TRANSLATE);
}
} else {
if (xt == 0.0 && yt == 0.0) {
state2d = (APPLY_SHEAR | APPLY_SCALE);
} else {
state2d = (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE);
}
}
}
}
void ensureCanTransform2DPoint() throws IllegalStateException {
if (state3d != APPLY_NON_3D) {
throw new IllegalStateException("Cannot transform 2D point "
+ "with a 3D transform");
}
}
private static void stateError() {
throw new InternalError("missing case in a switch");
}
@Override
void apply(final Affine3D trans) {
trans.concatenate(xx, xy, xz, xt,
yx, yy, yz, yt,
zx, zy, zz, zt);
}
@Override
BaseTransform derive(final BaseTransform trans) {
return trans.deriveWithConcatenation(xx, xy, xz, xt,
yx, yy, yz, yt,
zx, zy, zz, zt);
}
int getState2d() {
return state2d;
}
int getState3d() {
return state3d;
}
}
}
