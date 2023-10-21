package com.sun.glass.ui.monocle;
import com.sun.glass.ui.Application;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.BitSet;
import java.util.Map;
class LinuxInputDevice implements Runnable, InputDevice {
private LinuxInputProcessor inputProcessor;
private ReadableByteChannel in;
private long fd = -1;
private File devNode;
private File sysPath;
private Map<String, BitSet> capabilities;
private Map<Integer, LinuxAbsoluteInputCapabilities> absCaps;
private Map<String, String> udevManifest;
private final ByteBuffer event;
private RunnableProcessor runnableProcessor;
private EventProcessor processor = new EventProcessor();
private final LinuxEventBuffer buffer;
private Map<String,String> uevent;
private static LinuxSystem system = LinuxSystem.getLinuxSystem();
LinuxInputDevice(
File devNode,
File sysPath,
Map<String, String> udevManifest) throws IOException {
this.buffer = new LinuxEventBuffer(LinuxArch.getBits());
this.event = ByteBuffer.allocateDirect(buffer.getEventSize());
this.devNode = devNode;
this.sysPath = sysPath;
this.udevManifest = udevManifest;
this.capabilities = SysFS.readCapabilities(sysPath);
fd = system.open(devNode.getPath(), LinuxSystem.O_RDONLY);
if (fd == -1) {
throw new IOException(system.getErrorMessage() + " on " + devNode);
}
this.absCaps = LinuxAbsoluteInputCapabilities.getCapabilities(
devNode, capabilities.get("abs"));
int EVIOCGRAB = system.IOW('E', 0x90, 4);
system.ioctl(fd, EVIOCGRAB, 1);
this.runnableProcessor = NativePlatformFactory.getNativePlatform()
.getRunnableProcessor();
this.uevent = SysFS.readUEvent(sysPath);
}
LinuxInputDevice(
Map<String, BitSet> capabilities,
Map<Integer, LinuxAbsoluteInputCapabilities> absCaps,
ReadableByteChannel in,
Map<String, String> udevManifest,
Map<String, String> uevent) {
this.buffer = new LinuxEventBuffer(32);
this.event = ByteBuffer.allocateDirect(buffer.getEventSize());
this.capabilities = capabilities;
this.absCaps = absCaps;
this.in = in;
this.udevManifest = udevManifest;
this.uevent = uevent;
this.runnableProcessor = NativePlatformFactory.getNativePlatform()
.getRunnableProcessor();
}
void setInputProcessor(LinuxInputProcessor inputProcessor) {
this.inputProcessor = inputProcessor;
}
private void readToEventBuffer() throws IOException {
if (in != null) {
in.read(event);
} else if (fd != -1) {
int position = event.position();
int bytesRead = (int) system.read(fd, event, position, event.limit());
if (bytesRead == -1) {
throw new IOException(system.getErrorMessage() + " on " + devNode);
} else {
event.position(position + bytesRead);
}
}
}
@Override
public void run() {
if (inputProcessor == null) {
System.err.println("Error: no input processor set on " + devNode);
return;
}
while (true) {
try {
readToEventBuffer();
if (event.position() == event.limit()) {
event.flip();
synchronized (buffer) {
if (buffer.put(event) && !processor.scheduled) {
runnableProcessor.invokeLater(processor);
processor.scheduled = true;
}
}
event.rewind();
}
} catch (IOException | InterruptedException e) {
return;
}
}
}
class EventProcessor implements Runnable {
boolean scheduled;
public void run() {
buffer.startIteration();
try {
inputProcessor.processEvents(LinuxInputDevice.this);
} catch (RuntimeException e) {
Application.reportException(e);
}
synchronized (buffer) {
if (buffer.hasNextEvent()) {
runnableProcessor.invokeLater(processor);
} else {
processor.scheduled = false;
}
buffer.compact();
}
}
}
LinuxEventBuffer getBuffer() {
return buffer;
}
boolean isQuiet() {
synchronized (buffer) {
return !processor.scheduled && !buffer.hasData();
}
}
public String toString() {
return devNode == null ? "Robot" : devNode.toString();
}
BitSet getCapability(String type) {
return capabilities.get(type);
}
LinuxAbsoluteInputCapabilities getAbsoluteInputCapabilities(int axis) {
return absCaps == null ? null : absCaps.get(axis);
}
String getProduct() {
return uevent.get("PRODUCT");
}
@Override
public boolean isTouch() {
return "1".equals(udevManifest.get("ID_INPUT_TOUCHSCREEN"))
|| "1".equals(udevManifest.get("ID_INPUT_TABLET"))
|| isTouchDeclaredAsMouse();
}
private boolean isTouchDeclaredAsMouse() {
if ("1".equals(udevManifest.get("ID_INPUT_MOUSE"))) {
BitSet rel = capabilities.get("rel");
if (rel == null || (!rel.get(LinuxInput.REL_X) && !rel.get(LinuxInput.REL_Y))) {
BitSet abs = capabilities.get("abs");
if (abs != null
&& (abs.get(LinuxInput.ABS_X) || abs.get(LinuxInput.ABS_MT_POSITION_X))
&& (abs.get(LinuxInput.ABS_Y) || abs.get(LinuxInput.ABS_MT_POSITION_Y))) {
return true;
}
}
}
return false;
}
@Override
public boolean isMultiTouch() {
if (isTouch()) {
BitSet abs = capabilities.get("abs");
if (abs == null) {
return false;
}
return abs.get(LinuxInput.ABS_MT_SLOT)
|| (abs.get(LinuxInput.ABS_MT_POSITION_X)
&& abs.get(LinuxInput.ABS_MT_POSITION_Y));
} else {
return false;
}
}
@Override
public boolean isRelative() {
return "1".equals(udevManifest.get("ID_INPUT_MOUSE"));
}
@Override
public boolean is5Way() {
BitSet key = capabilities.get("key");
if (key == null) {
return false;
}
for (int i = 0; i < LinuxKeyBits.KEYBITS_ARROWS.length; i++) {
if (!key.get(LinuxKeyBits.KEYBITS_ARROWS[i])) {
return false;
}
}
for (int i = 0; i < LinuxKeyBits.KEYBITS_SELECT.length; i++) {
if (key.get(LinuxKeyBits.KEYBITS_SELECT[i])) {
return true;
}
}
return false;
}
@Override
public boolean isFullKeyboard() {
BitSet key = capabilities.get("key");
if (key == null) {
return false;
}
for (int i = 0; i < LinuxKeyBits.KEYBITS_PC.length; i++) {
if (!key.get(LinuxKeyBits.KEYBITS_PC[i])) {
return false;
}
}
return is5Way();
}
}
