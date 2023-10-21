package com.sun.javafx.tk.quantum;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import com.sun.javafx.PlatformUtil;
import com.sun.glass.ui.Application;
import com.sun.glass.ui.Window;
import com.sun.javafx.tk.CompletionListener;
import com.sun.javafx.tk.RenderJob;
import static com.sun.javafx.logging.PulseLogger.PULSE_LOGGING_ENABLED;
import com.sun.javafx.logging.PulseLogger;
final class PaintCollector implements CompletionListener {
private static volatile PaintCollector collector;
static PaintCollector createInstance(QuantumToolkit toolkit) {
return collector = new PaintCollector(toolkit);
}
static PaintCollector getInstance() {
return collector;
}
private static final Comparator<GlassScene> DIRTY_SCENE_SORTER = (o1, o2) -> {
int i1 = o1.isSynchronous() ? 1 : 0;
int i2 = o2.isSynchronous() ? 1 : 0;
return i1 - i2;
};
private final List<GlassScene> dirtyScenes = new ArrayList<>();
private volatile CountDownLatch allWorkCompletedLatch = new CountDownLatch(0);
private volatile boolean hasDirty;
private final QuantumToolkit toolkit;
private volatile boolean needsHint;
private PaintCollector(QuantumToolkit qt) {
toolkit = qt;
}
void waitForRenderingToComplete() {
while (true) {
try {
allWorkCompletedLatch.await();
return;
} catch (InterruptedException ex) {
}
}
}
final boolean hasDirty() {
return hasDirty;
}
private final void setDirty(boolean value) {
hasDirty = value;
if (hasDirty) {
QuantumToolkit.getToolkit().requestNextPulse();
}
}
final void addDirtyScene(GlassScene scene) {
assert Thread.currentThread() == QuantumToolkit.getFxUserThread();
assert scene != null;
if (QuantumToolkit.verbose) {
System.err.println("PC.addDirtyScene: " + System.nanoTime() + scene);
}
if (!dirtyScenes.contains(scene)) {
dirtyScenes.add(scene);
setDirty(true);
}
}
final void removeDirtyScene(GlassScene scene) {
assert Thread.currentThread() == QuantumToolkit.getFxUserThread();
assert scene != null;
if (QuantumToolkit.verbose) {
System.err.println("PC.removeDirtyScene: " + scene);
}
dirtyScenes.remove(scene);
setDirty(!dirtyScenes.isEmpty());
}
final CompletionListener getRendered() {
return this;
}
@Override public void done(RenderJob job) {
assert Thread.currentThread() != QuantumToolkit.getFxUserThread();
if (!(job instanceof PaintRenderJob)) {
throw new IllegalArgumentException("PaintCollector: invalid RenderJob");
}
final PaintRenderJob paintjob = (PaintRenderJob)job;
final GlassScene scene = paintjob.getScene();
if (scene == null) {
throw new IllegalArgumentException("PaintCollector: null scene");
}
scene.frameRendered();
if (allWorkCompletedLatch.getCount() == 1) {
if (needsHint && !toolkit.hasNativeSystemVsync()) {
toolkit.vsyncHint();
}
Application.GetApplication().notifyRenderingFinished();
if (PULSE_LOGGING_ENABLED) {
PulseLogger.renderEnd();
}
}
allWorkCompletedLatch.countDown();
}
final void liveRepaintRenderJob(final ViewScene scene) {
ViewPainter viewPainter = scene.getPainter();
QuantumToolkit quantum = (QuantumToolkit)QuantumToolkit.getToolkit();
quantum.pulse(false);
final CountDownLatch latch = new CountDownLatch(1);
QuantumToolkit.runWithoutRenderLock(() -> {
quantum.addRenderJob(new RenderJob(viewPainter, rj -> latch.countDown()));
try {
latch.await();
} catch (InterruptedException e) {
}
return null;
});
}
final void renderAll() {
assert Thread.currentThread() == QuantumToolkit.getFxUserThread();
if (QuantumToolkit.pulseDebug) {
System.err.println("PC.renderAll(" + dirtyScenes.size() + "): " + System.nanoTime());
}
if (!hasDirty) {
return;
}
assert !dirtyScenes.isEmpty();
Collections.sort(dirtyScenes, DIRTY_SCENE_SORTER);
setDirty(false);
needsHint = false;
if (PULSE_LOGGING_ENABLED) {
PulseLogger.renderStart();
}
if (!Application.GetApplication().hasWindowManager()) {
final List<com.sun.glass.ui.Window> glassWindowList = com.sun.glass.ui.Window.getWindows();
allWorkCompletedLatch = new CountDownLatch(glassWindowList.size());
for (int i = 0, n = glassWindowList.size(); i < n; i++) {
final Window w = glassWindowList.get(i);
final WindowStage ws = WindowStage.findWindowStage(w);
if (ws != null) {
final ViewScene vs = ws.getViewScene();
if (dirtyScenes.indexOf(vs) != -1) {
if (!needsHint) {
needsHint = vs.isSynchronous();
}
}
if (!PlatformUtil.useEGL() || i == (n - 1)) {
vs.setDoPresent(true);
} else {
vs.setDoPresent(false);
}
try {
vs.repaint();
} catch (Throwable t) {
t.printStackTrace();
}
}
}
} else {
allWorkCompletedLatch = new CountDownLatch(dirtyScenes.size());
for (final GlassScene gs : dirtyScenes) {
if (!needsHint) {
needsHint = gs.isSynchronous();
}
gs.setDoPresent(true);
try {
gs.repaint();
} catch (Throwable t) {
t.printStackTrace();
}
}
}
dirtyScenes.clear();
if (toolkit.shouldWaitForRenderingToComplete()) {
waitForRenderingToComplete();
}
}
}
