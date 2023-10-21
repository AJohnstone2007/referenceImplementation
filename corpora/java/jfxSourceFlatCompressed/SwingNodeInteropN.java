package com.sun.javafx.embed.swing.newimpl;
import com.sun.javafx.PlatformUtil;
import com.sun.javafx.embed.swing.DisposerRecord;
import com.sun.javafx.embed.swing.FXDnD;
import com.sun.javafx.embed.swing.SwingCursors;
import com.sun.javafx.embed.swing.SwingNodeHelper;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.stage.WindowHelper;
import com.sun.javafx.util.Utils;
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowFocusListener;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javax.swing.JComponent;
import jdk.swing.interop.DragSourceContextWrapper;
import jdk.swing.interop.LightweightContentWrapper;
import jdk.swing.interop.LightweightFrameWrapper;
public class SwingNodeInteropN {
private volatile LightweightFrameWrapper lwFrame;
private static OptionalMethod<LightweightFrameWrapper> jlfNotifyDisplayChanged;
private static Class lwFrameWrapperClass = null;
private static native void overrideNativeWindowHandle(Class lwFrameWrapperClass,
LightweightFrameWrapper lwFrame, long handle,
Runnable closeWindow);
static {
jlfNotifyDisplayChanged = new OptionalMethod<>(LightweightFrameWrapper.class,
"notifyDisplayChanged", Double.TYPE, Double.TYPE);
if (!jlfNotifyDisplayChanged.isSupported()) {
jlfNotifyDisplayChanged = new OptionalMethod<>(
LightweightFrameWrapper.class,"notifyDisplayChanged", Integer.TYPE);
}
try {
lwFrameWrapperClass = Class.forName("jdk.swing.interop.LightweightFrameWrapper");
} catch (Throwable t) {}
Utils.loadNativeSwingLibrary();
}
public LightweightFrameWrapper createLightweightFrame() {
lwFrame = new LightweightFrameWrapper();
return lwFrame;
}
public LightweightFrameWrapper getLightweightFrame() { return lwFrame; }
public MouseEvent createMouseEvent(Object frame,
int swingID, long swingWhen, int swingModifiers,
int relX, int relY, int absX, int absY,
int clickCount, boolean swingPopupTrigger,
int swingButton) {
LightweightFrameWrapper lwFrame = (LightweightFrameWrapper)frame;
return lwFrame.createMouseEvent(lwFrame, swingID,
swingWhen, swingModifiers, relX, relY, absX, absY,
clickCount, swingPopupTrigger, swingButton);
}
public MouseWheelEvent createMouseWheelEvent(Object frame,
int swingModifiers, int x, int y, int wheelRotation) {
LightweightFrameWrapper lwFrame = (LightweightFrameWrapper)frame;
return lwFrame.createMouseWheelEvent(lwFrame,
swingModifiers, x, y, wheelRotation);
}
public KeyEvent createKeyEvent(Object frame,
int swingID, long swingWhen,
int swingModifiers,
int swingKeyCode, char swingChar) {
LightweightFrameWrapper lwFrame = (LightweightFrameWrapper)frame;
return lwFrame.createKeyEvent(lwFrame, swingID,
swingWhen, swingModifiers, swingKeyCode,
swingChar);
}
public AWTEvent createUngrabEvent(Object frame) {
LightweightFrameWrapper lwFrame = (LightweightFrameWrapper)frame;
return lwFrame.createUngrabEvent(lwFrame);
}
public void overrideNativeWindowHandle(Object frame, long handle, Runnable closeWindow) {
LightweightFrameWrapper lwFrame = (LightweightFrameWrapper)frame;
overrideNativeWindowHandle(lwFrameWrapperClass, lwFrame, handle, closeWindow);
}
public void notifyDisplayChanged(Object frame, double scaleX, double scaleY) {
LightweightFrameWrapper lwFrame = (LightweightFrameWrapper)frame;
if (jlfNotifyDisplayChanged.isIntegerApi()) {
jlfNotifyDisplayChanged.invoke(lwFrame,
(int) Math.round(scaleX));
} else {
jlfNotifyDisplayChanged.invoke(lwFrame,
scaleX, scaleY);
}
}
public void setHostBounds(Object frame, int windowX, int windowY, int windowW, int windowH) {
LightweightFrameWrapper lwFrame = (LightweightFrameWrapper)frame;
jlfSetHostBounds.invoke(lwFrame, windowX, windowY, windowW, windowH);
}
public void setContent(Object frame, Object cnt) {
LightweightFrameWrapper lwFrame = (LightweightFrameWrapper)frame;
LightweightContentWrapper content = (LightweightContentWrapper)cnt;
lwFrame.setContent(content);
}
public void setVisible(Object frame, boolean visible) {
LightweightFrameWrapper lwFrame = (LightweightFrameWrapper)frame;
lwFrame.setVisible(visible);
}
public void setBounds(Object frame, int frameX, int frameY, int frameW, int frameH) {
LightweightFrameWrapper lwFrame = (LightweightFrameWrapper)frame;
lwFrame.setBounds(frameX, frameY, frameW, frameH);
}
public LightweightContentWrapper createSwingNodeContent(JComponent content, SwingNode node) {
return (LightweightContentWrapper)new SwingNodeContent(content, node);
}
public DisposerRecord createSwingNodeDisposer(Object frame) {
LightweightFrameWrapper lwFrame = (LightweightFrameWrapper)frame;
return new SwingNodeDisposer(lwFrame);
}
private static final class OptionalMethod<T> {
private final Method method;
private final boolean isIntegerAPI;
OptionalMethod(Class<T> cls, String name, Class<?>... args) {
Method m;
try {
m = cls.getMethod(name, args);
} catch (NoSuchMethodException ignored) {
m = null;
} catch (Throwable ex) {
throw new RuntimeException("Error when calling " + cls.getName() + ".getMethod('" + name + "').", ex);
}
method = m;
isIntegerAPI = args != null && args.length > 0 &&
args[0] == Integer.TYPE;
}
public boolean isSupported() {
return method != null;
}
public boolean isIntegerApi() {
return isIntegerAPI;
}
public Object invoke(T object, Object... args) {
if (method != null) {
try {
return method.invoke(object, args);
} catch (Throwable ex) {
throw new RuntimeException("Error when calling " + object.getClass().getName() + "." + method.getName() + "().", ex);
}
} else {
return null;
}
}
}
private static final OptionalMethod<LightweightFrameWrapper> jlfSetHostBounds =
new OptionalMethod<>(LightweightFrameWrapper.class, "setHostBounds",
Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE);
public void emulateActivation(Object frame, boolean activate) {
LightweightFrameWrapper lwFrame = (LightweightFrameWrapper)frame;
lwFrame.emulateActivation(activate);
}
public void disposeFrame(Object frame) {
LightweightFrameWrapper lwFrame = (LightweightFrameWrapper)frame;
lwFrame.dispose();
}
public void addWindowFocusListener(Object frame, WindowFocusListener l) {
LightweightFrameWrapper lwFrame = (LightweightFrameWrapper)frame;
lwFrame.addWindowFocusListener(l);
}
private static class SwingNodeDisposer implements DisposerRecord {
LightweightFrameWrapper lwFrame;
SwingNodeDisposer(LightweightFrameWrapper ref) {
this.lwFrame = ref;
}
public void dispose() {
if (lwFrame != null) {
lwFrame.dispose();
lwFrame = null;
}
}
}
private static class SwingNodeContent extends LightweightContentWrapper {
private JComponent comp;
private volatile FXDnD dnd;
private WeakReference<SwingNode> swingNodeRef;
SwingNodeContent(JComponent comp, SwingNode swingNode) {
this.comp = comp;
this.swingNodeRef = new WeakReference<SwingNode>(swingNode);
}
@Override
public JComponent getComponent() {
return comp;
}
@Override
public void paintLock() {
SwingNode swingNode = swingNodeRef.get();
if (swingNode != null) {
SwingNodeHelper.getPaintLock(swingNode).lock();
}
}
@Override
public void paintUnlock() {
SwingNode swingNode = swingNodeRef.get();
if (swingNode != null) {
SwingNodeHelper.getPaintLock(swingNode).unlock();
}
}
@Override
public void imageBufferReset(int[] data, int x, int y, int width, int height, int linestride) {
imageBufferReset(data, x, y, width, height, linestride, 1);
}
public void imageBufferReset(int[] data, int x, int y, int width, int height, int linestride, int scale) {
SwingNode swingNode = swingNodeRef.get();
if (swingNode != null) {
SwingNodeHelper.setImageBuffer(swingNode, data, x, y, width, height, linestride, scale, scale);
}
}
@Override
public void imageBufferReset(int[] data, int x, int y, int width, int height, int linestride, double scaleX, double scaleY) {
SwingNode swingNode = swingNodeRef.get();
if (swingNode != null) {
SwingNodeHelper.setImageBuffer(swingNode, data, x, y, width, height, linestride, scaleX, scaleY);
}
}
@Override
public void imageReshaped(int x, int y, int width, int height) {
SwingNode swingNode = swingNodeRef.get();
if (swingNode != null) {
SwingNodeHelper.setImageBounds(swingNode, x, y, width, height);
}
}
@Override
public void imageUpdated(int dirtyX, int dirtyY, int dirtyWidth, int dirtyHeight) {
SwingNode swingNode = swingNodeRef.get();
if (swingNode != null) {
SwingNodeHelper.repaintDirtyRegion(swingNode, dirtyX, dirtyY, dirtyWidth, dirtyHeight);
}
}
@Override
public void focusGrabbed() {
SwingNodeHelper.runOnFxThread(() -> {
if (PlatformUtil.isLinux()) return;
SwingNode swingNode = swingNodeRef.get();
if (swingNode != null) {
Scene scene = swingNode.getScene();
if (scene != null &&
scene.getWindow() != null &&
WindowHelper.getPeer(scene.getWindow()) != null) {
WindowHelper.getPeer(scene.getWindow()).grabFocus();
SwingNodeHelper.setGrabbed(swingNode, true);
}
}
});
}
@Override
public void focusUngrabbed() {
SwingNodeHelper.runOnFxThread(() -> {
SwingNode swingNode = swingNodeRef.get();
if (swingNode != null) {
Scene scene = swingNode.getScene();
SwingNodeHelper.ungrabFocus(swingNode, false);
}
});
}
@Override
public void preferredSizeChanged(final int width, final int height) {
SwingNodeHelper.runOnFxThread(() -> {
SwingNode swingNode = swingNodeRef.get();
if (swingNode != null) {
SwingNodeHelper.setSwingPrefWidth(swingNode, width);
SwingNodeHelper.setSwingPrefHeight(swingNode, height);
NodeHelper.notifyLayoutBoundsChanged(swingNode);
}
});
}
@Override
public void maximumSizeChanged(final int width, final int height) {
SwingNodeHelper.runOnFxThread(() -> {
SwingNode swingNode = swingNodeRef.get();
if (swingNode != null) {
SwingNodeHelper.setSwingMaxWidth(swingNode, width);
SwingNodeHelper.setSwingMaxHeight(swingNode, height);
NodeHelper.notifyLayoutBoundsChanged(swingNode);
}
});
}
@Override
public void minimumSizeChanged(final int width, final int height) {
SwingNodeHelper.runOnFxThread(() -> {
SwingNode swingNode = swingNodeRef.get();
if (swingNode != null) {
SwingNodeHelper.setSwingMinWidth(swingNode, width);
SwingNodeHelper.setSwingMinHeight(swingNode, height);
NodeHelper.notifyLayoutBoundsChanged(swingNode);
}
});
}
public void setCursor(Cursor cursor) {
SwingNodeHelper.runOnFxThread(() -> {
SwingNode swingNode = swingNodeRef.get();
if (swingNode != null) {
swingNode.setCursor(SwingCursors.embedCursorToCursor(cursor));
}
});
}
private void initDnD() {
synchronized (SwingNodeContent.this) {
if (this.dnd == null) {
SwingNode swingNode = swingNodeRef.get();
if (swingNode != null) {
this.dnd = new FXDnD(swingNode);
}
}
}
}
@Override
public synchronized <T extends DragGestureRecognizer> T createDragGestureRecognizer(
Class<T> abstractRecognizerClass,
DragSource ds, Component c, int srcActions,
DragGestureListener dgl)
{
initDnD();
return dnd.createDragGestureRecognizer(abstractRecognizerClass, ds, c, srcActions, dgl);
}
@Override
public DragSourceContextWrapper createDragSourceContext(DragGestureEvent dge) throws InvalidDnDOperationException
{
initDnD();
return (DragSourceContextWrapper)dnd.createDragSourceContext(dge);
}
@Override
public void addDropTarget(DropTarget dt) {
initDnD();
dnd.addDropTarget(dt);
}
@Override
public void removeDropTarget(DropTarget dt) {
initDnD();
dnd.removeDropTarget(dt);
}
}
}
