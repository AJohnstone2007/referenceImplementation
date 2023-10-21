package myapp3;
import java.util.Locale;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import myapp3.pkg2.MyData;
import static myapp3.Constants.*;
public class AppTableViewExported extends Application {
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
TableView<MyData> tableView = new TableView<>();
TableColumn<MyData, String> nameCol = new TableColumn<>();
nameCol.setText("Name");
nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
TableColumn<MyData, Integer> valueCol = new TableColumn<>();
valueCol.setText("Value");
valueCol.setCellValueFactory(new PropertyValueFactory<>("value"));
tableView.getColumns().addAll(nameCol, valueCol);
tableView.getItems().add(new MyData("Row A", 1));
tableView.getItems().add(new MyData("Row B", 2));
tableView.getItems().add(new MyData("Row C", 3));
root.getChildren().add(tableView);
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
