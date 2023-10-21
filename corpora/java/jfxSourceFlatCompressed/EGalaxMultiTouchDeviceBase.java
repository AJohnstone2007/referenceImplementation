package test.robot.com.sun.glass.ui.monocle.input.devices;
import test.robot.com.sun.glass.ui.monocle.TestApplication;
import com.sun.glass.ui.monocle.TestLogShim;
import test.robot.com.sun.glass.ui.monocle.UInput;
import org.junit.Assume;
import java.util.HashSet;
import java.util.Set;
public class EGalaxMultiTouchDeviceBase extends TestTouchDevice {
protected final boolean resendStationaryPoints;
protected final boolean sendBtnTouch;
protected final boolean sendTouchMajor;
protected final SendIDOnRelease sendIDOnRelease;
private Set<Integer> modifiedPoints = new HashSet<>();
enum SendIDOnRelease {
SEND_ID, SEND_MINUS_ONE, DONT_SEND
}
public EGalaxMultiTouchDeviceBase(boolean resendStationaryPoints,
boolean sendBtnTouch,
boolean sendTouchMajor,
SendIDOnRelease sendIDOnRelease) {
super(5);
this.resendStationaryPoints = resendStationaryPoints;
this.sendBtnTouch = sendBtnTouch;
this.sendTouchMajor = sendTouchMajor;
this.sendIDOnRelease = sendIDOnRelease;
}
@Override
public void create() {
Assume.assumeTrue(TestApplication.isMonocle());
ui = new UInput();
ui.processLine("OPEN");
ui.processLine("EVBIT EV_SYN");
if (sendBtnTouch) {
ui.processLine("EVBIT EV_KEY");
ui.processLine("KEYBIT BTN_TOUCH");
}
ui.processLine("ABSBIT ABS_MT_TRACKING_ID");
ui.processLine("ABSBIT ABS_MT_POSITION_X");
ui.processLine("ABSBIT ABS_MT_POSITION_Y");
if (sendTouchMajor) {
ui.processLine("ABSBIT ABS_MT_TOUCH_MAJOR");
ui.processLine("ABSMIN ABS_MT_TOUCH_MAJOR 0");
ui.processLine("ABSMAX ABS_MT_TOUCH_MAJOR 255");
}
ui.processLine("ABSMIN ABS_MT_TRACKING_ID 0");
ui.processLine("ABSMAX ABS_MT_TRACKING_ID 5");
ui.processLine("ABSMIN ABS_MT_POSITION_X 0");
ui.processLine("ABSMAX ABS_MT_POSITION_X 32767");
ui.processLine("ABSMIN ABS_MT_POSITION_Y 0");
ui.processLine("ABSMAX ABS_MT_POSITION_Y 32767");
ui.processLine("PROPBIT INPUT_PROP_POINTER");
ui.processLine("PROPBIT INPUT_PROP_DIRECT");
ui.processLine("PROPERTY ID_INPUT_TOUCHSCREEN 1");
ui.processLine("CREATE");
setAbsScale(32768, 32768);
}
@Override
public int addPoint(double x, double y) {
int p = super.addPoint(x, y);
ui.processLine("EV_ABS ABS_MT_TRACKING_ID " + p);
if (sendTouchMajor) {
ui.processLine("EV_ABS ABS_MT_TOUCH_MAJOR 1");
}
ui.processLine("EV_ABS ABS_MT_POSITION_X " + transformedXs[p]);
ui.processLine("EV_ABS ABS_MT_POSITION_Y " + transformedYs[p]);
ui.processLine("EV_SYN SYN_MT_REPORT 0");
modifiedPoints.add(p);
if (sendBtnTouch && pressedPoints == 1) {
ui.processLine("EV_KEY BTN_TOUCH 1");
}
return p;
}
@Override
public void removePoint(int p) {
super.removePoint(p);
if (pressedPoints == 0) {
switch (sendIDOnRelease) {
case SEND_ID:
ui.processLine("EV_ABS ABS_MT_TRACKING_ID " + p);
break;
case SEND_MINUS_ONE:
ui.processLine("EV_ABS ABS_MT_TRACKING_ID -1");
break;
case DONT_SEND:
break;
}
if (sendTouchMajor) {
ui.processLine("EV_ABS ABS_MT_TOUCH_MAJOR 0");
}
if (sendIDOnRelease == SendIDOnRelease.SEND_ID) {
ui.processLine("EV_SYN SYN_MT_REPORT 0");
}
if (sendBtnTouch) {
ui.processLine("EV_KEY BTN_TOUCH 0");
}
} else {
ui.processLine("EV_ABS ABS_MT_TRACKING_ID " + p);
if (sendTouchMajor) {
ui.processLine("EV_ABS ABS_MT_TOUCH_MAJOR 0");
}
ui.processLine("EV_SYN SYN_MT_REPORT 0");
}
}
@Override
public void setPoint(int p, double x, double y) {
super.setPoint(p, x, y);
ui.processLine("EV_ABS ABS_MT_TRACKING_ID " + p);
if (sendTouchMajor) {
ui.processLine("EV_ABS ABS_MT_TOUCH_MAJOR 1");
}
ui.processLine("EV_ABS ABS_MT_POSITION_X " + transformedXs[p]);
ui.processLine("EV_ABS ABS_MT_POSITION_Y " + transformedYs[p]);
ui.processLine("EV_SYN SYN_MT_REPORT 0");
modifiedPoints.add(p);
}
@Override
public void setAndRemovePoint(int p, double x, double y) {
removePoint(p);
}
@Override
public void sync() {
if (resendStationaryPoints) {
for (int p = 0; p < points.length; p++) {
if (points[p] && !modifiedPoints.contains(p)) {
ui.processLine("EV_ABS ABS_MT_TRACKING_ID " + p);
if (sendTouchMajor) {
ui.processLine("EV_ABS ABS_MT_TOUCH_MAJOR 1");
}
ui.processLine("EV_ABS ABS_MT_POSITION_X " + transformedXs[p]);
ui.processLine("EV_ABS ABS_MT_POSITION_Y " + transformedYs[p]);
ui.processLine("EV_SYN SYN_MT_REPORT 0");
}
}
}
modifiedPoints.clear();
super.sync();
}
@Override
public void resendStateAndSync() {
TestLogShim.log("TestTouchDevice: sync");
for (int p = 0; p < points.length; p++) {
if (points[p]) {
ui.processLine("EV_ABS ABS_MT_TRACKING_ID " + p);
ui.processLine("EV_ABS ABS_MT_TOUCH_MAJOR 1");
ui.processLine("EV_ABS ABS_MT_POSITION_X " + transformedXs[p]);
ui.processLine("EV_ABS ABS_MT_POSITION_Y " + transformedYs[p]);
ui.processLine("EV_SYN SYN_MT_REPORT 0");
}
}
modifiedPoints.clear();
sync();
}
}
