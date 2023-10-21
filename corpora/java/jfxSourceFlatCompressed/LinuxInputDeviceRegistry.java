package com.sun.glass.ui.monocle;
import java.io.File;
import java.io.IOException;
import java.security.AllPermission;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
class LinuxInputDeviceRegistry extends InputDeviceRegistry {
LinuxInputDeviceRegistry(boolean headless) {
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
private LinuxInputDevice createDevice(File devNode,
File sysPath,
Map<String, String> udevManifest)
throws IOException {
LinuxInputDevice device = new LinuxInputDevice(
devNode, sysPath, udevManifest);
return addDeviceInternal(device, "Linux input: " + devNode.toString());
}
LinuxInputDevice addDevice(LinuxInputDevice device, String name) {
@SuppressWarnings("removal")
SecurityManager security = System.getSecurityManager();
if (security != null) {
security.checkPermission(new AllPermission());
}
return addDeviceInternal(device, name);
}
private LinuxInputDevice addDeviceInternal(LinuxInputDevice device, String name) {
LinuxInputProcessor processor = createInputProcessor(device);
if (processor == null) {
return null;
} else {
device.setInputProcessor(processor);
Thread thread = new Thread(device);
thread.setName(name);
thread.setDaemon(true);
thread.start();
devices.add(device);
return device;
}
}
void removeDevice(LinuxInputDevice device) {
@SuppressWarnings("removal")
SecurityManager security = System.getSecurityManager();
if (security != null) {
security.checkPermission(new AllPermission());
}
devices.remove(device);
}
private LinuxInputProcessor createInputProcessor(LinuxInputDevice device) {
if (device.isTouch()) {
BitSet absCaps = device.getCapability("abs");
boolean isMT = absCaps.get(LinuxInput.ABS_MT_POSITION_X)
&& absCaps.get(LinuxInput.ABS_MT_POSITION_Y);
if (isMT) {
if (absCaps.get(LinuxInput.ABS_MT_TRACKING_ID)) {
return new LinuxStatefulMultiTouchProcessor(device);
} else {
return new LinuxStatelessMultiTouchProcessor(device);
}
} else {
return new LinuxSimpleTouchProcessor(device);
}
} else if (device.isRelative()) {
return new LinuxMouseProcessor();
} else {
BitSet keyCaps = device.getCapability("key");
if (keyCaps != null && !keyCaps.isEmpty()) {
return new LinuxKeyProcessor();
} else {
return null;
}
}
}
}
