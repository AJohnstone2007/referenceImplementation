package com.javafx.experiments.jfx3dviewer;
import java.io.File;
import java.util.List;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class Jfx3dViewerApp extends Application {
public static final String FILE_URL_PROPERTY = "fileUrl";
private static ContentModel contentModel;
private SessionManager sessionManager;
public static ContentModel getContentModel() {
return contentModel;
}
@Override public void start(Stage stage) throws Exception {
sessionManager = SessionManager.createSessionManager("Jfx3dViewerApp");
sessionManager.loadSession();
List<String> args = getParameters().getRaw();
if (!args.isEmpty()) {
sessionManager.getProperties().setProperty(FILE_URL_PROPERTY,
new File(args.get(0)).toURI().toURL().toString());
}
contentModel = new ContentModel();
Scene scene = new Scene(
FXMLLoader.<Parent>load(Jfx3dViewerApp.class.getResource("main.fxml")),
1024,600);
stage.setScene(scene);
stage.show();
stage.setOnCloseRequest(event -> sessionManager.saveSession());
}
public static void main(String[] args) {
launch(args);
}
}
