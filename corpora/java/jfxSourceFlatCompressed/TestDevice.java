package test.robot.com.sun.glass.ui.monocle.input.devices;
import test.robot.com.sun.glass.ui.monocle.UInput;
public abstract class TestDevice {
protected UInput ui;
public abstract void create();
public void destroy() {
if (ui != null) {
try {
ui.waitForQuiet();
} catch (InterruptedException e) {
}
try {
ui.processLine("DESTROY");
} catch (RuntimeException e) {
}
try {
ui.processLine("CLOSE");
} catch (RuntimeException e) {
}
ui.dispose();
}
}
public void sync() {
ui.processLine("EV_SYN SYN_REPORT 0");
}
}
