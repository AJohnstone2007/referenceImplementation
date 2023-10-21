package javafx.scene.transform;
import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.geom.transform.BaseTransform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
public class Affine extends Transform {
AffineAtomicChange atomicChange = new AffineAtomicChange();
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
public Affine() {
xx = yy = zz = 1.0;
}
public Affine(Transform transform) {
this(transform.getMxx(), transform.getMxy(), transform.getMxz(),
transform.getTx(),
transform.getMyx(), transform.getMyy(), transform.getMyz(),
transform.getTy(),
transform.getMzx(), transform.getMzy(), transform.getMzz(),
transform.getTz());
}
public Affine(double mxx, double mxy, double tx,
double myx, double myy, double ty) {
xx = mxx;
xy = mxy;
xt = tx;
yx = myx;
yy = myy;
yt = ty;
zz = 1.0;
updateState2D();
}
public Affine(double mxx, double mxy, double mxz, double tx,
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
public Affine(double[] matrix, MatrixType type, int offset) {
if (matrix.length < offset + type.elements()) {
throw new IndexOutOfBoundsException("The array is too short.");
}
switch(type) {
default:
stateError();
case MT_2D_3x3:
if (matrix[offset + 6] != 0.0 ||
matrix[offset + 7] != 0.0 ||
matrix[offset + 8] != 1.0) {
throw new IllegalArgumentException("The matrix is "
+ "not affine");
}
case MT_2D_2x3:
xx = matrix[offset++];
xy = matrix[offset++];
xt = matrix[offset++];
yx = matrix[offset++];
yy = matrix[offset++];
yt = matrix[offset];
zz = 1.0;
updateState2D();
return;
case MT_3D_4x4:
if (matrix[offset + 12] != 0.0 ||
matrix[offset + 13] != 0.0 ||
matrix[offset + 14] != 0.0 ||
matrix[offset + 15] != 1.0) {
throw new IllegalArgumentException("The matrix is "
+ "not affine");
}
case MT_3D_3x4:
xx = matrix[offset++];
xy = matrix[offset++];
xz = matrix[offset++];
xt = matrix[offset++];
yx = matrix[offset++];
yy = matrix[offset++];
yz = matrix[offset++];
yt = matrix[offset++];
zx = matrix[offset++];
zy = matrix[offset++];
zz = matrix[offset++];
zt = matrix[offset];
updateState();
return;
}
}
private AffineElementProperty mxx;
public final void setMxx(double value) {
if (mxx == null) {
if (xx != value) {
xx = value;
postProcessChange();
}
} else {
mxxProperty().set(value);
}
}
@Override
public final double getMxx() {
return mxx == null ? xx : mxx.get();
}
public final DoubleProperty mxxProperty() {
if (mxx == null) {
mxx = new AffineElementProperty(xx) {
@Override
public Object getBean() {
return Affine.this;
}
@Override
public String getName() {
return "mxx";
}
};
}
return mxx;
}
private AffineElementProperty mxy;
public final void setMxy(double value) {
if (mxy == null) {
if (xy != value) {
xy = value;
postProcessChange();
}
} else {
mxyProperty().set(value);
}
}
@Override
public final double getMxy() {
return mxy == null ? xy : mxy.get();
}
public final DoubleProperty mxyProperty() {
if (mxy == null) {
mxy = new AffineElementProperty(xy) {
@Override
public Object getBean() {
return Affine.this;
}
@Override
public String getName() {
return "mxy";
}
};
}
return mxy;
}
private AffineElementProperty mxz;
public final void setMxz(double value) {
if (mxz == null) {
if (xz != value) {
xz = value;
postProcessChange();
}
} else {
mxzProperty().set(value);
}
}
@Override
public final double getMxz() {
return mxz == null ? xz : mxz.get();
}
public final DoubleProperty mxzProperty() {
if (mxz == null) {
mxz = new AffineElementProperty(xz) {
@Override
public Object getBean() {
return Affine.this;
}
@Override
public String getName() {
return "mxz";
}
};
}
return mxz;
}
private AffineElementProperty tx;
public final void setTx(double value) {
if (tx == null) {
if (xt != value) {
xt = value;
postProcessChange();
}
} else {
txProperty().set(value);
}
}
@Override
public final double getTx() {
return tx == null ? xt : tx.get();
}
public final DoubleProperty txProperty() {
if (tx == null) {
tx = new AffineElementProperty(xt) {
@Override
public Object getBean() {
return Affine.this;
}
@Override
public String getName() {
return "tx";
}
};
}
return tx;
}
private AffineElementProperty myx;
public final void setMyx(double value) {
if (myx == null) {
if (yx != value) {
yx = value;
postProcessChange();
}
} else {
myxProperty().set(value);
}
}
@Override
public final double getMyx() {
return myx == null ? yx : myx.get();
}
public final DoubleProperty myxProperty() {
if (myx == null) {
myx = new AffineElementProperty(yx) {
@Override
public Object getBean() {
return Affine.this;
}
@Override
public String getName() {
return "myx";
}
};
}
return myx;
}
private AffineElementProperty myy;
public final void setMyy(double value) {
if (myy == null) {
if (yy != value) {
yy = value;
postProcessChange();
}
} else{
myyProperty().set(value);
}
}
@Override
public final double getMyy() {
return myy == null ? yy : myy.get();
}
public final DoubleProperty myyProperty() {
if (myy == null) {
myy = new AffineElementProperty(yy) {
@Override
public Object getBean() {
return Affine.this;
}
@Override
public String getName() {
return "myy";
}
};
}
return myy;
}
private AffineElementProperty myz;
public final void setMyz(double value) {
if (myz == null) {
if (yz != value) {
yz = value;
postProcessChange();
}
} else {
myzProperty().set(value);
}
}
@Override
public final double getMyz() {
return myz == null ? yz : myz.get();
}
public final DoubleProperty myzProperty() {
if (myz == null) {
myz = new AffineElementProperty(yz) {
@Override
public Object getBean() {
return Affine.this;
}
@Override
public String getName() {
return "myz";
}
};
}
return myz;
}
private AffineElementProperty ty;
public final void setTy(double value) {
if (ty == null) {
if (yt != value) {
yt = value;
postProcessChange();
}
} else {
tyProperty().set(value);
}
}
@Override
public final double getTy() {
return ty == null ? yt : ty.get();
}
public final DoubleProperty tyProperty() {
if (ty == null) {
ty = new AffineElementProperty(yt) {
@Override
public Object getBean() {
return Affine.this;
}
@Override
public String getName() {
return "ty";
}
};
}
return ty;
}
private AffineElementProperty mzx;
public final void setMzx(double value) {
if (mzx == null) {
if (zx != value) {
zx = value;
postProcessChange();
}
} else {
mzxProperty().set(value);
}
}
@Override
public final double getMzx() {
return mzx == null ? zx : mzx.get();
}
public final DoubleProperty mzxProperty() {
if (mzx == null) {
mzx = new AffineElementProperty(zx) {
@Override
public Object getBean() {
return Affine.this;
}
@Override
public String getName() {
return "mzx";
}
};
}
return mzx;
}
private AffineElementProperty mzy;
public final void setMzy(double value) {
if (mzy == null) {
if (zy != value) {
zy = value;
postProcessChange();
}
} else {
mzyProperty().set(value);
}
}
@Override
public final double getMzy() {
return mzy == null ? zy : mzy.get();
}
public final DoubleProperty mzyProperty() {
if (mzy == null) {
mzy = new AffineElementProperty(zy) {
@Override
public Object getBean() {
return Affine.this;
}
@Override
public String getName() {
return "mzy";
}
};
}
return mzy;
}
private AffineElementProperty mzz;
public final void setMzz(double value) {
if (mzz == null) {
if (zz != value) {
zz = value;
postProcessChange();
}
} else {
mzzProperty().set(value);
}
}
@Override
public final double getMzz() {
return mzz == null ? zz : mzz.get();
}
public final DoubleProperty mzzProperty() {
if (mzz == null) {
mzz = new AffineElementProperty(zz) {
@Override
public Object getBean() {
return Affine.this;
}
@Override
public String getName() {
return "mzz";
}
};
}
return mzz;
}
private AffineElementProperty tz;
public final void setTz(double value) {
if (tz == null) {
if (zt != value) {
zt = value;
postProcessChange();
}
} else {
tzProperty().set(value);
}
}
@Override
public final double getTz() {
return tz == null ? zt : tz.get();
}
public final DoubleProperty tzProperty() {
if (tz == null) {
tz = new AffineElementProperty(zt) {
@Override
public Object getBean() {
return Affine.this;
}
@Override
public String getName() {
return "tz";
}
};
}
return tz;
}
public void setElement(MatrixType type, int row, int column, double value) {
if (row < 0 || row >= type.rows() ||
column < 0 || column >= type.columns()) {
throw new IndexOutOfBoundsException("Index outside of affine "
+ "matrix " + type + ": [" + row + ", " + column + "]");
}
switch(type) {
default:
stateError();
case MT_2D_2x3:
case MT_2D_3x3:
if (!isType2D()) {
throw new IllegalArgumentException("Cannot access 2D matrix "
+ "of a 3D transform");
}
switch(row) {
case 0:
switch(column) {
case 0: setMxx(value); return;
case 1: setMxy(value); return;
case 2: setTx(value); return;
}
case 1:
switch(column) {
case 0: setMyx(value); return;
case 1: setMyy(value); return;
case 2: setTy(value); return;
}
case 2:
switch(column) {
case 0: if (value == 0.0) return; else break;
case 1: if (value == 0.0) return; else break;
case 2: if (value == 1.0) return; else break;
}
}
break;
case MT_3D_3x4:
case MT_3D_4x4:
switch(row) {
case 0:
switch(column) {
case 0: setMxx(value); return;
case 1: setMxy(value); return;
case 2: setMxz(value); return;
case 3: setTx(value); return;
}
case 1:
switch(column) {
case 0: setMyx(value); return;
case 1: setMyy(value); return;
case 2: setMyz(value); return;
case 3: setTy(value); return;
}
case 2:
switch(column) {
case 0: setMzx(value); return;
case 1: setMzy(value); return;
case 2: setMzz(value); return;
case 3: setTz(value); return;
}
case 3:
switch(column) {
case 0: if (value == 0.0) return; else break;
case 1: if (value == 0.0) return; else break;
case 2: if (value == 0.0) return; else break;
case 3: if (value == 1.0) return; else break;
}
}
break;
}
throw new IllegalArgumentException("Cannot set affine matrix " + type +
" element " + "[" + row + ", " + column + "] to " + value);
}
private class AffineElementProperty extends SimpleDoubleProperty {
private boolean needsValueChangedEvent = false;
private double oldValue;
public AffineElementProperty(double initialValue) {
super(initialValue);
}
@Override
public void invalidated() {
if (!atomicChange.runs()) {
updateState();
transformChanged();
}
}
@Override
protected void fireValueChangedEvent() {
if (!atomicChange.runs()) {
super.fireValueChangedEvent();
} else {
needsValueChangedEvent = true;
}
}
private void preProcessAtomicChange() {
oldValue = get();
}
private void postProcessAtomicChange() {
if (needsValueChangedEvent) {
needsValueChangedEvent = false;
if (oldValue != get()) {
super.fireValueChangedEvent();
}
}
}
}
private void postProcessChange() {
if (!atomicChange.runs()) {
updateState();
transformChanged();
}
}
@Override
boolean computeIs2D() {
return (state3d == APPLY_NON_3D);
}
@Override
boolean computeIsIdentity() {
return state3d == APPLY_NON_3D && state2d == APPLY_IDENTITY;
}
@Override
public double determinant() {
if (state3d == APPLY_NON_3D) {
return getDeterminant2D();
} else {
return getDeterminant3D();
}
}
private double getDeterminant2D() {
switch (state2d) {
default:
stateError();
case APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE:
case APPLY_SHEAR | APPLY_SCALE:
return getMxx() * getMyy() - getMxy() * getMyx();
case APPLY_SHEAR | APPLY_TRANSLATE:
case APPLY_SHEAR:
return -(getMxy() * getMyx());
case APPLY_SCALE | APPLY_TRANSLATE:
case APPLY_SCALE:
return getMxx() * getMyy();
case APPLY_TRANSLATE:
case APPLY_IDENTITY:
return 1.0;
}
}
private double getDeterminant3D() {
switch(state3d) {
default:
stateError();
case APPLY_TRANSLATE:
return 1.0;
case APPLY_SCALE:
case APPLY_SCALE | APPLY_TRANSLATE:
return getMxx() * getMyy() * getMzz();
case APPLY_3D_COMPLEX:
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
}
@Override
public Transform createConcatenation(Transform transform) {
Affine a = clone();
a.append(transform);
return a;
}
@Override
public Affine createInverse() throws NonInvertibleTransformException {
Affine t = clone();
t.invert();
return t;
}
@Override
public Affine clone() {
return new Affine(this);
}
public void setToTransform(Transform transform) {
setToTransform(
transform.getMxx(), transform.getMxy(),
transform.getMxz(), transform.getTx(),
transform.getMyx(), transform.getMyy(),
transform.getMyz(), transform.getTy(),
transform.getMzx(), transform.getMzy(),
transform.getMzz(), transform.getTz());
}
public void setToTransform(double mxx, double mxy, double tx,
double myx, double myy, double ty) {
setToTransform(mxx, mxy, 0.0, tx,
myx, myy, 0.0, ty,
0.0, 0.0, 1.0, 0.0);
}
public void setToTransform(double mxx, double mxy, double mxz, double tx,
double myx, double myy, double myz, double ty,
double mzx, double mzy, double mzz, double tz)
{
atomicChange.start();
setMxx(mxx);
setMxy(mxy);
setMxz(mxz);
setTx(tx);
setMyx(myx);
setMyy(myy);
setMyz(myz);
setTy(ty);
setMzx(mzx);
setMzy(mzy);
setMzz(mzz);
setTz(tz);
updateState();
atomicChange.end();
}
public void setToTransform(double[] matrix, MatrixType type, int offset) {
if (matrix.length < offset + type.elements()) {
throw new IndexOutOfBoundsException("The array is too short.");
}
switch(type) {
default:
stateError();
case MT_2D_3x3:
if (matrix[offset + 6] != 0.0 ||
matrix[offset + 7] != 0.0 ||
matrix[offset + 8] != 1.0) {
throw new IllegalArgumentException("The matrix is "
+ "not affine");
}
case MT_2D_2x3:
setToTransform(matrix[offset++], matrix[offset++],
matrix[offset++], matrix[offset++],
matrix[offset++], matrix[offset++]);
return;
case MT_3D_4x4:
if (matrix[offset + 12] != 0.0 ||
matrix[offset + 13] != 0.0 ||
matrix[offset + 14] != 0.0 ||
matrix[offset + 15] != 1.0) {
throw new IllegalArgumentException("The matrix is "
+ "not affine");
}
case MT_3D_3x4:
setToTransform(matrix[offset++], matrix[offset++],
matrix[offset++], matrix[offset++], matrix[offset++],
matrix[offset++], matrix[offset++], matrix[offset++],
matrix[offset++], matrix[offset++], matrix[offset++],
matrix[offset++]);
return;
}
}
public void setToIdentity() {
atomicChange.start();
if (state3d != APPLY_NON_3D) {
setMxx(1.0); setMxy(0.0); setMxz(0.0); setTx(0.0);
setMyx(0.0); setMyy(1.0); setMyz(0.0); setTy(0.0);
setMzx(0.0); setMzy(0.0); setMzz(1.0); setTz(0.0);
state3d = APPLY_NON_3D;
state2d = APPLY_IDENTITY;
} else if (state2d != APPLY_IDENTITY) {
setMxx(1.0); setMxy(0.0); setTx(0.0);
setMyx(0.0); setMyy(1.0); setTy(0.0);
state2d = APPLY_IDENTITY;
}
atomicChange.end();
}
public void invert() throws NonInvertibleTransformException {
atomicChange.start();
if (state3d == APPLY_NON_3D) {
invert2D();
updateState2D();
} else {
invert3D();
updateState();
}
atomicChange.end();
}
private void invert2D() throws NonInvertibleTransformException {
double Mxx, Mxy, Mxt;
double Myx, Myy, Myt;
double det;
switch (state2d) {
default:
stateError();
case APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE:
Mxx = getMxx(); Mxy = getMxy(); Mxt = getTx();
Myx = getMyx(); Myy = getMyy(); Myt = getTy();
det = getDeterminant2D();
if (det == 0.0) {
atomicChange.cancel();
throw new NonInvertibleTransformException("Determinant is 0");
}
setMxx(Myy / det);
setMyx(-Myx / det);
setMxy(-Mxy / det);
setMyy(Mxx / det);
setTx((Mxy * Myt - Myy * Mxt) / det);
setTy((Myx * Mxt - Mxx * Myt) / det);
return;
case APPLY_SHEAR | APPLY_SCALE:
Mxx = getMxx(); Mxy = getMxy();
Myx = getMyx(); Myy = getMyy();
det = getDeterminant2D();
if (det == 0.0) {
atomicChange.cancel();
throw new NonInvertibleTransformException("Determinant is 0");
}
setMxx(Myy / det);
setMyx(-Myx / det);
setMxy(-Mxy / det);
setMyy(Mxx / det);
return;
case APPLY_SHEAR | APPLY_TRANSLATE:
Mxy = getMxy(); Mxt = getTx();
Myx = getMyx(); Myt = getTy();
if (Mxy == 0.0 || Myx == 0.0) {
atomicChange.cancel();
throw new NonInvertibleTransformException("Determinant is 0");
}
setMyx(1.0 / Mxy);
setMxy(1.0 / Myx);
setTx(-Myt / Myx);
setTy(-Mxt / Mxy);
return;
case APPLY_SHEAR:
Mxy = getMxy();
Myx = getMyx();
if (Mxy == 0.0 || Myx == 0.0) {
atomicChange.cancel();
throw new NonInvertibleTransformException("Determinant is 0");
}
setMyx(1.0 / Mxy);
setMxy(1.0 / Myx);
return;
case APPLY_SCALE | APPLY_TRANSLATE:
Mxx = getMxx(); Mxt = getTx();
Myy = getMyy(); Myt = getTy();
if (Mxx == 0.0 || Myy == 0.0) {
atomicChange.cancel();
throw new NonInvertibleTransformException("Determinant is 0");
}
setMxx(1.0 / Mxx);
setMyy(1.0 / Myy);
setTx(-Mxt / Mxx);
setTy(-Myt / Myy);
return;
case APPLY_SCALE:
Mxx = getMxx();
Myy = getMyy();
if (Mxx == 0.0 || Myy == 0.0) {
atomicChange.cancel();
throw new NonInvertibleTransformException("Determinant is 0");
}
setMxx(1.0 / Mxx);
setMyy(1.0 / Myy);
return;
case APPLY_TRANSLATE:
setTx(-getTx());
setTy(-getTy());
return;
case APPLY_IDENTITY:
return;
}
}
private void invert3D() throws NonInvertibleTransformException {
switch(state3d) {
default:
stateError();
case APPLY_TRANSLATE:
setTx(-getTx());
setTy(-getTy());
setTz(-getTz());
return;
case APPLY_SCALE:
final double mxx_s = getMxx();
final double myy_s = getMyy();
final double mzz_s = getMzz();
if (mxx_s == 0.0 || myy_s == 0.0 || mzz_s == 0.0) {
atomicChange.cancel();
throw new NonInvertibleTransformException("Determinant is 0");
}
setMxx(1.0 / mxx_s);
setMyy(1.0 / myy_s);
setMzz(1.0 / mzz_s);
return;
case APPLY_SCALE | APPLY_TRANSLATE:
final double mxx_st = getMxx();
final double tx_st = getTx();
final double myy_st = getMyy();
final double ty_st = getTy();
final double mzz_st = getMzz();
final double tz_st = getTz();
if (mxx_st == 0.0 || myy_st == 0.0 || mzz_st == 0.0) {
atomicChange.cancel();
throw new NonInvertibleTransformException("Determinant is 0");
}
setMxx(1.0 / mxx_st);
setMyy(1.0 / myy_st);
setMzz(1.0 / mzz_st);
setTx(-tx_st / mxx_st);
setTy(-ty_st / myy_st);
setTz(-tz_st / mzz_st);
return;
case APPLY_3D_COMPLEX:
final double mxx = getMxx();
final double mxy = getMxy();
final double mxz = getMxz();
final double tx = getTx();
final double myx = getMyx();
final double myy = getMyy();
final double myz = getMyz();
final double ty = getTy();
final double mzy = getMzy();
final double mzx = getMzx();
final double mzz = getMzz();
final double tz = getTz();
final double det =
mxx * (myy * mzz - mzy * myz) +
mxy * (myz * mzx - mzz * myx) +
mxz * (myx * mzy - mzx * myy);
if (det == 0.0) {
atomicChange.cancel();
throw new NonInvertibleTransformException("Determinant is 0");
}
final double cxx = myy * mzz - myz * mzy;
final double cyx = - myx * mzz + myz * mzx;
final double czx = myx * mzy - myy * mzx;
final double cxt = - mxy * (myz * tz - mzz * ty)
- mxz * (ty * mzy - tz * myy)
- tx * (myy * mzz - mzy * myz);
final double cxy = - mxy * mzz + mxz * mzy;
final double cyy = mxx * mzz - mxz * mzx;
final double czy = - mxx * mzy + mxy * mzx;
final double cyt = mxx * (myz * tz - mzz * ty)
+ mxz * (ty * mzx - tz * myx)
+ tx * (myx * mzz - mzx * myz);
final double cxz = mxy * myz - mxz * myy;
final double cyz = - mxx * myz + mxz * myx;
final double czz = mxx * myy - mxy * myx;
final double czt = - mxx * (myy * tz - mzy * ty)
- mxy * (ty * mzx - tz * myx)
- tx * (myx * mzy - mzx * myy);
setMxx(cxx / det);
setMxy(cxy / det);
setMxz(cxz / det);
setTx(cxt / det);
setMyx(cyx / det);
setMyy(cyy / det);
setMyz(cyz / det);
setTy(cyt / det);
setMzx(czx / det);
setMzy(czy / det);
setMzz(czz / det);
setTz(czt / det);
return;
}
}
public void append(Transform transform) {
transform.appendTo(this);
}
public void append(double mxx, double mxy, double tx,
double myx, double myy, double ty) {
if (state3d == APPLY_NON_3D) {
atomicChange.start();
final double m_xx = getMxx();
final double m_xy = getMxy();
final double m_yx = getMyx();
final double m_yy = getMyy();
setMxx(m_xx * mxx + m_xy * myx);
setMxy(m_xx * mxy + m_xy * myy);
setTx(m_xx * tx + m_xy * ty + getTx());
setMyx(m_yx * mxx + m_yy * myx);
setMyy(m_yx * mxy + m_yy * myy);
setTy(m_yx * tx + m_yy * ty + getTy());
updateState();
atomicChange.end();
} else {
append(mxx, mxy, 0.0, tx,
myx, myy, 0.0, ty,
0.0, 0.0, 1.0, 0.0);
}
}
public void append(double mxx, double mxy, double mxz, double tx,
double myx, double myy, double myz, double ty,
double mzx, double mzy, double mzz, double tz)
{
atomicChange.start();
final double m_xx = getMxx();
final double m_xy = getMxy();
final double m_xz = getMxz();
final double t_x = getTx();
final double m_yx = getMyx();
final double m_yy = getMyy();
final double m_yz = getMyz();
final double t_y = getTy();
final double m_zx = getMzx();
final double m_zy = getMzy();
final double m_zz = getMzz();
final double t_z = getTz();
setMxx(m_xx * mxx + m_xy * myx + m_xz * mzx);
setMxy(m_xx * mxy + m_xy * myy + m_xz * mzy);
setMxz(m_xx * mxz + m_xy * myz + m_xz * mzz);
setTx( m_xx * tx + m_xy * ty + m_xz * tz + t_x);
setMyx(m_yx * mxx + m_yy * myx + m_yz * mzx);
setMyy(m_yx * mxy + m_yy * myy + m_yz * mzy);
setMyz(m_yx * mxz + m_yy * myz + m_yz * mzz);
setTy( m_yx * tx + m_yy * ty + m_yz * tz + t_y);
setMzx(m_zx * mxx + m_zy * myx + m_zz * mzx);
setMzy(m_zx * mxy + m_zy * myy + m_zz * mzy);
setMzz(m_zx * mxz + m_zy * myz + m_zz * mzz);
setTz( m_zx * tx + m_zy * ty + m_zz * tz + t_z);
updateState();
atomicChange.end();
}
public void append(double[] matrix, MatrixType type, int offset) {
if (matrix.length < offset + type.elements()) {
throw new IndexOutOfBoundsException("The array is too short.");
}
switch(type) {
default:
stateError();
case MT_2D_3x3:
if (matrix[offset + 6] != 0.0 ||
matrix[offset + 7] != 0.0 ||
matrix[offset + 8] != 1.0) {
throw new IllegalArgumentException("The matrix is "
+ "not affine");
}
case MT_2D_2x3:
append(matrix[offset++], matrix[offset++],
matrix[offset++], matrix[offset++],
matrix[offset++], matrix[offset++]);
return;
case MT_3D_4x4:
if (matrix[offset + 12] != 0.0 ||
matrix[offset + 13] != 0.0 ||
matrix[offset + 14] != 0.0 ||
matrix[offset + 15] != 1.0) {
throw new IllegalArgumentException("The matrix is "
+ "not affine");
}
case MT_3D_3x4:
append(matrix[offset++], matrix[offset++], matrix[offset++],
matrix[offset++], matrix[offset++], matrix[offset++],
matrix[offset++], matrix[offset++], matrix[offset++],
matrix[offset++], matrix[offset++], matrix[offset++]);
return;
}
}
@Override
void appendTo(Affine a) {
switch(state3d) {
default:
stateError();
case APPLY_NON_3D:
switch(state2d) {
case APPLY_IDENTITY:
return;
case APPLY_TRANSLATE:
a.appendTranslation(getTx(), getTy());
return;
case APPLY_SCALE:
a.appendScale(getMxx(), getMyy());
return;
case APPLY_SCALE | APPLY_TRANSLATE:
a.appendTranslation(getTx(), getTy());
a.appendScale(getMxx(), getMyy());
return;
default:
a.append(getMxx(), getMxy(), getTx(),
getMyx(), getMyy(), getTy());
return;
}
case APPLY_TRANSLATE:
a.appendTranslation(getTx(), getTy(), getTz());
return;
case APPLY_SCALE:
a.appendScale(getMxx(), getMyy(), getMzz());
return;
case APPLY_SCALE | APPLY_TRANSLATE:
a.appendTranslation(getTx(), getTy(), getTz());
a.appendScale(getMxx(), getMyy(), getMzz());
return;
case APPLY_3D_COMPLEX:
a.append(getMxx(), getMxy(), getMxz(), getTx(),
getMyx(), getMyy(), getMyz(), getTy(),
getMzx(), getMzy(), getMzz(), getTz());
return;
}
}
public void prepend(Transform transform) {
transform.prependTo(this);
}
public void prepend(double mxx, double mxy, double tx,
double myx, double myy, double ty) {
if (state3d == APPLY_NON_3D) {
atomicChange.start();
final double m_xx = getMxx();
final double m_xy = getMxy();
final double t_x = getTx();
final double m_yx = getMyx();
final double m_yy = getMyy();
final double t_y = getTy();
setMxx(mxx * m_xx + mxy * m_yx);
setMxy(mxx * m_xy + mxy * m_yy);
setTx(mxx * t_x + mxy * t_y + tx);
setMyx(myx * m_xx + myy * m_yx);
setMyy(myx * m_xy + myy * m_yy);
setTy(myx * t_x + myy * t_y + ty);
updateState2D();
atomicChange.end();
} else {
prepend(mxx, mxy, 0.0, tx,
myx, myy, 0.0, ty,
0.0, 0.0, 1.0, 0.0);
}
}
public void prepend(double mxx, double mxy, double mxz, double tx,
double myx, double myy, double myz, double ty,
double mzx, double mzy, double mzz, double tz) {
atomicChange.start();
final double m_xx = getMxx();
final double m_xy = getMxy();
final double m_xz = getMxz();
final double t_x = getTx();
final double m_yx = getMyx();
final double m_yy = getMyy();
final double m_yz = getMyz();
final double t_y = getTy();
final double m_zx = getMzx();
final double m_zy = getMzy();
final double m_zz = getMzz();
final double t_z = getTz();
setMxx(mxx * m_xx + mxy * m_yx + mxz * m_zx);
setMxy(mxx * m_xy + mxy * m_yy + mxz * m_zy);
setMxz(mxx * m_xz + mxy * m_yz + mxz * m_zz);
setTx( mxx * t_x + mxy * t_y + mxz * t_z + tx);
setMyx(myx * m_xx + myy * m_yx + myz * m_zx);
setMyy(myx * m_xy + myy * m_yy + myz * m_zy);
setMyz(myx * m_xz + myy * m_yz + myz * m_zz);
setTy( myx * t_x + myy * t_y + myz * t_z + ty);
setMzx(mzx * m_xx + mzy * m_yx + mzz * m_zx);
setMzy(mzx * m_xy + mzy * m_yy + mzz * m_zy);
setMzz(mzx * m_xz + mzy * m_yz + mzz * m_zz);
setTz( mzx * t_x + mzy * t_y + mzz * t_z + tz);
updateState();
atomicChange.end();
}
public void prepend(double[] matrix, MatrixType type, int offset) {
if (matrix.length < offset + type.elements()) {
throw new IndexOutOfBoundsException("The array is too short.");
}
switch(type) {
default:
stateError();
case MT_2D_3x3:
if (matrix[offset + 6] != 0.0 ||
matrix[offset + 7] != 0.0 ||
matrix[offset + 8] != 1.0) {
throw new IllegalArgumentException("The matrix is "
+ "not affine");
}
case MT_2D_2x3:
prepend(matrix[offset++], matrix[offset++],
matrix[offset++], matrix[offset++],
matrix[offset++], matrix[offset++]);
return;
case MT_3D_4x4:
if (matrix[offset + 12] != 0.0 ||
matrix[offset + 13] != 0.0 ||
matrix[offset + 14] != 0.0 ||
matrix[offset + 15] != 1.0) {
throw new IllegalArgumentException("The matrix is "
+ "not affine");
}
case MT_3D_3x4:
prepend(matrix[offset++], matrix[offset++], matrix[offset++],
matrix[offset++], matrix[offset++], matrix[offset++],
matrix[offset++], matrix[offset++], matrix[offset++],
matrix[offset++], matrix[offset++], matrix[offset++]);
return;
}
}
@Override
void prependTo(Affine a) {
switch(state3d) {
default:
stateError();
case APPLY_NON_3D:
switch(state2d) {
case APPLY_IDENTITY:
return;
case APPLY_TRANSLATE:
a.prependTranslation(getTx(), getTy());
return;
case APPLY_SCALE:
a.prependScale(getMxx(), getMyy());
return;
case APPLY_SCALE | APPLY_TRANSLATE:
a.prependScale(getMxx(), getMyy());
a.prependTranslation(getTx(), getTy());
return;
default:
a.prepend(getMxx(), getMxy(), getTx(),
getMyx(), getMyy(), getTy());
return;
}
case APPLY_TRANSLATE:
a.prependTranslation(getTx(), getTy(), getTz());
return;
case APPLY_SCALE:
a.prependScale(getMxx(), getMyy(), getMzz());
return;
case APPLY_SCALE | APPLY_TRANSLATE:
a.prependScale(getMxx(), getMyy(), getMzz());
a.prependTranslation(getTx(), getTy(), getTz());
return;
case APPLY_3D_COMPLEX:
a.prepend(getMxx(), getMxy(), getMxz(), getTx(),
getMyx(), getMyy(), getMyz(), getTy(),
getMzx(), getMzy(), getMzz(), getTz());
return;
}
}
public void appendTranslation(double tx, double ty) {
atomicChange.start();
translate2D(tx, ty);
atomicChange.end();
}
public void appendTranslation(double tx, double ty, double tz) {
atomicChange.start();
translate3D(tx, ty, tz);
atomicChange.end();
}
private void translate2D(double tx, double ty) {
if (state3d != APPLY_NON_3D) {
translate3D(tx, ty, 0.0);
return;
}
switch (state2d) {
default:
stateError();
case APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE:
setTx(tx * getMxx() + ty * getMxy() + getTx());
setTy(tx * getMyx() + ty * getMyy() + getTy());
if (getTx() == 0.0 && getTy() == 0.0) {
state2d = APPLY_SHEAR | APPLY_SCALE;
}
return;
case APPLY_SHEAR | APPLY_SCALE:
setTx(tx * getMxx() + ty * getMxy());
setTy(tx * getMyx() + ty * getMyy());
if (getTx() != 0.0 || getTy() != 0.0) {
state2d = APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE;
}
return;
case APPLY_SHEAR | APPLY_TRANSLATE:
setTx(ty * getMxy() + getTx());
setTy(tx * getMyx() + getTy());
if (getTx() == 0.0 && getTy() == 0.0) {
state2d = APPLY_SHEAR;
}
return;
case APPLY_SHEAR:
setTx(ty * getMxy());
setTy(tx * getMyx());
if (getTx() != 0.0 || getTy() != 0.0) {
state2d = APPLY_SHEAR | APPLY_TRANSLATE;
}
return;
case APPLY_SCALE | APPLY_TRANSLATE:
setTx(tx * getMxx() + getTx());
setTy(ty * getMyy() + getTy());
if (getTx() == 0.0 && getTy() == 0.0) {
state2d = APPLY_SCALE;
}
return;
case APPLY_SCALE:
setTx(tx * getMxx());
setTy(ty * getMyy());
if (getTx() != 0.0 || getTy() != 0.0) {
state2d = APPLY_SCALE | APPLY_TRANSLATE;
}
return;
case APPLY_TRANSLATE:
setTx(tx + getTx());
setTy(ty + getTy());
if (getTx() == 0.0 && getTy() == 0.0) {
state2d = APPLY_IDENTITY;
}
return;
case APPLY_IDENTITY:
setTx(tx);
setTy(ty);
if (tx != 0.0 || ty != 0.0) {
state2d = APPLY_TRANSLATE;
}
return;
}
}
private void translate3D(double tx, double ty, double tz) {
switch(state3d) {
default:
stateError();
case APPLY_NON_3D:
translate2D(tx, ty);
if (tz != 0.0) {
setTz(tz);
if ((state2d & APPLY_SHEAR) == 0) {
state3d = (state2d & APPLY_SCALE) | APPLY_TRANSLATE;
} else {
state3d = APPLY_3D_COMPLEX;
}
}
return;
case APPLY_TRANSLATE:
setTx(tx + getTx());
setTy(ty + getTy());
setTz(tz + getTz());
if (getTz() == 0.0) {
state3d = APPLY_NON_3D;
if (getTx() == 0.0 && getTy() == 0.0) {
state2d = APPLY_IDENTITY;
} else {
state2d = APPLY_TRANSLATE;
}
}
return;
case APPLY_SCALE:
setTx(tx * getMxx());
setTy(ty * getMyy());
setTz(tz * getMzz());
if (getTx() != 0.0 || getTy() != 0.0 || getTz() != 0.0) {
state3d |= APPLY_TRANSLATE;
}
return;
case APPLY_SCALE | APPLY_TRANSLATE:
setTx(tx * getMxx() + getTx());
setTy(ty * getMyy() + getTy());
setTz(tz * getMzz() + getTz());
if (getTz() == 0.0) {
if (getTx() == 0.0 && getTy() == 0.0) {
state3d = APPLY_SCALE;
}
if (getMzz() == 1.0) {
state2d = state3d;
state3d = APPLY_NON_3D;
}
}
return;
case APPLY_3D_COMPLEX:
setTx(tx * getMxx() + ty * getMxy() + tz * getMxz() + getTx());
setTy(tx * getMyx() + ty * getMyy() + tz * getMyz() + getTy());
setTz(tx * getMzx() + ty * getMzy() + tz * getMzz() + getTz());
updateState();
return;
}
}
public void prependTranslation(double tx, double ty, double tz) {
atomicChange.start();
preTranslate3D(tx, ty, tz);
atomicChange.end();
}
public void prependTranslation(double tx, double ty) {
atomicChange.start();
preTranslate2D(tx, ty);
atomicChange.end();
}
private void preTranslate2D(double tx, double ty) {
if (state3d != APPLY_NON_3D) {
preTranslate3D(tx, ty, 0.0);
return;
}
setTx(getTx() + tx);
setTy(getTy() + ty);
if (getTx() == 0.0 && getTy() == 0.0) {
state2d &= ~APPLY_TRANSLATE;
} else {
state2d |= APPLY_TRANSLATE;
}
}
private void preTranslate3D(double tx, double ty, double tz) {
switch(state3d) {
default:
stateError();
case APPLY_NON_3D:
preTranslate2D(tx, ty);
if (tz != 0.0) {
setTz(tz);
if ((state2d & APPLY_SHEAR) == 0) {
state3d = (state2d & APPLY_SCALE) | APPLY_TRANSLATE;
} else {
state3d = APPLY_3D_COMPLEX;
}
}
return;
case APPLY_TRANSLATE:
setTx(getTx() + tx);
setTy(getTy() + ty);
setTz(getTz() + tz);
if (getTz() == 0.0) {
state3d = APPLY_NON_3D;
if (getTx() == 0.0 && getTy() == 0.0) {
state2d = APPLY_IDENTITY;
} else {
state2d = APPLY_TRANSLATE;
}
}
return;
case APPLY_SCALE:
setTx(tx);
setTy(ty);
setTz(tz);
if (tx != 0.0 || ty != 0.0 || tz != 0.0) {
state3d |= APPLY_TRANSLATE;
}
return;
case APPLY_SCALE | APPLY_TRANSLATE:
setTx(getTx() + tx);
setTy(getTy() + ty);
setTz(getTz() + tz);
if (getTz() == 0.0) {
if (getTx() == 0.0 && getTy() == 0.0) {
state3d = APPLY_SCALE;
}
if (getMzz() == 1.0) {
state2d = state3d;
state3d = APPLY_NON_3D;
}
}
return;
case APPLY_3D_COMPLEX:
setTx(getTx() + tx);
setTy(getTy() + ty);
setTz(getTz() + tz);
if (getTz() == 0.0 && getMxz() == 0.0 && getMyz() == 0.0 &&
getMzx() == 0.0 && getMzy() == 0.0 && getMzz() == 1.0) {
state3d = APPLY_NON_3D;
updateState2D();
}
return;
}
}
public void appendScale(double sx, double sy) {
atomicChange.start();
scale2D(sx, sy);
atomicChange.end();
}
public void appendScale(double sx, double sy,
double pivotX, double pivotY) {
atomicChange.start();
if (pivotX != 0.0 || pivotY != 0.0) {
translate2D(pivotX, pivotY);
scale2D(sx, sy);
translate2D(-pivotX, -pivotY);
} else {
scale2D(sx, sy);
}
atomicChange.end();
}
public void appendScale(double sx, double sy, Point2D pivot) {
appendScale(sx, sy, pivot.getX(), pivot.getY());
}
public void appendScale(double sx, double sy, double sz) {
atomicChange.start();
scale3D(sx, sy, sz);
atomicChange.end();
}
public void appendScale(double sx, double sy, double sz,
double pivotX, double pivotY, double pivotZ) {
atomicChange.start();
if (pivotX != 0.0 || pivotY != 0.0 || pivotZ != 0.0) {
translate3D(pivotX, pivotY, pivotZ);
scale3D(sx, sy, sz);
translate3D(-pivotX, -pivotY, -pivotZ);
} else {
scale3D(sx, sy, sz);
}
atomicChange.end();
}
public void appendScale(double sx, double sy, double sz, Point3D pivot) {
appendScale(sx, sy, sz, pivot.getX(), pivot.getY(), pivot.getZ());
}
private void scale2D(double sx, double sy) {
if (state3d != APPLY_NON_3D) {
scale3D(sx, sy, 1.0);
return;
}
int mystate = state2d;
switch (mystate) {
default:
stateError();
case APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE:
case APPLY_SHEAR | APPLY_SCALE:
setMxx(getMxx() * sx);
setMyy(getMyy() * sy);
case APPLY_SHEAR | APPLY_TRANSLATE:
case APPLY_SHEAR:
setMxy(getMxy() * sy);
setMyx(getMyx() * sx);
if (getMxy() == 0.0 && getMyx() == 0.0) {
mystate &= APPLY_TRANSLATE;
if (getMxx() != 1.0 || getMyy() != 1.0) {
mystate |= APPLY_SCALE;
}
state2d = mystate;
} else if (getMxx() == 0.0 && getMyy() == 0.0) {
state2d &= ~APPLY_SCALE;
}
return;
case APPLY_SCALE | APPLY_TRANSLATE:
case APPLY_SCALE:
setMxx(getMxx() * sx);
setMyy(getMyy() * sy);
if (getMxx() == 1.0 && getMyy() == 1.0) {
state2d = (mystate &= APPLY_TRANSLATE);
}
return;
case APPLY_TRANSLATE:
case APPLY_IDENTITY:
setMxx(sx);
setMyy(sy);
if (sx != 1.0 || sy != 1.0) {
state2d = (mystate | APPLY_SCALE);
}
return;
}
}
private void scale3D(double sx, double sy, double sz) {
switch (state3d) {
default:
stateError();
case APPLY_NON_3D:
scale2D(sx, sy);
if (sz != 1.0) {
setMzz(sz);
if ((state2d & APPLY_SHEAR) == 0) {
state3d = (state2d & APPLY_TRANSLATE) | APPLY_SCALE;
} else {
state3d = APPLY_3D_COMPLEX;
}
}
return;
case APPLY_TRANSLATE:
setMxx(sx);
setMyy(sy);
setMzz(sz);
if (sx != 1.0 || sy != 1.0 || sz != 1.0) {
state3d |= APPLY_SCALE;
}
return;
case APPLY_SCALE:
setMxx(getMxx() * sx);
setMyy(getMyy() * sy);
setMzz(getMzz() * sz);
if (getMzz() == 1.0) {
state3d = APPLY_NON_3D;
if (getMxx() == 1.0 && getMyy() == 1.0) {
state2d = APPLY_IDENTITY;
} else {
state2d = APPLY_SCALE;
}
}
return;
case APPLY_SCALE | APPLY_TRANSLATE:
setMxx(getMxx() * sx);
setMyy(getMyy() * sy);
setMzz(getMzz() * sz);
if (getMxx() == 1.0 && getMyy() == 1.0 && getMzz() == 1.0) {
state3d &= ~APPLY_SCALE;
}
if (getTz() == 0.0 && getMzz() == 1.0) {
state2d = state3d;
state3d = APPLY_NON_3D;
}
return;
case APPLY_3D_COMPLEX:
setMxx(getMxx() * sx);
setMxy(getMxy() * sy);
setMxz(getMxz() * sz);
setMyx(getMyx() * sx);
setMyy(getMyy() * sy);
setMyz(getMyz() * sz);
setMzx(getMzx() * sx);
setMzy(getMzy() * sy);
setMzz(getMzz() * sz);
if (sx == 0.0 || sy == 0.0 || sz == 0.0) {
updateState();
}
return;
}
}
public void prependScale(double sx, double sy) {
atomicChange.start();
preScale2D(sx, sy);
atomicChange.end();
}
public void prependScale(double sx, double sy,
double pivotX, double pivotY) {
atomicChange.start();
if (pivotX != 0.0 || pivotY != 0.0) {
preTranslate2D(-pivotX, -pivotY);
preScale2D(sx, sy);
preTranslate2D(pivotX, pivotY);
} else {
preScale2D(sx, sy);
}
atomicChange.end();
}
public void prependScale(double sx, double sy, Point2D pivot) {
prependScale(sx, sy, pivot.getX(), pivot.getY());
}
public void prependScale(double sx, double sy, double sz) {
atomicChange.start();
preScale3D(sx, sy, sz);
atomicChange.end();
}
public void prependScale(double sx, double sy, double sz,
double pivotX, double pivotY, double pivotZ) {
atomicChange.start();
if (pivotX != 0.0 || pivotY != 0.0 || pivotZ != 0.0) {
preTranslate3D(-pivotX, -pivotY, -pivotZ);
preScale3D(sx, sy, sz);
preTranslate3D(pivotX, pivotY, pivotZ);
} else {
preScale3D(sx, sy, sz);
}
atomicChange.end();
}
public void prependScale(double sx, double sy, double sz, Point3D pivot) {
prependScale(sx, sy, sz, pivot.getX(), pivot.getY(), pivot.getZ());
}
private void preScale2D(double sx, double sy) {
if (state3d != APPLY_NON_3D) {
preScale3D(sx, sy, 1.0);
return;
}
int mystate = state2d;
switch (mystate) {
default:
stateError();
case APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE:
setTx(getTx() * sx);
setTy(getTy() * sy);
if (getTx() == 0.0 && getTy() == 0.0) {
mystate = mystate & ~APPLY_TRANSLATE;
state2d = mystate;
}
case APPLY_SHEAR | APPLY_SCALE:
setMxx(getMxx() * sx);
setMyy(getMyy() * sy);
case APPLY_SHEAR:
setMxy(getMxy() * sx);
setMyx(getMyx() * sy);
if (getMxy() == 0.0 && getMyx() == 0.0) {
mystate &= APPLY_TRANSLATE;
if (getMxx() != 1.0 || getMyy() != 1.0) {
mystate |= APPLY_SCALE;
}
state2d = mystate;
}
return;
case APPLY_SHEAR | APPLY_TRANSLATE:
setTx(getTx() * sx);
setTy(getTy() * sy);
setMxy(getMxy() * sx);
setMyx(getMyx() * sy);
if (getMxy() == 0.0 && getMyx() == 0.0) {
if (getTx() == 0.0 && getTy() == 0.0) {
state2d = APPLY_SCALE;
} else {
state2d = APPLY_SCALE | APPLY_TRANSLATE;
}
} else if (getTx() ==0.0 && getTy() == 0.0) {
state2d = APPLY_SHEAR;
}
return;
case APPLY_SCALE | APPLY_TRANSLATE:
setTx(getTx() * sx);
setTy(getTy() * sy);
if (getTx() == 0.0 && getTy() == 0.0) {
mystate = mystate & ~APPLY_TRANSLATE;
state2d = mystate;
}
case APPLY_SCALE:
setMxx(getMxx() * sx);
setMyy(getMyy() * sy);
if (getMxx() == 1.0 && getMyy() == 1.0) {
state2d = (mystate &= APPLY_TRANSLATE);
}
return;
case APPLY_TRANSLATE:
setTx(getTx() * sx);
setTy(getTy() * sy);
if (getTx() == 0.0 && getTy() == 0.0) {
mystate = mystate & ~APPLY_TRANSLATE;
state2d = mystate;
}
case APPLY_IDENTITY:
setMxx(sx);
setMyy(sy);
if (sx != 1.0 || sy != 1.0) {
state2d = mystate | APPLY_SCALE;
}
return;
}
}
private void preScale3D(double sx, double sy, double sz) {
switch (state3d) {
default:
stateError();
case APPLY_NON_3D:
preScale2D(sx, sy);
if (sz != 1.0) {
setMzz(sz);
if ((state2d & APPLY_SHEAR) == 0) {
state3d = (state2d & APPLY_TRANSLATE) | APPLY_SCALE;
} else {
state3d = APPLY_3D_COMPLEX;
}
}
return;
case APPLY_SCALE | APPLY_TRANSLATE:
setTx(getTx() * sx);
setTy(getTy() * sy);
setTz(getTz() * sz);
setMxx(getMxx() * sx);
setMyy(getMyy() * sy);
setMzz(getMzz() * sz);
if (getTx() == 0.0 && getTy() == 0.0 && getTz() == 0.0) {
state3d &= ~APPLY_TRANSLATE;
}
if (getMxx() == 1.0 && getMyy() == 1.0 && getMzz() == 1.0) {
state3d &= ~APPLY_SCALE;
}
if (getTz() == 0.0 && getMzz() == 1.0) {
state2d = state3d;
state3d = APPLY_NON_3D;
}
return;
case APPLY_SCALE:
setMxx(getMxx() * sx);
setMyy(getMyy() * sy);
setMzz(getMzz() * sz);
if (getMzz() == 1.0) {
state3d = APPLY_NON_3D;
if (getMxx() == 1.0 && getMyy() == 1.0) {
state2d = APPLY_IDENTITY;
} else {
state2d = APPLY_SCALE;
}
}
return;
case APPLY_TRANSLATE:
setTx(getTx() * sx);
setTy(getTy() * sy);
setTz(getTz() * sz);
setMxx(sx);
setMyy(sy);
setMzz(sz);
if (getTx() == 0.0 && getTy() == 0.0 && getTz() == 0.0) {
state3d &= ~APPLY_TRANSLATE;
}
if (sx != 1.0 || sy != 1.0 || sz != 1.0) {
state3d |= APPLY_SCALE;
}
return;
case APPLY_3D_COMPLEX:
setMxx(getMxx() * sx);
setMxy(getMxy() * sx);
setMxz(getMxz() * sx);
setTx(getTx() * sx);
setMyx(getMyx() * sy);
setMyy(getMyy() * sy);
setMyz(getMyz() * sy);
setTy(getTy() * sy);
setMzx(getMzx() * sz);
setMzy(getMzy() * sz);
setMzz(getMzz() * sz);
setTz(getTz() * sz);
if (sx == 0.0 || sy == 0.0 || sz == 0.0) {
updateState();
}
return;
}
}
public void appendShear(double shx, double shy) {
atomicChange.start();
shear2D(shx, shy);
atomicChange.end();
}
public void appendShear(double shx, double shy,
double pivotX, double pivotY) {
atomicChange.start();
if (pivotX != 0.0 || pivotY != 0.0) {
translate2D(pivotX, pivotY);
shear2D(shx, shy);
translate2D(-pivotX, -pivotY);
} else {
shear2D(shx, shy);
}
atomicChange.end();
}
public void appendShear(double shx, double shy, Point2D pivot) {
appendShear(shx, shy, pivot.getX(), pivot.getY());
}
private void shear2D(double shx, double shy) {
if (state3d != APPLY_NON_3D) {
shear3D(shx, shy);
return;
}
int mystate = state2d;
switch (mystate) {
default:
stateError();
case APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE:
case APPLY_SHEAR | APPLY_SCALE:
double M0, M1;
M0 = getMxx();
M1 = getMxy();
setMxx(M0 + M1 * shy);
setMxy(M0 * shx + M1);
M0 = getMyx();
M1 = getMyy();
setMyx(M0 + M1 * shy);
setMyy(M0 * shx + M1);
updateState2D();
return;
case APPLY_SHEAR | APPLY_TRANSLATE:
case APPLY_SHEAR:
setMxx(getMxy() * shy);
setMyy(getMyx() * shx);
if (getMxx() != 0.0 || getMyy() != 0.0) {
state2d = mystate | APPLY_SCALE;
}
return;
case APPLY_SCALE | APPLY_TRANSLATE:
case APPLY_SCALE:
setMxy(getMxx() * shx);
setMyx(getMyy() * shy);
if (getMxy() != 0.0 || getMyx() != 0.0) {
state2d = mystate | APPLY_SHEAR;
}
return;
case APPLY_TRANSLATE:
case APPLY_IDENTITY:
setMxy(shx);
setMyx(shy);
if (getMxy() != 0.0 || getMyx() != 0.0) {
state2d = mystate | APPLY_SCALE | APPLY_SHEAR;
}
return;
}
}
private void shear3D(double shx, double shy) {
switch (state3d) {
default:
stateError();
case APPLY_NON_3D:
shear2D(shx, shy);
return;
case APPLY_TRANSLATE:
setMxy(shx);
setMyx(shy);
if (shx != 0.0 || shy != 0.0) {
state3d = APPLY_3D_COMPLEX;
}
return;
case APPLY_SCALE:
case APPLY_SCALE | APPLY_TRANSLATE:
setMxy(getMxx() * shx);
setMyx(getMyy() * shy);
if (getMxy() != 0.0 || getMyx() != 0.0) {
state3d = APPLY_3D_COMPLEX;
}
return;
case APPLY_3D_COMPLEX:
final double m_xx = getMxx();
final double m_xy = getMxy();
final double m_yx = getMyx();
final double m_yy = getMyy();
final double m_zx = getMzx();
final double m_zy = getMzy();
setMxx(m_xx + m_xy * shy);
setMxy(m_xy + m_xx * shx);
setMyx(m_yx + m_yy * shy);
setMyy(m_yy + m_yx * shx);
setMzx(m_zx + m_zy * shy);
setMzy(m_zy + m_zx * shx);
updateState();
return;
}
}
public void prependShear(double shx, double shy) {
atomicChange.start();
preShear2D(shx, shy);
atomicChange.end();
}
public void prependShear(double shx, double shy,
double pivotX, double pivotY) {
atomicChange.start();
if (pivotX != 0.0 || pivotY != 0.0) {
preTranslate2D(-pivotX, -pivotY);
preShear2D(shx, shy);
preTranslate2D(pivotX, pivotY);
} else {
preShear2D(shx, shy);
}
atomicChange.end();
}
public void prependShear(double shx, double shy, Point2D pivot) {
prependShear(shx, shy, pivot.getX(), pivot.getY());
}
private void preShear2D(double shx, double shy) {
if (state3d != APPLY_NON_3D) {
preShear3D(shx, shy);
return;
}
int mystate = state2d;
switch (mystate) {
default:
stateError();
case APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE:
case APPLY_SHEAR | APPLY_TRANSLATE:
final double t_x_1 = getTx();
final double t_y_1 = getTy();
setTx(t_x_1 + shx * t_y_1);
setTy(t_y_1 + shy * t_x_1);
case APPLY_SHEAR | APPLY_SCALE:
case APPLY_SHEAR:
final double m_xx = getMxx();
final double m_xy = getMxy();
final double m_yx = getMyx();
final double m_yy = getMyy();
setMxx(m_xx + shx * m_yx);
setMxy(m_xy + shx * m_yy);
setMyx(shy * m_xx + m_yx);
setMyy(shy * m_xy + m_yy);
updateState2D();
return;
case APPLY_SCALE | APPLY_TRANSLATE:
final double t_x_2 = getTx();
final double t_y_2 = getTy();
setTx(t_x_2 + shx * t_y_2);
setTy(t_y_2 + shy * t_x_2);
if (getTx() == 0.0 && getTy() == 0.0) {
mystate = mystate & ~APPLY_TRANSLATE;
state2d = mystate;
}
case APPLY_SCALE:
setMxy(shx * getMyy());
setMyx(shy * getMxx());
if (getMxy() != 0.0 || getMyx() != 0.0) {
state2d = mystate | APPLY_SHEAR;
}
return;
case APPLY_TRANSLATE:
final double t_x_3 = getTx();
final double t_y_3 = getTy();
setTx(t_x_3 + shx * t_y_3);
setTy(t_y_3 + shy * t_x_3);
if (getTx() == 0.0 && getTy() == 0.0) {
mystate = mystate & ~APPLY_TRANSLATE;
state2d = mystate;
}
case APPLY_IDENTITY:
setMxy(shx);
setMyx(shy);
if (getMxy() != 0.0 || getMyx() != 0.0) {
state2d = mystate | APPLY_SCALE | APPLY_SHEAR;
}
return;
}
}
private void preShear3D(double shx, double shy) {
switch (state3d) {
default:
stateError();
case APPLY_NON_3D:
preShear2D(shx, shy);
return;
case APPLY_TRANSLATE:
final double tx_t = getTx();
setMxy(shx);
setTx(tx_t + getTy() * shx);
setMyx(shy);
setTy(tx_t * shy + getTy());
if (shx != 0.0 || shy != 0.0) {
state3d = APPLY_3D_COMPLEX;
}
return;
case APPLY_SCALE:
setMxy(getMyy() * shx);
setMyx(getMxx() * shy);
if (getMxy() != 0.0 || getMyx() != 0.0) {
state3d = APPLY_3D_COMPLEX;
}
return;
case APPLY_SCALE | APPLY_TRANSLATE:
final double tx_st = getTx();
setMxy(getMyy() * shx);
setTx(tx_st + getTy() * shx);
setMyx(getMxx() * shy);
setTy(tx_st * shy + getTy());
if (getMxy() != 0.0 || getMyx() != 0.0) {
state3d = APPLY_3D_COMPLEX;
}
return;
case APPLY_3D_COMPLEX:
final double m_xx = getMxx();
final double m_xy = getMxy();
final double m_yx = getMyx();
final double t_x = getTx();
final double m_yy = getMyy();
final double m_xz = getMxz();
final double m_yz = getMyz();
final double t_y = getTy();
setMxx(m_xx + m_yx * shx);
setMxy(m_xy + m_yy * shx);
setMxz(m_xz + m_yz * shx);
setTx(t_x + t_y * shx);
setMyx(m_xx * shy + m_yx);
setMyy(m_xy * shy + m_yy);
setMyz(m_xz * shy + m_yz);
setTy(t_x * shy + t_y);
updateState();
return;
}
}
public void appendRotation(double angle) {
atomicChange.start();
rotate2D(angle);
atomicChange.end();
}
public void appendRotation(double angle,
double pivotX, double pivotY) {
atomicChange.start();
if (pivotX != 0.0 || pivotY != 0.0) {
translate2D(pivotX, pivotY);
rotate2D(angle);
translate2D(-pivotX, -pivotY);
} else {
rotate2D(angle);
}
atomicChange.end();
}
public void appendRotation(double angle, Point2D pivot) {
appendRotation(angle, pivot.getX(), pivot.getY());
}
public void appendRotation(double angle,
double pivotX, double pivotY, double pivotZ,
double axisX, double axisY, double axisZ) {
atomicChange.start();
if (pivotX != 0.0 || pivotY != 0.0 || pivotZ != 0.0) {
translate3D(pivotX, pivotY, pivotZ);
rotate3D(angle, axisX, axisY, axisZ);
translate3D(-pivotX, -pivotY, -pivotZ);
} else {
rotate3D(angle, axisX, axisY, axisZ);
}
atomicChange.end();
}
public void appendRotation(double angle,
double pivotX, double pivotY, double pivotZ,
Point3D axis) {
appendRotation(angle, pivotX, pivotY, pivotZ,
axis.getX(), axis.getY(), axis.getZ());
}
public void appendRotation(double angle, Point3D pivot, Point3D axis) {
appendRotation(angle, pivot.getX(), pivot.getY(), pivot.getZ(),
axis.getX(), axis.getY(), axis.getZ());
}
private void rotate3D(double angle, double axisX, double axisY, double axisZ) {
if (axisX == 0.0 && axisY == 0.0) {
if (axisZ > 0.0) {
rotate3D(angle);
} else if (axisZ < 0.0) {
rotate3D(-angle);
}
return;
}
double mag = Math.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);
if (mag == 0.0) {
return;
}
mag = 1.0 / mag;
final double ax = axisX * mag;
final double ay = axisY * mag;
final double az = axisZ * mag;
final double sinTheta = Math.sin(Math.toRadians(angle));
final double cosTheta = Math.cos(Math.toRadians(angle));
final double t = 1.0 - cosTheta;
final double xz = ax * az;
final double xy = ax * ay;
final double yz = ay * az;
final double Txx = t * ax * ax + cosTheta;
final double Txy = t * xy - sinTheta * az;
final double Txz = t * xz + sinTheta * ay;
final double Tyx = t * xy + sinTheta * az;
final double Tyy = t * ay * ay + cosTheta;
final double Tyz = t * yz - sinTheta * ax;
final double Tzx = t * xz - sinTheta * ay;
final double Tzy = t * yz + sinTheta * ax;
final double Tzz = t * az * az + cosTheta;
switch (state3d) {
default:
stateError();
case APPLY_NON_3D:
switch (state2d) {
default:
stateError();
case APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE:
case APPLY_SHEAR | APPLY_SCALE:
final double xx_sst = getMxx();
final double xy_sst = getMxy();
final double yx_sst = getMyx();
final double yy_sst = getMyy();
setMxx(xx_sst * Txx + xy_sst * Tyx);
setMxy(xx_sst * Txy + xy_sst * Tyy);
setMxz(xx_sst * Txz + xy_sst * Tyz);
setMyx(yx_sst * Txx + yy_sst * Tyx);
setMyy(yx_sst * Txy + yy_sst * Tyy);
setMyz(yx_sst * Txz + yy_sst * Tyz);
setMzx(Tzx);
setMzy(Tzy);
setMzz(Tzz);
break;
case APPLY_SHEAR | APPLY_TRANSLATE:
case APPLY_SHEAR:
final double xy_sht = getMxy();
final double yx_sht = getMyx();
setMxx(xy_sht * Tyx);
setMxy(xy_sht * Tyy);
setMxz(xy_sht * Tyz);
setMyx(yx_sht * Txx);
setMyy(yx_sht * Txy);
setMyz(yx_sht * Txz);
setMzx(Tzx);
setMzy(Tzy);
setMzz(Tzz);
break;
case APPLY_SCALE | APPLY_TRANSLATE:
case APPLY_SCALE:
final double xx_s = getMxx();
final double yy_s = getMyy();
setMxx(xx_s * Txx);
setMxy(xx_s * Txy);
setMxz(xx_s * Txz);
setMyx(yy_s * Tyx);
setMyy(yy_s * Tyy);
setMyz(yy_s * Tyz);
setMzx(Tzx);
setMzy(Tzy);
setMzz(Tzz);
break;
case APPLY_TRANSLATE:
case APPLY_IDENTITY:
setMxx(Txx);
setMxy(Txy);
setMxz(Txz);
setMyx(Tyx);
setMyy(Tyy);
setMyz(Tyz);
setMzx(Tzx);
setMzy(Tzy);
setMzz(Tzz);
break;
}
break;
case APPLY_TRANSLATE:
setMxx(Txx);
setMxy(Txy);
setMxz(Txz);
setMyx(Tyx);
setMyy(Tyy);
setMyz(Tyz);
setMzx(Tzx);
setMzy(Tzy);
setMzz(Tzz);
break;
case APPLY_SCALE:
case APPLY_SCALE | APPLY_TRANSLATE:
final double xx_st = getMxx();
final double yy_st = getMyy();
final double zz_st = getMzz();
setMxx(xx_st * Txx);
setMxy(xx_st * Txy);
setMxz(xx_st * Txz);
setMyx(yy_st * Tyx);
setMyy(yy_st * Tyy);
setMyz(yy_st * Tyz);
setMzx(zz_st * Tzx);
setMzy(zz_st * Tzy);
setMzz(zz_st * Tzz);
break;
case APPLY_3D_COMPLEX:
final double m_xx = getMxx();
final double m_xy = getMxy();
final double m_xz = getMxz();
final double m_yx = getMyx();
final double m_yy = getMyy();
final double m_yz = getMyz();
final double m_zx = getMzx();
final double m_zy = getMzy();
final double m_zz = getMzz();
setMxx(m_xx * Txx + m_xy * Tyx + m_xz * Tzx );
setMxy(m_xx * Txy + m_xy * Tyy + m_xz * Tzy );
setMxz(m_xx * Txz + m_xy * Tyz + m_xz * Tzz );
setMyx(m_yx * Txx + m_yy * Tyx + m_yz * Tzx );
setMyy(m_yx * Txy + m_yy * Tyy + m_yz * Tzy );
setMyz(m_yx * Txz + m_yy * Tyz + m_yz * Tzz );
setMzx(m_zx * Txx + m_zy * Tyx + m_zz * Tzx );
setMzy(m_zx * Txy + m_zy * Tyy + m_zz * Tzy );
setMzz(m_zx * Txz + m_zy * Tyz + m_zz * Tzz );
break;
}
updateState();
}
private static final int rot90conversion[] = {
APPLY_SHEAR,
APPLY_SHEAR | APPLY_TRANSLATE,
APPLY_SHEAR,
APPLY_SHEAR | APPLY_TRANSLATE,
APPLY_SCALE,
APPLY_SCALE | APPLY_TRANSLATE,
APPLY_SHEAR | APPLY_SCALE,
APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE,
};
private void rotate2D(double theta) {
if (state3d != APPLY_NON_3D) {
rotate3D(theta);
return;
}
double sin = Math.sin(Math.toRadians(theta));
if (sin == 1.0) {
rotate2D_90();
} else if (sin == -1.0) {
rotate2D_270();
} else {
double cos = Math.cos(Math.toRadians(theta));
if (cos == -1.0) {
rotate2D_180();
} else if (cos != 1.0) {
double M0, M1;
M0 = getMxx();
M1 = getMxy();
setMxx(cos * M0 + sin * M1);
setMxy(-sin * M0 + cos * M1);
M0 = getMyx();
M1 = getMyy();
setMyx(cos * M0 + sin * M1);
setMyy(-sin * M0 + cos * M1);
updateState2D();
}
}
}
private void rotate2D_90() {
double M0 = getMxx();
setMxx(getMxy());
setMxy(-M0);
M0 = getMyx();
setMyx(getMyy());
setMyy(-M0);
int newstate = rot90conversion[state2d];
if ((newstate & (APPLY_SHEAR | APPLY_SCALE)) == APPLY_SCALE &&
getMxx() == 1.0 && getMyy() == 1.0) {
newstate -= APPLY_SCALE;
} else if ((newstate & (APPLY_SHEAR | APPLY_SCALE)) == APPLY_SHEAR &&
getMxy() == 0.0 && getMyx() == 0.0) {
newstate = (newstate & ~APPLY_SHEAR | APPLY_SCALE);
}
state2d = newstate;
}
private void rotate2D_180() {
setMxx(-getMxx());
setMyy(-getMyy());
int oldstate = state2d;
if ((oldstate & (APPLY_SHEAR)) != 0) {
setMxy(-getMxy());
setMyx(-getMyx());
} else {
if (getMxx() == 1.0 && getMyy() == 1.0) {
state2d = oldstate & ~APPLY_SCALE;
} else {
state2d = oldstate | APPLY_SCALE;
}
}
}
private void rotate2D_270() {
double M0 = getMxx();
setMxx(-getMxy());
setMxy(M0);
M0 = getMyx();
setMyx(-getMyy());
setMyy(M0);
int newstate = rot90conversion[state2d];
if ((newstate & (APPLY_SHEAR | APPLY_SCALE)) == APPLY_SCALE &&
getMxx() == 1.0 && getMyy() == 1.0) {
newstate -= APPLY_SCALE;
} else if ((newstate & (APPLY_SHEAR | APPLY_SCALE)) == APPLY_SHEAR &&
getMxy() == 0.0 && getMyx() == 0.0) {
newstate = (newstate & ~APPLY_SHEAR | APPLY_SCALE);
}
state2d = newstate;
}
private void rotate3D(double theta) {
if (state3d == APPLY_NON_3D) {
rotate2D(theta);
return;
}
double sin = Math.sin(Math.toRadians(theta));
if (sin == 1.0) {
rotate3D_90();
} else if (sin == -1.0) {
rotate3D_270();
} else {
double cos = Math.cos(Math.toRadians(theta));
if (cos == -1.0) {
rotate3D_180();
} else if (cos != 1.0) {
double M0, M1;
M0 = getMxx();
M1 = getMxy();
setMxx(cos * M0 + sin * M1);
setMxy(-sin * M0 + cos * M1);
M0 = getMyx();
M1 = getMyy();
setMyx(cos * M0 + sin * M1);
setMyy(-sin * M0 + cos * M1);
M0 = getMzx();
M1 = getMzy();
setMzx(cos * M0 + sin * M1);
setMzy(-sin * M0 + cos * M1);
updateState();
}
}
}
private void rotate3D_90() {
double M0 = getMxx();
setMxx(getMxy());
setMxy(-M0);
M0 = getMyx();
setMyx(getMyy());
setMyy(-M0);
M0 = getMzx();
setMzx(getMzy());
setMzy(-M0);
switch(state3d) {
default:
stateError();
case APPLY_TRANSLATE:
state3d = APPLY_3D_COMPLEX;
return;
case APPLY_SCALE:
case APPLY_SCALE | APPLY_TRANSLATE:
if (getMxy() != 0.0 || getMyx() != 0.0) {
state3d = APPLY_3D_COMPLEX;
}
return;
case APPLY_3D_COMPLEX:
updateState();
return;
}
}
private void rotate3D_180() {
final double mxx = getMxx();
final double myy = getMyy();
setMxx(-mxx);
setMyy(-myy);
if (state3d == APPLY_3D_COMPLEX) {
setMxy(-getMxy());
setMyx(-getMyx());
setMzx(-getMzx());
setMzy(-getMzy());
updateState();
return;
}
if (mxx == -1.0 && myy == -1.0 && getMzz() == 1.0) {
state3d &= ~APPLY_SCALE;
} else {
state3d |= APPLY_SCALE;
}
}
private void rotate3D_270() {
double M0 = getMxx();
setMxx(-getMxy());
setMxy(M0);
M0 = getMyx();
setMyx(-getMyy());
setMyy(M0);
M0 = getMzx();
setMzx(-getMzy());
setMzy(M0);
switch(state3d) {
default:
stateError();
case APPLY_TRANSLATE:
state3d = APPLY_3D_COMPLEX;
return;
case APPLY_SCALE:
case APPLY_SCALE | APPLY_TRANSLATE:
if (getMxy() != 0.0 || getMyx() != 0.0) {
state3d = APPLY_3D_COMPLEX;
}
return;
case APPLY_3D_COMPLEX:
updateState();
return;
}
}
public void prependRotation(double angle) {
atomicChange.start();
preRotate2D(angle);
atomicChange.end();
}
public void prependRotation(double angle, double pivotX, double pivotY) {
atomicChange.start();
if (pivotX != 0.0 || pivotY != 0.0) {
preTranslate2D(-pivotX, -pivotY);
preRotate2D(angle);
preTranslate2D(pivotX, pivotY);
} else {
preRotate2D(angle);
}
atomicChange.end();
}
public void prependRotation(double angle, Point2D pivot) {
prependRotation(angle, pivot.getX(), pivot.getY());
}
public void prependRotation(double angle,
double pivotX, double pivotY, double pivotZ,
double axisX, double axisY, double axisZ) {
atomicChange.start();
if (pivotX != 0.0 || pivotY != 0.0 || pivotZ != 0.0) {
preTranslate3D(-pivotX, -pivotY, -pivotZ);
preRotate3D(angle, axisX, axisY, axisZ);
preTranslate3D(pivotX, pivotY, pivotZ);
} else {
preRotate3D(angle, axisX, axisY, axisZ);
}
atomicChange.end();
}
public void prependRotation(double angle,
double pivotX, double pivotY, double pivotZ,
Point3D axis) {
prependRotation(angle, pivotX, pivotY, pivotZ,
axis.getX(), axis.getY(), axis.getZ());
}
public void prependRotation(double angle, Point3D pivot, Point3D axis) {
prependRotation(angle, pivot.getX(), pivot.getY(), pivot.getZ(),
axis.getX(), axis.getY(), axis.getZ());
}
private void preRotate3D(double angle,
double axisX, double axisY, double axisZ) {
if (axisX == 0.0 && axisY == 0.0) {
if (axisZ > 0.0) {
preRotate3D(angle);
} else if (axisZ < 0.0) {
preRotate3D(-angle);
}
return;
}
double mag = Math.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);
if (mag == 0.0) {
return;
}
mag = 1.0 / mag;
final double ax = axisX * mag;
final double ay = axisY * mag;
final double az = axisZ * mag;
final double sinTheta = Math.sin(Math.toRadians(angle));
final double cosTheta = Math.cos(Math.toRadians(angle));
final double t = 1.0 - cosTheta;
final double xz = ax * az;
final double xy = ax * ay;
final double yz = ay * az;
final double Txx = t * ax * ax + cosTheta;
final double Txy = t * xy - sinTheta * az;
final double Txz = t * xz + sinTheta * ay;
final double Tyx = t * xy + sinTheta * az;
final double Tyy = t * ay * ay + cosTheta;
final double Tyz = t * yz - sinTheta * ax;
final double Tzx = t * xz - sinTheta * ay;
final double Tzy = t * yz + sinTheta * ax;
final double Tzz = t * az * az + cosTheta;
switch (state3d) {
default:
stateError();
case APPLY_NON_3D:
switch (state2d) {
default:
stateError();
case APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE:
final double xx_sst = getMxx();
final double xy_sst = getMxy();
final double tx_sst = getTx();
final double yx_sst = getMyx();
final double yy_sst = getMyy();
final double ty_sst = getTy();
setMxx(Txx * xx_sst + Txy * yx_sst);
setMxy(Txx * xy_sst + Txy * yy_sst);
setMxz(Txz);
setTx( Txx * tx_sst + Txy * ty_sst);
setMyx(Tyx * xx_sst + Tyy * yx_sst);
setMyy(Tyx * xy_sst + Tyy * yy_sst);
setMyz(Tyz);
setTy( Tyx * tx_sst + Tyy * ty_sst);
setMzx(Tzx * xx_sst + Tzy * yx_sst);
setMzy(Tzx * xy_sst + Tzy * yy_sst);
setMzz(Tzz);
setTz( Tzx * tx_sst + Tzy * ty_sst);
break;
case APPLY_SHEAR | APPLY_SCALE:
final double xx_ss = getMxx();
final double xy_ss = getMxy();
final double yx_ss = getMyx();
final double yy_ss = getMyy();
setMxx(Txx * xx_ss + Txy * yx_ss);
setMxy(Txx * xy_ss + Txy * yy_ss);
setMxz(Txz);
setMyx(Tyx * xx_ss + Tyy * yx_ss);
setMyy(Tyx * xy_ss + Tyy * yy_ss);
setMyz(Tyz);
setMzx(Tzx * xx_ss + Tzy * yx_ss);
setMzy(Tzx * xy_ss + Tzy * yy_ss);
setMzz(Tzz);
break;
case APPLY_SHEAR | APPLY_TRANSLATE:
final double xy_sht = getMxy();
final double tx_sht = getTx();
final double yx_sht = getMyx();
final double ty_sht = getTy();
setMxx(Txy * yx_sht);
setMxy(Txx * xy_sht);
setMxz(Txz);
setTx( Txx * tx_sht + Txy * ty_sht);
setMyx(Tyy * yx_sht);
setMyy(Tyx * xy_sht);
setMyz(Tyz);
setTy( Tyx * tx_sht + Tyy * ty_sht);
setMzx(Tzy * yx_sht);
setMzy(Tzx * xy_sht);
setMzz(Tzz);
setTz( Tzx * tx_sht + Tzy * ty_sht);
break;
case APPLY_SHEAR:
final double xy_sh = getMxy();
final double yx_sh = getMyx();
setMxx(Txy * yx_sh);
setMxy(Txx * xy_sh);
setMxz(Txz);
setMyx(Tyy * yx_sh);
setMyy(Tyx * xy_sh);
setMyz(Tyz);
setMzx(Tzy * yx_sh);
setMzy(Tzx * xy_sh);
setMzz(Tzz);
break;
case APPLY_SCALE | APPLY_TRANSLATE:
final double xx_st = getMxx();
final double tx_st = getTx();
final double yy_st = getMyy();
final double ty_st = getTy();
setMxx(Txx * xx_st);
setMxy(Txy * yy_st);
setMxz(Txz);
setTx( Txx * tx_st + Txy * ty_st);
setMyx(Tyx * xx_st);
setMyy(Tyy * yy_st);
setMyz(Tyz);
setTy( Tyx * tx_st + Tyy * ty_st);
setMzx(Tzx * xx_st);
setMzy(Tzy * yy_st);
setMzz(Tzz);
setTz( Tzx * tx_st + Tzy * ty_st);
break;
case APPLY_SCALE:
final double xx_s = getMxx();
final double yy_s = getMyy();
setMxx(Txx * xx_s);
setMxy(Txy * yy_s);
setMxz(Txz);
setMyx(Tyx * xx_s);
setMyy(Tyy * yy_s);
setMyz(Tyz);
setMzx(Tzx * xx_s);
setMzy(Tzy * yy_s);
setMzz(Tzz);
break;
case APPLY_TRANSLATE:
final double tx_t = getTx();
final double ty_t = getTy();
setMxx(Txx);
setMxy(Txy);
setMxz(Txz);
setTx( Txx * tx_t + Txy * ty_t);
setMyx(Tyx);
setMyy(Tyy);
setMyz(Tyz);
setTy( Tyx * tx_t + Tyy * ty_t);
setMzx(Tzx);
setMzy(Tzy);
setMzz(Tzz);
setTz( Tzx * tx_t + Tzy * ty_t);
break;
case APPLY_IDENTITY:
setMxx(Txx);
setMxy(Txy);
setMxz(Txz);
setMyx(Tyx);
setMyy(Tyy);
setMyz(Tyz);
setMzx(Tzx);
setMzy(Tzy);
setMzz(Tzz);
break;
}
break;
case APPLY_TRANSLATE:
final double tx_t = getTx();
final double ty_t = getTy();
final double tz_t = getTz();
setMxx(Txx);
setMxy(Txy);
setMxz(Txz);
setMyx(Tyx);
setMyy(Tyy);
setMyz(Tyz);
setMzx(Tzx);
setMzy(Tzy);
setMzz(Tzz);
setTx( Txx * tx_t + Txy * ty_t + Txz * tz_t);
setTy( Tyx * tx_t + Tyy * ty_t + Tyz * tz_t);
setTz( Tzx * tx_t + Tzy * ty_t + Tzz * tz_t);
break;
case APPLY_SCALE:
final double xx_s = getMxx();
final double yy_s = getMyy();
final double zz_s = getMzz();
setMxx(Txx * xx_s);
setMxy(Txy * yy_s);
setMxz(Txz * zz_s);
setMyx(Tyx * xx_s);
setMyy(Tyy * yy_s);
setMyz(Tyz * zz_s);
setMzx(Tzx * xx_s);
setMzy(Tzy * yy_s);
setMzz(Tzz * zz_s);
break;
case APPLY_SCALE | APPLY_TRANSLATE:
final double xx_st = getMxx();
final double tx_st = getTx();
final double yy_st = getMyy();
final double ty_st = getTy();
final double zz_st = getMzz();
final double tz_st = getTz();
setMxx(Txx * xx_st);
setMxy(Txy * yy_st);
setMxz(Txz * zz_st);
setTx( Txx * tx_st + Txy * ty_st + Txz * tz_st);
setMyx(Tyx * xx_st);
setMyy(Tyy * yy_st);
setMyz(Tyz * zz_st);
setTy( Tyx * tx_st + Tyy * ty_st + Tyz * tz_st);
setMzx(Tzx * xx_st);
setMzy(Tzy * yy_st);
setMzz(Tzz * zz_st);
setTz( Tzx * tx_st + Tzy * ty_st + Tzz * tz_st);
break;
case APPLY_3D_COMPLEX:
final double m_xx = getMxx();
final double m_xy = getMxy();
final double m_xz = getMxz();
final double t_x = getTx();
final double m_yx = getMyx();
final double m_yy = getMyy();
final double m_yz = getMyz();
final double t_y = getTy();
final double m_zx = getMzx();
final double m_zy = getMzy();
final double m_zz = getMzz();
final double t_z = getTz();
setMxx(Txx * m_xx + Txy * m_yx + Txz * m_zx );
setMxy(Txx * m_xy + Txy * m_yy + Txz * m_zy );
setMxz(Txx * m_xz + Txy * m_yz + Txz * m_zz );
setTx( Txx * t_x + Txy * t_y + Txz * t_z );
setMyx(Tyx * m_xx + Tyy * m_yx + Tyz * m_zx );
setMyy(Tyx * m_xy + Tyy * m_yy + Tyz * m_zy );
setMyz(Tyx * m_xz + Tyy * m_yz + Tyz * m_zz );
setTy( Tyx * t_x + Tyy * t_y + Tyz * t_z );
setMzx(Tzx * m_xx + Tzy * m_yx + Tzz * m_zx );
setMzy(Tzx * m_xy + Tzy * m_yy + Tzz * m_zy );
setMzz(Tzx * m_xz + Tzy * m_yz + Tzz * m_zz );
setTz( Tzx * t_x + Tzy * t_y + Tzz * t_z );
break;
}
updateState();
}
private void preRotate2D(double theta) {
if (state3d != APPLY_NON_3D) {
preRotate3D(theta);
return;
}
double sin = Math.sin(Math.toRadians(theta));
if (sin == 1.0) {
preRotate2D_90();
} else if (sin == -1.0) {
preRotate2D_270();
} else {
double cos = Math.cos(Math.toRadians(theta));
if (cos == -1.0) {
preRotate2D_180();
} else if (cos != 1.0) {
double M0, M1;
M0 = getMxx();
M1 = getMyx();
setMxx(cos * M0 - sin * M1);
setMyx(sin * M0 + cos * M1);
M0 = getMxy();
M1 = getMyy();
setMxy(cos * M0 - sin * M1);
setMyy(sin * M0 + cos * M1);
M0 = getTx();
M1 = getTy();
setTx(cos * M0 - sin * M1);
setTy(sin * M0 + cos * M1);
updateState2D();
}
}
}
private void preRotate2D_90() {
double M0 = getMxx();
setMxx(-getMyx());
setMyx(M0);
M0 = getMxy();
setMxy(-getMyy());
setMyy(M0);
M0 = getTx();
setTx(-getTy());
setTy(M0);
int newstate = rot90conversion[state2d];
if ((newstate & (APPLY_SHEAR | APPLY_SCALE)) == APPLY_SCALE &&
getMxx() == 1.0 && getMyy() == 1.0) {
newstate -= APPLY_SCALE;
} else if ((newstate & (APPLY_SHEAR | APPLY_SCALE)) == APPLY_SHEAR &&
getMxy() == 0.0 && getMyx() == 0.0) {
newstate = (newstate & ~APPLY_SHEAR | APPLY_SCALE);
}
state2d = newstate;
}
private void preRotate2D_180() {
setMxx(-getMxx());
setMxy(-getMxy());
setTx(-getTx());
setMyx(-getMyx());
setMyy(-getMyy());
setTy(-getTy());
if ((state2d & APPLY_SHEAR) != 0) {
if (getMxx() == 0.0 && getMyy() == 0.0) {
state2d &= ~APPLY_SCALE;
} else {
state2d |= APPLY_SCALE;
}
} else {
if (getMxx() == 1.0 && getMyy() == 1.0) {
state2d &= ~APPLY_SCALE;
} else {
state2d |= APPLY_SCALE;
}
}
}
private void preRotate2D_270() {
double M0 = getMxx();
setMxx(getMyx());
setMyx(-M0);
M0 = getMxy();
setMxy(getMyy());
setMyy(-M0);
M0 = getTx();
setTx(getTy());
setTy(-M0);
int newstate = rot90conversion[state2d];
if ((newstate & (APPLY_SHEAR | APPLY_SCALE)) == APPLY_SCALE &&
getMxx() == 1.0 && getMyy() == 1.0) {
newstate -= APPLY_SCALE;
} else if ((newstate & (APPLY_SHEAR | APPLY_SCALE)) == APPLY_SHEAR &&
getMxy() == 0.0 && getMyx() == 0.0) {
newstate = (newstate & ~APPLY_SHEAR | APPLY_SCALE);
}
state2d = newstate;
}
private void preRotate3D(double theta) {
if (state3d == APPLY_NON_3D) {
preRotate2D(theta);
return;
}
double sin = Math.sin(Math.toRadians(theta));
if (sin == 1.0) {
preRotate3D_90();
} else if (sin == -1.0) {
preRotate3D_270();
} else {
double cos = Math.cos(Math.toRadians(theta));
if (cos == -1.0) {
preRotate3D_180();
} else if (cos != 1.0) {
double M0, M1;
M0 = getMxx();
M1 = getMyx();
setMxx(cos * M0 - sin * M1);
setMyx(sin * M0 + cos * M1);
M0 = getMxy();
M1 = getMyy();
setMxy(cos * M0 - sin * M1);
setMyy(sin * M0 + cos * M1);
M0 = getMxz();
M1 = getMyz();
setMxz(cos * M0 - sin * M1);
setMyz(sin * M0 + cos * M1);
M0 = getTx();
M1 = getTy();
setTx(cos * M0 - sin * M1);
setTy(sin * M0 + cos * M1);
updateState();
}
}
}
private void preRotate3D_90() {
double M0 = getMxx();
setMxx(-getMyx());
setMyx(M0);
M0 = getMxy();
setMxy(-getMyy());
setMyy(M0);
M0 = getMxz();
setMxz(-getMyz());
setMyz(M0);
M0 = getTx();
setTx(-getTy());
setTy(M0);
switch(state3d) {
default:
stateError();
case APPLY_TRANSLATE:
state3d = APPLY_3D_COMPLEX;
return;
case APPLY_SCALE:
case APPLY_SCALE | APPLY_TRANSLATE:
if (getMxy() != 0.0 || getMyx() != 0.0) {
state3d = APPLY_3D_COMPLEX;
}
return;
case APPLY_3D_COMPLEX:
updateState();
return;
}
}
private void preRotate3D_180() {
final double mxx = getMxx();
final double myy = getMyy();
setMxx(-mxx);
setMyy(-myy);
setTx(-getTx());
setTy(-getTy());
if (state3d == APPLY_3D_COMPLEX) {
setMxy(-getMxy());
setMxz(-getMxz());
setMyx(-getMyx());
setMyz(-getMyz());
updateState();
return;
}
if (mxx == -1.0 && myy == -1.0 && getMzz() == 1.0) {
state3d &= ~APPLY_SCALE;
} else {
state3d |= APPLY_SCALE;
}
}
private void preRotate3D_270() {
double M0 = getMxx();
setMxx(getMyx());
setMyx(-M0);
M0 = getMxy();
setMxy(getMyy());
setMyy(-M0);
M0 = getMxz();
setMxz(getMyz());
setMyz(-M0);
M0 = getTx();
setTx(getTy());
setTy(-M0);
switch(state3d) {
default:
stateError();
case APPLY_TRANSLATE:
state3d = APPLY_3D_COMPLEX;
return;
case APPLY_SCALE:
case APPLY_SCALE | APPLY_TRANSLATE:
if (getMxy() != 0.0 || getMyx() != 0.0) {
state3d = APPLY_3D_COMPLEX;
}
return;
case APPLY_3D_COMPLEX:
updateState();
return;
}
}
@Override
public Point2D transform(double x, double y) {
ensureCanTransform2DPoint();
switch (state2d) {
default:
stateError();
case APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE:
return new Point2D(
getMxx() * x + getMxy() * y + getTx(),
getMyx() * x + getMyy() * y + getTy());
case APPLY_SHEAR | APPLY_SCALE:
return new Point2D(
getMxx() * x + getMxy() * y,
getMyx() * x + getMyy() * y);
case APPLY_SHEAR | APPLY_TRANSLATE:
return new Point2D(
getMxy() * y + getTx(),
getMyx() * x + getTy());
case APPLY_SHEAR:
return new Point2D(getMxy() * y, getMyx() * x);
case APPLY_SCALE | APPLY_TRANSLATE:
return new Point2D(
getMxx() * x + getTx(),
getMyy() * y + getTy());
case APPLY_SCALE:
return new Point2D(getMxx() * x, getMyy() * y);
case APPLY_TRANSLATE:
return new Point2D(x + getTx(), y + getTy());
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
getMxx() * x + getMxy() * y + getTx(),
getMyx() * x + getMyy() * y + getTy(), z);
case APPLY_SHEAR | APPLY_SCALE:
return new Point3D(
getMxx() * x + getMxy() * y,
getMyx() * x + getMyy() * y, z);
case APPLY_SHEAR | APPLY_TRANSLATE:
return new Point3D(
getMxy() * y + getTx(), getMyx() * x + getTy(),
z);
case APPLY_SHEAR:
return new Point3D(getMxy() * y, getMyx() * x, z);
case APPLY_SCALE | APPLY_TRANSLATE:
return new Point3D(
getMxx() * x + getTx(), getMyy() * y + getTy(),
z);
case APPLY_SCALE:
return new Point3D(getMxx() * x, getMyy() * y, z);
case APPLY_TRANSLATE:
return new Point3D(x + getTx(), y + getTy(), z);
case APPLY_IDENTITY:
return new Point3D(x, y, z);
}
case APPLY_TRANSLATE:
return new Point3D(x + getTx(), y + getTy(), z + getTz());
case APPLY_SCALE:
return new Point3D(getMxx() * x, getMyy() * y, getMzz() * z);
case APPLY_SCALE | APPLY_TRANSLATE:
return new Point3D(
getMxx() * x + getTx(),
getMyy() * y + getTy(),
getMzz() * z + getTz());
case APPLY_3D_COMPLEX:
return new Point3D(
getMxx() * x + getMxy() * y + getMxz() * z + getTx(),
getMyx() * x + getMyy() * y + getMyz() * z + getTy(),
getMzx() * x + getMzy() * y + getMzz() * z + getTz());
}
}
@Override
void transform2DPointsImpl(double[] srcPts, int srcOff,
double[] dstPts, int dstOff, int numPts) {
double mxx, mxy, tx, myx, myy, ty;
switch (state2d) {
default:
stateError();
case APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE:
mxx = getMxx(); mxy = getMxy(); tx = getTx();
myx = getMyx(); myy = getMyy(); ty = getTy();
while (--numPts >= 0) {
final double x = srcPts[srcOff++];
final double y = srcPts[srcOff++];
dstPts[dstOff++] = mxx * x + mxy * y + tx;
dstPts[dstOff++] = myx * x + myy * y + ty;
}
return;
case APPLY_SHEAR | APPLY_SCALE:
mxx = getMxx(); mxy = getMxy();
myx = getMyx(); myy = getMyy();
while (--numPts >= 0) {
final double x = srcPts[srcOff++];
final double y = srcPts[srcOff++];
dstPts[dstOff++] = mxx * x + mxy * y;
dstPts[dstOff++] = myx * x + myy * y;
}
return;
case APPLY_SHEAR | APPLY_TRANSLATE:
mxy = getMxy(); tx = getTx();
myx = getMyx(); ty = getTy();
while (--numPts >= 0) {
final double x = srcPts[srcOff++];
dstPts[dstOff++] = mxy * srcPts[srcOff++] + tx;
dstPts[dstOff++] = myx * x + ty;
}
return;
case APPLY_SHEAR:
mxy = getMxy();
myx = getMyx();
while (--numPts >= 0) {
final double x = srcPts[srcOff++];
dstPts[dstOff++] = mxy * srcPts[srcOff++];
dstPts[dstOff++] = myx * x;
}
return;
case APPLY_SCALE | APPLY_TRANSLATE:
mxx = getMxx(); tx = getTx();
myy = getMyy(); ty = getTy();
while (--numPts >= 0) {
dstPts[dstOff++] = mxx * srcPts[srcOff++] + tx;
dstPts[dstOff++] = myy * srcPts[srcOff++] + ty;
}
return;
case APPLY_SCALE:
mxx = getMxx();
myy = getMyy();
while (--numPts >= 0) {
dstPts[dstOff++] = mxx * srcPts[srcOff++];
dstPts[dstOff++] = myy * srcPts[srcOff++];
}
return;
case APPLY_TRANSLATE:
tx = getTx();
ty = getTy();
while (--numPts >= 0) {
dstPts[dstOff++] = srcPts[srcOff++] + tx;
dstPts[dstOff++] = srcPts[srcOff++] + ty;
}
return;
case APPLY_IDENTITY:
if (srcPts != dstPts || srcOff != dstOff) {
System.arraycopy(srcPts, srcOff, dstPts, dstOff,
numPts * 2);
}
return;
}
}
@Override
void transform3DPointsImpl(double[] srcPts, int srcOff,
double[] dstPts, int dstOff, int numPts) {
double mxx, mxy, tx, myx, myy, ty, mzz, tz;
switch(state3d) {
default:
stateError();
case APPLY_NON_3D:
switch (state2d) {
default:
stateError();
case APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE:
mxx = getMxx(); mxy = getMxy(); tx = getTx();
myx = getMyx(); myy = getMyy(); ty = getTy();
while (--numPts >= 0) {
final double x = srcPts[srcOff++];
final double y = srcPts[srcOff++];
dstPts[dstOff++] = mxx * x + mxy * y + tx;
dstPts[dstOff++] = myx * x + myy * y + ty;
dstPts[dstOff++] = srcPts[srcOff++];
}
return;
case APPLY_SHEAR | APPLY_SCALE:
mxx = getMxx(); mxy = getMxy();
myx = getMyx(); myy = getMyy();
while (--numPts >= 0) {
final double x = srcPts[srcOff++];
final double y = srcPts[srcOff++];
dstPts[dstOff++] = mxx * x + mxy * y;
dstPts[dstOff++] = myx * x + myy * y;
dstPts[dstOff++] = srcPts[srcOff++];
}
return;
case APPLY_SHEAR | APPLY_TRANSLATE:
mxy = getMxy(); tx = getTx();
myx = getMyx(); ty = getTy();
while (--numPts >= 0) {
final double x = srcPts[srcOff++];
dstPts[dstOff++] = mxy * srcPts[srcOff++] + tx;
dstPts[dstOff++] = myx * x + ty;
dstPts[dstOff++] = srcPts[srcOff++];
}
return;
case APPLY_SHEAR:
mxy = getMxy();
myx = getMyx();
while (--numPts >= 0) {
final double x = srcPts[srcOff++];
dstPts[dstOff++] = mxy * srcPts[srcOff++];
dstPts[dstOff++] = myx * x;
dstPts[dstOff++] = srcPts[srcOff++];
}
return;
case APPLY_SCALE | APPLY_TRANSLATE:
mxx = getMxx(); tx = getTx();
myy = getMyy(); ty = getTy();
while (--numPts >= 0) {
dstPts[dstOff++] = mxx * srcPts[srcOff++] + tx;
dstPts[dstOff++] = myy * srcPts[srcOff++] + ty;
dstPts[dstOff++] = srcPts[srcOff++];
}
return;
case APPLY_SCALE:
mxx = getMxx();
myy = getMyy();
while (--numPts >= 0) {
dstPts[dstOff++] = mxx * srcPts[srcOff++];
dstPts[dstOff++] = myy * srcPts[srcOff++];
dstPts[dstOff++] = srcPts[srcOff++];
}
return;
case APPLY_TRANSLATE:
tx = getTx();
ty = getTy();
while (--numPts >= 0) {
dstPts[dstOff++] = srcPts[srcOff++] + tx;
dstPts[dstOff++] = srcPts[srcOff++] + ty;
dstPts[dstOff++] = srcPts[srcOff++];
}
return;
case APPLY_IDENTITY:
if (srcPts != dstPts || srcOff != dstOff) {
System.arraycopy(srcPts, srcOff, dstPts, dstOff,
numPts * 3);
}
return;
}
case APPLY_TRANSLATE:
tx = getTx();
ty = getTy();
tz = getTz();
while (--numPts >= 0) {
dstPts[dstOff++] = srcPts[srcOff++] + tx;
dstPts[dstOff++] = srcPts[srcOff++] + ty;
dstPts[dstOff++] = srcPts[srcOff++] + tz;
}
return;
case APPLY_SCALE:
mxx = getMxx();
myy = getMyy();
mzz = getMzz();
while (--numPts >= 0) {
dstPts[dstOff++] = mxx * srcPts[srcOff++];
dstPts[dstOff++] = myy * srcPts[srcOff++];
dstPts[dstOff++] = mzz * srcPts[srcOff++];
}
return;
case APPLY_SCALE | APPLY_TRANSLATE:
mxx = getMxx(); tx = getTx();
myy = getMyy(); ty = getTy();
mzz = getMzz(); tz = getTz();
while (--numPts >= 0) {
dstPts[dstOff++] = mxx * srcPts[srcOff++] + tx;
dstPts[dstOff++] = myy * srcPts[srcOff++] + ty;
dstPts[dstOff++] = mzz * srcPts[srcOff++] + tz;
}
return;
case APPLY_3D_COMPLEX:
mxx = getMxx();
mxy = getMxy();
double mxz = getMxz();
tx = getTx();
myx = getMyx();
myy = getMyy();
double myz = getMyz();
ty = getTy();
double mzx = getMzx();
double mzy = getMzy();
mzz = getMzz();
tz = getTz();
while (--numPts >= 0) {
final double x = srcPts[srcOff++];
final double y = srcPts[srcOff++];
final double z = srcPts[srcOff++];
dstPts[dstOff++] = mxx * x + mxy * y + mxz * z + tx;
dstPts[dstOff++] = myx * x + myy * y + myz * z + ty;
dstPts[dstOff++] = mzx * x + mzy * y + mzz * z + tz;
}
return;
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
getMxx() * x + getMxy() * y,
getMyx() * x + getMyy() * y);
case APPLY_SHEAR | APPLY_TRANSLATE:
case APPLY_SHEAR:
return new Point2D(getMxy() * y, getMyx() * x);
case APPLY_SCALE | APPLY_TRANSLATE:
case APPLY_SCALE:
return new Point2D(getMxx() * x, getMyy() * y);
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
getMxx() * x + getMxy() * y,
getMyx() * x + getMyy() * y, z);
case APPLY_SHEAR | APPLY_TRANSLATE:
case APPLY_SHEAR:
return new Point3D(getMxy() * y, getMyx() * x, z);
case APPLY_SCALE | APPLY_TRANSLATE:
case APPLY_SCALE:
return new Point3D(getMxx() * x, getMyy() * y, z);
case APPLY_TRANSLATE:
case APPLY_IDENTITY:
return new Point3D(x, y, z);
}
case APPLY_TRANSLATE:
return new Point3D(x, y, z);
case APPLY_SCALE:
case APPLY_SCALE | APPLY_TRANSLATE:
return new Point3D(getMxx() * x, getMyy() * y, getMzz() * z);
case APPLY_3D_COMPLEX:
return new Point3D(
getMxx() * x + getMxy() * y + getMxz() * z,
getMyx() * x + getMyy() * y + getMyz() * z,
getMzx() * x + getMzy() * y + getMzz() * z);
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
final double mxy_st = getMxy();
final double myx_st = getMyx();
if (mxy_st == 0.0 || myx_st == 0.0) {
throw new NonInvertibleTransformException("Determinant is 0");
}
return new Point2D(
(1.0 / myx_st) * y - getTy() / myx_st,
(1.0 / mxy_st) * x - getTx() / mxy_st);
case APPLY_SHEAR:
final double mxy_s = getMxy();
final double myx_s = getMyx();
if (mxy_s == 0.0 || myx_s == 0.0) {
throw new NonInvertibleTransformException("Determinant is 0");
}
return new Point2D((1.0 / myx_s) * y, (1.0 / mxy_s) * x);
case APPLY_SCALE | APPLY_TRANSLATE:
final double mxx_st = getMxx();
final double myy_st = getMyy();
if (mxx_st == 0.0 || myy_st == 0.0) {
throw new NonInvertibleTransformException("Determinant is 0");
}
return new Point2D(
(1.0 / mxx_st) * x - getTx() / mxx_st,
(1.0 / myy_st) * y - getTy() / myy_st);
case APPLY_SCALE:
final double mxx_s = getMxx();
final double myy_s = getMyy();
if (mxx_s == 0.0 || myy_s == 0.0) {
throw new NonInvertibleTransformException("Determinant is 0");
}
return new Point2D((1.0 / mxx_s) * x, (1.0 / myy_s) * y);
case APPLY_TRANSLATE:
return new Point2D(x - getTx(), y - getTy());
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
final double mxy_st = getMxy();
final double myx_st = getMyx();
if (mxy_st == 0.0 || myx_st == 0.0) {
throw new NonInvertibleTransformException(
"Determinant is 0");
}
return new Point3D(
(1.0 / myx_st) * y - getTy() / myx_st,
(1.0 / mxy_st) * x - getTx() / mxy_st, z);
case APPLY_SHEAR:
final double mxy_s = getMxy();
final double myx_s = getMyx();
if (mxy_s == 0.0 || myx_s == 0.0) {
throw new NonInvertibleTransformException(
"Determinant is 0");
}
return new Point3D(
(1.0 / myx_s) * y,
(1.0 / mxy_s) * x, z);
case APPLY_SCALE | APPLY_TRANSLATE:
final double mxx_st = getMxx();
final double myy_st = getMyy();
if (mxx_st == 0.0 || myy_st == 0.0) {
throw new NonInvertibleTransformException(
"Determinant is 0");
}
return new Point3D(
(1.0 / mxx_st) * x - getTx() / mxx_st,
(1.0 / myy_st) * y - getTy() / myy_st, z);
case APPLY_SCALE:
final double mxx_s = getMxx();
final double myy_s = getMyy();
if (mxx_s == 0.0 || myy_s == 0.0) {
throw new NonInvertibleTransformException(
"Determinant is 0");
}
return new Point3D((1.0 / mxx_s) * x, (1.0 / myy_s) * y, z);
case APPLY_TRANSLATE:
return new Point3D(x - getTx(), y - getTy(), z);
case APPLY_IDENTITY:
return new Point3D(x, y, z);
}
case APPLY_TRANSLATE:
return new Point3D(x - getTx(), y - getTy(), z - getTz());
case APPLY_SCALE:
final double mxx_s = getMxx();
final double myy_s = getMyy();
final double mzz_s = getMzz();
if (mxx_s == 0.0 || myy_s == 0.0 || mzz_s == 0.0) {
throw new NonInvertibleTransformException("Determinant is 0");
}
return new Point3D(
(1.0 / mxx_s) * x,
(1.0 / myy_s) * y,
(1.0 / mzz_s) * z);
case APPLY_SCALE | APPLY_TRANSLATE:
final double mxx_st = getMxx();
final double myy_st = getMyy();
final double mzz_st = getMzz();
if (mxx_st == 0.0 || myy_st == 0.0 || mzz_st == 0.0) {
throw new NonInvertibleTransformException("Determinant is 0");
}
return new Point3D(
(1.0 / mxx_st) * x - getTx() / mxx_st,
(1.0 / myy_st) * y - getTy() / myy_st,
(1.0 / mzz_st) * z - getTz() / mzz_st);
case APPLY_3D_COMPLEX:
return super.inverseTransform(x, y, z);
}
}
@Override
void inverseTransform2DPointsImpl(double[] srcPts, int srcOff,
double[] dstPts, int dstOff, int numPts)
throws NonInvertibleTransformException {
double mxx, mxy, tx, myx, myy, ty, tmp;
switch (state2d) {
default:
super.inverseTransform2DPointsImpl(srcPts, srcOff,
dstPts, dstOff, numPts);
return;
case APPLY_SHEAR | APPLY_TRANSLATE:
mxy = getMxy(); tx = getTx();
myx = getMyx(); ty = getTy();
if (mxy == 0.0 || myx == 0.0) {
throw new NonInvertibleTransformException("Determinant is 0");
}
tmp = tx;
tx = -ty / myx;
ty = -tmp / mxy;
tmp = myx;
myx = 1.0 / mxy;
mxy = 1.0 / tmp;
while (--numPts >= 0) {
final double x = srcPts[srcOff++];
dstPts[dstOff++] = mxy * srcPts[srcOff++] + tx;
dstPts[dstOff++] = myx * x + ty;
}
return;
case APPLY_SHEAR:
mxy = getMxy();
myx = getMyx();
if (mxy == 0.0 || myx == 0.0) {
throw new NonInvertibleTransformException("Determinant is 0");
}
tmp = myx;
myx = 1.0 / mxy;
mxy = 1.0 / tmp;
while (--numPts >= 0) {
final double x = srcPts[srcOff++];
dstPts[dstOff++] = mxy * srcPts[srcOff++];
dstPts[dstOff++] = myx * x;
}
return;
case APPLY_SCALE | APPLY_TRANSLATE:
mxx = getMxx(); tx = getTx();
myy = getMyy(); ty = getTy();
if (mxx == 0.0 || myy == 0.0) {
throw new NonInvertibleTransformException("Determinant is 0");
}
tx = -tx / mxx;
ty = -ty / myy;
mxx = 1.0 / mxx;
myy = 1.0 / myy;
while (--numPts >= 0) {
dstPts[dstOff++] = mxx * srcPts[srcOff++] + tx;
dstPts[dstOff++] = myy * srcPts[srcOff++] + ty;
}
return;
case APPLY_SCALE:
mxx = getMxx();
myy = getMyy();
if (mxx == 0.0 || myy == 0.0) {
throw new NonInvertibleTransformException("Determinant is 0");
}
mxx = 1.0 / mxx;
myy = 1.0 / myy;
while (--numPts >= 0) {
dstPts[dstOff++] = mxx * srcPts[srcOff++];
dstPts[dstOff++] = myy * srcPts[srcOff++];
}
return;
case APPLY_TRANSLATE:
tx = getTx();
ty = getTy();
while (--numPts >= 0) {
dstPts[dstOff++] = srcPts[srcOff++] - tx;
dstPts[dstOff++] = srcPts[srcOff++] - ty;
}
return;
case APPLY_IDENTITY:
if (srcPts != dstPts || srcOff != dstOff) {
System.arraycopy(srcPts, srcOff, dstPts, dstOff,
numPts * 2);
}
return;
}
}
@Override
void inverseTransform3DPointsImpl(double[] srcPts, int srcOff,
double[] dstPts, int dstOff, int numPts)
throws NonInvertibleTransformException {
double mxx, mxy, tx, myx, myy, ty, mzz, tz, tmp;
switch (state3d) {
default:
stateError();
case APPLY_NON_3D:
switch (state2d) {
default:
super.inverseTransform3DPointsImpl(srcPts, srcOff,
dstPts, dstOff, numPts);
return;
case APPLY_SHEAR | APPLY_TRANSLATE:
mxy = getMxy(); tx = getTx();
myx = getMyx(); ty = getTy();
if (mxy == 0.0 || myx == 0.0) {
throw new NonInvertibleTransformException(
"Determinant is 0");
}
tmp = tx;
tx = -ty / myx;
ty = -tmp / mxy;
tmp = myx;
myx = 1.0 / mxy;
mxy = 1.0 / tmp;
while (--numPts >= 0) {
final double x = srcPts[srcOff++];
dstPts[dstOff++] = mxy * srcPts[srcOff++] + tx;
dstPts[dstOff++] = myx * x + ty;
dstPts[dstOff++] = srcPts[srcOff++];
}
return;
case APPLY_SHEAR:
mxy = getMxy();
myx = getMyx();
if (mxy == 0.0 || myx == 0.0) {
throw new NonInvertibleTransformException(
"Determinant is 0");
}
tmp = myx;
myx = 1.0 / mxy;
mxy = 1.0 / tmp;
while (--numPts >= 0) {
final double x = srcPts[srcOff++];
dstPts[dstOff++] = mxy * srcPts[srcOff++];
dstPts[dstOff++] = myx * x;
dstPts[dstOff++] = srcPts[srcOff++];
}
return;
case APPLY_SCALE | APPLY_TRANSLATE:
mxx = getMxx(); tx = getTx();
myy = getMyy(); ty = getTy();
if (mxx == 0.0 || myy == 0.0) {
throw new NonInvertibleTransformException(
"Determinant is 0");
}
tx = -tx / mxx;
ty = -ty / myy;
mxx = 1.0 / mxx;
myy = 1.0 / myy;
while (--numPts >= 0) {
dstPts[dstOff++] = mxx * srcPts[srcOff++] + tx;
dstPts[dstOff++] = myy * srcPts[srcOff++] + ty;
dstPts[dstOff++] = srcPts[srcOff++];
}
return;
case APPLY_SCALE:
mxx = getMxx();
myy = getMyy();
if (mxx == 0.0 || myy == 0.0) {
throw new NonInvertibleTransformException(
"Determinant is 0");
}
mxx = 1.0 / mxx;
myy = 1.0 / myy;
while (--numPts >= 0) {
dstPts[dstOff++] = mxx * srcPts[srcOff++];
dstPts[dstOff++] = myy * srcPts[srcOff++];
dstPts[dstOff++] = srcPts[srcOff++];
}
return;
case APPLY_TRANSLATE:
tx = getTx();
ty = getTy();
while (--numPts >= 0) {
dstPts[dstOff++] = srcPts[srcOff++] - tx;
dstPts[dstOff++] = srcPts[srcOff++] - ty;
dstPts[dstOff++] = srcPts[srcOff++];
}
return;
case APPLY_IDENTITY:
if (srcPts != dstPts || srcOff != dstOff) {
System.arraycopy(srcPts, srcOff, dstPts, dstOff,
numPts * 3);
}
return;
}
case APPLY_TRANSLATE:
tx = getTx();
ty = getTy();
tz = getTz();
while (--numPts >= 0) {
dstPts[dstOff++] = srcPts[srcOff++] - tx;
dstPts[dstOff++] = srcPts[srcOff++] - ty;
dstPts[dstOff++] = srcPts[srcOff++] - tz;
}
return;
case APPLY_SCALE:
mxx = getMxx();
myy = getMyy();
mzz = getMzz();
if (mxx == 0.0 || myy == 0.0 | mzz == 0.0) {
throw new NonInvertibleTransformException("Determinant is 0");
}
mxx = 1.0 / mxx;
myy = 1.0 / myy;
mzz = 1.0 / mzz;
while (--numPts >= 0) {
dstPts[dstOff++] = mxx * srcPts[srcOff++];
dstPts[dstOff++] = myy * srcPts[srcOff++];
dstPts[dstOff++] = mzz * srcPts[srcOff++];
}
return;
case APPLY_SCALE | APPLY_TRANSLATE:
mxx = getMxx(); tx = getTx();
myy = getMyy(); ty = getTy();
mzz = getMzz(); tz = getTz();
if (mxx == 0.0 || myy == 0.0 || mzz == 0.0) {
throw new NonInvertibleTransformException("Determinant is 0");
}
tx = -tx / mxx;
ty = -ty / myy;
tz = -tz / mzz;
mxx = 1.0 / mxx;
myy = 1.0 / myy;
mzz = 1.0 / mzz;
while (--numPts >= 0) {
dstPts[dstOff++] = mxx * srcPts[srcOff++] + tx;
dstPts[dstOff++] = myy * srcPts[srcOff++] + ty;
dstPts[dstOff++] = mzz * srcPts[srcOff++] + tz;
}
return;
case APPLY_3D_COMPLEX:
super.inverseTransform3DPointsImpl(srcPts, srcOff,
dstPts, dstOff, numPts);
return;
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
final double mxy_s = getMxy();
final double myx_s = getMyx();
if (mxy_s == 0.0 || myx_s == 0.0) {
throw new NonInvertibleTransformException("Determinant is 0");
}
return new Point2D((1.0 / myx_s) * y, (1.0 / mxy_s) * x);
case APPLY_SCALE | APPLY_TRANSLATE:
case APPLY_SCALE:
final double mxx_s = getMxx();
final double myy_s = getMyy();
if (mxx_s == 0.0 || myy_s == 0.0) {
throw new NonInvertibleTransformException("Determinant is 0");
}
return new Point2D((1.0 / mxx_s) * x, (1.0 / myy_s) * y);
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
final double mxy_s = getMxy();
final double myx_s = getMyx();
if (mxy_s == 0.0 || myx_s == 0.0) {
throw new NonInvertibleTransformException(
"Determinant is 0");
}
return new Point3D(
(1.0 / myx_s) * y,
(1.0 / mxy_s) * x, z);
case APPLY_SCALE | APPLY_TRANSLATE:
case APPLY_SCALE:
final double mxx_s = getMxx();
final double myy_s = getMyy();
if (mxx_s == 0.0 || myy_s == 0.0) {
throw new NonInvertibleTransformException(
"Determinant is 0");
}
return new Point3D(
(1.0 / mxx_s) * x,
(1.0 / myy_s) * y, z);
case APPLY_TRANSLATE:
case APPLY_IDENTITY:
return new Point3D(x, y, z);
}
case APPLY_TRANSLATE:
return new Point3D(x, y, z);
case APPLY_SCALE | APPLY_TRANSLATE:
case APPLY_SCALE:
final double mxx_s = getMxx();
final double myy_s = getMyy();
final double mzz_s = getMzz();
if (mxx_s == 0.0 || myy_s == 0.0 || mzz_s == 0.0) {
throw new NonInvertibleTransformException("Determinant is 0");
}
return new Point3D(
(1.0 / mxx_s) * x,
(1.0 / myy_s) * y,
(1.0 / mzz_s) * z);
case APPLY_3D_COMPLEX:
return super.inverseDeltaTransform(x, y, z);
}
}
@Override
public String toString() {
final StringBuilder sb = new StringBuilder("Affine [\n");
sb.append("\t").append(getMxx());
sb.append(", ").append(getMxy());
sb.append(", ").append(getMxz());
sb.append(", ").append(getTx());
sb.append('\n');
sb.append("\t").append(getMyx());
sb.append(", ").append(getMyy());
sb.append(", ").append(getMyz());
sb.append(", ").append(getTy());
sb.append('\n');
sb.append("\t").append(getMzx());
sb.append(", ").append(getMzy());
sb.append(", ").append(getMzz());
sb.append(", ").append(getTz());
return sb.append("\n]").toString();
}
private void updateState() {
updateState2D();
state3d = APPLY_NON_3D;
if (getMxz() != 0.0 ||
getMyz() != 0.0 ||
getMzx() != 0.0 ||
getMzy() != 0.0)
{
state3d = APPLY_3D_COMPLEX;
} else {
if ((state2d & APPLY_SHEAR) == 0) {
if (getTz() != 0.0) {
state3d |= APPLY_TRANSLATE;
}
if (getMzz() != 1.0) {
state3d |= APPLY_SCALE;
}
if (state3d != APPLY_NON_3D) {
state3d |= (state2d & (APPLY_SCALE | APPLY_TRANSLATE));
}
} else {
if (getMzz() != 1.0 || getTz() != 0.0) {
state3d = APPLY_3D_COMPLEX;
}
}
}
}
private void updateState2D() {
if (getMxy() == 0.0 && getMyx() == 0.0) {
if (getMxx() == 1.0 && getMyy() == 1.0) {
if (getTx() == 0.0 && getTy() == 0.0) {
state2d = APPLY_IDENTITY;
} else {
state2d = APPLY_TRANSLATE;
}
} else {
if (getTx() == 0.0 && getTy() == 0.0) {
state2d = APPLY_SCALE;
} else {
state2d = (APPLY_SCALE | APPLY_TRANSLATE);
}
}
} else {
if (getMxx() == 0.0 && getMyy() == 0.0) {
if (getTx() == 0.0 && getTy() == 0.0) {
state2d = APPLY_SHEAR;
} else {
state2d = (APPLY_SHEAR | APPLY_TRANSLATE);
}
} else {
if (getTx() == 0.0 && getTy() == 0.0) {
state2d = (APPLY_SHEAR | APPLY_SCALE);
} else {
state2d = (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE);
}
}
}
}
private static void stateError() {
throw new InternalError("missing case in a switch");
}
@Override
void apply(final Affine3D trans) {
trans.concatenate(getMxx(), getMxy(), getMxz(), getTx(),
getMyx(), getMyy(), getMyz(), getTy(),
getMzx(), getMzy(), getMzz(), getTz());
}
@Override
BaseTransform derive(final BaseTransform trans) {
switch(state3d) {
default:
stateError();
case APPLY_NON_3D:
switch(state2d) {
case APPLY_IDENTITY:
return trans;
case APPLY_TRANSLATE:
return trans.deriveWithTranslation(getTx(), getTy());
case APPLY_SCALE:
return trans.deriveWithScale(getMxx(), getMyy(), 1.0);
case APPLY_SCALE | APPLY_TRANSLATE:
default:
return trans.deriveWithConcatenation(
getMxx(), getMyx(),
getMxy(), getMyy(),
getTx(), getTy());
}
case APPLY_TRANSLATE:
return trans.deriveWithTranslation(getTx(), getTy(), getTz());
case APPLY_SCALE:
return trans.deriveWithScale(getMxx(), getMyy(), getMzz());
case APPLY_SCALE | APPLY_TRANSLATE:
case APPLY_3D_COMPLEX:
return trans.deriveWithConcatenation(
getMxx(), getMxy(), getMxz(), getTx(),
getMyx(), getMyy(), getMyz(), getTy(),
getMzx(), getMzy(), getMzz(), getTz());
}
}
private class AffineAtomicChange {
private boolean running = false;
private void start() {
if (running) {
throw new InternalError("Affine internal error: "
+ "trying to run inner atomic operation");
}
if (mxx != null) mxx.preProcessAtomicChange();
if (mxy != null) mxy.preProcessAtomicChange();
if (mxz != null) mxz.preProcessAtomicChange();
if (tx != null) tx.preProcessAtomicChange();
if (myx != null) myx.preProcessAtomicChange();
if (myy != null) myy.preProcessAtomicChange();
if (myz != null) myz.preProcessAtomicChange();
if (ty != null) ty.preProcessAtomicChange();
if (mzx != null) mzx.preProcessAtomicChange();
if (mzy != null) mzy.preProcessAtomicChange();
if (mzz != null) mzz.preProcessAtomicChange();
if (tz != null) tz.preProcessAtomicChange();
running = true;
}
private void end() {
running = false;
transformChanged();
if (mxx != null) mxx.postProcessAtomicChange();
if (mxy != null) mxy.postProcessAtomicChange();
if (mxz != null) mxz.postProcessAtomicChange();
if (tx != null) tx.postProcessAtomicChange();
if (myx != null) myx.postProcessAtomicChange();
if (myy != null) myy.postProcessAtomicChange();
if (myz != null) myz.postProcessAtomicChange();
if (ty != null) ty.postProcessAtomicChange();
if (mzx != null) mzx.postProcessAtomicChange();
if (mzy != null) mzy.postProcessAtomicChange();
if (mzz != null) mzz.postProcessAtomicChange();
if (tz != null) tz.postProcessAtomicChange();
}
private void cancel() {
running = false;
}
private boolean runs() {
return running;
}
}
int getState2d() {
return state2d;
}
int getState3d() {
return state3d;
}
boolean atomicChangeRuns() {
return atomicChange.runs();
}
}
