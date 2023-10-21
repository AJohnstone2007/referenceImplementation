package javafx.scene.media;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.media.MediaViewHelper;
import com.sun.javafx.sg.prism.MediaFrameTracker;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.tk.Toolkit;
import com.sun.media.jfxmediaimpl.HostUtils;
import com.sun.media.jfxmedia.control.MediaPlayerOverlay;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableMap;
import javafx.event.EventHandler;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
public class MediaView extends Node {
static {
MediaViewHelper.setMediaViewAccessor(new MediaViewHelper.MediaViewAccessor() {
@Override
public NGNode doCreatePeer(Node node) {
return ((MediaView) node).doCreatePeer();
}
@Override
public void doUpdatePeer(Node node) {
((MediaView) node).doUpdatePeer();
}
@Override
public void doTransformsChanged(Node node) {
((MediaView) node).doTransformsChanged();
}
@Override
public BaseBounds doComputeGeomBounds(Node node,
BaseBounds bounds, BaseTransform tx) {
return ((MediaView) node).doComputeGeomBounds(bounds, tx);
}
@Override
public boolean doComputeContains(Node node, double localX, double localY) {
return ((MediaView) node).doComputeContains(localX, localY);
}
});
}
private static final String VIDEO_FRAME_RATE_PROPERTY_NAME = "jfxmedia.decodedVideoFPS";
private static final String DEFAULT_STYLE_CLASS = "media-view";
private class MediaErrorInvalidationListener implements InvalidationListener {
@Override public void invalidated(Observable value) {
ObservableObjectValue<MediaException> errorProperty = (ObservableObjectValue<MediaException>)value;
fireEvent(new MediaErrorEvent(getMediaPlayer(), getMediaView(), errorProperty.get()));
}
}
private InvalidationListener errorListener = new MediaErrorInvalidationListener();
private InvalidationListener mediaDimensionListener = value -> {
NodeHelper.markDirty(this, DirtyBits.NODE_VIEWPORT);
NodeHelper.geomChanged(this);
};
private com.sun.media.jfxmedia.events.VideoFrameRateListener decodedFrameRateListener;
private boolean registerVideoFrameRateListener = false;
private com.sun.media.jfxmedia.events.VideoFrameRateListener createVideoFrameRateListener() {
String listenerProp = null;
try {
listenerProp = System.getProperty(VIDEO_FRAME_RATE_PROPERTY_NAME);
} catch (Throwable t) {
}
if (listenerProp == null || !Boolean.getBoolean(VIDEO_FRAME_RATE_PROPERTY_NAME)) {
return null;
} else {
return videoFrameRate -> {
Platform.runLater(() -> {
ObservableMap props = getProperties();
props.put(VIDEO_FRAME_RATE_PROPERTY_NAME, videoFrameRate);
});
};
}
}
private MediaPlayerOverlay mediaPlayerOverlay = null;
private ChangeListener<Parent> parentListener;
private ChangeListener<Boolean> treeVisibleListener;
private ChangeListener<Number> opacityListener;
private void createListeners() {
parentListener = (ov2, oldParent, newParent) -> {
updateOverlayVisibility();
};
treeVisibleListener = (ov1, oldVisible, newVisible) -> {
updateOverlayVisibility();
};
opacityListener = (ov, oldOpacity, newOpacity) -> {
updateOverlayOpacity();
};
}
private boolean determineVisibility() {
return (getParent() != null && isVisible());
}
private synchronized void updateOverlayVisibility() {
if (mediaPlayerOverlay != null) {
mediaPlayerOverlay.setOverlayVisible(determineVisibility());
}
}
private synchronized void updateOverlayOpacity() {
if (mediaPlayerOverlay != null) {
mediaPlayerOverlay.setOverlayOpacity(getOpacity());
}
}
private synchronized void updateOverlayX() {
if (mediaPlayerOverlay != null) {
mediaPlayerOverlay.setOverlayX(getX());
}
}
private synchronized void updateOverlayY() {
if (mediaPlayerOverlay != null) {
mediaPlayerOverlay.setOverlayY(getY());
}
}
private synchronized void updateOverlayWidth() {
if (mediaPlayerOverlay != null) {
mediaPlayerOverlay.setOverlayWidth(getFitWidth());
}
}
private synchronized void updateOverlayHeight() {
if (mediaPlayerOverlay != null) {
mediaPlayerOverlay.setOverlayHeight(getFitHeight());
}
}
private synchronized void updateOverlayPreserveRatio() {
if (mediaPlayerOverlay != null) {
mediaPlayerOverlay.setOverlayPreserveRatio(isPreserveRatio());
}
}
private static Affine3D calculateNodeToSceneTransform(Node node) {
final Affine3D transform = new Affine3D();
do {
transform.preConcatenate(NodeHelper.getLeafTransform(node));
node = node.getParent();
} while (node != null);
return transform;
}
private void updateOverlayTransform() {
if (mediaPlayerOverlay != null) {
final Affine3D trans = MediaView.calculateNodeToSceneTransform(this);
mediaPlayerOverlay.setOverlayTransform(
trans.getMxx(), trans.getMxy(), trans.getMxz(), trans.getMxt(),
trans.getMyx(), trans.getMyy(), trans.getMyz(), trans.getMyt(),
trans.getMzx(), trans.getMzy(), trans.getMzz(), trans.getMzt());
}
}
private void updateMediaPlayerOverlay() {
mediaPlayerOverlay.setOverlayX(getX());
mediaPlayerOverlay.setOverlayY(getY());
mediaPlayerOverlay.setOverlayPreserveRatio(isPreserveRatio());
mediaPlayerOverlay.setOverlayWidth(getFitWidth());
mediaPlayerOverlay.setOverlayHeight(getFitHeight());
mediaPlayerOverlay.setOverlayOpacity(getOpacity());
mediaPlayerOverlay.setOverlayVisible(determineVisibility());
updateOverlayTransform();
}
private void doTransformsChanged() {
if (mediaPlayerOverlay != null) {
updateOverlayTransform();
}
}
private MediaView getMediaView() {
return this;
}
{
MediaViewHelper.initHelper(this);
}
public MediaView() {
getStyleClass().add(DEFAULT_STYLE_CLASS);
setSmooth(Toolkit.getToolkit().getDefaultImageSmooth());
decodedFrameRateListener = createVideoFrameRateListener();
setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
}
public MediaView(MediaPlayer mediaPlayer) {
this();
setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
setMediaPlayer(mediaPlayer);
}
private ObjectProperty<MediaPlayer> mediaPlayer;
public final void setMediaPlayer (MediaPlayer value) {
mediaPlayerProperty().set(value);
}
public final MediaPlayer getMediaPlayer() {
return mediaPlayer == null ? null : mediaPlayer.get();
}
public final ObjectProperty<MediaPlayer> mediaPlayerProperty() {
if (mediaPlayer == null) {
mediaPlayer = new ObjectPropertyBase<MediaPlayer>() {
MediaPlayer oldValue = null;
@Override protected void invalidated() {
if (oldValue != null) {
Media media = oldValue.getMedia();
if (media != null) {
media.widthProperty().removeListener(mediaDimensionListener);
media.heightProperty().removeListener(mediaDimensionListener);
}
if (decodedFrameRateListener != null && getMediaPlayer().retrieveJfxPlayer() != null) {
getMediaPlayer().retrieveJfxPlayer().getVideoRenderControl().removeVideoFrameRateListener(decodedFrameRateListener);
}
oldValue.errorProperty().removeListener(errorListener);
oldValue.removeView(getMediaView());
}
MediaPlayer newValue = get();
if (newValue != null) {
newValue.addView(getMediaView());
newValue.errorProperty().addListener(errorListener);
if (decodedFrameRateListener != null && getMediaPlayer().retrieveJfxPlayer() != null) {
getMediaPlayer().retrieveJfxPlayer().getVideoRenderControl().addVideoFrameRateListener(decodedFrameRateListener);
} else if (decodedFrameRateListener != null) {
registerVideoFrameRateListener = true;
}
Media media = newValue.getMedia();
if (media != null) {
media.widthProperty().addListener(mediaDimensionListener);
media.heightProperty().addListener(mediaDimensionListener);
}
}
NodeHelper.markDirty(MediaView.this, DirtyBits.MEDIAVIEW_MEDIA);
NodeHelper.geomChanged(MediaView.this);
oldValue = newValue;
}
@Override
public Object getBean() {
return MediaView.this;
}
@Override
public String getName() {
return "mediaPlayer";
}
};
}
return mediaPlayer;
}
private ObjectProperty<EventHandler<MediaErrorEvent>> onError;
public final void setOnError(EventHandler<MediaErrorEvent> value) {
onErrorProperty().set( value);
}
public final EventHandler<MediaErrorEvent> getOnError() {
return onError == null ? null : onError.get();
}
public final ObjectProperty<EventHandler<MediaErrorEvent>> onErrorProperty() {
if (onError == null) {
onError = new ObjectPropertyBase<EventHandler<MediaErrorEvent>>() {
@Override
protected void invalidated() {
setEventHandler(MediaErrorEvent.MEDIA_ERROR, get());
}
@Override
public Object getBean() {
return MediaView.this;
}
@Override
public String getName() {
return "onError";
}
};
}
return onError;
}
private BooleanProperty preserveRatio;
public final void setPreserveRatio(boolean value) {
preserveRatioProperty().set(value);
};
public final boolean isPreserveRatio() {
return preserveRatio == null ? true : preserveRatio.get();
}
public final BooleanProperty preserveRatioProperty() {
if (preserveRatio == null) {
preserveRatio = new BooleanPropertyBase(true) {
@Override
protected void invalidated() {
if (HostUtils.isIOS()) {
updateOverlayPreserveRatio();
}
else {
NodeHelper.markDirty(MediaView.this, DirtyBits.NODE_VIEWPORT);
NodeHelper.geomChanged(MediaView.this);
}
}
@Override
public Object getBean() {
return MediaView.this;
}
@Override
public String getName() {
return "preserveRatio";
}
};
}
return preserveRatio;
}
private BooleanProperty smooth;
public final void setSmooth(boolean value) {
smoothProperty().set(value);
}
public final boolean isSmooth() {
return smooth == null ? false : smooth.get();
}
public final BooleanProperty smoothProperty() {
if (smooth == null) {
smooth = new BooleanPropertyBase() {
@Override
protected void invalidated() {
NodeHelper.markDirty(MediaView.this, DirtyBits.NODE_SMOOTH);
}
@Override
public Object getBean() {
return MediaView.this;
}
@Override
public String getName() {
return "smooth";
}
};
}
return smooth;
}
private DoubleProperty x;
public final void setX(double value) {
xProperty().set(value);
}
public final double getX() {
return x == null ? 0.0 : x.get();
}
public final DoubleProperty xProperty() {
if (x == null) {
x = new DoublePropertyBase() {
@Override
protected void invalidated() {
if (HostUtils.isIOS()) {
updateOverlayX();
}
else {
NodeHelper.markDirty(MediaView.this, DirtyBits.NODE_GEOMETRY);
NodeHelper.geomChanged(MediaView.this);
}
}
@Override
public Object getBean() {
return MediaView.this;
}
@Override
public String getName() {
return "x";
}
};
}
return x;
}
private DoubleProperty y;
public final void setY(double value) {
yProperty().set(value);
}
public final double getY() {
return y == null ? 0.0 : y.get();
}
public final DoubleProperty yProperty() {
if (y == null) {
y = new DoublePropertyBase() {
@Override
protected void invalidated() {
if (HostUtils.isIOS()) {
updateOverlayY();
}
else {
NodeHelper.markDirty(MediaView.this, DirtyBits.NODE_GEOMETRY);
NodeHelper.geomChanged(MediaView.this);
}
}
@Override
public Object getBean() {
return MediaView.this;
}
@Override
public String getName() {
return "y";
}
};
}
return y;
}
private DoubleProperty fitWidth;
public final void setFitWidth(double value) {
fitWidthProperty().set(value);
}
public final double getFitWidth() {
return fitWidth == null ? 0.0 : fitWidth.get();
}
public final DoubleProperty fitWidthProperty() {
if (fitWidth == null) {
fitWidth = new DoublePropertyBase() {
@Override
protected void invalidated() {
if (HostUtils.isIOS()) {
updateOverlayWidth();
}
else {
NodeHelper.markDirty(MediaView.this, DirtyBits.NODE_VIEWPORT);
NodeHelper.geomChanged(MediaView.this);
}
}
@Override
public Object getBean() {
return MediaView.this;
}
@Override
public String getName() {
return "fitWidth";
}
};
}
return fitWidth;
}
private DoubleProperty fitHeight;
public final void setFitHeight(double value) {
fitHeightProperty().set(value);
};
public final double getFitHeight() {
return fitHeight == null ? 0.0 : fitHeight.get();
}
public final DoubleProperty fitHeightProperty() {
if (fitHeight == null) {
fitHeight = new DoublePropertyBase() {
@Override
protected void invalidated() {
if (HostUtils.isIOS()) {
updateOverlayHeight();
}
else {
NodeHelper.markDirty(MediaView.this, DirtyBits.NODE_VIEWPORT);
NodeHelper.geomChanged(MediaView.this);
}
}
@Override
public Object getBean() {
return MediaView.this;
}
@Override
public String getName() {
return "fitHeight";
}
};
}
return fitHeight;
}
private ObjectProperty<Rectangle2D> viewport;
public final void setViewport(Rectangle2D value) {
viewportProperty().set(value);
};
public final Rectangle2D getViewport() {
return viewport == null ? null : viewport.get();
}
public final ObjectProperty<Rectangle2D> viewportProperty() {
if (viewport == null) {
viewport = new ObjectPropertyBase<Rectangle2D>() {
@Override
protected void invalidated() {
NodeHelper.markDirty(MediaView.this, DirtyBits.NODE_VIEWPORT);
NodeHelper.geomChanged(MediaView.this);
}
@Override
public Object getBean() {
return MediaView.this;
}
@Override
public String getName() {
return "viewport";
}
};
}
return viewport;
}
void notifyMediaChange() {
MediaPlayer player = getMediaPlayer();
if (player != null) {
final NGMediaView peer = NodeHelper.getPeer(this);
peer.setMediaProvider(player);
}
NodeHelper.markDirty(this, DirtyBits.MEDIAVIEW_MEDIA);
NodeHelper.geomChanged(this);
}
void notifyMediaSizeChange() {
NodeHelper.markDirty(this, DirtyBits.NODE_VIEWPORT);
NodeHelper.geomChanged(this);
}
void notifyMediaFrameUpdated() {
decodedFrameCount++;
NodeHelper.markDirty(this, DirtyBits.NODE_CONTENTS);
}
private NGNode doCreatePeer() {
NGMediaView peer = new NGMediaView();
peer.setFrameTracker(new MediaViewFrameTracker());
return peer;
}
private BaseBounds doComputeGeomBounds(BaseBounds bounds, BaseTransform tx) {
Media media = (getMediaPlayer() == null) ? null : getMediaPlayer().getMedia();
double w = media != null ? media.getWidth() : 0;
double h = media != null ? media.getHeight() : 0;
double newW = getFitWidth();
double newH = getFitHeight();
final double vw = getViewport() != null ? getViewport().getWidth() : 0;
final double vh = getViewport() != null ? getViewport().getHeight() : 0;
if (vw > 0 && vh > 0) {
w = vw;
h = vh;
}
if (getFitWidth() <= 0.0 && getFitHeight() <= 0.0) {
newW = w;
newH = h;
} else if (isPreserveRatio()) {
if (getFitWidth() <= 0.0) {
newW = h > 0 ? w * (getFitHeight() / h) : 0.0F;
newH = getFitHeight();
} else if (getFitHeight() <= 0.0) {
newW = getFitWidth();
newH = w > 0 ? h * (getFitWidth() / w) : 0.0F;
} else {
if (w == 0.0) w = getFitWidth();
if (h == 0.0) h = getFitHeight();
double scale = Math.min(getFitWidth() / w, getFitHeight() / h);
newW = w * scale;
newH = h * scale;
}
} else if (getFitHeight() <= 0.0) {
newH = h;
} else if (getFitWidth() <= 0.0) {
newW = w;
}
if (newH < 1.0F) {
newH = 1.0F;
}
if (newW < 1.0F) {
newW = 1.0F;
}
w = newW;
h = newH;
if (w <= 0 || h <= 0) {
return bounds.makeEmpty();
}
bounds = bounds.deriveWithNewBounds((float)getX(), (float)getY(), 0.0f,
(float)(getX()+w), (float)(getY()+h), 0.0f);
bounds = tx.transform(bounds, bounds);
return bounds;
}
private boolean doComputeContains(double localX, double localY) {
return true;
}
void updateViewport() {
if (getMediaPlayer() == null) {
return;
}
final NGMediaView peer = NodeHelper.getPeer(this);
if (getViewport() != null) {
peer.setViewport((float)getFitWidth(), (float)getFitHeight(),
(float)getViewport().getMinX(), (float)getViewport().getMinY(),
(float)getViewport().getWidth(), (float)getViewport().getHeight(),
isPreserveRatio());
} else {
peer.setViewport((float)getFitWidth(), (float)getFitHeight(),
0.0F, 0.0F, 0.0F, 0.0F,
isPreserveRatio());
}
}
private void doUpdatePeer() {
final NGMediaView peer = NodeHelper.getPeer(this);
if (NodeHelper.isDirty(this, DirtyBits.NODE_GEOMETRY)) {
peer.setX((float)getX());
peer.setY((float)getY());
}
if (NodeHelper.isDirty(this, DirtyBits.NODE_SMOOTH)) {
peer.setSmooth(isSmooth());
}
if (NodeHelper.isDirty(this, DirtyBits.NODE_VIEWPORT)) {
updateViewport();
}
if (NodeHelper.isDirty(this, DirtyBits.NODE_CONTENTS)) {
peer.renderNextFrame();
}
if (NodeHelper.isDirty(this, DirtyBits.MEDIAVIEW_MEDIA)) {
MediaPlayer player = getMediaPlayer();
if (player != null) {
peer.setMediaProvider(player);
updateViewport();
} else {
peer.setMediaProvider(null);
}
}
}
private int decodedFrameCount;
private int renderedFrameCount;
void perfReset() {
decodedFrameCount = 0;
renderedFrameCount = 0;
}
int perfGetDecodedFrameCount() {
return decodedFrameCount;
}
int perfGetRenderedFrameCount() {
return renderedFrameCount;
}
private class MediaViewFrameTracker implements MediaFrameTracker {
@Override
public void incrementDecodedFrameCount(int count) {
decodedFrameCount += count;
}
@Override
public void incrementRenderedFrameCount(int count) {
renderedFrameCount += count;
}
}
void _mediaPlayerOnReady() {
com.sun.media.jfxmedia.MediaPlayer jfxPlayer = getMediaPlayer().retrieveJfxPlayer();
if (jfxPlayer != null) {
if (decodedFrameRateListener != null && registerVideoFrameRateListener) {
jfxPlayer.getVideoRenderControl().addVideoFrameRateListener(decodedFrameRateListener);
registerVideoFrameRateListener = false;
}
mediaPlayerOverlay = jfxPlayer.getMediaPlayerOverlay();
if (mediaPlayerOverlay != null) {
createListeners();
parentProperty().addListener(parentListener);
NodeHelper.treeVisibleProperty(this).addListener(treeVisibleListener);
opacityProperty().addListener(opacityListener);
synchronized (this) {
updateMediaPlayerOverlay();
}
}
}
}
}
