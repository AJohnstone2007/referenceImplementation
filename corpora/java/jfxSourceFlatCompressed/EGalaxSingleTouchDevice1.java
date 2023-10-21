package test.robot.com.sun.glass.ui.monocle.input.devices;
public class EGalaxSingleTouchDevice1 extends EGalaxSingleTouchDeviceBase {
private boolean firstPress = true;
@Override
public int addPoint(double x, double y) {
int p = super.addPoint(x, y);
if (firstPress) {
ui.processLine("EV_ABS ABS_X " + transformedXs[p]);
ui.processLine("EV_ABS ABS_Y " + transformedYs[p]);
ui.processLine("EV_SYN SYN_REPORT 0");
firstPress = false;
}
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
}
