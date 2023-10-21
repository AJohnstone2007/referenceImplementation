package com.sun.glass.ui.monocle;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
class EPDInputDeviceRegistry extends InputDeviceRegistry {
private static final String KEYPAD_FILENAME = "event0";
private static final String TOUCH_FILENAME = "event1";
EPDInputDeviceRegistry(boolean headless) {
if (headless) {
return;
}
Map<File, LinuxInputDevice> deviceMap = new HashMap<>();
UdevListener udevListener = (action, event) -> {
String subsystem = event.get("SUBSYSTEM");
String devPath = event.get("DEVPATH");
String devName = event.get("DEVNAME");
if (subsystem != null && subsystem.equals("input")
&& devPath != null && devName != null) {
try {
File sysPath = new File("/sys", devPath);
if (action.equals("add")
|| (action.equals("change")
&& !deviceMap.containsKey(sysPath))) {
File devNode = new File(devName);
LinuxInputDevice device = createDevice(
devNode, sysPath, event);
if (device != null) {
deviceMap.put(sysPath, device);
}
} else if (action.equals("remove")) {
LinuxInputDevice device = deviceMap.get(sysPath);
deviceMap.remove(sysPath);
if (device != null) {
devices.remove(device);
}
}
} catch (IOException e) {
e.printStackTrace();
}
}
};
Udev.getInstance().addListener(udevListener);
SysFS.triggerUdevNotification("input");
}
private LinuxInputDevice createDevice(File devNode, File sysPath,
Map<String, String> udevManifest) throws IOException {
LinuxSystem system = LinuxSystem.getLinuxSystem();
system.open(devNode.getPath(), LinuxSystem.O_RDONLY);
var device = new LinuxInputDevice(devNode, sysPath, udevManifest);
return addDeviceInternal(device, "Linux input: " + devNode.toString());
}
private LinuxInputDevice addDeviceInternal(LinuxInputDevice device, String name) {
LinuxInputProcessor processor = null;
if (name.endsWith(KEYPAD_FILENAME)) {
processor = new LinuxKeyProcessor();
} else if (name.endsWith(TOUCH_FILENAME)) {
processor = new LinuxSimpleTouchProcessor(device);
}
if (processor == null) {
return null;
} else {
device.setInputProcessor(processor);
var thread = new Thread(device);
thread.setName(name);
thread.setDaemon(true);
thread.start();
devices.add(device);
return device;
}
}
@Override
public String toString() {
return MessageFormat.format("{0}[devices={1}]", getClass().getName(), devices);
}
}
