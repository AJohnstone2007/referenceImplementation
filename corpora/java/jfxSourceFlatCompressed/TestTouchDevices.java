package test.robot.com.sun.glass.ui.monocle.input.devices;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
public class TestTouchDevices {
public static List<TestTouchDevice> getTouchDevices() {
List<TestTouchDevice> devices = new ArrayList<>();
String selectedDeviceClass = System.getProperty("device");
if (selectedDeviceClass != null) {
if (!selectedDeviceClass.contains(".")) {
selectedDeviceClass = "com.sun.glass.ui.monocle.input.devices."
+ selectedDeviceClass;
}
try {
devices.add((TestTouchDevice)
Class.forName(selectedDeviceClass).getDeclaredConstructor().newInstance());
} catch (Exception e) {
e.printStackTrace();
}
return devices;
}
devices.addAll(Arrays.asList(new TestTouchDevice[] {
new SingleTouchDevice1(),
new SingleTouchDevice2(),
new EGalaxSingleTouchDevice1(),
new EGalaxSingleTouchDevice2(),
new EGalaxMultiTouchDevice1(),
new EGalaxMultiTouchDevice2(),
new EGalaxMultiTouchDevice3(),
new EGalaxMultiTouchDevice4(),
new EGalaxMultiTouchDevice5(),
new EGalaxMultiTouchDevice6(),
new TouchRevolutionFusionDevice(),
new NTrigDevice(),
new SamsungLMS700KF07004Device(),
new TabletDevice(),
new DellP2714TDevice(),
}));
return devices;
}
public static List<TestTouchDevice> getTouchDevices(int minPoints) {
return getTouchDevices().stream()
.filter(d -> d.points.length >= minPoints)
.collect(Collectors.toList());
}
public static Collection<Object[]> getTouchDeviceParameters(int minPoints) {
Collection c = getTouchDevices().stream()
.filter(d -> d.points.length >= minPoints)
.map(d -> new Object[] { d })
.collect(Collectors.toList());
return c;
}
}
