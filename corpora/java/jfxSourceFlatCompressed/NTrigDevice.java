package test.robot.com.sun.glass.ui.monocle.input.devices;
import test.robot.com.sun.glass.ui.monocle.UInput;
import java.util.Random;
public class NTrigDevice extends TestTouchDevice {
private Random random = new Random(1l);
public NTrigDevice() {
super(5);
}
@Override
public void create() {
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
ui.processLine("ABSBIT ABS_MT_ORIENTATION");
ui.processLine("ABSBIT ABS_MT_TOUCH_MAJOR");
ui.processLine("ABSBIT ABS_MT_TOUCH_MINOR");
ui.processLine("ABSMIN ABS_X 0");
ui.processLine("ABSMAX ABS_X 4095");
ui.processLine("ABSMIN ABS_Y 0");
ui.processLine("ABSMAX ABS_Y 4095");
ui.processLine("ABSMIN ABS_MT_POSITION_X 0");
ui.processLine("ABSMAX ABS_MT_POSITION_X 4095");
ui.processLine("ABSMIN ABS_MT_POSITION_Y 0");
ui.processLine("ABSMAX ABS_MT_POSITION_Y 4095");
ui.processLine("ABSMIN ABS_MT_ORIENTATION 0");
ui.processLine("ABSMAX ABS_MT_ORIENTATION 1");
ui.processLine("PROPBIT INPUT_PROP_POINTER");
ui.processLine("PROPBIT INPUT_PROP_DIRECT");
ui.processLine("PROPERTY ID_INPUT_TOUCHSCREEN 1");
ui.processLine("CREATE");
setAbsScale(4096, 4096);
}
@Override
public void sync() {
if (pressedPoints > 0) {
ui.processLine("EV_ABS ABS_X " + random.nextInt(4096));
ui.processLine("EV_ABS ABS_Y " + random.nextInt(4096));
}
for (int p = 0; p < points.length; p++) {
if (points[p]) {
ui.processLine("EV_ABS ABS_MT_POSITION_X " + transformedXs[p]);
ui.processLine("EV_ABS ABS_MT_POSITION_Y " + transformedYs[p]);
ui.processLine("EV_ABS ABS_MT_TOUCH_MAJOR 635");
ui.processLine("EV_ABS ABS_MT_TOUCH_MINOR 533");
ui.processLine("EV_SYN SYN_MT_REPORT 0");
}
}
if (previousPressedPoints == 0 && pressedPoints > 0) {
ui.processLine("EV_KEY BTN_TOOL_DOUBLETAP 1");
ui.processLine("EV_KEY BTN_TOUCH 1");
} else if (previousPressedPoints > 0 && pressedPoints == 0) {
ui.processLine("EV_KEY BTN_TOOL_DOUBLETAP 0");
ui.processLine("EV_KEY BTN_TOUCH 0 ");
}
super.sync();
}
}
