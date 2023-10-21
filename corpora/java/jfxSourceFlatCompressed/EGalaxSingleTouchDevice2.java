package test.robot.com.sun.glass.ui.monocle.input.devices;
public class EGalaxSingleTouchDevice2 extends EGalaxSingleTouchDeviceBase {
@Override
public int addPoint(double x, double y) {
int p = super.addPoint(x, y);
ui.processLine("EV_ABS ABS_X " + transformedXs[p]);
ui.processLine("EV_ABS ABS_Y " + transformedYs[p]);
ui.processLine("EV_KEY BTN_TOUCH 1");
ui.processLine("EV_ABS ABS_PRESSURE 1");
return p;
}
@Override
public void removePoint(int p) {
super.removePoint(p);
ui.processLine("EV_KEY BTN_TOUCH 0");
ui.processLine("EV_ABS ABS_PRESSURE 0");
}
}
