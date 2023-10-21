package test.javafx.scene.control.skin;
import com.sun.javafx.tk.Toolkit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.com.sun.javafx.scene.control.infrastructure.StageLoader;
import test.com.sun.javafx.scene.control.infrastructure.VirtualFlowTestUtils;
import test.com.sun.javafx.scene.control.test.Person;
import static org.junit.Assert.assertEquals;
public class TableRowSkinTest {
private TableView<Person> tableView;
private StageLoader stageLoader;
@Before
public void before() {
tableView = new TableView<>();
TableColumn<Person, String> firstNameCol = new TableColumn<>("Firstname");
firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
TableColumn<Person, String> lastNameCol = new TableColumn<>("Lastname");
lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
TableColumn<Person, String> emailCol = new TableColumn<>("Email");
emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
TableColumn<Person, Integer> ageCol = new TableColumn<>("Age");
ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));
tableView.getColumns().addAll(firstNameCol, lastNameCol, emailCol, ageCol);
ObservableList<Person> items = FXCollections.observableArrayList(
new Person("firstName1", "lastName1", "email1@javafx.com", 1),
new Person("firstName2", "lastName2", "email2@javafx.com", 2),
new Person("firstName3", "lastName3", "email3@javafx.com", 3),
new Person("firstName4", "lastName4", "email4@javafx.com", 4)
);
tableView.setItems(items);
stageLoader = new StageLoader(tableView);
}
@Test
public void removedColumnsShouldRemoveCorrespondingCellsInRowFixedCellSize() {
tableView.setFixedCellSize(24);
removedColumnsShouldRemoveCorrespondingCellsInRowImpl();
}
@Test
public void removedColumnsShouldRemoveCorrespondingCellsInRow() {
removedColumnsShouldRemoveCorrespondingCellsInRowImpl();
}
@Test
public void invisibleColumnsShouldRemoveCorrespondingCellsInRowFixedCellSize() {
tableView.setFixedCellSize(24);
invisibleColumnsShouldRemoveCorrespondingCellsInRowImpl();
}
@Test
public void invisibleColumnsShouldRemoveCorrespondingCellsInRow() {
invisibleColumnsShouldRemoveCorrespondingCellsInRowImpl();
}
@After
public void after() {
stageLoader.dispose();
}
private void invisibleColumnsShouldRemoveCorrespondingCellsInRowImpl() {
tableView.getColumns().get(tableView.getColumns().size() - 1).setVisible(false);
tableView.getColumns().get(tableView.getColumns().size() - 2).setVisible(false);
Toolkit.getToolkit().firePulse();
assertEquals(tableView.getColumns().size() - 2,
VirtualFlowTestUtils.getCell(tableView, 0).getChildrenUnmodifiable().size());
}
private void removedColumnsShouldRemoveCorrespondingCellsInRowImpl() {
tableView.getColumns().remove(tableView.getColumns().size() - 1, tableView.getColumns().size());
Toolkit.getToolkit().firePulse();
assertEquals(tableView.getColumns().size(),
VirtualFlowTestUtils.getCell(tableView, 0).getChildrenUnmodifiable().size());
}
}
