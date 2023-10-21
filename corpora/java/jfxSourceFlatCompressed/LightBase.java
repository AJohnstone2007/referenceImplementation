package javafx.scene;
import com.sun.javafx.collections.TrackableObservableList;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.BoxBounds;
import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.LightBaseHelper;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.transform.TransformHelper;
import com.sun.javafx.sg.prism.NGLightBase;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.tk.Toolkit;
import java.util.List;
import java.util.stream.Collectors;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape3D;
import com.sun.javafx.logging.PlatformLogger;
public abstract class LightBase extends Node {
static {
LightBaseHelper.setLightBaseAccessor(new LightBaseHelper.LightBaseAccessor() {
@Override
public void doMarkDirty(Node node, DirtyBits dirtyBit) {
((LightBase) node).doMarkDirty(dirtyBit);
}
@Override
public void doUpdatePeer(Node node) {
((LightBase) node).doUpdatePeer();
}
@Override
public BaseBounds doComputeGeomBounds(Node node,
BaseBounds bounds, BaseTransform tx) {
return ((LightBase) node).doComputeGeomBounds(bounds, tx);
}
@Override
public boolean doComputeContains(Node node, double localX, double localY) {
return ((LightBase) node).doComputeContains(localX, localY);
}
});
}
private Affine3D localToSceneTx = new Affine3D();
{
LightBaseHelper.initHelper(this);
}
protected LightBase() {
this(Color.WHITE);
}
protected LightBase(Color color) {
if (!Platform.isSupported(ConditionalFeature.SCENE3D)) {
String logname = LightBase.class.getName();
PlatformLogger.getLogger(logname).warning("System can't support "
+ "ConditionalFeature.SCENE3D");
}
setColor(color);
this.localToSceneTransformProperty().addListener(observable ->
NodeHelper.markDirty(this, DirtyBits.NODE_LIGHT_TRANSFORM));
}
private ObjectProperty<Color> color;
public final void setColor(Color value) {
colorProperty().set(value);
}
public final Color getColor() {
return color == null ? null : color.get();
}
public final ObjectProperty<Color> colorProperty() {
if (color == null) {
color = new SimpleObjectProperty<Color>(LightBase.this, "color") {
@Override
protected void invalidated() {
NodeHelper.markDirty(LightBase.this, DirtyBits.NODE_LIGHT);
}
};
}
return color;
}
private BooleanProperty lightOn;
public final void setLightOn(boolean value) {
lightOnProperty().set(value);
}
public final boolean isLightOn() {
return lightOn == null ? true : lightOn.get();
}
public final BooleanProperty lightOnProperty() {
if (lightOn == null) {
lightOn = new SimpleBooleanProperty(LightBase.this, "lightOn", true) {
@Override
protected void invalidated() {
NodeHelper.markDirty(LightBase.this, DirtyBits.NODE_LIGHT);
}
};
}
return lightOn;
}
private ObservableList<Node> scope;
public ObservableList<Node> getScope() {
if (scope == null) {
scope = new TrackableObservableList<>() {
@Override
protected void onChanged(Change<Node> c) {
doOnChanged(c, exclusionScope);
}
};
}
return scope;
}
private ObservableList<Node> exclusionScope;
public ObservableList<Node> getExclusionScope() {
if (exclusionScope == null) {
exclusionScope = new TrackableObservableList<>() {
@Override
protected void onChanged(Change<Node> c) {
doOnChanged(c, scope);
}
};
}
return exclusionScope;
}
private void doOnChanged(Change<Node> c, ObservableList<Node> otherScope) {
NodeHelper.markDirty(this, DirtyBits.NODE_LIGHT_SCOPE);
while (c.next()) {
c.getRemoved().forEach(this::markChildrenDirty);
c.getAddedSubList().forEach(node -> {
if (otherScope != null && otherScope.remove(node)) {
return;
}
markChildrenDirty(node);
});
}
}
@Override
void scenesChanged(final Scene newScene, final SubScene newSubScene,
final Scene oldScene, final SubScene oldSubScene) {
if (oldSubScene != null) {
oldSubScene.removeLight(this);
} else if (oldScene != null) {
oldScene.removeLight(this);
}
if (newSubScene != null) {
newSubScene.addLight(this);
} else if (newScene != null) {
newScene.addLight(this);
}
}
DoubleProperty getLightDoubleProperty(String name, double initialValue) {
return new SimpleDoubleProperty(this, name, initialValue) {
@Override
protected void invalidated() {
NodeHelper.markDirty(LightBase.this, DirtyBits.NODE_LIGHT);
}
};
}
private void markOwnerDirty() {
SubScene subScene = getSubScene();
if (subScene != null) {
subScene.markContentDirty();
} else {
Scene scene = getScene();
if (scene != null) {
scene.setNeedsRepaint();
}
}
}
private void markChildrenDirty(Node node) {
if (node instanceof Shape3D) {
NodeHelper.markDirty(((Shape3D) node), DirtyBits.NODE_DRAWMODE);
} else if (node instanceof Parent) {
for (Node child : ((Parent) node).getChildren()) {
if ((scope != null && getScope().contains(child)) ||
(exclusionScope != null && getExclusionScope().contains(child))) {
continue;
}
markChildrenDirty(child);
}
}
}
private void doMarkDirty(DirtyBits dirtyBit) {
if ((scope == null) || getScope().isEmpty()) {
markOwnerDirty();
} else if (dirtyBit != DirtyBits.NODE_LIGHT_SCOPE) {
getScope().forEach(this::markChildrenDirty);
}
}
private void doUpdatePeer() {
NGLightBase peer = getPeer();
if (isDirty(DirtyBits.NODE_LIGHT)) {
peer.setColor((getColor() == null) ?
Toolkit.getPaintAccessor().getPlatformPaint(Color.WHITE)
: Toolkit.getPaintAccessor().getPlatformPaint(getColor()));
peer.setLightOn(isLightOn());
}
if (isDirty(DirtyBits.NODE_LIGHT_SCOPE)) {
if (scope != null) {
if (getScope().isEmpty()) {
peer.setScope(List.of());
} else {
peer.setScope(getScope().stream().map(n -> n.<NGNode>getPeer()).collect(Collectors.toList()));
}
}
if (exclusionScope != null) {
if (getExclusionScope().isEmpty()) {
peer.setExclusionScope(List.of());
} else {
peer.setExclusionScope(getExclusionScope().stream().map(n -> n.<NGNode>getPeer()).collect(Collectors.toList()));
}
}
}
if (isDirty(DirtyBits.NODE_LIGHT_TRANSFORM)) {
localToSceneTx.setToIdentity();
TransformHelper.apply(getLocalToSceneTransform(), localToSceneTx);
peer.setWorldTransform(localToSceneTx);
}
}
private BaseBounds doComputeGeomBounds(BaseBounds bounds, BaseTransform tx) {
return new BoxBounds();
}
private boolean doComputeContains(double localX, double localY) {
return false;
}
}
