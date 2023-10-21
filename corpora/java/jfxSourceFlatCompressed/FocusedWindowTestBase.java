package test.javafx.stage;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.lang.ref.WeakReference;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import junit.framework.Assert;
import test.util.Util;
public abstract class FocusedWindowTestBase {
static CountDownLatch startupLatch;
public static void initFXBase() throws Exception {
startupLatch = new CountDownLatch(1);
Platform.startup(startupLatch::countDown);
Platform.setImplicitExit(false);
Assert.assertTrue("Timeout waiting for FX runtime to start",
startupLatch.await(15, TimeUnit.MILLISECONDS));
}
WeakReference<Stage> closedFocusedStageWeak = null;
Stage closedFocusedStage = null;
public void testClosedFocusedStageLeakBase() throws Exception {
CountDownLatch latch = new CountDownLatch(1);
Util.runAndWait(() -> {
closedFocusedStage = new Stage();
closedFocusedStage.setTitle("Focused Stage");
closedFocusedStageWeak = new WeakReference<>(closedFocusedStage);
TextField textField = new TextField();
closedFocusedStage.setScene(new Scene(textField));
closedFocusedStage.setOnShown(l -> {
latch.countDown();
});
closedFocusedStage.show();
});
Assert.assertTrue("Timeout waiting for closedFocusedStage to show`",
latch.await(15, TimeUnit.MILLISECONDS));
CountDownLatch hideLatch = new CountDownLatch(1);
closedFocusedStage.setOnHidden(a -> {
hideLatch.countDown();
});
Util.runAndWait(() -> closedFocusedStage.close());
Assert.assertTrue("Timeout waiting for closedFocusedStage to hide`",
hideLatch.await(15, TimeUnit.MILLISECONDS));
closedFocusedStage.requestFocus();
closedFocusedStage = null;
assertCollectable(closedFocusedStageWeak);
}
public static void assertCollectable(WeakReference weakReference) throws Exception {
int counter = 0;
System.gc();
while (counter < 10 && weakReference.get() != null) {
Thread.sleep(100);
counter = counter + 1;
System.gc();
}
Assert.assertNull(weakReference.get());
}
}
