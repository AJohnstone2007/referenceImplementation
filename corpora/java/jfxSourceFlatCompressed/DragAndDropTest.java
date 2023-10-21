package test.robot.com.sun.glass.ui.monocle;
import com.sun.glass.ui.monocle.TestLogShim;
import test.robot.com.sun.glass.ui.monocle.TestApplication;
import test.robot.com.sun.glass.ui.monocle.input.devices.TestTouchDevice;
import test.robot.com.sun.glass.ui.monocle.input.devices.TestTouchDevices;
import javafx.scene.Node;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.InputEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.shape.Rectangle;
import org.junit.Test;
import org.junit.runners.Parameterized;
import java.util.Collection;
import test.com.sun.glass.ui.monocle.TestRunnable;
public class DragAndDropTest extends ParameterizedTestBase {
public DragAndDropTest(TestTouchDevice device) {
super(device);
}
@Parameterized.Parameters
public static Collection<Object[]> data() {
return TestTouchDevices.getTouchDeviceParameters(1);
}
@Test
public void testDragOneNodeToAnother() throws Exception {
TestRunnable.invokeAndWait(() -> {
Node n1 = new Rectangle(10, 10, 10, 10);
Node n2 = new Rectangle(210, 10, 10, 10);
TestApplication.getRootGroup().getChildren().add(n1);
TestApplication.getRootGroup().getChildren().add(n2);
n1.setOnDragDetected((event) -> {
TestLogShim.log("Drag detected on n1");
Dragboard db = n1.startDragAndDrop(TransferMode.ANY);
ClipboardContent content = new ClipboardContent();
content.putString("");
db.setContent(content);
});
n2.setOnDragEntered((e) -> TestLogShim.log("Drag entered on n2"));
n2.setOnDragOver((event) -> {
TestLogShim.log("Drag over on n2");
event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
});
n2.setOnDragDropped((e) -> TestLogShim.log("Drag dropped on n2"));
n1.setOnDragDone((e) -> TestLogShim.log("Drag done on n1"));
n1.addEventHandler(InputEvent.ANY, (e) -> TestLogShim.log(e.toString()));
n2.addEventHandler(InputEvent.ANY, (e) -> TestLogShim.log(e.toString()));
});
try {
int p = device.addPoint(15, 15);
device.sync();
device.setPoint(p, 110, 15);
device.sync();
TestLogShim.waitForLogContaining("Drag detected on n1");
TestLogShim.clear();
device.setPoint(p, 215, 15);
device.sync();
TestLogShim.waitForLogContaining("Drag entered on n2");
TestLogShim.waitForLogContaining("Drag over on n2");
device.removePoint(p);
device.sync();
TestLogShim.waitForLogContaining("Drag dropped on n2");
TestLogShim.waitForLogContaining("Drag done on n1");
} finally {
TestRunnable.invokeAndWait(() -> TestApplication.getRootGroup().getChildren().clear());
}
}
}
