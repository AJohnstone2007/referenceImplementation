package test.javafx.scene.control.skin;
import com.sun.javafx.tk.Toolkit;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.skin.TableHeaderRow;
import javafx.scene.control.skin.TableViewSkin;
import javafx.stage.Stage;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
public class TableViewSkinTest {
@Test
public void test_JDK_8188164() {
TableView<String> tableView = new TableView<>();
for (int i = 0; i < 5; i++) {
TableColumn<String, String> column = new TableColumn<>("Col " + i);
tableView.getColumns().add(column);
}
Scene scene = new Scene(tableView);
scene.getStylesheets().add(TableViewSkinTest.class.getResource("TableViewSkinTest.css").toExternalForm());
Toolkit tk = Toolkit.getToolkit();
Stage stage = new Stage();
stage.setScene(scene);
stage.setWidth(500);
stage.setHeight(400);
stage.centerOnScreen();
stage.show();
tk.firePulse();
TableHeaderRow header = (TableHeaderRow)tableView.lookup("TableHeaderRow");
assertEquals("Table Header height specified in CSS",
100.0, header.getHeight(), 0.001);
}
}
