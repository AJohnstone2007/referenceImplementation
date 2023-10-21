package ensemble.samples.controls.treeview;
import java.util.Arrays;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
public class TreeViewApp extends Application {
public Parent createContent() {
final TreeItem<String> treeRoot = new TreeItem<String>("Root node");
treeRoot.getChildren().addAll(Arrays.asList(
new TreeItem<String>("Child Node 1"),
new TreeItem<String>("Child Node 2"),
new TreeItem<String>("Child Node 3")));
treeRoot.getChildren().get(2).getChildren().addAll(Arrays.asList(
new TreeItem<String>("Child Node 4"),
new TreeItem<String>("Child Node 5"),
new TreeItem<String>("Child Node 6"),
new TreeItem<String>("Child Node 7"),
new TreeItem<String>("Child Node 8")));
final TreeView treeView = new TreeView();
treeView.setShowRoot(true);
treeView.setRoot(treeRoot);
treeRoot.setExpanded(true);
return treeView;
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
