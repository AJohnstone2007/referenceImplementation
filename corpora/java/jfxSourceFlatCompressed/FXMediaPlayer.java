package fxmediaplayer;
import fxmediaplayer.control.MediaPlayerControl;
import fxmediaplayer.info.MediaPlayerInfo;
import fxmediaplayer.menu.MediaPlayerMenu;
import fxmediaplayer.states.MediaPlayerStates;
import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Application;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
public class FXMediaPlayer extends Application implements FXMediaPlayerInterface {
private Stage stage = null;
private Scene scene = null;
private BorderPane pane = null;
private Media media = null;
private MediaPlayer mediaPlayer = null;
private MediaView mediaView = null;
private ImageView imageView = null;
private boolean autoPlay = false;
private MediaPlayerMenu menu = null;
private MediaPlayerStates states = null;
private MediaPlayerInfo info = null;
private MediaPlayerControl control = null;
private final Color sceneColor = Color.web("#F0F0F0");
private final Color sceneFullScreenColor = Color.BLACK;
private Timer cursorTimer = null;
private long lastMouseMovedTime = 0;
private boolean isScrubbingOn = false;
public static void main(String[] args) {
Application.launch(args);
}
@Override
public void start(Stage stage) {
this.stage = stage;
stage.setTitle("FX Media Player");
stage.fullScreenProperty().addListener(
(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) -> {
onFullScreen();
});
pane = new BorderPane();
onSourceChanged(MediaPlayerDefaults.DEFAULT_SOURCE);
scene = new Scene(pane, 1280, 720);
scene.setFill(sceneColor);
scene.getStylesheets().add(
FXMediaPlayer.class.getResource("main.css").toExternalForm());
scene.getStylesheets().add(
FXMediaPlayer.class.getResource("spectrum.css").toExternalForm());
scene.widthProperty().addListener((Observable o) -> {
onSceneWidth();
});
scene.heightProperty().addListener((Observable o) -> {
onSceneHeight();
});
scene.setOnDragOver((DragEvent event) -> {
event.acceptTransferModes(TransferMode.ANY);
});
scene.setOnDragDropped((DragEvent event) -> {
onSceneDragDropped(event);
});
scene.setOnMouseMoved((MouseEvent me) -> {
onSceneMouseMoved();
});
stage.setScene(scene);
stage.show();
layoutChildren();
}
@Override
public synchronized void onSourceChanged(String source) {
if (source == null) {
media = null;
if (mediaPlayer != null) {
mediaPlayer.stop();
if (states != null) {
states.onMediaPlayerChanged(mediaPlayer);
}
if (info != null) {
info.onMediaPlayerChanged(mediaPlayer);
}
if (control != null) {
control.onMediaPlayerChanged(mediaPlayer);
}
mediaPlayer.dispose();
mediaPlayer = null;
}
if (mediaView != null) {
mediaView.setMediaPlayer(null);
}
return;
}
MediaPlayer oldMediaPlayer = mediaPlayer;
if (mediaPlayer != null) {
mediaPlayer.stop();
}
if (mediaView != null) {
mediaView.setMediaPlayer(null);
}
media = new Media(source);
mediaPlayer = new MediaPlayer(media);
if (mediaView == null) {
mediaView = new MediaView();
mediaView.setOnMouseClicked((MouseEvent t) -> {
onViewMouseClicked();
});
}
if (imageView == null) {
imageView = new ImageView();
imageView.setPreserveRatio(true);
imageView.setOnMouseClicked((MouseEvent t) -> {
onViewMouseClicked();
});
}
mediaPlayer.setAutoPlay(autoPlay);
mediaView.setMediaPlayer(mediaPlayer);
pane.setCenter(mediaView);
if (menu == null) {
menu = new MediaPlayerMenu(this);
pane.setTop(menu.getMenu());
}
if (states == null) {
states = new MediaPlayerStates(this);
pane.setLeft(states.getStates());
}
if (info == null) {
info = new MediaPlayerInfo(this);
pane.setRight(info.getInfo());
}
if (control == null) {
control = new MediaPlayerControl(this);
pane.setBottom(control.getControl());
}
states.onMediaPlayerChanged(oldMediaPlayer);
info.onMediaPlayerChanged(oldMediaPlayer);
control.onMediaPlayerChanged(oldMediaPlayer);
if (oldMediaPlayer != null) {
oldMediaPlayer.dispose();
}
layoutChildren();
}
@Override
public synchronized void onImageAvailable(Image image) {
imageView.setImage(image);
pane.setCenter(imageView);
layoutChildren();
}
@Override
public void setAutoPlay(boolean autoPlay) {
this.autoPlay = autoPlay;
}
@Override
public void setFullScreen(boolean isFullScreen) {
stage.setFullScreen(isFullScreen);
}
@Override
public MediaPlayer getMediaPlayer() {
return mediaPlayer;
}
@Override
public MediaView getMediaView() {
return mediaView;
}
@Override
public void setScrubbing(boolean isScrubbingOn) {
this.isScrubbingOn = isScrubbingOn;
}
@Override
public boolean getScrubbing() {
return isScrubbingOn;
}
private void onFullScreen() {
if (stage.isFullScreen()) {
pane.setTop(null);
pane.setLeft(null);
pane.setRight(null);
pane.setBottom(null);
scene.setFill(sceneFullScreenColor);
scene.setCursor(Cursor.NONE);
cursorTimer = new Timer(true);
cursorTimer.scheduleAtFixedRate(new CursorTimerTask(this), 1000, 1000);
layoutChildren();
} else {
pane.setTop(menu.getMenu());
pane.setLeft(states.getStates());
pane.setRight(info.getInfo());
pane.setBottom(control.getControl());
scene.setFill(sceneColor);
scene.setCursor(Cursor.DEFAULT);
if (cursorTimer != null) {
cursorTimer.cancel();
cursorTimer = null;
}
layoutChildren();
}
}
private void onViewMouseClicked() {
if (stage.isFullScreen()) {
if (pane.getBottom() == null) {
pane.setBottom(control.getControl());
layoutChildren();
} else {
pane.setBottom(null);
layoutChildren();
}
}
}
private void onSceneWidth() {
layoutChildren();
}
private void onSceneHeight() {
layoutChildren();
}
private void onSceneDragDropped(DragEvent event) {
event.setDropCompleted(true);
String source = FXMediaPlayerUtils.getSourceFromDragboard(event.getDragboard());
if (source != null) {
onSourceChanged(source);
}
}
private void onSceneMouseMoved() {
if (stage.isFullScreen()) {
scene.setCursor(Cursor.DEFAULT);
lastMouseMovedTime = System.currentTimeMillis();
}
}
private synchronized void layoutChildren() {
if (scene == null) {
return;
}
double sceneWidth = scene.getWidth();
double sceneHeight = scene.getHeight();
double leftWidth = 0.0;
double rightWidth = 0.0;
if (pane.getLeft() != null) {
leftWidth = states.getStates().getWidth();
}
if (pane.getRight() != null) {
rightWidth = info.getInfo().getWidth();
}
double topHeight = 0.0;
double bottomHeight = 0.0;
if (pane.getTop() != null) {
topHeight = menu.getMenu().getHeight();
}
if (pane.getBottom() != null) {
bottomHeight = control.getControl().getHeight();
}
double fitWidth = sceneWidth - leftWidth - rightWidth;
double fitHeight = sceneHeight - topHeight - bottomHeight;
if (pane.getCenter() != null) {
if (pane.getCenter() instanceof MediaView) {
mediaView.setFitWidth(fitWidth);
mediaView.setFitHeight(fitHeight);
} else if (pane.getCenter() instanceof ImageView) {
imageView.setFitWidth(fitWidth);
imageView.setFitHeight(fitHeight);
}
}
}
class CursorTimerTask extends TimerTask {
WeakReference<FXMediaPlayer> playerRef;
CursorTimerTask(FXMediaPlayer player) {
playerRef = new WeakReference<>(player);
}
@Override
public void run() {
final FXMediaPlayer player = playerRef.get();
if (player != null) {
if (player.stage.isFullScreen()) {
if (System.currentTimeMillis() - lastMouseMovedTime > 7000) {
player.scene.setCursor(Cursor.NONE);
}
}
} else {
cancel();
}
}
}
}
