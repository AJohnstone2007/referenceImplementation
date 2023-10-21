package com.javafx.experiments.height2normal;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.imageio.ImageIO;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import com.javafx.experiments.jfx3dviewer.AutoScalingGroup;
public class Height2NormalApp extends Application {
private Image testImage;
private SimpleObjectProperty<Image> heightImage = new SimpleObjectProperty<>();
private SimpleObjectProperty<Image> normalImage = new SimpleObjectProperty<>();
private File heightFile;
private Stage stage;
@Override public void start(Stage stage) throws Exception {
this.stage = stage;
testImage = new Image(Height2NormalApp.class.getResource("/com/javafx/experiments/jfx3dviewer/blue.jpg").toExternalForm());
heightImage.set(testImage);
ToolBar toolBar = new ToolBar();
Button openButton = new Button("Open...");
openButton.setOnAction(event -> open());
Button saveButton = new Button("Save...");
saveButton.setOnAction(event -> save());
final CheckBox invertCheckBox = new CheckBox("invert");
final Slider scaleSlider = new Slider(1,50,2);
toolBar.getItems().addAll(openButton,saveButton,
new Separator(),
invertCheckBox,
new Separator(),
new Label("Scale:"),scaleSlider);
normalImage.bind(
new ObjectBinding<Image>() {
{ bind(heightImage, invertCheckBox.selectedProperty(), scaleSlider.valueProperty()); }
@Override protected Image computeValue() {
return Height2NormalConverter.convertToNormals(heightImage.get(), invertCheckBox.isSelected(), scaleSlider.getValue());
}
});
VBox root = new VBox();
HBox views = new HBox();
VBox.setVgrow(views, Priority.ALWAYS);
root.getChildren().addAll(toolBar, views);
ImageView srcImageView = new ImageView();
srcImageView.setFitWidth(512);
srcImageView.setFitHeight(512);
srcImageView.imageProperty().bind(heightImage);
ImageView dstImageView = new ImageView();
dstImageView.setFitWidth(512);
dstImageView.setFitHeight(512);
dstImageView.imageProperty().bind(normalImage);
views.getChildren().addAll(srcImageView,dstImageView,new View3D().create());
Scene scene = new Scene(root);
stage.setScene(scene);
stage.show();
}
public void open() {
FileChooser fileChooser = new FileChooser();
heightFile = fileChooser.showOpenDialog(stage);
if (heightFile != null) {
try {
heightImage.set(new Image(heightFile.toURI().toURL().toExternalForm()));
} catch (MalformedURLException e) {
e.printStackTrace();
}
} else {
heightImage.set(testImage);
}
}
public void save() {
FileChooser fileChooser = new FileChooser();
if (heightFile != null) {
String filePath = heightFile.getName();
fileChooser.setInitialFileName(filePath.substring(0,filePath.lastIndexOf('.'))+"-normal-map.png");
} else {
fileChooser.setInitialFileName("normal-map.png");
}
File normalFile = fileChooser.showSaveDialog(stage);
if (normalFile != null) {
try {
ImageIO.write(SwingFXUtils.fromFXImage(normalImage.get(),null),"png",normalFile);
} catch (IOException e) {
e.printStackTrace();
}
}
}
public static void main(String[] args) {
launch(args);
}
private class View3D {
private final Group root3D = new Group();
private final PerspectiveCamera camera = new PerspectiveCamera(true);
private final Rotate cameraXRotate = new Rotate(-40,0,0,0,Rotate.X_AXIS);
private final Rotate cameraYRotate = new Rotate(-20,0,0,0,Rotate.Y_AXIS);
private final Rotate cameraLookXRotate = new Rotate(0,0,0,0,Rotate.X_AXIS);
private final Rotate cameraLookZRotate = new Rotate(0,0,0,0,Rotate.Z_AXIS);
private final Translate cameraPosition = new Translate(0,0,-7);
private AutoScalingGroup autoScalingGroup = new AutoScalingGroup(2);
public SubScene create() {
SubScene scene = new SubScene(root3D,512,512,true,null);
scene.setFill(Color.ALICEBLUE);
camera.getTransforms().addAll(
cameraXRotate,
cameraYRotate,
cameraPosition,
cameraLookXRotate,
cameraLookZRotate);
camera.setNearClip(0.1);
camera.setFarClip(100);
scene.setCamera(camera);
root3D.getChildren().addAll(camera, autoScalingGroup);
Box box = new Box(10,0.11,10);
PhongMaterial material = new PhongMaterial(Color.DODGERBLUE);
material.bumpMapProperty().bind(normalImage);
box.setMaterial(material);
autoScalingGroup.getChildren().add(box);
Timeline timeline = new Timeline(
new KeyFrame(Duration.ZERO, new KeyValue(cameraYRotate.angleProperty(),0)),
new KeyFrame(Duration.seconds(10), new KeyValue(cameraYRotate.angleProperty(),360))
);
timeline.setCycleCount(Timeline.INDEFINITE);
timeline.play();
return scene;
}
}
}
