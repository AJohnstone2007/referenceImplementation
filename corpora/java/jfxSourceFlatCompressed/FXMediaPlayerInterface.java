package fxmediaplayer;
import javafx.scene.image.Image;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
public interface FXMediaPlayerInterface {
public void onSourceChanged(String source);
public void onImageAvailable(Image image);
public void setAutoPlay(boolean autoPlay);
public void setFullScreen(boolean isFullScreen);
public MediaPlayer getMediaPlayer();
public MediaView getMediaView();
public void setScrubbing(boolean isScrubbingOn);
public boolean getScrubbing();
}
