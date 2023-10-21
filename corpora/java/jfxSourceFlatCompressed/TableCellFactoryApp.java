package ensemble.samples.controls.tablecellfactory;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.StringConverter;
public class TableCellFactoryApp extends Application {
public Parent createContent() {
final ObservableList<Person> data = FXCollections.observableArrayList(
new Person(true, "Jacob", "Smith", "jacob.smith@example.com"),
new Person(false, "Sue", "Jones", "sue@example.com"),
new Person(true, "Ethan", "Fox", "ethan.fox@example.com"),
new Person(true, "Emma", "Jones", "emma.jones@example.com"),
new Person(false, "Mike", "Brown", "mike.brown@example.com"));
StringConverter<Object> sc = new StringConverter<Object>() {
@Override
public String toString(Object t) {
return t == null ? null : t.toString();
}
@Override
public Object fromString(String string) {
return string;
}
};
TableColumn invitedCol = new TableColumn<>();
invitedCol.setText("Invited");
invitedCol.setMinWidth(70);
invitedCol.setCellValueFactory(new PropertyValueFactory("invited"));
invitedCol.setCellFactory(CheckBoxTableCell.forTableColumn(invitedCol));
TableColumn firstNameCol = new TableColumn();
firstNameCol.setText("First");
firstNameCol.setCellValueFactory(new PropertyValueFactory("firstName"));
firstNameCol.setCellFactory(TextFieldTableCell.forTableColumn(sc));
TableColumn lastNameCol = new TableColumn();
lastNameCol.setText("Last");
lastNameCol.setCellValueFactory(new PropertyValueFactory("lastName"));
lastNameCol.setCellFactory(TextFieldTableCell.forTableColumn(sc));
TableColumn emailCol = new TableColumn();
emailCol.setText("Email");
emailCol.setMinWidth(200);
emailCol.setCellValueFactory(new PropertyValueFactory("email"));
emailCol.setCellFactory(TextFieldTableCell.forTableColumn(sc));
TableView tableView = new TableView();
tableView.setItems(data);
tableView.setEditable(true);
tableView.getColumns().addAll(invitedCol, firstNameCol, lastNameCol, emailCol);
return tableView;
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
