package com.sun.javafx.scene.transform;
import javafx.scene.transform.Transform;
public class TransformUtils {
public static Transform immutableTransform(
double mxx, double mxy, double mxz, double tx,
double myx, double myy, double myz, double ty,
double mzx, double mzy, double mzz, double tz) {
return TransformHelper.createImmutableTransform(
mxx, mxy, mxz, tx,
myx, myy, myz, ty,
mzx, mzy, mzz, tz);
}
public static Transform immutableTransform(Transform t) {
return TransformHelper.createImmutableTransform(
t.getMxx(), t.getMxy(), t.getMxz(), t.getTx(),
t.getMyx(), t.getMyy(), t.getMyz(), t.getTy(),
t.getMzx(), t.getMzy(), t.getMzz(), t.getTz());
}
public static Transform immutableTransform(Transform reuse,
double mxx, double mxy, double mxz, double tx,
double myx, double myy, double myz, double ty,
double mzx, double mzy, double mzz, double tz) {
return TransformHelper.createImmutableTransform(reuse,
mxx, mxy, mxz, tx,
myx, myy, myz, ty,
mzx, mzy, mzz, tz);
}
public static Transform immutableTransform(Transform reuse,
Transform t) {
return TransformHelper.createImmutableTransform(reuse,
t.getMxx(), t.getMxy(), t.getMxz(), t.getTx(),
t.getMyx(), t.getMyy(), t.getMyz(), t.getTy(),
t.getMzx(), t.getMzy(), t.getMzz(), t.getTz());
}
public static Transform immutableTransform(Transform reuse,
Transform left, Transform right) {
return TransformHelper.createImmutableTransform(reuse, left, right);
}
}
