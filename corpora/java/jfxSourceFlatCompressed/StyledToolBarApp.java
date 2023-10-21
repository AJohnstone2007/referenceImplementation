package ensemble.samples.controls.toolbar.styledtoolbar;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
public class StyledToolBarApp extends Application {
private ToolBar createToolBar(String id) {
ToolBar toolBar = new ToolBar(new Button("Button 1"),
new Button("Button 2"),
new Slider());
toolBar.setId(id);
return toolBar;
}
public Parent createContent() {
ToolBar standardToolbar = createToolBar("standard");
ToolBar darkToolbar = createToolBar("dark");
final String styledToolBarCss =
getClass().getResource("StyledToolBar.css").toExternalForm();
darkToolbar.getStylesheets().add(styledToolBarCss);
ToolBar blueToolbar = createToolBar("blue");
blueToolbar.getStylesheets().add(styledToolBarCss);
final VBox box = new VBox(10, standardToolbar, darkToolbar, blueToolbar);
box.setPadding(new Insets(10));
return box;
}
@Override
public void start(Stage primaryStage) throws Exception {
primaryStage.setScene(new Scene(createContent()));
primaryStage.show();
}
public static void main(String[] args) {
launch(args);
}
}
