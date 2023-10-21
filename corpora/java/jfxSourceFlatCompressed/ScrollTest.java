package test.robot.com.sun.glass.ui.monocle;
import com.sun.glass.ui.monocle.TestLogShim;
import test.robot.com.sun.glass.ui.monocle.ScrollTestBase;
import test.robot.com.sun.glass.ui.monocle.input.devices.TestTouchDevice;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
public class ScrollTest extends ScrollTestBase {
public ScrollTest(TestTouchDevice device) {
super(device);
}
private int getDelta() throws Exception {
int max = Math.max(getScrollThreshold(), device.getTapRadius());
return Math.max(max, 30) + 1;
}
@Test
public void testScrollUp() throws Exception {
pressFirstFinger();
moveOneFinger(0, -getDelta(), 3, true);
releaseFirstFinger();
}
@Test
public void testScrollRight() throws Exception {
pressFirstFinger();
moveOneFinger(getDelta(), 0, 2, true);
releaseFirstFinger();
}
@Test
public void testScrollDown() throws Exception {
pressFirstFinger();
moveOneFinger(0, getDelta(), 5, true);
releaseFirstFinger();
}
@Test
public void testScrollLeft() throws Exception {
pressFirstFinger();
moveOneFinger(-getDelta() * 2, 0, 4, true);
releaseFirstFinger();
}
@Test
public void testScrollUpAndRight() throws Exception {
pressFirstFinger();
moveOneFinger(getDelta(), -getDelta(), 3, true);
releaseFirstFinger();
}
@Test
public void testScrollDownAndRight() throws Exception {
pressFirstFinger();
moveOneFinger(getDelta(), getDelta(), 2, true);
releaseFirstFinger();
}
@Test
public void testScrollDownAndLeft() throws Exception {
pressFirstFinger();
moveOneFinger(-getDelta(), getDelta(), 5, true);
releaseFirstFinger();
}
@Test
public void testScrollUpAndLeft() throws Exception {
pressFirstFinger();
moveOneFinger(-getDelta(), -getDelta() * 2, 4, true);
releaseFirstFinger();
}
@Test
public void testTwoFingersScrollUp() throws Exception {
Assume.assumeTrue(device.getPointCount() >= 2);
pressFirstFinger();
pressSecondFinger();
moveTwoFingers(0, -getDelta(), 3, true, false);
releaseAllFingers();
}
@Test
public void testTwoFingersScrollTwice() throws Exception {
Assume.assumeTrue(device.getPointCount() >= 2);
pressFirstFinger();
pressSecondFinger();
moveTwoFingers(0, -getDelta(), 1, true, false);
moveTwoFingers(0, getDelta() * 2, 1, false, false);
releaseAllFingers();
}
@Test
public void testTwoFingersScroll1() throws Exception {
Assume.assumeTrue(device.getPointCount() >= 2);
pressFirstFinger();
moveOneFinger(0, getDelta(), 2, true);
pressSecondFinger();
moveTwoFingers(0, getDelta(), 3, false, true);
releaseAllFingers();
}
@Test
public void testTwoFingersScroll2()
throws Exception {
Assume.assumeTrue(device.getPointCount() >= 2);
pressFirstFinger();
pressSecondFinger();
moveTwoFingers(0, getDelta(), 1, true, false);
releaseSecondFinger();
moveOneFinger(0, getDelta(), 2, false);
releaseFirstFinger();
}
@Test
public void testTwoFingersScroll3()
throws Exception {
Assume.assumeTrue(device.getPointCount() >= 2);
pressFirstFinger();
moveOneFinger(0, getDelta(), 2, true);
pressSecondFinger();
moveTwoFingers(0, -getDelta() * 2, 2, false, true);
releaseSecondFinger();
moveOneFinger(0, getDelta(), 2, false);
releaseFirstFinger();
}
@Test
public void testTwoFingersAsymmetricScroll() throws Exception {
Assume.assumeTrue(device.getPointCount() >= 2);
int deltaY1 = getDelta() + 1;
int deltaY2 = deltaY1 * 2;
int numOfIterations = 4;
Assert.assertTrue(paramsValid(0, deltaY1, numOfIterations,
point1X, point1Y) &&
paramsValid(0, deltaY2, numOfIterations,
point2X, point2Y));
TestLogShim.reset();
p1 = device.addPoint(point1X, point1Y);
p2 = device.addPoint(point2X, point2Y);
device.sync();
TestLogShim.waitForLogContaining("TouchPoint: PRESSED %d, %d", point1X, point1Y);
TestLogShim.waitForLogContaining("TouchPoint: PRESSED %d, %d", point2X, point2Y);
point1Y += deltaY1;
point2Y += deltaY2;
int avgDelta = (deltaY1 + deltaY2) / 2;
TestLogShim.reset();
device.setPoint(p1, point1X, point1Y);
device.setPoint(p2, point2X, point2Y);
device.sync();
TestLogShim.waitForLogContaining("TouchPoint: MOVED %d, %d", point1X, point1Y);
TestLogShim.waitForLogContaining("TouchPoint: MOVED %d, %d", point2X, point2Y);
totalDeltaY = avgDelta;
TestLogShim.waitForLogContaining("Scroll started, DeltaX: " + 0
+ ", DeltaY: " + 0
+ ", totalDeltaX: " + 0
+ ", totalDeltaY: " + 0
+ ", touch points: " + 2
+ ", inertia value: false");
TestLogShim.waitForLogContaining("Scroll, DeltaX: " + 0
+ ", DeltaY: " + avgDelta
+ ", totalDeltaX: " + 0
+ ", totalDeltaY: " + totalDeltaY
+ ", touch points: " + 2
+ ", inertia value: false");
String expectedLog;
for (int i = 2; i <= numOfIterations; i++) {
point1Y += deltaY1;
point2Y += deltaY2;
TestLogShim.reset();
device.setPoint(p1, point1X, point1Y);
device.setPoint(p2, point2X, point2Y);
device.sync();
TestLogShim.waitForLogContaining("TouchPoint: MOVED %d, %d", point1X, point1Y);
TestLogShim.waitForLogContaining("TouchPoint: MOVED %d, %d", point2X, point2Y);
totalDeltaY = avgDelta * i;
expectedLog = "Scroll, DeltaX: " + 0 + ", DeltaY: " + avgDelta
+ ", totalDeltaX: " + 0
+ ", totalDeltaY: " + totalDeltaY
+ ", touch points: " + 2 + ", inertia value: false";
TestLogShim.waitForLogContaining(expectedLog);
}
releaseAllFingers();
}
}
