package test.com.sun.javafx.application;
import org.junit.BeforeClass;
import org.junit.Test;
public class InitializeJavaFXLaunch2Test extends InitializeJavaFXLaunchBase {
@BeforeClass
public static void initialize() throws Exception {
InitializeJavaFXLaunchBase.initializeApplicationLaunch();
}
@Test (timeout = 15000)
public void testLaunchThenLaunch() throws Exception {
doTestInitializeThenSecondLaunch();
}
}
