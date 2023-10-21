package com.sun.javafx.tk.quantum;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import com.sun.glass.ui.Application;
import com.sun.glass.ui.EventLoop;
import com.sun.javafx.tk.FocusCause;
import com.sun.javafx.tk.TKScene;
import com.sun.javafx.tk.TKStage;
import com.sun.javafx.tk.TKStageListener;
import com.sun.javafx.tk.Toolkit;
abstract class GlassStage implements TKStage {
private static final List<GlassStage> windows = new ArrayList<>();
private static List<TKStage> importantWindows = new ArrayList<>();
private GlassScene scene;
protected TKStageListener stageListener;
private boolean visible;
private boolean important = true;
@SuppressWarnings("removal")
private AccessControlContext accessCtrlCtx = null;
protected static final AtomicReference<GlassStage> activeFSWindow = new AtomicReference<>();
protected GlassStage() {
windows.add(this);
}
@Override public void close() {
assert scene == null;
windows.remove(this);
importantWindows.remove(this);
notifyWindowListeners();
}
@Override public void setTKStageListener(final TKStageListener listener) {
this.stageListener = listener;
}
protected final GlassScene getScene() {
return scene;
}
@Override public void setScene(TKScene scene) {
if (this.scene != null) {
this.scene.setStage(null);
}
this.scene = (GlassScene)scene;
if (this.scene != null) {
this.scene.setStage(this);
}
}
@SuppressWarnings("removal")
final AccessControlContext getAccessControlContext() {
if (accessCtrlCtx == null) {
throw new RuntimeException("Stage security context has not been set!");
}
return accessCtrlCtx;
}
@SuppressWarnings("removal")
static AccessControlContext doIntersectionPrivilege(PrivilegedAction<AccessControlContext> action,
AccessControlContext stack,
AccessControlContext context) {
return AccessController.doPrivileged((PrivilegedAction<AccessControlContext>) () -> {
return AccessController.doPrivilegedWithCombiner((PrivilegedAction<AccessControlContext>) () -> {
return AccessController.getContext();
}, stack);
}, context);
}
@SuppressWarnings("removal")
public final void setSecurityContext(AccessControlContext ctx) {
if (accessCtrlCtx != null) {
throw new RuntimeException("Stage security context has been already set!");
}
AccessControlContext acc = AccessController.getContext();
accessCtrlCtx = doIntersectionPrivilege(
() -> AccessController.getContext(), acc, ctx);
}
@Override public void requestFocus() {
}
@Override public void requestFocus(FocusCause cause) {
}
@Override public void setVisible(boolean visible) {
this.visible = visible;
if (visible) {
if (important) {
importantWindows.add(this);
notifyWindowListeners();
}
} else {
if (important) {
importantWindows.remove(this);
notifyWindowListeners();
}
}
if (scene != null) {
scene.stageVisible(visible);
}
}
boolean isVisible() {
return visible;
}
protected void setPlatformEnabled(boolean enabled) {
}
void windowsSetEnabled(boolean enabled) {
for (GlassStage window : windows.toArray(new GlassStage[windows.size()])) {
if (window != this && windows.contains(window)) {
window.setPlatformEnabled(enabled);
}
}
}
@Override
public void setImportant(boolean important) {
this.important = important;
}
private static void notifyWindowListeners() {
Toolkit.getToolkit().notifyWindowListeners(importantWindows);
}
@SuppressWarnings("removal")
static void requestClosingAllWindows() {
GlassStage fsWindow = activeFSWindow.get();
if (fsWindow != null) {
fsWindow.setFullScreen(false);
}
for (final GlassStage window : windows.toArray(new GlassStage[windows.size()])) {
if (windows.contains(window) && window.isVisible() && window.stageListener != null) {
AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
window.stageListener.closing();
return null;
}, window.getAccessControlContext());
}
}
}
}
