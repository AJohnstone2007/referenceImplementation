package test.robot.com.sun.glass.ui.monocle;
import com.sun.glass.ui.monocle.TestLogShim;
import test.robot.com.sun.glass.ui.monocle.ParameterizedTestBase;
import test.robot.com.sun.glass.ui.monocle.input.devices.TestTouchDevice;
import test.robot.com.sun.glass.ui.monocle.input.devices.TestTouchDevices;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.Parameterized;
import java.util.Collection;
public class RotateTest extends ParameterizedTestBase {
private int newX1;
private int newY1;
private static final int ZERO_ANGLE = 0;
private int centerX;
private int centerY;
private int radius;
private int p1;
private int p2;
public RotateTest(TestTouchDevice device) {
super(device);
}
@Parameterized.Parameters
public static Collection<Object[]> data() {
return TestTouchDevices.getTouchDeviceParameters(2);
}
@Before
public void init() {
Assume.assumeTrue(Boolean.getBoolean("com.sun.javafx.gestures.rotate"));
centerX = (int) Math.round(width * 0.5);
centerY = (int) Math.round(height * 0.5);
radius = (int) Math.round(height * 0.45);
}
@After
public void releaseAll() throws Exception {
if (device.getPressedPoints() == 2) {
TestLogShim.reset();
device.removePoint(p1);
device.removePoint(p2);
device.sync();
}
}
private void updateNewTouchPoint(int angle, int radius, int centerX, int centerY) {
int transformedAngle = 90 - angle;
newX1 = centerX + (int) Math.round(radius *
Math.cos(Math.toRadians(transformedAngle)));
newY1 = centerY - (int) Math.round(radius *
Math.sin(Math.toRadians(transformedAngle)));
}
private int getDistance(int xPoint1, int yPoint1, int xPoint2, int yPoint2) {
double d = Math.sqrt(Math.pow((xPoint1 - xPoint2), 2)
+ Math.pow((yPoint1 - yPoint2), 2));
return (int) d;
}
private int getRotateThreshold() {
String s = System.getProperty("com.sun.javafx.gestures.rotate.threshold");
if (s != null) {
return Integer.valueOf(s);
} else {
return 5;
}
}
private void Rotate(int startAngle, int radius, int x2, int y2, int angleStep,
int numOfIterations) throws Exception {
int totalAngle = angleStep;
updateNewTouchPoint(startAngle, radius, x2, y2);
TestLogShim.reset();
p1 = device.addPoint(newX1, newY1);
p2 = device.addPoint(x2, y2);
device.sync();
TestLogShim.waitForLogContaining("TouchPoint: PRESSED %d, %d", newX1, newY1);
TestLogShim.waitForLogContaining("TouchPoint: PRESSED %d, %d", x2, y2);
int previousX = newX1;
int previousY = newY1;
updateNewTouchPoint((angleStep + startAngle), radius, x2, y2);
Assume.assumeTrue(getDistance(previousX, previousY, newX1, newY1 )
> device.getTapRadius());
TestLogShim.reset();
device.setPoint(p1, newX1, newY1);
device.sync();
TestLogShim.waitForLogContaining("TouchPoint: MOVED %d, %d", newX1, newY1);
TestLogShim.waitForLogContaining("TouchPoint: STATIONARY %d, %d", x2, y2);
if (Math.abs(angleStep) >= getRotateThreshold()) {
TestLogShim.waitForLogContaining("Rotation started, angle: " + ZERO_ANGLE
+ ", total angle: " + ZERO_ANGLE + ", inertia value: false");
TestLogShim.waitForLogContaining("Rotation, angle: " + angleStep
+ ", total angle: " + totalAngle
+ ", inertia value: false");
} else {
Assert.assertEquals(0, TestLogShim.countLogContaining("Rotation started"));
Assert.assertEquals(0, TestLogShim.countLogContaining("Rotation, angle"));
}
boolean passedTheThreshold =false;
if (numOfIterations >= 2) {
for (int i = 2; i <= numOfIterations; i++) {
updateNewTouchPoint(angleStep * i + startAngle, radius, x2, y2);
totalAngle += angleStep;
TestLogShim.reset();
device.setPoint(p1, newX1, newY1);
device.sync();
TestLogShim.waitForLogContaining("TouchPoint: MOVED %d, %d", newX1, newY1);
TestLogShim.waitForLogContaining("TouchPoint: STATIONARY %d, %d", x2, y2);
String expectedLog;
if (Math.abs(angleStep) < getRotateThreshold()) {
if(Math.abs(totalAngle) >= getRotateThreshold()) {
if (!passedTheThreshold) {
expectedLog = "Rotation, angle: " + totalAngle
+ ", total angle: " + totalAngle
+ ", inertia value: false";
passedTheThreshold = true;
} else {
expectedLog = "Rotation, angle: " + angleStep
+ ", total angle: " + totalAngle
+ ", inertia value: false";
}
} else {
expectedLog = "sync";
}
} else {
expectedLog = "Rotation, angle: " + angleStep
+ ", total angle: " + totalAngle
+ ", inertia value: false";
}
TestLogShim.waitForLogContaining(expectedLog);
}
}
TestLogShim.reset();
device.removePoint(p1);
device.removePoint(p2);
device.sync();
TestLogShim.waitForLogContaining("TouchPoint: RELEASED %d, %d", newX1, newY1);
TestLogShim.waitForLogContaining("TouchPoint: RELEASED %d, %d", x2, y2);
if (Math.abs(totalAngle) >= getRotateThreshold()) {
TestLogShim.waitForLogContaining("Rotation finished, angle: " + ZERO_ANGLE
+ ", total angle: " + totalAngle + ", inertia value: false");
Assert.assertEquals(1, TestLogShim.countLogContaining("Rotation "
+ "finished, " + "angle: " + ZERO_ANGLE
+ ", total angle: " + totalAngle
+ ", inertia value: false"));
} else {
Assert.assertEquals(0, TestLogShim.countLogContaining("Rotation finished, "
+ "angle: " + ZERO_ANGLE + ", total angle: " + totalAngle
+ ", inertia value: false"));
}
if (TestLogShim.countLogContaining("Rotation finished") > 0) {
TestLogShim.waitForLogContainingSubstrings("Rotation", "inertia value: true");
}
TestLogShim.reset();
p2 = device.addPoint(x2, y2);
device.sync();
device.removePoint(p2);
device.sync();
TestLogShim.waitForLogContaining("TouchPoint: RELEASED %d, %d", x2, y2);
}
private void Rotate(int radius, int x2, int y2, int angleStep,
int numOfIterations) throws Exception {
Rotate(0, radius, x2, y2, angleStep, numOfIterations);
}
private void Rotate(int startAngle, int angleStep, int numOfIterations) throws Exception {
Rotate(startAngle, radius, centerX, centerY, angleStep, numOfIterations);
}
private void Rotate(int angleStep, int numOfIterations) throws Exception {
Rotate(0, radius, centerX, centerY, angleStep, numOfIterations);
}
@Test
public void testSmallStepRightNoRotateSent() throws Exception {
Rotate(4, 1);
}
@Test
public void testRotateRightByFewSmallSteps() throws Exception {
Rotate(4, 5);
}
@Test
public void testRotateRight() throws Exception {
Rotate(15, 6);
}
@Test
public void testRotateRightBigSteps() throws Exception {
Rotate(50, 3);
}
@Test
@Ignore
public void testRotateRightOneBigStep() throws Exception {
Rotate(80, 1);
}
@Test
public void testSmallStepLeftNoRotateSent() throws Exception {
Rotate(-4, 1);
}
@Test
public void testRotateLeftByFewSmallSteps() throws Exception {
Rotate(-4, 10);
}
@Test
public void testRotateLeft() throws Exception {
Rotate(-10, 4);
}
@Test
public void testRotateLeftBigSteps() throws Exception {
Rotate(-40, 5);
}
@Test
@Ignore
public void testRotateLeftOneBigStep() throws Exception {
Rotate(-70, 1);
}
@Test
public void testRotateRightFrom45Degrees() throws Exception {
Rotate(45, 20, 3);
}
@Test
public void testRotateLeftFrom45Degrees() throws Exception {
Rotate(45, -20, 3);
}
@Test
public void testRotateRightFromMinus45Degrees() throws Exception {
Rotate(-45, 20, 3);
}
@Test
public void testRotateLeftFromMinus45Degrees() throws Exception {
Rotate(-45, -20, 3);
}
@Test
public void testRotateRightFrom140Degrees() throws Exception {
Rotate(140, 20, 3);
}
@Test
public void testRotateLeftFrom140Degrees() throws Exception {
Rotate(140, -20, 3);
}
@Test
public void testRotateRightFromMinus140Degrees() throws Exception {
Rotate(-140, 20, 3);
}
@Test
public void testRotateLeftFromMinus140Degrees() throws Exception {
Rotate(-140, -20, 3);
}
}
