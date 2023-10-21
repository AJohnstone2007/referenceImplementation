package test.com.sun.javafx.scene.control.infrastructure;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static test.com.sun.javafx.scene.control.infrastructure.VirtualizedControlTestUtils.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
public class VirtualizedControlTestUtilsTest {
private Scene scene;
private Stage stage;
private Pane root;
int rows;
@Test
public void testFireMouseOnVerticalTrack() {
ListView<?> list = createAndShowListView();
ScrollBar scrollBar = getVerticalScrollBar(list);
assertEquals("sanity: initial value of scrollBar", 0, scrollBar.getValue(), 0.1);
fireMouseOnVerticalTrack(list);
assertTrue("mouse on track must have scrolled", scrollBar.getValue() > 0);
}
@Test
public void testFireMouseOnHorizontalTrack() {
ListView<?> list = createAndShowListView();
ScrollBar scrollBar = getHorizontalScrollBar(list);
assertEquals("sanity: initial value of scrollBar", 0, scrollBar.getValue(), 0.1);
fireMouseOnHorizontalTrack(list);
assertTrue("mouse on track must have scrolled", scrollBar.getValue() > 0);
}
@Test (expected=IllegalStateException.class)
public void testGetVerticalScrollBarThrowsWithoutSkin() {
ListView<?> list = new ListView<>();
getVerticalScrollBar(list);
}
@Test (expected=IllegalStateException.class)
public void testGetHorizontalScrollBarThrowsWithoutSkin() {
ListView<?> list = new ListView<>();
getHorizontalScrollBar(list);
}
@Test
public void testListViewEditing() {
ListView<?> control = createAndShowListView();
assertEquals(rows, control.getItems().size());
assertEquals(100, scene.getWidth(), 1);
assertEquals(330, scene.getHeight(), 1);
assertTrue("sanity: vertical scrollbar visible for list " ,
getHorizontalScrollBar(control).isVisible());
assertTrue("sanity: vertical scrollbar visible for list " ,
getVerticalScrollBar(control).isVisible());
}
private ListView<?> createAndShowListView() {
ObservableList<String> baseData = createData(rows, true);
ListView<String> control = new ListView<>(baseData);
showControl(control, true, 100, 330);
return control;
}
private ObservableList<String> createData(int size, boolean wide) {
ObservableList<String> data = FXCollections.observableArrayList();
String item = wide ? "something that really really guarantees a horizontal scrollbar is visible  " : "item";
for (int i = 0; i < size; i++) {
data.add(item + i);
}
return data;
}
protected void showControl(Control control, boolean focused) {
showControl(control, focused, -1, -1);
}
protected void showControl(Control control, boolean focused, double width, double height) {
if (root == null) {
root = new VBox();
if (width > 0) {
scene = new Scene(root, width, height);
} else {
scene = new Scene(root);
}
stage = new Stage();
stage.setScene(scene);
}
if (!root.getChildren().contains(control)) {
root.getChildren().add(control);
}
stage.show();
if (focused) {
stage.requestFocus();
control.requestFocus();
assertTrue(control.isFocused());
assertSame(control, scene.getFocusOwner());
}
}
@Before public void setup() {
Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
if (throwable instanceof RuntimeException) {
throw (RuntimeException)throwable;
} else {
Thread.currentThread().getThreadGroup().uncaughtException(thread, throwable);
}
});
rows = 60;
}
@After public void cleanup() {
if (stage != null) stage.hide();
Thread.currentThread().setUncaughtExceptionHandler(null);
}
}
