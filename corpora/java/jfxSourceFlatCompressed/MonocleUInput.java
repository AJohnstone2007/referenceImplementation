package test.robot.com.sun.glass.ui.monocle;
import test.robot.com.sun.glass.ui.monocle.TestApplication;
import com.sun.glass.ui.Application;
import com.sun.glass.ui.monocle.LinuxInputDeviceRegistryShim;
import javafx.application.Platform;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import test.com.sun.glass.ui.monocle.NativeUInput;
class MonocleUInput extends NativeUInput {
private Pipe pipe;
private LinuxInputDeviceRegistryShim.LinuxInputDeviceShim device;
MonocleUInput() {
super();
}
@Override
protected void createDevice() {
try {
pipe = Pipe.open();
} catch (IOException e) {
e.printStackTrace();
}
uevent.put("PRODUCT",
Integer.toHexString(bus) + "/"
+ Integer.toHexString(vendor) + "/"
+ Integer.toHexString(product) + "/"
+ Integer.toHexString(version));
Application.invokeAndWait(() -> device
= LinuxInputDeviceRegistryShim.addDevice(capabilities,
absCaps,
pipe.source(),
udevManifest,
uevent,
"Simulated Linux Input Device"));
}
protected void openConnection() {
}
@Override
protected void closeConnection() {
}
@Override
protected void destroyDevice() {
try {
if (pipe != null) {
pipe.sink().close();
pipe.source().close();
}
} catch (IOException e) {
e.printStackTrace();
}
pipe = null;
if (device != null) {
final LinuxInputDeviceRegistryShim.LinuxInputDeviceShim d = device;
if (Platform.isFxApplicationThread()) {
LinuxInputDeviceRegistryShim.removeDevice(d);
} else {
CountDownLatch latch = new CountDownLatch(1);
Platform.runLater(() -> {
LinuxInputDeviceRegistryShim.removeDevice(d);
latch.countDown();
});
try {
latch.await();
} catch (InterruptedException e) {
e.printStackTrace();
}
}
device = null;
}
}
@Override
public void setup() {
}
@Override
public void dispose() {
destroyDevice();
closeConnection();
}
@Override
public void write(ByteBuffer buffer) throws IOException {
pipe.sink().write(buffer);
}
@Override
public void waitForQuiet() throws InterruptedException {
if (device != null) {
AtomicReference<Boolean> done = new AtomicReference<>(Boolean.FALSE);
do {
Application.invokeAndWait(() -> done.set(device.isQuiet()));
if (!done.get()) {
TestApplication.waitForNextPulse();
}
} while (!done.get());
}
}
}
