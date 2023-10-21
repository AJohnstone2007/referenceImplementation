package test.renderlock;
import static org.junit.Assume.assumeTrue;
import org.junit.Test;
public class RenderLock1Test extends RenderLockCommon {
@Test
public void windowCloseTest() throws Exception {
assumeTrue(Boolean.getBoolean("unstable.test"));
doWindowCloseTest();
}
}
