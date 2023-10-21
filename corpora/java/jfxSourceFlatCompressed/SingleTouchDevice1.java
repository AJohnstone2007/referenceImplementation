package test.robot.com.sun.glass.ui.monocle.input.devices;
import test.robot.com.sun.glass.ui.monocle.UInput;
public class SingleTouchDevice1 extends TestTouchDevice {
public SingleTouchDevice1() {
super(1);
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
ui.processLine("ABSMIN ABS_X 0");
ui.processLine("ABSMAX ABS_X 4095");
ui.processLine("ABSMIN ABS_Y 0");
ui.processLine("ABSMAX ABS_Y 4095");
ui.processLine("PROPBIT INPUT_PROP_POINTER");
ui.processLine("PROPBIT INPUT_PROP_DIRECT");
ui.processLine("PROPERTY ID_INPUT_TOUCHSCREEN 1");
ui.processLine("CREATE");
setAbsScale(4096, 4096);
}
@Override
public int addPoint(double x, double y) {
int p = super.addPoint(x, y);
ui.processLine("EV_KEY BTN_TOUCH 1");
ui.processLine("EV_ABS ABS_X " + transformedXs[p]);
ui.processLine("EV_ABS ABS_Y " + transformedYs[p]);
return p;
}
@Override
public void removePoint(int p) {
super.removePoint(p);
ui.processLine("EV_KEY BTN_TOUCH 0");
}
@Override
public void setPoint(int p, double x, double y) {
super.setPoint(p, x, y);
ui.processLine("EV_ABS ABS_X " + transformedXs[p]);
ui.processLine("EV_ABS ABS_Y " + transformedYs[p]);
}
@Override
public void resendStateAndSync() {
if (points[0]) {
ui.processLine("EV_ABS ABS_X " + transformedXs[0]);
ui.processLine("EV_ABS ABS_Y " + transformedYs[0]);
}
sync();
}
}
