package test.javafx.scene.control;
import org.junit.Test;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTablePosition;
public class TablePositionBaseTest {
@Test
public void testNullTable() {
new TablePosition<>(null, 2, new TableColumn<>());
}
@Test
public void testNullTreeTable() {
new TreeTablePosition<>(null, 2, new TreeTableColumn<>());
}
}
