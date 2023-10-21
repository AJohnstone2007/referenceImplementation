package javafx.scene.media;
import com.sun.media.jfxmedia.MetadataParser;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.image.Image;
import javafx.util.Duration;
import com.sun.media.jfxmedia.locator.Locator;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import com.sun.media.jfxmedia.events.MetadataListener;
import com.sun.media.jfxmedia.track.VideoResolution;
public final class Media {
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
return Media.this;
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
return Media.this;
}
@Override
public String getName() {
return "onError";
}
};
}
return onError;
}
private MetadataListener metadataListener = new _MetadataListener();
private ObservableMap<String, Object> metadata;
public final ObservableMap<String, Object> getMetadata() {
return metadata;
}
private final ObservableMap<String,Object> metadataBacking = FXCollections.observableMap(new HashMap<String,Object>());
private ReadOnlyIntegerWrapper width;
final void setWidth(int value) {
widthPropertyImpl().set(value);
}
public final int getWidth() {
return width == null ? 0 : width.get();
}
public ReadOnlyIntegerProperty widthProperty() {
return widthPropertyImpl().getReadOnlyProperty();
}
private ReadOnlyIntegerWrapper widthPropertyImpl() {
if (width == null) {
width = new ReadOnlyIntegerWrapper(this, "width");
}
return width;
}
private ReadOnlyIntegerWrapper height;
final void setHeight(int value) {
heightPropertyImpl().set(value);
}
public final int getHeight() {
return height == null ? 0 : height.get();
}
public ReadOnlyIntegerProperty heightProperty() {
return heightPropertyImpl().getReadOnlyProperty();
}
private ReadOnlyIntegerWrapper heightPropertyImpl() {
if (height == null) {
height = new ReadOnlyIntegerWrapper(this, "height");
}
return height;
}
private ReadOnlyObjectWrapper<Duration> duration;
final void setDuration(Duration value) {
durationPropertyImpl().set(value);
}
public final Duration getDuration() {
return duration == null || duration.get() == null ? Duration.UNKNOWN : duration.get();
}
public ReadOnlyObjectProperty<Duration> durationProperty() {
return durationPropertyImpl().getReadOnlyProperty();
}
private ReadOnlyObjectWrapper<Duration> durationPropertyImpl() {
if (duration == null) {
duration = new ReadOnlyObjectWrapper<Duration>(this, "duration");
}
return duration;
}
private ObservableList<Track> tracks;
public final ObservableList<Track> getTracks() {
return tracks;
}
private final ObservableList<Track> tracksBacking = FXCollections.observableArrayList();
private ObservableMap<String, Duration> markers = FXCollections.observableMap(new HashMap<String,Duration>());
public final ObservableMap<String, Duration> getMarkers() {
return markers;
}
public Media(@NamedArg("source") String source) {
this.source = source;
URI uri = null;
try {
uri = new URI(source);
} catch(URISyntaxException use) {
throw new IllegalArgumentException(use);
}
metadata = FXCollections.unmodifiableObservableMap(metadataBacking);
tracks = FXCollections.unmodifiableObservableList(tracksBacking);
Locator locator = null;
try {
locator = new com.sun.media.jfxmedia.locator.Locator(uri);
jfxLocator = locator;
if (locator.canBlock()) {
InitLocator locatorInit = new InitLocator();
Thread t = new Thread(locatorInit);
t.setDaemon(true);
t.start();
} else {
locator.init();
runMetadataParser();
}
} catch(URISyntaxException use) {
throw new IllegalArgumentException(use);
} catch(FileNotFoundException fnfe) {
throw new MediaException(MediaException.Type.MEDIA_UNAVAILABLE, fnfe.getMessage());
} catch(IOException ioe) {
throw new MediaException(MediaException.Type.MEDIA_INACCESSIBLE, ioe.getMessage());
} catch(com.sun.media.jfxmedia.MediaException me) {
throw new MediaException(MediaException.Type.MEDIA_UNSUPPORTED, me.getMessage());
}
}
private void runMetadataParser() {
try {
jfxParser = com.sun.media.jfxmedia.MediaManager.getMetadataParser(jfxLocator);
jfxParser.addListener(metadataListener);
jfxParser.startParser();
} catch (Exception e) {
jfxParser = null;
}
}
private final String source;
public String getSource() {
return source;
}
private final Locator jfxLocator;
Locator retrieveJfxLocator() {
return jfxLocator;
}
private MetadataParser jfxParser;
private Track getTrackWithID(long trackID) {
for (Track track : tracksBacking) {
if (track.getTrackID() == trackID) {
return track;
}
}
return null;
}
void _updateMedia(com.sun.media.jfxmedia.Media _media) {
try {
List<com.sun.media.jfxmedia.track.Track> trackList = _media.getTracks();
if (trackList != null) {
for (com.sun.media.jfxmedia.track.Track trackElement : trackList) {
long trackID = trackElement.getTrackID();
if (getTrackWithID(trackID) == null) {
Track newTrack = null;
Map<String,Object> trackMetadata = new HashMap<String,Object>();
if (null != trackElement.getName()) {
trackMetadata.put("name", trackElement.getName());
}
if (null != trackElement.getLocale()) {
trackMetadata.put("locale", trackElement.getLocale());
}
trackMetadata.put("encoding", trackElement.getEncodingType().toString());
trackMetadata.put("enabled", Boolean.valueOf(trackElement.isEnabled()));
if (trackElement instanceof com.sun.media.jfxmedia.track.VideoTrack) {
com.sun.media.jfxmedia.track.VideoTrack vt =
(com.sun.media.jfxmedia.track.VideoTrack) trackElement;
int videoWidth = vt.getFrameSize().getWidth();
int videoHeight = vt.getFrameSize().getHeight();
setWidth(videoWidth);
setHeight(videoHeight);
trackMetadata.put("video width", Integer.valueOf(videoWidth));
trackMetadata.put("video height", Integer.valueOf(videoHeight));
newTrack = new VideoTrack(trackElement.getTrackID(), trackMetadata);
} else if (trackElement instanceof com.sun.media.jfxmedia.track.AudioTrack) {
newTrack = new AudioTrack(trackElement.getTrackID(), trackMetadata);
} else if (trackElement instanceof com.sun.media.jfxmedia.track.SubtitleTrack) {
newTrack = new SubtitleTrack(trackID, trackMetadata);
}
if (null != newTrack) {
tracksBacking.add(newTrack);
}
}
}
}
} catch (Exception e) {
setError(new MediaException(MediaException.Type.UNKNOWN, e));
}
}
void _setError(MediaException.Type type, String message) {
setError(new MediaException(type, message));
}
private synchronized void updateMetadata(Map<String, Object> metadata) {
if (metadata != null) {
for (Map.Entry<String,Object> entry : metadata.entrySet()) {
String key = entry.getKey();
Object value = entry.getValue();
if (key.equals(MetadataParser.IMAGE_TAG_NAME) && value instanceof byte[]) {
byte[] imageData = (byte[]) value;
Image image = new Image(new ByteArrayInputStream(imageData));
if (!image.isError()) {
metadataBacking.put(MetadataParser.IMAGE_TAG_NAME, image);
}
} else if (key.equals(MetadataParser.DURATION_TAG_NAME) && value instanceof java.lang.Long) {
Duration d = new Duration((Long) value);
if (d != null) {
metadataBacking.put(MetadataParser.DURATION_TAG_NAME, d);
}
} else {
metadataBacking.put(key, value);
}
}
}
}
private class _MetadataListener implements MetadataListener {
@Override
public void onMetadata(final Map<String, Object> metadata) {
Platform.runLater(() -> {
updateMetadata(metadata);
jfxParser.removeListener(metadataListener);
jfxParser.stopParser();
jfxParser = null;
});
}
}
private class InitLocator implements Runnable {
@Override
public void run() {
try {
jfxLocator.init();
runMetadataParser();
} catch (URISyntaxException use) {
_setError(MediaException.Type.OPERATION_UNSUPPORTED, use.getMessage());
} catch (FileNotFoundException fnfe) {
_setError(MediaException.Type.MEDIA_UNAVAILABLE, fnfe.getMessage());
} catch (IOException ioe) {
_setError(MediaException.Type.MEDIA_INACCESSIBLE, ioe.getMessage());
} catch (com.sun.media.jfxmedia.MediaException me) {
_setError(MediaException.Type.MEDIA_UNSUPPORTED, me.getMessage());
} catch (Exception e) {
_setError(MediaException.Type.UNKNOWN, e.getMessage());
}
}
}
}
