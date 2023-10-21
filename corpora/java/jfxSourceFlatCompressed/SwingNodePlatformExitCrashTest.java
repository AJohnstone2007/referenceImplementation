package test.robot.javafx.embed.swing;
import javafx.application.Platform;
import org.junit.Ignore;
import org.junit.Test;
import test.util.Util;
import javax.swing.SwingUtilities;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CountDownLatch;
public class SwingNodePlatformExitCrashTest extends SwingNodeBase {
@Test
@Ignore("JDK-8190329")
public void testPlatformExitBeforeShowHoldEDT() throws InvocationTargetException, InterruptedException {
myApp.createAndShowStage();
CountDownLatch latch = new CountDownLatch(1);
SwingUtilities.invokeLater(()-> {
myApp.createDialogRunnable.run();
latch.countDown();
Util.sleep(LONG_WAIT_TIME);
myApp.dialog.setVisible(true);
});
latch.await();
testAbove(false);
runWaitSleep(()-> Platform.exit());
myApp.disposeDialog();
}
}
