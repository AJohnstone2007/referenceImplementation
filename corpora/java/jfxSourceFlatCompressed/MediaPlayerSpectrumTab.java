package fxmediaplayer.control;
import fxmediaplayer.FXMediaPlayerControlInterface;
import fxmediaplayer.FXMediaPlayerInterface;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.event.Event;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioSpectrumListener;
import javafx.scene.media.MediaPlayer;
public class MediaPlayerSpectrumTab implements FXMediaPlayerControlInterface {
private FXMediaPlayerInterface FXMediaPlayer = null;
private Tab spectrumTab = null;
private AudioSpectrumListenerImpl listener = null;
private XYChart.Data<String, Number>[] seriesData;
private InvalidationListener statusPropertyListener = null;
public MediaPlayerSpectrumTab(FXMediaPlayerInterface FXMediaPlayer) {
this.FXMediaPlayer = FXMediaPlayer;
}
public Tab getSpectrumTab() {
if (spectrumTab == null) {
spectrumTab = new Tab();
spectrumTab.setText("Spectrum");
spectrumTab.setOnSelectionChanged((Event t) -> {
onSelectionChanged();
});
VBox vBox = new VBox();
vBox.setId("mediaPlayerTab");
vBox.getChildren().add(createBarChart());
spectrumTab.setContent(vBox);
}
return spectrumTab;
}
@Override
public void onMediaPlayerChanged(MediaPlayer oldMediaPlayer) {
removeListeners(oldMediaPlayer);
addListeners();
}
@SuppressWarnings("unchecked")
private void addListeners() {
if (FXMediaPlayer.getMediaPlayer() == null) {
return;
}
statusPropertyListener = (Observable o) -> {
ReadOnlyObjectProperty<MediaPlayer.Status> prop =
(ReadOnlyObjectProperty<MediaPlayer.Status>) o;
MediaPlayer.Status status = prop.getValue();
if (status == MediaPlayer.Status.READY) {
spectrumTab.setDisable(false);
} else if (status == MediaPlayer.Status.DISPOSED ||
status == MediaPlayer.Status.HALTED) {
spectrumTab.setDisable(true);
}
};
FXMediaPlayer.getMediaPlayer()
.statusProperty().addListener(statusPropertyListener);
}
private void removeListeners(MediaPlayer mediaPlayer) {
if (mediaPlayer == null) {
return;
}
mediaPlayer.statusProperty()
.removeListener(statusPropertyListener);
}
private void onSelectionChanged() {
if (FXMediaPlayer.getMediaPlayer() == null) {
return;
}
if (spectrumTab.isSelected()) {
if (listener == null) {
listener = new AudioSpectrumListenerImpl();
}
FXMediaPlayer.getMediaPlayer().setAudioSpectrumListener(listener);
} else {
FXMediaPlayer.getMediaPlayer().setAudioSpectrumListener(null);
}
}
@SuppressWarnings("unchecked")
private Chart createBarChart() {
final CategoryAxis xAxis = new CategoryAxis();
final NumberAxis yAxis = new NumberAxis(0, 50, 10);
final BarChart<String, Number> bc = new BarChart<>(xAxis, yAxis);
bc.setId("mediaPlayerSpectrum");
bc.setLegendVisible(false);
bc.setAnimated(false);
bc.setBarGap(0);
bc.setCategoryGap(1);
bc.setVerticalGridLinesVisible(false);
yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis, null, "dB"));
XYChart.Series<String, Number> series = new XYChart.Series<>();
series.setName("Data Series 1");
seriesData = new XYChart.Data[128];
String[] categories = new String[128];
for (int i = 0; i < seriesData.length; i++) {
categories[i] = Integer.toString(i + 1);
seriesData[i] = new XYChart.Data<>(categories[i], 50);
series.getData().add(seriesData[i]);
}
bc.getData().add(series);
return bc;
}
private class AudioSpectrumListenerImpl implements AudioSpectrumListener {
@Override
public void spectrumDataUpdate(double timestamp, double duration,
float[] magnitudes, float[] phases) {
for (int i = 0; i < seriesData.length; i++) {
seriesData[i].setYValue(magnitudes[i] + 60);
}
}
}
}
