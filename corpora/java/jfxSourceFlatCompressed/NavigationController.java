package com.javafx.experiments.jfx3dviewer;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.control.ScrollBar;
public class NavigationController implements Initializable {
public ScrollBar zoomBar;
private ContentModel contentModel = Jfx3dViewerApp.getContentModel();
@Override public void initialize(URL location, ResourceBundle resources) {
zoomBar.setMin(-100);
zoomBar.setMax(0);
zoomBar.setValue(contentModel.getCameraPosition().getZ());
zoomBar.setVisibleAmount(5);
contentModel.getCameraPosition().zProperty().bindBidirectional(zoomBar.valueProperty());
}
}
