package test.javafx.scene.web;
import com.sun.javafx.PlatformUtil;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import javafx.event.EventHandler;
import javafx.scene.web.WebEvent;
import netscape.javascript.JSException;
import org.junit.Assume;
import org.junit.Test;
import org.junit.Ignore;
@Ignore("JDK-8280421")
public class IrresponsiveScriptTest extends TestBase {
@Test public void testInfiniteLoopInScript() {
try {
executeScript("while (true) {}");
} catch (AssertionError e) {
Throwable cause = e.getCause();
if (!(cause instanceof JSException)) {
throw new AssertionError(cause);
}
}
}
@Test public void testLongWaitInHandler() {
final int TIMEOUT = 24;
getEngine().setOnAlert(new EventHandler<WebEvent<String>>() {
public void handle(WebEvent<String> ev) {
try {
synchronized (this) {
wait(TIMEOUT * 1000);
}
} catch (InterruptedException e) {
}
}
});
executeScript("alert('Jumbo!');");
}
@Test public void testLongLoopInHandler() {
final long CPU_TIME_TO_RUN = 24L * 1000 * 1000 * 1000;
getEngine().setOnAlert(ev -> {
ThreadMXBean bean = ManagementFactory.getThreadMXBean();
long startCpuTime = bean.getCurrentThreadCpuTime();
while (bean.getCurrentThreadCpuTime() - startCpuTime
< CPU_TIME_TO_RUN)
{
Math.sqrt(Math.random() * 21082013);
}
});
executeScript("alert('Jumbo!');");
}
}
