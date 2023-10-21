package javafx.scene;
import com.sun.javafx.geom.PickRay;
import com.sun.javafx.geom.Vec3d;
import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.geom.transform.GeneralTransform3D;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.PerspectiveCameraHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.sg.prism.NGPerspectiveCamera;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import com.sun.javafx.logging.PlatformLogger;
public class PerspectiveCamera extends Camera {
private boolean fixedEyeAtCameraZero = false;
private static final Affine3D LOOK_AT_TX = new Affine3D();
private static final Affine3D LOOK_AT_TX_FIXED_EYE = new Affine3D();
static {
PerspectiveCameraHelper.setPerspectiveCameraAccessor(new PerspectiveCameraHelper.PerspectiveCameraAccessor() {
@Override
public NGNode doCreatePeer(Node node) {
return ((PerspectiveCamera) node).doCreatePeer();
}
@Override
public void doUpdatePeer(Node node) {
((PerspectiveCamera) node).doUpdatePeer();
}
});
LOOK_AT_TX.setToTranslation(0, 0, -1);
LOOK_AT_TX.rotate(Math.PI, 1, 0, 0);
LOOK_AT_TX_FIXED_EYE.rotate(Math.PI, 1, 0, 0);
}
private DoubleProperty fieldOfView;
public final void setFieldOfView(double value){
fieldOfViewProperty().set(value);
}
public final double getFieldOfView() {
return fieldOfView == null ? 30 : fieldOfView.get();
}
public final DoubleProperty fieldOfViewProperty() {
if (fieldOfView == null) {
fieldOfView = new SimpleDoubleProperty(PerspectiveCamera.this, "fieldOfView", 30) {
@Override
protected void invalidated() {
NodeHelper.markDirty(PerspectiveCamera.this, DirtyBits.NODE_CAMERA);
}
};
}
return fieldOfView;
}
private BooleanProperty verticalFieldOfView;
public final void setVerticalFieldOfView(boolean value) {
verticalFieldOfViewProperty().set(value);
}
public final boolean isVerticalFieldOfView() {
return verticalFieldOfView == null ? true : verticalFieldOfView.get();
}
public final BooleanProperty verticalFieldOfViewProperty() {
if (verticalFieldOfView == null) {
verticalFieldOfView = new SimpleBooleanProperty(PerspectiveCamera.this, "verticalFieldOfView", true) {
@Override
protected void invalidated() {
NodeHelper.markDirty(PerspectiveCamera.this, DirtyBits.NODE_CAMERA);
}
};
}
return verticalFieldOfView;
}
{
PerspectiveCameraHelper.initHelper(this);
}
public PerspectiveCamera() {
this(false);
}
public PerspectiveCamera(boolean fixedEyeAtCameraZero) {
if (!Platform.isSupported(ConditionalFeature.SCENE3D)) {
String logname = PerspectiveCamera.class.getName();
PlatformLogger.getLogger(logname).warning("System can't support "
+ "ConditionalFeature.SCENE3D");
}
this.fixedEyeAtCameraZero = fixedEyeAtCameraZero;
}
public final boolean isFixedEyeAtCameraZero() {
return fixedEyeAtCameraZero;
}
@Override
final PickRay computePickRay(double x, double y, PickRay pickRay) {
return PickRay.computePerspectivePickRay(x, y, fixedEyeAtCameraZero,
getViewWidth(), getViewHeight(),
Math.toRadians(getFieldOfView()), isVerticalFieldOfView(),
getCameraTransform(),
getNearClip(), getFarClip(),
pickRay);
}
@Override Camera copy() {
PerspectiveCamera c = new PerspectiveCamera(fixedEyeAtCameraZero);
c.setNearClip(getNearClip());
c.setFarClip(getFarClip());
c.setFieldOfView(getFieldOfView());
return c;
}
private NGNode doCreatePeer() {
NGPerspectiveCamera peer = new NGPerspectiveCamera(fixedEyeAtCameraZero);
peer.setNearClip((float) getNearClip());
peer.setFarClip((float) getFarClip());
peer.setFieldOfView((float) getFieldOfView());
return peer;
}
private void doUpdatePeer() {
NGPerspectiveCamera pgPerspectiveCamera = getPeer();
if (isDirty(DirtyBits.NODE_CAMERA)) {
pgPerspectiveCamera.setVerticalFieldOfView(isVerticalFieldOfView());
pgPerspectiveCamera.setFieldOfView((float) getFieldOfView());
}
}
@Override
void computeProjectionTransform(GeneralTransform3D proj) {
proj.perspective(isVerticalFieldOfView(), Math.toRadians(getFieldOfView()),
getViewWidth() / getViewHeight(), getNearClip(), getFarClip());
}
@Override
void computeViewTransform(Affine3D view) {
if (isFixedEyeAtCameraZero()) {
view.setTransform(LOOK_AT_TX_FIXED_EYE);
} else {
final double viewWidth = getViewWidth();
final double viewHeight = getViewHeight();
final boolean verticalFOV = isVerticalFieldOfView();
final double aspect = viewWidth / viewHeight;
final double tanOfHalfFOV = Math.tan(Math.toRadians(getFieldOfView()) / 2.0);
final double xOffset = -tanOfHalfFOV * (verticalFOV ? aspect : 1.0);
final double yOffset = tanOfHalfFOV * (verticalFOV ? 1.0 : 1.0 / aspect);
final double scale = 2.0 * tanOfHalfFOV /
(verticalFOV ? viewHeight : viewWidth);
view.setToTranslation(xOffset, yOffset, 0.0);
view.concatenate(LOOK_AT_TX);
view.scale(scale, scale, scale);
}
}
@Override
Vec3d computePosition(Vec3d position) {
if (position == null) {
position = new Vec3d();
}
if (fixedEyeAtCameraZero) {
position.set(0.0, 0.0, 0.0);
} else {
final double halfViewWidth = getViewWidth() / 2.0;
final double halfViewHeight = getViewHeight() / 2.0;
final double halfViewDim = isVerticalFieldOfView()
? halfViewHeight : halfViewWidth;
final double distanceZ = halfViewDim
/ Math.tan(Math.toRadians(getFieldOfView() / 2.0));
position.set(halfViewWidth, halfViewHeight, -distanceZ);
}
return position;
}
}
