package test.robot.com.sun.glass.ui.monocle;
import com.sun.glass.ui.monocle.TestLogShim;
import test.robot.com.sun.glass.ui.monocle.TestApplication;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.robot.Robot;
import javafx.stage.Modality;
import javafx.stage.Stage;
import junit.framework.AssertionFailedError;
import org.junit.*;
import org.junit.rules.TestName;
public class ModalDialogTest {
@Rule
public TestName name = new TestName();
@Before
public void setUpScreen() throws Exception {
TestLogShim.reset();
TestLogShim.log(name.getMethodName());
TestApplication.showFullScreenScene();
}
@Test
public void test1() throws Exception {
Stage rootStage = TestApplication.getStage();
rootStage.getScene().setOnMouseClicked(
(e) -> TestLogShim.format("Clicked at %.0f, %.0f",
e.getScreenX(), e.getScreenY()));
Platform.runLater(() -> {
final Stage p = new Stage();
p.initOwner(rootStage);
p.initModality(Modality.APPLICATION_MODAL);
p.setX(0);
p.setY(0);
p.setWidth(200);
p.setHeight(200);
p.setScene(new Scene(new Group()));
p.getScene().setOnMouseClicked(
(e) -> TestLogShim.format("Clicked at %.0f, %.0f",
e.getScreenX(), e.getScreenY()));
p.show();
});
TestLogShim.clear();
Platform.runLater(() -> {
Robot robot = new Robot();
robot.mouseMove(300, 400);
robot.mousePress(MouseButton.PRIMARY);
robot.mouseRelease(MouseButton.PRIMARY);
robot.mouseMove(100, 100);
robot.mousePress(MouseButton.PRIMARY);
robot.mouseRelease(MouseButton.PRIMARY);
});
TestLogShim.waitForLog("Clicked at 100, 100");
if (TestLogShim.countLog("Clicked at 300, 400") != 0) {
throw new AssertionFailedError("Disabled window should not receive mouse events!");
}
}
}
