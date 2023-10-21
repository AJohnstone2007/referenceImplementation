package test.robot.javafx.embed.swing;
import org.junit.Test;
import test.util.Util;
import javax.swing.SwingUtilities;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CountDownLatch;
public class SwingNodeJDialogTest extends SwingNodeBase {
@Test
public void testJDialogAbove() throws InterruptedException, InvocationTargetException {
myApp.createStageAndDialog();
myApp.showDialog();
testAbove(true);
myApp.closeStageAndDialog();
}
@Test
public void testNodeRemovalAfterShow() throws InterruptedException, InvocationTargetException {
myApp.createStageAndDialog();
myApp.showDialog();
testAbove(true);
myApp.detachSwingNode();
testAbove(false);
myApp.closeStageAndDialog();
myApp.attachSwingNode();
}
@Test
public void testNodeRemovalBeforeShow() throws InterruptedException, InvocationTargetException {
myApp.createStageAndDialog();
myApp.detachSwingNode();
myApp.showDialog();
testAbove(false);
myApp.closeStageAndDialog();
myApp.attachSwingNode();
}
@Test
public void testStageCloseAfterShow() throws InvocationTargetException, InterruptedException {
myApp.createStageAndDialog();
myApp.showDialog();
testAbove(true);
myApp.closeStage();
myApp.disposeDialog();
}
@Test
public void testStageCloseBeforeShow() throws InvocationTargetException, InterruptedException {
myApp.createStageAndDialog();
myApp.closeStage();
myApp.showDialog();
testAbove(true);
myApp.disposeDialog();
}
@Test
public void testNodeRemovalBeforeShowHoldEDT() throws InterruptedException, InvocationTargetException {
myApp.createAndShowStage();
CountDownLatch latch = new CountDownLatch(1);
SwingUtilities.invokeLater(()-> {
myApp.createDialogRunnable.run();
latch.countDown();
Util.sleep(LONG_WAIT_TIME);
myApp.dialog.setVisible(true);
});
latch.await();
myApp.detachSwingNode();
testAbove(false);
myApp.closeStageAndDialog();
myApp.attachSwingNode();
}
@Test
public void testStageCloseBeforeShowHoldEDT() throws InvocationTargetException, InterruptedException {
myApp.createAndShowStage();
CountDownLatch latch = new CountDownLatch(1);
SwingUtilities.invokeLater(()-> {
myApp.createDialogRunnable.run();
latch.countDown();
Util.sleep(LONG_WAIT_TIME);
myApp.dialog.setVisible(true);
});
latch.await();
myApp.closeStage();
testAbove(false);
myApp.disposeDialog();
}
}
