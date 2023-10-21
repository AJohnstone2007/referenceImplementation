package test.javafx.scene.control.skin;
import com.sun.javafx.tk.Toolkit;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.control.skin.TableColumnHeader;
import javafx.scene.control.skin.TableColumnHeaderShim;
import javafx.scene.layout.HBox;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.com.sun.javafx.scene.control.infrastructure.StageLoader;
import test.com.sun.javafx.scene.control.infrastructure.VirtualFlowTestUtils;
import test.com.sun.javafx.scene.control.test.Person;
import java.util.List;
import static org.junit.Assert.assertTrue;
public class TreeTableColumnHeaderTest {
private TableColumnHeader firstColumnHeader;
private TreeTableView<Person> treeTableView;
private StageLoader sl;
private static String NAME0 = "Humphrey McPhee";
private static String NAME1 = "Justice Caldwell";
private static String NAME2 = "Orrin Davies";
private static String NAME3 = "Emma Wilson";
@Before
public void before() {
TreeItem<Person> root = new TreeItem<>(new Person("Witty quotes", "", ""));
root.getChildren().addAll(List.of(
new TreeItem<Person>(new Person(NAME0, 76)),
new TreeItem<Person>(new Person(NAME1, 30)),
new TreeItem<Person>(new Person(NAME2, 30)),
new TreeItem<Person>(new Person(NAME3, 8))
));
TreeTableColumn<Person, String> column = new TreeTableColumn<>("Col ");
column.setCellValueFactory(new TreeItemPropertyValueFactory<Person, String>("firstName"));
treeTableView = new TreeTableView<>(root);
treeTableView.getColumns().add(column);
sl = new StageLoader(treeTableView);
Toolkit tk = Toolkit.getToolkit();
tk.firePulse();
column.setStyle("-fx-font: System;");
firstColumnHeader = VirtualFlowTestUtils.getTableColumnHeader(treeTableView, column);
}
@After
public void after() {
sl.dispose();
}
@Test
public void test_resizeColumnToFitContentRowStyle() {
TreeTableColumn column = treeTableView.getColumns().get(0);
treeTableView.setRowFactory(this::createSmallRow);
TableColumnHeaderShim.resizeColumnToFitContent(firstColumnHeader, -1);
double width = column.getWidth();
treeTableView.setRowFactory(this::createLargeRow);
TableColumnHeaderShim.resizeColumnToFitContent(firstColumnHeader, -1);
assertTrue("Column width must be greater", width < column.getWidth());
}
@Test
public void test_resizeColumnToFitContentCustomRowSkin() {
TreeTableColumn column = treeTableView.getColumns().get(0);
treeTableView.setRowFactory(this::createCustomRow);
TableColumnHeaderShim.resizeColumnToFitContent(firstColumnHeader, -1);
double width = column.getWidth();
assertTrue(width > 0);
}
private TreeTableRow<Person> createCustomRow(TreeTableView<Person> treeTableView) {
TreeTableRow<Person> row = new TreeTableRow<>() {
protected Skin<?> createDefaultSkin() {
return new CustomSkin(this);
};
};
return row;
}
private static class CustomSkin implements Skin<TreeTableRow<?>> {
private TreeTableRow<?> row;
private Node node = new HBox();
CustomSkin(TreeTableRow<?> row) {
this.row = row;
}
@Override
public TreeTableRow<?> getSkinnable() {
return row;
}
@Override
public Node getNode() {
return node;
}
@Override
public void dispose() {
node = null;
}
}
private TreeTableRow<Person> createSmallRow(TreeTableView<Person> treeTableView) {
TreeTableRow<Person> row = new TreeTableRow<>();
row.setStyle("-fx-font: 24 Amble");
return row;
}
private TreeTableRow<Person> createLargeRow(TreeTableView<Person> param) {
TreeTableRow<Person> row = new TreeTableRow<>();
row.setStyle("-fx-font: 48 Amble");
return row;
}
}
