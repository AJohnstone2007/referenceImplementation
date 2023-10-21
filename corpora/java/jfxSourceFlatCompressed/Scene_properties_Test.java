package test.javafx.scene;
import com.sun.javafx.cursor.CursorFrame;
import com.sun.javafx.event.EventHandlerManager;
import java.util.Arrays;
import java.util.Collection;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.NodeOrientation;
import javafx.scene.Cursor;
import javafx.scene.Cursor;
import javafx.scene.CursorShim;
import javafx.scene.Group;
import javafx.scene.Group;
import javafx.scene.ParallelCamera;
import javafx.scene.ParallelCamera;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PerspectiveCamera;
import javafx.scene.paint.Color;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.test.PropertiesTestBase;
import test.com.sun.javafx.test.PropertiesTestBase.Configuration;
import test.com.sun.javafx.test.objects.TestScene;
import test.com.sun.javafx.test.objects.TestStage;
import javafx.scene.layout.Pane;
@RunWith(Parameterized.class)
public final class Scene_properties_Test extends PropertiesTestBase {
@Parameters
public static Collection data() {
final TestScene testScene = new TestScene(new Group());
final TestStage testStage1 = new TestStage("STAGE_1");
final TestStage testStage2 = new TestStage("STAGE_2");
final EventHandler testEventHandler =
event -> {
};
final Configuration windowCfg =
new Configuration(testScene,
"_window", testStage1, testStage2,
"window", testStage1, testStage2);
windowCfg.setAllowMultipleNotifications(true);
return Arrays.asList(new Object[] {
config(windowCfg),
config(testScene, "camera", null, new ParallelCamera()),
config(testScene, "fill", Color.WHITE, Color.BLACK),
config(testScene, "fill", null, Color.TRANSPARENT),
config(testScene, "root", new Group(), new Pane()),
config(testScene, "cursor", null, CursorShim.getCursor("TestCursor")),
config(testScene, "cursor", Cursor.DEFAULT, Cursor.CROSSHAIR),
config(testScene, "eventDispatcher",
null,
new EventHandlerManager(null)),
config(testScene, "camera", null, new PerspectiveCamera()),
config(testScene, "onMouseClicked", null, testEventHandler),
config(testScene, "onMouseDragged", null, testEventHandler),
config(testScene, "onMouseEntered", null, testEventHandler),
config(testScene, "onMouseExited", null, testEventHandler),
config(testScene, "onMouseMoved", null, testEventHandler),
config(testScene, "onMousePressed", null, testEventHandler),
config(testScene, "onMouseReleased", null, testEventHandler),
config(testScene, "onDragDetected", null, testEventHandler),
config(testScene, "onDragEntered", null, testEventHandler),
config(testScene, "onDragExited", null, testEventHandler),
config(testScene, "onDragOver", null, testEventHandler),
config(testScene, "onDragDropped", null, testEventHandler),
config(testScene, "onDragDone", null, testEventHandler),
config(testScene, "onKeyPressed", null, testEventHandler),
config(testScene, "onKeyReleased", null, testEventHandler),
config(testScene, "onKeyTyped", null, testEventHandler),
config(testScene, "onContextMenuRequested", null, testEventHandler),
config(testScene, "onDragDetected", null, testEventHandler),
config(testScene, "onDragDone", null, testEventHandler),
config(testScene, "onDragDropped", null, testEventHandler),
config(testScene, "onDragEntered", null, testEventHandler),
config(testScene, "onDragExited", null, testEventHandler),
config(testScene, "onDragOver", null, testEventHandler),
config(testScene, "onMouseDragEntered", null, testEventHandler),
config(testScene, "onMouseDragExited", null, testEventHandler),
config(testScene, "onMouseDragOver", null, testEventHandler),
config(testScene, "onMouseDragReleased", null, testEventHandler),
config(testScene, "onRotate", null, testEventHandler),
config(testScene, "onRotationStarted", null, testEventHandler),
config(testScene, "onRotationFinished", null, testEventHandler),
config(testScene, "onZoom", null, testEventHandler),
config(testScene, "onZoomStarted", null, testEventHandler),
config(testScene, "onZoomFinished", null, testEventHandler),
config(testScene, "onScroll", null, testEventHandler),
config(testScene, "onScrollStarted", null, testEventHandler),
config(testScene, "onScrollFinished", null, testEventHandler),
config(testScene, "onSwipeLeft", null, testEventHandler),
config(testScene, "onSwipeRight", null, testEventHandler),
config(testScene, "onSwipeUp", null, testEventHandler),
config(testScene, "onSwipeDown", null, testEventHandler),
config(testScene, "onTouchPressed", null, testEventHandler),
config(testScene, "onTouchReleased", null, testEventHandler),
config(testScene, "onTouchMoved", null, testEventHandler),
config(testScene, "onTouchStationary", null, testEventHandler),
config(testScene, "onInputMethodTextChanged",
null, testEventHandler),
config(testScene,
"nodeOrientation", NodeOrientation.INHERIT,
NodeOrientation.RIGHT_TO_LEFT,
"effectiveNodeOrientation", NodeOrientation.LEFT_TO_RIGHT,
NodeOrientation.RIGHT_TO_LEFT)
});
}
public Scene_properties_Test(final Configuration configuration) {
super(configuration);
}
}
