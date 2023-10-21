package test.com.sun.javafx.application;
import org.junit.Test;
public class PlatformExit4Test extends PlatformExitCommon {
@Test (timeout = 15000)
public void testPlatformExitOnAppThreadTwice() {
doTestPlatformExitOnAppThread(true);
}
}
