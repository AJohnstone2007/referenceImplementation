package test.robot.com.sun.glass.ui.monocle.input.devices;
import test.robot.com.sun.glass.ui.monocle.TestApplication;
import test.robot.com.sun.glass.ui.monocle.UInput;
import org.junit.Assume;
import java.util.HashMap;
import java.util.Map;
public class TouchRevolutionFusionDevice extends TestTouchDevice {
private int currentSlot = 0;
private Map<Integer, Integer> slotsToPoints = new HashMap<>();
private Map<Integer, Integer> pointsToSlots = new HashMap<>();
public TouchRevolutionFusionDevice() {
super(5);
}
@Override
public void create() {
Assume.assumeTrue(TestApplication.isMonocle());
ui = new UInput();
ui.processLine("OPEN");
ui.processLine("EVBIT EV_SYN");
ui.processLine("EVBIT EV_KEY");
ui.processLine("KEYBIT BTN_TOUCH");
ui.processLine("EVBIT EV_ABS");
ui.processLine("ABSBIT ABS_X");
ui.processLine("ABSBIT ABS_Y");
ui.processLine("ABSBIT ABS_MT_POSITION_X");
ui.processLine("ABSBIT ABS_MT_POSITION_Y");
ui.processLine("ABSBIT ABS_MT_TRACKING_ID");
ui.processLine("ABSBIT ABS_MT_SLOT");
ui.processLine("ABSMIN ABS_X 0");
ui.processLine("ABSMAX ABS_X 4095");
ui.processLine("ABSMIN ABS_Y 0");
ui.processLine("ABSMAX ABS_Y 4095");
ui.processLine("ABSMIN ABS_MT_POSITION_X 0");
ui.processLine("ABSMAX ABS_MT_POSITION_X 4095");
ui.processLine("ABSMIN ABS_MT_POSITION_Y 0");
ui.processLine("ABSMAX ABS_MT_POSITION_Y 4095");
ui.processLine("PROPBIT INPUT_PROP_POINTER");
ui.processLine("PROPBIT INPUT_PROP_DIRECT");
ui.processLine("PROPERTY ID_INPUT_TOUCHSCREEN 1");
ui.processLine("BUS 0x0003");
ui.processLine("VENDOR 0x1391");
ui.processLine("PRODUCT 0x3001");
ui.processLine("VERSION 0x0111");
ui.processLine("CREATE");
setAbsScale(4096, 4096);
}
@Override
public int addPoint(double x, double y) {
int p = super.addPoint(x, y);
int slot = -1;
for (int i = 0; i < points.length; i++) {
if (!slotsToPoints.containsKey(i)) {
slot = i;
break;
}
}
if (slot == -1) {
throw new IllegalStateException("No free slot");
}
if (currentSlot != slot) {
ui.processLine("EV_ABS ABS_MT_SLOT " + slot);
currentSlot = slot;
}
slotsToPoints.put(slot, p);
pointsToSlots.put(p, slot);
ui.processLine("EV_ABS ABS_MT_TRACKING_ID " + getID(p));
ui.processLine("EV_ABS ABS_MT_POSITION_X " + transformedXs[p]);
ui.processLine("EV_ABS ABS_MT_POSITION_Y " + transformedYs[p]);
if (pressedPoints == 1) {
ui.processLine("EV_KEY BTN_TOUCH 1");
}
ui.processLine("EV_ABS ABS_X " + transformedXs[p]);
ui.processLine("EV_ABS ABS_Y " + transformedYs[p]);
return p;
}
private int selectSlotForPoint(int p) {
int slot = pointsToSlots.get(p);
if (slot != currentSlot) {
ui.processLine("EV_ABS ABS_MT_SLOT " + slot);
currentSlot = slot;
}
return currentSlot;
}
@Override
public void removePoint(int p) {
super.removePoint(p);
int slot = selectSlotForPoint(p);
pointsToSlots.remove(p);
slotsToPoints.remove(slot);
ui.processLine("EV_ABS ABS_MT_TRACKING_ID -1");
if (pressedPoints == 0) {
ui.processLine("EV_KEY BTN_TOUCH 0");
}
}
@Override
public void setPoint(int p, double x, double y) {
int oldX = transformedXs[p];
int oldY = transformedYs[p];
super.setPoint(p, x, y);
if (oldX != transformedXs[p]) {
selectSlotForPoint(p);
ui.processLine("EV_ABS ABS_MT_POSITION_X " + transformedXs[p]);
ui.processLine("EV_ABS ABS_X " + transformedXs[p]);
}
if (oldY != transformedYs[p]) {
selectSlotForPoint(p);
ui.processLine("EV_ABS ABS_MT_POSITION_Y " + transformedYs[p]);
ui.processLine("EV_ABS ABS_Y " + transformedYs[p]);
}
}
@Override
public void resendStateAndSync() {
for (int p = 0; p < points.length; p++) {
if (points[p]) {
selectSlotForPoint(p);
ui.processLine("EV_ABS ABS_MT_POSITION_X " + transformedXs[p]);
ui.processLine("EV_ABS ABS_X " + transformedXs[p]);
ui.processLine("EV_ABS ABS_MT_POSITION_Y " + transformedYs[p]);
ui.processLine("EV_ABS ABS_Y " + transformedYs[p]);
}
}
sync();
}
}
