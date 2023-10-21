package com.sun.javafx.geom.transform;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.geom.Point2D;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.Shape;
import com.sun.javafx.geom.Vec3d;
public abstract class AffineBase extends BaseTransform {
protected static final int APPLY_IDENTITY = 0;
protected static final int APPLY_TRANSLATE = 1;
protected static final int APPLY_SCALE = 2;
protected static final int APPLY_SHEAR = 4;
protected static final int APPLY_3D = 8;
protected static final int APPLY_2D_MASK = (APPLY_TRANSLATE | APPLY_SCALE | APPLY_SHEAR);
protected static final int APPLY_2D_DELTA_MASK = (APPLY_SCALE | APPLY_SHEAR);
protected static final int HI_SHIFT = 4;
protected static final int HI_IDENTITY = APPLY_IDENTITY << HI_SHIFT;
protected static final int HI_TRANSLATE = APPLY_TRANSLATE << HI_SHIFT;
protected static final int HI_SCALE = APPLY_SCALE << HI_SHIFT;
protected static final int HI_SHEAR = APPLY_SHEAR << HI_SHIFT;
protected static final int HI_3D = APPLY_3D << HI_SHIFT;
protected double mxx;
protected double myx;
protected double mxy;
protected double myy;
protected double mxt;
protected double myt;
protected transient int state;
protected transient int type;
protected static void stateError() {
throw new InternalError("missing case in transform state switch");
}
protected void updateState() {
updateState2D();
}
protected void updateState2D() {
if (mxy == 0.0 && myx == 0.0) {
if (mxx == 1.0 && myy == 1.0) {
if (mxt == 0.0 && myt == 0.0) {
state = APPLY_IDENTITY;
type = TYPE_IDENTITY;
} else {
state = APPLY_TRANSLATE;
type = TYPE_TRANSLATION;
}
} else {
if (mxt == 0.0 && myt == 0.0) {
state = APPLY_SCALE;
} else {
state = (APPLY_SCALE | APPLY_TRANSLATE);
}
type = TYPE_UNKNOWN;
}
} else {
if (mxx == 0.0 && myy == 0.0) {
if (mxt == 0.0 && myt == 0.0) {
state = APPLY_SHEAR;
} else {
state = (APPLY_SHEAR | APPLY_TRANSLATE);
}
} else {
if (mxt == 0.0 && myt == 0.0) {
state = (APPLY_SHEAR | APPLY_SCALE);
} else {
state = (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE);
}
}
type = TYPE_UNKNOWN;
}
}
public int getType() {
if (type == TYPE_UNKNOWN) {
updateState();
if (type == TYPE_UNKNOWN) {
type = calculateType();
}
}
return type;
}
protected int calculateType() {
int ret = ((state & APPLY_3D) == 0) ? TYPE_IDENTITY : TYPE_AFFINE_3D;
boolean sgn0, sgn1;
switch (state & APPLY_2D_MASK) {
default:
stateError();
case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
ret |= TYPE_TRANSLATION;
case (APPLY_SHEAR | APPLY_SCALE):
if (mxx * mxy + myx * myy != 0) {
ret |= TYPE_GENERAL_TRANSFORM;
break;
}
sgn0 = (mxx >= 0.0);
sgn1 = (myy >= 0.0);
if (sgn0 == sgn1) {
if (mxx != myy || mxy != -myx) {
ret |= (TYPE_GENERAL_ROTATION | TYPE_GENERAL_SCALE);
} else if (mxx * myy - mxy * myx != 1.0) {
ret |= (TYPE_GENERAL_ROTATION | TYPE_UNIFORM_SCALE);
} else {
ret |= TYPE_GENERAL_ROTATION;
}
} else {
if (mxx != -myy || mxy != myx) {
ret |= (TYPE_GENERAL_ROTATION |
TYPE_FLIP |
TYPE_GENERAL_SCALE);
} else if (mxx * myy - mxy * myx != 1.0) {
ret |= (TYPE_GENERAL_ROTATION |
TYPE_FLIP |
TYPE_UNIFORM_SCALE);
} else {
ret |= (TYPE_GENERAL_ROTATION | TYPE_FLIP);
}
}
break;
case (APPLY_SHEAR | APPLY_TRANSLATE):
ret |= TYPE_TRANSLATION;
case (APPLY_SHEAR):
sgn0 = (mxy >= 0.0);
sgn1 = (myx >= 0.0);
if (sgn0 != sgn1) {
if (mxy != -myx) {
ret |= (TYPE_QUADRANT_ROTATION | TYPE_GENERAL_SCALE);
} else if (mxy != 1.0 && mxy != -1.0) {
ret |= (TYPE_QUADRANT_ROTATION | TYPE_UNIFORM_SCALE);
} else {
ret |= TYPE_QUADRANT_ROTATION;
}
} else {
if (mxy == myx) {
ret |= (TYPE_QUADRANT_ROTATION |
TYPE_FLIP |
TYPE_UNIFORM_SCALE);
} else {
ret |= (TYPE_QUADRANT_ROTATION |
TYPE_FLIP |
TYPE_GENERAL_SCALE);
}
}
break;
case (APPLY_SCALE | APPLY_TRANSLATE):
ret |= TYPE_TRANSLATION;
case (APPLY_SCALE):
sgn0 = (mxx >= 0.0);
sgn1 = (myy >= 0.0);
if (sgn0 == sgn1) {
if (sgn0) {
if (mxx == myy) {
ret |= TYPE_UNIFORM_SCALE;
} else {
ret |= TYPE_GENERAL_SCALE;
}
} else {
if (mxx != myy) {
ret |= (TYPE_QUADRANT_ROTATION | TYPE_GENERAL_SCALE);
} else if (mxx != -1.0) {
ret |= (TYPE_QUADRANT_ROTATION | TYPE_UNIFORM_SCALE);
} else {
ret |= TYPE_QUADRANT_ROTATION;
}
}
} else {
if (mxx == -myy) {
if (mxx == 1.0 || mxx == -1.0) {
ret |= TYPE_FLIP;
} else {
ret |= (TYPE_FLIP | TYPE_UNIFORM_SCALE);
}
} else {
ret |= (TYPE_FLIP | TYPE_GENERAL_SCALE);
}
}
break;
case (APPLY_TRANSLATE):
ret |= TYPE_TRANSLATION;
break;
case (APPLY_IDENTITY):
break;
}
return ret;
}
@Override
public double getMxx() {
return mxx;
}
@Override
public double getMyy() {
return myy;
}
@Override
public double getMxy() {
return mxy;
}
@Override
public double getMyx() {
return myx;
}
@Override
public double getMxt() {
return mxt;
}
@Override
public double getMyt() {
return myt;
}
public boolean isIdentity() {
return (state == APPLY_IDENTITY || (getType() == TYPE_IDENTITY));
}
@Override
public boolean isTranslateOrIdentity() {
return (state <= APPLY_TRANSLATE || (getType() <= TYPE_TRANSLATION));
}
@Override
public boolean is2D() {
return (state < APPLY_3D || getType() <= TYPE_AFFINE2D_MASK);
}
public double getDeterminant() {
switch (state) {
default:
stateError();
case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
case (APPLY_SHEAR | APPLY_SCALE):
return mxx * myy - mxy * myx;
case (APPLY_SHEAR | APPLY_TRANSLATE):
case (APPLY_SHEAR):
return -(mxy * myx);
case (APPLY_SCALE | APPLY_TRANSLATE):
case (APPLY_SCALE):
return mxx * myy;
case (APPLY_TRANSLATE):
case (APPLY_IDENTITY):
return 1.0;
}
}
protected abstract void reset3Delements();
public void setToIdentity() {
mxx = myy = 1.0;
myx = mxy = mxt = myt = 0.0;
reset3Delements();
state = APPLY_IDENTITY;
type = TYPE_IDENTITY;
}
public void setTransform(double mxx, double myx,
double mxy, double myy,
double mxt, double myt) {
this.mxx = mxx;
this.myx = myx;
this.mxy = mxy;
this.myy = myy;
this.mxt = mxt;
this.myt = myt;
reset3Delements();
updateState2D();
}
public void setToShear(double shx, double shy) {
mxx = 1.0;
mxy = shx;
myx = shy;
myy = 1.0;
mxt = 0.0;
myt = 0.0;
reset3Delements();
if (shx != 0.0 || shy != 0.0) {
state = (APPLY_SHEAR | APPLY_SCALE);
type = TYPE_UNKNOWN;
} else {
state = APPLY_IDENTITY;
type = TYPE_IDENTITY;
}
}
public Point2D transform(Point2D pt) {
return transform(pt, pt);
}
public Point2D transform(Point2D ptSrc, Point2D ptDst) {
if (ptDst == null) {
ptDst = new Point2D();
}
double x = ptSrc.x;
double y = ptSrc.y;
switch (state & APPLY_2D_MASK) {
default:
stateError();
case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
ptDst.setLocation((float)(x * mxx + y * mxy + mxt),
(float)(x * myx + y * myy + myt));
return ptDst;
case (APPLY_SHEAR | APPLY_SCALE):
ptDst.setLocation((float)(x * mxx + y * mxy),
(float)(x * myx + y * myy));
return ptDst;
case (APPLY_SHEAR | APPLY_TRANSLATE):
ptDst.setLocation((float)(y * mxy + mxt),
(float)(x * myx + myt));
return ptDst;
case (APPLY_SHEAR):
ptDst.setLocation((float)(y * mxy), (float)(x * myx));
return ptDst;
case (APPLY_SCALE | APPLY_TRANSLATE):
ptDst.setLocation((float)(x * mxx + mxt), (float)(y * myy + myt));
return ptDst;
case (APPLY_SCALE):
ptDst.setLocation((float)(x * mxx), (float)(y * myy));
return ptDst;
case (APPLY_TRANSLATE):
ptDst.setLocation((float)(x + mxt), (float)(y + myt));
return ptDst;
case (APPLY_IDENTITY):
ptDst.setLocation((float) x, (float) y);
return ptDst;
}
}
public Vec3d transform(Vec3d src, Vec3d dst) {
if (dst == null) {
dst = new Vec3d();
}
double x = src.x;
double y = src.y;
double z = src.z;
switch (state) {
default:
stateError();
case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
dst.x = x * mxx + y * mxy + mxt;
dst.y = x * myx + y * myy + myt;
dst.z = z;
return dst;
case (APPLY_SHEAR | APPLY_SCALE):
dst.x = x * mxx + y * mxy;
dst.y = x * myx + y * myy;
dst.z = z;
return dst;
case (APPLY_SHEAR | APPLY_TRANSLATE):
dst.x = y * mxy + mxt;
dst.y = x * myx + myt;
dst.z = z;
return dst;
case (APPLY_SHEAR):
dst.x = y * mxy;
dst.y = x * myx;
dst.z = z;
return dst;
case (APPLY_SCALE | APPLY_TRANSLATE):
dst.x = x * mxx + mxt;
dst.y = y * myy + myt;
dst.z = z;
return dst;
case (APPLY_SCALE):
dst.x = x * mxx;
dst.y = y * myy;
dst.z = z;
return dst;
case (APPLY_TRANSLATE):
dst.x = x + mxt;
dst.y = y + myt;
dst.z = z;
return dst;
case (APPLY_IDENTITY):
dst.x = x;
dst.y = y;
dst.z = z;
return dst;
}
}
public Vec3d deltaTransform(Vec3d src, Vec3d dst) {
if (dst == null) {
dst = new Vec3d();
}
double x = src.x;
double y = src.y;
double z = src.z;
switch (state) {
default:
stateError();
case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
case (APPLY_SHEAR | APPLY_SCALE):
dst.x = x * mxx + y * mxy ;
dst.y = x * myx + y * myy;
dst.z = z;
return dst;
case (APPLY_SHEAR | APPLY_TRANSLATE):
case (APPLY_SHEAR):
dst.x = y * mxy;
dst.y = x * myx;
dst.z = z;
return dst;
case (APPLY_SCALE | APPLY_TRANSLATE):
case (APPLY_SCALE):
dst.x = x * mxx;
dst.y = y * myy;
dst.z = z;
return dst;
case (APPLY_TRANSLATE):
case (APPLY_IDENTITY):
dst.x = x;
dst.y = y;
dst.z = z;
return dst;
}
}
private BaseBounds transform2DBounds(RectBounds src, RectBounds dst) {
switch (state & APPLY_2D_MASK) {
default:
stateError();
case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
case (APPLY_SHEAR | APPLY_SCALE):
double x1 = src.getMinX();
double y1 = src.getMinY();
double x2 = src.getMaxX();
double y2 = src.getMaxY();
dst.setBoundsAndSort((float) (x1 * mxx + y1 * mxy),
(float) (x1 * myx + y1 * myy),
(float) (x2 * mxx + y2 * mxy),
(float) (x2 * myx + y2 * myy));
dst.add((float) (x1 * mxx + y2 * mxy),
(float) (x1 * myx + y2 * myy));
dst.add((float) (x2 * mxx + y1 * mxy),
(float) (x2 * myx + y1 * myy));
dst.setBounds((float) (dst.getMinX() + mxt),
(float) (dst.getMinY() + myt),
(float) (dst.getMaxX() + mxt),
(float) (dst.getMaxY() + myt));
break;
case (APPLY_SHEAR | APPLY_TRANSLATE):
dst.setBoundsAndSort((float) (src.getMinY() * mxy + mxt),
(float) (src.getMinX() * myx + myt),
(float) (src.getMaxY() * mxy + mxt),
(float) (src.getMaxX() * myx + myt));
break;
case (APPLY_SHEAR):
dst.setBoundsAndSort((float) (src.getMinY() * mxy),
(float) (src.getMinX() * myx),
(float) (src.getMaxY() * mxy),
(float) (src.getMaxX() * myx));
break;
case (APPLY_SCALE | APPLY_TRANSLATE):
dst.setBoundsAndSort((float) (src.getMinX() * mxx + mxt),
(float) (src.getMinY() * myy + myt),
(float) (src.getMaxX() * mxx + mxt),
(float) (src.getMaxY() * myy + myt));
break;
case (APPLY_SCALE):
dst.setBoundsAndSort((float) (src.getMinX() * mxx),
(float) (src.getMinY() * myy),
(float) (src.getMaxX() * mxx),
(float) (src.getMaxY() * myy));
break;
case (APPLY_TRANSLATE):
dst.setBounds((float) (src.getMinX() + mxt),
(float) (src.getMinY() + myt),
(float) (src.getMaxX() + mxt),
(float) (src.getMaxY() + myt));
break;
case (APPLY_IDENTITY):
if (src != dst) {
dst.setBounds(src);
}
break;
}
return dst;
}
private BaseBounds transform3DBounds(BaseBounds src, BaseBounds dst) {
switch (state & APPLY_2D_MASK) {
default:
stateError();
case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
case (APPLY_SHEAR | APPLY_SCALE):
double x1 = src.getMinX();
double y1 = src.getMinY();
double z1 = src.getMinZ();
double x2 = src.getMaxX();
double y2 = src.getMaxY();
double z2 = src.getMaxZ();
dst.setBoundsAndSort((float) (x1 * mxx + y1 * mxy),
(float) (x1 * myx + y1 * myy),
(float) z1,
(float) (x2 * mxx + y2 * mxy),
(float) (x2 * myx + y2 * myy),
(float) z2);
dst.add((float) (x1 * mxx + y2 * mxy),
(float) (x1 * myx + y2 * myy), 0);
dst.add((float) (x2 * mxx + y1 * mxy),
(float) (x2 * myx + y1 * myy), 0);
dst.deriveWithNewBounds((float) (dst.getMinX() + mxt),
(float) (dst.getMinY() + myt),
(float) dst.getMinZ(),
(float) (dst.getMaxX() + mxt),
(float) (dst.getMaxY() + myt),
(float) dst.getMaxZ());
break;
case (APPLY_SHEAR | APPLY_TRANSLATE):
dst = dst.deriveWithNewBoundsAndSort((float) (src.getMinY() * mxy + mxt),
(float) (src.getMinX() * myx + myt),
(float) src.getMinZ(),
(float) (src.getMaxY() * mxy + mxt),
(float) (src.getMaxX() * myx + myt),
(float) src.getMaxZ());
break;
case (APPLY_SHEAR):
dst = dst.deriveWithNewBoundsAndSort((float) (src.getMinY() * mxy),
(float) (src.getMinX() * myx),
(float) src.getMinZ(),
(float) (src.getMaxY() * mxy),
(float) (src.getMaxX() * myx),
(float) src.getMaxZ());
break;
case (APPLY_SCALE | APPLY_TRANSLATE):
dst = dst.deriveWithNewBoundsAndSort((float) (src.getMinX() * mxx + mxt),
(float) (src.getMinY() * myy + myt),
(float) src.getMinZ(),
(float) (src.getMaxX() * mxx + mxt),
(float) (src.getMaxY() * myy + myt),
(float) src.getMaxZ());
break;
case (APPLY_SCALE):
dst = dst.deriveWithNewBoundsAndSort((float) (src.getMinX() * mxx),
(float) (src.getMinY() * myy),
(float) src.getMinZ(),
(float) (src.getMaxX() * mxx),
(float) (src.getMaxY() * myy),
(float) src.getMaxZ());
break;
case (APPLY_TRANSLATE):
dst = dst.deriveWithNewBounds((float) (src.getMinX() + mxt),
(float) (src.getMinY() + myt),
(float) src.getMinZ(),
(float) (src.getMaxX() + mxt),
(float) (src.getMaxY() + myt),
(float) src.getMaxZ());
break;
case (APPLY_IDENTITY):
if (src != dst) {
dst = dst.deriveWithNewBounds(src);
}
break;
}
return dst;
}
public BaseBounds transform(BaseBounds src, BaseBounds dst) {
if (src.getBoundsType() != BaseBounds.BoundsType.RECTANGLE ||
dst.getBoundsType() != BaseBounds.BoundsType.RECTANGLE) {
return transform3DBounds(src, dst);
}
return transform2DBounds((RectBounds)src, (RectBounds)dst);
}
public void transform(Rectangle src, Rectangle dst) {
switch (state & APPLY_2D_MASK) {
default:
stateError();
case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
case (APPLY_SHEAR | APPLY_SCALE):
case (APPLY_SHEAR | APPLY_TRANSLATE):
case (APPLY_SHEAR):
case (APPLY_SCALE | APPLY_TRANSLATE):
case (APPLY_SCALE):
RectBounds b = new RectBounds(src);
b = (RectBounds) transform(b, b);
dst.setBounds(b);
return;
case (APPLY_TRANSLATE):
Translate2D.transform(src, dst, mxt, myt);
return;
case (APPLY_IDENTITY):
if (dst != src) {
dst.setBounds(src);
}
return;
}
}
public void transform(float[] srcPts, int srcOff,
float[] dstPts, int dstOff,
int numPts)
{
doTransform(srcPts, srcOff, dstPts, dstOff, numPts,
(this.state & APPLY_2D_MASK));
}
public void deltaTransform(float[] srcPts, int srcOff,
float[] dstPts, int dstOff,
int numPts)
{
doTransform(srcPts, srcOff, dstPts, dstOff, numPts,
(this.state & APPLY_2D_DELTA_MASK));
}
private void doTransform(float[] srcPts, int srcOff,
float[] dstPts, int dstOff,
int numPts, int thestate)
{
double Mxx, Mxy, Mxt, Myx, Myy, Myt;
if (dstPts == srcPts &&
dstOff > srcOff && dstOff < srcOff + numPts * 2)
{
System.arraycopy(srcPts, srcOff, dstPts, dstOff, numPts * 2);
srcOff = dstOff;
}
switch (thestate) {
default:
stateError();
case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
Mxx = mxx; Mxy = mxy; Mxt = mxt;
Myx = myx; Myy = myy; Myt = myt;
while (--numPts >= 0) {
double x = srcPts[srcOff++];
double y = srcPts[srcOff++];
dstPts[dstOff++] = (float) (Mxx * x + Mxy * y + Mxt);
dstPts[dstOff++] = (float) (Myx * x + Myy * y + Myt);
}
return;
case (APPLY_SHEAR | APPLY_SCALE):
Mxx = mxx; Mxy = mxy;
Myx = myx; Myy = myy;
while (--numPts >= 0) {
double x = srcPts[srcOff++];
double y = srcPts[srcOff++];
dstPts[dstOff++] = (float) (Mxx * x + Mxy * y);
dstPts[dstOff++] = (float) (Myx * x + Myy * y);
}
return;
case (APPLY_SHEAR | APPLY_TRANSLATE):
Mxy = mxy; Mxt = mxt;
Myx = myx; Myt = myt;
while (--numPts >= 0) {
double x = srcPts[srcOff++];
dstPts[dstOff++] = (float) (Mxy * srcPts[srcOff++] + Mxt);
dstPts[dstOff++] = (float) (Myx * x + Myt);
}
return;
case (APPLY_SHEAR):
Mxy = mxy; Myx = myx;
while (--numPts >= 0) {
double x = srcPts[srcOff++];
dstPts[dstOff++] = (float) (Mxy * srcPts[srcOff++]);
dstPts[dstOff++] = (float) (Myx * x);
}
return;
case (APPLY_SCALE | APPLY_TRANSLATE):
Mxx = mxx; Mxt = mxt;
Myy = myy; Myt = myt;
while (--numPts >= 0) {
dstPts[dstOff++] = (float) (Mxx * srcPts[srcOff++] + Mxt);
dstPts[dstOff++] = (float) (Myy * srcPts[srcOff++] + Myt);
}
return;
case (APPLY_SCALE):
Mxx = mxx; Myy = myy;
while (--numPts >= 0) {
dstPts[dstOff++] = (float) (Mxx * srcPts[srcOff++]);
dstPts[dstOff++] = (float) (Myy * srcPts[srcOff++]);
}
return;
case (APPLY_TRANSLATE):
Mxt = mxt; Myt = myt;
while (--numPts >= 0) {
dstPts[dstOff++] = (float) (srcPts[srcOff++] + Mxt);
dstPts[dstOff++] = (float) (srcPts[srcOff++] + Myt);
}
return;
case (APPLY_IDENTITY):
if (srcPts != dstPts || srcOff != dstOff) {
System.arraycopy(srcPts, srcOff, dstPts, dstOff,
numPts * 2);
}
return;
}
}
public void transform(double[] srcPts, int srcOff,
double[] dstPts, int dstOff,
int numPts)
{
doTransform(srcPts, srcOff, dstPts, dstOff, numPts,
(this.state & APPLY_2D_MASK));
}
public void deltaTransform(double[] srcPts, int srcOff,
double[] dstPts, int dstOff,
int numPts)
{
doTransform(srcPts, srcOff, dstPts, dstOff, numPts,
(this.state & APPLY_2D_DELTA_MASK));
}
private void doTransform(double[] srcPts, int srcOff,
double[] dstPts, int dstOff,
int numPts, int thestate)
{
double Mxx, Mxy, Mxt, Myx, Myy, Myt;
if (dstPts == srcPts &&
dstOff > srcOff && dstOff < srcOff + numPts * 2)
{
System.arraycopy(srcPts, srcOff, dstPts, dstOff, numPts * 2);
srcOff = dstOff;
}
switch (thestate) {
default:
stateError();
case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
Mxx = mxx; Mxy = mxy; Mxt = mxt;
Myx = myx; Myy = myy; Myt = myt;
while (--numPts >= 0) {
double x = srcPts[srcOff++];
double y = srcPts[srcOff++];
dstPts[dstOff++] = Mxx * x + Mxy * y + Mxt;
dstPts[dstOff++] = Myx * x + Myy * y + Myt;
}
return;
case (APPLY_SHEAR | APPLY_SCALE):
Mxx = mxx; Mxy = mxy;
Myx = myx; Myy = myy;
while (--numPts >= 0) {
double x = srcPts[srcOff++];
double y = srcPts[srcOff++];
dstPts[dstOff++] = Mxx * x + Mxy * y;
dstPts[dstOff++] = Myx * x + Myy * y;
}
return;
case (APPLY_SHEAR | APPLY_TRANSLATE):
Mxy = mxy; Mxt = mxt;
Myx = myx; Myt = myt;
while (--numPts >= 0) {
double x = srcPts[srcOff++];
dstPts[dstOff++] = Mxy * srcPts[srcOff++] + Mxt;
dstPts[dstOff++] = Myx * x + Myt;
}
return;
case (APPLY_SHEAR):
Mxy = mxy; Myx = myx;
while (--numPts >= 0) {
double x = srcPts[srcOff++];
dstPts[dstOff++] = Mxy * srcPts[srcOff++];
dstPts[dstOff++] = Myx * x;
}
return;
case (APPLY_SCALE | APPLY_TRANSLATE):
Mxx = mxx; Mxt = mxt;
Myy = myy; Myt = myt;
while (--numPts >= 0) {
dstPts[dstOff++] = Mxx * srcPts[srcOff++] + Mxt;
dstPts[dstOff++] = Myy * srcPts[srcOff++] + Myt;
}
return;
case (APPLY_SCALE):
Mxx = mxx; Myy = myy;
while (--numPts >= 0) {
dstPts[dstOff++] = Mxx * srcPts[srcOff++];
dstPts[dstOff++] = Myy * srcPts[srcOff++];
}
return;
case (APPLY_TRANSLATE):
Mxt = mxt; Myt = myt;
while (--numPts >= 0) {
dstPts[dstOff++] = srcPts[srcOff++] + Mxt;
dstPts[dstOff++] = srcPts[srcOff++] + Myt;
}
return;
case (APPLY_IDENTITY):
if (srcPts != dstPts || srcOff != dstOff) {
System.arraycopy(srcPts, srcOff, dstPts, dstOff,
numPts * 2);
}
return;
}
}
public void transform(float[] srcPts, int srcOff,
double[] dstPts, int dstOff,
int numPts) {
double Mxx, Mxy, Mxt, Myx, Myy, Myt;
switch (state & APPLY_2D_MASK) {
default:
stateError();
case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
Mxx = mxx; Mxy = mxy; Mxt = mxt;
Myx = myx; Myy = myy; Myt = myt;
while (--numPts >= 0) {
double x = srcPts[srcOff++];
double y = srcPts[srcOff++];
dstPts[dstOff++] = Mxx * x + Mxy * y + Mxt;
dstPts[dstOff++] = Myx * x + Myy * y + Myt;
}
return;
case (APPLY_SHEAR | APPLY_SCALE):
Mxx = mxx; Mxy = mxy;
Myx = myx; Myy = myy;
while (--numPts >= 0) {
double x = srcPts[srcOff++];
double y = srcPts[srcOff++];
dstPts[dstOff++] = Mxx * x + Mxy * y;
dstPts[dstOff++] = Myx * x + Myy * y;
}
return;
case (APPLY_SHEAR | APPLY_TRANSLATE):
Mxy = mxy; Mxt = mxt;
Myx = myx; Myt = myt;
while (--numPts >= 0) {
double x = srcPts[srcOff++];
dstPts[dstOff++] = Mxy * srcPts[srcOff++] + Mxt;
dstPts[dstOff++] = Myx * x + Myt;
}
return;
case (APPLY_SHEAR):
Mxy = mxy; Myx = myx;
while (--numPts >= 0) {
double x = srcPts[srcOff++];
dstPts[dstOff++] = Mxy * srcPts[srcOff++];
dstPts[dstOff++] = Myx * x;
}
return;
case (APPLY_SCALE | APPLY_TRANSLATE):
Mxx = mxx; Mxt = mxt;
Myy = myy; Myt = myt;
while (--numPts >= 0) {
dstPts[dstOff++] = Mxx * srcPts[srcOff++] + Mxt;
dstPts[dstOff++] = Myy * srcPts[srcOff++] + Myt;
}
return;
case (APPLY_SCALE):
Mxx = mxx; Myy = myy;
while (--numPts >= 0) {
dstPts[dstOff++] = Mxx * srcPts[srcOff++];
dstPts[dstOff++] = Myy * srcPts[srcOff++];
}
return;
case (APPLY_TRANSLATE):
Mxt = mxt; Myt = myt;
while (--numPts >= 0) {
dstPts[dstOff++] = srcPts[srcOff++] + Mxt;
dstPts[dstOff++] = srcPts[srcOff++] + Myt;
}
return;
case (APPLY_IDENTITY):
while (--numPts >= 0) {
dstPts[dstOff++] = srcPts[srcOff++];
dstPts[dstOff++] = srcPts[srcOff++];
}
return;
}
}
public void transform(double[] srcPts, int srcOff,
float[] dstPts, int dstOff,
int numPts) {
double Mxx, Mxy, Mxt, Myx, Myy, Myt;
switch (state & APPLY_2D_MASK) {
default:
stateError();
case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
Mxx = mxx; Mxy = mxy; Mxt = mxt;
Myx = myx; Myy = myy; Myt = myt;
while (--numPts >= 0) {
double x = srcPts[srcOff++];
double y = srcPts[srcOff++];
dstPts[dstOff++] = (float) (Mxx * x + Mxy * y + Mxt);
dstPts[dstOff++] = (float) (Myx * x + Myy * y + Myt);
}
return;
case (APPLY_SHEAR | APPLY_SCALE):
Mxx = mxx; Mxy = mxy;
Myx = myx; Myy = myy;
while (--numPts >= 0) {
double x = srcPts[srcOff++];
double y = srcPts[srcOff++];
dstPts[dstOff++] = (float) (Mxx * x + Mxy * y);
dstPts[dstOff++] = (float) (Myx * x + Myy * y);
}
return;
case (APPLY_SHEAR | APPLY_TRANSLATE):
Mxy = mxy; Mxt = mxt;
Myx = myx; Myt = myt;
while (--numPts >= 0) {
double x = srcPts[srcOff++];
dstPts[dstOff++] = (float) (Mxy * srcPts[srcOff++] + Mxt);
dstPts[dstOff++] = (float) (Myx * x + Myt);
}
return;
case (APPLY_SHEAR):
Mxy = mxy; Myx = myx;
while (--numPts >= 0) {
double x = srcPts[srcOff++];
dstPts[dstOff++] = (float) (Mxy * srcPts[srcOff++]);
dstPts[dstOff++] = (float) (Myx * x);
}
return;
case (APPLY_SCALE | APPLY_TRANSLATE):
Mxx = mxx; Mxt = mxt;
Myy = myy; Myt = myt;
while (--numPts >= 0) {
dstPts[dstOff++] = (float) (Mxx * srcPts[srcOff++] + Mxt);
dstPts[dstOff++] = (float) (Myy * srcPts[srcOff++] + Myt);
}
return;
case (APPLY_SCALE):
Mxx = mxx; Myy = myy;
while (--numPts >= 0) {
dstPts[dstOff++] = (float) (Mxx * srcPts[srcOff++]);
dstPts[dstOff++] = (float) (Myy * srcPts[srcOff++]);
}
return;
case (APPLY_TRANSLATE):
Mxt = mxt; Myt = myt;
while (--numPts >= 0) {
dstPts[dstOff++] = (float) (srcPts[srcOff++] + Mxt);
dstPts[dstOff++] = (float) (srcPts[srcOff++] + Myt);
}
return;
case (APPLY_IDENTITY):
while (--numPts >= 0) {
dstPts[dstOff++] = (float) (srcPts[srcOff++]);
dstPts[dstOff++] = (float) (srcPts[srcOff++]);
}
return;
}
}
public Point2D inverseTransform(Point2D ptSrc, Point2D ptDst)
throws NoninvertibleTransformException
{
if (ptDst == null) {
ptDst = new Point2D();
}
double x = ptSrc.x;
double y = ptSrc.y;
switch (state) {
default:
stateError();
case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
x -= mxt;
y -= myt;
case (APPLY_SHEAR | APPLY_SCALE):
double det = mxx * myy - mxy * myx;
if (det == 0 || Math.abs(det) <= Double.MIN_VALUE) {
throw new NoninvertibleTransformException("Determinant is "+
det);
}
ptDst.setLocation((float)((x * myy - y * mxy) / det),
(float)((y * mxx - x * myx) / det));
return ptDst;
case (APPLY_SHEAR | APPLY_TRANSLATE):
x -= mxt;
y -= myt;
case (APPLY_SHEAR):
if (mxy == 0.0 || myx == 0.0) {
throw new NoninvertibleTransformException("Determinant is 0");
}
ptDst.setLocation((float)(y / myx), (float)(x / mxy));
return ptDst;
case (APPLY_SCALE | APPLY_TRANSLATE):
x -= mxt;
y -= myt;
case (APPLY_SCALE):
if (mxx == 0.0 || myy == 0.0) {
throw new NoninvertibleTransformException("Determinant is 0");
}
ptDst.setLocation((float)(x / mxx), (float)(y / myy));
return ptDst;
case (APPLY_TRANSLATE):
ptDst.setLocation((float)(x - mxt), (float)(y - myt));
return ptDst;
case (APPLY_IDENTITY):
ptDst.setLocation((float) x, (float) y);
return ptDst;
}
}
@Override
public Vec3d inverseTransform(Vec3d src, Vec3d dst)
throws NoninvertibleTransformException
{
if (dst == null) {
dst = new Vec3d();
}
double x = src.x;
double y = src.y;
double z = src.z;
switch (state) {
default:
stateError();
case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
x -= mxt;
y -= myt;
case (APPLY_SHEAR | APPLY_SCALE):
double det = mxx * myy - mxy * myx;
if (det == 0 || Math.abs(det) <= Double.MIN_VALUE) {
throw new NoninvertibleTransformException("Determinant is "+
det);
}
dst.set(((x * myy - y * mxy) / det), ((y * mxx - x * myx) / det), z);
return dst;
case (APPLY_SHEAR | APPLY_TRANSLATE):
x -= mxt;
y -= myt;
case (APPLY_SHEAR):
if (mxy == 0.0 || myx == 0.0) {
throw new NoninvertibleTransformException("Determinant is 0");
}
dst.set((y / myx), (x / mxy), z);
return dst;
case (APPLY_SCALE | APPLY_TRANSLATE):
x -= mxt;
y -= myt;
case (APPLY_SCALE):
if (mxx == 0.0 || myy == 0.0) {
throw new NoninvertibleTransformException("Determinant is 0");
}
dst.set((x / mxx), (y / myy), z);
return dst;
case (APPLY_TRANSLATE):
dst.set((x - mxt), (y - myt), z);
return dst;
case (APPLY_IDENTITY):
dst.set(x, y, z);
return dst;
}
}
@Override
public Vec3d inverseDeltaTransform(Vec3d src, Vec3d dst)
throws NoninvertibleTransformException
{
if (dst == null) {
dst = new Vec3d();
}
double x = src.x;
double y = src.y;
double z = src.z;
switch (state) {
default:
stateError();
case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
case (APPLY_SHEAR | APPLY_SCALE):
double det = mxx * myy - mxy * myx;
if (det == 0 || Math.abs(det) <= Double.MIN_VALUE) {
throw new NoninvertibleTransformException("Determinant is "+
det);
}
dst.set(((x * myy - y * mxy) / det), ((y * mxx - x * myx) / det), z);
return dst;
case (APPLY_SHEAR | APPLY_TRANSLATE):
case (APPLY_SHEAR):
if (mxy == 0.0 || myx == 0.0) {
throw new NoninvertibleTransformException("Determinant is 0");
}
dst.set((y / myx), (x / mxy), z);
return dst;
case (APPLY_SCALE | APPLY_TRANSLATE):
case (APPLY_SCALE):
if (mxx == 0.0 || myy == 0.0) {
throw new NoninvertibleTransformException("Determinant is 0");
}
dst.set((x / mxx), (y / myy), z);
return dst;
case (APPLY_TRANSLATE):
case (APPLY_IDENTITY):
dst.set(x, y, z);
return dst;
}
}
private BaseBounds inversTransform2DBounds(RectBounds src, RectBounds dst)
throws NoninvertibleTransformException
{
switch (state) {
default:
stateError();
case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
case (APPLY_SHEAR | APPLY_SCALE):
double det = mxx * myy - mxy * myx;
if (det == 0 || Math.abs(det) <= Double.MIN_VALUE) {
throw new NoninvertibleTransformException("Determinant is "+
det);
}
double x1 = src.getMinX() - mxt;
double y1 = src.getMinY() - myt;
double x2 = src.getMaxX() - mxt;
double y2 = src.getMaxY() - myt;
dst.setBoundsAndSort((float) ((x1 * myy - y1 * mxy) / det),
(float) ((y1 * mxx - x1 * myx) / det),
(float) ((x2 * myy - y2 * mxy) / det),
(float) ((y2 * mxx - x2 * myx) / det));
dst.add((float) ((x2 * myy - y1 * mxy) / det),
(float) ((y1 * mxx - x2 * myx) / det));
dst.add((float) ((x1 * myy - y2 * mxy) / det),
(float) ((y2 * mxx - x1 * myx) / det));
return dst;
case (APPLY_SHEAR | APPLY_TRANSLATE):
if (mxy == 0.0 || myx == 0.0) {
throw new NoninvertibleTransformException("Determinant is 0");
}
dst.setBoundsAndSort((float) ((src.getMinY() - myt) / myx),
(float) ((src.getMinX() - mxt) / mxy),
(float) ((src.getMaxY() - myt) / myx),
(float) ((src.getMaxX() - mxt) / mxy));
break;
case (APPLY_SHEAR):
if (mxy == 0.0 || myx == 0.0) {
throw new NoninvertibleTransformException("Determinant is 0");
}
dst.setBoundsAndSort((float) (src.getMinY() / myx),
(float) (src.getMinX() / mxy),
(float) (src.getMaxY() / myx),
(float) (src.getMaxX() / mxy));
break;
case (APPLY_SCALE | APPLY_TRANSLATE):
if (mxx == 0.0 || myy == 0.0) {
throw new NoninvertibleTransformException("Determinant is 0");
}
dst.setBoundsAndSort((float) ((src.getMinX() - mxt) / mxx),
(float) ((src.getMinY() - myt) / myy),
(float) ((src.getMaxX() - mxt) / mxx),
(float) ((src.getMaxY() - myt) / myy));
break;
case (APPLY_SCALE):
if (mxx == 0.0 || myy == 0.0) {
throw new NoninvertibleTransformException("Determinant is 0");
}
dst.setBoundsAndSort((float) (src.getMinX() / mxx),
(float) (src.getMinY() / myy),
(float) (src.getMaxX() / mxx),
(float) (src.getMaxY() / myy));
break;
case (APPLY_TRANSLATE):
dst.setBounds((float) (src.getMinX() - mxt),
(float) (src.getMinY() - myt),
(float) (src.getMaxX() - mxt),
(float) (src.getMaxY() - myt));
break;
case (APPLY_IDENTITY):
if (dst != src) {
((RectBounds) dst).setBounds((RectBounds) src);
}
break;
}
return dst;
}
private BaseBounds inversTransform3DBounds(BaseBounds src, BaseBounds dst)
throws NoninvertibleTransformException
{
switch (state) {
default:
stateError();
case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
case (APPLY_SHEAR | APPLY_SCALE):
case (APPLY_SHEAR | APPLY_TRANSLATE):
case (APPLY_SHEAR):
double det = mxx * myy - mxy * myx;
if (det == 0 || Math.abs(det) <= Double.MIN_VALUE) {
throw new NoninvertibleTransformException("Determinant is "
+ det);
}
double x1 = src.getMinX() - mxt;
double y1 = src.getMinY() - myt;
double z1 = src.getMinZ();
double x2 = src.getMaxX() - mxt;
double y2 = src.getMaxY() - myt;
double z2 = src.getMaxZ();
dst = dst.deriveWithNewBoundsAndSort(
(float) ((x1 * myy - y1 * mxy) / det),
(float) ((y1 * mxx - x1 * myx) / det),
(float) (z1 / det),
(float) ((x2 * myy - y2 * mxy) / det),
(float) ((y2 * mxx - x2 * myx) / det),
(float) (z2 / det));
dst.add((float) ((x2 * myy - y1 * mxy) / det),
(float) ((y1 * mxx - x2 * myx) / det), 0);
dst.add((float) ((x1 * myy - y2 * mxy) / det),
(float) ((y2 * mxx - x1 * myx) / det), 0);
return dst;
case (APPLY_SCALE | APPLY_TRANSLATE):
if (mxx == 0.0 || myy == 0.0) {
throw new NoninvertibleTransformException("Determinant is 0");
}
dst = dst.deriveWithNewBoundsAndSort((float) ((src.getMinX() - mxt) / mxx),
(float) ((src.getMinY() - myt) / myy),
(float) src.getMinZ(),
(float) ((src.getMaxX() - mxt) / mxx),
(float) ((src.getMaxY() - myt) / myy),
(float) src.getMaxZ());
break;
case (APPLY_SCALE):
if (mxx == 0.0 || myy == 0.0) {
throw new NoninvertibleTransformException("Determinant is 0");
}
dst = dst.deriveWithNewBoundsAndSort((float) (src.getMinX() / mxx),
(float) (src.getMinY() / myy),
(float) src.getMinZ(),
(float) (src.getMaxX() / mxx),
(float) (src.getMaxY() / myy),
(float) src.getMaxZ());
break;
case (APPLY_TRANSLATE):
dst = dst.deriveWithNewBounds((float) (src.getMinX() - mxt),
(float) (src.getMinY() - myt),
(float) src.getMinZ(),
(float) (src.getMaxX() - mxt),
(float) (src.getMaxY() - myt),
(float) src.getMaxZ());
break;
case (APPLY_IDENTITY):
if (dst != src) {
dst = dst.deriveWithNewBounds(src);
}
break;
}
return dst;
}
public BaseBounds inverseTransform(BaseBounds src, BaseBounds dst)
throws NoninvertibleTransformException
{
if (src.getBoundsType() != BaseBounds.BoundsType.RECTANGLE ||
dst.getBoundsType() != BaseBounds.BoundsType.RECTANGLE) {
return inversTransform3DBounds(src, dst);
}
return inversTransform2DBounds((RectBounds)src, (RectBounds)dst);
}
public void inverseTransform(Rectangle src, Rectangle dst)
throws NoninvertibleTransformException
{
switch (state) {
default:
stateError();
case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
case (APPLY_SHEAR | APPLY_SCALE):
case (APPLY_SHEAR | APPLY_TRANSLATE):
case (APPLY_SHEAR):
case (APPLY_SCALE | APPLY_TRANSLATE):
case (APPLY_SCALE):
RectBounds b = new RectBounds(src);
b = (RectBounds) inverseTransform(b, b);
dst.setBounds(b);
return;
case (APPLY_TRANSLATE):
Translate2D.transform(src, dst, -mxt, -myt);
return;
case (APPLY_IDENTITY):
if (dst != src) {
dst.setBounds(src);
}
return;
}
}
public void inverseTransform(float[] srcPts, int srcOff,
float[] dstPts, int dstOff,
int numPts)
throws NoninvertibleTransformException
{
doInverseTransform(srcPts, srcOff, dstPts, dstOff, numPts, state);
}
public void inverseDeltaTransform(float[] srcPts, int srcOff,
float[] dstPts, int dstOff,
int numPts)
throws NoninvertibleTransformException
{
doInverseTransform(srcPts, srcOff, dstPts, dstOff, numPts,
state & ~APPLY_TRANSLATE);
}
private void doInverseTransform(float[] srcPts, int srcOff,
float[] dstPts, int dstOff,
int numPts, int thestate)
throws NoninvertibleTransformException
{
double Mxx, Mxy, Mxt, Myx, Myy, Myt;
double det;
if (dstPts == srcPts &&
dstOff > srcOff && dstOff < srcOff + numPts * 2)
{
System.arraycopy(srcPts, srcOff, dstPts, dstOff, numPts * 2);
srcOff = dstOff;
}
switch (thestate) {
default:
stateError();
case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
Mxx = mxx; Mxy = mxy; Mxt = mxt;
Myx = myx; Myy = myy; Myt = myt;
det = Mxx * Myy - Mxy * Myx;
if (det == 0 || Math.abs(det) <= Double.MIN_VALUE) {
throw new NoninvertibleTransformException("Determinant is "+
det);
}
while (--numPts >= 0) {
double x = srcPts[srcOff++] - Mxt;
double y = srcPts[srcOff++] - Myt;
dstPts[dstOff++] = (float) ((x * Myy - y * Mxy) / det);
dstPts[dstOff++] = (float) ((y * Mxx - x * Myx) / det);
}
return;
case (APPLY_SHEAR | APPLY_SCALE):
Mxx = mxx; Mxy = mxy;
Myx = myx; Myy = myy;
det = Mxx * Myy - Mxy * Myx;
if (det == 0 || Math.abs(det) <= Double.MIN_VALUE) {
throw new NoninvertibleTransformException("Determinant is "+
det);
}
while (--numPts >= 0) {
double x = srcPts[srcOff++];
double y = srcPts[srcOff++];
dstPts[dstOff++] = (float) ((x * Myy - y * Mxy) / det);
dstPts[dstOff++] = (float) ((y * Mxx - x * Myx) / det);
}
return;
case (APPLY_SHEAR | APPLY_TRANSLATE):
Mxy = mxy; Mxt = mxt;
Myx = myx; Myt = myt;
if (Mxy == 0.0 || Myx == 0.0) {
throw new NoninvertibleTransformException("Determinant is 0");
}
while (--numPts >= 0) {
double x = srcPts[srcOff++] - Mxt;
dstPts[dstOff++] = (float) ((srcPts[srcOff++] - Myt) / Myx);
dstPts[dstOff++] = (float) (x / Mxy);
}
return;
case (APPLY_SHEAR):
Mxy = mxy; Myx = myx;
if (Mxy == 0.0 || Myx == 0.0) {
throw new NoninvertibleTransformException("Determinant is 0");
}
while (--numPts >= 0) {
double x = srcPts[srcOff++];
dstPts[dstOff++] = (float) (srcPts[srcOff++] / Myx);
dstPts[dstOff++] = (float) (x / Mxy);
}
return;
case (APPLY_SCALE | APPLY_TRANSLATE):
Mxx = mxx; Mxt = mxt;
Myy = myy; Myt = myt;
if (Mxx == 0.0 || Myy == 0.0) {
throw new NoninvertibleTransformException("Determinant is 0");
}
while (--numPts >= 0) {
dstPts[dstOff++] = (float) ((srcPts[srcOff++] - Mxt) / Mxx);
dstPts[dstOff++] = (float) ((srcPts[srcOff++] - Myt) / Myy);
}
return;
case (APPLY_SCALE):
Mxx = mxx; Myy = myy;
if (Mxx == 0.0 || Myy == 0.0) {
throw new NoninvertibleTransformException("Determinant is 0");
}
while (--numPts >= 0) {
dstPts[dstOff++] = (float) (srcPts[srcOff++] / Mxx);
dstPts[dstOff++] = (float) (srcPts[srcOff++] / Myy);
}
return;
case (APPLY_TRANSLATE):
Mxt = mxt; Myt = myt;
while (--numPts >= 0) {
dstPts[dstOff++] = (float) (srcPts[srcOff++] - Mxt);
dstPts[dstOff++] = (float) (srcPts[srcOff++] - Myt);
}
return;
case (APPLY_IDENTITY):
if (srcPts != dstPts || srcOff != dstOff) {
System.arraycopy(srcPts, srcOff, dstPts, dstOff,
numPts * 2);
}
return;
}
}
public void inverseTransform(double[] srcPts, int srcOff,
double[] dstPts, int dstOff,
int numPts)
throws NoninvertibleTransformException
{
double Mxx, Mxy, Mxt, Myx, Myy, Myt;
double det;
if (dstPts == srcPts &&
dstOff > srcOff && dstOff < srcOff + numPts * 2)
{
System.arraycopy(srcPts, srcOff, dstPts, dstOff, numPts * 2);
srcOff = dstOff;
}
switch (state) {
default:
stateError();
case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
Mxx = mxx; Mxy = mxy; Mxt = mxt;
Myx = myx; Myy = myy; Myt = myt;
det = Mxx * Myy - Mxy * Myx;
if (det == 0 || Math.abs(det) <= Double.MIN_VALUE) {
throw new NoninvertibleTransformException("Determinant is "+
det);
}
while (--numPts >= 0) {
double x = srcPts[srcOff++] - Mxt;
double y = srcPts[srcOff++] - Myt;
dstPts[dstOff++] = (x * Myy - y * Mxy) / det;
dstPts[dstOff++] = (y * Mxx - x * Myx) / det;
}
return;
case (APPLY_SHEAR | APPLY_SCALE):
Mxx = mxx; Mxy = mxy;
Myx = myx; Myy = myy;
det = Mxx * Myy - Mxy * Myx;
if (det == 0 || Math.abs(det) <= Double.MIN_VALUE) {
throw new NoninvertibleTransformException("Determinant is "+
det);
}
while (--numPts >= 0) {
double x = srcPts[srcOff++];
double y = srcPts[srcOff++];
dstPts[dstOff++] = (x * Myy - y * Mxy) / det;
dstPts[dstOff++] = (y * Mxx - x * Myx) / det;
}
return;
case (APPLY_SHEAR | APPLY_TRANSLATE):
Mxy = mxy; Mxt = mxt;
Myx = myx; Myt = myt;
if (Mxy == 0.0 || Myx == 0.0) {
throw new NoninvertibleTransformException("Determinant is 0");
}
while (--numPts >= 0) {
double x = srcPts[srcOff++] - Mxt;
dstPts[dstOff++] = (srcPts[srcOff++] - Myt) / Myx;
dstPts[dstOff++] = x / Mxy;
}
return;
case (APPLY_SHEAR):
Mxy = mxy; Myx = myx;
if (Mxy == 0.0 || Myx == 0.0) {
throw new NoninvertibleTransformException("Determinant is 0");
}
while (--numPts >= 0) {
double x = srcPts[srcOff++];
dstPts[dstOff++] = srcPts[srcOff++] / Myx;
dstPts[dstOff++] = x / Mxy;
}
return;
case (APPLY_SCALE | APPLY_TRANSLATE):
Mxx = mxx; Mxt = mxt;
Myy = myy; Myt = myt;
if (Mxx == 0.0 || Myy == 0.0) {
throw new NoninvertibleTransformException("Determinant is 0");
}
while (--numPts >= 0) {
dstPts[dstOff++] = (srcPts[srcOff++] - Mxt) / Mxx;
dstPts[dstOff++] = (srcPts[srcOff++] - Myt) / Myy;
}
return;
case (APPLY_SCALE):
Mxx = mxx; Myy = myy;
if (Mxx == 0.0 || Myy == 0.0) {
throw new NoninvertibleTransformException("Determinant is 0");
}
while (--numPts >= 0) {
dstPts[dstOff++] = srcPts[srcOff++] / Mxx;
dstPts[dstOff++] = srcPts[srcOff++] / Myy;
}
return;
case (APPLY_TRANSLATE):
Mxt = mxt; Myt = myt;
while (--numPts >= 0) {
dstPts[dstOff++] = srcPts[srcOff++] - Mxt;
dstPts[dstOff++] = srcPts[srcOff++] - Myt;
}
return;
case (APPLY_IDENTITY):
if (srcPts != dstPts || srcOff != dstOff) {
System.arraycopy(srcPts, srcOff, dstPts, dstOff,
numPts * 2);
}
return;
}
}
public Shape createTransformedShape(Shape s) {
if (s == null) {
return null;
}
return new Path2D(s, this);
}
public void translate(double tx, double ty) {
switch (state) {
default:
stateError();
case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
mxt = tx * mxx + ty * mxy + mxt;
myt = tx * myx + ty * myy + myt;
if (mxt == 0.0 && myt == 0.0) {
state = APPLY_SHEAR | APPLY_SCALE;
if (type != TYPE_UNKNOWN) {
type &= ~TYPE_TRANSLATION;
}
}
return;
case (APPLY_SHEAR | APPLY_SCALE):
mxt = tx * mxx + ty * mxy;
myt = tx * myx + ty * myy;
if (mxt != 0.0 || myt != 0.0) {
state = APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE;
type |= TYPE_TRANSLATION;
}
return;
case (APPLY_SHEAR | APPLY_TRANSLATE):
mxt = ty * mxy + mxt;
myt = tx * myx + myt;
if (mxt == 0.0 && myt == 0.0) {
state = APPLY_SHEAR;
if (type != TYPE_UNKNOWN) {
type &= ~TYPE_TRANSLATION;
}
}
return;
case (APPLY_SHEAR):
mxt = ty * mxy;
myt = tx * myx;
if (mxt != 0.0 || myt != 0.0) {
state = APPLY_SHEAR | APPLY_TRANSLATE;
type |= TYPE_TRANSLATION;
}
return;
case (APPLY_SCALE | APPLY_TRANSLATE):
mxt = tx * mxx + mxt;
myt = ty * myy + myt;
if (mxt == 0.0 && myt == 0.0) {
state = APPLY_SCALE;
if (type != TYPE_UNKNOWN) {
type &= ~TYPE_TRANSLATION;
}
}
return;
case (APPLY_SCALE):
mxt = tx * mxx;
myt = ty * myy;
if (mxt != 0.0 || myt != 0.0) {
state = APPLY_SCALE | APPLY_TRANSLATE;
type |= TYPE_TRANSLATION;
}
return;
case (APPLY_TRANSLATE):
mxt = tx + mxt;
myt = ty + myt;
if (mxt == 0.0 && myt == 0.0) {
state = APPLY_IDENTITY;
type = TYPE_IDENTITY;
}
return;
case (APPLY_IDENTITY):
mxt = tx;
myt = ty;
if (tx != 0.0 || ty != 0.0) {
state = APPLY_TRANSLATE;
type = TYPE_TRANSLATION;
}
return;
}
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
protected final void rotate90() {
double M0 = mxx;
mxx = mxy;
mxy = -M0;
M0 = myx;
myx = myy;
myy = -M0;
int newstate = rot90conversion[this.state];
if ((newstate & (APPLY_SHEAR | APPLY_SCALE)) == APPLY_SCALE &&
mxx == 1.0 && myy == 1.0)
{
newstate -= APPLY_SCALE;
}
this.state = newstate;
type = TYPE_UNKNOWN;
}
protected final void rotate180() {
mxx = -mxx;
myy = -myy;
int oldstate = this.state;
if ((oldstate & (APPLY_SHEAR)) != 0) {
mxy = -mxy;
myx = -myx;
} else {
if (mxx == 1.0 && myy == 1.0) {
this.state = oldstate & ~APPLY_SCALE;
} else {
this.state = oldstate | APPLY_SCALE;
}
}
type = TYPE_UNKNOWN;
}
protected final void rotate270() {
double M0 = mxx;
mxx = -mxy;
mxy = M0;
M0 = myx;
myx = -myy;
myy = M0;
int newstate = rot90conversion[this.state];
if ((newstate & (APPLY_SHEAR | APPLY_SCALE)) == APPLY_SCALE &&
mxx == 1.0 && myy == 1.0)
{
newstate -= APPLY_SCALE;
}
this.state = newstate;
type = TYPE_UNKNOWN;
}
public void rotate(double theta) {
double sin = Math.sin(theta);
if (sin == 1.0) {
rotate90();
} else if (sin == -1.0) {
rotate270();
} else {
double cos = Math.cos(theta);
if (cos == -1.0) {
rotate180();
} else if (cos != 1.0) {
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
}
public void scale(double sx, double sy) {
int mystate = this.state;
switch (mystate) {
default:
stateError();
case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
case (APPLY_SHEAR | APPLY_SCALE):
mxx *= sx;
myy *= sy;
case (APPLY_SHEAR | APPLY_TRANSLATE):
case (APPLY_SHEAR):
mxy *= sy;
myx *= sx;
if (mxy == 0 && myx == 0) {
mystate &= APPLY_TRANSLATE;
if (mxx == 1.0 && myy == 1.0) {
this.type = (mystate == APPLY_IDENTITY
? TYPE_IDENTITY
: TYPE_TRANSLATION);
} else {
mystate |= APPLY_SCALE;
this.type = TYPE_UNKNOWN;
}
this.state = mystate;
}
return;
case (APPLY_SCALE | APPLY_TRANSLATE):
case (APPLY_SCALE):
mxx *= sx;
myy *= sy;
if (mxx == 1.0 && myy == 1.0) {
this.state = (mystate &= APPLY_TRANSLATE);
this.type = (mystate == APPLY_IDENTITY
? TYPE_IDENTITY
: TYPE_TRANSLATION);
} else {
this.type = TYPE_UNKNOWN;
}
return;
case (APPLY_TRANSLATE):
case (APPLY_IDENTITY):
mxx = sx;
myy = sy;
if (sx != 1.0 || sy != 1.0) {
this.state = mystate | APPLY_SCALE;
this.type = TYPE_UNKNOWN;
}
return;
}
}
public void shear(double shx, double shy) {
int mystate = this.state;
switch (mystate) {
default:
stateError();
case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
case (APPLY_SHEAR | APPLY_SCALE):
double M0, M1;
M0 = mxx;
M1 = mxy;
mxx = M0 + M1 * shy;
mxy = M0 * shx + M1;
M0 = myx;
M1 = myy;
myx = M0 + M1 * shy;
myy = M0 * shx + M1;
updateState2D();
return;
case (APPLY_SHEAR | APPLY_TRANSLATE):
case (APPLY_SHEAR):
mxx = mxy * shy;
myy = myx * shx;
if (mxx != 0.0 || myy != 0.0) {
this.state = mystate | APPLY_SCALE;
}
this.type = TYPE_UNKNOWN;
return;
case (APPLY_SCALE | APPLY_TRANSLATE):
case (APPLY_SCALE):
mxy = mxx * shx;
myx = myy * shy;
if (mxy != 0.0 || myx != 0.0) {
this.state = mystate | APPLY_SHEAR;
}
this.type = TYPE_UNKNOWN;
return;
case (APPLY_TRANSLATE):
case (APPLY_IDENTITY):
mxy = shx;
myx = shy;
if (mxy != 0.0 || myx != 0.0) {
this.state = mystate | APPLY_SCALE | APPLY_SHEAR;
this.type = TYPE_UNKNOWN;
}
return;
}
}
public void concatenate(BaseTransform Tx) {
switch (Tx.getDegree()) {
case IDENTITY:
return;
case TRANSLATE_2D:
translate(Tx.getMxt(), Tx.getMyt());
return;
case AFFINE_2D:
break;
default:
if (!Tx.is2D()) {
degreeError(Degree.AFFINE_2D);
}
if (!(Tx instanceof AffineBase)) {
Tx = new Affine2D(Tx);
}
break;
}
double M0, M1;
double Txx, Txy, Tyx, Tyy;
double Txt, Tyt;
int mystate = state;
AffineBase at = (AffineBase) Tx;
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
case (HI_SHEAR | HI_SCALE | HI_TRANSLATE | APPLY_IDENTITY):
mxy = at.mxy;
myx = at.myx;
case (HI_SCALE | HI_TRANSLATE | APPLY_IDENTITY):
mxx = at.mxx;
myy = at.myy;
case (HI_TRANSLATE | APPLY_IDENTITY):
mxt = at.mxt;
myt = at.myt;
state = txstate;
type = at.type;
return;
case (HI_SHEAR | HI_SCALE | APPLY_IDENTITY):
mxy = at.mxy;
myx = at.myx;
case (HI_SCALE | APPLY_IDENTITY):
mxx = at.mxx;
myy = at.myy;
state = txstate;
type = at.type;
return;
case (HI_SHEAR | HI_TRANSLATE | APPLY_IDENTITY):
mxt = at.mxt;
myt = at.myt;
case (HI_SHEAR | APPLY_IDENTITY):
mxy = at.mxy;
myx = at.myx;
mxx = myy = 0.0;
state = txstate;
type = at.type;
return;
case (HI_TRANSLATE | APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
case (HI_TRANSLATE | APPLY_SHEAR | APPLY_SCALE):
case (HI_TRANSLATE | APPLY_SHEAR | APPLY_TRANSLATE):
case (HI_TRANSLATE | APPLY_SHEAR):
case (HI_TRANSLATE | APPLY_SCALE | APPLY_TRANSLATE):
case (HI_TRANSLATE | APPLY_SCALE):
case (HI_TRANSLATE | APPLY_TRANSLATE):
translate(at.mxt, at.myt);
return;
case (HI_SCALE | APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
case (HI_SCALE | APPLY_SHEAR | APPLY_SCALE):
case (HI_SCALE | APPLY_SHEAR | APPLY_TRANSLATE):
case (HI_SCALE | APPLY_SHEAR):
case (HI_SCALE | APPLY_SCALE | APPLY_TRANSLATE):
case (HI_SCALE | APPLY_SCALE):
case (HI_SCALE | APPLY_TRANSLATE):
scale(at.mxx, at.myy);
return;
case (HI_SHEAR | APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
case (HI_SHEAR | APPLY_SHEAR | APPLY_SCALE):
Txy = at.mxy; Tyx = at.myx;
M0 = mxx;
mxx = mxy * Tyx;
mxy = M0 * Txy;
M0 = myx;
myx = myy * Tyx;
myy = M0 * Txy;
type = TYPE_UNKNOWN;
return;
case (HI_SHEAR | APPLY_SHEAR | APPLY_TRANSLATE):
case (HI_SHEAR | APPLY_SHEAR):
mxx = mxy * at.myx;
mxy = 0.0;
myy = myx * at.mxy;
myx = 0.0;
state = mystate ^ (APPLY_SHEAR | APPLY_SCALE);
type = TYPE_UNKNOWN;
return;
case (HI_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
case (HI_SHEAR | APPLY_SCALE):
mxy = mxx * at.mxy;
mxx = 0.0;
myx = myy * at.myx;
myy = 0.0;
state = mystate ^ (APPLY_SHEAR | APPLY_SCALE);
type = TYPE_UNKNOWN;
return;
case (HI_SHEAR | APPLY_TRANSLATE):
mxx = 0.0;
mxy = at.mxy;
myx = at.myx;
myy = 0.0;
state = APPLY_TRANSLATE | APPLY_SHEAR;
type = TYPE_UNKNOWN;
return;
}
Txx = at.mxx; Txy = at.mxy; Txt = at.mxt;
Tyx = at.myx; Tyy = at.myy; Tyt = at.myt;
switch (mystate) {
default:
stateError();
case (APPLY_SHEAR | APPLY_SCALE):
state = mystate | txstate;
case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
M0 = mxx;
M1 = mxy;
mxx = Txx * M0 + Tyx * M1;
mxy = Txy * M0 + Tyy * M1;
mxt += Txt * M0 + Tyt * M1;
M0 = myx;
M1 = myy;
myx = Txx * M0 + Tyx * M1;
myy = Txy * M0 + Tyy * M1;
myt += Txt * M0 + Tyt * M1;
type = TYPE_UNKNOWN;
return;
case (APPLY_SHEAR | APPLY_TRANSLATE):
case (APPLY_SHEAR):
M0 = mxy;
mxx = Tyx * M0;
mxy = Tyy * M0;
mxt += Tyt * M0;
M0 = myx;
myx = Txx * M0;
myy = Txy * M0;
myt += Txt * M0;
break;
case (APPLY_SCALE | APPLY_TRANSLATE):
case (APPLY_SCALE):
M0 = mxx;
mxx = Txx * M0;
mxy = Txy * M0;
mxt += Txt * M0;
M0 = myy;
myx = Tyx * M0;
myy = Tyy * M0;
myt += Tyt * M0;
break;
case (APPLY_TRANSLATE):
mxx = Txx;
mxy = Txy;
mxt += Txt;
myx = Tyx;
myy = Tyy;
myt += Tyt;
state = txstate | APPLY_TRANSLATE;
type = TYPE_UNKNOWN;
return;
}
updateState2D();
}
public void concatenate(double Txx, double Txy, double Txt,
double Tyx, double Tyy, double Tyt)
{
double rxx = (mxx * Txx + mxy * Tyx );
double rxy = (mxx * Txy + mxy * Tyy );
double rxt = (mxx * Txt + mxy * Tyt + mxt );
double ryx = (myx * Txx + myy * Tyx );
double ryy = (myx * Txy + myy * Tyy );
double ryt = (myx * Txt + myy * Tyt + myt );
this.mxx = rxx;
this.mxy = rxy;
this.mxt = rxt;
this.myx = ryx;
this.myy = ryy;
this.myt = ryt;
updateState();
}
public void invert()
throws NoninvertibleTransformException
{
double Mxx, Mxy, Mxt;
double Myx, Myy, Myt;
double det;
switch (state) {
default:
stateError();
case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
Mxx = mxx; Mxy = mxy; Mxt = mxt;
Myx = myx; Myy = myy; Myt = myt;
det = Mxx * Myy - Mxy * Myx;
if (det == 0 || Math.abs(det) <= Double.MIN_VALUE) {
throw new NoninvertibleTransformException("Determinant is "+
det);
}
mxx = Myy / det;
myx = -Myx / det;
mxy = -Mxy / det;
myy = Mxx / det;
mxt = (Mxy * Myt - Myy * Mxt) / det;
myt = (Myx * Mxt - Mxx * Myt) / det;
break;
case (APPLY_SHEAR | APPLY_SCALE):
Mxx = mxx; Mxy = mxy;
Myx = myx; Myy = myy;
det = Mxx * Myy - Mxy * Myx;
if (det == 0 || Math.abs(det) <= Double.MIN_VALUE) {
throw new NoninvertibleTransformException("Determinant is "+
det);
}
mxx = Myy / det;
myx = -Myx / det;
mxy = -Mxy / det;
myy = Mxx / det;
break;
case (APPLY_SHEAR | APPLY_TRANSLATE):
Mxy = mxy; Mxt = mxt;
Myx = myx; Myt = myt;
if (Mxy == 0.0 || Myx == 0.0) {
throw new NoninvertibleTransformException("Determinant is 0");
}
myx = 1.0 / Mxy;
mxy = 1.0 / Myx;
mxt = -Myt / Myx;
myt = -Mxt / Mxy;
break;
case (APPLY_SHEAR):
Mxy = mxy;
Myx = myx;
if (Mxy == 0.0 || Myx == 0.0) {
throw new NoninvertibleTransformException("Determinant is 0");
}
myx = 1.0 / Mxy;
mxy = 1.0 / Myx;
break;
case (APPLY_SCALE | APPLY_TRANSLATE):
Mxx = mxx; Mxt = mxt;
Myy = myy; Myt = myt;
if (Mxx == 0.0 || Myy == 0.0) {
throw new NoninvertibleTransformException("Determinant is 0");
}
mxx = 1.0 / Mxx;
myy = 1.0 / Myy;
mxt = -Mxt / Mxx;
myt = -Myt / Myy;
break;
case (APPLY_SCALE):
Mxx = mxx;
Myy = myy;
if (Mxx == 0.0 || Myy == 0.0) {
throw new NoninvertibleTransformException("Determinant is 0");
}
mxx = 1.0 / Mxx;
myy = 1.0 / Myy;
break;
case (APPLY_TRANSLATE):
mxt = -mxt;
myt = -myt;
break;
case (APPLY_IDENTITY):
break;
}
}
}
