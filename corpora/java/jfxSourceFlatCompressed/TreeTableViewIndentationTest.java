package test.javafx.scene.control.skin;
import com.sun.javafx.tk.Toolkit;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.skin.LabeledSkinBase;
import javafx.scene.control.skin.LabeledSkinBaseShim;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.com.sun.javafx.scene.control.infrastructure.StageLoader;
import test.com.sun.javafx.scene.control.infrastructure.VirtualFlowTestUtils;
import java.util.List;
import static org.junit.Assert.assertEquals;
public class TreeTableViewIndentationTest {
private TreeTableView<String> treeTableView;
private TreeTableColumn<String, String> column;
private StageLoader stageLoader;
@Before
public void setup() {
treeTableView = new TreeTableView<>();
column = new TreeTableColumn<>("Column");
treeTableView.getColumns().add(column);
TreeItem<String> root = new TreeItem<>("Root");
root.getChildren().addAll(List.of(new TreeItem<>("TreeItem 1"), new TreeItem<>("TreeItem 2")));
treeTableView.setRoot(root);
stageLoader = new StageLoader(treeTableView);
}
@After
public void cleanup() {
stageLoader.dispose();
}
@Test
public void testIndentationOfCell() {
column.setCellFactory(col -> new TreeTableCell<>());
testXCoordinateIsAfterDisclosureNode();
}
@Test
public void testIndentationOfCellWithGraphic() {
column.setCellFactory(col -> new TreeTableCell<>() {
@Override
protected void updateItem(String item, boolean empty) {
super.updateItem(item, empty);
if (empty) {
setGraphic(null);
} else {
setGraphic(new Label("Graphic"));
}
}
});
testXCoordinateIsAfterDisclosureNode();
}
@Test
public void testIndentationOfCellWithText() {
column.setCellFactory(col -> new TreeTableCell<>() {
@Override
protected void updateItem(String item, boolean empty) {
super.updateItem(item, empty);
if (empty) {
setText(null);
} else {
setText("Text");
}
}
});
testXCoordinateIsAfterDisclosureNode();
}
@Test
public void testIndentationOfCellWithGraphicAndText() {
column.setCellFactory(col -> new TreeTableCell<>() {
@Override
protected void updateItem(String item, boolean empty) {
super.updateItem(item, empty);
if (empty) {
setGraphic(null);
setText(null);
} else {
setGraphic(new Label("Graphic"));
setText("Text");
}
}
});
testXCoordinateIsAfterDisclosureNode();
}
private void testXCoordinateIsAfterDisclosureNode() {
Toolkit.getToolkit().firePulse();
TreeTableRow<String> row = (TreeTableRow<String>) VirtualFlowTestUtils.getCell(treeTableView, 0);
TreeTableCell<String, String> cell = (TreeTableCell<String, String>) row.getChildrenUnmodifiable().get(1);
Node graphic = cell.getGraphic();
double x;
if (graphic != null) {
x = graphic.getLayoutX();
} else {
x = LabeledSkinBaseShim.get_text((LabeledSkinBase<TreeTableCell>) cell.getSkin()).getLayoutX();
}
double leftInset = cell.snappedLeftInset();
double disclosureNodeWidth = row.getDisclosureNode().prefWidth(-1);
double expectedX = leftInset + disclosureNodeWidth;
assertEquals(expectedX, x, 0);
}
}
