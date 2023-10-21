package com.javafx.experiments.jfx3dviewer;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.SubScene;
import javafx.scene.layout.Pane;
public class SubSceneResizer extends Pane {
private SubScene subScene;
private final Node controlsPanel;
public SubSceneResizer(SubScene subScene, Node controlsPanel) {
this.subScene = subScene;
this.controlsPanel = controlsPanel;
setPrefSize(subScene.getWidth(),subScene.getHeight());
setMinSize(50,50);
setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
getChildren().addAll(subScene, controlsPanel);
}
public SubSceneResizer(ObjectProperty<SubScene> subScene, Node controlsPanel) {
this.subScene = subScene.get();
this.controlsPanel = controlsPanel;
if (this.subScene != null) {
setPrefSize(this.subScene.getWidth(),this.subScene.getHeight());
getChildren().add(this.subScene);
}
subScene.addListener((o,old,newSubScene) -> {
this.subScene = newSubScene;
if (this.subScene != null) {
setPrefSize(this.subScene.getWidth(),this.subScene.getHeight());
if (getChildren().size() == 1) {
getChildren().add(0,this.subScene);
} else {
getChildren().set(0,this.subScene);
}
}
});
setMinSize(50,50);
setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
getChildren().add(controlsPanel);
}
@Override protected void layoutChildren() {
final double width = getWidth();
final double height = getHeight();
if (subScene!=null) {
subScene.setWidth(width);
subScene.setHeight(height);
}
final double controlsWidth = snapSizeX(controlsPanel.prefWidth(-1));
final double controlsHeight = snapSizeY(controlsPanel.prefHeight(-1));
controlsPanel.resizeRelocate(width-controlsWidth,0,controlsWidth,controlsHeight);
}
}
