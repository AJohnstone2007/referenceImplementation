package com.sun.webkit.graphics;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.webkit.Invoker;
public abstract class WCMediaPlayer extends Ref {
protected final static PlatformLogger log = PlatformLogger.getLogger("webkit.mediaplayer");
private long nPtr;
protected WCMediaPlayer() {
}
void setNativePointer(long nativePointer) {
if (nativePointer == 0) {
throw new IllegalArgumentException("nativePointer is 0");
}
if (nPtr != 0) {
throw new IllegalStateException("nPtr is not 0");
}
this.nPtr = nativePointer;
}
protected abstract void load(String url, String userAgent);
protected abstract void cancelLoad();
protected abstract void disposePlayer();
protected abstract void prepareToPlay();
protected abstract void play();
protected abstract void pause();
protected abstract float getCurrentTime();
protected abstract void seek(float time);
protected abstract void setRate(float rate);
protected abstract void setVolume(float volume);
protected abstract void setMute(boolean mute);
protected abstract void setSize(int w, int h);
protected abstract void setPreservesPitch(boolean preserve);
protected abstract void renderCurrentFrame(WCGraphicsContext gc, int x, int y, int w, int h);
protected boolean getPreservesPitch() {
return preserve;
}
protected int getNetworkState() {
return networkState;
}
protected int getReadyState() {
return readyState;
}
protected int getPreload() {
return preload;
}
protected boolean isPaused() {
return paused;
}
protected boolean isSeeking() {
return seeking;
}
protected final static int NETWORK_STATE_EMPTY = 0;
protected final static int NETWORK_STATE_IDLE = 1;
protected final static int NETWORK_STATE_LOADING = 2;
protected final static int NETWORK_STATE_LOADED = 3;
protected final static int NETWORK_STATE_FORMAT_ERROR = 4;
protected final static int NETWORK_STATE_NETWORK_ERROR = 5;
protected final static int NETWORK_STATE_DECODE_ERROR = 6;
protected final static int READY_STATE_HAVE_NOTHING = 0;
protected final static int READY_STATE_HAVE_METADATA = 1;
protected final static int READY_STATE_HAVE_CURRENT_DATA = 2;
protected final static int READY_STATE_HAVE_FUTURE_DATA = 3;
protected final static int READY_STATE_HAVE_ENOUGH_DATA = 4;
protected final static int PRELOAD_NONE = 0;
protected final static int PRELOAD_METADATA = 1;
protected final static int PRELOAD_AUTO = 2;
private int networkState = NETWORK_STATE_EMPTY;
private int readyState = READY_STATE_HAVE_NOTHING;
private int preload = PRELOAD_AUTO;
private boolean paused = true;
private boolean seeking = false;
protected void notifyNetworkStateChanged(int networkState) {
if (this.networkState != networkState) {
this.networkState = networkState;
final int _networkState = networkState;
Invoker.getInvoker().invokeOnEventThread(() -> {
if (nPtr != 0) {
notifyNetworkStateChanged(nPtr, _networkState);
}
});
}
}
protected void notifyReadyStateChanged(int readyState) {
if (this.readyState != readyState) {
this.readyState = readyState;
final int _readyState = readyState;
Invoker.getInvoker().invokeOnEventThread(() -> {
if (nPtr != 0) {
notifyReadyStateChanged(nPtr, _readyState);
}
});
}
}
protected void notifyPaused(boolean paused) {
log.fine("notifyPaused, {0} => {1}",
new Object[]{Boolean.valueOf(this.paused), Boolean.valueOf(paused)});
if (this.paused != paused) {
this.paused = paused;
final boolean _paused = paused;
Invoker.getInvoker().invokeOnEventThread(() -> {
if (nPtr != 0) {
notifyPaused(nPtr, _paused);
}
});
}
}
protected void notifySeeking(boolean seeking, int readyState) {
log.fine("notifySeeking, {0} => {1}",
new Object[]{Boolean.valueOf(this.seeking), Boolean.valueOf(seeking)});
if (this.seeking != seeking || this.readyState != readyState) {
this.seeking = seeking;
this.readyState = readyState;
final boolean _seeking = seeking;
final int _readyState = readyState;
Invoker.getInvoker().invokeOnEventThread(() -> {
if (nPtr != 0) {
notifySeeking(nPtr, _seeking, _readyState);
}
});
}
}
protected void notifyFinished() {
Invoker.getInvoker().invokeOnEventThread(() -> {
if (nPtr != 0) {
notifyFinished(nPtr);
}
});
}
protected void notifyReady(boolean hasVideo, boolean hasAudio, float duration) {
final boolean _hasVideo = hasVideo;
final boolean _hasAudio = hasAudio;
final float _duration = duration;
Invoker.getInvoker().invokeOnEventThread(() -> {
if (nPtr != 0) {
notifyReady(nPtr, _hasVideo, _hasAudio, _duration);
}
});
}
protected void notifyDurationChanged(float newDuration) {
final float _newDuration = newDuration;
Invoker.getInvoker().invokeOnEventThread(() -> {
if (nPtr != 0) {
notifyDurationChanged(nPtr, _newDuration);
}
});
}
protected void notifySizeChanged(int width, int height) {
final int _width = width;
final int _height = height;
Invoker.getInvoker().invokeOnEventThread(() -> {
if (nPtr != 0) {
notifySizeChanged(nPtr, _width, _height);
}
});
}
private Runnable newFrameNotifier = () -> {
if (nPtr != 0) {
notifyNewFrame(nPtr);
}
};
protected void notifyNewFrame() {
Invoker.getInvoker().invokeOnEventThread(newFrameNotifier);
}
protected void notifyBufferChanged(float[] ranges, int bytesLoaded) {
final float[] _ranges = ranges;
final int _bytesLoaded = bytesLoaded;
Invoker.getInvoker().invokeOnEventThread(() -> {
if (nPtr != 0) {
notifyBufferChanged(nPtr, _ranges, _bytesLoaded);
}
});
}
private void fwkLoad(String url, String userAgent) {
log.fine("fwkLoad, url={0}, userAgent={1}", new Object[] {url, userAgent});
load(url, userAgent);
}
private void fwkCancelLoad() {
log.fine("fwkCancelLoad");
cancelLoad();
}
private void fwkPrepareToPlay() {
log.fine("fwkPrepareToPlay");
prepareToPlay();
}
private void fwkDispose() {
log.fine("fwkDispose");
nPtr = 0;
cancelLoad();
disposePlayer();
}
private void fwkPlay() {
log.fine("fwkPlay");
play();
}
private void fwkPause() {
log.fine("fwkPause");
pause();
}
private float fwkGetCurrentTime() {
float res = getCurrentTime();
log.finer("fwkGetCurrentTime(), return {0}", res);
return res;
}
private void fwkSeek(float time) {
log.fine("fwkSeek({0})", time);
seek(time);
}
private void fwkSetRate(float rate) {
log.fine("fwkSetRate({0})", rate);
setRate(rate);
}
private void fwkSetVolume(float volume) {
log.fine("fwkSetVolume({0})", volume);
setVolume(volume);
}
private void fwkSetMute(boolean mute) {
log.fine("fwkSetMute({0})", mute);
setMute(mute);
}
private void fwkSetSize(int w, int h) {
setSize(w, h);
}
private boolean preserve = true;
private void fwkSetPreservesPitch(boolean preserve) {
log.fine("setPreservesPitch({0})", preserve);
this.preserve = preserve;
setPreservesPitch(preserve);
}
private void fwkSetPreload(int preload) {
log.fine("fwkSetPreload({0})",
preload == PRELOAD_NONE ? "PRELOAD_NONE"
: preload == PRELOAD_METADATA ? "PRELOAD_METADATA"
: preload == PRELOAD_AUTO ? "PRELOAD_AUTO"
: ("INVALID VALUE: " + preload));
this.preload = preload;
}
void render(WCGraphicsContext gc, int x, int y, int w, int h) {
log.finer("render(x={0}, y={1}, w={2}, h={3}", new Object[]{x, y, w, h});
renderCurrentFrame(gc, x, y, w, h);
}
private native void notifyNetworkStateChanged(long nPtr, int networkState);
private native void notifyReadyStateChanged(long nPtr, int readyState);
private native void notifyPaused(long nPtr, boolean paused);
private native void notifySeeking(long nPtr, boolean seeking, int readyState);
private native void notifyFinished(long nPtr);
private native void notifyReady(long nPtr, boolean hasVideo, boolean hasAudio, float duration);
private native void notifyDurationChanged(long nPtr, float duration);
private native void notifySizeChanged(long nPtr, int width, int height);
private native void notifyNewFrame(long nPtr);
private native void notifyBufferChanged(long nPtr, float[] ranges, int bytesLoaded);
}
