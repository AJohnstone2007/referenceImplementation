package test.javafx.scene.text;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeTrue;
import javafx.geometry.VPos;
import test.javafx.scene.NodeTest;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.layout.HBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import test.com.sun.javafx.pgstub.StubToolkit;
import com.sun.javafx.tk.Toolkit;
import org.junit.Test;
public class TextTest {
@Test public void testCtors() {
Text t1 = new Text();
assertEquals("", t1.getText());
Text t2 = new Text("test content");
assertEquals("test content", t2.getText());
Text t3 = new Text(10, 20, "2");
assertEquals(10f, t3.getX(), 0);
assertEquals(20f, t3.getY(), 0);
assertEquals("2", t3.getText());
}
@Test public void testSettingNullText() {
Text t = new Text();
t.setText(null);
assertEquals("", t.getText());
t.textProperty().set(null);
assertEquals("", t.getText());
t.setText("1");
assertEquals("1", t.getText());
assertEquals("1", t.textProperty().get());
t.setText(null);
assertEquals("", t.getText());
t.textProperty().set(null);
assertEquals("", t.getText());
}
@Test public void testDefaultTextNotNull() {
Text t = new Text();
assertEquals("", t.getText());
assertEquals("", t.textProperty().get());
}
@Test public void testStoreFont() {
Text t = new Text();
Font f = new Font(44);
assertEquals(Font.getDefault(), t.getFont());
t.setFont(f);
assertEquals(44f, t.getBaselineOffset(), 0);
}
@Test public void toStringShouldReturnNonEmptyString() {
String s = new Text().toString();
assertNotNull(s);
assertFalse(s.isEmpty());
}
@Test public void testTabSize() {
assumeTrue(Boolean.getBoolean("unstable.test"));
Toolkit tk = (StubToolkit)Toolkit.getToolkit();
HBox root = new HBox();
Scene scene = new Scene(root);
Stage stage = new Stage();
stage.setScene(scene);
stage.setWidth(300);
stage.setHeight(200);
try {
Text text = new Text("\tHello");
root.getChildren().addAll(text);
stage.show();
tk.firePulse();
assertEquals(8, text.getTabSize());
double widthT8 = text.getBoundsInLocal().getWidth();
text.setTabSize(1);
tk.firePulse();
double widthT1 = text.getBoundsInLocal().getWidth();
double widthSpace = (widthT8 - widthT1) / 7;
assertTrue(widthSpace > 0);
text.setTabSize(4);
tk.firePulse();
double widthT4 = text.getBoundsInLocal().getWidth();
double expected = widthT8 - 4 * widthSpace;
assertEquals(expected, widthT4, 0.5);
assertEquals(4, text.getTabSize());
assertEquals(4, text.tabSizeProperty().get());
text.tabSizeProperty().set(5);
assertEquals(5, text.tabSizeProperty().get());
assertEquals(5, text.getTabSize());
tk.firePulse();
double widthT5 = text.getBoundsInLocal().getWidth();
expected = widthT8 - 3 * widthSpace;
assertEquals(expected, widthT5, 0.5);
text.tabSizeProperty().set(0);
assertEquals(0, text.tabSizeProperty().get());
assertEquals(0, text.getTabSize());
tk.firePulse();
double widthT0Clamp = text.getBoundsInLocal().getWidth();
assertEquals(widthT1, widthT0Clamp, 0.5);
} finally {
stage.hide();
}
}
}
