package com.sun.media.jfxmediaimpl;
import com.sun.media.jfxmedia.control.VideoDataBuffer;
import com.sun.media.jfxmedia.control.VideoFormat;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;
final class NativeVideoBuffer implements VideoDataBuffer {
private long nativePeer;
private final AtomicInteger holdCount;
private NativeVideoBuffer cachedBGRARep;
private static native void nativeDisposeBuffer(long handle);
private native double nativeGetTimestamp(long handle);
private native ByteBuffer nativeGetBufferForPlane(long handle, int plane);
private native int nativeGetWidth(long handle);
private native int nativeGetHeight(long handle);
private native int nativeGetEncodedWidth(long handle);
private native int nativeGetEncodedHeight(long handle);
private native int nativeGetFormat(long handle);
private native boolean nativeHasAlpha(long handle);
private native int nativeGetPlaneCount(long handle);
private native int[] nativeGetPlaneStrides(long handle);
private native long nativeConvertToFormat(long handle, int formatType);
private native void nativeSetDirty(long handle);
private static final boolean DEBUG_DISPOSED_BUFFERS = false;
private static final VideoBufferDisposer disposer = new VideoBufferDisposer();
public static NativeVideoBuffer createVideoBuffer(long nativePeer) {
NativeVideoBuffer buffer = new NativeVideoBuffer(nativePeer);
MediaDisposer.addResourceDisposer(buffer, (Long)nativePeer, disposer);
return buffer;
}
private NativeVideoBuffer(long nativePeer) {
holdCount = new AtomicInteger(1);
this.nativePeer = nativePeer;
}
@Override
public void holdFrame() {
if (0 != nativePeer) {
holdCount.incrementAndGet();
} else if (DEBUG_DISPOSED_BUFFERS) {
throw new NullPointerException("method called on disposed NativeVideoBuffer");
}
}
@Override
public void releaseFrame() {
if (0 != nativePeer) {
if (holdCount.decrementAndGet() <= 0) {
if (null != cachedBGRARep) {
cachedBGRARep.releaseFrame();
cachedBGRARep = null;
}
MediaDisposer.removeResourceDisposer((Long)nativePeer);
nativeDisposeBuffer(nativePeer);
nativePeer = 0;
}
} else if (DEBUG_DISPOSED_BUFFERS) {
throw new NullPointerException("method called on disposed NativeVideoBuffer");
}
}
@Override
public double getTimestamp() {
if (0 != nativePeer) {
return nativeGetTimestamp(nativePeer);
} else if (DEBUG_DISPOSED_BUFFERS) {
throw new NullPointerException("method called on disposed NativeVideoBuffer");
}
return 0.0;
}
@Override
public ByteBuffer getBufferForPlane(int plane) {
if (0 != nativePeer) {
ByteBuffer buffer = nativeGetBufferForPlane(nativePeer, plane);
buffer.order(java.nio.ByteOrder.nativeOrder());
return buffer;
} else if (DEBUG_DISPOSED_BUFFERS) {
throw new NullPointerException("method called on disposed NativeVideoBuffer");
}
return null;
}
@Override
public int getWidth() {
if (0 != nativePeer) {
return nativeGetWidth(nativePeer);
} else if (DEBUG_DISPOSED_BUFFERS) {
throw new NullPointerException("method called on disposed NativeVideoBuffer");
}
return 0;
}
@Override
public int getHeight() {
if (0 != nativePeer) {
return nativeGetHeight(nativePeer);
} else if (DEBUG_DISPOSED_BUFFERS) {
throw new NullPointerException("method called on disposed NativeVideoBuffer");
}
return 0;
}
@Override
public int getEncodedWidth() {
if (0 != nativePeer) {
return nativeGetEncodedWidth(nativePeer);
} else if (DEBUG_DISPOSED_BUFFERS) {
throw new NullPointerException("method called on disposed NativeVideoBuffer");
}
return 0;
}
@Override
public int getEncodedHeight() {
if (0 != nativePeer) {
return nativeGetEncodedHeight(nativePeer);
} else if (DEBUG_DISPOSED_BUFFERS) {
throw new NullPointerException("method called on disposed NativeVideoBuffer");
}
return 0;
}
@Override
public VideoFormat getFormat() {
if (0 != nativePeer) {
int formatType = nativeGetFormat(nativePeer);
return VideoFormat.formatForType(formatType);
} else if (DEBUG_DISPOSED_BUFFERS) {
throw new NullPointerException("method called on disposed NativeVideoBuffer");
}
return null;
}
@Override
public boolean hasAlpha() {
if (0 != nativePeer) {
return nativeHasAlpha(nativePeer);
} else if (DEBUG_DISPOSED_BUFFERS) {
throw new NullPointerException("method called on disposed NativeVideoBuffer");
}
return false;
}
@Override
public int getPlaneCount() {
if (0 != nativePeer) {
return nativeGetPlaneCount(nativePeer);
} else if (DEBUG_DISPOSED_BUFFERS) {
throw new NullPointerException("method called on disposed NativeVideoBuffer");
}
return 0;
}
@Override
public int getStrideForPlane(int planeIndex) {
if (0 != nativePeer) {
int[] strides = nativeGetPlaneStrides(nativePeer);
return strides[planeIndex];
} else if (DEBUG_DISPOSED_BUFFERS) {
throw new NullPointerException("method called on disposed NativeVideoBuffer");
}
return 0;
}
@Override
public int[] getPlaneStrides() {
if (0 != nativePeer) {
return nativeGetPlaneStrides(nativePeer);
} else if (DEBUG_DISPOSED_BUFFERS) {
throw new NullPointerException("method called on disposed NativeVideoBuffer");
}
return null;
}
@Override
public VideoDataBuffer convertToFormat(VideoFormat newFormat) {
if (0 != nativePeer) {
if (newFormat == VideoFormat.BGRA_PRE && null != cachedBGRARep) {
cachedBGRARep.holdFrame();
return cachedBGRARep;
}
long newFrame = nativeConvertToFormat(nativePeer, newFormat.getNativeType());
if (0 != newFrame) {
NativeVideoBuffer frame = createVideoBuffer(newFrame);
if (newFormat == VideoFormat.BGRA_PRE) {
frame.holdFrame();
cachedBGRARep = frame;
}
return frame;
} else {
throw new UnsupportedOperationException("Conversion from "+getFormat()+" to "+newFormat+" is not supported.");
}
} else if (DEBUG_DISPOSED_BUFFERS) {
throw new NullPointerException("method called on disposed NativeVideoBuffer");
}
return null;
}
@Override
public void setDirty() {
if (0 != nativePeer) {
nativeSetDirty(nativePeer);
} else if (DEBUG_DISPOSED_BUFFERS) {
throw new NullPointerException("method called on disposed NativeVideoBuffer");
}
}
private static class VideoBufferDisposer implements MediaDisposer.ResourceDisposer {
@Override
public void disposeResource(Object resource) {
if (resource instanceof Long) {
nativeDisposeBuffer(((Long)resource).longValue());
}
}
}
@Override
public String toString() {
if (DEBUG_DISPOSED_BUFFERS) {
return "[NativeVideoBuffer peer="+Long.toHexString(nativePeer)+", format="+getFormat()+", size=("+getWidth()+","+getHeight()+"), timestamp="+getTimestamp()+", retain count "+holdCount.get()+"]";
}
return "[NativeVideoBuffer peer="+Long.toHexString(nativePeer)+", format="+getFormat()+", size=("+getWidth()+","+getHeight()+"), timestamp="+getTimestamp()+"]";
}
}
