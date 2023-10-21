package ensemble.samples.charts.bar.audio;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.NumberAxis.DefaultFormatter;
import javafx.scene.chart.XYChart;
import javafx.scene.media.AudioSpectrumListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
public class AudioBarChartApp extends Application {
private XYChart.Data<String, Number>[] series1Data;
private AudioSpectrumListener audioSpectrumListener;
private static final String AUDIO_URI =
System.getProperty("demo.audio.url",
"https://download.oracle.com/otndocs/products/javafx/oow2010-2.mp4");
private MediaPlayer audioMediaPlayer;
private static final boolean PLAY_AUDIO = Boolean.parseBoolean(
System.getProperty("demo.play.audio", "true"));
public AudioBarChartApp() {
audioSpectrumListener = (double timestamp, double duration,
float[] magnitudes, float[] phases) -> {
for (int i = 0; i < series1Data.length; i++) {
series1Data[i].setYValue(magnitudes[i] + 60);
}
};
}
public void play() {
this.startAudio();
}
@Override
public void stop() {
this.stopAudio();
}
public Parent createContent() {
final CategoryAxis xAxis = new CategoryAxis();
final NumberAxis yAxis = new NumberAxis(0, 50, 10);
final BarChart<String, Number> bc = new BarChart<>(xAxis, yAxis);
final String audioBarChartCss =
getClass().getResource("AudioBarChart.css").toExternalForm();
bc.getStylesheets().add(audioBarChartCss);
bc.setLegendVisible(false);
bc.setAnimated(false);
bc.setBarGap(0);
bc.setCategoryGap(1);
bc.setVerticalGridLinesVisible(false);
bc.setTitle("Live Audio Spectrum Data");
xAxis.setLabel("Frequency Bands");
yAxis.setLabel("Magnitudes");
yAxis.setTickLabelFormatter(new DefaultFormatter(yAxis, null, "dB"));
XYChart.Series<String, Number> series1 = new XYChart.Series<>();
series1.setName("Data Series 1");
series1Data = new XYChart.Data[128];
String[] categories = new String[128];
for (int i = 0; i < series1Data.length; i++) {
categories[i] = Integer.toString(i + 1);
series1Data[i] = new XYChart.Data<String, Number>(categories[i], 50);
series1.getData().add(series1Data[i]);
}
bc.getData().add(series1);
return bc;
}
private void startAudio() {
if (PLAY_AUDIO) {
getAudioMediaPlayer()
.setAudioSpectrumListener(audioSpectrumListener);
getAudioMediaPlayer().play();
}
}
private void stopAudio() {
if (getAudioMediaPlayer().getAudioSpectrumListener() == audioSpectrumListener) {
getAudioMediaPlayer().pause();
}
}
private MediaPlayer getAudioMediaPlayer() {
if (audioMediaPlayer == null) {
Media audioMedia = new Media(AUDIO_URI);
audioMediaPlayer = new MediaPlayer(audioMedia);
audioMediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
}
return audioMediaPlayer;
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
