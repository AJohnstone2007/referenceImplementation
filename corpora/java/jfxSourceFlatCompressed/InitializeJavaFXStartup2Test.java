package test.com.sun.javafx.application;
import org.junit.BeforeClass;
import org.junit.Test;
public class InitializeJavaFXStartup2Test extends InitializeJavaFXStartupBase {
@BeforeClass
public static void initialize() throws Exception {
InitializeJavaFXStartupBase.initializeStartup();
}
@Test (timeout = 15000)
public void testStartupThenLaunch() throws Exception {
InitializeJavaFXLaunchBase.initializeApplicationLaunch();
doTestInitializeThenSecondLaunch();
}
}
