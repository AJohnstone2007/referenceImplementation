package test.robot.javafx.stage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.junit.Test;
import test.robot.testharness.VisualTestBase;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static test.util.Util.TIMEOUT;
public class IconifyTest extends VisualTestBase {
private static final int WIDTH = 300;
private static final int HEIGHT = 300;
private static final Color BOTTOM_COLOR = Color.LIME;
private static final Color TOP_COLOR = Color.RED;
private static final double TOLERANCE = 0.07;
private Stage bottomStage;
private Stage topStage;
public void canIconifyStage(StageStyle stageStyle, boolean resizable) throws Exception {
final CountDownLatch shownLatch = new CountDownLatch(2);
runAndWait(() -> {
bottomStage = getStage(true);
Scene bottomScene = new Scene(new Pane(), WIDTH, HEIGHT);
bottomScene.setFill(BOTTOM_COLOR);
bottomStage.setScene(bottomScene);
bottomStage.setX(0);
bottomStage.setY(0);
bottomStage.setOnShown(e -> Platform.runLater(shownLatch::countDown));
bottomStage.show();
topStage = getStage(true);
topStage.initStyle(stageStyle);
topStage.setResizable(resizable);
Scene topScene = new Scene(new Pane(), WIDTH, HEIGHT);
topScene.setFill(TOP_COLOR);
topStage.setScene(topScene);
topStage.setX(0);
topStage.setY(0);
topStage.setOnShown(e -> Platform.runLater(shownLatch::countDown));
topStage.show();
});
assertTrue("Timeout waiting for stages to be shown",
shownLatch.await(TIMEOUT, TimeUnit.MILLISECONDS));
runAndWait(() -> {
topStage.toFront();
});
sleep(500);
runAndWait(() -> {
assertFalse(topStage.isIconified());
Color color = getColor(100, 100);
assertColorEquals(TOP_COLOR, color, TOLERANCE);
});
runAndWait(() -> {
topStage.setIconified(true);
});
sleep(500);
runAndWait(() -> {
assertTrue(topStage.isIconified());
Color color = getColor(100, 100);
assertColorEquals(BOTTOM_COLOR, color, TOLERANCE);
});
runAndWait(() -> {
topStage.setIconified(false);
});
sleep(500);
runAndWait(() -> {
assertFalse(topStage.isIconified());
Color color = getColor(100, 100);
assertColorEquals(TOP_COLOR, color, TOLERANCE);
});
}
@Test(timeout = 15000)
public void canIconifyDecoratedStage() throws Exception {
canIconifyStage(StageStyle.DECORATED, true);
}
@Test(timeout = 15000)
public void canIconifyUndecoratedStage() throws Exception {
canIconifyStage(StageStyle.UNDECORATED, true);
}
@Test(timeout = 15000)
public void canIconifyTransparentStage() throws Exception {
canIconifyStage(StageStyle.TRANSPARENT, true);
}
@Test(timeout = 15000)
public void canIconifyNonResizableStage() throws Exception {
canIconifyStage(StageStyle.DECORATED, false);
}
}
