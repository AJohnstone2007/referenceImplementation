package com.sun.glass.ui.monocle;
import com.sun.glass.ui.Pixels;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.util.Logging;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.MessageFormat;
class EPDScreen implements NativeScreen {
private static final String FB_PATH_KEY = "monocle.screen.fb";
private static final String FB_PATH_DEFAULT = "/dev/fb0";
private static final int DPI = 167;
private static final float SCALE = 1.0f;
private final PlatformLogger logger = Logging.getJavaFXLogger();
private final String fbPath;
private final EPDFrameBuffer fbDevice;
private final ByteBuffer fbMapping;
private final FileChannel fbChannel;
private final Framebuffer pixels;
private final int width;
private final int height;
private final int bitDepth;
private boolean isShutdown;
EPDScreen() {
@SuppressWarnings("removal")
String tmp = AccessController.doPrivileged((PrivilegedAction<String>) ()
-> System.getProperty(FB_PATH_KEY, FB_PATH_DEFAULT));
fbPath = tmp;
try {
fbDevice = new EPDFrameBuffer(fbPath);
fbDevice.init();
width = fbDevice.getWidth();
height = fbDevice.getHeight();
bitDepth = fbDevice.getBitDepth();
logger.fine("Native screen geometry: {0} px x {1} px x {2} bpp",
width, height, bitDepth);
ByteBuffer mapping = null;
if (bitDepth == Integer.SIZE) {
mapping = fbDevice.getMappedBuffer();
}
if (mapping != null) {
fbMapping = mapping;
fbChannel = null;
} else {
Path path = FileSystems.getDefault().getPath(fbPath);
fbChannel = FileChannel.open(path, StandardOpenOption.WRITE);
fbMapping = null;
}
} catch (IOException e) {
String msg = MessageFormat.format("Failed opening frame buffer: {0}", fbPath);
logger.severe(msg, e);
throw new IllegalStateException(msg, e);
}
ByteBuffer buffer = fbMapping != null ? fbMapping : fbDevice.getOffscreenBuffer();
buffer.order(ByteOrder.nativeOrder());
pixels = new FramebufferY8(buffer, width, height, bitDepth, true);
clearScreen();
}
private void close() {
try {
if (fbChannel != null) {
fbChannel.close();
}
} catch (IOException e) {
logger.severe("Failed closing frame buffer channel", e);
} finally {
if (fbMapping != null) {
fbDevice.releaseMappedBuffer(fbMapping);
}
fbDevice.close();
}
}
private void writeBuffer() {
if (fbChannel != null) {
try {
fbChannel.position(fbDevice.getByteOffset());
pixels.write(fbChannel);
} catch (IOException e) {
logger.severe("Failed writing to frame buffer channel", e);
}
}
}
private void clearScreen() {
pixels.clearBufferContents();
writeBuffer();
fbDevice.clear();
}
@Override
public int getDepth() {
return bitDepth;
}
@Override
public int getNativeFormat() {
return Pixels.Format.BYTE_BGRA_PRE;
}
@Override
public int getWidth() {
return width;
}
@Override
public int getHeight() {
return height;
}
@Override
public int getDPI() {
return DPI;
}
@Override
public long getNativeHandle() {
return fbDevice.getNativeHandle();
}
@Override
public synchronized void shutdown() {
close();
isShutdown = true;
}
@Override
public synchronized void uploadPixels(Buffer b, int x, int y, int width, int height, float alpha) {
pixels.composePixels(b, x, y, width, height, alpha);
}
@Override
public synchronized void swapBuffers() {
if (!isShutdown && pixels.hasReceivedData()) {
writeBuffer();
fbDevice.sync();
pixels.reset();
}
}
@Override
public synchronized ByteBuffer getScreenCapture() {
return pixels.getBuffer().asReadOnlyBuffer();
}
@Override
public float getScale() {
return SCALE;
}
@Override
public String toString() {
return MessageFormat.format("{0}[width={1} height={2} depth={3} DPI={4} scale={5,number,0.0#}]",
getClass().getName(), getWidth(), getHeight(), getDepth(), getDPI(), getScale());
}
}
