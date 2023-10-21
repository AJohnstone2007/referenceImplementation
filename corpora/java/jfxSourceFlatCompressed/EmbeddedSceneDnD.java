package com.sun.javafx.tk.quantum;
import com.sun.glass.ui.ClipboardAssistance;
import com.sun.javafx.embed.EmbeddedSceneDSInterface;
import com.sun.javafx.embed.HostDragStartListener;
import com.sun.javafx.embed.EmbeddedSceneDTInterface;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import com.sun.javafx.tk.TKClipboard;
import com.sun.javafx.tk.Toolkit;
import javafx.application.Platform;
import javafx.scene.input.TransferMode;
final class EmbeddedSceneDnD {
private final GlassSceneDnDEventHandler dndHandler;
private HostDragStartListener dragStartListener;
private EmbeddedSceneDSInterface fxDragSource;
private EmbeddedSceneDTInterface fxDropTarget;
private Thread hostThread;
public EmbeddedSceneDnD(final GlassScene scene) {
this.dndHandler = new GlassSceneDnDEventHandler(scene);
}
private void startDrag() {
assert Platform.isFxApplicationThread();
assert fxDragSource != null;
dragStartListener.dragStarted(fxDragSource, TransferMode.COPY);
}
private void setHostThread() {
if (hostThread == null) {
hostThread = Thread.currentThread();
}
}
public boolean isHostThread() {
return (Thread.currentThread() == hostThread);
}
public void onDragSourceReleased(final EmbeddedSceneDSInterface ds) {
assert fxDragSource == ds;
fxDragSource = null;
Toolkit.getToolkit().exitNestedEventLoop(this, null);
}
public void onDropTargetReleased(final EmbeddedSceneDTInterface dt) {
fxDropTarget = null;
}
<T> T executeOnFXThread(final Callable<T> r) {
if (Platform.isFxApplicationThread()) {
try {
return r.call();
} catch (Exception z) {
}
return null;
}
final AtomicReference<T> result = new AtomicReference<>();
final CountDownLatch l = new CountDownLatch(1);
Platform.runLater(() -> {
try {
result.set(r.call());
} catch (Exception z) {
} finally {
l.countDown();
}
});
try {
l.await();
} catch (Exception z) {
}
return result.get();
}
public TKClipboard createDragboard(boolean isDragSource) {
assert Platform.isFxApplicationThread();
assert fxDragSource == null;
assert isDragSource;
ClipboardAssistance assistant = new ClipboardAssistance("DND-Embedded") {
@Override
public void flush() {
super.flush();
startDrag();
Toolkit.getToolkit().enterNestedEventLoop(EmbeddedSceneDnD.this);
}
};
fxDragSource = new EmbeddedSceneDS(this, assistant, dndHandler);
return QuantumClipboard.getDragboardInstance(assistant, isDragSource);
}
public void setDragStartListener(HostDragStartListener l) {
setHostThread();
dragStartListener = l;
}
public EmbeddedSceneDTInterface createDropTarget() {
setHostThread();
return executeOnFXThread(() -> {
assert fxDropTarget == null;
fxDropTarget = new EmbeddedSceneDT(EmbeddedSceneDnD.this, dndHandler);
return fxDropTarget;
});
}
}
