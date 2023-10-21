package fxmediaplayer.menu;
import fxmediaplayer.FXMediaPlayerInterface;
import java.io.File;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
public class MediaPlayerMenuOpenDialog {
private static final int WIDTH = 500;
private static final int HEIGHT = 100;
private FXMediaPlayerInterface FXMediaPlayer = null;
private Stage stage = null;
private Scene scene = null;
private TextField textField = null;
private Button buttonBrowse = null;
private Button buttonOpen = null;
private Button buttonCancel = null;
private FileChooser fileChooser = null;
public MediaPlayerMenuOpenDialog(FXMediaPlayerInterface FXMediaPlayer) {
this.FXMediaPlayer = FXMediaPlayer;
}
public void open() {
if (stage == null) {
stage = new Stage();
stage.setTitle("Open file");
stage.initModality(Modality.APPLICATION_MODAL);
stage.setResizable(false);
stage.setOnCloseRequest((WindowEvent event) -> {
onCloseRequest();
});
scene = new Scene(getLayout(), WIDTH, HEIGHT);
scene.setFill(Color.web("#F0F0F0"));
stage.setScene(scene);
}
stage.show();
}
private VBox getLayout() {
VBox vBox = new VBox(10);
Label label = new Label("Enter URL or browse for local files:");
vBox.getChildren().add(label);
textField = new TextField();
vBox.getChildren().add(textField);
HBox hBox = new HBox(25);
hBox.setAlignment(Pos.CENTER);
buttonBrowse = new Button("Browse");
buttonBrowse.setOnAction((ActionEvent event) -> {
onButtonBrowse();
});
buttonOpen = new Button("Open");
buttonOpen.setOnAction((ActionEvent event) -> {
onButtonOpen();
});
buttonCancel = new Button("Cancel");
buttonCancel.setOnAction((ActionEvent event) -> {
onButtonCancel();
});
hBox.getChildren().addAll(buttonBrowse, buttonOpen, buttonCancel);
vBox.getChildren().add(hBox);
return vBox;
}
private void onCloseRequest() {
textField.setText("");
}
private void onButtonBrowse() {
if (fileChooser == null) {
fileChooser = new FileChooser();
}
File file = fileChooser.showOpenDialog(stage);
if (file != null) {
textField.setText(file.toURI().toString());
}
}
private void onButtonOpen() {
if (textField.getText() != null && !textField.getText().isEmpty()) {
FXMediaPlayer.onSourceChanged(textField.getText());
}
stage.close();
textField.setText("");
}
private void onButtonCancel() {
stage.close();
textField.setText("");
}
}
