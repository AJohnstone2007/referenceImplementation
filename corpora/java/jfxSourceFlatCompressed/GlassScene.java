package com.sun.javafx.tk.quantum;
import javafx.application.Platform;
import javafx.scene.input.InputMethodRequests;
import javafx.stage.StageStyle;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.atomic.AtomicBoolean;
import com.sun.glass.ui.Clipboard;
import com.sun.glass.ui.ClipboardAssistance;
import com.sun.glass.ui.View;
import com.sun.javafx.sg.prism.NGCamera;
import com.sun.javafx.sg.prism.NGLightBase;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.tk.TKClipboard;
import com.sun.javafx.tk.TKDragGestureListener;
import com.sun.javafx.tk.TKDragSourceListener;
import com.sun.javafx.tk.TKDropTargetListener;
import com.sun.javafx.tk.TKScene;
import com.sun.javafx.tk.TKSceneListener;
import com.sun.javafx.tk.TKScenePaintListener;
import com.sun.prism.impl.PrismSettings;
import com.sun.prism.paint.Color;
import com.sun.prism.paint.Paint;
abstract class GlassScene implements TKScene {
private GlassStage stage;
protected TKSceneListener sceneListener;
protected TKDragGestureListener dragGestureListener;
protected TKDragSourceListener dragSourceListener;
protected TKDropTargetListener dropTargetListener;
protected InputMethodRequests inputMethodRequests;
private TKScenePaintListener scenePaintListener;
private NGNode root;
private NGCamera camera;
protected Paint fillPaint;
private volatile boolean entireSceneDirty = true;
private boolean doPresent = true;
private final AtomicBoolean painting = new AtomicBoolean(false);
private final boolean depthBuffer;
private final boolean msaa;
SceneState sceneState;
@SuppressWarnings("removal")
private AccessControlContext accessCtrlCtx = null;
protected GlassScene(boolean depthBuffer, boolean msaa) {
this.msaa = msaa;
this.depthBuffer = depthBuffer;
sceneState = new SceneState(this);
}
@Override
public void dispose() {
assert stage == null;
setTKScenePaintListener(null);
root = null;
camera = null;
fillPaint = null;
sceneListener = null;
dragGestureListener = null;
dragSourceListener = null;
dropTargetListener = null;
inputMethodRequests = null;
sceneState = null;
}
@SuppressWarnings("removal")
@Override
public final AccessControlContext getAccessControlContext() {
if (accessCtrlCtx == null) {
throw new RuntimeException("Scene security context has not been set!");
}
return accessCtrlCtx;
}
@SuppressWarnings("removal")
public final void setSecurityContext(AccessControlContext ctx) {
if (accessCtrlCtx != null) {
throw new RuntimeException("Scene security context has been already set!");
}
AccessControlContext acc = AccessController.getContext();
accessCtrlCtx = GlassStage.doIntersectionPrivilege(
() -> AccessController.getContext(), acc, ctx);
}
public void waitForRenderingToComplete() {
PaintCollector.getInstance().waitForRenderingToComplete();
}
@Override
public void waitForSynchronization() {
ViewPainter.renderLock.lock();
}
@Override
public void releaseSynchronization(boolean updateState) {
if (updateState) {
updateSceneState();
}
ViewPainter.renderLock.unlock();
}
boolean getDepthBuffer() {
return depthBuffer;
}
boolean isMSAA() {
return msaa;
}
protected abstract boolean isSynchronous();
@Override public void setTKSceneListener(final TKSceneListener listener) {
this.sceneListener = listener;
}
@Override public synchronized void setTKScenePaintListener(final TKScenePaintListener listener) {
this.scenePaintListener = listener;
}
public void setTKDropTargetListener(final TKDropTargetListener listener) {
this.dropTargetListener = listener;
}
public void setTKDragSourceListener(final TKDragSourceListener listener) {
this.dragSourceListener = listener;
}
public void setTKDragGestureListener(final TKDragGestureListener listener) {
this.dragGestureListener = listener;
}
public void setInputMethodRequests(final InputMethodRequests requests) {
this.inputMethodRequests = requests;
}
@Override
public void setRoot(NGNode root) {
this.root = root;
entireSceneNeedsRepaint();
}
protected NGNode getRoot() {
return root;
}
NGCamera getCamera() {
return camera;
}
private NGLightBase[] lights;
public NGLightBase[] getLights() { return lights; }
public void setLights(NGLightBase[] lights) { this.lights = lights; }
@Override
public void setCamera(NGCamera camera) {
this.camera = camera == null ? NGCamera.INSTANCE : camera;
entireSceneNeedsRepaint();
}
@Override
public void setFillPaint(Object fillPaint) {
this.fillPaint = (Paint)fillPaint;
entireSceneNeedsRepaint();
}
@Override
public void setCursor(Object cursor) {
}
@Override
public final void markDirty() {
sceneChanged();
}
public void entireSceneNeedsRepaint() {
if (Platform.isFxApplicationThread()) {
entireSceneDirty = true;
sceneChanged();
} else {
Platform.runLater(() -> {
entireSceneDirty = true;
sceneChanged();
});
}
}
public boolean isEntireSceneDirty() {
return entireSceneDirty;
}
public void clearEntireSceneDirty() {
entireSceneDirty = false;
}
@Override
public TKClipboard createDragboard(boolean isDragSource) {
ClipboardAssistance assistant = new ClipboardAssistance(Clipboard.DND) {
@SuppressWarnings("removal")
@Override
public void actionPerformed(final int performedAction) {
super.actionPerformed(performedAction);
AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
try {
if (dragSourceListener != null) {
dragSourceListener.dragDropEnd(0, 0, 0, 0,
QuantumToolkit.clipboardActionToTransferMode(performedAction));
}
} finally {
QuantumClipboard.releaseCurrentDragboard();
}
return null;
}, getAccessControlContext());
}
};
return QuantumClipboard.getDragboardInstance(assistant, isDragSource);
}
protected final GlassStage getStage() {
return stage;
}
void setStage(GlassStage stage) {
this.stage = stage;
sceneChanged();
}
final SceneState getSceneState() {
return sceneState;
}
final void updateSceneState() {
sceneState.update();
}
protected View getPlatformView() {
return null;
}
boolean setPainting(boolean value) {
return painting.getAndSet(value);
}
void repaint() {
}
final void stageVisible(boolean visible) {
if (!visible && PrismSettings.forceRepaint) {
PaintCollector.getInstance().removeDirtyScene(this);
}
if (visible) {
PaintCollector.getInstance().addDirtyScene(this);
}
}
public void sceneChanged() {
if (stage != null) {
PaintCollector.getInstance().addDirtyScene(this);
} else {
PaintCollector.getInstance().removeDirtyScene(this);
}
}
public final synchronized void frameRendered() {
if (scenePaintListener != null) {
scenePaintListener.frameRendered();
}
}
public final synchronized void setDoPresent(boolean value) {
doPresent = value;
}
public final synchronized boolean getDoPresent() {
return doPresent;
}
protected Color getClearColor() {
WindowStage windowStage = stage instanceof WindowStage ? (WindowStage)stage : null;
if (windowStage != null && windowStage.getPlatformWindow().isTransparentWindow()) {
return (Color.TRANSPARENT);
} else {
if (fillPaint == null) {
return Color.WHITE;
} else if (fillPaint.isOpaque() ||
(windowStage != null && windowStage.getPlatformWindow().isUnifiedWindow())) {
if (fillPaint.getType() == Paint.Type.COLOR) {
return (Color)fillPaint;
} else if (depthBuffer) {
return Color.TRANSPARENT;
} else {
return null;
}
} else {
return Color.WHITE;
}
}
}
final Paint getCurrentPaint() {
WindowStage windowStage = stage instanceof WindowStage ? (WindowStage)stage : null;
if ((windowStage != null) && windowStage.getStyle() == StageStyle.TRANSPARENT) {
return Color.TRANSPARENT.equals(fillPaint) ? null : fillPaint;
}
if ((fillPaint != null) && fillPaint.isOpaque() && (fillPaint.getType() == Paint.Type.COLOR)) {
return null;
}
return fillPaint;
}
@Override public String toString() {
return (" scene: " + hashCode() + ")");
}
}
