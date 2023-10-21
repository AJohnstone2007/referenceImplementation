package test.com.sun.javafx.tk.quantum;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.concurrent.CountDownLatch;
public class CloseWindowTest {
private static final CountDownLatch startupLatch = new CountDownLatch(1);
private static Stage primaryStage;
private static volatile Throwable exception;
public static class TestApp extends Application {
@Override
public void start(Stage t) {
primaryStage = t;
t.setScene(new Scene(new Group()));
t.setWidth(100);
t.setHeight(100);
t.show();
Thread.currentThread().setUncaughtExceptionHandler((t2, e) -> {
System.err.println("Exception caught in thread: " + t2);
e.printStackTrace();
exception = e;
});
startupLatch.countDown();
}
}
@BeforeClass
public static void setup() throws Exception {
new Thread(() -> Application.launch(TestApp.class)).start();
startupLatch.await();
}
@AfterClass
public static void shutdown() {
Platform.runLater(primaryStage::hide);
}
@Test
public void test1() throws Throwable {
final CountDownLatch l = new CountDownLatch(1);
Platform.runLater(() -> {
final Stage t = new Stage();
t.setScene(new Scene(new Group()));
t.show();
Platform.runLater(() -> {
try {
t.hide();
t.hide();
} catch (Throwable z) {
exception = z;
} finally {
l.countDown();
}
});
});
l.await();
if (exception != null) {
throw exception;
}
}
@Test
public void test2() throws Throwable {
final CountDownLatch l = new CountDownLatch(1);
Platform.runLater(() -> {
final Stage t = new Stage();
t.setScene(new Scene(new Group()));
t.show();
final Stage p = new Stage();
p.initOwner(t);
p.setScene(new Scene(new Group()));
p.show();
Platform.runLater(() -> {
try {
t.hide();
p.hide();
} catch (Throwable z) {
exception = z;
} finally {
l.countDown();
}
});
});
l.await();
if (exception != null) {
throw exception;
}
}
@Test
public void test3() throws Throwable {
final CountDownLatch l = new CountDownLatch(1);
Platform.runLater(() -> {
final Stage t = new Stage();
t.setScene(new Scene(new Group()));
t.show();
final Stage p = new Stage();
p.initOwner(t);
p.initModality(Modality.WINDOW_MODAL);
p.setScene(new Scene(new Group()));
p.show();
Platform.runLater(() -> {
try {
t.hide();
p.hide();
} catch (Throwable z) {
exception = z;
} finally {
l.countDown();
}
});
});
l.await();
if (exception != null) {
throw exception;
}
}
@Test
public void test4() throws Throwable {
final CountDownLatch l = new CountDownLatch(1);
Platform.runLater(() -> {
final Stage t = new Stage();
t.setScene(new Scene(new Group()));
t.show();
final Stage p = new Stage();
p.initOwner(t);
p.setScene(new Scene(new Group()));
p.show();
final Stage s = new Stage();
s.initOwner(p);
s.setScene(new Scene(new Group()));
p.show();
Platform.runLater(() -> {
try {
t.hide();
s.hide();
p.hide();
} catch (Throwable z) {
exception = z;
} finally {
l.countDown();
}
});
});
l.await();
if (exception != null) {
throw exception;
}
}
}
