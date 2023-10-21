package ensemble.samples.media.overlaymediaplayer;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
public class OverlayMediaPlayerApp extends Application {
private MediaPlayer mediaPlayer;
public Parent createContent() {
final String MEDIA_URL =
"https://download.oracle.com/otndocs/javafx/" +
"JavaRap_ProRes_H264_768kbit_Widescreen.mp4";
final String overlayMediaPlayerCss =
getClass().getResource("OverlayMediaPlayer.css").toExternalForm();
final double mediaWidth = 480;
final double mediaHeight = 270;
mediaPlayer = new MediaPlayer(new Media(MEDIA_URL));
mediaPlayer.setAutoPlay(true);
PlayerPane playerPane = new PlayerPane(mediaPlayer);
playerPane.setMinSize(mediaWidth, mediaHeight);
playerPane.setPrefSize(mediaWidth, mediaHeight);
playerPane.setMaxSize(mediaWidth, mediaHeight);
playerPane.getStylesheets().add(overlayMediaPlayerCss);
return playerPane;
}
public void play() {
MediaPlayer.Status status = mediaPlayer.getStatus();
if (status == MediaPlayer.Status.UNKNOWN ||
status == MediaPlayer.Status.HALTED) {
return;
}
if (status == MediaPlayer.Status.PAUSED ||
status == MediaPlayer.Status.STOPPED ||
status == MediaPlayer.Status.READY) {
mediaPlayer.play();
}
}
@Override
public void stop() {
mediaPlayer.stop();
}
@Override
public void start(Stage primaryStage) throws Exception {
primaryStage.setScene(new Scene(createContent()));
primaryStage.show();
play();
}
public static void main(String[] args) {
launch(args);
}
}
