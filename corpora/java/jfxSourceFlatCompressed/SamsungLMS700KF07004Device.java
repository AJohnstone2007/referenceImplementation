package test.robot.com.sun.glass.ui.monocle.input.devices;
import test.robot.com.sun.glass.ui.monocle.TestApplication;
import test.robot.com.sun.glass.ui.monocle.UInput;
import org.junit.Assume;
public class SamsungLMS700KF07004Device extends TestTouchDevice {
public SamsungLMS700KF07004Device() {
super(1);
}
@Override
public void create() {
Assume.assumeTrue(TestApplication.isMonocle());
ui = new UInput();
ui.processLine("OPEN");
ui.processLine("EVBIT EV_SYN");
ui.processLine("EVBIT EV_KEY");
ui.processLine("KEYBIT BTN_TOUCH");
ui.processLine("KEYBIT BTN_TOOL_PEN");
ui.processLine("EVBIT EV_ABS");
ui.processLine("ABSBIT ABS_X");
ui.processLine("ABSBIT ABS_Y");
ui.processLine("ABSMIN ABS_X 0");
ui.processLine("ABSMAX ABS_X 4095");
ui.processLine("ABSMIN ABS_Y 0");
ui.processLine("ABSMAX ABS_Y 4095");
ui.processLine("PROPBIT INPUT_PROP_POINTER");
ui.processLine("PROPBIT INPUT_PROP_DIRECT");
ui.processLine("PROPERTY ID_INPUT_TABLET 1");
ui.processLine("PROPERTY ID_VENDOR eGalax_Inc.");
ui.processLine("PROPERTY ID_VENDOR_ID 0x0eef");
ui.processLine("PROPERTY ID_VENDOR_ENC eGalax");
ui.processLine("PROPERTY ID_BUS usb");
ui.processLine("PROPERTY ID_INPUT 1");
ui.processLine("PROPERTY ID_MODEL Touch");
ui.processLine("PROPERTY ID_MODEL_ENC Touch");
ui.processLine("PROPERTY ID_MODEL_ID 0001");
ui.processLine("PROPERTY ID_REVISION 0100");
ui.processLine("PROPERTY ID_SERIAL eGalax_Inc._Touch");
ui.processLine("PROPERTY ID_TYPE hid");
ui.processLine("PROPERTY ID_USB_DRIVER usbhid");
ui.processLine("PROPERTY ID_MODEL_ID 0001");
ui.processLine("PROPERTY ID_REVISION 0100");
ui.processLine("PROPERTY ID_USB_INTERFACES :030000:");
ui.processLine("PROPERTY ID_USB_INTERFACE_NUM 00");
ui.processLine("PROPERTY MAJOR 13");
ui.processLine("PROPERTY MINOR 65");
ui.processLine("PROPERTY SUBSYSTEM input");
ui.processLine("CREATE");
setAbsScale(4096, 4096);
}
@Override
public int addPoint(double x, double y) {
int p = super.addPoint(x, y);
ui.processLine("EV_KEY BTN_TOUCH 1");
ui.processLine("EV_KEY BTN_TOOL_PEN 1");
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
int oldX = transformedXs[p];
int oldY = transformedYs[p];
super.setPoint(p, x, y);
if (oldX != transformedXs[p] || oldY == transformedYs[p]) {
ui.processLine("EV_ABS ABS_X " + transformedXs[p]);
}
if (oldY != transformedYs[p]) {
ui.processLine("EV_ABS ABS_Y " + transformedYs[p]);
}
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
