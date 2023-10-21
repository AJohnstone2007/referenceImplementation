package test.javafx.scene.control.cell;
import java.util.Arrays;
import java.util.Collection;
import javafx.scene.control.Cell;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;
@RunWith(Parameterized.class)
public class ParameterisedPrebuiltCellTest {
@Parameters public static Collection implementations() {
return Arrays.asList(new Object[][] {
{ CheckBoxListCell.class },
{ CheckBoxTableCell.class },
{ CheckBoxTreeCell.class },
{ CheckBoxTreeTableCell.class },
});
}
private Class<? extends Cell> cellClass;
private Cell cell;
private int count = 0;
public ParameterisedPrebuiltCellTest(Class<? extends Cell> cellClass) {
this.cellClass = cellClass;
}
@Before public void setup() throws Exception {
count = 0;
cell = cellClass.getDeclaredConstructor().newInstance();
}
@Test public void testSetText() {
assertNull(cell.getText());
cell.setText("TEST");
assertEquals("TEST", cell.getText());
}
@Test public void testTextProperty() {
assertEquals(0, count);
cell.textProperty().addListener((observable, oldValue, newValue) -> {
count++;
});
cell.setText("TEST");
assertEquals(1, count);
cell.setText("TEST");
assertEquals(1, count);
cell.setText("TEST 2");
assertEquals(2, count);
cell.textProperty().set("TEST");
assertEquals(3, count);
}
@Test public void testSetGraphic() {
Rectangle rect = new Rectangle(10, 10, Color.RED);
cell.setGraphic(rect);
assertEquals(rect, cell.getGraphic());
}
@Test public void testGraphicProperty() {
assertEquals(0, count);
cell.graphicProperty().addListener((observable, oldValue, newValue) -> {
count++;
});
Rectangle rect1 = new Rectangle(10, 10, Color.RED);
Rectangle rect2 = new Rectangle(10, 10, Color.GREEN);
cell.setGraphic(rect1);
assertEquals(1, count);
cell.setGraphic(rect1);
assertEquals(1, count);
cell.setGraphic(rect2);
assertEquals(2, count);
cell.graphicProperty().set(rect1);
assertEquals(3, count);
}
}
