package test.com.sun.glass.ui.monocle;
import com.sun.glass.ui.monocle.LinuxInputShim;
import com.sun.glass.ui.monocle.LinuxSystemShim;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.BitSet;
import java.util.concurrent.CountDownLatch;
public class LensUInput extends NativeUInput {
private long fd = -1;
private long devFD = -1;
private String deviceName;
private short bustype, product, vendor, version;
private String devNode;
private static int devNodeSuffix = 0;
private static final String INPUT_PATH = "/tmp/testInput";
private static final String DEVNODE_PREFIX = "/tmp/input";
private static ByteBuffer byteBuffer = ByteBuffer.allocateDirect(256)
.order(ByteOrder.nativeOrder());
private static boolean isSetup;
@Override
public void setup() {
if (isSetup) {
return;
}
try {
LinuxSystemShim.loadLibrary();
LinuxSystemShim.setenv("LENS_TEST_INPUT", INPUT_PATH, true);
File pipe = new File(INPUT_PATH);
if (pipe.exists()) {
pipe.delete();
}
pipe.deleteOnExit();
if (LinuxSystemShim.mkfifo(pipe.getPath(), LinuxSystemShim.S_IRWXU) != 0) {
throw new IOException(LinuxSystemShim.getErrorMessage());
}
isSetup = true;
} catch (IOException e) {
e.printStackTrace();
}
}
@Override
public void init() {
super.init();
fd = -1;
devFD = -1;
deviceName = "Test Input";
bustype = 0x18;
product = 0x01;
vendor = 0x01;
version = 0x01;
}
@Override
public void dispose() {
}
@Override
public void waitForQuiet() throws InterruptedException {
final CountDownLatch frameCounter = new CountDownLatch(3);
Platform.runLater(() -> new AnimationTimer() {
@Override
public void handle(long now) {
frameCounter.countDown();
if (frameCounter.getCount() == 0l) {
stop();
}
}
}.start());
frameCounter.await();
}
private void writeBytes(int length) throws IOException {
int offset = 0;
while (offset < length) {
int bytesWritten = (int) LinuxSystemShim.write(fd, byteBuffer, offset, length);
if (bytesWritten < 0) {
throw new IOException(LinuxSystemShim.getErrorMessage());
} else {
offset += bytesWritten;
}
}
}
private void writeInt(int i) throws IOException {
byteBuffer.putInt(0, i);
writeBytes(4);
}
private void writeShort(short s) throws IOException {
byteBuffer.putShort(0, s);
writeBytes(2);
}
private void writeString(String s) throws IOException {
byte[] bytes = s.getBytes("UTF-8");
int offset = 0;
while (offset < bytes.length) {
int length = Math.min(byteBuffer.capacity(), bytes.length - offset);
byteBuffer.clear();
byteBuffer.put(bytes, offset, length);
writeBytes(length);
offset += length;
}
byteBuffer.put(0, (byte) 0);
writeBytes(1);
}
@Override
protected void createDevice() throws IOException {
devNode = DEVNODE_PREFIX + (++devNodeSuffix);
File devNodeFile = new File(devNode);
if (devNodeFile.exists()) {
devNodeFile.delete();
}
if (LinuxSystemShim.mkfifo(devNode, LinuxSystemShim.S_IRWXU) != 0) {
throw new IOException(LinuxSystemShim.getErrorMessage());
}
writeInt(1);
writeShort(bustype);
writeShort(product);
writeShort(vendor);
writeShort(version);
writeString(deviceName);
writeString(devNode);
writeString("Test Input Device");
BitSet evBits = capabilities.get("ev");
if (evBits != null) {
for (int i = 0; i < LinuxInputShim.EV_MAX; i++) {
if (evBits.get(i)) {
writeInt(i);
}
}
}
writeInt(-1);
BitSet keyBits = capabilities.get("key");
if (keyBits != null) {
for (int i = 0; i < LinuxInputShim.KEY_MAX; i++) {
if (keyBits.get(i)) {
writeInt(i);
}
}
}
writeInt(-1);
BitSet relBits = capabilities.get("rel");
if (relBits != null) {
for (int i = 0; i < LinuxInputShim.REL_MAX; i++) {
if (relBits.get(i)) {
writeInt(i);
}
}
}
writeInt(-1);
BitSet absBits = capabilities.get("abs");
if (absBits != null) {
for (int i = 0; i < LinuxInputShim.ABS_MAX; i++) {
if (absBits.get(i)) {
writeInt(i);
int[] caps = absCaps.get(i);
if (caps == null) {
caps = new int[6];
}
for (int val : caps) {
writeInt(val);
}
}
}
}
writeInt(-1);
for (String key : udevManifest.keySet()) {
writeString(key);
writeString(udevManifest.get(key));
}
writeString("");
devFD = openPipe(devNode);
}
@Override
protected void destroyDevice() throws IOException {
if (devFD != -1l) {
LinuxSystemShim.close(devFD);
devFD = -1;
}
new File(devNode).delete();
writeInt(2);
writeString(devNode);
}
private long openPipe(String path) throws IOException {
long timeOut = System.currentTimeMillis() + 10000l;
while (System.currentTimeMillis() < timeOut) {
long pipeFD = LinuxSystemShim.open(path,
LinuxSystemShim.O_WRONLY | LinuxSystemShim.O_NONBLOCK);
if (pipeFD < 0l) {
if (LinuxSystemShim.errno() == LinuxSystemShim.ENXIO) {
try {
Thread.sleep(100l);
} catch (InterruptedException e) { }
} else {
break;
}
} else {
return pipeFD;
}
}
throw new IOException(LinuxSystemShim.getErrorMessage());
}
@Override
protected void openConnection() throws IOException {
fd = openPipe(INPUT_PATH);
}
@Override
protected void closeConnection() {
if (fd >= 0l) {
LinuxSystemShim.close(fd);
fd = -1l;
}
}
@Override
public void write(ByteBuffer buffer) throws IOException {
int offset = 0;
while (offset < buffer.limit()) {
int bytesWritten = (int) LinuxSystemShim.write(devFD, buffer, offset, buffer.limit());
if (bytesWritten < 0) {
if (LinuxSystemShim.errno() == LinuxSystemShim.EAGAIN) {
try {
Thread.sleep(1);
} catch (InterruptedException e) {
e.printStackTrace();
}
} else {
throw new IOException(LinuxSystemShim.getErrorMessage());
}
} else {
offset += bytesWritten;
}
}
LinuxSystemShim.ioctl(devFD, LinuxSystemShim.I_FLUSH, LinuxSystemShim.FLUSHRW);
}
}
