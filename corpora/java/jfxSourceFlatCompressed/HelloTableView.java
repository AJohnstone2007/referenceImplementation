package hello;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
public class HelloTableView extends Application {
private static int NB_COL = 30;
private static int NB_LINE = 10000;
public static class Person {
private BooleanProperty invited;
private StringProperty firstName;
private StringProperty lastName;
private StringProperty email;
private final String country = "New Zealand";
public Person(String fName, String lName) {
this(fName, lName, null);
}
public Person(String fName, String lName, String email) {
this(fName, lName, email, false);
}
public Person(String fName, String lName, String email, boolean invited) {
this.firstName = new SimpleStringProperty(fName);
this.lastName = new SimpleStringProperty(lName);
this.email = new SimpleStringProperty(email);
this.invited = new SimpleBooleanProperty(invited);
this.invited.addListener((ov, t, t1) -> System.out.println(getFirstName() + " invited: " + t1));
}
public Boolean isInvited() { return invited.get(); }
public BooleanProperty invitedProperty() { return invited; }
public String getFirstName() {
return firstName.get();
}
public void setFirstName(String firstName) {
this.firstName.set(firstName);
}
public StringProperty firstNameProperty() {
return firstName;
}
public String getLastName() {
return lastName.get();
}
public void setLastName(String lastName) {
this.lastName.set(lastName);
}
public StringProperty lastNameProperty() {
return lastName;
}
public String getEmail() {
return email.get();
}
public void setEmail(String email) {
this.email.set(email);
}
public StringProperty emailProperty() {
return email;
}
public String getCountry() {
return country;
}
public String toString() {
return "Person [ " + getFirstName() + " " + getLastName() + " ]";
}
}
private static final String MULTI_SELECT = "multiSelect";
private static final String CELL_SELECT = "cellSelect";
private static ObservableList<Person> createTestData() {
ObservableList<Person> data = FXCollections.observableArrayList();
data.addAll(
new Person("Jacob", "Smith\nSmith\nSmith", "jacob.smith<at>example.com", true ),
new Person("Isabella", "Johnson", "isabella.johnson<at>example.com" ),
new Person("Ethan", "Williams", "ethan.williams<at>example.com", true ),
new Person("Emma", "Jones", "emma.jones<at>example.com" ),
new Person("Michael", "Brown", "michael.brown<at>example.com", true ),
new Person("Olivia", "Davis", "olivia.davis<at>example.com" ),
new Person("Alexander", "Miller", "alexander.miller<at>example.com", true ),
new Person("Sophia", "Wilson", "sophia.wilson<at>example.com" ),
new Person("William", "Moore", "william.moore<at>example.com", true ),
new Person("Ava", "Taylor", "ava.taylor<at>example.com" ),
new Person("Joshua", "Anderson", "joshua.anderson<at>example.com" ),
new Person("Emily", "Thomas", "emily.thomas<at>example.com" ),
new Person("Daniel", "Jackson", "daniel.jackson<at>example.com" ),
new Person("Madison", "White", "madison.white<at>example.com" ),
new Person("Jayden", "Harris", "jayden.harris<at>example.com" ),
new Person("Abigail", "Martin", "abigail.martin<at>example.com" ),
new Person("Noah", "Thompson", "noah.thompson<at>example.com" ),
new Person("Chloe", "Garcia", "chloe.garcia<at>example.com" ),
new Person("Anthony", "Martinez", "anthony.martinez<at>example.com" ),
new Person("Mia", "Robinson", "mia.robinson<at>example.com" ),
new Person("Jacob", "Smith" ),
new Person("Isabella", "Johnson" ),
new Person("Ethan", "Williams" ),
new Person("Emma", "Jones" ),
new Person("Michael", "Brown" ),
new Person("Olivia", "Davis" ),
new Person("Alexander", "Miller" ),
new Person("Sophia", "Wilson" ),
new Person("William", "Moore" ),
new Person("Ava", "Taylor" ),
new Person("Joshua", "Anderson" ),
new Person("Emily", "Thomas" ),
new Person("Daniel", "Jackson" ),
new Person("Madison", "White" ),
new Person("Jayden", "Harris" ),
new Person("Abigail", "Martin" ),
new Person("Noah", "Thompson" ),
new Person("Chloe", "Garcia" ),
new Person("Anthony", "Martinez" ),
new Person("Mia", "Robinson" )
);
return data;
}
public static void main(String[] args) {
Application.launch(args);
}
@Override public void start(Stage stage) {
stage.setTitle("Hello TableView");
final Scene scene = new Scene(new Group(), 875, 700);
scene.setFill(Color.LIGHTGRAY);
Group root = (Group)scene.getRoot();
final TabPane tabPane = new TabPane();
tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
tabPane.setPrefWidth(scene.getWidth());
tabPane.setPrefHeight(scene.getHeight());
InvalidationListener sceneListener = ov -> {
tabPane.setPrefWidth(scene.getWidth());
tabPane.setPrefHeight(scene.getHeight());
};
scene.widthProperty().addListener(sceneListener);
scene.heightProperty().addListener(sceneListener);
Tab simpleTab = new Tab("Simple");
buildSimpleTab(simpleTab);
tabPane.getTabs().add(simpleTab);
Tab unsortedTab = new Tab("Unsorting");
buildUnsortedTab(unsortedTab);
tabPane.getTabs().add(unsortedTab);
Tab sortAndFilterTab = new Tab("Sort & Filter");
buildSortAndFilterTab(sortAndFilterTab);
tabPane.getTabs().add(sortAndFilterTab);
Tab perfTestTab = new Tab("Performance Test");
buildPerformanceTestTab(perfTestTab, false);
tabPane.getTabs().add(perfTestTab);
Tab customCellPerfTestTab = new Tab("Custom Cell Performance Test");
buildPerformanceTestTab(customCellPerfTestTab, true);
tabPane.getTabs().add(customCellPerfTestTab);
root.getChildren().add(tabPane);
stage.setScene(scene);
stage.show();
}
private void buildSimpleTab(Tab tab) {
GridPane grid = new GridPane();
grid.setPadding(new Insets(5, 5, 5, 5));
grid.setHgap(5);
grid.setVgap(5);
ObservableList<Person> data = createTestData();
TableColumn<Person, String> firstNameCol = new TableColumn<>();
firstNameCol.setText("First");
Rectangle sortNode = new Rectangle(10, 10, Color.RED);
sortNode.fillProperty().bind(new ObjectBinding<Paint>() {
{ bind(firstNameCol.sortTypeProperty()); }
@Override protected Paint computeValue() {
switch (firstNameCol.getSortType()) {
case ASCENDING: return Color.GREEN;
case DESCENDING: return Color.RED;
default: return Color.BLACK;
}
}
});
firstNameCol.setSortNode(sortNode);
firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
firstNameCol.setOnEditCommit(t -> {
System.out.println("Edit commit event: " + t.getNewValue());
});
TableColumn<Person, String> lastNameCol = new TableColumn<>();
lastNameCol.setText("Last");
lastNameCol.setCellValueFactory(p -> p.getValue().lastNameProperty());
TableColumn<Person, String> nameCol = new TableColumn<>();
nameCol.setText("Name");
nameCol.getColumns().addAll(firstNameCol, lastNameCol);
TableColumn<Person, String> emailCol = new TableColumn<>();
emailCol.setText("Email");
emailCol.setMinWidth(200);
emailCol.setCellValueFactory(p -> p.getValue().emailProperty());
TableColumn<Person, String> countryCol = new TableColumn<>();
countryCol.setText("Country");
countryCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>("New Zealand"));
TableColumn<Person, Boolean> invitedCol = new TableColumn<>();
invitedCol.setText("Invited");
invitedCol.setMaxWidth(50);
invitedCol.setCellValueFactory(new PropertyValueFactory("invited"));
invitedCol.setCellFactory(p -> new CheckBoxTableCell<>());
invitedCol.setEditable(true);
final TableView<Person> tableView = new TableView<>();
tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
tableView.getSelectionModel().setCellSelectionEnabled(false);
tableView.setTableMenuButtonVisible(false);
tableView.setItems(data);
tableView.getColumns().addAll(invitedCol, nameCol, emailCol, countryCol);
tableView.setPrefSize(485, 300);
tableView.setEditable(true);
final MenuItem cutMI = new MenuItem("Cut");
final MenuItem copyMI = new MenuItem("Copy");
final MenuItem pasteMI = new MenuItem("Paste");
final MenuItem deleteMI = new MenuItem("DeleteSelection");
final MenuItem selectMI = new MenuItem("SelectAll");
final ContextMenu cm = new ContextMenu(cutMI, copyMI, pasteMI, deleteMI,
new SeparatorMenuItem(), selectMI);
invitedCol.setContextMenu(cm);
tableView.setPlaceholder(new ProgressBar(-1));
grid.getChildren().addAll(tableView);
GridPane.setConstraints(tableView, 0, 0, 1, 12);
GridPane.setVgrow(tableView, Priority.ALWAYS);
GridPane.setHgrow(tableView, Priority.ALWAYS);
firstNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
final ToggleGroup tg = new ToggleGroup();
RadioButton singleRow = new RadioButton("Single Row");
singleRow.getProperties().put(MULTI_SELECT, SelectionMode.SINGLE);
singleRow.getProperties().put(CELL_SELECT, false);
singleRow.setToggleGroup(tg);
tg.selectToggle(singleRow);
grid.getChildren().add(singleRow);
GridPane.setConstraints(singleRow, 1, 0);
RadioButton singleCell = new RadioButton("Single Cell");
singleCell.getProperties().put(MULTI_SELECT, SelectionMode.SINGLE);
singleCell.getProperties().put(CELL_SELECT, true);
singleCell.setToggleGroup(tg);
grid.getChildren().add(singleCell);
GridPane.setConstraints(singleCell, 1, 1);
RadioButton multipleRow = new RadioButton("Multiple Rows");
multipleRow.getProperties().put(MULTI_SELECT, SelectionMode.MULTIPLE);
multipleRow.getProperties().put(CELL_SELECT, false);
multipleRow.setToggleGroup(tg);
grid.getChildren().add(multipleRow);
GridPane.setConstraints(multipleRow, 1, 2);
RadioButton multipleCell = new RadioButton("Multiple Cells");
multipleCell.getProperties().put(MULTI_SELECT, SelectionMode.MULTIPLE);
multipleCell.getProperties().put(CELL_SELECT, true);
multipleCell.setToggleGroup(tg);
grid.getChildren().add(multipleCell);
GridPane.setConstraints(multipleCell, 1, 3);
tg.selectedToggleProperty().addListener(ov -> {
RadioButton toggle = (RadioButton) tg.getSelectedToggle();
if (toggle == null) return;
Map<Object, Object> properties = toggle.getProperties();
SelectionMode selectMode = (SelectionMode) properties.get(MULTI_SELECT);
boolean cellSelect = (Boolean) properties.get(CELL_SELECT);
tableView.getSelectionModel().setSelectionMode(selectMode);
tableView.getSelectionModel().setCellSelectionEnabled(cellSelect);
});
final ToggleButton columnControlBtn = new ToggleButton("Column Control");
columnControlBtn.setSelected(false);
columnControlBtn.selectedProperty().addListener(ov -> tableView.setTableMenuButtonVisible(columnControlBtn.isSelected()));
grid.getChildren().add(columnControlBtn);
GridPane.setConstraints(columnControlBtn, 1, 5);
final ToggleButton constrainResizeBtn = new ToggleButton("Constrained Resize");
constrainResizeBtn.setSelected(false);
constrainResizeBtn.selectedProperty().addListener(ov -> {
if (constrainResizeBtn.isSelected()) {
tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
} else {
tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
}
});
grid.add(constrainResizeBtn, 1, 7);
Button dumpButton = new Button("Dump to console");
dumpButton.setOnAction(e -> {
System.out.println("================================");
System.out.println("TableView dump:");
System.out.println("Columns: " + tableView.getColumns());
System.out.println("Visible Leaf Columns: " + tableView.getVisibleLeafColumns());
System.out.println("================================");
});
grid.getChildren().add(dumpButton);
GridPane.setConstraints(dumpButton, 1, 8);
final Button insertBtn = new Button("Insert row");
insertBtn.setOnAction(t -> data.add(0, new Person("First Name", "Last Name", "Email")));
grid.getChildren().add(insertBtn);
GridPane.setConstraints(insertBtn, 1, 9);
final Button renameEthanBtn = new Button("Rename Ethan");
renameEthanBtn.setOnAction(t -> {
data.get(2).setFirstName(new BigInteger(40, new Random()).toString(32));
});
grid.getChildren().add(renameEthanBtn);
GridPane.setConstraints(renameEthanBtn, 1, 10);
tab.setContent(grid);
tableView.getSelectionModel().getSelectedCells().addListener(new ListChangeListener<TablePosition>() {
public void onChanged(Change<? extends TablePosition> change) {
System.out.println(tableView.getSelectionModel().getSelectedCells() + "\n\n");
}
});
}
private void buildUnsortedTab(Tab tab) {
GridPane grid = new GridPane();
grid.setPadding(new Insets(5, 5, 5, 5));
grid.setHgap(5);
grid.setVgap(5);
TableView tableView = buildUnsortedTable(FXCollections.observableArrayList(2, 1, 3));
grid.add(new Label("TableView w/ ObservableList:"), 0, 0);
grid.add(tableView, 0, 1);
GridPane.setVgrow(tableView, Priority.ALWAYS);
GridPane.setHgrow(tableView, Priority.ALWAYS);
SortedList sortedList = new SortedList(FXCollections.observableArrayList(2, 1, 3));
TableView tableView1 = buildUnsortedTable(sortedList);
sortedList.comparatorProperty().bind(tableView1.comparatorProperty());
grid.add(new Label("TableView w/ SortedList:"), 1, 0);
grid.add(tableView1, 1, 1);
GridPane.setVgrow(tableView1, Priority.ALWAYS);
GridPane.setHgrow(tableView1, Priority.ALWAYS);
tab.setContent(grid);
}
private TableView buildUnsortedTable(ObservableList collection) {
final TableView<Integer> tableView = new TableView<Integer>(collection);
TableColumn<Integer, Number> numberColumn = new TableColumn<>("Numbers");
numberColumn.setPrefWidth(81);
numberColumn.setCellValueFactory(param -> new ReadOnlyIntegerWrapper(param.getValue()));
tableView.getColumns().add(numberColumn);
return tableView;
}
private void buildSortAndFilterTab(Tab tab) {
Predicate<Person> NO_MATCHER = e -> true;
Comparator<Person> NO_COMPARATOR = (o1, o2) -> 0;
final ObservableList<Person> data = createTestData();
GridPane grid = new GridPane();
grid.setPadding(new Insets(5, 5, 5, 5));
grid.setHgap(5);
grid.setVgap(5);
final TableView<Person> unmodifiedTableView = new TableView<>();
unmodifiedTableView.setId("Unmodified table");
unmodifiedTableView.setItems(data);
unmodifiedTableView.getColumns().setAll(createFirstNameCol(), createLastNameCol());
Node unmodifiedLabel = createLabel("Original TableView:");
grid.getChildren().addAll(unmodifiedLabel, unmodifiedTableView);
GridPane.setConstraints(unmodifiedLabel, 0, 0);
GridPane.setConstraints(unmodifiedTableView, 0, 1);
GridPane.setVgrow(unmodifiedTableView, Priority.ALWAYS);
final SortedList<Person> sortedList1 = new SortedList<Person>(data, NO_COMPARATOR);
final TableView<Person> sortedTableView = new TableView<Person>();
sortedTableView.setId("sorted table");
sortedTableView.setItems(sortedList1);
sortedList1.comparatorProperty().bind(sortedTableView.comparatorProperty());
sortedTableView.getColumns().setAll(createFirstNameCol(), createLastNameCol());
Node sortedLabel = createLabel("Sorted TableView:");
grid.getChildren().addAll(sortedLabel, sortedTableView);
GridPane.setConstraints(sortedLabel, 1, 0);
GridPane.setConstraints(sortedTableView, 1, 1);
GridPane.setVgrow(sortedTableView, Priority.ALWAYS);
final SortedList<Person> sortedList2 = new SortedList<Person>(data, NO_COMPARATOR);
final FilteredList<Person> filteredList2 = new FilteredList<Person>(sortedList2, NO_MATCHER);
final TableView<Person> filteredTableView = new TableView<Person>();
filteredTableView.setId("filtered table");
filteredTableView.setItems(filteredList2);
filteredTableView.getColumns().setAll(createFirstNameCol(), createLastNameCol());
Node filteredLabel = createLabel("Filtered (and sorted) TableView:");
grid.getChildren().addAll(filteredLabel, filteredTableView);
GridPane.setConstraints(filteredLabel, 2, 0);
GridPane.setConstraints(filteredTableView, 2, 1);
GridPane.setVgrow(filteredTableView, Priority.ALWAYS);
VBox vbox = new VBox(10);
vbox.getChildren().add(new Label("Note: Double-click table cells to edit."));
final TextField filterInput = new TextField();
filterInput.setPromptText("Enter filter text");
filterInput.setOnKeyReleased(t -> filteredList2.setPredicate((Person e) -> {
String input = filterInput.getText().toUpperCase();
return e.getFirstName().toUpperCase().contains(input) || e.getLastName().toUpperCase().contains(input);
}
));
vbox.getChildren().add(filterInput);
final TextField newItemInput = new TextField();
newItemInput.setPromptText("Enter \"firstName lastName\", then press enter to add person to table");
newItemInput.setOnKeyReleased(t -> {
if (t.getCode() == KeyCode.ENTER) {
String[] name = newItemInput.getText().split(" ");
data.add(new Person(name[0], name[1]));
newItemInput.setText("");
}
});
vbox.getChildren().add(newItemInput);
grid.setConstraints(vbox, 3, 1);
grid.getChildren().add(vbox);
tab.setContent(grid);
}
private TableColumn createFirstNameCol() {
TableColumn<Person, String> firstNameCol = new TableColumn<>();
firstNameCol.setText("First");
firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
firstNameCol.setOnEditCommit(t -> System.out.println("Edit commit event: " + t.getNewValue()));
return firstNameCol;
}
private TableColumn createLastNameCol() {
TableColumn<Person, String> lastNameCol = new TableColumn<Person, String>();
lastNameCol.setText("Last");
lastNameCol.setCellValueFactory(p -> p.getValue().lastNameProperty());
return lastNameCol;
}
private void buildPerformanceTestTab(Tab tab, boolean customCell) {
GridPane grid = new GridPane();
grid.setPadding(new Insets(5, 5, 5, 5));
grid.setHgap(5);
grid.setVgap(5);
final ObservableList<List<Double>> bigData = FXCollections.observableArrayList();
TableView<List<Double>> tableView = new TableView<List<Double>>();
tableView.setItems(bigData);
tableView.getColumns().addAll(getColumns(customCell));
tableView.setLayoutX(30);
tableView.setLayoutY(150);
tableView.setPrefSize(1100, 300);
tableView.setTableMenuButtonVisible(true);
getLines(bigData);
tableView.setOnMouseReleased(t -> {
if (t.getClickCount() == 3) {
System.out.println("resetting data...");
bigData.clear();
getLines(bigData);
System.out.println("Done");
}
});
int row = 0;
if (customCell) {
grid.add(new Label("Note: the CheckBox cells do not persist their state in this demo!" +
"\n(This means that if you select checkboxes and scroll, they may not be in the same " +
"state once you scroll back."), 0, row++);
}
grid.getChildren().addAll(tableView);
GridPane.setConstraints(tableView,0, row++);
GridPane.setVgrow(tableView, Priority.ALWAYS);
GridPane.setHgrow(tableView, Priority.ALWAYS);
tab.setContent(grid);
}
public void getLines(ObservableList<List<Double>> bigData) {
for (int row = 0; row < NB_LINE; row++) {
List<Double> line = new ArrayList<>();
for (int col = 0; col <= NB_COL; col++) {
if(col == 0) line.add((double)row);
else line.add(Math.random() * 1000);
}
bigData.add(line);
}
}
public List<TableColumn<List<Double>,Double>> getColumns(boolean customCell) {
List<TableColumn<List<Double>,Double>> cols = new ArrayList<>();
for (int i = 0; i <= NB_COL; i++) {
TableColumn<List<Double>,Double> col = new TableColumn<>("Col" + i);
final int coli = i;
col.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>((p.getValue()).get(coli)));
if (customCell) {
col.setCellFactory(p -> new TableCell<List<Double>,Double>() {
CheckBox chk;
@Override public void updateItem(Double item, boolean empty) {
super.updateItem(item, empty);
if (empty) {
setGraphic(null);
} else {
if (chk == null) {
chk = new CheckBox();
}
chk.setText(item.toString());
setGraphic(chk);
}
setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
}
});
}
cols.add(col);
}
return cols;
}
private Node createLabel(String text) {
Label label = new Label(text);
return label;
}
}
