package test.javafx.stage;
import javafx.application.Platform;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
public class FocusedWindowNativeTest extends FocusedWindowTestBase {
@BeforeClass
public static void initFX() throws Exception {
initFXBase();
}
@Test
public void testClosedFocusedStageLeak() throws Exception {
testClosedFocusedStageLeakBase();
}
@AfterClass
public static void teardownOnce() {
Platform.runLater(() -> {
Platform.exit();
});
}
}
