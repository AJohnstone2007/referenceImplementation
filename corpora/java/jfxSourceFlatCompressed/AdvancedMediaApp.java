package ensemble.samples.media.advancedmedia;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
public class AdvancedMediaApp extends Application {
private static final String MEDIA_URL =
"https://download.oracle.com/otndocs/products/javafx/oow2010-2.mp4";
private MediaPlayer mediaPlayer;
private MediaControl mediaControl;
public Parent createContent() {
mediaPlayer = new MediaPlayer(new Media(MEDIA_URL));
mediaPlayer.setAutoPlay(true);
mediaControl = new MediaControl(mediaPlayer);
mediaControl.setMinSize(480, 280);
mediaControl.setPrefSize(480, 280);
mediaControl.setMaxSize(480, 280);
return mediaControl;
}
@Override
public void start(Stage primaryStage) throws Exception {
primaryStage.setScene(new Scene(createContent()));
primaryStage.show();
}
@Override
public void stop() {
mediaPlayer.stop();
}
public static void main(String[] args) {
launch(args);
}
}
