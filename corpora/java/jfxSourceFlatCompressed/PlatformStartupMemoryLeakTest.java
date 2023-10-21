package test.com.sun.javafx.application;
import javafx.application.Platform;
import org.junit.Test;
import org.junit.AfterClass;
import test.util.memory.JMemoryBuddy;
public class PlatformStartupMemoryLeakTest {
@Test
public void testStartupLeak() {
JMemoryBuddy.memoryTest((checker) -> {
Runnable r = new Runnable() {
@Override
public void run() {
System.out.println("Startup called!");
}
};
Platform.startup(r);
checker.assertCollectable(r);
});
}
@AfterClass
public static void tearDown() {
Platform.exit();
}
}
