package com.sun.javafx.embed.swing;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.tk.Toolkit;
import com.sun.javafx.util.Utils;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.scene.Node;
import javax.swing.SwingUtilities;
public class SwingNodeHelper extends NodeHelper {
private static final SwingNodeHelper theInstance;
private static SwingNodeAccessor swingNodeAccessor;
static {
theInstance = new SwingNodeHelper();
Utils.forceInit(SwingNode.class);
}
private static SwingNodeHelper getInstance() {
return theInstance;
}
public static void initHelper(SwingNode swingNode) {
setHelper(swingNode, getInstance());
}
@Override
protected NGNode createPeerImpl(Node node) {
return swingNodeAccessor.doCreatePeer(node);
}
@Override
protected void updatePeerImpl(Node node) {
super.updatePeerImpl(node);
swingNodeAccessor.doUpdatePeer(node);
}
@Override
protected BaseBounds computeGeomBoundsImpl(Node node, BaseBounds bounds,
BaseTransform tx) {
return swingNodeAccessor.doComputeGeomBounds(node, bounds, tx);
}
@Override
protected boolean computeContainsImpl(Node node, double localX, double localY) {
return swingNodeAccessor.doComputeContains(node, localX, localY);
}
public static Object getLightweightFrame(SwingNode node) {
return swingNodeAccessor.getLightweightFrame(node);
}
public static ReentrantLock getPaintLock(SwingNode node) {
return swingNodeAccessor.getPaintLock(node);
}
public static void setImageBuffer(SwingNode node, final int[] data,
final int x, final int y,
final int w, final int h, final int linestride,
final double scaleX, final double scaleY) {
swingNodeAccessor.setImageBuffer(node, data, x, y, w, h,
linestride, scaleX, scaleY);
}
public static void setImageBounds(SwingNode node, final int x, final int y,
final int w, final int h) {
swingNodeAccessor.setImageBounds(node, x, y, w, h);
}
public static void repaintDirtyRegion(SwingNode node, final int dirtyX, final int dirtyY,
final int dirtyWidth, final int dirtyHeight) {
swingNodeAccessor.repaintDirtyRegion(node, dirtyX, dirtyY,
dirtyWidth, dirtyHeight);
}
public static void ungrabFocus(SwingNode node, boolean postUngrabEvent) {
swingNodeAccessor.ungrabFocus(node, postUngrabEvent);
}
public static void setSwingPrefWidth(SwingNode node, int swingPrefWidth) {
swingNodeAccessor.setSwingPrefWidth(node, swingPrefWidth);;
}
public static void setSwingPrefHeight(SwingNode node, int swingPrefHeight) {
swingNodeAccessor.setSwingPrefHeight(node, swingPrefHeight);
}
public static void setSwingMaxWidth(SwingNode node, int swingMaxWidth) {
swingNodeAccessor.setSwingMaxWidth(node, swingMaxWidth);
}
public static void setSwingMaxHeight(SwingNode node, int swingMaxHeight) {
swingNodeAccessor.setSwingMaxHeight(node, swingMaxHeight);
}
public static void setSwingMinWidth(SwingNode node, int swingMinWidth) {
swingNodeAccessor.setSwingMinWidth(node, swingMinWidth);
}
public static void setSwingMinHeight(SwingNode node, int swingMinHeight) {
swingNodeAccessor.setSwingMinHeight(node, swingMinHeight);
}
public static void setGrabbed(SwingNode node, boolean grab) {
swingNodeAccessor.setGrabbed(node, grab);
}
public static void runOnFxThread(Runnable runnable) {
if (Platform.isFxApplicationThread()) {
runnable.run();
} else {
Platform.runLater(runnable);
}
}
public static void runOnEDT(final Runnable r) {
if (SwingUtilities.isEventDispatchThread()) {
r.run();
} else {
SwingUtilities.invokeLater(r);
}
}
private static final Set<Object> eventLoopKeys = new HashSet<>();
public static void runOnEDTAndWait(Object nestedLoopKey, Runnable r) {
Toolkit.getToolkit().checkFxUserThread();
if (SwingUtilities.isEventDispatchThread()) {
r.run();
} else {
eventLoopKeys.add(nestedLoopKey);
SwingUtilities.invokeLater(r);
Toolkit.getToolkit().enterNestedEventLoop(nestedLoopKey);
}
}
public static void leaveFXNestedLoop(Object nestedLoopKey) {
if (!eventLoopKeys.contains(nestedLoopKey)) return;
if (Platform.isFxApplicationThread()) {
Toolkit.getToolkit().exitNestedEventLoop(nestedLoopKey, null);
} else {
Platform.runLater(() -> {
Toolkit.getToolkit().exitNestedEventLoop(nestedLoopKey, null);
});
}
eventLoopKeys.remove(nestedLoopKey);
}
public static void setSwingNodeAccessor(final SwingNodeAccessor newAccessor) {
if (swingNodeAccessor != null) {
throw new IllegalStateException();
}
swingNodeAccessor = newAccessor;
}
public interface SwingNodeAccessor {
NGNode doCreatePeer(Node node);
void doUpdatePeer(Node node);
BaseBounds doComputeGeomBounds(Node node, BaseBounds bounds, BaseTransform tx);
boolean doComputeContains(Node node, double localX, double localY);
Object getLightweightFrame(SwingNode node);
ReentrantLock getPaintLock(SwingNode node);
void setImageBuffer(SwingNode node, final int[] data,
final int x, final int y,
final int w, final int h, final int linestride,
final double scaleX, final double scaleY);
void setImageBounds(SwingNode node, final int x, final int y,
final int w, final int h);
void repaintDirtyRegion(SwingNode node, final int dirtyX, final int dirtyY,
final int dirtyWidth, final int dirtyHeight);
void ungrabFocus(SwingNode node, boolean postUngrabEvent);
void setSwingPrefWidth(SwingNode node, int swingPrefWidth);
void setSwingPrefHeight(SwingNode node, int swingPrefHeight);
void setSwingMaxWidth(SwingNode node, int swingMaxWidth);
void setSwingMaxHeight(SwingNode node, int swingMaxHeight);
void setSwingMinWidth(SwingNode node, int swingMinWidth);
void setSwingMinHeight(SwingNode node, int swingMinHeight);
void setGrabbed(SwingNode node, boolean grab);
}
}
