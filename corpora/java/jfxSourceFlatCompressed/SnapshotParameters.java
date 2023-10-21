package javafx.scene;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Transform;
import com.sun.javafx.logging.PlatformLogger;
public class SnapshotParameters {
private boolean depthBuffer;
private Camera camera;
private Transform transform;
private Paint fill;
private Rectangle2D viewport;
public SnapshotParameters() {
}
public boolean isDepthBuffer() {
return depthBuffer;
}
boolean isDepthBufferInternal() {
if(!Platform.isSupported(ConditionalFeature.SCENE3D)) {
return false;
}
return depthBuffer;
}
public void setDepthBuffer(boolean depthBuffer) {
if (depthBuffer && !Platform.isSupported(ConditionalFeature.SCENE3D)) {
String logname = SnapshotParameters.class.getName();
PlatformLogger.getLogger(logname).warning("System can't support "
+ "ConditionalFeature.SCENE3D");
}
this.depthBuffer = depthBuffer;
}
public Camera getCamera() {
return camera;
}
Camera defaultCamera;
Camera getEffectiveCamera() {
if (camera instanceof PerspectiveCamera
&& !Platform.isSupported(ConditionalFeature.SCENE3D)) {
if (defaultCamera == null) {
defaultCamera = new ParallelCamera();
}
return defaultCamera;
}
return camera;
}
public void setCamera(Camera camera) {
if (camera instanceof PerspectiveCamera
&& !Platform.isSupported(ConditionalFeature.SCENE3D)) {
String logname = SnapshotParameters.class.getName();
PlatformLogger.getLogger(logname).warning("System can't support "
+ "ConditionalFeature.SCENE3D");
}
this.camera = camera;
}
public Transform getTransform() {
return transform;
}
public void setTransform(Transform transform) {
this.transform = transform;
}
public Paint getFill() {
return fill;
}
public void setFill(Paint fill) {
this.fill = fill;
}
public Rectangle2D getViewport() {
return viewport;
}
public void setViewport(Rectangle2D viewport) {
this.viewport = viewport;
}
SnapshotParameters copy() {
SnapshotParameters params = new SnapshotParameters();
params.camera = camera == null ? null : camera.copy();
params.depthBuffer = depthBuffer;
params.fill = fill;
params.viewport = viewport;
params.transform = transform == null ? null : transform.clone();
return params;
}
}
