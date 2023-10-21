package ensemble.samples.controls.splitpane;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
public class HiddenSplitPaneApp extends Application {
public Parent createContent() {
Region region1 = new Region();
Region region2 = new Region();
Region region3 = new Region();
region1.getStyleClass().add("rounded");
region2.getStyleClass().add("rounded");
region3.getStyleClass().add("rounded");
final SplitPane splitPane = new SplitPane();
final String hidingSplitPaneCss =
getClass().getResource("HiddenSplitPane.css").toExternalForm();
splitPane.setId("hiddenSplitter");
splitPane.getItems().addAll(region1, region2, region3);
splitPane.setDividerPositions(0.33, 0.66);
splitPane.getStylesheets().add(hidingSplitPaneCss);
return splitPane;
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
