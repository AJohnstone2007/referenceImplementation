package test.com.sun.javafx.application;
import org.junit.Test;
public class PlatformExit2Test extends PlatformExitCommon {
@Test (timeout = 15000)
public void testPlatformExitTwice() {
doTestPlatformExit(true);
}
}
