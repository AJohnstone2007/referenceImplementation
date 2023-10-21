package com.sun.javafx.geom.transform;
import com.sun.javafx.geom.Point2D;
public class Affine2D extends AffineBase {
private Affine2D(double mxx, double myx,
double mxy, double myy,
double mxt, double myt,
int state)
{
this.mxx = mxx;
this.myx = myx;
this.mxy = mxy;
this.myy = myy;
this.mxt = mxt;
this.myt = myt;
this.state = state;
this.type = TYPE_UNKNOWN;
}
public Affine2D() {
mxx = myy = 1.0;
}
public Affine2D(BaseTransform Tx) {
setTransform(Tx);
}
public Affine2D(float mxx, float myx,
float mxy, float myy,
float mxt, float myt)
{
this.mxx = mxx;
this.myx = myx;
this.mxy = mxy;
this.myy = myy;
this.mxt = mxt;
this.myt = myt;
updateState2D();
}
public Affine2D(double mxx, double myx,
double mxy, double myy,
double mxt, double myt)
{
this.mxx = mxx;
this.myx = myx;
this.mxy = mxy;
this.myy = myy;
this.mxt = mxt;
this.myt = myt;
updateState2D();
}
@Override
public Degree getDegree() {
return Degree.AFFINE_2D;
}
@Override
protected void reset3Delements() { }
public void rotate(double theta, double anchorx, double anchory) {
translate(anchorx, anchory);
rotate(theta);
translate(-anchorx, -anchory);
}
public void rotate(double vecx, double vecy) {
if (vecy == 0.0) {
if (vecx < 0.0) {
rotate180();
}
} else if (vecx == 0.0) {
if (vecy > 0.0) {
rotate90();
} else {
rotate270();
}
} else {
double len = Math.sqrt(vecx * vecx + vecy * vecy);
double sin = vecy / len;
double cos = vecx / len;
double M0, M1;
M0 = mxx;
M1 = mxy;
mxx = cos * M0 + sin * M1;
mxy = -sin * M0 + cos * M1;
M0 = myx;
M1 = myy;
myx = cos * M0 + sin * M1;
myy = -sin * M0 + cos * M1;
updateState2D();
}
}
public void rotate(double vecx, double vecy,
double anchorx, double anchory)
{
translate(anchorx, anchory);
rotate(vecx, vecy);
translate(-anchorx, -anchory);
}
public void quadrantRotate(int numquadrants) {
switch (numquadrants & 3) {
case 0:
break;
case 1:
rotate90();
break;
case 2:
rotate180();
break;
case 3:
rotate270();
break;
}
}
public void quadrantRotate(int numquadrants,
double anchorx, double anchory)
{
switch (numquadrants & 3) {
case 0:
return;
case 1:
mxt += anchorx * (mxx - mxy) + anchory * (mxy + mxx);
myt += anchorx * (myx - myy) + anchory * (myy + myx);
rotate90();
break;
case 2:
mxt += anchorx * (mxx + mxx) + anchory * (mxy + mxy);
myt += anchorx * (myx + myx) + anchory * (myy + myy);
rotate180();
break;
case 3:
mxt += anchorx * (mxx + mxy) + anchory * (mxy - mxx);
myt += anchorx * (myx + myy) + anchory * (myy - myx);
rotate270();
break;
}
if (mxt == 0.0 && myt == 0.0) {
state &= ~APPLY_TRANSLATE;
if (type != TYPE_UNKNOWN) {
type &= ~TYPE_TRANSLATION;
}
} else {
state |= APPLY_TRANSLATE;
type |= TYPE_TRANSLATION;
}
}
public void setToTranslation(double tx, double ty) {
mxx = 1.0;
myx = 0.0;
mxy = 0.0;
myy = 1.0;
mxt = tx;
myt = ty;
if (tx != 0.0 || ty != 0.0) {
state = APPLY_TRANSLATE;
type = TYPE_TRANSLATION;
} else {
state = APPLY_IDENTITY;
type = TYPE_IDENTITY;
}
}
public void setToRotation(double theta) {
double sin = Math.sin(theta);
double cos;
if (sin == 1.0 || sin == -1.0) {
cos = 0.0;
state = APPLY_SHEAR;
type = TYPE_QUADRANT_ROTATION;
} else {
cos = Math.cos(theta);
if (cos == -1.0) {
sin = 0.0;
state = APPLY_SCALE;
type = TYPE_QUADRANT_ROTATION;
} else if (cos == 1.0) {
sin = 0.0;
state = APPLY_IDENTITY;
type = TYPE_IDENTITY;
} else {
state = APPLY_SHEAR | APPLY_SCALE;
type = TYPE_GENERAL_ROTATION;
}
}
mxx = cos;
myx = sin;
mxy = -sin;
myy = cos;
mxt = 0.0;
myt = 0.0;
}
public void setToRotation(double theta, double anchorx, double anchory) {
setToRotation(theta);
double sin = myx;
double oneMinusCos = 1.0 - mxx;
mxt = anchorx * oneMinusCos + anchory * sin;
myt = anchory * oneMinusCos - anchorx * sin;
if (mxt != 0.0 || myt != 0.0) {
state |= APPLY_TRANSLATE;
type |= TYPE_TRANSLATION;
}
}
public void setToRotation(double vecx, double vecy) {
double sin, cos;
if (vecy == 0) {
sin = 0.0;
if (vecx < 0.0) {
cos = -1.0;
state = APPLY_SCALE;
type = TYPE_QUADRANT_ROTATION;
} else {
cos = 1.0;
state = APPLY_IDENTITY;
type = TYPE_IDENTITY;
}
} else if (vecx == 0) {
cos = 0.0;
sin = (vecy > 0.0) ? 1.0 : -1.0;
state = APPLY_SHEAR;
type = TYPE_QUADRANT_ROTATION;
} else {
double len = Math.sqrt(vecx * vecx + vecy * vecy);
cos = vecx / len;
sin = vecy / len;
state = APPLY_SHEAR | APPLY_SCALE;
type = TYPE_GENERAL_ROTATION;
}
mxx = cos;
myx = sin;
mxy = -sin;
myy = cos;
mxt = 0.0;
myt = 0.0;
}
public void setToRotation(double vecx, double vecy,
double anchorx, double anchory)
{
setToRotation(vecx, vecy);
double sin = myx;
double oneMinusCos = 1.0 - mxx;
mxt = anchorx * oneMinusCos + anchory * sin;
myt = anchory * oneMinusCos - anchorx * sin;
if (mxt != 0.0 || myt != 0.0) {
state |= APPLY_TRANSLATE;
type |= TYPE_TRANSLATION;
}
}
public void setToQuadrantRotation(int numquadrants) {
switch (numquadrants & 3) {
case 0:
mxx = 1.0;
myx = 0.0;
mxy = 0.0;
myy = 1.0;
mxt = 0.0;
myt = 0.0;
state = APPLY_IDENTITY;
type = TYPE_IDENTITY;
break;
case 1:
mxx = 0.0;
myx = 1.0;
mxy = -1.0;
myy = 0.0;
mxt = 0.0;
myt = 0.0;
state = APPLY_SHEAR;
type = TYPE_QUADRANT_ROTATION;
break;
case 2:
mxx = -1.0;
myx = 0.0;
mxy = 0.0;
myy = -1.0;
mxt = 0.0;
myt = 0.0;
state = APPLY_SCALE;
type = TYPE_QUADRANT_ROTATION;
break;
case 3:
mxx = 0.0;
myx = -1.0;
mxy = 1.0;
myy = 0.0;
mxt = 0.0;
myt = 0.0;
state = APPLY_SHEAR;
type = TYPE_QUADRANT_ROTATION;
break;
}
}
public void setToQuadrantRotation(int numquadrants,
double anchorx, double anchory)
{
switch (numquadrants & 3) {
case 0:
mxx = 1.0;
myx = 0.0;
mxy = 0.0;
myy = 1.0;
mxt = 0.0;
myt = 0.0;
state = APPLY_IDENTITY;
type = TYPE_IDENTITY;
break;
case 1:
mxx = 0.0;
myx = 1.0;
mxy = -1.0;
myy = 0.0;
mxt = anchorx + anchory;
myt = anchory - anchorx;
if (mxt == 0.0 && myt == 0.0) {
state = APPLY_SHEAR;
type = TYPE_QUADRANT_ROTATION;
} else {
state = APPLY_SHEAR | APPLY_TRANSLATE;
type = TYPE_QUADRANT_ROTATION | TYPE_TRANSLATION;
}
break;
case 2:
mxx = -1.0;
myx = 0.0;
mxy = 0.0;
myy = -1.0;
mxt = anchorx + anchorx;
myt = anchory + anchory;
if (mxt == 0.0 && myt == 0.0) {
state = APPLY_SCALE;
type = TYPE_QUADRANT_ROTATION;
} else {
state = APPLY_SCALE | APPLY_TRANSLATE;
type = TYPE_QUADRANT_ROTATION | TYPE_TRANSLATION;
}
break;
case 3:
mxx = 0.0;
myx = -1.0;
mxy = 1.0;
myy = 0.0;
mxt = anchorx - anchory;
myt = anchory + anchorx;
if (mxt == 0.0 && myt == 0.0) {
state = APPLY_SHEAR;
type = TYPE_QUADRANT_ROTATION;
} else {
state = APPLY_SHEAR | APPLY_TRANSLATE;
type = TYPE_QUADRANT_ROTATION | TYPE_TRANSLATION;
}
break;
}
}
public void setToScale(double sx, double sy) {
mxx = sx;
myx = 0.0;
mxy = 0.0;
myy = sy;
mxt = 0.0;
myt = 0.0;
if (sx != 1.0 || sy != 1.0) {
state = APPLY_SCALE;
type = TYPE_UNKNOWN;
} else {
state = APPLY_IDENTITY;
type = TYPE_IDENTITY;
}
}
public void setTransform(BaseTransform Tx) {
switch (Tx.getDegree()) {
case IDENTITY:
setToIdentity();
break;
case TRANSLATE_2D:
setToTranslation(Tx.getMxt(), Tx.getMyt());
break;
default:
if (Tx.getType() > TYPE_AFFINE2D_MASK) {
System.out.println(Tx+" is "+Tx.getType());
System.out.print("  "+Tx.getMxx());
System.out.print(", "+Tx.getMxy());
System.out.print(", "+Tx.getMxz());
System.out.print(", "+Tx.getMxt());
System.out.println();
System.out.print("  "+Tx.getMyx());
System.out.print(", "+Tx.getMyy());
System.out.print(", "+Tx.getMyz());
System.out.print(", "+Tx.getMyt());
System.out.println();
System.out.print("  "+Tx.getMzx());
System.out.print(", "+Tx.getMzy());
System.out.print(", "+Tx.getMzz());
System.out.print(", "+Tx.getMzt());
System.out.println();
degreeError(Degree.AFFINE_2D);
}
case AFFINE_2D:
this.mxx = Tx.getMxx();
this.myx = Tx.getMyx();
this.mxy = Tx.getMxy();
this.myy = Tx.getMyy();
this.mxt = Tx.getMxt();
this.myt = Tx.getMyt();
if (Tx instanceof AffineBase) {
this.state = ((AffineBase) Tx).state;
this.type = ((AffineBase) Tx).type;
} else {
updateState2D();
}
break;
}
}
public void preConcatenate(BaseTransform Tx) {
switch (Tx.getDegree()) {
case IDENTITY:
return;
case TRANSLATE_2D:
translate(Tx.getMxt(), Tx.getMyt());
return;
case AFFINE_2D:
break;
default:
degreeError(Degree.AFFINE_2D);
}
double M0, M1;
double Txx, Txy, Tyx, Tyy;
double Txt, Tyt;
int mystate = state;
Affine2D at = (Affine2D) Tx;
int txstate = at.state;
switch ((txstate << HI_SHIFT) | mystate) {
case (HI_IDENTITY | APPLY_IDENTITY):
case (HI_IDENTITY | APPLY_TRANSLATE):
case (HI_IDENTITY | APPLY_SCALE):
case (HI_IDENTITY | APPLY_SCALE | APPLY_TRANSLATE):
case (HI_IDENTITY | APPLY_SHEAR):
case (HI_IDENTITY | APPLY_SHEAR | APPLY_TRANSLATE):
case (HI_IDENTITY | APPLY_SHEAR | APPLY_SCALE):
case (HI_IDENTITY | APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
return;
case (HI_TRANSLATE | APPLY_IDENTITY):
case (HI_TRANSLATE | APPLY_SCALE):
case (HI_TRANSLATE | APPLY_SHEAR):
case (HI_TRANSLATE | APPLY_SHEAR | APPLY_SCALE):
mxt = at.mxt;
myt = at.myt;
state = mystate | APPLY_TRANSLATE;
type |= TYPE_TRANSLATION;
return;
case (HI_TRANSLATE | APPLY_TRANSLATE):
case (HI_TRANSLATE | APPLY_SCALE | APPLY_TRANSLATE):
case (HI_TRANSLATE | APPLY_SHEAR | APPLY_TRANSLATE):
case (HI_TRANSLATE | APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
mxt = mxt + at.mxt;
myt = myt + at.myt;
return;
case (HI_SCALE | APPLY_TRANSLATE):
case (HI_SCALE | APPLY_IDENTITY):
state = mystate | APPLY_SCALE;
case (HI_SCALE | APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
case (HI_SCALE | APPLY_SHEAR | APPLY_SCALE):
case (HI_SCALE | APPLY_SHEAR | APPLY_TRANSLATE):
case (HI_SCALE | APPLY_SHEAR):
case (HI_SCALE | APPLY_SCALE | APPLY_TRANSLATE):
case (HI_SCALE | APPLY_SCALE):
Txx = at.mxx;
Tyy = at.myy;
if ((mystate & APPLY_SHEAR) != 0) {
mxy = mxy * Txx;
myx = myx * Tyy;
if ((mystate & APPLY_SCALE) != 0) {
mxx = mxx * Txx;
myy = myy * Tyy;
}
} else {
mxx = mxx * Txx;
myy = myy * Tyy;
}
if ((mystate & APPLY_TRANSLATE) != 0) {
mxt = mxt * Txx;
myt = myt * Tyy;
}
type = TYPE_UNKNOWN;
return;
case (HI_SHEAR | APPLY_SHEAR | APPLY_TRANSLATE):
case (HI_SHEAR | APPLY_SHEAR):
mystate = mystate | APPLY_SCALE;
case (HI_SHEAR | APPLY_TRANSLATE):
case (HI_SHEAR | APPLY_IDENTITY):
case (HI_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
case (HI_SHEAR | APPLY_SCALE):
state = mystate ^ APPLY_SHEAR;
case (HI_SHEAR | APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
case (HI_SHEAR | APPLY_SHEAR | APPLY_SCALE):
Txy = at.mxy;
Tyx = at.myx;
M0 = mxx;
mxx = myx * Txy;
myx = M0 * Tyx;
M0 = mxy;
mxy = myy * Txy;
myy = M0 * Tyx;
M0 = mxt;
mxt = myt * Txy;
myt = M0 * Tyx;
type = TYPE_UNKNOWN;
return;
}
Txx = at.mxx; Txy = at.mxy; Txt = at.mxt;
Tyx = at.myx; Tyy = at.myy; Tyt = at.myt;
switch (mystate) {
default:
stateError();
case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
M0 = mxt;
M1 = myt;
Txt += M0 * Txx + M1 * Txy;
Tyt += M0 * Tyx + M1 * Tyy;
case (APPLY_SHEAR | APPLY_SCALE):
mxt = Txt;
myt = Tyt;
M0 = mxx;
M1 = myx;
mxx = M0 * Txx + M1 * Txy;
myx = M0 * Tyx + M1 * Tyy;
M0 = mxy;
M1 = myy;
mxy = M0 * Txx + M1 * Txy;
myy = M0 * Tyx + M1 * Tyy;
break;
case (APPLY_SHEAR | APPLY_TRANSLATE):
M0 = mxt;
M1 = myt;
Txt += M0 * Txx + M1 * Txy;
Tyt += M0 * Tyx + M1 * Tyy;
case (APPLY_SHEAR):
mxt = Txt;
myt = Tyt;
M0 = myx;
mxx = M0 * Txy;
myx = M0 * Tyy;
M0 = mxy;
mxy = M0 * Txx;
myy = M0 * Tyx;
break;
case (APPLY_SCALE | APPLY_TRANSLATE):
M0 = mxt;
M1 = myt;
Txt += M0 * Txx + M1 * Txy;
Tyt += M0 * Tyx + M1 * Tyy;
case (APPLY_SCALE):
mxt = Txt;
myt = Tyt;
M0 = mxx;
mxx = M0 * Txx;
myx = M0 * Tyx;
M0 = myy;
mxy = M0 * Txy;
myy = M0 * Tyy;
break;
case (APPLY_TRANSLATE):
M0 = mxt;
M1 = myt;
Txt += M0 * Txx + M1 * Txy;
Tyt += M0 * Tyx + M1 * Tyy;
case (APPLY_IDENTITY):
mxt = Txt;
myt = Tyt;
mxx = Txx;
myx = Tyx;
mxy = Txy;
myy = Tyy;
state = mystate | txstate;
type = TYPE_UNKNOWN;
return;
}
updateState2D();
}
public Affine2D createInverse()
throws NoninvertibleTransformException
{
double det;
switch (state) {
default:
stateError();
case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
det = mxx * myy - mxy * myx;
if (det == 0 || Math.abs(det) <= Double.MIN_VALUE) {
throw new NoninvertibleTransformException("Determinant is "+
det);
}
return new Affine2D( myy / det, -myx / det,
-mxy / det, mxx / det,
(mxy * myt - myy * mxt) / det,
(myx * mxt - mxx * myt) / det,
(APPLY_SHEAR |
APPLY_SCALE |
APPLY_TRANSLATE));
case (APPLY_SHEAR | APPLY_SCALE):
det = mxx * myy - mxy * myx;
if (det == 0 || Math.abs(det) <= Double.MIN_VALUE) {
throw new NoninvertibleTransformException("Determinant is "+
det);
}
return new Affine2D( myy / det, -myx / det,
-mxy / det, mxx / det,
0.0, 0.0,
(APPLY_SHEAR | APPLY_SCALE));
case (APPLY_SHEAR | APPLY_TRANSLATE):
if (mxy == 0.0 || myx == 0.0) {
throw new NoninvertibleTransformException("Determinant is 0");
}
return new Affine2D( 0.0, 1.0 / mxy,
1.0 / myx, 0.0,
-myt / myx, -mxt / mxy,
(APPLY_SHEAR | APPLY_TRANSLATE));
case (APPLY_SHEAR):
if (mxy == 0.0 || myx == 0.0) {
throw new NoninvertibleTransformException("Determinant is 0");
}
return new Affine2D(0.0, 1.0 / mxy,
1.0 / myx, 0.0,
0.0, 0.0,
(APPLY_SHEAR));
case (APPLY_SCALE | APPLY_TRANSLATE):
if (mxx == 0.0 || myy == 0.0) {
throw new NoninvertibleTransformException("Determinant is 0");
}
return new Affine2D( 1.0 / mxx, 0.0,
0.0, 1.0 / myy,
-mxt / mxx, -myt / myy,
(APPLY_SCALE | APPLY_TRANSLATE));
case (APPLY_SCALE):
if (mxx == 0.0 || myy == 0.0) {
throw new NoninvertibleTransformException("Determinant is 0");
}
return new Affine2D(1.0 / mxx, 0.0,
0.0, 1.0 / myy,
0.0, 0.0,
(APPLY_SCALE));
case (APPLY_TRANSLATE):
return new Affine2D( 1.0, 0.0,
0.0, 1.0,
-mxt, -myt,
(APPLY_TRANSLATE));
case (APPLY_IDENTITY):
return new Affine2D();
}
}
public void transform(Point2D[] ptSrc, int srcOff,
Point2D[] ptDst, int dstOff,
int numPts) {
int mystate = this.state;
while (--numPts >= 0) {
Point2D src = ptSrc[srcOff++];
double x = src.x;
double y = src.y;
Point2D dst = ptDst[dstOff++];
if (dst == null) {
dst = new Point2D();
ptDst[dstOff - 1] = dst;
}
switch (mystate) {
default:
stateError();
case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
dst.setLocation((float)(x * mxx + y * mxy + mxt),
(float)(x * myx + y * myy + myt));
break;
case (APPLY_SHEAR | APPLY_SCALE):
dst.setLocation((float)(x * mxx + y * mxy),
(float)(x * myx + y * myy));
break;
case (APPLY_SHEAR | APPLY_TRANSLATE):
dst.setLocation((float)(y * mxy + mxt),
(float)(x * myx + myt));
break;
case (APPLY_SHEAR):
dst.setLocation((float)(y * mxy), (float)(x * myx));
break;
case (APPLY_SCALE | APPLY_TRANSLATE):
dst.setLocation((float)(x * mxx + mxt), (float)(y * myy + myt));
break;
case (APPLY_SCALE):
dst.setLocation((float)(x * mxx), (float)(y * myy));
break;
case (APPLY_TRANSLATE):
dst.setLocation((float)(x + mxt), (float)(y + myt));
break;
case (APPLY_IDENTITY):
dst.setLocation((float) x, (float) y);
break;
}
}
}
public Point2D deltaTransform(Point2D ptSrc, Point2D ptDst) {
if (ptDst == null) {
ptDst = new Point2D();
}
double x = ptSrc.x;
double y = ptSrc.y;
switch (state) {
default:
stateError();
case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
case (APPLY_SHEAR | APPLY_SCALE):
ptDst.setLocation((float)(x * mxx + y * mxy), (float)(x * myx + y * myy));
return ptDst;
case (APPLY_SHEAR | APPLY_TRANSLATE):
case (APPLY_SHEAR):
ptDst.setLocation((float)(y * mxy), (float)(x * myx));
return ptDst;
case (APPLY_SCALE | APPLY_TRANSLATE):
case (APPLY_SCALE):
ptDst.setLocation((float)(x * mxx), (float)(y * myy));
return ptDst;
case (APPLY_TRANSLATE):
case (APPLY_IDENTITY):
ptDst.setLocation((float) x, (float) y);
return ptDst;
}
}
private static double _matround(double matval) {
return Math.rint(matval * 1E15) / 1E15;
}
@Override
public String toString() {
return ("Affine2D[["
+ _matround(mxx) + ", "
+ _matround(mxy) + ", "
+ _matround(mxt) + "], ["
+ _matround(myx) + ", "
+ _matround(myy) + ", "
+ _matround(myt) + "]]");
}
@Override
public boolean is2D() {
return true;
}
@Override
public void restoreTransform(double mxx, double myx,
double mxy, double myy,
double mxt, double myt)
{
setTransform(mxx, myx, mxy, myy, mxt, myt);
}
@Override
public void restoreTransform(double mxx, double mxy, double mxz, double mxt,
double myx, double myy, double myz, double myt,
double mzx, double mzy, double mzz, double mzt)
{
if ( mxz != 0 ||
myz != 0 ||
mzx != 0 || mzy != 0 || mzz != 1 || mzt != 0.0)
{
degreeError(Degree.AFFINE_2D);
}
setTransform(mxx, myx, mxy, myy, mxt, myt);
}
@Override
public BaseTransform deriveWithTranslation(double mxt, double myt) {
translate(mxt, myt);
return this;
}
@Override
public BaseTransform deriveWithTranslation(double mxt, double myt, double mzt) {
if (mzt == 0.0) {
translate(mxt, myt);
return this;
}
Affine3D a = new Affine3D(this);
a.translate(mxt, myt, mzt);
return a;
}
@Override
public BaseTransform deriveWithScale(double mxx, double myy, double mzz) {
if (mzz == 1.0) {
scale(mxx, myy);
return this;
}
Affine3D a = new Affine3D(this);
a.scale(mxx, myy, mzz);
return a;
}
@Override
public BaseTransform deriveWithRotation(double theta,
double axisX, double axisY, double axisZ) {
if (theta == 0.0) {
return this;
}
if (almostZero(axisX) && almostZero(axisY)) {
if (axisZ > 0) {
rotate(theta);
} else if (axisZ < 0) {
rotate(-theta);
}
return this;
}
Affine3D a = new Affine3D(this);
a.rotate(theta, axisX, axisY, axisZ);
return a;
}
@Override
public BaseTransform deriveWithPreTranslation(double mxt, double myt) {
this.mxt += mxt;
this.myt += myt;
if (this.mxt != 0.0 || this.myt != 0.0) {
state |= APPLY_TRANSLATE;
type |= TYPE_TRANSLATION;
} else {
state &= ~APPLY_TRANSLATE;
if (type != TYPE_UNKNOWN) {
type &= ~TYPE_TRANSLATION;
}
}
return this;
}
@Override
public BaseTransform deriveWithConcatenation(double mxx, double myx,
double mxy, double myy,
double mxt, double myt)
{
BaseTransform tmpTx = getInstance(mxx, myx,
mxy, myy,
mxt, myt);
concatenate(tmpTx);
return this;
}
@Override
public BaseTransform deriveWithConcatenation(
double mxx, double mxy, double mxz, double mxt,
double myx, double myy, double myz, double myt,
double mzx, double mzy, double mzz, double mzt) {
if ( mxz == 0.0
&& myz == 0.0
&& mzx == 0.0 && mzy == 0.0 && mzz == 1.0 && mzt == 0.0) {
concatenate(mxx, mxy,
mxt, myx,
myy, myt);
return this;
}
Affine3D t3d = new Affine3D(this);
t3d.concatenate(mxx, mxy, mxz, mxt,
myx, myy, myz, myt,
mzx, mzy, mzz, mzt);
return t3d;
}
@Override
public BaseTransform deriveWithConcatenation(BaseTransform tx) {
if (tx.is2D()) {
concatenate(tx);
return this;
}
Affine3D t3d = new Affine3D(this);
t3d.concatenate(tx);
return t3d;
}
@Override
public BaseTransform deriveWithPreConcatenation(BaseTransform tx) {
if (tx.is2D()) {
preConcatenate(tx);
return this;
}
Affine3D t3d = new Affine3D(this);
t3d.preConcatenate(tx);
return t3d;
}
@Override
public BaseTransform deriveWithNewTransform(BaseTransform tx) {
if (tx.is2D()) {
setTransform(tx);
return this;
}
return getInstance(tx);
}
@Override
public BaseTransform copy() {
return new Affine2D(this);
}
private static final long BASE_HASH;
static {
long bits = 0;
bits = bits * 31 + Double.doubleToLongBits(IDENTITY_TRANSFORM.getMzz());
bits = bits * 31 + Double.doubleToLongBits(IDENTITY_TRANSFORM.getMzy());
bits = bits * 31 + Double.doubleToLongBits(IDENTITY_TRANSFORM.getMzx());
bits = bits * 31 + Double.doubleToLongBits(IDENTITY_TRANSFORM.getMyz());
bits = bits * 31 + Double.doubleToLongBits(IDENTITY_TRANSFORM.getMxz());
BASE_HASH = bits;
}
@Override
public int hashCode() {
if (isIdentity()) return 0;
long bits = BASE_HASH;
bits = bits * 31 + Double.doubleToLongBits(getMyy());
bits = bits * 31 + Double.doubleToLongBits(getMyx());
bits = bits * 31 + Double.doubleToLongBits(getMxy());
bits = bits * 31 + Double.doubleToLongBits(getMxx());
bits = bits * 31 + Double.doubleToLongBits(0.0);
bits = bits * 31 + Double.doubleToLongBits(getMyt());
bits = bits * 31 + Double.doubleToLongBits(getMxt());
return (((int) bits) ^ ((int) (bits >> 32)));
}
@Override
public boolean equals(Object obj) {
if (obj instanceof BaseTransform) {
BaseTransform a = (BaseTransform) obj;
return (a.getType() <= TYPE_AFFINE2D_MASK &&
a.getMxx() == this.mxx &&
a.getMxy() == this.mxy &&
a.getMxt() == this.mxt &&
a.getMyx() == this.myx &&
a.getMyy() == this.myy &&
a.getMyt() == this.myt);
}
return false;
}
}
