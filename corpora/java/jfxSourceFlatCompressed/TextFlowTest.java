package test.javafx.scene.text;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import test.com.sun.javafx.pgstub.StubToolkit;
import com.sun.javafx.tk.Toolkit;
import org.junit.Test;
public class TextFlowTest {
@Test public void testTabSize() {
Toolkit tk = (StubToolkit) Toolkit.getToolkit();
VBox root = new VBox();
Scene scene = new Scene(root);
Stage stage = new Stage();
stage.setScene(scene);
stage.setWidth(300);
stage.setHeight(200);
try {
Text text1 = new Text("\tfirst");
Text text2 = new Text("\tsecond");
TextFlow textFlow = new TextFlow(text1, text2);
textFlow.setPrefWidth(TextFlow.USE_COMPUTED_SIZE);
textFlow.setMaxWidth(TextFlow.USE_PREF_SIZE);
root.getChildren().addAll(textFlow);
stage.show();
tk.firePulse();
assertEquals(8, textFlow.getTabSize());
double widthT8 = textFlow.getBoundsInLocal().getWidth();
text1.setTabSize(4);
text2.setTabSize(3);
tk.getTextLayoutFactory().disposeLayout(tk.getTextLayoutFactory().getLayout());
tk.firePulse();
assertEquals(widthT8, textFlow.getBoundsInLocal().getWidth(), 0.0);
textFlow.setTabSize(1);
tk.firePulse();
double widthT1 = textFlow.getBoundsInLocal().getWidth();
assertTrue(widthT1 < widthT8);
textFlow.setTabSize(20);
tk.firePulse();
double widthT20 = textFlow.getBoundsInLocal().getWidth();
assertTrue(widthT20 > widthT8);
assertEquals(20, textFlow.getTabSize());
assertEquals(20, textFlow.tabSizeProperty().get());
textFlow.tabSizeProperty().set(10);
tk.firePulse();
double widthT10 = textFlow.getBoundsInLocal().getWidth();
assertTrue(widthT10 > widthT8);
assertTrue(widthT10 < widthT20);
assertEquals(10, textFlow.getTabSize());
assertEquals(10, textFlow.tabSizeProperty().get());
assertEquals(4, text1.getTabSize());
assertEquals(3, text2.getTabSize());
textFlow.tabSizeProperty().set(0);
assertEquals(0, textFlow.tabSizeProperty().get());
assertEquals(0, textFlow.getTabSize());
tk.firePulse();
double widthT0Clamp = textFlow.getBoundsInLocal().getWidth();
assertEquals(widthT1, widthT0Clamp, 0.5);
} finally {
stage.hide();
}
}
}