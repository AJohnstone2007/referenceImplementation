package test.com.sun.scenario.animation.shared;
import com.sun.scenario.animation.shared.TimelineClipCore;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;
import org.junit.Before;
import org.junit.Test;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import javafx.animation.TimelineShim;
public class TimelineClipCoreTest {
private Timeline timeline;
private KeyFrame start;
private KeyFrame middle;
private KeyFrame end;
private IntegerProperty target;
private TimelineClipCore core;
private boolean tmpBool;
@Before
public void setUp() {
target = new SimpleIntegerProperty();
start = new KeyFrame(Duration.ZERO, new KeyValue(target, 10));
middle = new KeyFrame(new Duration(500));
end = new KeyFrame(new Duration(1000), new KeyValue(target, 20));
timeline = new Timeline();
timeline.getKeyFrames().setAll(start, middle, end);
timeline.setRate(1.0);
timeline.setCycleCount(1);
timeline.setAutoReverse(false);
core = TimelineShim.getClipCore(timeline);
}
@Test
public void testPlayTo() {
timeline.play();
timeline.pause();
core.playTo(6 * 500);
assertEquals(15, target.get());
core.playTo(6 * 1000);
assertEquals(20, target.get());
core.playTo(6 * 200);
assertEquals(12, target.get());
core.playTo(0);
assertEquals(10, target.get());
tmpBool = false;
final KeyFrame newMiddle = new KeyFrame(
Duration.millis(500),
event -> {
tmpBool = true;
}
);
timeline.getKeyFrames().set(1, newMiddle);
core.playTo(6 * 1000);
assertEquals(20, target.get());
assertTrue(tmpBool);
}
@Test
public void testPlayTo_ThrowsException() {
final PrintStream defaultErrorStream = System.err;
final PrintStream nirvana = new PrintStream(new OutputStream() {
@Override
public void write(int i) throws IOException {
}
});
final OnFinishedExceptionListener eventHandler = new OnFinishedExceptionListener() ;
start = new KeyFrame(Duration.ZERO, eventHandler);
middle = new KeyFrame(new Duration(500), eventHandler);
end = new KeyFrame(new Duration(1000), eventHandler);
timeline.getKeyFrames().setAll(start, middle, end);
try {
System.setErr(nirvana);
} catch (SecurityException ex) {
}
timeline.play();
timeline.pause();
core.playTo(6 * 100);
try {
System.setErr(defaultErrorStream);
} catch (SecurityException ex) {
}
assertEquals(1, eventHandler.callCount);
try {
System.setErr(nirvana);
} catch (SecurityException ex) {
}
core.playTo(6 * 600);
try {
System.setErr(defaultErrorStream);
} catch (SecurityException ex) {
}
assertEquals(2, eventHandler.callCount);
try {
System.setErr(nirvana);
} catch (SecurityException ex) {
}
core.playTo(6 * 1000);
try {
System.setErr(defaultErrorStream);
} catch (SecurityException ex) {
}
assertEquals(3, eventHandler.callCount);
}
@Test
public void testJumpTo() {
tmpBool = false;
final KeyFrame newMiddle = new KeyFrame(
Duration.millis(500),
event -> {
tmpBool = true;
}
);
timeline.getKeyFrames().set(1, newMiddle);
core.jumpTo(6 * 600, false);
assertEquals(0, target.get());
assertFalse(tmpBool);
tmpBool = false;
timeline.play();
timeline.pause();
core.jumpTo(6 * 400, false);
assertEquals(14, target.get());
assertFalse(tmpBool);
}
private static class OnFinishedExceptionListener implements EventHandler<ActionEvent> {
private int callCount = 0;
@Override
public void handle(ActionEvent event) {
callCount++;
throw new RuntimeException("Test Exception");
}
}
}
