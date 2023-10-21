package test.javafx.scene.control;
import com.sun.javafx.tk.Toolkit;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.StackPane;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
public class DialogTest {
private Dialog<ButtonType> dialog;
@Before
public void setUp() {
dialog = new Dialog<>();
}
@After
public void cleanUp() {
dialog.setResult(new ButtonType(""));
dialog.hide();
}
@Test
public void testDialogMaxHeight() {
int maxHeight = 400;
StackPane stackPane = new StackPane();
stackPane.setPrefHeight(700);
dialog.getDialogPane().setContent(stackPane);
dialog.getDialogPane().setMaxHeight(maxHeight);
dialog.show();
assertDialogPaneHeightEquals(maxHeight);
assertEquals(maxHeight, dialog.getDialogPane().getMaxHeight(), 0);
}
@Test
public void testDialogMinHeight() {
int minHeight = 400;
dialog.getDialogPane().setContent(new StackPane());
dialog.getDialogPane().setMinHeight(minHeight);
dialog.show();
assertDialogPaneHeightEquals(minHeight);
assertEquals(minHeight, dialog.getDialogPane().getMinHeight(), 0);
}
@Test
public void testDialogPrefHeight() {
int prefHeight = 400;
dialog.getDialogPane().setContent(new StackPane());
dialog.getDialogPane().setPrefHeight(prefHeight);
dialog.show();
assertDialogPaneHeightEquals(prefHeight);
assertEquals(prefHeight, dialog.getDialogPane().getPrefHeight(), 0);
}
private void assertDialogPaneHeightEquals(int height) {
Toolkit.getToolkit().firePulse();
assertEquals(height, dialog.getDialogPane().getHeight(), 0);
Toolkit.getToolkit().firePulse();
assertEquals(height, dialog.getDialogPane().getHeight(), 0);
}
}
