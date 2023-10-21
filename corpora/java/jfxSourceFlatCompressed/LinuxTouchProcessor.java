package com.sun.glass.ui.monocle;
import java.security.AccessController;
import java.security.PrivilegedAction;
abstract class LinuxTouchProcessor implements LinuxInputProcessor {
final TouchState state = new TouchState();
final TouchPipeline pipeline;
final LinuxTouchTransform transform;
@SuppressWarnings("removal")
LinuxTouchProcessor(LinuxInputDevice device) {
transform = new LinuxTouchTransform(device);
PrivilegedAction<String> getFilterProperty =
() -> System.getProperty(
"monocle.input." + device.getProduct()
+ ".touchFilters",
"");
pipeline = new TouchPipeline();
pipeline.addNamedFilters(
AccessController.doPrivileged(getFilterProperty));
pipeline.add(TouchInput.getInstance().getBasePipeline());
}
}
