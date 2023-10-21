package test.robot.javafx.scene.layout;
import javafx.geometry.BoundingBox;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Ellipse;
import javafx.stage.Stage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import test.robot.testharness.VisualTestBase;
public class LayoutTest extends VisualTestBase {
private static class CustomPane extends Pane {
private int callCounter = 0;
@Override
public void requestLayout() {
super.requestLayout();
callCounter++;
}
public void assertAndClearLayoutCallCounter(int count) {
assertEquals(count, callCounter);
callCounter = 0;
}
public void clearLayoutCallCounter() {
callCounter = 0;
}
public int getLayoutCallCounter() {
return callCounter;
}
}
final int WIDTH = 800;
final int HEIGHT = 400;
private Stage testStage;
private Scene testScene;
@Test(timeout = 15000)
public void testRequestLayout() {
final Label l1 = new Label("1 2");
final Ellipse e1 = new Ellipse(20, 30);
final CustomPane cp = new CustomPane();
runAndWait(() -> {
e1.setOpacity(0.5);
cp.getChildren().addAll(l1, e1);
e1.radiusXProperty().bind(l1.widthProperty());
e1.radiusYProperty().bind(l1.heightProperty());
Pane topPane = new Pane();
topPane.getChildren().addAll(cp);
cp.relocate(200, 100);
cp.setStyle("-fx-border-color: BLACK;");
testStage = getStage();
testStage.setTitle("Test CustomPane's requestLayout() call");
testScene = new Scene(topPane, WIDTH, HEIGHT);
testStage.setScene(testScene);
testStage.show();
});
waitFirstFrame();
runAndWait(() -> {
cp.assertAndClearLayoutCallCounter(6);
l1.setText("A B C D E F G");
cp.assertAndClearLayoutCallCounter(2);
});
waitNextFrame();
runAndWait(() -> {
cp.assertAndClearLayoutCallCounter(0);
l1.setText("1 2 3");
cp.assertAndClearLayoutCallCounter(2);
});
waitNextFrame();
runAndWait(() -> {
cp.assertAndClearLayoutCallCounter(0);
l1.setLayoutX(10);
cp.assertAndClearLayoutCallCounter(1);
});
waitNextFrame();
runAndWait(() -> {
cp.assertAndClearLayoutCallCounter(0);
e1.setLayoutY(10);
cp.assertAndClearLayoutCallCounter(1);
});
waitNextFrame();
runAndWait(() -> {
cp.assertAndClearLayoutCallCounter(0);
e1.setLayoutY(10);
cp.assertAndClearLayoutCallCounter(0);
});
waitNextFrame();
runAndWait(() -> {
cp.assertAndClearLayoutCallCounter(0);
e1.setLayoutX(10);
l1.setLayoutY(10);
cp.assertAndClearLayoutCallCounter(2);
});
waitNextFrame();
runAndWait(() -> {
cp.assertAndClearLayoutCallCounter(0);
e1.resizeRelocate(5, 5, 30, 40);
l1.resizeRelocate(5, 15, 20, 50);
cp.assertAndClearLayoutCallCounter(8);
});
}
@Test(timeout = 15000)
public void testStackPane() {
final Label l1 = new Label("1 2");
final Ellipse e1 = new Ellipse(20, 30);
final StackPane sp = new StackPane();
runAndWait(() -> {
e1.setOpacity(0.5);
sp.getChildren().addAll(l1, e1);
e1.radiusXProperty().bind(l1.widthProperty());
e1.radiusYProperty().bind(l1.heightProperty());
Pane topPane = new Pane();
topPane.getChildren().addAll(sp);
sp.relocate(200, 100);
sp.setStyle("-fx-border-color: RED;");
testStage = getStage();
testStage.setTitle("Test StackPane's layoutBounds update");
testScene = new Scene(topPane, WIDTH, HEIGHT);
testStage.setScene(testScene);
testStage.show();
});
waitFirstFrame();
runAndWait(() -> {
BoundingBox e1Bounds = (BoundingBox) e1.getLayoutBounds();
BoundingBox l1Bounds = (BoundingBox) l1.getLayoutBounds();
BoundingBox testbb = new BoundingBox(0, 0, 0,
Math.max(e1Bounds.getWidth(), l1Bounds.getWidth()),
Math.max(e1Bounds.getHeight(), l1Bounds.getHeight()),
Math.max(e1Bounds.getDepth(), l1Bounds.getDepth()));
BoundingBox bb = (BoundingBox) sp.getLayoutBounds();
assertTrue(bb.contains(testbb));
l1.setText("A B C D E F G");
});
waitNextFrame();
runAndWait(() -> {
BoundingBox e1Bounds = (BoundingBox) e1.getLayoutBounds();
BoundingBox l1Bounds = (BoundingBox) l1.getLayoutBounds();
BoundingBox testbb = new BoundingBox(0, 0, 0,
Math.max(e1Bounds.getWidth(), l1Bounds.getWidth()),
Math.max(e1Bounds.getHeight(), l1Bounds.getHeight()),
Math.max(e1Bounds.getDepth(), l1Bounds.getDepth()));
BoundingBox bb = (BoundingBox) sp.getLayoutBounds();
assertTrue(bb.contains(testbb));
l1.setText("1 2 3");
});
waitNextFrame();
runAndWait(() -> {
BoundingBox e1Bounds = (BoundingBox) e1.getLayoutBounds();
BoundingBox l1Bounds = (BoundingBox) l1.getLayoutBounds();
BoundingBox testbb = new BoundingBox(0, 0, 0,
Math.max(e1Bounds.getWidth(), l1Bounds.getWidth()),
Math.max(e1Bounds.getHeight(), l1Bounds.getHeight()),
Math.max(e1Bounds.getDepth(), l1Bounds.getDepth()));
BoundingBox bb = (BoundingBox) sp.getLayoutBounds();
assertTrue(bb.contains(testbb));
});
}
@Test(timeout = 15000)
public void testVBox() {
final Label l1 = new Label("1 2");
final Ellipse e1 = new Ellipse(20, 30);
final VBox vBox = new VBox();
runAndWait(() -> {
e1.setOpacity(0.5);
vBox.getChildren().addAll(l1, e1);
e1.radiusXProperty().bind(l1.widthProperty());
e1.radiusYProperty().bind(l1.heightProperty());
Pane topPane = new Pane();
topPane.getChildren().addAll(vBox);
vBox.relocate(200, 100);
vBox.setStyle("-fx-border-color: BLUE;");
testStage = getStage();
testStage.setTitle("Test VBox's layoutBounds update");
testScene = new Scene(topPane, WIDTH, HEIGHT);
testStage.setScene(testScene);
testStage.show();
});
waitFirstFrame();
runAndWait(() -> {
BoundingBox e1Bounds = (BoundingBox) e1.getLayoutBounds();
BoundingBox l1Bounds = (BoundingBox) l1.getLayoutBounds();
BoundingBox testbb = new BoundingBox(0, 0, 0,
Math.max(e1Bounds.getWidth(), l1Bounds.getWidth()),
Math.max(e1Bounds.getHeight(), l1Bounds.getHeight()),
Math.max(e1Bounds.getDepth(), l1Bounds.getDepth()));
BoundingBox bb = (BoundingBox) vBox.getLayoutBounds();
assertTrue(bb.contains(testbb));
l1.setText("A B C D E F G");
});
waitNextFrame();
runAndWait(() -> {
BoundingBox e1Bounds = (BoundingBox) e1.getLayoutBounds();
BoundingBox l1Bounds = (BoundingBox) l1.getLayoutBounds();
BoundingBox testbb = new BoundingBox(0, 0, 0,
Math.max(e1Bounds.getWidth(), l1Bounds.getWidth()),
Math.max(e1Bounds.getHeight(), l1Bounds.getHeight()),
Math.max(e1Bounds.getDepth(), l1Bounds.getDepth()));
BoundingBox bb = (BoundingBox) vBox.getLayoutBounds();
assertTrue(bb.contains(testbb));
l1.setText("1 2 3");
});
waitNextFrame();
runAndWait(() -> {
BoundingBox e1Bounds = (BoundingBox) e1.getLayoutBounds();
BoundingBox l1Bounds = (BoundingBox) l1.getLayoutBounds();
BoundingBox testbb = new BoundingBox(0, 0, 0,
Math.max(e1Bounds.getWidth(), l1Bounds.getWidth()),
Math.max(e1Bounds.getHeight(), l1Bounds.getHeight()),
Math.max(e1Bounds.getDepth(), l1Bounds.getDepth()));
BoundingBox bb = (BoundingBox) vBox.getLayoutBounds();
assertTrue(bb.contains(testbb));
});
}
@Test(timeout = 15000)
public void testHBox() {
final Label l1 = new Label("1 2");
final Ellipse e1 = new Ellipse(20, 30);
final HBox hBox = new HBox();
runAndWait(() -> {
e1.setOpacity(0.5);
hBox.getChildren().addAll(l1, e1);
e1.radiusXProperty().bind(l1.widthProperty());
e1.radiusYProperty().bind(l1.heightProperty());
Pane topPane = new Pane();
topPane.getChildren().addAll(hBox);
hBox.relocate(200, 100);
hBox.setStyle("-fx-border-color: Green;");
testStage = getStage();
testStage.setTitle("Test HBox's layoutBounds update");
testScene = new Scene(topPane, WIDTH, HEIGHT);
testStage.setScene(testScene);
testStage.show();
});
waitFirstFrame();
runAndWait(() -> {
BoundingBox e1Bounds = (BoundingBox) e1.getLayoutBounds();
BoundingBox l1Bounds = (BoundingBox) l1.getLayoutBounds();
BoundingBox testbb = new BoundingBox(0, 0, 0,
Math.max(e1Bounds.getWidth(), l1Bounds.getWidth()),
Math.max(e1Bounds.getHeight(), l1Bounds.getHeight()),
Math.max(e1Bounds.getDepth(), l1Bounds.getDepth()));
BoundingBox bb = (BoundingBox) hBox.getLayoutBounds();
assertTrue(bb.contains(testbb));
l1.setText("A B C D E F G");
});
waitNextFrame();
runAndWait(() -> {
BoundingBox e1Bounds = (BoundingBox) e1.getLayoutBounds();
BoundingBox l1Bounds = (BoundingBox) l1.getLayoutBounds();
BoundingBox testbb = new BoundingBox(0, 0, 0,
Math.max(e1Bounds.getWidth(), l1Bounds.getWidth()),
Math.max(e1Bounds.getHeight(), l1Bounds.getHeight()),
Math.max(e1Bounds.getDepth(), l1Bounds.getDepth()));
BoundingBox bb = (BoundingBox) hBox.getLayoutBounds();
assertTrue(bb.contains(testbb));
l1.setText("1 2 3");
});
waitNextFrame();
runAndWait(() -> {
BoundingBox e1Bounds = (BoundingBox) e1.getLayoutBounds();
BoundingBox l1Bounds = (BoundingBox) l1.getLayoutBounds();
BoundingBox testbb = new BoundingBox(0, 0, 0,
Math.max(e1Bounds.getWidth(), l1Bounds.getWidth()),
Math.max(e1Bounds.getHeight(), l1Bounds.getHeight()),
Math.max(e1Bounds.getDepth(), l1Bounds.getDepth()));
BoundingBox bb = (BoundingBox) hBox.getLayoutBounds();
assertTrue(bb.contains(testbb));
});
}
}
