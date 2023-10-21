package layout;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
public class LayoutDemo extends Application {
final double STAGE_WIDTH = 1244;
final double STAGE_HEIGHT = 700;
public static void main(String[] args) {
Application.launch(args);
}
@Override
public void start(Stage stage) {
ResizableTab resizableTab = new ResizableTab("Resizable");
PaneTab paneTab = new PaneTab("Pane");
HBoxTab hboxTab = new HBoxTab("HBox");
VBoxTab vboxTab = new VBoxTab("VBox");
FlowPaneTab flowPaneTab = new FlowPaneTab("FlowPane");
BorderPaneTab borderPaneTab = new BorderPaneTab("BorderPane");
StackPaneTab stackPaneTab = new StackPaneTab("StackPane");
TilePaneTab tilePaneTab = new TilePaneTab("TilePane");
AnchorPaneTab anchorTab = new AnchorPaneTab("AnchorPane");
GridPaneTab gridPane = new GridPaneTab("GridPane");
CustomPaneTab customPane = new CustomPaneTab("CustomPane");;
CustomTilePaneTab customTilePane = new CustomTilePaneTab("CustomTilePane");
TabPane tabPane = new TabPane();
tabPane.getTabs().addAll(resizableTab, paneTab, hboxTab, vboxTab,
flowPaneTab, borderPaneTab, stackPaneTab, tilePaneTab,
anchorTab, gridPane, customPane, customTilePane);
tabPane.getSelectionModel().selectedIndexProperty()
.addListener((o, oldValue, newValue) -> {
stage.setWidth(STAGE_WIDTH);
stage.setHeight(STAGE_HEIGHT);
});
BorderPane root = new BorderPane();
root.setCenter(tabPane);
Scene scene = new Scene(root);
scene.getStylesheets().addAll("resources/css/layoutdemos.css");
stage.setScene(scene);
stage.setWidth(STAGE_WIDTH);
stage.setHeight(STAGE_HEIGHT);
stage.setTitle("JavaOne Layout Demo");
stage.show();
}
}
