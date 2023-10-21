package test.com.sun.javafx.application;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.stage.Stage;
import junit.framework.AssertionFailedError;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static test.util.Util.TIMEOUT;
public class HostServicesTest {
private static final CountDownLatch launchLatch = new CountDownLatch(1);
private static MyApp myApp;
public static class MyApp extends Application {
@Override
public void start(Stage primaryStage) throws Exception {
HostServicesTest.myApp = this;
Platform.setImplicitExit(false);
launchLatch.countDown();
}
}
@BeforeClass
public static void setupOnce() {
new Thread(() -> Application.launch(MyApp.class, (String[]) null)).start();
try {
if (!launchLatch.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
throw new AssertionFailedError("Timeout waiting for Application to launch");
}
} catch (InterruptedException ex) {
AssertionFailedError err = new AssertionFailedError("Unexpected exception");
err.initCause(ex);
throw err;
}
}
@AfterClass
public static void teardownOnce() {
Platform.exit();
}
@Test
public void testCodeBase() {
final HostServices hs = myApp.getHostServices();
assertNotNull(hs);
String cbStr = hs.getCodeBase();
assertNotNull(cbStr);
assertTrue(cbStr.isEmpty());
}
@Test
public void testDocumentBase() {
final HostServices hs = myApp.getHostServices();
assertNotNull(hs);
String dbStr = hs.getDocumentBase();
assertNotNull(dbStr);
String userDir = System.getProperty("user.dir");
userDir = userDir.replace("\\", "/");
System.err.println("userDir = " + userDir);
if (!userDir.startsWith("/")) {
userDir = "/" + userDir;
}
String testDocBase = "file:" + userDir + "/";
assertTrue(dbStr.equals(testDocBase));
}
@Test
public void testWebContext() {
final HostServices hs = myApp.getHostServices();
assertNotNull(hs);
boolean nsme = false;
try {
Method m_getWebContext = HostServices.class.getMethod("getWebContext");
} catch (NoSuchMethodException ex) {
nsme = true;
}
assertTrue("Did not get the expected NoSuchMethodException", nsme);
}
}
