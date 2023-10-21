package a11y;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
public class HelloSimpleTreeView extends Application {
public void start(Stage stage) {
TreeItem<String> root = new TreeItem<>("Root node");
for (int i = 0; i < 200; i++) {
TreeItem<String> item = new TreeItem<>("Child node " + i);
root.getChildren().add(item);
if ((i % 3) == 0) {
for (int j = 0; j < 5; j++) {
TreeItem<String> sitem = new TreeItem<>("sub item " + i + " " + j);
item.getChildren().add(sitem);
}
if ((i % 2) == 0) item.setExpanded(true);
}
}
root.setExpanded(true);
TreeView<String> treeView = new TreeView<>(root);
Label label = new Label("JFX TreeView");
label.setLabelFor(treeView);
Button button = new Button("okay");
ToggleButton button2 = new ToggleButton("empty");
button2.setOnAction(t-> {
if (treeView.getRoot()!=null) {
treeView.setRoot(null);
} else {
treeView.setRoot(root);
}
});
VBox group = new VBox(label, treeView, button, button2);
stage.setScene(new Scene(group, 800, 600));
stage.show();
}
public static void main(String[] args) {
launch(args);
}
}
