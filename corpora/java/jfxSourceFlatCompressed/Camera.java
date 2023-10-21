package javafx.scene;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.transform.Transform;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.BoxBounds;
import com.sun.javafx.geom.PickRay;
import com.sun.javafx.geom.Vec3d;
import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.geom.transform.GeneralTransform3D;
import com.sun.javafx.geom.transform.NoninvertibleTransformException;
import com.sun.javafx.scene.CameraHelper;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.transform.TransformHelper;
import com.sun.javafx.sg.prism.NGCamera;
import com.sun.javafx.logging.PlatformLogger;
public abstract class Camera extends Node {
static {
CameraHelper.setCameraAccessor(new CameraHelper.CameraAccessor() {
@Override
public void doMarkDirty(Node node, DirtyBits dirtyBit) {
((Camera) node).doMarkDirty(dirtyBit);
}
@Override
public void doUpdatePeer(Node node) {
((Camera) node).doUpdatePeer();
}
@Override
public BaseBounds doComputeGeomBounds(Node node,
BaseBounds bounds, BaseTransform tx) {
return ((Camera) node).doComputeGeomBounds(bounds, tx);
}
@Override
public boolean doComputeContains(Node node, double localX, double localY) {
return ((Camera) node).doComputeContains(localX, localY);
}
@Override
public Point2D project(Camera camera, Point3D p) {
return camera.project(p);
}
@Override
public Point2D pickNodeXYPlane(Camera camera, Node node, double x, double y) {
return camera.pickNodeXYPlane(node, x, y);
}
@Override
public Point3D pickProjectPlane(Camera camera, double x, double y) {
return camera.pickProjectPlane(x, y);
}
});
}
private Affine3D localToSceneTx = new Affine3D();
{
CameraHelper.initHelper(this);
}
protected Camera() {
InvalidationListener dirtyTransformListener = observable
-> NodeHelper.markDirty(this, DirtyBits.NODE_CAMERA_TRANSFORM);
this.localToSceneTransformProperty().addListener(dirtyTransformListener);
this.sceneProperty().addListener(dirtyTransformListener);
}
private double farClipInScene;
private double nearClipInScene;
private Scene ownerScene = null;
private SubScene ownerSubScene = null;
private GeneralTransform3D projViewTx = new GeneralTransform3D();
private GeneralTransform3D projTx = new GeneralTransform3D();
private Affine3D viewTx = new Affine3D();
private double viewWidth = 1.0;
private double viewHeight = 1.0;
private Vec3d position = new Vec3d();
private boolean clipInSceneValid = false;
private boolean projViewTxValid = false;
private boolean localToSceneValid = false;
private boolean sceneToLocalValid = false;
double getFarClipInScene() {
updateClipPlane();
return farClipInScene;
}
double getNearClipInScene() {
updateClipPlane();
return nearClipInScene;
}
private void updateClipPlane() {
if (!clipInSceneValid) {
final Transform localToSceneTransform = getLocalToSceneTransform();
nearClipInScene = localToSceneTransform.transform(0, 0, getNearClip()).getZ();
farClipInScene = localToSceneTransform.transform(0, 0, getFarClip()).getZ();
clipInSceneValid = true;
}
}
private Affine3D sceneToLocalTx = new Affine3D();
Affine3D getSceneToLocalTransform() {
if (!sceneToLocalValid) {
sceneToLocalTx.setTransform(getCameraTransform());
try {
sceneToLocalTx.invert();
} catch (NoninvertibleTransformException ex) {
String logname = Camera.class.getName();
PlatformLogger.getLogger(logname).severe("getSceneToLocalTransform", ex);
sceneToLocalTx.setToIdentity();
}
sceneToLocalValid = true;
}
return sceneToLocalTx;
}
private DoubleProperty nearClip;
public final void setNearClip(double value){
nearClipProperty().set(value);
}
public final double getNearClip() {
return nearClip == null ? 0.1 : nearClip.get();
}
public final DoubleProperty nearClipProperty() {
if (nearClip == null) {
nearClip = new SimpleDoubleProperty(Camera.this, "nearClip", 0.1) {
@Override
protected void invalidated() {
clipInSceneValid = false;
NodeHelper.markDirty(Camera.this, DirtyBits.NODE_CAMERA);
}
};
}
return nearClip;
}
private DoubleProperty farClip;
public final void setFarClip(double value){
farClipProperty().set(value);
}
public final double getFarClip() {
return farClip == null ? 100.0 : farClip.get();
}
public final DoubleProperty farClipProperty() {
if (farClip == null) {
farClip = new SimpleDoubleProperty(Camera.this, "farClip", 100.0) {
@Override
protected void invalidated() {
clipInSceneValid = false;
NodeHelper.markDirty(Camera.this, DirtyBits.NODE_CAMERA);
}
};
}
return farClip;
}
Camera copy() {
return this;
}
private void doUpdatePeer() {
NGCamera peer = getPeer();
if (!NodeHelper.isDirtyEmpty(this)) {
if (isDirty(DirtyBits.NODE_CAMERA)) {
peer.setNearClip((float) getNearClip());
peer.setFarClip((float) getFarClip());
peer.setViewWidth(getViewWidth());
peer.setViewHeight(getViewHeight());
}
if (isDirty(DirtyBits.NODE_CAMERA_TRANSFORM)) {
peer.setWorldTransform(getCameraTransform());
}
peer.setProjViewTransform(getProjViewTransform());
position = computePosition(position);
getCameraTransform().transform(position, position);
peer.setPosition(position);
}
}
void setViewWidth(double width) {
this.viewWidth = width;
NodeHelper.markDirty(this, DirtyBits.NODE_CAMERA);
}
double getViewWidth() {
return viewWidth;
}
void setViewHeight(double height) {
this.viewHeight = height;
NodeHelper.markDirty(this, DirtyBits.NODE_CAMERA);
}
double getViewHeight() {
return viewHeight;
}
void setOwnerScene(Scene s) {
if (s == null) {
ownerScene = null;
} else if (s != ownerScene) {
if (ownerScene != null || ownerSubScene != null) {
throw new IllegalArgumentException(this
+ "is already set as camera in other scene or subscene");
}
ownerScene = s;
markOwnerDirty();
}
}
void setOwnerSubScene(SubScene s) {
if (s == null) {
ownerSubScene = null;
} else if (s != ownerSubScene) {
if (ownerScene != null || ownerSubScene != null) {
throw new IllegalArgumentException(this
+ "is already set as camera in other scene or subscene");
}
ownerSubScene = s;
markOwnerDirty();
}
}
private void doMarkDirty(DirtyBits dirtyBit) {
if (dirtyBit == DirtyBits.NODE_CAMERA_TRANSFORM) {
localToSceneValid = false;
sceneToLocalValid = false;
clipInSceneValid = false;
projViewTxValid = false;
} else if (dirtyBit == DirtyBits.NODE_CAMERA) {
projViewTxValid = false;
}
markOwnerDirty();
}
private void markOwnerDirty() {
if (ownerScene != null) {
ownerScene.markCameraDirty();
}
if (ownerSubScene != null) {
ownerSubScene.markContentDirty();
}
}
Affine3D getCameraTransform() {
if (!localToSceneValid) {
localToSceneTx.setToIdentity();
TransformHelper.apply(getLocalToSceneTransform(), localToSceneTx);
localToSceneValid = true;
}
return localToSceneTx;
}
abstract void computeProjectionTransform(GeneralTransform3D proj);
abstract void computeViewTransform(Affine3D view);
GeneralTransform3D getProjViewTransform() {
if (!projViewTxValid) {
computeProjectionTransform(projTx);
computeViewTransform(viewTx);
projViewTx.set(projTx);
projViewTx.mul(viewTx);
projViewTx.mul(getSceneToLocalTransform());
projViewTxValid = true;
}
return projViewTx;
}
private Point2D project(Point3D p) {
final Vec3d vec = getProjViewTransform().transform(new Vec3d(
p.getX(), p.getY(), p.getZ()));
final double halfViewWidth = getViewWidth() / 2.0;
final double halfViewHeight = getViewHeight() / 2.0;
return new Point2D(
halfViewWidth * (1 + vec.x),
halfViewHeight * (1 - vec.y));
}
private Point2D pickNodeXYPlane(Node node, double x, double y) {
final PickRay ray = computePickRay(x, y, null);
final Affine3D localToScene = new Affine3D();
TransformHelper.apply(node.getLocalToSceneTransform(), localToScene);
final Vec3d o = ray.getOriginNoClone();
final Vec3d d = ray.getDirectionNoClone();
try {
localToScene.inverseTransform(o, o);
localToScene.inverseDeltaTransform(d, d);
} catch (NoninvertibleTransformException e) {
return null;
}
if (almostZero(d.z)) {
return null;
}
final double t = -o.z / d.z;
return new Point2D(o.x + (d.x * t), o.y + (d.y * t));
}
Point3D pickProjectPlane(double x, double y) {
final PickRay ray = computePickRay(x, y, null);
final Vec3d p = new Vec3d();
p.add(ray.getOriginNoClone(), ray.getDirectionNoClone());
return new Point3D(p.x, p.y, p.z);
}
abstract PickRay computePickRay(double x, double y, PickRay pickRay);
abstract Vec3d computePosition(Vec3d position);
private BaseBounds doComputeGeomBounds(BaseBounds bounds, BaseTransform tx) {
return new BoxBounds(0, 0, 0, 0, 0, 0);
}
private boolean doComputeContains(double localX, double localY) {
return false;
}
}
