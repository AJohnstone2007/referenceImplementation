package test.javafx.fxml;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
public class RT_36633Test {
@Test public void test_rt36633_tableColumn() throws Exception {
VBox pane = FXMLLoader.load(getClass().getResource("rt_36633.fxml"));
TableView tableView = (TableView) pane.getChildren().get(0);
TableColumn column = (TableColumn) tableView.getColumns().get(0);
assertEquals("rt36633_tableColumn", column.getId());
}
@Test public void test_rt36633_tab() throws Exception {
VBox pane = FXMLLoader.load(getClass().getResource("rt_36633.fxml"));
TabPane tabPane = (TabPane) pane.getChildren().get(1);
Tab tab = tabPane.getTabs().get(0);
assertEquals("rt36633_tab", tab.getId());
}
@Test public void test_rt36633_menuItem() throws Exception {
VBox pane = FXMLLoader.load(getClass().getResource("rt_36633.fxml"));
MenuBar menuBar = (MenuBar) pane.getChildren().get(2);
Menu menu = menuBar.getMenus().get(0);
MenuItem menuItem = menu.getItems().get(0);
assertEquals("rt36633_menu", menu.getId());
assertEquals("rt36633_menuItem", menuItem.getId());
}
}
