package javafx.scene.transform;
import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.geom.transform.BaseTransform;
public class TransformShim {
public static boolean computeIs2D(Transform t) {
return t.computeIs2D();
}
public static void clearInverseCache(Transform t) {
t.clearInverseCache();
}
public static class ImmutableTransformShim extends Transform.ImmutableTransform {
}
public static Transform getImmutableTransform(Transform transform) {
return new Transform.ImmutableTransform(transform);
}
public static Transform getImmutableTransform(
double mxx, double mxy, double mxz, double tx,
double myx, double myy, double myz, double ty,
double mzx, double mzy, double mzz, double tz) {
return new Transform.ImmutableTransform(
mxx, mxy, mxz, tx,
myx, myy, myz, ty,
mzx, mzy, mzz, tz);
}
public static int getImmutableState2d(Transform t) {
return ((Transform.ImmutableTransform) t).getState2d();
}
public static int getImmutableState3d(Transform t) {
return ((Transform.ImmutableTransform) t).getState3d();
}
public static Transform createRawTransform(
double mxx, double mxy, double mxz, double tx,
double myx, double myy, double myz, double ty,
double mzx, double mzy, double mzz, double tz) {
return new RawTransform(
mxx, mxy, mxz, tx,
myx, myy, myz, ty,
mzx, mzy, mzz, tz);
}
private static class RawTransform extends Transform {
private final double mxx, mxy, mxz, tx;
private final double myx, myy, myz, ty;
private final double mzx, mzy, mzz, tz;
public RawTransform(
double mxx, double mxy, double mxz, double tx,
double myx, double myy, double myz, double ty,
double mzx, double mzy, double mzz, double tz) {
this.mxx = mxx;
this.mxy = mxy;
this.mxz = mxz;
this.tx = tx;
this.myx = myx;
this.myy = myy;
this.myz = myz;
this.ty = ty;
this.mzx = mzx;
this.mzy = mzy;
this.mzz = mzz;
this.tz = tz;
}
@Override
public double getMxx() {
return mxx;
}
@Override
public double getMxy() {
return mxy;
}
@Override
public double getMxz() {
return mxz;
}
@Override
public double getTx() {
return tx;
}
@Override
public double getMyx() {
return myx;
}
@Override
public double getMyy() {
return myy;
}
@Override
public double getMyz() {
return myz;
}
@Override
public double getTy() {
return ty;
}
@Override
public double getMzx() {
return mzx;
}
@Override
public double getMzy() {
return mzy;
}
@Override
public double getMzz() {
return mzz;
}
@Override
public double getTz() {
return tz;
}
@Override
void apply(Affine3D t) {
t.concatenate(
getMxx(), getMxy(), getMxz(), getTx(),
getMyx(), getMyy(), getMyz(), getTy(),
getMzx(), getMzy(), getMzz(), getTz());
}
@Override
BaseTransform derive(BaseTransform t) {
return t.deriveWithConcatenation(
getMxx(), getMxy(), getMxz(), getTx(),
getMyx(), getMyy(), getMyz(), getTy(),
getMzx(), getMzy(), getMzz(), getTz());
}
}
}
