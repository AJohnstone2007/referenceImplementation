package test.javafx.fxml;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.shape.Rectangle;
import org.junit.Assert;
import org.junit.Test;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
public class FXMLLoaderTest_FieldInjectionTest {
public static class SuperController {
@FXML private Parent root;
public final Parent getRoot() {
return root;
}
}
public static class SubController_1 extends SuperController {
@FXML private Rectangle rectangle;
public Rectangle getRectangle() {
return rectangle;
}
}
public static class SubController_2 extends SuperController {
@FXML private Node root;
@FXML private Rectangle rectangle;
public Rectangle getRectangle() {
return rectangle;
}
public Node getRootFromSub() {
return root;
}
}
@Test
public void testFieldInjectionInSuperClass() throws IOException {
FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("simple.fxml"));
SubController_1 controller = new SubController_1();
fxmlLoader.setController(controller);
fxmlLoader.load();
assertNotNull(controller.getRectangle());
assertNotNull(controller.getRoot());
}
@Test
public void testFieldInjectionInSuperClassNotSuppressed() throws IOException {
FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("simple.fxml"));
SubController_2 controller = new SubController_2();
fxmlLoader.setController(controller);
fxmlLoader.load();
assertNotNull(controller.getRectangle());
assertNotNull(controller.getRoot());
assertNotNull(controller.getRootFromSub());
assertEquals(controller.getRoot(), controller.getRootFromSub());
}
}
