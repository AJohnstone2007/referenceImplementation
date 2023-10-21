package ensemble.samples.charts.area.audio;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.NumberAxis.DefaultFormatter;
import javafx.scene.chart.XYChart;
import javafx.scene.media.AudioSpectrumListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
public class AudioAreaChartApp extends Application {
private XYChart.Data<Number, Number>[] series1Data;
private AudioSpectrumListener audioSpectrumListener;
private static final String AUDIO_URI = System.getProperty("demo.audio.url",
"https://download.oracle.com/otndocs/javafx/JavaRap_Audio.mp4");
private MediaPlayer audioMediaPlayer;
private static final boolean PLAY_AUDIO = Boolean.parseBoolean(
System.getProperty("demo.play.audio", "true"));
public AudioAreaChartApp() {
audioSpectrumListener = (double timestamp, double duration,
float[] magnitudes, float[] phases) -> {
for (int i = 0; i < series1Data.length; i++) {
series1Data[i].setYValue(magnitudes[i] + 60);
}
};
}
public void play() {
audioMediaPlayer.play();
}
@Override
public void stop() {
audioMediaPlayer.pause();
}
public Parent createContent() {
final NumberAxis xAxis = new NumberAxis(0, 128, 8);
final NumberAxis yAxis = new NumberAxis(0, 50, 10);
final AreaChart<Number, Number> ac = new AreaChart<>(xAxis, yAxis);
final String audioAreaChartCss =
getClass().getResource("AudioAreaChart.css").toExternalForm();
final Media audioMedia = new Media(AUDIO_URI);
audioMediaPlayer = new MediaPlayer(audioMedia);
audioMediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
audioMediaPlayer.setAudioSpectrumListener(audioSpectrumListener);
ac.getStylesheets().add(audioAreaChartCss);
ac.setLegendVisible(false);
ac.setTitle("Live Audio Spectrum Data");
ac.setAnimated(false);
xAxis.setLabel("Frequency Bands");
yAxis.setLabel("Magnitudes");
yAxis.setTickLabelFormatter(new DefaultFormatter(yAxis, null, "dB"));
XYChart.Series<Number, Number> series = new XYChart.Series<>();
series.setName("Audio Spectrum");
series1Data = new XYChart.Data[(int) xAxis.getUpperBound()];
for (int i = 0; i < series1Data.length; i++) {
series1Data[i] = new XYChart.Data<Number, Number>(i, 50);
series.getData().add(series1Data[i]);
}
ac.getData().add(series);
return ac;
}
@Override public void start(Stage primaryStage) throws Exception {
primaryStage.setScene(new Scene(createContent()));
primaryStage.show();
play();
}
public static void main(String[] args) {
launch(args);
}
}
