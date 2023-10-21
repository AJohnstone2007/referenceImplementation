package fxmediaplayer.control;
import fxmediaplayer.FXMediaPlayerControlInterface;
import fxmediaplayer.FXMediaPlayerInterface;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
public class MediaPlayerControl implements FXMediaPlayerControlInterface {
private FXMediaPlayerInterface FXMediaPlayer = null;
private MediaPlayerToolBar toolBar = null;
private MediaPlayerTabControl tabControl = null;
private VBox control = null;
public MediaPlayerControl(FXMediaPlayerInterface FXMediaPlayer) {
this.FXMediaPlayer = FXMediaPlayer;
}
public VBox getControl() {
if (control == null) {
control = new VBox();
toolBar = new MediaPlayerToolBar(FXMediaPlayer);
tabControl = new MediaPlayerTabControl(FXMediaPlayer);
control.getChildren().addAll(toolBar.getToolBar(),
tabControl.getTabControl());
}
return control;
}
@Override
public void onMediaPlayerChanged(MediaPlayer oldMediaPlayer) {
toolBar.onMediaPlayerChanged(oldMediaPlayer);
tabControl.onMediaPlayerChanged(oldMediaPlayer);
}
}
