package ensemble.samples.language.concurrency.task;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
public class TaskApp extends Application {
public Parent createContent() {
TableView<DailySales> tableView = new TableView<>();
Region veil = new Region();
veil.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4)");
ProgressIndicator p = new ProgressIndicator();
p.setMaxSize(150, 150);
TableColumn idCol = new TableColumn();
idCol.setText("ID");
idCol.setCellValueFactory(new PropertyValueFactory("dailySalesId"));
idCol.setPrefWidth(32);
tableView.getColumns().add(idCol);
TableColumn qtyCol = new TableColumn();
qtyCol.setText("Qty");
qtyCol.setCellValueFactory(new PropertyValueFactory("quantity"));
qtyCol.setPrefWidth(60);
tableView.getColumns().add(qtyCol);
TableColumn dateCol = new TableColumn();
dateCol.setText("Date");
dateCol.setCellValueFactory(new PropertyValueFactory("date"));
dateCol.setMinWidth(240);
tableView.getColumns().add(dateCol);
tableView.setMinSize(240, 200);
StackPane stack = new StackPane();
stack.getChildren().addAll(tableView, veil, p);
Task<ObservableList<DailySales>> task = new GetDailySalesTask();
p.progressProperty().bind(task.progressProperty());
veil.visibleProperty().bind(task.runningProperty());
p.visibleProperty().bind(task.runningProperty());
tableView.itemsProperty().bind(task.valueProperty());
new Thread(task).start();
return stack;
}
@Override public void start(Stage primaryStage) throws Exception {
primaryStage.setScene(new Scene(createContent()));
primaryStage.show();
}
public static void main(String[] args) {
launch(args);
}
}
