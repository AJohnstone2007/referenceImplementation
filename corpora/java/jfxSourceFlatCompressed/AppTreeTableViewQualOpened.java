package myapp3;
import java.util.Locale;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import myapp3.pkg5.MyData;
import static myapp3.Constants.*;
public class AppTreeTableViewQualOpened extends Application {
public static void main(String[] args) {
try {
Application.launch(args);
} catch (Throwable t) {
System.err.println("ERROR: caught unexpected exception: " + t);
t.printStackTrace(System.err);
System.exit(ERROR_UNEXPECTED_EXCEPTION);
}
}
private Logger logger;
private Handler logHandler;
private void initLogger() {
Locale.setDefault(Locale.US);
logHandler = new Handler() {
@Override
public void publish(LogRecord record) {
final Throwable t = record.getThrown();
if (t != null) {
System.err.println("ERROR: unexpected exception was logged: " + record.getMessage());
t.printStackTrace();
System.exit(ERROR_UNEXPECTED_EXCEPTION);
}
}
@Override
public void flush() {
}
@Override
public void close() {
}
};
logger = Logger.getLogger("javafx.scene.control");
logger.addHandler(logHandler);
}
@Override
public void start(Stage stage) throws Exception {
initLogger();
try {
StackPane root = new StackPane();
Scene scene = new Scene(root);
TreeTableView<MyData> treeTableView = new TreeTableView<>();
TreeTableColumn<MyData, String> nameCol = new TreeTableColumn<>();
nameCol.setText("Name");
nameCol.setPrefWidth(150);
nameCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
TreeTableColumn<MyData, Integer> valueCol = new TreeTableColumn<>();
valueCol.setText("Value");
valueCol.setPrefWidth(100);
valueCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("value"));
treeTableView.getColumns().addAll(nameCol, valueCol);
TreeItem<MyData> treeRoot = new TreeItem<>(new MyData("Row A", 1));
treeRoot.setExpanded(true);
treeTableView.setRoot(treeRoot);
TreeItem<MyData> item1 = new TreeItem<>(new MyData("Row B", 2));
TreeItem<MyData> item2 = new TreeItem<>(new MyData("Row C", 3));
treeRoot.getChildren().addAll(item1, item2);
root.getChildren().add(treeTableView);
stage.setScene(scene);
stage.show();
KeyFrame kf = new KeyFrame(Duration.millis(SHOWTIME), e -> stage.hide());
Timeline timeline = new Timeline(kf);
timeline.play();
} catch (Error | Exception ex) {
System.err.println("ERROR: caught unexpected exception: " + ex);
ex.printStackTrace(System.err);
System.exit(ERROR_UNEXPECTED_EXCEPTION);
}
}
@Override public void stop() {
System.exit(ERROR_NONE);
}
}
