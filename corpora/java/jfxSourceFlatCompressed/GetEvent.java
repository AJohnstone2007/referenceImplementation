package com.sun.glass.ui.monocle;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
class GetEvent {
private static Set<File> devices = new HashSet<>();
private static UdevListener udevListener = (action, event) -> {
String subsystem = event.get("SUBSYSTEM");
String devPath = event.get("DEVPATH");
if (subsystem != null && subsystem.equals("input")
&& devPath != null) {
System.out.format("%1$ts.%1$tL Received UEVENT:\n",
new Date());
List<String> keys = new ArrayList<>(event.keySet());
Collections.sort(keys);
for (String key: keys) {
System.out.format("  %s='%s'\n", key, event.get(key));
}
try {
File sysPath = new File("/sys", devPath);
String devNode = event.get("DEVNAME");
if (devNode == null) {
return;
}
if (action.equals("add")
|| (action.equals("change")
&& !devices.contains(sysPath))) {
LinuxInputDevice device = new LinuxInputDevice(
new File(devNode), sysPath, event);
device.setInputProcessor(new LinuxInputProcessor.Logger());
Thread thread = new Thread(device);
thread.setName(devNode.toString());
thread.setDaemon(true);
thread.start();
System.out.println("Added device " + devNode);
System.out.println("  touch=" + device.isTouch());
System.out.println("  multiTouch=" + device.isMultiTouch());
System.out.println("  relative=" + device.isRelative());
System.out.println("  5-way=" + device.is5Way());
System.out.println("  fullKeyboard=" + device.isFullKeyboard());
System.out.println("  PRODUCT=" + device.getProduct());
for (short axis = 0; axis < LinuxInput.ABS_MAX; axis++) {
LinuxAbsoluteInputCapabilities caps =
device.getAbsoluteInputCapabilities(axis);
if (caps != null) {
String axisName = LinuxInput.codeToString("EV_ABS", axis);
System.out.format("  ABSVAL %s %d\n",
axisName, caps.getValue());
System.out.format("  ABSMIN %s %d\n",
axisName, caps.getMinimum());
System.out.format("  ABSMAX %s %d\n",
axisName, caps.getMaximum());
System.out.format("  ABSFUZZ %s %d\n",
axisName, caps.getFuzz());
System.out.format("  ABSFLAT %s %d\n",
axisName, caps.getFlat());
System.out.format("  ABSRES %s %d\n",
axisName, caps.getResolution());
}
}
devices.add(sysPath);
} else if (action.equals("remove")) {
devices.remove(devPath);
}
} catch (IOException | RuntimeException e) {
e.printStackTrace();
}
}
};
public static void main(String[] argv) throws Exception {
NativePlatform platform = NativePlatformFactory.getNativePlatform();
Udev.getInstance().addListener(udevListener);
SysFS.triggerUdevNotification("input");
new Thread(platform.getRunnableProcessor()).start();
}
}
