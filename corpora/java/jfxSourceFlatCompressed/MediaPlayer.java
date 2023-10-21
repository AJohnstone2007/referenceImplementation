package javafx.scene.media;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.List;
import java.util.ListIterator;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.IntegerPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.util.Duration;
import javafx.util.Pair;
import com.sun.javafx.tk.TKPulseListener;
import com.sun.javafx.tk.Toolkit;
import com.sun.media.jfxmedia.MediaManager;
import com.sun.media.jfxmedia.control.VideoDataBuffer;
import com.sun.media.jfxmedia.effects.AudioSpectrum;
import com.sun.media.jfxmedia.events.AudioSpectrumEvent;
import com.sun.media.jfxmedia.events.BufferListener;
import com.sun.media.jfxmedia.events.BufferProgressEvent;
import com.sun.media.jfxmedia.events.MarkerEvent;
import com.sun.media.jfxmedia.events.MarkerListener;
import com.sun.media.jfxmedia.events.NewFrameEvent;
import com.sun.media.jfxmedia.events.PlayerStateEvent;
import com.sun.media.jfxmedia.events.PlayerStateListener;
import com.sun.media.jfxmedia.events.PlayerTimeListener;
import com.sun.media.jfxmedia.events.VideoTrackSizeListener;
import com.sun.media.jfxmedia.locator.Locator;
import java.util.*;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.EventHandler;
public final class MediaPlayer {
public enum Status {
UNKNOWN,
READY,
PAUSED,
PLAYING,
STOPPED,
STALLED,
HALTED,
DISPOSED
};
public static final int INDEFINITE = -1;
private static final double RATE_MIN = 0.0;
private static final double RATE_MAX = 8.0;
private static final int AUDIOSPECTRUM_THRESHOLD_MAX = 0;
private static final double AUDIOSPECTRUM_INTERVAL_MIN = 0.000000001;
private static final int AUDIOSPECTRUM_NUMBANDS_MIN = 2;
private com.sun.media.jfxmedia.MediaPlayer jfxPlayer;
com.sun.media.jfxmedia.MediaPlayer retrieveJfxPlayer() {
synchronized (disposeLock) {
return jfxPlayer;
}
}
private MapChangeListener<String,Duration> markerMapListener = null;
private MarkerListener markerEventListener = null;
private PlayerStateListener stateListener = null;
private PlayerTimeListener timeListener = null;
private VideoTrackSizeListener sizeListener = null;
private com.sun.media.jfxmedia.events.MediaErrorListener errorListener = null;
private BufferListener bufferListener = null;
private com.sun.media.jfxmedia.events.AudioSpectrumListener spectrumListener = null;
private RendererListener rendererListener = null;
private boolean rateChangeRequested = false;
private boolean volumeChangeRequested = false;
private boolean balanceChangeRequested = false;
private boolean startTimeChangeRequested = false;
private boolean stopTimeChangeRequested = false;
private boolean muteChangeRequested = false;
private boolean playRequested = false;
private boolean audioSpectrumNumBandsChangeRequested = false;
private boolean audioSpectrumIntervalChangeRequested = false;
private boolean audioSpectrumThresholdChangeRequested = false;
private boolean audioSpectrumEnabledChangeRequested = false;
private MediaTimerTask mediaTimerTask = null;
private double prevTimeMs = -1.0;
private boolean isUpdateTimeEnabled = false;
private BufferProgressEvent lastBufferEvent = null;
private Duration startTimeAtStop = null;
private boolean isEOS = false;
private final Object disposeLock = new Object();
private final static int DEFAULT_SPECTRUM_BAND_COUNT = 128;
private final static double DEFAULT_SPECTRUM_INTERVAL = 0.1;
private final static int DEFAULT_SPECTRUM_THRESHOLD = -60;
private final Set<WeakReference<MediaView>> viewRefs =
new HashSet<WeakReference<MediaView>>();
private AudioEqualizer audioEqualizer;
private static double clamp(double dvalue, double dmin, double dmax) {
if (dmin != Double.MIN_VALUE && dvalue < dmin) {
return dmin;
} else if (dmax != Double.MAX_VALUE && dvalue > dmax) {
return dmax;
} else {
return dvalue;
}
}
private static int clamp(int ivalue, int imin, int imax) {
if (imin != Integer.MIN_VALUE && ivalue < imin) {
return imin;
} else if (imax != Integer.MAX_VALUE && ivalue > imax) {
return imax;
} else {
return ivalue;
}
}
public final AudioEqualizer getAudioEqualizer() {
synchronized (disposeLock) {
if (getStatus() == Status.DISPOSED) {
return null;
}
if (audioEqualizer == null) {
audioEqualizer = new AudioEqualizer();
if (jfxPlayer != null) {
audioEqualizer.setAudioEqualizer(jfxPlayer.getEqualizer());
}
audioEqualizer.setEnabled(true);
}
return audioEqualizer;
}
}
public MediaPlayer(@NamedArg("media") Media media) {
if (null == media) {
throw new NullPointerException("media == null!");
}
this.media = media;
errorListener = new _MediaErrorListener();
MediaManager.addMediaErrorListener(errorListener);
try {
Locator locator = media.retrieveJfxLocator();
if (locator.canBlock()) {
InitMediaPlayer initMediaPlayer = new InitMediaPlayer();
Thread t = new Thread(initMediaPlayer);
t.setDaemon(true);
t.start();
} else {
init();
}
} catch (com.sun.media.jfxmedia.MediaException e) {
throw MediaException.exceptionToMediaException(e);
} catch (MediaException e) {
throw e;
}
}
void registerListeners() {
synchronized (disposeLock) {
if (getStatus() == Status.DISPOSED) {
return;
}
if (jfxPlayer != null) {
MediaManager.registerMediaPlayerForDispose(this, jfxPlayer);
jfxPlayer.addMediaErrorListener(errorListener);
jfxPlayer.addMediaTimeListener(timeListener);
jfxPlayer.addVideoTrackSizeListener(sizeListener);
jfxPlayer.addBufferListener(bufferListener);
jfxPlayer.addMarkerListener(markerEventListener);
jfxPlayer.addAudioSpectrumListener(spectrumListener);
jfxPlayer.getVideoRenderControl().addVideoRendererListener(rendererListener);
jfxPlayer.addMediaPlayerListener(stateListener);
}
if (null != rendererListener) {
Toolkit.getToolkit().addStageTkPulseListener(rendererListener);
}
}
}
private void init() throws MediaException {
try {
Locator locator = media.retrieveJfxLocator();
locator.waitForReadySignal();
synchronized (disposeLock) {
if (getStatus() == Status.DISPOSED) {
return;
}
jfxPlayer = MediaManager.getPlayer(locator);
if (jfxPlayer != null) {
MediaPlayerShutdownHook.addMediaPlayer(this);
jfxPlayer.setBalance((float) getBalance());
jfxPlayer.setMute(isMute());
jfxPlayer.setVolume((float) getVolume());
sizeListener = new _VideoTrackSizeListener();
stateListener = new _PlayerStateListener();
timeListener = new _PlayerTimeListener();
bufferListener = new _BufferListener();
markerEventListener = new _MarkerListener();
spectrumListener = new _SpectrumListener();
rendererListener = new RendererListener();
}
markerMapListener = new MarkerMapChangeListener();
ObservableMap<String, Duration> markers = media.getMarkers();
markers.addListener(markerMapListener);
com.sun.media.jfxmedia.Media jfxMedia = jfxPlayer.getMedia();
for (Map.Entry<String, Duration> entry : markers.entrySet()) {
String markerName = entry.getKey();
if (markerName != null) {
Duration markerTime = entry.getValue();
if (markerTime != null) {
double msec = markerTime.toMillis();
if (msec >= 0.0) {
jfxMedia.addMarker(markerName, msec / 1000.0);
}
}
}
}
}
} catch (com.sun.media.jfxmedia.MediaException e) {
throw MediaException.exceptionToMediaException(e);
}
Platform.runLater(() -> {
registerListeners();
});
}
private class InitMediaPlayer implements Runnable {
@Override
public void run() {
try {
init();
} catch (com.sun.media.jfxmedia.MediaException e) {
handleError(MediaException.exceptionToMediaException(e));
} catch (MediaException e) {
if (media.getError() != null) {
handleError(media.getError());
} else {
handleError(e);
}
} catch (Exception e) {
handleError(new MediaException(MediaException.Type.UNKNOWN, e.getMessage()));
}
}
}
private ReadOnlyObjectWrapper<MediaException> error;
private void setError(MediaException value) {
if (getError() == null) {
errorPropertyImpl().set(value);
}
}
public final MediaException getError() {
return error == null ? null : error.get();
}
public ReadOnlyObjectProperty<MediaException> errorProperty() {
return errorPropertyImpl().getReadOnlyProperty();
}
private ReadOnlyObjectWrapper<MediaException> errorPropertyImpl() {
if (error == null) {
error = new ReadOnlyObjectWrapper<MediaException>() {
@Override
protected void invalidated() {
if (getOnError() != null) {
Platform.runLater(getOnError());
}
}
@Override
public Object getBean() {
return MediaPlayer.this;
}
@Override
public String getName() {
return "error";
}
};
}
return error;
}
private ObjectProperty<Runnable> onError;
public final void setOnError(Runnable value) {
onErrorProperty().set(value);
}
public final Runnable getOnError() {
return onError == null ? null : onError.get();
}
public ObjectProperty<Runnable> onErrorProperty() {
if (onError == null) {
onError = new ObjectPropertyBase<Runnable>() {
@Override
protected void invalidated() {
if (get() != null && getError() != null) {
Platform.runLater(get());
}
}
@Override
public Object getBean() {
return MediaPlayer.this;
}
@Override
public String getName() {
return "onError";
}
};
}
return onError;
}
private Media media;
public final Media getMedia() {
return media;
}
private BooleanProperty autoPlay;
public final void setAutoPlay(boolean value) {
autoPlayProperty().set(value);
}
public final boolean isAutoPlay() {
return autoPlay == null ? false : autoPlay.get();
}
public BooleanProperty autoPlayProperty() {
if (autoPlay == null) {
autoPlay = new BooleanPropertyBase() {
@Override
protected void invalidated() {
if (autoPlay.get()) {
play();
} else {
playRequested = false;
}
}
@Override
public Object getBean() {
return MediaPlayer.this;
}
@Override
public String getName() {
return "autoPlay";
}
};
}
return autoPlay;
}
private boolean playerReady;
public void play() {
synchronized (disposeLock) {
if (getStatus() != Status.DISPOSED) {
if (playerReady) {
jfxPlayer.play();
} else {
playRequested = true;
}
}
}
}
public void pause() {
synchronized (disposeLock) {
if (getStatus() != Status.DISPOSED) {
if (playerReady) {
jfxPlayer.pause();
} else {
playRequested = false;
}
}
}
}
public void stop() {
synchronized (disposeLock) {
if (getStatus() != Status.DISPOSED) {
if (playerReady) {
jfxPlayer.stop();
setCurrentCount(0);
destroyMediaTimer();
} else {
playRequested = false;
}
}
}
}
private DoubleProperty rate;
public final void setRate(double value) {
rateProperty().set(value);
}
public final double getRate() {
return rate == null ? 1.0 : rate.get();
}
public DoubleProperty rateProperty() {
if (rate == null) {
rate = new DoublePropertyBase(1.0) {
@Override
protected void invalidated() {
synchronized (disposeLock) {
if (getStatus() != Status.DISPOSED) {
if (playerReady) {
if (jfxPlayer.getDuration() != Double.POSITIVE_INFINITY) {
jfxPlayer.setRate((float) clamp(rate.get(), RATE_MIN, RATE_MAX));
}
} else {
rateChangeRequested = true;
}
}
}
}
@Override
public Object getBean() {
return MediaPlayer.this;
}
@Override
public String getName() {
return "rate";
}
};
}
return rate;
}
private ReadOnlyDoubleWrapper currentRate;
private void setCurrentRate(double value) {
currentRatePropertyImpl().set(value);
}
public final double getCurrentRate() {
return currentRate == null ? 0.0 : currentRate.get();
}
public ReadOnlyDoubleProperty currentRateProperty() {
return currentRatePropertyImpl().getReadOnlyProperty();
}
private ReadOnlyDoubleWrapper currentRatePropertyImpl() {
if (currentRate == null) {
currentRate = new ReadOnlyDoubleWrapper(this, "currentRate");
}
return currentRate;
}
private DoubleProperty volume;
public final void setVolume(double value) {
volumeProperty().set(value);
}
public final double getVolume() {
return volume == null ? 1.0 : volume.get();
}
public DoubleProperty volumeProperty() {
if (volume == null) {
volume = new DoublePropertyBase(1.0) {
@Override
protected void invalidated() {
synchronized (disposeLock) {
if (getStatus() != Status.DISPOSED) {
if (playerReady) {
jfxPlayer.setVolume((float) clamp(volume.get(), 0.0, 1.0));
} else {
volumeChangeRequested = true;
}
}
}
}
@Override
public Object getBean() {
return MediaPlayer.this;
}
@Override
public String getName() {
return "volume";
}
};
}
return volume;
}
private DoubleProperty balance;
public final void setBalance(double value) {
balanceProperty().set(value);
}
public final double getBalance() {
return balance == null ? 0.0F : balance.get();
}
public DoubleProperty balanceProperty() {
if (balance == null) {
balance = new DoublePropertyBase() {
@Override
protected void invalidated() {
synchronized (disposeLock) {
if (getStatus() != Status.DISPOSED) {
if (playerReady) {
jfxPlayer.setBalance((float) clamp(balance.get(), -1.0, 1.0));
} else {
balanceChangeRequested = true;
}
}
}
}
@Override
public Object getBean() {
return MediaPlayer.this;
}
@Override
public String getName() {
return "balance";
}
};
}
return balance;
}
private double[] calculateStartStopTimes(Duration startValue, Duration stopValue) {
double newStart;
if (startValue == null || startValue.lessThan(Duration.ZERO)
|| startValue.equals(Duration.UNKNOWN)) {
newStart = 0.0;
} else if (startValue.equals(Duration.INDEFINITE)) {
newStart = Double.MAX_VALUE;
} else {
newStart = startValue.toMillis() / 1000.0;
}
double newStop;
if (stopValue == null || stopValue.equals(Duration.UNKNOWN)
|| stopValue.equals(Duration.INDEFINITE)) {
newStop = Double.MAX_VALUE;
} else if (stopValue.lessThan(Duration.ZERO)) {
newStop = 0.0;
} else {
newStop = stopValue.toMillis() / 1000.0;
}
Duration mediaDuration = media.getDuration();
double duration = mediaDuration == Duration.UNKNOWN ?
Double.MAX_VALUE : mediaDuration.toMillis()/1000.0;
double actualStart = clamp(newStart, 0.0, duration);
double actualStop = clamp(newStop, 0.0, duration);
if (actualStart > actualStop) {
actualStop = actualStart;
}
return new double[] {actualStart, actualStop};
}
private void setStartStopTimes(Duration startValue, boolean isStartValueSet, Duration stopValue, boolean isStopValueSet) {
if (jfxPlayer.getDuration() == Double.POSITIVE_INFINITY) {
return;
}
double[] startStop = calculateStartStopTimes(startValue, stopValue);
if (isStartValueSet) {
jfxPlayer.setStartTime(startStop[0]);
if (getStatus() == Status.READY || getStatus() == Status.PAUSED) {
Platform.runLater(() -> {
setCurrentTime(getStartTime());
});
}
}
if (isStopValueSet) {
jfxPlayer.setStopTime(startStop[1]);
}
}
private ObjectProperty<Duration> startTime;
public final void setStartTime(Duration value) {
startTimeProperty().set(value);
}
public final Duration getStartTime() {
return startTime == null ? Duration.ZERO : startTime.get();
}
public ObjectProperty<Duration> startTimeProperty() {
if (startTime == null) {
startTime = new ObjectPropertyBase<Duration>() {
@Override
protected void invalidated() {
synchronized (disposeLock) {
if (getStatus() != Status.DISPOSED) {
if (playerReady) {
setStartStopTimes(startTime.get(), true, getStopTime(), false);
} else {
startTimeChangeRequested = true;
}
calculateCycleDuration();
}
}
}
@Override
public Object getBean() {
return MediaPlayer.this;
}
@Override
public String getName() {
return "startTime";
}
};
}
return startTime;
}
private ObjectProperty<Duration> stopTime;
public final void setStopTime (Duration value) {
stopTimeProperty().set(value);
}
public final Duration getStopTime() {
return stopTime == null ? media.getDuration() : stopTime.get();
}
public ObjectProperty<Duration> stopTimeProperty() {
if (stopTime == null) {
stopTime = new ObjectPropertyBase<Duration>() {
@Override
protected void invalidated() {
synchronized (disposeLock) {
if (getStatus() != Status.DISPOSED) {
if (playerReady) {
setStartStopTimes(getStartTime(), false, stopTime.get(), true);
} else {
stopTimeChangeRequested = true;
}
calculateCycleDuration();
}
}
}
@Override
public Object getBean() {
return MediaPlayer.this;
}
@Override
public String getName() {
return "stopTime";
}
};
}
return stopTime;
}
private ReadOnlyObjectWrapper<Duration> cycleDuration;
private void setCycleDuration(Duration value) {
cycleDurationPropertyImpl().set(value);
}
public final Duration getCycleDuration() {
return cycleDuration == null ? Duration.UNKNOWN : cycleDuration.get();
}
public ReadOnlyObjectProperty<Duration> cycleDurationProperty() {
return cycleDurationPropertyImpl().getReadOnlyProperty();
}
private ReadOnlyObjectWrapper<Duration> cycleDurationPropertyImpl() {
if (cycleDuration == null) {
cycleDuration = new ReadOnlyObjectWrapper<Duration>(this, "cycleDuration");
}
return cycleDuration;
}
private void calculateCycleDuration() {
Duration endTime;
Duration mediaDuration = media.getDuration();
if (!getStopTime().isUnknown()) {
endTime = getStopTime();
} else {
endTime = mediaDuration;
}
if (endTime.greaterThan(mediaDuration)) {
endTime = mediaDuration;
}
if (endTime.isUnknown() || getStartTime().isUnknown() || getStartTime().isIndefinite()) {
if (!getCycleDuration().isUnknown())
setCycleDuration(Duration.UNKNOWN);
}
setCycleDuration(endTime.subtract(getStartTime()));
calculateTotalDuration();
}
private ReadOnlyObjectWrapper<Duration> totalDuration;
private void setTotalDuration(Duration value) {
totalDurationPropertyImpl().set(value);
}
public final Duration getTotalDuration() {
return totalDuration == null ? Duration.UNKNOWN : totalDuration.get();
}
public ReadOnlyObjectProperty<Duration> totalDurationProperty() {
return totalDurationPropertyImpl().getReadOnlyProperty();
}
private ReadOnlyObjectWrapper<Duration> totalDurationPropertyImpl() {
if (totalDuration == null) {
totalDuration = new ReadOnlyObjectWrapper<Duration>(this, "totalDuration");
}
return totalDuration;
}
private void calculateTotalDuration() {
if (getCycleCount() == INDEFINITE) {
setTotalDuration(Duration.INDEFINITE);
} else if (getCycleDuration().isUnknown()) {
setTotalDuration(Duration.UNKNOWN);
} else {
setTotalDuration(getCycleDuration().multiply((double)getCycleCount()));
}
}
private ReadOnlyObjectWrapper<Duration> currentTime;
private void setCurrentTime(Duration value) {
currentTimePropertyImpl().set(value);
}
public final Duration getCurrentTime() {
synchronized (disposeLock) {
if (getStatus() == Status.DISPOSED) {
return Duration.ZERO;
}
if (getStatus() == Status.STOPPED) {
return Duration.millis(getStartTime().toMillis());
}
if (isEOS) {
Duration duration = media.getDuration();
Duration stopTime = getStopTime();
if (stopTime != Duration.UNKNOWN && duration != Duration.UNKNOWN) {
if (stopTime.greaterThan(duration)) {
return Duration.millis(duration.toMillis());
} else {
return Duration.millis(stopTime.toMillis());
}
}
}
Duration theCurrentTime = currentTimeProperty().get();
if (playerReady) {
double timeSeconds = jfxPlayer.getPresentationTime();
if (timeSeconds >= 0.0) {
theCurrentTime = Duration.seconds(timeSeconds);
}
}
return theCurrentTime;
}
}
public ReadOnlyObjectProperty<Duration> currentTimeProperty() {
return currentTimePropertyImpl().getReadOnlyProperty();
}
private ReadOnlyObjectWrapper<Duration> currentTimePropertyImpl() {
if (currentTime == null) {
currentTime = new ReadOnlyObjectWrapper<Duration>(this, "currentTime");
currentTime.setValue(Duration.ZERO);
updateTime();
}
return currentTime;
}
public void seek(Duration seekTime) {
synchronized (disposeLock) {
if (getStatus() == Status.DISPOSED) {
return;
}
if (playerReady && seekTime != null && !seekTime.isUnknown()) {
if (jfxPlayer.getDuration() == Double.POSITIVE_INFINITY) {
return;
}
double seekSeconds;
if (seekTime.isIndefinite()) {
Duration duration = media.getDuration();
if (duration == null
|| duration.isUnknown()
|| duration.isIndefinite()) {
duration = Duration.millis(Double.MAX_VALUE);
}
seekSeconds = duration.toMillis() / 1000.0;
} else {
seekSeconds = seekTime.toMillis() / 1000.0;
double[] startStop = calculateStartStopTimes(getStartTime(), getStopTime());
if (seekSeconds < startStop[0]) {
seekSeconds = startStop[0];
} else if (seekSeconds > startStop[1]) {
seekSeconds = startStop[1];
}
}
if (!isUpdateTimeEnabled) {
Status playerStatus = getStatus();
if ((playerStatus == MediaPlayer.Status.PLAYING
|| playerStatus == MediaPlayer.Status.PAUSED)
&& getStartTime().toSeconds() <= seekSeconds
&& seekSeconds <= getStopTime().toSeconds()) {
isEOS = false;
isUpdateTimeEnabled = true;
setCurrentRate(getRate());
}
}
jfxPlayer.seek(seekSeconds);
}
}
}
private ReadOnlyObjectWrapper<Status> status;
private void setStatus(Status value) {
statusPropertyImpl().set(value);
}
public final Status getStatus() {
return status == null ? Status.UNKNOWN : status.get();
}
public ReadOnlyObjectProperty<Status> statusProperty() {
return statusPropertyImpl().getReadOnlyProperty();
}
private ReadOnlyObjectWrapper<Status> statusPropertyImpl() {
if (status == null) {
status = new ReadOnlyObjectWrapper<Status>() {
@Override
protected void invalidated() {
if (get() == Status.PLAYING) {
setCurrentRate(getRate());
} else {
setCurrentRate(0.0);
}
if (get() == Status.READY) {
if (getOnReady() != null) {
Platform.runLater(getOnReady());
}
} else if (get() == Status.PLAYING) {
if (getOnPlaying() != null) {
Platform.runLater(getOnPlaying());
}
} else if (get() == Status.PAUSED) {
if (getOnPaused() != null) {
Platform.runLater(getOnPaused());
}
} else if (get() == Status.STOPPED) {
if (getOnStopped() != null) {
Platform.runLater(getOnStopped());
}
} else if (get() == Status.STALLED) {
if (getOnStalled() != null) {
Platform.runLater(getOnStalled());
}
}
}
@Override
public Object getBean() {
return MediaPlayer.this;
}
@Override
public String getName() {
return "status";
}
};
}
return status;
}
private ReadOnlyObjectWrapper<Duration> bufferProgressTime;
private void setBufferProgressTime(Duration value) {
bufferProgressTimePropertyImpl().set(value);
}
public final Duration getBufferProgressTime() {
return bufferProgressTime == null ? null : bufferProgressTime.get();
}
public ReadOnlyObjectProperty<Duration> bufferProgressTimeProperty() {
return bufferProgressTimePropertyImpl().getReadOnlyProperty();
}
private ReadOnlyObjectWrapper<Duration> bufferProgressTimePropertyImpl() {
if (bufferProgressTime == null) {
bufferProgressTime = new ReadOnlyObjectWrapper<Duration>(this, "bufferProgressTime");
}
return bufferProgressTime;
}
private IntegerProperty cycleCount;
public final void setCycleCount(int value) {
cycleCountProperty().set(value);
}
public final int getCycleCount() {
return cycleCount == null ? 1 : cycleCount.get();
}
public IntegerProperty cycleCountProperty() {
if (cycleCount == null) {
cycleCount = new IntegerPropertyBase(1) {
@Override
public Object getBean() {
return MediaPlayer.this;
}
@Override
public String getName() {
return "cycleCount";
}
};
}
return cycleCount;
}
private ReadOnlyIntegerWrapper currentCount;
private void setCurrentCount(int value) {
currentCountPropertyImpl().set(value);
}
public final int getCurrentCount() {
return currentCount == null ? 0 : currentCount.get();
}
public ReadOnlyIntegerProperty currentCountProperty() {
return currentCountPropertyImpl().getReadOnlyProperty();
}
private ReadOnlyIntegerWrapper currentCountPropertyImpl() {
if (currentCount == null) {
currentCount = new ReadOnlyIntegerWrapper(this, "currentCount");
}
return currentCount;
}
private BooleanProperty mute;
public final void setMute (boolean value) {
muteProperty().set(value);
}
public final boolean isMute() {
return mute == null ? false : mute.get();
}
public BooleanProperty muteProperty() {
if (mute == null) {
mute = new BooleanPropertyBase() {
@Override
protected void invalidated() {
synchronized (disposeLock) {
if (getStatus() != Status.DISPOSED) {
if (playerReady) {
jfxPlayer.setMute(get());
} else {
muteChangeRequested = true;
}
}
}
}
@Override
public Object getBean() {
return MediaPlayer.this;
}
@Override
public String getName() {
return "mute";
}
};
}
return mute;
}
private ObjectProperty<EventHandler<MediaMarkerEvent>> onMarker;
public final void setOnMarker(EventHandler<MediaMarkerEvent> onMarker) {
onMarkerProperty().set(onMarker);
}
public final EventHandler<MediaMarkerEvent> getOnMarker() {
return onMarker == null ? null : onMarker.get();
}
public ObjectProperty<EventHandler<MediaMarkerEvent>> onMarkerProperty() {
if (onMarker == null) {
onMarker = new SimpleObjectProperty<EventHandler<MediaMarkerEvent>>(this, "onMarker");
}
return onMarker;
}
void addView(MediaView view) {
WeakReference<MediaView> vref = new WeakReference<MediaView>(view);
synchronized (viewRefs) {
viewRefs.add(vref);
}
}
void removeView(MediaView view) {
synchronized (viewRefs) {
for (WeakReference<MediaView> vref : viewRefs) {
MediaView v = vref.get();
if (v != null && v.equals(view)) {
viewRefs.remove(vref);
}
}
}
}
void handleError(final MediaException error) {
Platform.runLater(() -> {
setError(error);
if (error.getType() == MediaException.Type.MEDIA_CORRUPTED
|| error.getType() == MediaException.Type.MEDIA_UNSUPPORTED
|| error.getType() == MediaException.Type.MEDIA_INACCESSIBLE
|| error.getType() == MediaException.Type.MEDIA_UNAVAILABLE) {
media._setError(error.getType(), error.getMessage());
}
});
}
void createMediaTimer() {
synchronized (MediaTimerTask.timerLock) {
if (mediaTimerTask == null) {
mediaTimerTask = new MediaTimerTask(this);
mediaTimerTask.start();
}
isUpdateTimeEnabled = true;
}
}
void destroyMediaTimer() {
synchronized (MediaTimerTask.timerLock) {
if (mediaTimerTask != null) {
isUpdateTimeEnabled = false;
mediaTimerTask.stop();
mediaTimerTask = null;
}
}
}
void updateTime() {
if (playerReady && isUpdateTimeEnabled && jfxPlayer != null) {
double timeSeconds = jfxPlayer.getPresentationTime();
if (timeSeconds >= 0.0) {
double newTimeMs = timeSeconds*1000.0;
if (Double.compare(newTimeMs, prevTimeMs) != 0) {
setCurrentTime(Duration.millis(newTimeMs));
prevTimeMs = newTimeMs;
}
}
}
}
void loopPlayback() {
seek (getStartTime());
}
void handleRequestedChanges() {
if (rateChangeRequested) {
if (jfxPlayer.getDuration() != Double.POSITIVE_INFINITY) {
jfxPlayer.setRate((float)clamp(getRate(), RATE_MIN, RATE_MAX));
}
rateChangeRequested = false;
}
if (volumeChangeRequested) {
jfxPlayer.setVolume((float)clamp(getVolume(), 0.0, 1.0));
volumeChangeRequested = false;
}
if (balanceChangeRequested) {
jfxPlayer.setBalance((float)clamp(getBalance(), -1.0, 1.0));
balanceChangeRequested = false;
}
if (startTimeChangeRequested || stopTimeChangeRequested) {
setStartStopTimes(getStartTime(), startTimeChangeRequested, getStopTime(), stopTimeChangeRequested);
startTimeChangeRequested = stopTimeChangeRequested = false;
}
if (muteChangeRequested) {
jfxPlayer.setMute(isMute());
muteChangeRequested = false;
}
if (audioSpectrumNumBandsChangeRequested) {
jfxPlayer.getAudioSpectrum().setBandCount(clamp(getAudioSpectrumNumBands(), AUDIOSPECTRUM_NUMBANDS_MIN, Integer.MAX_VALUE));
audioSpectrumNumBandsChangeRequested = false;
}
if (audioSpectrumIntervalChangeRequested) {
jfxPlayer.getAudioSpectrum().setInterval(clamp(getAudioSpectrumInterval(), AUDIOSPECTRUM_INTERVAL_MIN, Double.MAX_VALUE));
audioSpectrumIntervalChangeRequested = false;
}
if (audioSpectrumThresholdChangeRequested) {
jfxPlayer.getAudioSpectrum().setSensitivityThreshold(clamp(getAudioSpectrumThreshold(), Integer.MIN_VALUE, AUDIOSPECTRUM_THRESHOLD_MAX));
audioSpectrumThresholdChangeRequested = false;
}
if (audioSpectrumEnabledChangeRequested) {
boolean enabled = (getAudioSpectrumListener() != null);
jfxPlayer.getAudioSpectrum().setEnabled(enabled);
audioSpectrumEnabledChangeRequested = false;
}
if (playRequested) {
jfxPlayer.play();
playRequested = false;
}
}
void preReady() {
synchronized (viewRefs) {
for (WeakReference<MediaView> vref : viewRefs) {
MediaView v = vref.get();
if (v != null) {
v._mediaPlayerOnReady();
}
}
}
if (audioEqualizer != null) {
audioEqualizer.setAudioEqualizer(jfxPlayer.getEqualizer());
}
double durationSeconds = jfxPlayer.getDuration();
Duration duration;
if (durationSeconds >= 0.0 && !Double.isNaN(durationSeconds)) {
duration = Duration.millis(durationSeconds * 1000.0);
} else {
duration = Duration.UNKNOWN;
}
playerReady = true;
media.setDuration(duration);
media._updateMedia(jfxPlayer.getMedia());
handleRequestedChanges();
calculateCycleDuration();
if (lastBufferEvent != null && duration.toMillis() > 0.0) {
double position = lastBufferEvent.getBufferPosition();
double stop = lastBufferEvent.getBufferStop();
final double bufferedTime = position / stop * duration.toMillis();
lastBufferEvent = null;
setBufferProgressTime(Duration.millis(bufferedTime));
}
setStatus(Status.READY);
}
private ObjectProperty<Runnable> onEndOfMedia;
public final void setOnEndOfMedia(Runnable value) {
onEndOfMediaProperty().set(value);
}
public final Runnable getOnEndOfMedia() {
return onEndOfMedia == null ? null : onEndOfMedia.get();
}
public ObjectProperty<Runnable> onEndOfMediaProperty() {
if (onEndOfMedia == null) {
onEndOfMedia = new SimpleObjectProperty<Runnable>(this, "onEndOfMedia");
}
return onEndOfMedia;
}
private ObjectProperty<Runnable> onReady;
public final void setOnReady(Runnable value) {
onReadyProperty().set(value);
}
public final Runnable getOnReady() {
return onReady == null ? null : onReady.get();
}
public ObjectProperty<Runnable> onReadyProperty() {
if (onReady == null) {
onReady = new SimpleObjectProperty<Runnable>(this, "onReady");
}
return onReady;
}
private ObjectProperty<Runnable> onPlaying;
public final void setOnPlaying(Runnable value) {
onPlayingProperty().set(value);
}
public final Runnable getOnPlaying() {
return onPlaying == null ? null : onPlaying.get();
}
public ObjectProperty<Runnable> onPlayingProperty() {
if (onPlaying == null) {
onPlaying = new SimpleObjectProperty<Runnable>(this, "onPlaying");
}
return onPlaying;
}
private ObjectProperty<Runnable> onPaused;
public final void setOnPaused(Runnable value) {
onPausedProperty().set(value);
}
public final Runnable getOnPaused() {
return onPaused == null ? null : onPaused.get();
}
public ObjectProperty<Runnable> onPausedProperty() {
if (onPaused == null) {
onPaused = new SimpleObjectProperty<Runnable>(this, "onPaused");
}
return onPaused;
}
private ObjectProperty<Runnable> onStopped;
public final void setOnStopped(Runnable value) {
onStoppedProperty().set(value);
}
public final Runnable getOnStopped() {
return onStopped == null ? null : onStopped.get();
}
public ObjectProperty<Runnable> onStoppedProperty() {
if (onStopped == null) {
onStopped = new SimpleObjectProperty<Runnable>(this, "onStopped");
}
return onStopped;
}
private ObjectProperty<Runnable> onHalted;
public final void setOnHalted(Runnable value) {
onHaltedProperty().set(value);
}
public final Runnable getOnHalted() {
return onHalted == null ? null : onHalted.get();
}
public ObjectProperty<Runnable> onHaltedProperty() {
if (onHalted == null) {
onHalted = new SimpleObjectProperty<Runnable>(this, "onHalted");
}
return onHalted;
}
private ObjectProperty<Runnable> onRepeat;
public final void setOnRepeat(Runnable value) {
onRepeatProperty().set(value);
}
public final Runnable getOnRepeat() {
return onRepeat == null ? null : onRepeat.get();
}
public ObjectProperty<Runnable> onRepeatProperty() {
if (onRepeat == null) {
onRepeat = new SimpleObjectProperty<Runnable>(this, "onRepeat");
}
return onRepeat;
}
private ObjectProperty<Runnable> onStalled;
public final void setOnStalled(Runnable value) {
onStalledProperty().set(value);
}
public final Runnable getOnStalled() {
return onStalled == null ? null : onStalled.get();
}
public ObjectProperty<Runnable> onStalledProperty() {
if (onStalled == null) {
onStalled = new SimpleObjectProperty<Runnable>(this, "onStalled");
}
return onStalled;
}
private IntegerProperty audioSpectrumNumBands;
public final void setAudioSpectrumNumBands(int value) {
audioSpectrumNumBandsProperty().setValue(value);
}
public final int getAudioSpectrumNumBands() {
return audioSpectrumNumBandsProperty().getValue();
}
public IntegerProperty audioSpectrumNumBandsProperty() {
if (audioSpectrumNumBands == null) {
audioSpectrumNumBands = new IntegerPropertyBase(DEFAULT_SPECTRUM_BAND_COUNT) {
@Override
protected void invalidated() {
synchronized (disposeLock) {
if (getStatus() != Status.DISPOSED) {
if (playerReady) {
jfxPlayer.getAudioSpectrum().setBandCount(clamp(audioSpectrumNumBands.get(), AUDIOSPECTRUM_NUMBANDS_MIN, Integer.MAX_VALUE));
} else {
audioSpectrumNumBandsChangeRequested = true;
}
}
}
}
@Override
public Object getBean() {
return MediaPlayer.this;
}
@Override
public String getName() {
return "audioSpectrumNumBands";
}
};
}
return audioSpectrumNumBands;
}
private DoubleProperty audioSpectrumInterval;
public final void setAudioSpectrumInterval(double value) {
audioSpectrumIntervalProperty().set(value);
}
public final double getAudioSpectrumInterval() {
return audioSpectrumIntervalProperty().get();
}
public DoubleProperty audioSpectrumIntervalProperty() {
if (audioSpectrumInterval == null) {
audioSpectrumInterval = new DoublePropertyBase(DEFAULT_SPECTRUM_INTERVAL) {
@Override
protected void invalidated() {
synchronized (disposeLock) {
if (getStatus() != Status.DISPOSED) {
if (playerReady) {
jfxPlayer.getAudioSpectrum().setInterval(clamp(audioSpectrumInterval.get(), AUDIOSPECTRUM_INTERVAL_MIN, Double.MAX_VALUE));
} else {
audioSpectrumIntervalChangeRequested = true;
}
}
}
}
@Override
public Object getBean() {
return MediaPlayer.this;
}
@Override
public String getName() {
return "audioSpectrumInterval";
}
};
}
return audioSpectrumInterval;
}
private IntegerProperty audioSpectrumThreshold;
public final void setAudioSpectrumThreshold(int value) {
audioSpectrumThresholdProperty().set(value);
}
public final int getAudioSpectrumThreshold() {
return audioSpectrumThresholdProperty().get();
}
public IntegerProperty audioSpectrumThresholdProperty() {
if (audioSpectrumThreshold == null) {
audioSpectrumThreshold = new IntegerPropertyBase(DEFAULT_SPECTRUM_THRESHOLD) {
@Override
protected void invalidated() {
synchronized (disposeLock) {
if (getStatus() != Status.DISPOSED) {
if (playerReady) {
jfxPlayer.getAudioSpectrum().setSensitivityThreshold(clamp(audioSpectrumThreshold.get(), Integer.MIN_VALUE, AUDIOSPECTRUM_THRESHOLD_MAX));
} else {
audioSpectrumThresholdChangeRequested = true;
}
}
}
}
@Override
public Object getBean() {
return MediaPlayer.this;
}
@Override
public String getName() {
return "audioSpectrumThreshold";
}
};
}
return audioSpectrumThreshold;
}
private ObjectProperty<AudioSpectrumListener> audioSpectrumListener;
public final void setAudioSpectrumListener(AudioSpectrumListener listener) {
audioSpectrumListenerProperty().set(listener);
}
public final AudioSpectrumListener getAudioSpectrumListener() {
return audioSpectrumListenerProperty().get();
}
public ObjectProperty<AudioSpectrumListener> audioSpectrumListenerProperty() {
if (audioSpectrumListener == null) {
audioSpectrumListener = new ObjectPropertyBase<AudioSpectrumListener>() {
@Override
protected void invalidated() {
synchronized (disposeLock) {
if (getStatus() != Status.DISPOSED) {
if (playerReady) {
boolean enabled = (audioSpectrumListener.get() != null);
jfxPlayer.getAudioSpectrum().setEnabled(enabled);
} else {
audioSpectrumEnabledChangeRequested = true;
}
}
}
}
@Override
public Object getBean() {
return MediaPlayer.this;
}
@Override
public String getName() {
return "audioSpectrumListener";
}
};
}
return audioSpectrumListener;
}
public synchronized void dispose() {
synchronized (disposeLock) {
setStatus(Status.DISPOSED);
destroyMediaTimer();
if (audioEqualizer != null) {
audioEqualizer.setAudioEqualizer(null);
audioEqualizer = null;
}
if (jfxPlayer != null) {
jfxPlayer.dispose();
synchronized (renderLock) {
if (rendererListener != null) {
Toolkit.getToolkit().removeStageTkPulseListener(rendererListener);
rendererListener = null;
}
}
jfxPlayer = null;
}
}
}
private class MarkerMapChangeListener implements MapChangeListener<String, Duration> {
@Override
public void onChanged(Change<? extends String, ? extends Duration> change) {
synchronized (disposeLock) {
if (getStatus() != Status.DISPOSED) {
String key = change.getKey();
if (key == null) {
return;
}
com.sun.media.jfxmedia.Media jfxMedia = jfxPlayer.getMedia();
if (change.wasAdded()) {
if (change.wasRemoved()) {
jfxMedia.removeMarker(key);
}
Duration value = change.getValueAdded();
if (value != null && value.greaterThanOrEqualTo(Duration.ZERO)) {
jfxMedia.addMarker(key, change.getValueAdded().toMillis() / 1000.0);
}
} else if (change.wasRemoved()) {
jfxMedia.removeMarker(key);
}
}
}
}
}
private class _MarkerListener implements MarkerListener {
@Override
public void onMarker(final MarkerEvent evt) {
Platform.runLater(() -> {
Duration markerTime = Duration.millis(evt.getPresentationTime() * 1000.0);
if (getOnMarker() != null) {
getOnMarker().handle(new MediaMarkerEvent(new Pair<String, Duration>(evt.getMarkerName(), markerTime)));
}
});
}
}
private class _PlayerStateListener implements PlayerStateListener {
@Override
public void onReady(PlayerStateEvent evt) {
Platform.runLater(() -> {
synchronized (disposeLock) {
if (getStatus() == Status.DISPOSED) {
return;
}
preReady();
}
});
}
@Override
public void onPlaying(PlayerStateEvent evt) {
startTimeAtStop = null;
Platform.runLater(() -> {
createMediaTimer();
setStatus(Status.PLAYING);
});
}
@Override
public void onPause(PlayerStateEvent evt) {
Platform.runLater(() -> {
isUpdateTimeEnabled = false;
setStatus(Status.PAUSED);
});
if (startTimeAtStop != null && startTimeAtStop != getStartTime()) {
startTimeAtStop = null;
Platform.runLater(() -> {
setCurrentTime(getStartTime());
});
}
}
@Override
public void onStop(PlayerStateEvent evt) {
Platform.runLater(() -> {
destroyMediaTimer();
startTimeAtStop = getStartTime();
setCurrentTime(getStartTime());
setStatus(Status.STOPPED);
});
}
@Override
public void onStall(PlayerStateEvent evt) {
Platform.runLater(() -> {
isUpdateTimeEnabled = false;
setStatus(Status.STALLED);
});
}
void handleFinish() {
setCurrentCount(getCurrentCount() + 1);
if ((getCurrentCount() < getCycleCount()) || (getCycleCount() == INDEFINITE)) {
if (getOnEndOfMedia() != null) {
Platform.runLater(getOnEndOfMedia());
}
loopPlayback();
if (getOnRepeat() != null) {
Platform.runLater(getOnRepeat());
}
} else {
isUpdateTimeEnabled = false;
setCurrentRate(0.0);
isEOS = true;
if (getOnEndOfMedia() != null) {
Platform.runLater(getOnEndOfMedia());
}
}
}
@Override
public void onFinish(PlayerStateEvent evt) {
startTimeAtStop = null;
Platform.runLater(() -> {
handleFinish();
});
}
@Override
public void onHalt(final PlayerStateEvent evt) {
Platform.runLater(() -> {
setStatus(Status.HALTED);
handleError(MediaException.haltException(evt.getMessage()));
isUpdateTimeEnabled = false;
});
}
}
private class _PlayerTimeListener implements PlayerTimeListener {
double theDuration;
void handleDurationChanged() {
media.setDuration(Duration.millis(theDuration * 1000.0));
}
@Override
public void onDurationChanged(final double duration) {
Platform.runLater(() -> {
theDuration = duration;
handleDurationChanged();
});
}
}
private class _VideoTrackSizeListener implements VideoTrackSizeListener {
int trackWidth;
int trackHeight;
@Override
public void onSizeChanged(final int width, final int height) {
Platform.runLater(() -> {
if (media != null) {
trackWidth = width;
trackHeight = height;
setSize();
}
});
}
void setSize() {
media.setWidth(trackWidth);
media.setHeight(trackHeight);
synchronized (viewRefs) {
for (WeakReference<MediaView> vref : viewRefs) {
MediaView v = vref.get();
if (v != null) {
v.notifyMediaSizeChange();
}
}
}
}
}
private class _MediaErrorListener implements com.sun.media.jfxmedia.events.MediaErrorListener {
@Override
public void onError(Object source, int errorCode, String message) {
MediaException error = MediaException.getMediaException(source, errorCode, message);
handleError(error);
}
}
private class _BufferListener implements BufferListener {
double bufferedTime;
@Override
public void onBufferProgress(BufferProgressEvent evt) {
if (media != null) {
if (evt.getDuration() > 0.0) {
double position = evt.getBufferPosition();
double stop = evt.getBufferStop();
bufferedTime = position/stop * evt.getDuration()*1000.0;
lastBufferEvent = null;
Platform.runLater(() -> {
setBufferProgressTime(Duration.millis(bufferedTime));
});
} else {
lastBufferEvent = evt;
}
}
}
}
private class _SpectrumListener implements com.sun.media.jfxmedia.events.AudioSpectrumListener {
private float[] magnitudes;
private float[] phases;
@Override public void onAudioSpectrumEvent(final AudioSpectrumEvent evt) {
Platform.runLater(() -> {
AudioSpectrumListener listener = getAudioSpectrumListener();
if (listener != null) {
listener.spectrumDataUpdate(evt.getTimestamp(),
evt.getDuration(),
magnitudes = evt.getSource().getMagnitudes(magnitudes),
phases = evt.getSource().getPhases(phases));
}
});
}
}
private final Object renderLock = new Object();
private VideoDataBuffer currentRenderFrame;
private VideoDataBuffer nextRenderFrame;
VideoDataBuffer getLatestFrame() {
synchronized (renderLock) {
if (null != currentRenderFrame) {
currentRenderFrame.holdFrame();
}
return currentRenderFrame;
}
}
private class RendererListener implements
com.sun.media.jfxmedia.events.VideoRendererListener,
TKPulseListener
{
boolean updateMediaViews;
@Override
public void videoFrameUpdated(NewFrameEvent nfe) {
VideoDataBuffer vdb = nfe.getFrameData();
if (null != vdb) {
Duration frameTS = new Duration(vdb.getTimestamp() * 1000);
Duration stopTime = getStopTime();
if (frameTS.greaterThanOrEqualTo(getStartTime()) && (stopTime.isUnknown() || frameTS.lessThanOrEqualTo(stopTime))) {
updateMediaViews = true;
synchronized (renderLock) {
vdb.holdFrame();
if (null != nextRenderFrame) {
nextRenderFrame.releaseFrame();
}
nextRenderFrame = vdb;
}
Toolkit.getToolkit().requestNextPulse();
} else {
vdb.releaseFrame();
}
}
}
@Override
public void releaseVideoFrames() {
synchronized (renderLock) {
if (null != currentRenderFrame) {
currentRenderFrame.releaseFrame();
currentRenderFrame = null;
}
if (null != nextRenderFrame) {
nextRenderFrame.releaseFrame();
nextRenderFrame = null;
}
}
}
@Override
public void pulse() {
if (updateMediaViews) {
updateMediaViews = false;
synchronized (renderLock) {
if (null != nextRenderFrame) {
if (null != currentRenderFrame) {
currentRenderFrame.releaseFrame();
}
currentRenderFrame = nextRenderFrame;
nextRenderFrame = null;
}
}
synchronized (viewRefs) {
Iterator<WeakReference<MediaView>> iter = viewRefs.iterator();
while (iter.hasNext()) {
MediaView view = iter.next().get();
if (null != view) {
view.notifyMediaFrameUpdated();
} else {
iter.remove();
}
}
}
}
}
}
}
class MediaPlayerShutdownHook implements Runnable {
private final static List<WeakReference<MediaPlayer>> playerRefs = new ArrayList<WeakReference<MediaPlayer>>();
private static boolean isShutdown = false;
static {
Toolkit.getToolkit().addShutdownHook(new MediaPlayerShutdownHook());
}
public static void addMediaPlayer(MediaPlayer player) {
synchronized (playerRefs) {
if (isShutdown) {
com.sun.media.jfxmedia.MediaPlayer jfxPlayer = player.retrieveJfxPlayer();
if (jfxPlayer != null) {
jfxPlayer.dispose();
}
} else {
for (ListIterator<WeakReference<MediaPlayer>> it = playerRefs.listIterator(); it.hasNext();) {
MediaPlayer l = it.next().get();
if (l == null) {
it.remove();
}
}
playerRefs.add(new WeakReference<MediaPlayer>(player));
}
}
}
@Override
public void run() {
synchronized (playerRefs) {
for (ListIterator<WeakReference<MediaPlayer>> it = playerRefs.listIterator(); it.hasNext();) {
MediaPlayer player = it.next().get();
if (player != null) {
player.destroyMediaTimer();
com.sun.media.jfxmedia.MediaPlayer jfxPlayer = player.retrieveJfxPlayer();
if (jfxPlayer != null) {
jfxPlayer.dispose();
}
} else {
it.remove();
}
}
isShutdown = true;
}
}
}
class MediaTimerTask extends TimerTask {
private Timer mediaTimer = null;
static final Object timerLock = new Object();
private WeakReference<MediaPlayer> playerRef;
MediaTimerTask(MediaPlayer player) {
playerRef = new WeakReference<MediaPlayer>(player);
}
void start() {
if (mediaTimer == null) {
mediaTimer = new Timer(true);
mediaTimer.scheduleAtFixedRate(this, 0, 100 );
}
}
void stop() {
if (mediaTimer != null) {
mediaTimer.cancel();
mediaTimer = null;
}
}
@Override
public void run() {
synchronized (timerLock) {
final MediaPlayer player = playerRef.get();
if (player != null) {
Platform.runLater(() -> {
synchronized (timerLock) {
player.updateTime();
}
});
} else {
cancel();
}
}
}
}
