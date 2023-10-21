package test.javafx.stage;
import javafx.application.Platform;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
public class FocusedWindowMonocleTest extends FocusedWindowTestBase {
static {
System.setProperty("glass.platform","Monocle");
System.setProperty("monocle.platform","Headless");
System.setProperty("prism.order","sw");
}
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
