package com.sun.glass.ui.monocle;
import java.nio.channels.ReadableByteChannel;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
public class LinuxInputDeviceRegistryShim {
private static LinuxInputDeviceRegistry registry
= (LinuxInputDeviceRegistry) NativePlatformFactory.getNativePlatform().getInputDeviceRegistry();
public static class LinuxInputDeviceShim {
LinuxInputDevice device;
private LinuxInputDeviceShim(LinuxInputDevice d) {
device = d;
}
public boolean isQuiet() {
return device.isQuiet();
}
}
private static final int INDEX_VALUE = 0;
private static final int INDEX_MIN = 1;
private static final int INDEX_MAX = 2;
private static final int INDEX_FUZZ = 3;
private static final int INDEX_FLAT = 4;
private static final int INDEX_RESOLUTION = 5;
private static final int INDEX_COUNT = 6;
static Map<Integer, LinuxAbsoluteInputCapabilities> createAbsCapsMap(Map<Integer, int[]> absCaps) {
Map<Integer, LinuxAbsoluteInputCapabilities> map
= new HashMap<Integer, LinuxAbsoluteInputCapabilities>();
for (Integer axis : absCaps.keySet()) {
int[] a = absCaps.get(axis);
if (a != null) {
LinuxAbsoluteInputCapabilities absCap = new LinuxAbsoluteInputCapabilities(
a[INDEX_VALUE],
a[INDEX_MAX],
a[INDEX_MIN],
a[INDEX_FUZZ],
a[INDEX_FLAT],
a[INDEX_RESOLUTION]);
map.put(axis, absCap);
}
}
return map;
}
public static LinuxInputDeviceShim addDevice(
Map<String, BitSet> capabilities,
Map<Integer, int[]> absCaps,
ReadableByteChannel in,
Map<String, String> udevManifest,
Map<String, String> uevent,
String name) {
LinuxInputDevice device = new LinuxInputDevice(capabilities,
createAbsCapsMap(absCaps),
in,
udevManifest,
uevent);
registry.addDevice(
device,
name);
return new LinuxInputDeviceShim(device);
}
public static void removeDevice(LinuxInputDeviceShim device) {
registry.removeDevice(device.device);
}
}
