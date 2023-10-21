package test.com.sun.javafx.application;
import org.junit.Test;
public class PlatformExit3Test extends PlatformExitCommon {
@Test (timeout = 15000)
public void testPlatformExitOnAppThread() {
doTestPlatformExitOnAppThread(false);
}
}
