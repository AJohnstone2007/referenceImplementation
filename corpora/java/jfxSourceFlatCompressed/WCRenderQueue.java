package com.sun.webkit.graphics;
import java.lang.annotation.Native;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.logging.PlatformLogger.Level;
import com.sun.webkit.Invoker;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;
public abstract class WCRenderQueue extends Ref {
private final static AtomicInteger idCountObj = new AtomicInteger(0);
private final static PlatformLogger log =
PlatformLogger.getLogger(WCRenderQueue.class.getName());
@Native public final static int MAX_QUEUE_SIZE = 0x80000;
private final LinkedList<BufferData> buffers = new LinkedList<BufferData>();
private BufferData currentBuffer = new BufferData();
private final WCRectangle clip;
private int size = 0;
private final boolean opaque;
protected final WCGraphicsContext gc;
protected WCRenderQueue(WCGraphicsContext gc) {
this.clip = null;
this.opaque = false;
this.gc = gc;
}
protected WCRenderQueue(WCRectangle clip, boolean opaque) {
this.clip = clip;
this.opaque = opaque;
this.gc = null;
}
public synchronized int getSize() {
return size;
}
public synchronized void addBuffer(ByteBuffer buffer) {
if (log.isLoggable(Level.FINE) && buffers.isEmpty()) {
log.fine("'{'WCRenderQueue{0}[{1}]",
new Object[]{hashCode(), idCountObj.incrementAndGet()});
}
currentBuffer.setBuffer(buffer);
buffers.addLast(currentBuffer);
currentBuffer = new BufferData();
size += buffer.capacity();
if (size > MAX_QUEUE_SIZE && gc!=null) {
flush();
}
}
public synchronized boolean isEmpty() {
return buffers.isEmpty();
}
public synchronized void decode(WCGraphicsContext gc) {
if (gc == null || !gc.isValid()) {
log.fine("WCRenderQueue::decode : GC is " + (gc == null ? "null" : " invalid"));
return;
}
for (BufferData bdata : buffers) {
try {
GraphicsDecoder.decode(
WCGraphicsManager.getGraphicsManager(), gc, bdata);
} catch (RuntimeException e) {
e.printStackTrace(System.err);
}
}
dispose();
}
public synchronized void decode() {
if (gc == null || !gc.isValid()) {
log.fine("WCRenderQueue::decode : GC is " + (gc == null ? "null" : " invalid"));
return;
}
decode(gc);
gc.flush();
}
public synchronized void decode(int fontSmoothingType) {
if (gc == null || !gc.isValid()) {
log.fine("WCRenderQueue::decode : GC is " + (gc == null ? "null" : " invalid"));
return;
}
gc.setFontSmoothingType(fontSmoothingType);
decode();
}
protected abstract void flush();
private void fwkFlush() {
flush();
}
private void fwkAddBuffer(ByteBuffer buffer) {
addBuffer(buffer);
}
public WCRectangle getClip() {
return clip;
}
public synchronized void dispose() {
int n = buffers.size();
if (n > 0) {
int i = 0;
final Object[] arr = new Object[n];
for (BufferData bdata: buffers) {
arr[i++] = bdata.getBuffer();
}
buffers.clear();
Invoker.getInvoker().invokeOnEventThread(() -> {
twkRelease(arr);
});
size = 0;
if (log.isLoggable(Level.FINE)) {
log.fine("'}'WCRenderQueue{0}[{1}]",
new Object[]{hashCode(), idCountObj.decrementAndGet()});
}
}
}
protected abstract void disposeGraphics();
private void fwkDisposeGraphics() {
disposeGraphics();
}
private native void twkRelease(Object[] bufs);
private int refString(String str) {
return currentBuffer.addString(str);
}
private int refIntArr(int[] arr) {
return currentBuffer.addIntArray(arr);
}
private int refFloatArr(float[] arr) {
return currentBuffer.addFloatArray(arr);
}
public boolean isOpaque() {
return opaque;
}
@Override public synchronized String toString() {
return "WCRenderQueue{"
+ "clip=" + clip + ", "
+ "size=" + size + ", "
+ "opaque=" + opaque
+ "}";
}
}
final class BufferData {
private final AtomicInteger idCount = new AtomicInteger(0);
private final HashMap<Integer,String> strMap =
new HashMap<Integer,String>();
private final HashMap<Integer,int[]> intArrMap =
new HashMap<Integer,int[]>();
private final HashMap<Integer,float[]> floatArrMap =
new HashMap<Integer,float[]>();
private ByteBuffer buffer;
private int createID() {
return idCount.incrementAndGet();
}
int addIntArray(int[] a) {
int id = createID();
intArrMap.put(id, a);
return id;
}
int[] getIntArray(int id) {
return intArrMap.get(id);
}
int addFloatArray(float[] a) {
int id = createID();
floatArrMap.put(id, a);
return id;
}
float[] getFloatArray(int id) {
return floatArrMap.get(id);
}
int addString(String s) {
int id = createID();
strMap.put(id, s);
return id;
}
String getString(int id) {
return strMap.get(id);
}
ByteBuffer getBuffer() {
return buffer;
}
void setBuffer(ByteBuffer buffer) {
this.buffer = buffer;
}
}
