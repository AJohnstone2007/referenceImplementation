package test.com.sun.javafx.application;
import org.junit.BeforeClass;
import org.junit.Test;
public class InitializeJavaFXLaunch1Test extends InitializeJavaFXLaunchBase {
@BeforeClass
public static void initialize() throws Exception {
InitializeJavaFXLaunchBase.initializeApplicationLaunch();
}
@Test (timeout = 15000)
public void testLaunchThenLaunchInFX() throws Exception {
doTestInitializeThenLaunchInFX();
}
}
