package com.sun.prism.impl;
import com.sun.glass.ui.Application;
import com.sun.glass.ui.Pixels;
import com.sun.prism.PixelSource;
import java.lang.ref.WeakReference;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
public class QueuedPixelSource implements PixelSource {
private volatile Pixels beingConsumed;
private volatile Pixels enqueued;
private final List<WeakReference<Pixels>> saved =
new ArrayList<WeakReference<Pixels>>(3);
private final boolean useDirectBuffers;
public QueuedPixelSource(boolean useDirectBuffers) {
this.useDirectBuffers = useDirectBuffers;
}
@Override
public synchronized Pixels getLatestPixels() {
if (beingConsumed != null) {
throw new IllegalStateException("already consuming pixels: "+beingConsumed);
}
if (enqueued != null) {
beingConsumed = enqueued;
enqueued = null;
}
return beingConsumed;
}
@Override
public synchronized void doneWithPixels(Pixels used) {
if (beingConsumed != used) {
throw new IllegalStateException("wrong pixels buffer: "+used+" != "+beingConsumed);
}
beingConsumed = null;
}
@Override
public synchronized void skipLatestPixels() {
if (beingConsumed != null) {
throw new IllegalStateException("cannot skip while processing: "+beingConsumed);
}
enqueued = null;
}
private boolean usesSameBuffer(Pixels p1, Pixels p2) {
if (p1 == p2) return true;
if (p1 == null || p2 == null) return false;
return (p1.getBuffer() == p2.getBuffer());
}
public synchronized Pixels getUnusedPixels(int w, int h, float scalex, float scaley) {
int i = 0;
IntBuffer reuseBuffer = null;
while (i < saved.size()) {
WeakReference<Pixels> ref = saved.get(i);
Pixels p = ref.get();
if (p == null) {
saved.remove(i);
continue;
}
if (usesSameBuffer(p, beingConsumed) || usesSameBuffer(p, enqueued)) {
i++;
continue;
}
if (p.getWidthUnsafe() == w &&
p.getHeightUnsafe() == h &&
p.getScaleXUnsafe() == scalex &&
p.getScaleYUnsafe() == scaley)
{
return p;
}
saved.remove(i);
reuseBuffer = (IntBuffer) p.getPixels();
if (reuseBuffer.capacity() >= w * h) {
break;
}
reuseBuffer = null;
}
if (reuseBuffer == null) {
int bufsize = w * h;
if (useDirectBuffers) {
reuseBuffer = BufferUtil.newIntBuffer(bufsize);
} else {
reuseBuffer = IntBuffer.allocate(bufsize);
}
}
Pixels p = Application.GetApplication().createPixels(w, h, reuseBuffer, scalex, scaley);
saved.add(new WeakReference<>(p));
return p;
}
public synchronized void enqueuePixels(Pixels pixels) {
enqueued = pixels;
}
}
