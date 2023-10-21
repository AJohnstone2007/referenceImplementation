package javafx.scene;
import com.sun.javafx.css.StyleManager;
import com.sun.javafx.scene.traversal.Direction;
import com.sun.javafx.scene.traversal.SubSceneTraversalEngine;
import com.sun.javafx.scene.traversal.TopMostTraversalEngine;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.beans.property.*;
import javafx.css.Stylesheet;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Point3D;
import javafx.scene.input.PickResult;
import javafx.scene.paint.Paint;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.PickRay;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.CssFlags;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.SubSceneHelper;
import com.sun.javafx.scene.input.PickResultChooser;
import com.sun.javafx.sg.prism.NGCamera;
import com.sun.javafx.sg.prism.NGLightBase;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.sg.prism.NGSubScene;
import com.sun.javafx.tk.Toolkit;
import com.sun.javafx.logging.PlatformLogger;
public class SubScene extends Node {
static {
SubSceneHelper.setSubSceneAccessor(new SubSceneHelper.SubSceneAccessor() {
@Override
public NGNode doCreatePeer(Node node) {
return ((SubScene) node).doCreatePeer();
}
@Override
public void doUpdatePeer(Node node) {
((SubScene) node).doUpdatePeer();
}
@Override
public BaseBounds doComputeGeomBounds(Node node,
BaseBounds bounds, BaseTransform tx) {
return ((SubScene) node).doComputeGeomBounds(bounds, tx);
}
@Override
public boolean doComputeContains(Node node, double localX, double localY) {
return ((SubScene) node).doComputeContains(localX, localY);
}
@Override
public void doProcessCSS(Node node) {
((SubScene) node).doProcessCSS();
}
@Override
public void doPickNodeLocal(Node node, PickRay localPickRay,
PickResultChooser result) {
((SubScene) node).doPickNodeLocal(localPickRay, result);
}
@Override
public boolean isDepthBuffer(SubScene subScene) {
return subScene.isDepthBufferInternal();
};
@Override
public Camera getEffectiveCamera(SubScene subScene) {
return subScene.getEffectiveCamera();
}
});
}
{
SubSceneHelper.initHelper(this);
}
public SubScene(@NamedArg("root") Parent root, @NamedArg("width") double width, @NamedArg("height") double height) {
this(root, width, height, false, SceneAntialiasing.DISABLED);
}
public SubScene(@NamedArg("root") Parent root, @NamedArg("width") double width, @NamedArg("height") double height,
@NamedArg("depthBuffer") boolean depthBuffer, @NamedArg("antiAliasing") SceneAntialiasing antiAliasing)
{
this.depthBuffer = depthBuffer;
this.antiAliasing = antiAliasing;
boolean isAntiAliasing = !(antiAliasing == null || antiAliasing == SceneAntialiasing.DISABLED);
setRoot(root);
setWidth(width);
setHeight(height);
if ((depthBuffer || isAntiAliasing) && !is3DSupported) {
String logname = SubScene.class.getName();
PlatformLogger.getLogger(logname).warning("System can't support "
+ "ConditionalFeature.SCENE3D");
}
if (isAntiAliasing && !Toolkit.getToolkit().isMSAASupported()) {
String logname = SubScene.class.getName();
PlatformLogger.getLogger(logname).warning("System can't support "
+ "antiAliasing");
}
}
private static boolean is3DSupported =
Platform.isSupported(ConditionalFeature.SCENE3D);
private final SceneAntialiasing antiAliasing;
public final SceneAntialiasing getAntiAliasing() {
return antiAliasing;
}
private final boolean depthBuffer;
public final boolean isDepthBuffer() {
return depthBuffer;
}
private boolean isDepthBufferInternal() {
return is3DSupported ? depthBuffer : false;
}
private ObjectProperty<Parent> root;
public final void setRoot(Parent value) {
rootProperty().set(value);
}
public final Parent getRoot() {
return root == null ? null : root.get();
}
public final ObjectProperty<Parent> rootProperty() {
if (root == null) {
root = new ObjectPropertyBase<Parent>() {
private Parent oldRoot;
private void forceUnbind() {
System.err.println("Unbinding illegal root.");
unbind();
}
@Override
protected void invalidated() {
Parent _value = get();
if (_value == null) {
if (isBound()) { forceUnbind(); }
throw new NullPointerException("Scene's root cannot be null");
}
if (_value.getParent() != null) {
if (isBound()) { forceUnbind(); }
throw new IllegalArgumentException(_value +
"is already inside a scene-graph and cannot be set as root");
}
if (_value.getClipParent() != null) {
if (isBound()) forceUnbind();
throw new IllegalArgumentException(_value +
"is set as a clip on another node, so cannot be set as root");
}
if ((_value.getScene() != null &&
_value.getScene().getRoot() == _value) ||
(_value.getSubScene() != null &&
_value.getSubScene().getRoot() == _value &&
_value.getSubScene() != SubScene.this))
{
if (isBound()) { forceUnbind(); }
throw new IllegalArgumentException(_value +
"is already set as root of another scene or subScene");
}
_value.setTreeVisible(isTreeVisible());
_value.setDisabled(isDisabled());
if (oldRoot != null) {
StyleManager.getInstance().forget(SubScene.this);
oldRoot.setScenes(null, null);
}
oldRoot = _value;
_value.getStyleClass().add(0, "root");
_value.setScenes(getScene(), SubScene.this);
markDirty(SubSceneDirtyBits.ROOT_SG_DIRTY);
_value.resize(getWidth(), getHeight());
_value.requestLayout();
}
@Override
public Object getBean() {
return SubScene.this;
}
@Override
public String getName() {
return "root";
}
};
}
return root;
}
private ObjectProperty<Camera> camera;
public final void setCamera(Camera value) {
cameraProperty().set(value);
}
public final Camera getCamera() {
return camera == null ? null : camera.get();
}
public final ObjectProperty<Camera> cameraProperty() {
if (camera == null) {
camera = new ObjectPropertyBase<Camera>() {
Camera oldCamera = null;
@Override
protected void invalidated() {
Camera _value = get();
if (_value != null) {
if (_value instanceof PerspectiveCamera
&& !SubScene.is3DSupported) {
String logname = SubScene.class.getName();
PlatformLogger.getLogger(logname).warning("System can't support "
+ "ConditionalFeature.SCENE3D");
}
if ((_value.getScene() != null || _value.getSubScene() != null)
&& (_value.getScene() != getScene() || _value.getSubScene() != SubScene.this)) {
throw new IllegalArgumentException(_value
+ "is already part of other scene or subscene");
}
_value.setOwnerSubScene(SubScene.this);
_value.setViewWidth(getWidth());
_value.setViewHeight(getHeight());
}
markDirty(SubSceneDirtyBits.CAMERA_DIRTY);
if (oldCamera != null && oldCamera != _value) {
oldCamera.setOwnerSubScene(null);
}
oldCamera = _value;
}
@Override
public Object getBean() {
return SubScene.this;
}
@Override
public String getName() {
return "camera";
}
};
}
return camera;
}
private Camera defaultCamera;
Camera getEffectiveCamera() {
final Camera cam = getCamera();
if (cam == null
|| (cam instanceof PerspectiveCamera && !is3DSupported)) {
if (defaultCamera == null) {
defaultCamera = new ParallelCamera();
defaultCamera.setOwnerSubScene(this);
defaultCamera.setViewWidth(getWidth());
defaultCamera.setViewHeight(getHeight());
}
return defaultCamera;
}
return cam;
}
final void markContentDirty() {
markDirty(SubSceneDirtyBits.CONTENT_DIRTY);
}
private DoubleProperty width;
public final void setWidth(double value) {
widthProperty().set(value);
}
public final double getWidth() {
return width == null ? 0.0 : width.get();
}
public final DoubleProperty widthProperty() {
if (width == null) {
width = new DoublePropertyBase() {
@Override
public void invalidated() {
final Parent _root = getRoot();
if (_root.getEffectiveNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT) {
NodeHelper.transformsChanged(_root);
}
if (_root.isResizable()) {
_root.resize(get() - _root.getLayoutX() - _root.getTranslateX(), _root.getLayoutBounds().getHeight());
}
markDirty(SubSceneDirtyBits.SIZE_DIRTY);
NodeHelper.geomChanged(SubScene.this);
getEffectiveCamera().setViewWidth(get());
}
@Override
public Object getBean() {
return SubScene.this;
}
@Override
public String getName() {
return "width";
}
};
}
return width;
}
private DoubleProperty height;
public final void setHeight(double value) {
heightProperty().set(value);
}
public final double getHeight() {
return height == null ? 0.0 : height.get();
}
public final DoubleProperty heightProperty() {
if (height == null) {
height = new DoublePropertyBase() {
@Override
public void invalidated() {
final Parent _root = getRoot();
if (_root.isResizable()) {
_root.resize(_root.getLayoutBounds().getWidth(), get() - _root.getLayoutY() - _root.getTranslateY());
}
markDirty(SubSceneDirtyBits.SIZE_DIRTY);
NodeHelper.geomChanged(SubScene.this);
getEffectiveCamera().setViewHeight(get());
}
@Override
public Object getBean() {
return SubScene.this;
}
@Override
public String getName() {
return "height";
}
};
}
return height;
}
private ObjectProperty<Paint> fill;
public final void setFill(Paint value) {
fillProperty().set(value);
}
public final Paint getFill() {
return fill == null ? null : fill.get();
}
public final ObjectProperty<Paint> fillProperty() {
if (fill == null) {
fill = new ObjectPropertyBase<Paint>(null) {
@Override
protected void invalidated() {
markDirty(SubSceneDirtyBits.FILL_DIRTY);
}
@Override
public Object getBean() {
return SubScene.this;
}
@Override
public String getName() {
return "fill";
}
};
}
return fill;
}
private void doUpdatePeer() {
dirtyNodes = false;
if (isDirty()) {
NGSubScene peer = getPeer();
final Camera cam = getEffectiveCamera();
boolean contentChanged = false;
if (cam.getSubScene() == null &&
isDirty(SubSceneDirtyBits.CONTENT_DIRTY)) {
cam.syncPeer();
}
if (isDirty(SubSceneDirtyBits.FILL_DIRTY)) {
Object platformPaint = getFill() == null ? null :
Toolkit.getPaintAccessor().getPlatformPaint(getFill());
peer.setFillPaint(platformPaint);
contentChanged = true;
}
if (isDirty(SubSceneDirtyBits.SIZE_DIRTY)) {
peer.setWidth((float)getWidth());
peer.setHeight((float)getHeight());
}
if (isDirty(SubSceneDirtyBits.CAMERA_DIRTY)) {
peer.setCamera((NGCamera) cam.getPeer());
contentChanged = true;
}
if (isDirty(SubSceneDirtyBits.ROOT_SG_DIRTY)) {
peer.setRoot(getRoot().getPeer());
contentChanged = true;
}
contentChanged |= syncLights();
if (contentChanged || isDirty(SubSceneDirtyBits.CONTENT_DIRTY)) {
peer.markContentDirty();
}
clearDirtyBits();
}
}
@Override
void nodeResolvedOrientationChanged() {
getRoot().parentResolvedOrientationInvalidated();
}
private void doProcessCSS() {
if (cssFlag == CssFlags.CLEAN) { return; }
if (getRoot().cssFlag == CssFlags.CLEAN) {
getRoot().cssFlag = cssFlag;
}
SubSceneHelper.superProcessCSS(this);
getRoot().processCSS();
}
@Override
void processCSS() {
Parent root = getRoot();
if (root.isDirty(DirtyBits.NODE_CSS)) {
root.clearDirty(DirtyBits.NODE_CSS);
if (cssFlag == CssFlags.CLEAN) { cssFlag = CssFlags.UPDATE; }
}
super.processCSS();
}
private ObjectProperty<String> userAgentStylesheet = null;
public final ObjectProperty<String> userAgentStylesheetProperty() {
if (userAgentStylesheet == null) {
userAgentStylesheet = new SimpleObjectProperty<String>(SubScene.this, "userAgentStylesheet", null) {
@Override protected void invalidated() {
StyleManager.getInstance().forget(SubScene.this);
reapplyCSS();
}
};
}
return userAgentStylesheet;
}
public final String getUserAgentStylesheet() {
return userAgentStylesheet == null ? null : userAgentStylesheet.get();
}
public final void setUserAgentStylesheet(String url) {
userAgentStylesheetProperty().set(url);
}
@Override void updateBounds() {
super.updateBounds();
getRoot().updateBounds();
}
private NGNode doCreatePeer() {
if (!is3DSupported) {
return new NGSubScene(false, false);
}
boolean aa = !(antiAliasing == null || antiAliasing == SceneAntialiasing.DISABLED);
return new NGSubScene(depthBuffer, aa && Toolkit.getToolkit().isMSAASupported());
}
private BaseBounds doComputeGeomBounds(BaseBounds bounds, BaseTransform tx) {
int w = (int)Math.ceil(width.get());
int h = (int)Math.ceil(height.get());
bounds = bounds.deriveWithNewBounds(0.0f, 0.0f, 0.0f,
w, h, 0.0f);
bounds = tx.transform(bounds, bounds);
return bounds;
}
boolean dirtyLayout = false;
void setDirtyLayout(Parent p) {
if (!dirtyLayout && p != null && p.getSubScene() == this &&
this.getScene() != null) {
dirtyLayout = true;
markDirtyLayoutBranch();
markDirty(SubSceneDirtyBits.CONTENT_DIRTY);
}
}
private boolean dirtyNodes = false;
void setDirty(Node n) {
if (!dirtyNodes && n != null && n.getSubScene() == this &&
this.getScene() != null) {
dirtyNodes = true;
markDirty(SubSceneDirtyBits.CONTENT_DIRTY);
}
}
void layoutPass() {
if (dirtyLayout) {
Parent r = getRoot();
if (r != null) {
r.layout();
}
dirtyLayout = false;
}
}
private TopMostTraversalEngine traversalEngine = new SubSceneTraversalEngine(this);
boolean traverse(Node node, Direction dir) {
return traversalEngine.trav(node, dir) != null;
}
private enum SubSceneDirtyBits {
SIZE_DIRTY,
FILL_DIRTY,
ROOT_SG_DIRTY,
CAMERA_DIRTY,
LIGHTS_DIRTY,
CONTENT_DIRTY;
private int mask;
private SubSceneDirtyBits() { mask = 1 << ordinal(); }
public final int getMask() { return mask; }
}
private int dirtyBits = ~0;
private void clearDirtyBits() { dirtyBits = 0; }
private boolean isDirty() { return dirtyBits != 0; }
private void setDirty(SubSceneDirtyBits dirtyBit) {
this.dirtyBits |= dirtyBit.getMask();
}
private boolean isDirty(SubSceneDirtyBits dirtyBit) {
return ((this.dirtyBits & dirtyBit.getMask()) != 0);
}
private void markDirty(SubSceneDirtyBits dirtyBit) {
if (!isDirty()) {
NodeHelper.markDirty(this, DirtyBits.NODE_CONTENTS);
}
setDirty(dirtyBit);
}
private boolean doComputeContains(double localX, double localY) {
if (subSceneComputeContains(localX, localY)) {
return true;
} else {
return NodeHelper.computeContains(getRoot(), localX, localY);
}
}
private boolean subSceneComputeContains(double localX, double localY) {
if (localX < 0 || localY < 0 || localX > getWidth() || localY > getHeight()) {
return false;
}
return getFill() != null;
}
private PickResult pickRootSG(double localX, double localY) {
final double viewWidth = getWidth();
final double viewHeight = getHeight();
if (localX < 0 || localY < 0 || localX > viewWidth || localY > viewHeight) {
return null;
}
final PickResultChooser result = new PickResultChooser();
final PickRay pickRay = getEffectiveCamera().computePickRay(localX, localY, new PickRay());
pickRay.getDirectionNoClone().normalize();
getRoot().pickNode(pickRay, result);
return result.toPickResult();
}
private void doPickNodeLocal(PickRay localPickRay, PickResultChooser result) {
final double boundsDistance = intersectsBounds(localPickRay);
if (!Double.isNaN(boundsDistance) && result.isCloser(boundsDistance)) {
final Point3D intersectPt = PickResultChooser.computePoint(
localPickRay, boundsDistance);
final PickResult subSceneResult =
pickRootSG(intersectPt.getX(), intersectPt.getY());
if (subSceneResult != null) {
result.offerSubScenePickResult(this, subSceneResult, boundsDistance);
} else if (isPickOnBounds() ||
subSceneComputeContains(intersectPt.getX(), intersectPt.getY())) {
result.offer(this, boundsDistance, intersectPt);
}
}
}
private List<LightBase> lights = new ArrayList<>();
final void addLight(LightBase light) {
if (!lights.contains(light)) {
markDirty(SubSceneDirtyBits.LIGHTS_DIRTY);
lights.add(light);
}
}
final void removeLight(LightBase light) {
if (lights.remove(light)) {
markDirty(SubSceneDirtyBits.LIGHTS_DIRTY);
}
}
private boolean syncLights() {
boolean lightOwnerChanged = false;
if (!isDirty(SubSceneDirtyBits.LIGHTS_DIRTY)) {
return lightOwnerChanged;
}
NGSubScene pgSubScene = getPeer();
NGLightBase peerLights[] = pgSubScene.getLights();
if (!lights.isEmpty() || (peerLights != null)) {
if (lights.isEmpty()) {
pgSubScene.setLights(null);
} else {
if (peerLights == null || peerLights.length < lights.size()) {
peerLights = new NGLightBase[lights.size()];
}
int i = 0;
for (; i < lights.size(); i++) {
peerLights[i] = lights.get(i).getPeer();
}
while (i < peerLights.length && peerLights[i] != null) {
peerLights[i++] = null;
}
pgSubScene.setLights(peerLights);
}
lightOwnerChanged = true;
}
return lightOwnerChanged;
}
}
