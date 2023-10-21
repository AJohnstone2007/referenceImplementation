package com.sun.glass.ui.monocle;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
class Udev implements Runnable {
private static Udev instance;
private long fd;
private ByteBuffer buffer;
private Thread thread;
private UdevListener[] listeners;
static synchronized Udev getInstance() {
if (instance == null) {
try {
instance = new Udev();
} catch (IOException e) {
System.err.println("Udev: failed to open connection");
e.printStackTrace();
}
}
return instance;
}
private Udev() throws IOException {
fd = _open();
buffer = ByteBuffer.allocateDirect(4096);
buffer.order(ByteOrder.nativeOrder());
thread = new Thread(this, "udev monitor");
thread.setDaemon(true);
thread.start();
}
synchronized void addListener(UdevListener listener) {
if (listeners == null) {
listeners = new UdevListener[] { listener };
} else {
listeners = Arrays.copyOf(listeners, listeners.length + 1);
listeners[listeners.length - 1] = listener;
}
}
private native long _open() throws IOException;
private native int _readEvent(long fd, ByteBuffer buffer) throws
IOException;
private native void _close(long fd);
private native int _getPropertiesOffset(ByteBuffer buffer);
private native int _getPropertiesLength(ByteBuffer buffer);
@Override
public void run() {
try {
RunnableProcessor runnableProcessor =
NativePlatformFactory.getNativePlatform().getRunnableProcessor();
while (true) {
Map<String, String> event = readEvent();
runnableProcessor.invokeLater(new Runnable() {
public void run() {
String action = event.get("ACTION");
if (action != null) {
UdevListener[] uls;
synchronized (this) {
uls = listeners;
}
if (uls != null) {
for (int i = 0; i < uls.length; i++) {
try {
uls[i].udevEvent(action, event);
} catch (RuntimeException e) {
System.err.println(
"Exception in udev listener:");
e.printStackTrace();
} catch (Error e) {
System.err.println(
"Error in udev listener, " +
"closing udev");
e.printStackTrace();
close();
return;
}
}
}
}
}
});
}
} catch (IOException e) {
if (!thread.isInterrupted()) {
System.err.println("Exception in udev thread:");
e.printStackTrace();
close();
}
}
}
private Map<String, String> readEvent() throws IOException {
Map<String, String> map = new HashMap<>();
ByteBuffer b;
synchronized (this) {
b = buffer;
if (b == null) {
return map;
}
}
int length = _readEvent(fd, b);
synchronized (this) {
if (buffer == null) {
return map;
}
int propertiesOffset = _getPropertiesOffset(buffer);
int propertiesLength = _getPropertiesLength(buffer);
int propertiesEnd = propertiesOffset + propertiesLength;
if (length < propertiesEnd) {
throw new IOException("Mismatched property segment length");
}
buffer.position(propertiesOffset);
StringBuffer key = new StringBuffer();
StringBuffer value = new StringBuffer();
nextKey: while (buffer.position() < propertiesEnd) {
key.setLength(0);
value.setLength(0);
boolean readKey = false;
while (buffer.position() < length && !readKey) {
char ch = (char) buffer.get();
switch (ch) {
case '\000':
map.put(key.toString(), "");
continue nextKey;
case '=':
readKey = true;
break;
default:
key.append(ch);
}
}
while (buffer.position() < propertiesEnd) {
char ch = (char) buffer.get();
switch (ch) {
case '\000':
map.put(key.toString(), value.toString());
continue nextKey;
default:
value.append(ch);
}
}
}
buffer.clear();
}
return map;
}
synchronized void close() {
thread.interrupt();
_close(fd);
fd = 0l;
buffer = null;
thread = null;
}
}
