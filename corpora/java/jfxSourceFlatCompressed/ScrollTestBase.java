package test.robot.com.sun.glass.ui.monocle;
import com.sun.glass.ui.monocle.TestLogShim;
import test.robot.com.sun.glass.ui.monocle.TestApplication;
import test.robot.com.sun.glass.ui.monocle.input.devices.TestTouchDevice;
import test.robot.com.sun.glass.ui.monocle.input.devices.TestTouchDevices;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.runners.Parameterized;
import java.util.Collection;
public abstract class ScrollTestBase extends ParameterizedTestBase {
protected int point1X;
protected int point1Y;
protected int point2X;
protected int point2Y;
protected int p1;
protected int p2;
protected int totalDeltaX = 0;
protected int totalDeltaY = 0;
public ScrollTestBase(TestTouchDevice device) {
super(device);
}
@Parameterized.Parameters
public static Collection<Object[]> data() {
return TestTouchDevices.getTouchDeviceParameters(1);
}
@Before
public void init() {
Assume.assumeTrue(TestApplication.isMonocle());
Assume.assumeTrue(Boolean.getBoolean("com.sun.javafx.gestures.scroll"));
point1X = (int) Math.round(width * 0.5);
point1Y = (int) Math.round(height * 0.5);
point2X = point1X + 40;
point2Y = point1Y;
}
@After
public void releaseAll() throws Exception {
if (device.getPressedPoints() == 1) {
releaseFirstFinger();
} else if (device.getPressedPoints() == 2){
releaseAllFingers();
}
}
protected int getScrollThreshold() {
String s = System.getProperty("com.sun.javafx.gestures.scroll.threshold");
if (s != null) {
return Integer.valueOf(s);
} else {
return 10;
}
}
protected boolean paramsValid(int dX, int dY, int num, int x, int y) {
if ((0 < x + (dX * num))
&& (x + (dX * num) < (int) Math.round(width))
&& (0 < y + (dY * num))
&& (y + (dY * num) < (int) Math.round(height))) {
return true;
} else {
return false;
}
}
protected void pressFirstFinger() throws Exception {
Assert.assertEquals(0, device.getPressedPoints());
TestLogShim.reset();
p1 = device.addPoint(point1X, point1Y);
device.sync();
TestLogShim.waitForLogContaining("TouchPoint: PRESSED %d, %d", point1X, point1Y);
}
protected void pressSecondFinger() throws Exception {
Assert.assertEquals(1, device.getPressedPoints());
TestLogShim.reset();
point2X = point1X + 40;
point2Y = point1Y;
p2 = device.addPoint(point2X, point2Y);
device.sync();
TestLogShim.waitForLogContaining("TouchPoint: STATIONARY %d, %d",
point1X, point1Y);
TestLogShim.waitForLogContaining("TouchPoint: PRESSED %d, %d",
point2X, point2Y);
}
protected void moveOneFinger(int deltaX, int deltaY, int numOfIterations,
boolean firstMove) throws Exception {
TestLogShim.reset();
Assert.assertEquals(1, device.getPressedPoints());
Assert.assertTrue(paramsValid(deltaX, deltaY, numOfIterations,
point1X, point1Y));
point1X += deltaX;
point1Y += deltaY;
device.setPoint(p1, point1X, point1Y);
device.sync();
TestLogShim.waitForLogContaining("TouchPoint: MOVED %d, %d", point1X,
point1Y);
if (firstMove) {
totalDeltaX = deltaX;
totalDeltaY = deltaY;
if (Math.abs(deltaX) > getScrollThreshold()
|| Math.abs(deltaY) > getScrollThreshold()) {
TestLogShim.waitForLogContaining("Scroll started, DeltaX: " + 0
+ ", DeltaY: " + 0
+ ", totalDeltaX: " + 0
+ ", totalDeltaY: " + 0
+ ", touch points: " + 1
+ ", inertia value: false");
TestLogShim.waitForLogContaining("Scroll, DeltaX: " + deltaX
+ ", DeltaY: " + deltaY
+ ", totalDeltaX: " + totalDeltaX
+ ", totalDeltaY: " + totalDeltaY
+ ", touch points: " + 1
+ ", inertia value: false");
} else {
Assert.assertEquals(0, TestLogShim.countLogContaining("Scroll started"));
Assert.assertEquals(0, TestLogShim.countLogContaining("Scroll, DeltaX:"));
}
} else {
totalDeltaX += deltaX;
totalDeltaY += deltaY;
TestLogShim.waitForLogContaining("Scroll, DeltaX: " + deltaX
+ ", DeltaY: " + deltaY
+ ", totalDeltaX: " + totalDeltaX
+ ", totalDeltaY: " + totalDeltaY
+ ", touch points: " + 1
+ ", inertia value: false");
}
String expectedLog;
boolean passedTheThreshold =false;
if (numOfIterations >= 2) {
for (int i = 2; i <= numOfIterations; i++) {
point1X += deltaX;
point1Y += deltaY;
TestLogShim.reset();
device.setPoint(p1, point1X, point1Y);
device.sync();
TestLogShim.waitForLogContaining("TouchPoint: MOVED %d, %d",
point1X, point1Y);
totalDeltaX += deltaX;
totalDeltaY += deltaY;
expectedLog = "Scroll, DeltaX: " + deltaX + ", DeltaY: " + deltaY
+ ", totalDeltaX: " + totalDeltaX
+ ", totalDeltaY: " + totalDeltaY
+ ", touch points: " + 1
+ ", inertia value: false";
if (Math.abs(deltaX) < getScrollThreshold()
&& Math.abs(deltaY) < getScrollThreshold()) {
if(Math.abs(totalDeltaX) > getScrollThreshold()
|| Math.abs(totalDeltaY) > getScrollThreshold()) {
if (!passedTheThreshold) {
expectedLog = "Scroll, DeltaX: " + totalDeltaX
+ ", DeltaY: " + totalDeltaY
+ ", totalDeltaX: " + totalDeltaX
+ ", totalDeltaY: " + totalDeltaY
+ ", touch points: " + 1
+ ", inertia value: false";
passedTheThreshold = true;
}
} else {
expectedLog = "sync";
}
}
TestLogShim.waitForLogContaining(expectedLog);
}
}
}
protected void moveTwoFingers(int deltaX, int deltaY, int numOfIterations,
boolean firstMove, boolean fingersChanged)
throws Exception {
TestLogShim.reset();
Assert.assertEquals(2, device.getPressedPoints());
Assert.assertTrue(paramsValid(deltaX, deltaY, numOfIterations,
point1X, point1Y) && paramsValid(deltaX, deltaY, numOfIterations,
point2X, point2Y));
point1X += deltaX;
point1Y += deltaY;
point2X += deltaX;
point2Y += deltaY;
device.setPoint(p1, point1X, point1Y);
device.setPoint(p2, point2X, point2Y);
device.sync();
TestLogShim.waitForLogContaining("TouchPoint: MOVED %d, %d", point1X, point1Y);
TestLogShim.waitForLogContaining("TouchPoint: MOVED %d, %d", point2X, point2Y);
boolean passedTheThreshold = false;
if (firstMove) {
totalDeltaX = deltaX;
totalDeltaY = deltaY;
if (Math.abs(deltaX) > getScrollThreshold()
|| Math.abs(deltaY) > getScrollThreshold()) {
TestLogShim.waitForLogContaining("Scroll started, DeltaX: " + 0
+ ", DeltaY: " + 0
+ ", totalDeltaX: " + 0
+ ", totalDeltaY: " + 0
+ ", touch points: " + 2
+ ", inertia value: false");
TestLogShim.waitForLogContaining("Scroll, DeltaX: " + deltaX
+ ", DeltaY: " + deltaY
+ ", totalDeltaX: " + totalDeltaX
+ ", totalDeltaY: " + totalDeltaY
+ ", touch points: " + 2
+ ", inertia value: false");
} else {
Assert.assertEquals(0, TestLogShim.countLogContaining("Scroll " +
"started"));
Assert.assertEquals(0, TestLogShim.countLogContaining("Scroll, DeltaX:"));
}
} else {
if (fingersChanged) {
totalDeltaX = deltaX;
totalDeltaY = deltaY;
} else {
totalDeltaX += deltaX;
totalDeltaY += deltaY;
}
TestLogShim.waitForLogContaining("Scroll, DeltaX: " + deltaX
+ ", DeltaY: " + deltaY
+ ", totalDeltaX: " + totalDeltaX
+ ", totalDeltaY: " + totalDeltaY
+ ", touch points: " + 2
+ ", inertia value: false");
passedTheThreshold = true;
}
String expectedLog;
if (numOfIterations >= 2) {
for (int i = 2; i <= numOfIterations; i++) {
point1X += deltaX;
point1Y += deltaY;
point2X += deltaX;
point2Y += deltaY;
TestLogShim.reset();
device.setPoint(p1, point1X, point1Y);
device.setPoint(p2, point2X, point2Y);
device.sync();
TestLogShim.waitForLogContaining("TouchPoint: MOVED %d, %d",
point1X, point1Y);
TestLogShim.waitForLogContaining("TouchPoint: MOVED %d, %d",
point2X, point2Y);
totalDeltaX += deltaX;
totalDeltaY += deltaY;
expectedLog = "Scroll, DeltaX: " + deltaX + ", DeltaY: " + deltaY
+ ", totalDeltaX: " + totalDeltaX
+ ", totalDeltaY: " + totalDeltaY
+ ", touch points: " + 2
+ ", inertia value: false";
if (firstMove && Math.abs(deltaX) < getScrollThreshold()
&& Math.abs(deltaY) < getScrollThreshold()) {
if(Math.abs(totalDeltaX) > getScrollThreshold()
|| Math.abs(totalDeltaY) > getScrollThreshold()) {
if (!passedTheThreshold) {
expectedLog = "Scroll, DeltaX: " + totalDeltaX
+ ", DeltaY: " + totalDeltaY
+ ", totalDeltaX: " + totalDeltaX
+ ", totalDeltaY: " + totalDeltaY
+ ", touch points: " + 2
+ ", inertia value: false";
passedTheThreshold = true;
}
} else {
expectedLog = "sync";
}
}
TestLogShim.waitForLogContaining(expectedLog);
}
}
}
protected void releaseFirstFinger() throws Exception {
Assert.assertEquals(1, device.getPressedPoints());
String expectedLog;
TestLogShim.reset();
device.removePoint(p1);
device.sync();
int expectedValue = 0;
expectedLog = "Scroll finished, DeltaX: " + 0
+ ", DeltaY: " + 0
+ ", totalDeltaX: " + totalDeltaX
+ ", totalDeltaY: " + totalDeltaY
+ ", touch points: " + 1
+ ", inertia value: false";
TestLogShim.waitForLogContaining("TouchPoint: RELEASED %d, %d",
point1X, point1Y);
if (Math.abs(totalDeltaX) > getScrollThreshold()
|| Math.abs(totalDeltaY) > getScrollThreshold()) {
expectedValue = 1;
TestLogShim.waitForLogContaining(expectedLog);
}
totalDeltaX = 0;
totalDeltaY = 0;
Assert.assertEquals(expectedValue, TestLogShim.countLogContaining(expectedLog));
if (TestLogShim.countLogContaining("Scroll finished") > 0) {
TestLogShim.waitForLogContainingSubstrings("Scroll", "inertia value: true");
}
}
protected void releaseSecondFinger() throws Exception {
Assert.assertEquals(2, device.getPressedPoints());
String expectedLog;
TestLogShim.reset();
device.removePoint(p2);
device.sync();
int expectedValue = 0;
expectedLog = "Scroll finished, DeltaX: " + 0
+ ", DeltaY: " + 0
+ ", totalDeltaX: " + totalDeltaX
+ ", totalDeltaY: " + totalDeltaY
+ ", touch points: " + 2
+ ", inertia value: false";
TestLogShim.waitForLogContaining("TouchPoint: RELEASED %d, %d",
point2X, point2Y);
if (Math.abs(totalDeltaX) > getScrollThreshold()
|| Math.abs(totalDeltaY) > getScrollThreshold()) {
expectedValue = 1;
TestLogShim.waitForLogContaining(expectedLog);
}
totalDeltaX = 0;
totalDeltaY = 0;
Assert.assertEquals(expectedValue, TestLogShim.countLogContaining(expectedLog));
}
protected void releaseAllFingers() throws Exception {
Assert.assertEquals(2, device.getPressedPoints());
String expectedLog;
TestLogShim.reset();
device.removePoint(p1);
device.removePoint(p2);
device.sync();
int expectedValue = 0;
expectedLog = "Scroll finished, DeltaX: " + 0
+ ", DeltaY: " + 0
+ ", totalDeltaX: " + totalDeltaX
+ ", totalDeltaY: " + totalDeltaY
+ ", touch points: " + 2
+ ", inertia value: false";
TestLogShim.waitForLogContaining("TouchPoint: RELEASED %d, %d", point1X, point1Y);
TestLogShim.waitForLogContaining("TouchPoint: RELEASED %d, %d", point2X, point2Y);
if (Math.abs(totalDeltaX) > getScrollThreshold() ||
Math.abs(totalDeltaY) > getScrollThreshold()) {
expectedValue = 1;
TestLogShim.waitForLogContaining(expectedLog);
}
totalDeltaX = 0;
totalDeltaY = 0;
Assert.assertEquals(expectedValue, TestLogShim.countLogContaining(expectedLog));
if (TestLogShim.countLogContaining("Scroll finished") > 0) {
TestLogShim.waitForLogContainingSubstrings("Scroll", "inertia value: true");
}
}
protected void tapToStopInertia() throws Exception {
Assert.assertEquals(0, device.getPressedPoints());
TestLogShim.reset();
int p = device.addPoint(point1X, point1Y);
device.sync();
device.removePoint(p);
device.sync();
TestLogShim.waitForLogContaining("TouchPoint: RELEASED %d, %d", point1X, point1Y);
}
}
