package com.sun.javafx.tk.quantum;
import com.sun.javafx.embed.HostDragStartListener;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.InputMethodRequests;
import javafx.scene.input.InputMethodTextRun;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.image.PixelFormat;
import java.nio.IntBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import com.sun.javafx.cursor.CursorFrame;
import com.sun.javafx.embed.AbstractEvents;
import com.sun.javafx.embed.EmbeddedSceneDTInterface;
import com.sun.javafx.embed.EmbeddedSceneInterface;
import com.sun.javafx.embed.HostInterface;
import com.sun.javafx.scene.input.KeyCodeMap;
import com.sun.javafx.scene.traversal.Direction;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.tk.TKClipboard;
import com.sun.javafx.tk.Toolkit;
import com.sun.prism.paint.Color;
import com.sun.prism.paint.Paint;
import com.sun.glass.ui.Pixels;
import java.nio.ByteOrder;
final class EmbeddedScene extends GlassScene implements EmbeddedSceneInterface {
private HostInterface host;
private UploadingPainter painter;
private PaintRenderJob paintRenderJob;
private float renderScaleX;
private float renderScaleY;
private final EmbeddedSceneDnD embeddedDnD;
private volatile IntBuffer texBits;
private volatile int texLineStride;
private volatile float texScaleFactorX = 1.0f;
private volatile float texScaleFactorY = 1.0f;
private volatile PixelFormat<?> pixelFormat;
public EmbeddedScene(HostInterface host, boolean depthBuffer, boolean msaa) {
super(depthBuffer, msaa);
sceneState = new EmbeddedState(this);
this.host = host;
this.embeddedDnD = new EmbeddedSceneDnD(this);
PaintCollector collector = PaintCollector.getInstance();
painter = new UploadingPainter(this);
paintRenderJob = new PaintRenderJob(this, collector.getRendered(), painter);
int nativeFormat = Pixels.getNativeFormat();
ByteOrder byteorder = ByteOrder.nativeOrder();
if (nativeFormat == Pixels.Format.BYTE_BGRA_PRE &&
byteorder == ByteOrder.LITTLE_ENDIAN)
{
pixelFormat = PixelFormat.getIntArgbPreInstance();
} else if (nativeFormat == Pixels.Format.BYTE_ARGB &&
byteorder == ByteOrder.BIG_ENDIAN)
{
pixelFormat = PixelFormat.getIntArgbInstance();
}
assert pixelFormat != null;
}
@Override
public void dispose() {
assert host != null;
QuantumToolkit.runWithRenderLock(() -> {
host.setEmbeddedScene(null);
host = null;
updateSceneState();
painter = null;
paintRenderJob = null;
texBits = null;
return null;
});
super.dispose();
}
@Override
void setStage(GlassStage stage) {
super.setStage(stage);
assert host != null;
host.setEmbeddedScene(stage != null ? this : null);
}
@Override protected boolean isSynchronous() {
return false;
}
@Override public void setRoot(NGNode root) {
super.setRoot(root);
painter.setRoot(root);
}
@Override
public TKClipboard createDragboard(boolean isDragSource) {
return embeddedDnD.createDragboard(isDragSource);
}
@Override
public void enableInputMethodEvents(boolean enable) {
if (QuantumToolkit.verbose) {
System.err.println("EmbeddedScene.enableInputMethodEvents " + enable);
}
}
@Override
public void finishInputMethodComposition() {
if (QuantumToolkit.verbose) {
System.err.println("EmbeddedScene.finishInputMethodComposition");
}
}
@Override
public void setPixelScaleFactors(float scalex, float scaley) {
renderScaleX = scalex;
renderScaleY = scaley;
entireSceneNeedsRepaint();
}
public float getRenderScaleX() {
return renderScaleX;
}
public float getRenderScaleY() {
return renderScaleY;
}
@Override
public PixelFormat<?> getPixelFormat() {
return pixelFormat;
}
void uploadPixels(Pixels pixels) {
texBits = (IntBuffer)pixels.getPixels();
texLineStride = pixels.getWidthUnsafe();
texScaleFactorX = pixels.getScaleXUnsafe();
texScaleFactorY = pixels.getScaleYUnsafe();
if (host != null) {
host.repaint();
}
}
@Override
public void repaint() {
Toolkit tk = Toolkit.getToolkit();
tk.addRenderJob(paintRenderJob);
}
@Override
public boolean traverseOut(Direction dir) {
if (dir == Direction.NEXT) {
return host.traverseFocusOut(true);
} else if (dir == Direction.PREVIOUS) {
return host.traverseFocusOut(false);
}
return false;
}
@SuppressWarnings("removal")
@Override
public void setSize(final int width, final int height) {
Platform.runLater(() -> {
AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
if (sceneListener != null) {
sceneListener.changedSize(width, height);
}
return null;
}, getAccessControlContext());
});
}
@Override
public boolean getPixels(final IntBuffer dest, final int width, final int height) {
return QuantumToolkit.runWithRenderLock(() -> {
int scaledWidth = width;
int scaledHeight = height;
if (getRenderScaleX() != texScaleFactorX ||
getRenderScaleY() != texScaleFactorY ||
texBits == null)
{
return false;
}
scaledWidth = (int) Math.ceil(scaledWidth * texScaleFactorX);
scaledHeight = (int) Math.ceil(scaledHeight * texScaleFactorY);
dest.rewind();
texBits.rewind();
if (dest.capacity() != texBits.capacity()) {
int w = Math.min(scaledWidth, texLineStride);
int h = Math.min(scaledHeight, texBits.capacity() / texLineStride);
int[] linebuf = new int[w];
for (int i = 0; i < h; i++) {
texBits.position(i * texLineStride);
texBits.get(linebuf, 0, w);
dest.position(i * scaledWidth);
dest.put(linebuf);
}
return true;
}
dest.put(texBits);
return true;
});
}
@Override
protected Color getClearColor() {
if (fillPaint != null && fillPaint.getType() == Paint.Type.COLOR &&
((Color)fillPaint).getAlpha() == 0f)
{
return (Color)fillPaint;
}
return super.getClearColor();
}
@SuppressWarnings("removal")
@Override
public void mouseEvent(final int type, final int button,
final boolean primaryBtnDown, final boolean middleBtnDown, final boolean secondaryBtnDown,
final boolean backBtnDown, final boolean forwardBtnDown,
final int x, final int y, final int xAbs, final int yAbs,
final boolean shift, final boolean ctrl, final boolean alt, final boolean meta,
final boolean popupTrigger)
{
Platform.runLater(() -> {
AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
if (sceneListener == null) {
return null;
}
assert type != AbstractEvents.MOUSEEVENT_CLICKED;
EventType<MouseEvent> eventType = AbstractEvents.mouseIDToFXEventID(type);
sceneListener.mouseEvent(eventType, x, y, xAbs, yAbs,
AbstractEvents.mouseButtonToFXMouseButton(button),
popupTrigger, false,
shift, ctrl, alt, meta,
primaryBtnDown, middleBtnDown, secondaryBtnDown,
backBtnDown, forwardBtnDown
);
return null;
}, getAccessControlContext());
});
}
@SuppressWarnings("removal")
@Override
public void scrollEvent(final int type,
final double scrollX, final double scrollY,
final double totalScrollX, final double totalScrollY,
double xMultiplier, double yMultiplier,
final double x, final double y,
final double xAbs, final double yAbs,
final boolean shift, final boolean ctrl, final boolean alt, final boolean meta,
final boolean inertia) {
{
Platform.runLater(() -> {
AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
if (sceneListener == null) {
return null;
}
sceneListener.scrollEvent(AbstractEvents.scrollIDToFXEventType(type), scrollX, scrollY, totalScrollX, totalScrollY, xMultiplier, yMultiplier,
0, 0, 0, 0, 0, x, y, xAbs, yAbs, shift, ctrl, alt, meta, false, inertia);
return null;
}, getAccessControlContext());
});
}
}
@SuppressWarnings("removal")
@Override
public void inputMethodEvent(final EventType<InputMethodEvent> type,
final ObservableList<InputMethodTextRun> composed, final String committed,
final int caretPosition) {
Platform.runLater(() -> {
AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
if (sceneListener != null) {
sceneListener.inputMethodEvent(type, composed, committed, caretPosition);
}
return null;
});
});
}
@SuppressWarnings("removal")
@Override
public void menuEvent(final int x, final int y, final int xAbs, final int yAbs, final boolean isKeyboardTrigger) {
Platform.runLater(() -> {
AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
if (sceneListener != null) {
sceneListener.menuEvent(x, y, xAbs, yAbs, isKeyboardTrigger);
}
return null;
}, getAccessControlContext());
});
}
@SuppressWarnings("removal")
@Override
public void keyEvent(final int type, final int key, final char[] ch, final int modifiers) {
Platform.runLater(() -> {
AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
if (sceneListener != null) {
boolean shiftDown = (modifiers & AbstractEvents.MODIFIER_SHIFT) != 0;
boolean controlDown = (modifiers & AbstractEvents.MODIFIER_CONTROL) != 0;
boolean altDown = (modifiers & AbstractEvents.MODIFIER_ALT) != 0;
boolean metaDown = (modifiers & AbstractEvents.MODIFIER_META) != 0;
String str = new String(ch);
String text = str;
javafx.scene.input.KeyEvent keyEvent = new javafx.scene.input.KeyEvent(
AbstractEvents.keyIDToFXEventType(type),
str, text,
KeyCodeMap.valueOf(key),
shiftDown, controlDown, altDown, metaDown);
sceneListener.keyEvent(keyEvent);
}
return null;
}, getAccessControlContext());
});
}
@SuppressWarnings("removal")
@Override
public void zoomEvent(final int type, final double zoomFactor, final double totalZoomFactor,
final double x, final double y, final double screenX, final double screenY,
boolean shift, boolean ctrl, boolean alt, boolean meta, boolean inertia)
{
Platform.runLater(() -> {
AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
if (sceneListener == null) {
return null;
}
sceneListener.zoomEvent(AbstractEvents.zoomIDToFXEventType(type),
zoomFactor, totalZoomFactor,
x, y, screenX, screenY,
shift, ctrl, alt, meta, false, inertia);
return null;
}, getAccessControlContext());
});
}
@SuppressWarnings("removal")
@Override
public void rotateEvent(final int type, final double angle, final double totalAngle,
final double x, final double y, final double screenX, final double screenY,
boolean shift, boolean ctrl, boolean alt, boolean meta, boolean inertia)
{
Platform.runLater(() -> {
AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
if (sceneListener == null) {
return null;
}
sceneListener.rotateEvent(AbstractEvents.rotateIDToFXEventType(type),
angle, totalAngle,
x, y, screenX, screenY,
shift, ctrl, alt, meta, false, inertia);
return null;
}, getAccessControlContext());
});
}
@SuppressWarnings("removal")
@Override
public void swipeEvent(final int type, final double x, final double y, final double screenX, final double screenY,
boolean shift, boolean ctrl, boolean alt, boolean meta)
{
Platform.runLater(() -> {
AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
if (sceneListener == null) {
return null;
}
sceneListener.swipeEvent(AbstractEvents.swipeIDToFXEventType(type),
0, x, y, screenX, screenY,
shift, ctrl, alt, meta, false);
return null;
}, getAccessControlContext());
});
}
@Override
public void setCursor(final Object cursor) {
super.setCursor(cursor);
host.setCursor((CursorFrame) cursor);
}
@Override
public void setDragStartListener(HostDragStartListener l) {
embeddedDnD.setDragStartListener(l);
}
@Override
public EmbeddedSceneDTInterface createDropTarget() {
return embeddedDnD.createDropTarget();
}
@Override
public InputMethodRequests getInputMethodRequests() {
return inputMethodRequests;
}
}
