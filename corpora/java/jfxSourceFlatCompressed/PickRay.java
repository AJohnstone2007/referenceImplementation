package com.sun.javafx.geom;
import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.geom.transform.NoninvertibleTransformException;
public class PickRay {
private Vec3d origin = new Vec3d();
private Vec3d direction = new Vec3d();
private double nearClip = 0.0;
private double farClip = Double.POSITIVE_INFINITY;
static final double EPS = 1.0e-5f;
public PickRay() { }
public PickRay(Vec3d origin, Vec3d direction, double nearClip, double farClip) {
set(origin, direction, nearClip, farClip);
}
public PickRay(double x, double y, double z, double nearClip, double farClip) {
set(x, y, z, nearClip, farClip);
}
public static PickRay computePerspectivePickRay(
double x, double y, boolean fixedEye,
double viewWidth, double viewHeight,
double fieldOfViewRadians, boolean verticalFieldOfView,
Affine3D cameraTransform,
double nearClip, double farClip,
PickRay pickRay) {
if (pickRay == null) {
pickRay = new PickRay();
}
Vec3d direction = pickRay.getDirectionNoClone();
double halfViewWidth = viewWidth / 2.0;
double halfViewHeight = viewHeight / 2.0;
double halfViewDim = verticalFieldOfView? halfViewHeight: halfViewWidth;
double distanceZ = halfViewDim / Math.tan(fieldOfViewRadians / 2.0);
direction.x = x - halfViewWidth;
direction.y = y - halfViewHeight;
direction.z = distanceZ;
Vec3d eye = pickRay.getOriginNoClone();
if (fixedEye) {
eye.set(0.0, 0.0, 0.0);
} else {
eye.set(halfViewWidth, halfViewHeight, -distanceZ);
}
pickRay.nearClip = nearClip * (direction.length() / (fixedEye ? distanceZ : 1.0));
pickRay.farClip = farClip * (direction.length() / (fixedEye ? distanceZ : 1.0));
pickRay.transform(cameraTransform);
return pickRay;
}
public static PickRay computeParallelPickRay(
double x, double y, double viewHeight,
Affine3D cameraTransform,
double nearClip, double farClip,
PickRay pickRay) {
if (pickRay == null) {
pickRay = new PickRay();
}
final double distanceZ = (viewHeight / 2.0)
/ Math.tan(Math.toRadians(15.0));
pickRay.set(x, y, distanceZ, nearClip * distanceZ, farClip * distanceZ);
if (cameraTransform != null) {
pickRay.transform(cameraTransform);
}
return pickRay;
}
public final void set(Vec3d origin, Vec3d direction, double nearClip, double farClip) {
setOrigin(origin);
setDirection(direction);
this.nearClip = nearClip;
this.farClip = farClip;
}
public final void set(double x, double y, double z, double nearClip, double farClip) {
setOrigin(x, y, -z);
setDirection(0, 0, z);
this.nearClip = nearClip;
this.farClip = farClip;
}
public void setPickRay(PickRay other) {
setOrigin(other.origin);
setDirection(other.direction);
nearClip = other.nearClip;
farClip = other.farClip;
}
public PickRay copy() {
return new PickRay(origin, direction, nearClip, farClip);
}
public void setOrigin(Vec3d origin) {
this.origin.set(origin);
}
public void setOrigin(double x, double y, double z) {
this.origin.set(x, y, z);
}
public Vec3d getOrigin(Vec3d rv) {
if (rv == null) {
rv = new Vec3d();
}
rv.set(origin);
return rv;
}
public Vec3d getOriginNoClone() {
return origin;
}
public void setDirection(Vec3d direction) {
this.direction.set(direction);
}
public void setDirection(double x, double y, double z) {
this.direction.set(x, y, z);
}
public Vec3d getDirection(Vec3d rv) {
if (rv == null) {
rv = new Vec3d();
}
rv.set(direction);
return rv;
}
public Vec3d getDirectionNoClone() {
return direction;
}
public double getNearClip() {
return nearClip;
}
public double getFarClip() {
return farClip;
}
public double distance(Vec3d iPnt) {
double x = iPnt.x - origin.x;
double y = iPnt.y - origin.y;
double z = iPnt.z - origin.z;
return Math.sqrt(x*x + y*y + z*z);
}
public Point2D projectToZeroPlane(BaseTransform inversetx,
boolean perspective,
Vec3d tmpvec, Point2D ret)
{
if (tmpvec == null) {
tmpvec = new Vec3d();
}
inversetx.transform(origin, tmpvec);
double origX = tmpvec.x;
double origY = tmpvec.y;
double origZ = tmpvec.z;
tmpvec.add(origin, direction);
inversetx.transform(tmpvec, tmpvec);
double dirX = tmpvec.x - origX;
double dirY = tmpvec.y - origY;
double dirZ = tmpvec.z - origZ;
if (almostZero(dirZ)) {
return null;
}
double t = -origZ / dirZ;
if (perspective && t < 0) {
return null;
}
if (ret == null) {
ret = new Point2D();
}
ret.setLocation((float) (origX + (dirX * t)),
(float) (origY + (dirY * t)));
return ret;
}
private static final double EPSILON_ABSOLUTE = 1.0e-5;
static boolean almostZero(double a) {
return ((a < EPSILON_ABSOLUTE) && (a > -EPSILON_ABSOLUTE));
}
private static boolean isNonZero(double v) {
return ((v > EPS) || (v < -EPS));
}
public void transform(BaseTransform t) {
t.transform(origin, origin);
t.deltaTransform(direction, direction);
}
public void inverseTransform(BaseTransform t)
throws NoninvertibleTransformException {
t.inverseTransform(origin, origin);
t.inverseDeltaTransform(direction, direction);
}
public PickRay project(BaseTransform inversetx,
boolean perspective,
Vec3d tmpvec, Point2D ret)
{
if (tmpvec == null) {
tmpvec = new Vec3d();
}
inversetx.transform(origin, tmpvec);
double origX = tmpvec.x;
double origY = tmpvec.y;
double origZ = tmpvec.z;
tmpvec.add(origin, direction);
inversetx.transform(tmpvec, tmpvec);
double dirX = tmpvec.x - origX;
double dirY = tmpvec.y - origY;
double dirZ = tmpvec.z - origZ;
PickRay pr = new PickRay();
pr.origin.x = origX;
pr.origin.y = origY;
pr.origin.z = origZ;
pr.direction.x = dirX;
pr.direction.y = dirY;
pr.direction.z = dirZ;
return pr;
}
@Override
public String toString() {
return "origin: " + origin + "  direction: " + direction;
}
}
