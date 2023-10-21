package fxmediaplayer.control;
import fxmediaplayer.FXMediaPlayerControlInterface;
import fxmediaplayer.FXMediaPlayerInterface;
import fxmediaplayer.FXMediaPlayerUtils;
import java.util.HashMap;
import java.util.Set;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaMarkerEvent;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.util.Pair;
public class MediaPlayerMarkersTab implements FXMediaPlayerControlInterface {
private static final int DEFAULT_NUM_OF_MARKERS = 7;
private static final double ON_OPACITY = 1.0;
private static final double OFF_OPACITY = 0.1;
private FXMediaPlayerInterface FXMediaPlayer = null;
private Tab markersTab = null;
private ToggleButton buttonEnable = null;
private Label addMarkerLabel = null;
private TextField markerTimeTextField = null;
private Button buttonAdd = null;
private HBox markersInfoContainer = null;
private HashMap<String, Duration> markers = null;
private HashMap<String, Label> markerLabels = null;
private InvalidationListener durationPropertyListener = null;
private EventHandler<MediaMarkerEvent> onMarkerListener = null;
private InvalidationListener statusPropertyListener = null;
public MediaPlayerMarkersTab(FXMediaPlayerInterface FXMediaPlayer) {
this.FXMediaPlayer = FXMediaPlayer;
}
public Tab getMarkersTab() {
if (markersTab == null) {
markersTab = new Tab();
markersTab.setText("Markers");
VBox markersTabContent = new VBox();
markersTabContent.setId("mediaPlayerTab");
markersTabContent.setAlignment(Pos.TOP_CENTER);
ToolBar toolBar = new ToolBar();
buttonEnable = new ToggleButton("Enable");
buttonEnable.setOnAction((ActionEvent event) -> {
onButtonEnable();
});
toolBar.getItems().add(buttonEnable);
markerTimeTextField = new TextField();
addMarkerLabel = new Label("Time (ms):", markerTimeTextField);
addMarkerLabel.setContentDisplay(ContentDisplay.RIGHT);
addMarkerLabel.setDisable(true);
toolBar.getItems().add(addMarkerLabel);
buttonAdd = new Button("Add");
buttonAdd.setOnAction((ActionEvent event) -> {
onButtonAdd();
});
buttonAdd.setDisable(true);
toolBar.getItems().add(buttonAdd);
markersTabContent.getChildren().add(toolBar);
markersInfoContainer = new HBox(10);
markersInfoContainer.setAlignment(Pos.CENTER);
markersInfoContainer.setPadding(new Insets(20, 0, 0, 0));
markersTabContent.getChildren().add(markersInfoContainer);
markersTab.setContent(markersTabContent);
createListeners();
addListeners();
}
return markersTab;
}
@Override
public void onMediaPlayerChanged(MediaPlayer oldMediaPlayer) {
if (oldMediaPlayer != null) {
removeListeners(oldMediaPlayer);
removeMarkers(oldMediaPlayer);
}
addListeners();
}
@SuppressWarnings("unchecked")
private void createListeners() {
durationPropertyListener = (Observable o) -> {
ReadOnlyObjectProperty property = (ReadOnlyObjectProperty) o;
Duration duration = ((Duration) property.getValue());
if (duration.toMillis() > 0) {
createMarkers(duration);
}
};
onMarkerListener = (MediaMarkerEvent event) -> {
if (event != null) {
Pair<String, Duration> pair = event.getMarker();
Label markerLabel = markerLabels.get(pair.getKey());
if (markerLabel != null) {
markerOFFON(markerLabel);
}
}
};
statusPropertyListener = (Observable o) -> {
ReadOnlyObjectProperty<MediaPlayer.Status> prop =
(ReadOnlyObjectProperty<MediaPlayer.Status>) o;
MediaPlayer.Status status = prop.getValue();
if (status == MediaPlayer.Status.READY) {
markersTab.setDisable(false);
} else if (status == MediaPlayer.Status.DISPOSED ||
status == MediaPlayer.Status.HALTED) {
markersTab.setDisable(true);
}
};
}
private void addListeners() {
if (FXMediaPlayer.getMediaPlayer() == null) {
return;
}
FXMediaPlayer.getMediaPlayer().getMedia()
.durationProperty().addListener(durationPropertyListener);
FXMediaPlayer.getMediaPlayer()
.setOnMarker(onMarkerListener);
FXMediaPlayer.getMediaPlayer()
.statusProperty().addListener(statusPropertyListener);
}
private void removeListeners(MediaPlayer mediaPlayer) {
if (mediaPlayer == null) {
return;
}
mediaPlayer.getMedia()
.durationProperty().removeListener(durationPropertyListener);
mediaPlayer.setOnMarker(null);
mediaPlayer.statusProperty()
.removeListener(statusPropertyListener);
}
private void onButtonEnable() {
if (buttonEnable.isSelected()) {
buttonEnable.setText("Enabled");
addMarkerLabel.setDisable(false);
buttonAdd.setDisable(false);
addMarkers();
} else {
buttonEnable.setText("Enable");
addMarkerLabel.setDisable(true);
buttonAdd.setDisable(true);
removeMarkers(FXMediaPlayer.getMediaPlayer());
}
if (markerLabels != null) {
Set<String> keys = markerLabels.keySet();
for (String key : keys) {
Label label = markerLabels.get(key);
if (buttonEnable.isSelected()) {
label.setDisable(false);
} else {
label.setDisable(true);
}
}
}
}
private void onButtonAdd() {
if (markerTimeTextField.getText() != null && !markerTimeTextField.getText().isEmpty()) {
double millis = Double.parseDouble(markerTimeTextField.getText());
addMarker(new Duration(millis), true);
markerTimeTextField.setText("");
}
}
private void addMarker(Duration markerDuration, boolean addToPlayer) {
String key = FXMediaPlayerUtils.millisToString(markerDuration.toMillis());
markers.put(key, markerDuration);
Rectangle rect = new Rectangle(0, 0, 100, 100);
rect.setArcHeight(20);
rect.setArcWidth(20);
rect.setFill(Color.LIGHTGRAY);
Label label = new Label(key, rect);
label.setContentDisplay(ContentDisplay.CENTER);
if (buttonEnable.isSelected()) {
label.setDisable(false);
} else {
label.setDisable(true);
}
markerLabels.put(key, label);
markersInfoContainer.getChildren().add(label);
if (addToPlayer) {
FXMediaPlayer.getMediaPlayer().getMedia().getMarkers().put(key, markerDuration);
}
}
private void createMarkers(Duration duration) {
markersInfoContainer.getChildren().clear();
markers = new HashMap<>();
markerLabels = new HashMap<>();
double millis = duration.toMillis();
millis /= DEFAULT_NUM_OF_MARKERS;
Duration millisDuration = new Duration(millis);
Duration markerDuration = new Duration(0);
for (int i = 0; i < DEFAULT_NUM_OF_MARKERS; i++) {
addMarker(markerDuration, false);
markerDuration = markerDuration.add(millisDuration);
}
onButtonEnable();
}
private void addMarkers() {
FXMediaPlayer.getMediaPlayer().getMedia().getMarkers().putAll(markers);
}
private void removeMarkers(MediaPlayer oldMediaPlayer) {
oldMediaPlayer.getMedia().getMarkers().clear();
}
private void markerOFFON(Label label) {
SequentialTransition st = new SequentialTransition();
FadeTransition ftOFF = new FadeTransition(Duration.millis(500), label);
ftOFF.setFromValue(ON_OPACITY);
ftOFF.setToValue(OFF_OPACITY);
FadeTransition ftON = new FadeTransition(Duration.millis(500), label);
ftON.setFromValue(OFF_OPACITY);
ftON.setToValue(ON_OPACITY);
st.getChildren().addAll(ftOFF, ftON);
st.play();
}
}
