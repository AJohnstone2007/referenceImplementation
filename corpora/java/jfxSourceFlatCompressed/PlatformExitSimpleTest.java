package test.com.sun.javafx.application;
import javafx.application.Platform;
import org.junit.Test;
public class PlatformExitSimpleTest {
@Test (timeout = 5000)
public void testPlatformExit() {
Platform.exit();
}
}
