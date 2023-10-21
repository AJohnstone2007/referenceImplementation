package test.javafx.scene.control;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import test.com.sun.javafx.scene.control.infrastructure.StageLoader;
import test.util.memory.JMemoryBuddy;
import static test.com.sun.javafx.scene.control.infrastructure.ControlTestUtils.*;
import org.junit.Test;
import org.junit.BeforeClass;
import static org.junit.Assert.assertEquals;
public class ControlAcceleratorSupportTest {
@Test
public void testNumberOfListenersByRemovingAndAddingMenuItems() {
Menu menu1 = new Menu("1");
MenuItem item11 = new MenuItem("Item 1");
MenuItem item12 = new MenuItem("Item 2");
menu1.getItems().addAll(item11, item12);
Menu menu2 = new Menu("2");
MenuItem item21 = new MenuItem("Item 1");
MenuItem item22 = new MenuItem("Item 2");
menu2.getItems().addAll(item21, item22);
MenuBar menuBar = new MenuBar();
menuBar.getMenus().addAll(menu1, menu2);
BorderPane pane = new BorderPane();
pane.setTop(menuBar);
StageLoader sl = new StageLoader(pane);
assertEquals(1, getListenerCount(item11.acceleratorProperty()));
assertEquals(1, getListenerCount(item12.acceleratorProperty()));
assertEquals(1, getListenerCount(item21.acceleratorProperty()));
assertEquals(1, getListenerCount(item22.acceleratorProperty()));
menu1.getItems().clear();
assertEquals(0, getListenerCount(item11.acceleratorProperty()));
assertEquals(0, getListenerCount(item12.acceleratorProperty()));
assertEquals(1, getListenerCount(item21.acceleratorProperty()));
assertEquals(1, getListenerCount(item22.acceleratorProperty()));
menu2.getItems().clear();
assertEquals(0, getListenerCount(item11.acceleratorProperty()));
assertEquals(0, getListenerCount(item12.acceleratorProperty()));
assertEquals(0, getListenerCount(item21.acceleratorProperty()));
assertEquals(0, getListenerCount(item22.acceleratorProperty()));
menu1.getItems().addAll(item11, item12);
assertEquals(1, getListenerCount(item11.acceleratorProperty()));
assertEquals(1, getListenerCount(item12.acceleratorProperty()));
assertEquals(0, getListenerCount(item21.acceleratorProperty()));
assertEquals(0, getListenerCount(item22.acceleratorProperty()));
menu2.getItems().addAll(item21, item22);
assertEquals(1, getListenerCount(item11.acceleratorProperty()));
assertEquals(1, getListenerCount(item12.acceleratorProperty()));
assertEquals(1, getListenerCount(item21.acceleratorProperty()));
assertEquals(1, getListenerCount(item22.acceleratorProperty()));
menu2.getItems().clear();
menu1.getItems().clear();
assertEquals(0, getListenerCount(item11.acceleratorProperty()));
assertEquals(0, getListenerCount(item12.acceleratorProperty()));
assertEquals(0, getListenerCount(item21.acceleratorProperty()));
assertEquals(0, getListenerCount(item22.acceleratorProperty()));
sl.dispose();
}
@Test
public void testMemoryLeak_JDK_8274022() {
JMemoryBuddy.memoryTest(checker -> {
MenuItem menuItem = new MenuItem("LeakingItem");
MenuBar menuBar = new MenuBar(new Menu("MENU_BAR", null, menuItem));
StageLoader sl = new StageLoader(new StackPane(menuBar));
sl.getStage().close();
menuItem.setOnAction((e) -> { menuItem.fire();});
checker.assertCollectable(menuItem);
});
}
}
