package test.com.sun.javafx.util;
import com.sun.javafx.util.Utils;
import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.junit.Test;
public class UtilsTest {
@Test
public void testSplit() {
String s = "VK_ENTER";
String[] split = Utils.split(s, "_");
assertEquals("Array content: " + Arrays.toString(split),2, split.length);
assertEquals("VK", split[0]);
assertEquals("ENTER", split[1]);
s = "VK_LEFT_ARROW";
split = Utils.split(s, "_");
assertEquals("Array content: " + Arrays.toString(split),3, split.length);
assertEquals("VK", split[0]);
assertEquals("LEFT", split[1]);
assertEquals("ARROW", split[2]);
s = "VK_LEFT_ARROW";
split = Utils.split(s, "VK_LEFT_ARROW");
assertEquals("Array content: " + Arrays.toString(split),0, split.length);
s = "VK_LEFT_ARROW";
split = Utils.split(s, "VK_LEFT_ARROW_EXT");
assertEquals("Array content: " + Arrays.toString(split),0, split.length);
}
@Test
public void testConvertUnicode() {
String s = "";
String r = Utils.convertUnicode(s);
assertEquals("", r);
s = "test";
r = Utils.convertUnicode(s);
assertEquals("test", r);
s = "hi\\u1234";
r = Utils.convertUnicode(s);
assertEquals("hi\u1234", r);
s = "\\u5678";
r = Utils.convertUnicode(s);
assertEquals("\u5678", r);
s = "hi\\u1234there\\u432112";
r = Utils.convertUnicode(s);
assertEquals("hi\u1234there\u432112", r);
s = "Hello\u5678There";
r = Utils.convertUnicode(s);
assertEquals("Hello\u5678There", r);
s = "\\this\\is\\a\\windows\\path";
r = Utils.convertUnicode(s);
assertEquals("\\this\\is\\a\\windows\\path", r);
s = "\\this\\is\\a\\12\\windows\\path";
r = Utils.convertUnicode(s);
assertEquals("\\this\\is\\a\\12\\windows\\path", r);
s = "u12u12";
r = Utils.convertUnicode(s);
assertEquals("u12u12", r);
s = "hello\nu1234\n";
r = Utils.convertUnicode(s);
assertEquals("hello\nu1234\n", r);
}
@Test
public void testConvertUnicodeFail2_2() {
}
@Test
public void testConvertUnicodeWrong2_2() {
String s = "hi\\u12";
String r = Utils.convertUnicode(s);
s = "\\this\\is\\a\\umm\\windows\\path";
r = Utils.convertUnicode(s);
}
@Test
public void testPointRelativeTo() {
VBox root = new VBox();
final Rectangle rectangle = new Rectangle(50, 50, 100, 100);
root.getChildren().add(rectangle);
Scene scene = new Scene(root,800,600);
Stage stage = new Stage();
stage.setX(0);
stage.setY(0);
stage.setScene(scene);
final Point2D res = Utils.pointRelativeTo(rectangle, 0, 0, HPos.CENTER, VPos.CENTER, 0, 0, false);
assertEquals(50, res.getX(), 1e-1);
assertEquals(50, res.getY(), 1e-1);
}
@Test
public void testPointRelativeTo_InSubScene() {
Group root = new Group();
Scene scene = new Scene(root,800,600);
VBox subRoot = new VBox();
SubScene subScene = new SubScene(subRoot, 100, 100);
subScene.setLayoutX(20);
subScene.setLayoutY(20);
root.getChildren().addAll(subScene);
final Rectangle rectangle = new Rectangle(50, 50, 100, 100);
subRoot.getChildren().add(rectangle);
Stage stage = new Stage();
stage.setX(0);
stage.setY(0);
stage.setScene(scene);
final Point2D res = Utils.pointRelativeTo(rectangle, 0, 0, HPos.CENTER, VPos.CENTER, 0, 0, false);
assertEquals(70, res.getX(), 1e-1);
assertEquals(70, res.getY(), 1e-1);
}
}
