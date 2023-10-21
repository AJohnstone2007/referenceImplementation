package com.sun.media.jfxmediaimpl;
import java.lang.annotation.Native;
import com.sun.media.jfxmedia.Media;
import com.sun.media.jfxmedia.MediaError;
import com.sun.media.jfxmedia.MediaException;
import com.sun.media.jfxmedia.MediaPlayer;
import com.sun.media.jfxmedia.control.VideoRenderControl;
import com.sun.media.jfxmedia.effects.AudioEqualizer;
import com.sun.media.jfxmedia.effects.AudioSpectrum;
import com.sun.media.jfxmedia.events.AudioSpectrumEvent;
import com.sun.media.jfxmedia.events.AudioSpectrumListener;
import com.sun.media.jfxmedia.events.BufferListener;
import com.sun.media.jfxmedia.events.BufferProgressEvent;
import com.sun.media.jfxmedia.events.MarkerEvent;
import com.sun.media.jfxmedia.events.MarkerListener;
import com.sun.media.jfxmedia.events.MediaErrorListener;
import com.sun.media.jfxmedia.events.NewFrameEvent;
import com.sun.media.jfxmedia.events.PlayerEvent;
import com.sun.media.jfxmedia.events.PlayerStateEvent;
import com.sun.media.jfxmedia.events.PlayerStateEvent.PlayerState;
import com.sun.media.jfxmedia.events.PlayerStateListener;
import com.sun.media.jfxmedia.events.PlayerTimeListener;
import com.sun.media.jfxmedia.events.VideoFrameRateListener;
import com.sun.media.jfxmedia.events.VideoRendererListener;
import com.sun.media.jfxmedia.events.VideoTrackSizeListener;
import com.sun.media.jfxmedia.logging.Logger;
import com.sun.media.jfxmedia.track.AudioTrack;
import com.sun.media.jfxmedia.track.SubtitleTrack;
import com.sun.media.jfxmedia.track.Track;
import com.sun.media.jfxmedia.track.Track.Encoding;
import com.sun.media.jfxmedia.track.VideoResolution;
import com.sun.media.jfxmedia.track.VideoTrack;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
public abstract class NativeMediaPlayer implements MediaPlayer, MarkerStateListener {
@Native public final static int eventPlayerUnknown = 100;
@Native public final static int eventPlayerReady = 101;
@Native public final static int eventPlayerPlaying = 102;
@Native public final static int eventPlayerPaused = 103;
@Native public final static int eventPlayerStopped = 104;
@Native public final static int eventPlayerStalled = 105;
@Native public final static int eventPlayerFinished = 106;
@Native public final static int eventPlayerError = 107;
@Native private static final int NOMINAL_VIDEO_FPS = 30;
@Native public static final long ONE_SECOND = 1000000000L;
private NativeMedia media;
private VideoRenderControl videoRenderControl;
private final List<WeakReference<MediaErrorListener>> errorListeners = new ArrayList<>();
private final List<WeakReference<PlayerStateListener>> playerStateListeners = new ArrayList<>();
private final List<WeakReference<PlayerTimeListener>> playerTimeListeners = new ArrayList<>();
private final List<WeakReference<VideoTrackSizeListener>> videoTrackSizeListeners = new ArrayList<>();
private final List<WeakReference<VideoRendererListener>> videoUpdateListeners = new ArrayList<>();
private final List<WeakReference<VideoFrameRateListener>> videoFrameRateListeners = new ArrayList<>();
private final List<WeakReference<MarkerListener>> markerListeners = new ArrayList<>();
private final List<WeakReference<BufferListener>> bufferListeners = new ArrayList<>();
private final List<WeakReference<AudioSpectrumListener>> audioSpectrumListeners = new ArrayList<>();
private final List<PlayerStateEvent> cachedStateEvents = new ArrayList<>();
private final List<PlayerTimeEvent> cachedTimeEvents = new ArrayList<>();
private final List<BufferProgressEvent> cachedBufferEvents = new ArrayList<>();
private final List<MediaErrorEvent> cachedErrorEvents = new ArrayList<>();
private boolean isFirstFrame = true;
private NewFrameEvent firstFrameEvent = null;
private double firstFrameTime;
private final Object firstFrameLock = new Object();
private EventQueueThread eventLoop = new EventQueueThread();
private int frameWidth = -1;
private int frameHeight = -1;
private final AtomicBoolean isMediaPulseEnabled = new AtomicBoolean(false);
private final Lock mediaPulseLock = new ReentrantLock();
private Timer mediaPulseTimer;
private final Lock markerLock = new ReentrantLock();
private boolean checkSeek = false;
private double timeBeforeSeek = 0.0;
private double timeAfterSeek = 0.0;
private double previousTime = 0.0;
private double firedMarkerTime = -1.0;
private double startTime = 0.0;
private double stopTime = Double.POSITIVE_INFINITY;
private boolean isStartTimeUpdated = false;
private boolean isStopTimeSet = false;
private double encodedFrameRate = 0.0;
private boolean recomputeFrameRate = true;
private double previousFrameTime;
private long numFramesSincePlaying;
private double meanFrameDuration;
private double decodedFrameRate;
private PlayerState playerState = PlayerState.UNKNOWN;
private final Lock disposeLock = new ReentrantLock();
private boolean isDisposed = false;
private Runnable onDispose;
protected NativeMediaPlayer(NativeMedia clip) {
if (clip == null) {
throw new IllegalArgumentException("clip == null!");
}
media = clip;
videoRenderControl = new VideoRenderer();
}
protected void init() {
media.addMarkerStateListener(this);
eventLoop.start();
}
void setOnDispose(Runnable onDispose) {
disposeLock.lock();
try {
if (!isDisposed) {
this.onDispose = onDispose;
}
} finally {
disposeLock.unlock();
}
}
private static class WarningEvent extends PlayerEvent {
private final Object source;
private final String message;
WarningEvent(Object source, String message) {
this.source = source;
this.message = message;
}
public Object getSource() {
return source;
}
public String getMessage() {
return message;
}
}
public static class MediaErrorEvent extends PlayerEvent {
private final Object source;
private final MediaError error;
public MediaErrorEvent(Object source, MediaError error) {
this.source = source;
this.error = error;
}
public Object getSource() {
return source;
}
public String getMessage() {
return error.description();
}
public int getErrorCode() {
return error.code();
}
}
private static class PlayerTimeEvent extends PlayerEvent {
private final double time;
public PlayerTimeEvent(double time) {
this.time = time;
}
public double getTime() {
return time;
}
}
private static class TrackEvent extends PlayerEvent {
private final Track track;
TrackEvent(Track track) {
this.track = track;
}
public Track getTrack() {
return this.track;
}
}
private static class FrameSizeChangedEvent extends PlayerEvent {
private final int width;
private final int height;
public FrameSizeChangedEvent(int width, int height) {
if (width > 0) {
this.width = width;
} else {
this.width = 0;
}
if (height > 0) {
this.height = height;
} else {
this.height = 0;
}
}
public int getWidth() {
return width;
}
public int getHeight() {
return height;
}
}
private class VideoRenderer implements VideoRenderControl {
@Override
public void addVideoRendererListener(VideoRendererListener listener) {
if (listener != null) {
synchronized (firstFrameLock) {
if (firstFrameEvent != null) {
listener.videoFrameUpdated(firstFrameEvent);
}
}
videoUpdateListeners.add(new WeakReference<>(listener));
}
}
@Override
public void removeVideoRendererListener(VideoRendererListener listener) {
if (listener != null) {
for (ListIterator<WeakReference<VideoRendererListener>> it = videoUpdateListeners.listIterator(); it.hasNext();) {
VideoRendererListener l = it.next().get();
if (l == null || l == listener) {
it.remove();
}
}
}
}
@Override
public void addVideoFrameRateListener(VideoFrameRateListener listener) {
if (listener != null) {
videoFrameRateListeners.add(new WeakReference<>(listener));
}
}
@Override
public void removeVideoFrameRateListener(VideoFrameRateListener listener) {
if (listener != null) {
for (ListIterator<WeakReference<VideoFrameRateListener>> it = videoFrameRateListeners.listIterator(); it.hasNext();) {
VideoFrameRateListener l = it.next().get();
if (l == null || l == listener) {
it.remove();
}
}
}
}
@Override
public int getFrameWidth() {
return frameWidth;
}
@Override
public int getFrameHeight() {
return frameHeight;
}
}
private class EventQueueThread extends Thread {
private final BlockingQueue<PlayerEvent> eventQueue =
new LinkedBlockingQueue<>();
private volatile boolean stopped = false;
EventQueueThread() {
setName("JFXMedia Player EventQueueThread");
setDaemon(true);
}
@Override
public void run() {
while (!stopped) {
try {
PlayerEvent evt = eventQueue.take();
if (!stopped) {
if (evt instanceof NewFrameEvent) {
try {
HandleRendererEvents((NewFrameEvent) evt);
} catch (Throwable t) {
if (Logger.canLog(Logger.ERROR)) {
Logger.logMsg(Logger.ERROR, "Caught exception in HandleRendererEvents: " + t.toString());
}
}
} else if (evt instanceof PlayerStateEvent) {
HandleStateEvents((PlayerStateEvent) evt);
} else if (evt instanceof FrameSizeChangedEvent) {
HandleFrameSizeChangedEvents((FrameSizeChangedEvent) evt);
} else if (evt instanceof TrackEvent) {
HandleTrackEvents((TrackEvent) evt);
} else if (evt instanceof MarkerEvent) {
HandleMarkerEvents((MarkerEvent) evt);
} else if (evt instanceof WarningEvent) {
HandleWarningEvents((WarningEvent) evt);
} else if (evt instanceof PlayerTimeEvent) {
HandlePlayerTimeEvents((PlayerTimeEvent) evt);
} else if (evt instanceof BufferProgressEvent) {
HandleBufferEvents((BufferProgressEvent) evt);
} else if (evt instanceof AudioSpectrumEvent) {
HandleAudioSpectrumEvents((AudioSpectrumEvent) evt);
} else if (evt instanceof MediaErrorEvent) {
HandleErrorEvents((MediaErrorEvent) evt);
}
}
} catch (Exception e) {
}
}
eventQueue.clear();
}
private void HandleRendererEvents(NewFrameEvent evt) {
if (isFirstFrame) {
isFirstFrame = false;
synchronized (firstFrameLock) {
firstFrameEvent = evt;
firstFrameTime = firstFrameEvent.getFrameData().getTimestamp();
firstFrameEvent.getFrameData().holdFrame();
}
} else if (firstFrameEvent != null
&& firstFrameTime != evt.getFrameData().getTimestamp()) {
synchronized (firstFrameLock) {
firstFrameEvent.getFrameData().releaseFrame();
firstFrameEvent = null;
}
}
for (ListIterator<WeakReference<VideoRendererListener>> it = videoUpdateListeners.listIterator(); it.hasNext();) {
VideoRendererListener l = it.next().get();
if (l != null) {
l.videoFrameUpdated(evt);
} else {
it.remove();
}
}
evt.getFrameData().releaseFrame();
if (!videoFrameRateListeners.isEmpty()) {
double currentFrameTime = System.nanoTime() / (double) ONE_SECOND;
if (recomputeFrameRate) {
recomputeFrameRate = false;
previousFrameTime = currentFrameTime;
numFramesSincePlaying = 1;
} else {
boolean fireFrameRateEvent = false;
if (numFramesSincePlaying == 1) {
meanFrameDuration = currentFrameTime - previousFrameTime;
if (meanFrameDuration > 0.0) {
decodedFrameRate = 1.0 / meanFrameDuration;
fireFrameRateEvent = true;
}
} else {
double previousMeanFrameDuration = meanFrameDuration;
int movingAverageLength = encodedFrameRate != 0.0
? ((int) (encodedFrameRate + 0.5)) : NOMINAL_VIDEO_FPS;
long numFrames = numFramesSincePlaying < movingAverageLength
? numFramesSincePlaying : movingAverageLength;
meanFrameDuration = ((numFrames - 1) * previousMeanFrameDuration
+ currentFrameTime - previousFrameTime) / numFrames;
if (meanFrameDuration > 0.0
&& Math.abs(decodedFrameRate - 1.0 / meanFrameDuration) > 0.5) {
decodedFrameRate = 1.0 / meanFrameDuration;
fireFrameRateEvent = true;
}
}
if (fireFrameRateEvent) {
for (ListIterator<WeakReference<VideoFrameRateListener>> it = videoFrameRateListeners.listIterator(); it.hasNext();) {
VideoFrameRateListener l = it.next().get();
if (l != null) {
l.onFrameRateChanged(decodedFrameRate);
} else {
it.remove();
}
}
}
previousFrameTime = currentFrameTime;
numFramesSincePlaying++;
}
}
}
private void HandleStateEvents(PlayerStateEvent evt) {
playerState = evt.getState();
recomputeFrameRate = PlayerState.PLAYING == evt.getState();
switch (playerState) {
case READY:
onNativeInit();
sendFakeBufferProgressEvent();
break;
case PLAYING:
isMediaPulseEnabled.set(true);
break;
case STOPPED:
case FINISHED:
doMediaPulseTask();
case PAUSED:
case STALLED:
case HALTED:
isMediaPulseEnabled.set(false);
break;
default:
break;
}
synchronized (cachedStateEvents) {
if (playerStateListeners.isEmpty()) {
cachedStateEvents.add(evt);
return;
}
}
for (ListIterator<WeakReference<PlayerStateListener>> it = playerStateListeners.listIterator(); it.hasNext();) {
PlayerStateListener listener = it.next().get();
if (listener != null) {
switch (playerState) {
case READY:
onNativeInit();
sendFakeBufferProgressEvent();
listener.onReady(evt);
break;
case PLAYING:
listener.onPlaying(evt);
break;
case PAUSED:
listener.onPause(evt);
break;
case STOPPED:
listener.onStop(evt);
break;
case STALLED:
listener.onStall(evt);
break;
case FINISHED:
listener.onFinish(evt);
break;
case HALTED:
listener.onHalt(evt);
break;
default:
break;
}
} else {
it.remove();
}
}
}
private void HandlePlayerTimeEvents(PlayerTimeEvent evt) {
synchronized (cachedTimeEvents) {
if (playerTimeListeners.isEmpty()) {
cachedTimeEvents.add(evt);
return;
}
}
for (ListIterator<WeakReference<PlayerTimeListener>> it = playerTimeListeners.listIterator(); it.hasNext();) {
PlayerTimeListener listener = it.next().get();
if (listener != null) {
listener.onDurationChanged(evt.getTime());
} else {
it.remove();
}
}
}
private void HandleFrameSizeChangedEvents(FrameSizeChangedEvent evt) {
frameWidth = evt.getWidth();
frameHeight = evt.getHeight();
Logger.logMsg(Logger.DEBUG, "** Frame size changed (" + frameWidth + ", " + frameHeight + ")");
for (ListIterator<WeakReference<VideoTrackSizeListener>> it = videoTrackSizeListeners.listIterator(); it.hasNext();) {
VideoTrackSizeListener listener = it.next().get();
if (listener != null) {
listener.onSizeChanged(frameWidth, frameHeight);
} else {
it.remove();
}
}
}
private void HandleTrackEvents(TrackEvent evt) {
media.addTrack(evt.getTrack());
if (evt.getTrack() instanceof VideoTrack) {
encodedFrameRate = ((VideoTrack) evt.getTrack()).getEncodedFrameRate();
}
}
private void HandleMarkerEvents(MarkerEvent evt) {
for (ListIterator<WeakReference<MarkerListener>> it = markerListeners.listIterator(); it.hasNext();) {
MarkerListener listener = it.next().get();
if (listener != null) {
listener.onMarker(evt);
} else {
it.remove();
}
}
}
private void HandleWarningEvents(WarningEvent evt) {
Logger.logMsg(Logger.WARNING, evt.getSource() + evt.getMessage());
}
private void HandleErrorEvents(MediaErrorEvent evt) {
Logger.logMsg(Logger.ERROR, evt.getMessage());
synchronized (cachedErrorEvents) {
if (errorListeners.isEmpty()) {
cachedErrorEvents.add(evt);
return;
}
}
for (ListIterator<WeakReference<MediaErrorListener>> it = errorListeners.listIterator(); it.hasNext();) {
MediaErrorListener l = it.next().get();
if (l != null) {
l.onError(evt.getSource(), evt.getErrorCode(), evt.getMessage());
} else {
it.remove();
}
}
}
private void HandleBufferEvents(BufferProgressEvent evt) {
synchronized (cachedBufferEvents) {
if (bufferListeners.isEmpty()) {
cachedBufferEvents.add(evt);
return;
}
}
for (ListIterator<WeakReference<BufferListener>> it = bufferListeners.listIterator(); it.hasNext();) {
BufferListener listener = it.next().get();
if (listener != null) {
listener.onBufferProgress(evt);
} else {
it.remove();
}
}
}
private void HandleAudioSpectrumEvents(AudioSpectrumEvent evt) {
for (ListIterator<WeakReference<AudioSpectrumListener>> it = audioSpectrumListeners.listIterator(); it.hasNext();) {
AudioSpectrumListener listener = it.next().get();
if (listener != null) {
if (evt.queryTimestamp()) {
double timestamp = playerGetPresentationTime();
evt.setTimestamp(timestamp);
}
listener.onAudioSpectrumEvent(evt);
} else {
it.remove();
}
}
}
public void postEvent(PlayerEvent event) {
if (eventQueue != null) {
eventQueue.offer(event);
}
}
public void terminateLoop() {
stopped = true;
try {
eventQueue.put(new PlayerEvent());
} catch(InterruptedException ex) {}
}
private void sendFakeBufferProgressEvent() {
String contentType = media.getLocator().getContentType();
String protocol = media.getLocator().getProtocol();
if ((contentType != null && (contentType.equals(MediaUtils.CONTENT_TYPE_M3U) || contentType.equals(MediaUtils.CONTENT_TYPE_M3U8)))
|| (protocol != null && !protocol.equals("http") && !protocol.equals("https"))) {
HandleBufferEvents(new BufferProgressEvent(getDuration(), 0, 1, 1));
}
}
}
private synchronized void onNativeInit() {
try {
playerInit();
} catch (MediaException me) {
sendPlayerMediaErrorEvent(me.getMediaError().code());
}
}
@Override
public void addMediaErrorListener(MediaErrorListener listener) {
if (listener != null) {
this.errorListeners.add(new WeakReference<>(listener));
synchronized (cachedErrorEvents) {
if (!cachedErrorEvents.isEmpty() && !errorListeners.isEmpty()) {
cachedErrorEvents.stream().forEach((evt) -> {
sendPlayerEvent(evt);
});
cachedErrorEvents.clear();
}
}
}
}
@Override
public void removeMediaErrorListener(MediaErrorListener listener) {
if (listener != null) {
for (ListIterator<WeakReference<MediaErrorListener>> it = errorListeners.listIterator(); it.hasNext();) {
MediaErrorListener l = it.next().get();
if (l == null || l == listener) {
it.remove();
}
}
}
}
@Override
public void addMediaPlayerListener(PlayerStateListener listener) {
if (listener != null) {
synchronized (cachedStateEvents) {
if (!cachedStateEvents.isEmpty() && playerStateListeners.isEmpty()) {
Iterator<PlayerStateEvent> events = cachedStateEvents.iterator();
while (events.hasNext()) {
PlayerStateEvent evt = events.next();
switch (evt.getState()) {
case READY:
listener.onReady(evt);
break;
case PLAYING:
listener.onPlaying(evt);
break;
case PAUSED:
listener.onPause(evt);
break;
case STOPPED:
listener.onStop(evt);
break;
case STALLED:
listener.onStall(evt);
break;
case FINISHED:
listener.onFinish(evt);
break;
case HALTED:
listener.onHalt(evt);
break;
default:
break;
}
}
cachedStateEvents.clear();
}
playerStateListeners.add(new WeakReference(listener));
}
}
}
@Override
public void removeMediaPlayerListener(PlayerStateListener listener) {
if (listener != null) {
for (ListIterator<WeakReference<PlayerStateListener>> it = playerStateListeners.listIterator(); it.hasNext();) {
PlayerStateListener l = it.next().get();
if (l == null || l == listener) {
it.remove();
}
}
}
}
@Override
public void addMediaTimeListener(PlayerTimeListener listener) {
if (listener != null) {
synchronized (cachedTimeEvents) {
if (!cachedTimeEvents.isEmpty() && playerTimeListeners.isEmpty()) {
Iterator<PlayerTimeEvent> events = cachedTimeEvents.iterator();
while (events.hasNext()) {
PlayerTimeEvent evt = events.next();
listener.onDurationChanged(evt.getTime());
}
cachedTimeEvents.clear();
} else {
double duration = getDuration();
if (duration != Double.POSITIVE_INFINITY) {
listener.onDurationChanged(duration);
}
}
playerTimeListeners.add(new WeakReference(listener));
}
}
}
@Override
public void removeMediaTimeListener(PlayerTimeListener listener) {
if (listener != null) {
for (ListIterator<WeakReference<PlayerTimeListener>> it = playerTimeListeners.listIterator(); it.hasNext();) {
PlayerTimeListener l = it.next().get();
if (l == null || l == listener) {
it.remove();
}
}
}
}
@Override
public void addVideoTrackSizeListener(VideoTrackSizeListener listener) {
if (listener != null) {
if (frameWidth != -1 && frameHeight != -1) {
listener.onSizeChanged(frameWidth, frameHeight);
}
videoTrackSizeListeners.add(new WeakReference(listener));
}
}
@Override
public void removeVideoTrackSizeListener(VideoTrackSizeListener listener) {
if (listener != null) {
for (ListIterator<WeakReference<VideoTrackSizeListener>> it = videoTrackSizeListeners.listIterator(); it.hasNext();) {
VideoTrackSizeListener l = it.next().get();
if (l == null || l == listener) {
it.remove();
}
}
}
}
@Override
public void addMarkerListener(MarkerListener listener) {
if (listener != null) {
markerListeners.add(new WeakReference(listener));
}
}
@Override
public void removeMarkerListener(MarkerListener listener) {
if (listener != null) {
for (ListIterator<WeakReference<MarkerListener>> it = markerListeners.listIterator(); it.hasNext();) {
MarkerListener l = it.next().get();
if (l == null || l == listener) {
it.remove();
}
}
}
}
@Override
public void addBufferListener(BufferListener listener) {
if (listener != null) {
synchronized (cachedBufferEvents) {
if (!cachedBufferEvents.isEmpty() && bufferListeners.isEmpty()) {
cachedBufferEvents.stream().forEach((evt) -> {
listener.onBufferProgress(evt);
});
cachedBufferEvents.clear();
}
bufferListeners.add(new WeakReference(listener));
}
}
}
@Override
public void removeBufferListener(BufferListener listener) {
if (listener != null) {
for (ListIterator<WeakReference<BufferListener>> it = bufferListeners.listIterator(); it.hasNext();) {
BufferListener l = it.next().get();
if (l == null || l == listener) {
it.remove();
}
}
}
}
@Override
public void addAudioSpectrumListener(AudioSpectrumListener listener) {
if (listener != null) {
audioSpectrumListeners.add(new WeakReference(listener));
}
}
@Override
public void removeAudioSpectrumListener(AudioSpectrumListener listener) {
if (listener != null) {
for (ListIterator<WeakReference<AudioSpectrumListener>> it = audioSpectrumListeners.listIterator(); it.hasNext();) {
AudioSpectrumListener l = it.next().get();
if (l == null || l == listener) {
it.remove();
}
}
}
}
@Override
public VideoRenderControl getVideoRenderControl() {
return videoRenderControl;
}
@Override
public Media getMedia() {
return media;
}
@Override
public void setAudioSyncDelay(long delay) {
try {
playerSetAudioSyncDelay(delay);
} catch (MediaException me) {
sendPlayerEvent(new MediaErrorEvent(this, me.getMediaError()));
}
}
@Override
public long getAudioSyncDelay() {
try {
return playerGetAudioSyncDelay();
} catch (MediaException me) {
sendPlayerEvent(new MediaErrorEvent(this, me.getMediaError()));
}
return 0;
}
@Override
public void play() {
try {
if (isStartTimeUpdated) {
playerSeek(startTime);
}
isMediaPulseEnabled.set(true);
playerPlay();
} catch (MediaException me) {
sendPlayerEvent(new MediaErrorEvent(this, me.getMediaError()));
}
}
@Override
public void stop() {
try {
playerStop();
playerSeek(startTime);
} catch (MediaException me) {
MediaUtils.warning(this, "stop() failed!");
}
}
@Override
public void pause() {
try {
playerPause();
} catch (MediaException me) {
sendPlayerEvent(new MediaErrorEvent(this, me.getMediaError()));
}
}
@Override
public float getRate() {
try {
return playerGetRate();
} catch (MediaException me) {
sendPlayerEvent(new MediaErrorEvent(this, me.getMediaError()));
}
return 0;
}
@Override
public void setRate(float rate) {
try {
playerSetRate(rate);
} catch (MediaException me) {
MediaUtils.warning(this, "setRate(" + rate + ") failed!");
}
}
@Override
public double getPresentationTime() {
try {
return playerGetPresentationTime();
} catch (MediaException me) {
}
return -1.0;
}
@Override
public float getVolume() {
try {
return playerGetVolume();
} catch (MediaException me) {
sendPlayerEvent(new MediaErrorEvent(this, me.getMediaError()));
}
return 0;
}
@Override
public void setVolume(float vol) {
if (vol < 0.0F) {
vol = 0.0F;
} else if (vol > 1.0F) {
vol = 1.0F;
}
try {
playerSetVolume(vol);
} catch (MediaException me) {
sendPlayerEvent(new MediaErrorEvent(this, me.getMediaError()));
}
}
@Override
public boolean getMute() {
try {
return playerGetMute();
} catch (MediaException me) {
sendPlayerEvent(new MediaErrorEvent(this, me.getMediaError()));
}
return false;
}
@Override
public void setMute(boolean enable) {
try {
playerSetMute(enable);
} catch (MediaException me) {
sendPlayerEvent(new MediaErrorEvent(this, me.getMediaError()));
}
}
@Override
public float getBalance() {
try {
return playerGetBalance();
} catch (MediaException me) {
sendPlayerEvent(new MediaErrorEvent(this, me.getMediaError()));
}
return 0;
}
@Override
public void setBalance(float bal) {
if (bal < -1.0F) {
bal = -1.0F;
} else if (bal > 1.0F) {
bal = 1.0F;
}
try {
playerSetBalance(bal);
} catch (MediaException me) {
sendPlayerEvent(new MediaErrorEvent(this, me.getMediaError()));
}
}
@Override
public abstract AudioEqualizer getEqualizer();
@Override
public abstract AudioSpectrum getAudioSpectrum();
@Override
public double getDuration() {
try {
return playerGetDuration();
} catch (MediaException me) {
}
return Double.POSITIVE_INFINITY;
}
@Override
public double getStartTime() {
return startTime;
}
@Override
public void setStartTime(double startTime) {
try {
markerLock.lock();
this.startTime = startTime;
if (playerState != PlayerState.PLAYING && playerState != PlayerState.FINISHED && playerState != PlayerState.STOPPED) {
playerSeek(startTime);
} else if (playerState == PlayerState.STOPPED) {
isStartTimeUpdated = true;
}
} finally {
markerLock.unlock();
}
}
@Override
public double getStopTime() {
return stopTime;
}
@Override
public void setStopTime(double stopTime) {
try {
markerLock.lock();
this.stopTime = stopTime;
isStopTimeSet = true;
createMediaPulse();
} finally {
markerLock.unlock();
}
}
@Override
public void seek(double streamTime) {
if (playerState == PlayerState.STOPPED) {
return;
}
if (streamTime < 0.0) {
streamTime = 0.0;
} else {
double duration = getDuration();
if (duration >= 0.0 && streamTime > duration) {
streamTime = duration;
}
}
if (!isMediaPulseEnabled.get()) {
if ((playerState == PlayerState.PLAYING
|| playerState == PlayerState.PAUSED
|| playerState == PlayerState.FINISHED)
&& getStartTime() <= streamTime && streamTime <= getStopTime()) {
isMediaPulseEnabled.set(true);
}
}
markerLock.lock();
try {
timeBeforeSeek = getPresentationTime();
timeAfterSeek = streamTime;
checkSeek = timeBeforeSeek != timeAfterSeek;
previousTime = streamTime;
firedMarkerTime = -1.0;
try {
playerSeek(streamTime);
} catch (MediaException me) {
MediaUtils.warning(this, "seek(" + streamTime + ") failed!");
}
} finally {
markerLock.unlock();
}
}
protected abstract long playerGetAudioSyncDelay() throws MediaException;
protected abstract void playerSetAudioSyncDelay(long delay) throws MediaException;
protected abstract void playerPlay() throws MediaException;
protected abstract void playerStop() throws MediaException;
protected abstract void playerPause() throws MediaException;
protected abstract void playerFinish() throws MediaException;
protected abstract float playerGetRate() throws MediaException;
protected abstract void playerSetRate(float rate) throws MediaException;
protected abstract double playerGetPresentationTime() throws MediaException;
protected abstract boolean playerGetMute() throws MediaException;
protected abstract void playerSetMute(boolean state) throws MediaException;
protected abstract float playerGetVolume() throws MediaException;
protected abstract void playerSetVolume(float volume) throws MediaException;
protected abstract float playerGetBalance() throws MediaException;
protected abstract void playerSetBalance(float balance) throws MediaException;
protected abstract double playerGetDuration() throws MediaException;
protected abstract void playerSeek(double streamTime) throws MediaException;
protected abstract void playerInit() throws MediaException;
protected abstract void playerDispose();
@Override
public PlayerState getState() {
return playerState;
}
@Override
final public void dispose() {
disposeLock.lock();
try {
if (!isDisposed) {
destroyMediaPulse();
if (eventLoop != null) {
eventLoop.terminateLoop();
eventLoop = null;
}
synchronized (firstFrameLock) {
if (firstFrameEvent != null) {
firstFrameEvent.getFrameData().releaseFrame();
firstFrameEvent = null;
}
}
playerDispose();
if (media != null) {
media.dispose();
media = null;
}
if (videoUpdateListeners != null) {
for (ListIterator<WeakReference<VideoRendererListener>> it = videoUpdateListeners.listIterator(); it.hasNext();) {
VideoRendererListener l = it.next().get();
if (l != null) {
l.releaseVideoFrames();
} else {
it.remove();
}
}
videoUpdateListeners.clear();
}
if (playerStateListeners != null) {
playerStateListeners.clear();
}
if (videoTrackSizeListeners != null) {
videoTrackSizeListeners.clear();
}
if (videoFrameRateListeners != null) {
videoFrameRateListeners.clear();
}
if (cachedStateEvents != null) {
cachedStateEvents.clear();
}
if (cachedTimeEvents != null) {
cachedTimeEvents.clear();
}
if (cachedBufferEvents != null) {
cachedBufferEvents.clear();
}
if (errorListeners != null) {
errorListeners.clear();
}
if (playerTimeListeners != null) {
playerTimeListeners.clear();
}
if (markerListeners != null) {
markerListeners.clear();
}
if (bufferListeners != null) {
bufferListeners.clear();
}
if (audioSpectrumListeners != null) {
audioSpectrumListeners.clear();
}
if (videoRenderControl != null) {
videoRenderControl = null;
}
if (onDispose != null) {
onDispose.run();
}
isDisposed = true;
}
} finally {
disposeLock.unlock();
}
}
@Override
public boolean isErrorEventCached() {
synchronized (cachedErrorEvents) {
if (cachedErrorEvents.isEmpty()) {
return false;
} else {
return true;
}
}
}
protected void sendWarning(int warningCode, String warningMessage) {
if (eventLoop != null) {
String message = String.format(MediaUtils.NATIVE_MEDIA_WARNING_FORMAT,
warningCode);
if (warningMessage != null) {
message += ": " + warningMessage;
}
eventLoop.postEvent(new WarningEvent(this, message));
}
}
protected void sendPlayerEvent(PlayerEvent evt) {
if (eventLoop != null) {
eventLoop.postEvent(evt);
}
}
protected void sendPlayerHaltEvent(String message, double time) {
Logger.logMsg(Logger.ERROR, message);
if (eventLoop != null) {
eventLoop.postEvent(new PlayerStateEvent(PlayerStateEvent.PlayerState.HALTED, time, message));
}
}
protected void sendPlayerMediaErrorEvent(int errorCode) {
sendPlayerEvent(new MediaErrorEvent(this, MediaError.getFromCode(errorCode)));
}
protected void sendPlayerStateEvent(int eventID, double time) {
switch (eventID) {
case eventPlayerReady:
sendPlayerEvent(new PlayerStateEvent(PlayerStateEvent.PlayerState.READY, time));
break;
case eventPlayerPlaying:
sendPlayerEvent(new PlayerStateEvent(PlayerStateEvent.PlayerState.PLAYING, time));
break;
case eventPlayerPaused:
sendPlayerEvent(new PlayerStateEvent(PlayerStateEvent.PlayerState.PAUSED, time));
break;
case eventPlayerStopped:
sendPlayerEvent(new PlayerStateEvent(PlayerStateEvent.PlayerState.STOPPED, time));
break;
case eventPlayerStalled:
sendPlayerEvent(new PlayerStateEvent(PlayerStateEvent.PlayerState.STALLED, time));
break;
case eventPlayerFinished:
sendPlayerEvent(new PlayerStateEvent(PlayerStateEvent.PlayerState.FINISHED, time));
break;
default:
break;
}
}
protected void sendNewFrameEvent(long nativeRef) {
NativeVideoBuffer newFrameData = NativeVideoBuffer.createVideoBuffer(nativeRef);
sendPlayerEvent(new NewFrameEvent(newFrameData));
}
protected void sendFrameSizeChangedEvent(int width, int height) {
sendPlayerEvent(new FrameSizeChangedEvent(width, height));
}
protected void sendAudioTrack(boolean enabled, long trackID, String name, int encoding,
String language, int numChannels,
int channelMask, float sampleRate) {
Locale locale = null;
if (!language.equals("und")) {
locale = new Locale(language);
}
Track track = new AudioTrack(enabled, trackID, name,
locale, Encoding.toEncoding(encoding),
numChannels, channelMask, sampleRate);
TrackEvent evt = new TrackEvent(track);
sendPlayerEvent(evt);
}
protected void sendVideoTrack(boolean enabled, long trackID, String name, int encoding,
int width, int height, float frameRate,
boolean hasAlphaChannel) {
Track track = new VideoTrack(enabled, trackID, name, null,
Encoding.toEncoding(encoding),
new VideoResolution(width, height), frameRate, hasAlphaChannel);
TrackEvent evt = new TrackEvent(track);
sendPlayerEvent(evt);
}
protected void sendSubtitleTrack(boolean enabled, long trackID, String name,
int encoding, String language)
{
Locale locale = null;
if (null != language) {
locale = new Locale(language);
}
Track track = new SubtitleTrack(enabled, trackID, name, locale,
Encoding.toEncoding(encoding));
sendPlayerEvent(new TrackEvent(track));
}
protected void sendMarkerEvent(String name, double time) {
sendPlayerEvent(new MarkerEvent(name, time));
}
protected void sendDurationUpdateEvent(double duration) {
sendPlayerEvent(new PlayerTimeEvent(duration));
}
protected void sendBufferProgressEvent(double clipDuration, long bufferStart, long bufferStop, long bufferPosition) {
sendPlayerEvent(new BufferProgressEvent(clipDuration, bufferStart, bufferStop, bufferPosition));
}
protected void sendAudioSpectrumEvent(double timestamp, double duration, boolean queryTimestamp) {
sendPlayerEvent(new AudioSpectrumEvent(getAudioSpectrum(), timestamp, duration, queryTimestamp));
}
@Override
public void markerStateChanged(boolean hasMarkers) {
if (hasMarkers) {
markerLock.lock();
try {
previousTime = getPresentationTime();
} finally {
markerLock.unlock();
}
createMediaPulse();
} else {
if (!isStopTimeSet) {
destroyMediaPulse();
}
}
}
private void createMediaPulse() {
mediaPulseLock.lock();
try {
if (mediaPulseTimer == null) {
mediaPulseTimer = new Timer(true);
mediaPulseTimer.scheduleAtFixedRate(new MediaPulseTask(this), 0, 40
);
}
} finally {
mediaPulseLock.unlock();
}
}
private void destroyMediaPulse() {
mediaPulseLock.lock();
try {
if (mediaPulseTimer != null) {
mediaPulseTimer.cancel();
mediaPulseTimer = null;
}
} finally {
mediaPulseLock.unlock();
}
}
boolean doMediaPulseTask() {
if (this.isMediaPulseEnabled.get()) {
disposeLock.lock();
if (isDisposed) {
disposeLock.unlock();
return false;
}
double thisTime = getPresentationTime();
markerLock.lock();
try {
if (checkSeek) {
if (timeAfterSeek > timeBeforeSeek) {
if (thisTime >= timeAfterSeek) {
checkSeek = false;
} else {
return true;
}
} else if (timeAfterSeek < timeBeforeSeek) {
if (thisTime >= timeBeforeSeek) {
return true;
} else {
checkSeek = false;
}
}
}
Map.Entry<Double, String> marker = media.getNextMarker(previousTime, true);
while (marker != null) {
double nextMarkerTime = marker.getKey();
if (nextMarkerTime > thisTime) {
break;
} else if (nextMarkerTime != firedMarkerTime
&& nextMarkerTime >= previousTime
&& nextMarkerTime >= getStartTime()
&& nextMarkerTime <= getStopTime()) {
MarkerEvent evt = new MarkerEvent(marker.getValue(), nextMarkerTime);
for (ListIterator<WeakReference<MarkerListener>> it = markerListeners.listIterator(); it.hasNext();) {
MarkerListener listener = it.next().get();
if (listener != null) {
listener.onMarker(evt);
} else {
it.remove();
}
}
firedMarkerTime = nextMarkerTime;
}
marker = media.getNextMarker(nextMarkerTime, false);
}
previousTime = thisTime;
if (isStopTimeSet && thisTime >= stopTime) {
playerFinish();
}
} finally {
disposeLock.unlock();
markerLock.unlock();
}
}
return true;
}
protected AudioEqualizer createNativeAudioEqualizer(long nativeRef) {
return new NativeAudioEqualizer(nativeRef);
}
protected AudioSpectrum createNativeAudioSpectrum(long nativeRef) {
return new NativeAudioSpectrum(nativeRef);
}
}
class MediaPulseTask extends TimerTask {
WeakReference<NativeMediaPlayer> playerRef;
MediaPulseTask(NativeMediaPlayer player) {
playerRef = new WeakReference<>(player);
}
@Override
public void run() {
final NativeMediaPlayer player = playerRef.get();
if (player != null) {
if (!player.doMediaPulseTask()) {
cancel();
}
} else {
cancel();
}
}
}
