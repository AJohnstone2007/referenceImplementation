package javafx.scene;
import com.sun.javafx.geom.PickRay;
import com.sun.javafx.geom.Vec3d;
import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.geom.transform.GeneralTransform3D;
import com.sun.javafx.scene.ParallelCameraHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.sg.prism.NGParallelCamera;
public class ParallelCamera extends Camera {
static {
ParallelCameraHelper.setParallelCameraAccessor(new ParallelCameraHelper.ParallelCameraAccessor() {
@Override
public NGNode doCreatePeer(Node node) {
return ((ParallelCamera) node).doCreatePeer();
}
});
}
{
ParallelCameraHelper.initHelper(this);
}
public ParallelCamera() {
}
@Override
Camera copy() {
ParallelCamera c = new ParallelCamera();
c.setNearClip(getNearClip());
c.setFarClip(getFarClip());
return c;
}
private NGNode doCreatePeer() {
final NGParallelCamera peer = new NGParallelCamera();
peer.setNearClip((float) getNearClip());
peer.setFarClip((float) getFarClip());
return peer;
}
@Override
final PickRay computePickRay(double x, double y, PickRay pickRay) {
return PickRay.computeParallelPickRay(x, y, getViewHeight(),
getCameraTransform(),
getNearClip(), getFarClip(), pickRay);
}
@Override
void computeProjectionTransform(GeneralTransform3D proj) {
final double viewWidth = getViewWidth();
final double viewHeight = getViewHeight();
final double halfDepth =
(viewWidth > viewHeight) ? viewWidth / 2.0 : viewHeight / 2.0;
proj.ortho(0.0, viewWidth, viewHeight, 0.0, -halfDepth, halfDepth);
}
@Override
void computeViewTransform(Affine3D view) {
view.setToIdentity();
}
@Override
Vec3d computePosition(Vec3d position) {
if (position == null) {
position = new Vec3d();
}
final double halfViewWidth = getViewWidth() / 2.0;
final double halfViewHeight = getViewHeight() / 2.0;
final double distanceZ = halfViewHeight / Math.tan(Math.toRadians(15.0));
position.set(halfViewWidth, halfViewHeight, -distanceZ);
return position;
}
}
