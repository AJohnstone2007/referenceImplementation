package test.robot.com.sun.glass.ui.monocle;
import com.sun.glass.ui.monocle.TestLogShim;
import test.robot.com.sun.glass.ui.monocle.TestApplication;
import test.robot.com.sun.glass.ui.monocle.input.devices.TestTouchDevice;
import test.robot.com.sun.glass.ui.monocle.input.devices.TestTouchDevices;
import javafx.scene.input.GestureEvent;
import org.junit.*;
import org.junit.runners.Parameterized;
import java.util.Collection;
public class SwipeSimpleTest extends ParameterizedTestBase {
private final int SWIPE_THRESHOLD = 10;
int startPointX;
int startPointY;
static {
System.setProperty("com.sun.javafx.isEmbedded", "true");
}
public SwipeSimpleTest(TestTouchDevice device) {
super(device);
}
@Parameterized.Parameters
public static Collection<Object[]> data() {
return TestTouchDevices.getTouchDeviceParameters(1);
}
private int getDelta() throws Exception {
int max = Math.max(SWIPE_THRESHOLD, device.getTapRadius());
return max + 1;
}
@BeforeClass
public static void beforeInit() {
System.setProperty("com.sun.javafx.gestures.swipe.maxduration", "1200");
}
@Before
public void addListener() throws Exception {
TestApplication.getStage().getScene().addEventHandler(
GestureEvent.ANY,
e -> TestLogShim.format("%s at %.0f, %.0f",
e.getEventType(),
e.getScreenX(),
e.getScreenY()));
startPointX = (int) Math.round(width * 0.5);
startPointY = (int) Math.round(height * 0.5);
}
private void swipe(Point[] points, String expectedSwipe) throws Exception {
Assert.assertTrue(points.length > 1);
int x = points[0].getX();
int y = points[0].getY();
TestLogShim.reset();
int p1 = device.addPoint(x, y);
device.sync();
for (int i = 1; i < points.length; i++) {
device.setPoint(p1, points[i].getX(), points[i].getY());
device.sync();
}
device.removePoint(p1);
device.sync();
int finalX = points[points.length - 1].getX();
int finalY = points[points.length - 1].getY();
TestLogShim.waitForLogContaining("Touch released: %d, %d", finalX, finalY);
TestLogShim.waitForLogContaining("Mouse released: %d, %d", finalX, finalY);
TestLogShim.waitForLogContaining("Mouse clicked: %d, %d", finalX, finalY);
if (expectedSwipe != null) {
TestLogShim.waitForLogContaining(expectedSwipe);
} else {
Assert.assertEquals(0, TestLogShim.countLogContaining("SWIPE"));
}
}
@Test
public void testSwipeRight1() throws Exception {
Point[] path = {new Point(startPointX, startPointY),
new Point(startPointX + getDelta(), startPointY)};
swipe(path, "SWIPE_RIGHT");
}
@Test
public void testSwipeRight2() throws Exception {
Point[] path = {new Point(startPointX, startPointY), new Point(65,
getDelta(), startPointX, startPointY)};
swipe(path, "SWIPE_RIGHT");
}
@Test
public void testSwipeRight3() throws Exception {
Point[] path = {new Point(startPointX, startPointY), new Point(80,
getDelta(), startPointX, startPointY)};
swipe(path, "SWIPE_RIGHT");
}
@Test
public void testSwipeRight4() throws Exception {
Point[] path = {new Point(startPointX, startPointY), new Point(115,
getDelta(), startPointX, startPointY)};
swipe(path, "SWIPE_RIGHT");
}
@Test
public void testSwipeLeft1() throws Exception {
Point[] path = {new Point(startPointX, startPointY),
new Point(startPointX - getDelta(), startPointY)};
swipe(path, "SWIPE_LEFT");
}
@Test
public void testSwipeLeft2() throws Exception {
Point[] path = {new Point(startPointX, startPointY), new Point(-65,
getDelta(), startPointX, startPointY)};
swipe(path, "SWIPE_LEFT");
}
@Test
public void testSwipeLeft3() throws Exception {
Point[] path = {new Point(startPointX, startPointY), new Point(-80,
getDelta(), startPointX, startPointY)};
swipe(path, "SWIPE_LEFT");
}
@Test
public void testSwipeLeft4() throws Exception {
Point[] path = {new Point(startPointX, startPointY), new Point(-115,
getDelta(), startPointX, startPointY)};
swipe(path, "SWIPE_LEFT");
}
@Test
public void testSwipeUp1() throws Exception {
Point[] path = {new Point(startPointX, startPointY),
new Point(startPointX, startPointY - getDelta())};
swipe(path, "SWIPE_UP");
}
@Test
public void testSwipeUp2() throws Exception {
Point[] path = {new Point(startPointX, startPointY), new Point(25,
getDelta(), startPointX, startPointY)};
swipe(path, "SWIPE_UP");
}
@Test
public void testSwipeUp3() throws Exception {
Point[] path = {new Point(startPointX, startPointY), new Point(-25,
getDelta(), startPointX, startPointY)};
swipe(path, "SWIPE_UP");
}
@Test
public void testSwipeDown1() throws Exception {
Point[] path = {new Point(startPointX, startPointY),
new Point(startPointX, startPointY + getDelta())};
swipe(path, "SWIPE_DOWN");
}
@Test
public void testSwipeDown2() throws Exception {
Point[] path = {new Point(startPointX, startPointY), new Point(155,
getDelta(), startPointX, startPointY)};
swipe(path, "SWIPE_DOWN");
}
@Test
public void testSwipeDown3() throws Exception {
Point[] path = {new Point(startPointX, startPointY), new Point(-155,
getDelta(), startPointX, startPointY)};
swipe(path, "SWIPE_DOWN");
}
@Test
public void testNoSwipeUp() throws Exception {
Point[] path = {new Point(startPointX, startPointY), new Point(31,
getDelta(), startPointX, startPointY)};
swipe(path, null);
}
@Test
public void testNoSwipeRight() throws Exception {
Point[] path = {new Point(startPointX, startPointY), new Point(59,
getDelta(), startPointX, startPointY)};
swipe(path, null);
}
@Test
public void testSwipeUp4Points() throws Exception {
int delta = getDelta();
Point p1 = new Point(startPointX, startPointY);
Point p2 = new Point(45, delta, p1);
Point p3 = new Point(22, delta, p2);
Point p4 = new Point(10, delta, p3);
Point[] path = {p1, p2, p3, p4};
swipe(path, "SWIPE_UP");
}
private class Point {
int x;
int y;
Point(int x, int y) {
this.x = x;
this.y = y;
}
Point(int angle, int distance, int centerX, int centerY) {
int transformedAngle = 90 - angle;
this.x = centerX + (int) Math.round(distance * Math.cos(Math
.toRadians(transformedAngle)));
this.y = centerY - (int) Math.round(distance * Math.sin(Math
.toRadians(transformedAngle)));
}
Point(int angle, int radius, Point p) {
this(angle, radius, p.getX(), p.getY());
}
public int getX() {
return x;
}
public int getY() {
return y;
}
}
}
