package com.sun.glass.ui.monocle;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
class LinuxTouchTransform {
private LinuxInputDevice device;
private int[] axes = new int[2];
private double[] translates = new double[2];
private double[] scalars = new double[2];
private boolean flipXY;
private int[] mins = new int[2];
private int[] maxs = new int[2];
@SuppressWarnings("removal")
LinuxTouchTransform(LinuxInputDevice device) {
this.device = device;
Arrays.fill(axes, -1);
String product = device.getProduct();
AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
flipXY = Boolean.getBoolean("monocle.input."
+ product + ".flipXY");
return null;
});
}
int getValue(LinuxEventBuffer buffer) {
int axis = buffer.getEventCode();
int value = buffer.getEventValue();
int i;
for (i = 0; i < axes.length && axes[i] != -1; i++) {
if (axes[i] == axis) {
return transform(i, value);
}
}
if (i == axes.length) {
axes = Arrays.copyOf(axes, axes.length * 2);
Arrays.fill(axes, i + 1, axes.length - 1, -1);
translates = Arrays.copyOf(translates, translates.length * 2);
scalars = Arrays.copyOf(scalars, scalars.length * 2);
mins = Arrays.copyOf(mins, mins.length * 2);
maxs = Arrays.copyOf(maxs, maxs.length * 2);
}
initTransform(axis, i);
return transform(i, value);
}
int getAxis(LinuxEventBuffer buffer) {
int axis = buffer.getEventCode();
if (flipXY) {
switch (axis) {
case LinuxInput.ABS_X: return LinuxInput.ABS_Y;
case LinuxInput.ABS_Y: return LinuxInput.ABS_X;
case LinuxInput.ABS_MT_POSITION_X: return LinuxInput.ABS_MT_POSITION_Y;
case LinuxInput.ABS_MT_POSITION_Y: return LinuxInput.ABS_MT_POSITION_X;
default:
return axis;
}
} else {
return axis;
}
}
private void initTransform(int axis, int index) {
double range;
String axisName;
switch (axis) {
case LinuxInput.ABS_X:
case LinuxInput.ABS_MT_POSITION_X:
range = NativePlatformFactory.getNativePlatform()
.getScreen().getWidth();
axisName = "X";
break;
case LinuxInput.ABS_Y:
case LinuxInput.ABS_MT_POSITION_Y:
range = NativePlatformFactory.getNativePlatform()
.getScreen().getHeight();
axisName = "Y";
break;
default:
translates[index] = 0.0;
scalars[index] = 1.0;
return;
}
LinuxAbsoluteInputCapabilities caps = device.getAbsoluteInputCapabilities(axis);
String product = device.getProduct();
@SuppressWarnings("removal")
var dummy = AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
int minimum = Integer.getInteger(
"monocle.input." + product + ".min" + axisName,
caps.getMinimum());
int maximum = Integer.getInteger(
"monocle.input." + product + ".max" + axisName,
caps.getMaximum());
translates[index] = -minimum;
scalars[index] = ((double) (range)) / (maximum - minimum);
return null;
});
}
private int transform(int index, int value) {
return (int) Math.round((value + translates[index]) * scalars[index]);
}
}
