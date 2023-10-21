package test.javafx.scene.control;
import test.com.sun.javafx.pgstub.StubToolkit;
import com.sun.javafx.tk.Toolkit;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import junit.framework.Assert;
import org.junit.Test;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
public class MiscellaneousTests {
@Test
public void test_RT_31168() {
Button button = new Button("RT-31168");
Rectangle rectangle = new Rectangle(50,50);
Group container = new Group();
container.getChildren().add(rectangle);
Scene scene = new Scene(new Group(container, new Button("button")));
Stage stage = new Stage();
stage.setScene(scene);
stage.show();
((StubToolkit)Toolkit.getToolkit()).fireTestPulse();
container.getChildren().set(0, button);
container.getChildren().set(0, rectangle);
((StubToolkit)Toolkit.getToolkit()).fireTestPulse();
assertNull(button.getBackground());
container.getChildren().set(0, button);
((StubToolkit)Toolkit.getToolkit()).fireTestPulse();
assertNotNull(button.getBackground());
}
@Test public void test_RT_33103() {
HBox box = new HBox();
TextField field = new TextField();
Label badLabel = new Label("Field:", field);
box.getChildren().addAll(badLabel, field);
Scene scene = new Scene(box);
Stage stage = new Stage();
stage.setScene(scene);
stage.show();
((StubToolkit)Toolkit.getToolkit()).fireTestPulse();
assertSame(badLabel, field.getParent());
}
@Test public void test_RT_33080() {
final HBox root = new HBox(10);
final RadioButton rb1 = new RadioButton("RB1");
final RadioButton rb2 = new RadioButton("RB2");
root.getChildren().addAll(rb1, rb2);
Scene scene = new Scene(root);
Stage stage = new Stage();
stage.setScene(scene);
stage.show();
((StubToolkit)Toolkit.getToolkit()).fireTestPulse();
rb1.setSelected(true);
((StubToolkit)Toolkit.getToolkit()).fireTestPulse();
rb1.setFont(new Font("system", 22));
rb2.setFont(new Font("system", 22));
((StubToolkit) Toolkit.getToolkit()).fireTestPulse();
rb1.setSelected(false);
((StubToolkit) Toolkit.getToolkit()).fireTestPulse();
Bounds b1 = rb1.getLayoutBounds();
Bounds b2 = rb2.getLayoutBounds();
Assert.assertEquals(rb1.getWidth(), rb2.getWidth(), 0.00001);
}
}
