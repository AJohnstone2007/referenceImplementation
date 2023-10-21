package test.com.sun.javafx.application;
import org.junit.Test;
public class PlatformExit1Test extends PlatformExitCommon {
@Test (timeout = 15000)
public void testPlatformExit() {
doTestPlatformExit(false);
}
}
