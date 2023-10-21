package test.javafx.scene;
import java.util.Arrays;
import java.util.Collection;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.Glow;
import javafx.scene.input.InputMethodRequests;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import com.sun.javafx.event.EventHandlerManager;
import test.com.sun.javafx.test.PropertiesTestBase;
import javafx.geometry.NodeOrientation;
import javafx.scene.CacheHint;
import javafx.scene.Cursor;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.Node;
@RunWith(Parameterized.class)
public final class Node_properties_Test extends PropertiesTestBase {
@Parameters
public static Collection data() {
final Group testParent = new Group();
final Node testNode = new Rectangle();
testParent.getChildren().add(testNode);
final EventHandler testEventHandler =
event -> {
};
return Arrays.asList(new Object[] {
config(testNode, "id", "rect_1", "rect_2"),
config(testNode, "style", "style_1", "style_2"),
config(testNode, "visible", true, false),
config(testNode, "cursor", Cursor.DEFAULT, Cursor.CROSSHAIR),
config(testNode, "opacity", 1.0, 0.5),
config(testNode, "viewOrder", 0.0, 0.5),
config(testNode, "blendMode",
BlendMode.SRC_OVER, BlendMode.SRC_ATOP),
config(testNode, "clip", null, new Rectangle(0, 0, 100, 100)),
config(testNode, "cache", false, true),
config(testNode, "cacheHint", CacheHint.QUALITY, CacheHint.SPEED),
config(testNode, "effect", null, new Glow()),
config(testNode, "depthTest", DepthTest.DISABLE, DepthTest.ENABLE),
config(testNode, "disable", false, true),
config(testNode, "pickOnBounds", false, true),
config(testParent, "disable", false, true,
testNode, "disabled", false, true),
config(testNode, "onDragEntered", null, testEventHandler),
config(testNode, "onDragExited", null, testEventHandler),
config(testNode, "onDragOver", null, testEventHandler),
config(testNode, "onDragDropped", null, testEventHandler),
config(testNode, "onDragDone", null, testEventHandler),
config(testNode, "managed", false, true),
config(testNode, "layoutX", 0.0, 100.0),
config(testNode, "layoutY", 0.0, 100.0),
config(testNode, "translateX", 0.0, 100.0),
config(testNode, "translateY", 0.0, 100.0),
config(testNode, "translateZ", 0.0, 100.0),
config(testNode, "scaleX", 1.0, 0.5),
config(testNode, "scaleY", 1.0, 0.5),
config(testNode, "scaleZ", 1.0, 0.5),
config(testNode, "rotate", 0.0, 45.0),
config(testNode, "rotationAxis", Rotate.Z_AXIS, Rotate.X_AXIS),
config(testNode, "mouseTransparent", false, true),
config(testNode, "onMouseClicked", null, testEventHandler),
config(testNode, "onMouseDragged", null, testEventHandler),
config(testNode, "onMouseEntered", null, testEventHandler),
config(testNode, "onMouseExited", null, testEventHandler),
config(testNode, "onMouseMoved", null, testEventHandler),
config(testNode, "onMousePressed", null, testEventHandler),
config(testNode, "onMouseReleased", null, testEventHandler),
config(testNode, "onDragDetected", null, testEventHandler),
config(testNode, "onKeyPressed", null, testEventHandler),
config(testNode, "onKeyReleased", null, testEventHandler),
config(testNode, "onKeyTyped", null, testEventHandler),
config(testNode, "onInputMethodTextChanged",
null, testEventHandler),
config(testNode, "inputMethodRequests",
null,
new InputMethodRequests() {
@Override
public Point2D getTextLocation(final int offset) {
return new Point2D(0, 0);
}
@Override
public int getLocationOffset(final int x, final int y) {
return 0;
}
@Override
public void cancelLatestCommittedText() {
}
@Override
public String getSelectedText() {
return "";
}
}),
config(testNode, "focusTraversable", false, true),
config(testNode, "eventDispatcher",
null,
new EventHandlerManager(null)),
config(testNode,
"nodeOrientation", NodeOrientation.INHERIT,
NodeOrientation.RIGHT_TO_LEFT,
"effectiveNodeOrientation", NodeOrientation.LEFT_TO_RIGHT,
NodeOrientation.RIGHT_TO_LEFT),
config(testParent, "nodeOrientation",
NodeOrientation.LEFT_TO_RIGHT,
NodeOrientation.RIGHT_TO_LEFT,
testNode, "effectiveNodeOrientation",
NodeOrientation.LEFT_TO_RIGHT,
NodeOrientation.RIGHT_TO_LEFT)
});
}
public Node_properties_Test(final Configuration configuration) {
super(configuration);
}
}
