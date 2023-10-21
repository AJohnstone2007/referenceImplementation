package com.sun.javafx.iio.jpeg;
import com.sun.javafx.iio.ImageFrame;
import com.sun.javafx.iio.ImageMetadata;
import com.sun.javafx.iio.ImageStorage.ImageType;
import com.sun.glass.utils.NativeLibLoader;
import com.sun.javafx.iio.common.ImageLoaderImpl;
import com.sun.javafx.iio.common.ImageTools;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
public class JPEGImageLoader extends ImageLoaderImpl {
public static final int JCS_UNKNOWN = 0;
public static final int JCS_GRAYSCALE = 1;
public static final int JCS_RGB = 2;
public static final int JCS_YCbCr = 3;
public static final int JCS_CMYK = 4;
public static final int JCS_YCC = 5;
public static final int JCS_RGBA = 6;
public static final int JCS_YCbCrA = 7;
public static final int JCS_YCCA = 10;
public static final int JCS_YCCK = 11;
private long structPointer = 0L;
private int inWidth;
private int inHeight;
private int inColorSpaceCode;
private int outColorSpaceCode;
private byte[] iccData;
private int outWidth;
private int outHeight;
private ImageType outImageType;
private boolean isDisposed = false;
private Lock accessLock = new Lock();
private static native void initJPEGMethodIDs(Class inputStreamClass);
private static native void disposeNative(long structPointer);
private native long initDecompressor(InputStream stream) throws IOException;
private native int startDecompression(long structPointer,
int outColorSpaceCode, int scaleNum, int scaleDenom);
private native boolean decompressIndirect(long structPointer, boolean reportProgress, byte[] array) throws IOException;
static {
@SuppressWarnings("removal")
var dummy = AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
NativeLibLoader.loadLibrary("javafx_iio");
return null;
});
initJPEGMethodIDs(InputStream.class);
}
private void setInputAttributes(int width,
int height,
int colorSpaceCode,
int outColorSpaceCode,
int numComponents,
byte[] iccData) {
this.inWidth = width;
this.inHeight = height;
this.inColorSpaceCode = colorSpaceCode;
this.outColorSpaceCode = outColorSpaceCode;
this.iccData = iccData;
switch (outColorSpaceCode) {
case JCS_GRAYSCALE:
this.outImageType = ImageType.GRAY;
break;
case JCS_YCbCr:
case JCS_YCC:
case JCS_RGB:
this.outImageType = ImageType.RGB;
break;
case JCS_CMYK:
case JCS_YCbCrA:
case JCS_YCCA:
case JCS_YCCK:
case JCS_RGBA:
this.outImageType = ImageType.RGBA_PRE;
break;
case JCS_UNKNOWN:
switch (numComponents) {
case 1:
this.outImageType = ImageType.GRAY;
break;
case 3:
this.outImageType = ImageType.RGB;
break;
case 4:
this.outImageType = ImageType.RGBA_PRE;
break;
default:
assert false;
}
break;
default:
assert false;
break;
}
}
private void setOutputAttributes(int width, int height) {
this.outWidth = width;
this.outHeight = height;
}
private void updateImageProgress(int outLinesDecoded) {
updateImageProgress(100.0F * outLinesDecoded / outHeight);
}
JPEGImageLoader(InputStream input) throws IOException {
super(JPEGDescriptor.getInstance());
if (input == null) {
throw new IllegalArgumentException("input == null!");
}
try {
this.structPointer = initDecompressor(input);
} catch (IOException e) {
dispose();
throw e;
}
if (this.structPointer == 0L) {
throw new IOException("Unable to initialize JPEG decompressor");
}
}
public synchronized void dispose() {
if(!accessLock.isLocked() && !isDisposed && structPointer != 0L) {
isDisposed = true;
disposeNative(structPointer);
structPointer = 0L;
}
}
public ImageFrame load(int imageIndex, int width, int height, boolean preserveAspectRatio, boolean smooth) throws IOException {
if (imageIndex != 0) {
return null;
}
accessLock.lock();
int[] widthHeight = ImageTools.computeDimensions(inWidth, inHeight, width, height, preserveAspectRatio);
width = widthHeight[0];
height = widthHeight[1];
ImageMetadata md = new ImageMetadata(null, true,
null, null, null, null, null,
width, height, null, null, null);
updateImageMetadata(md);
ByteBuffer buffer = null;
int outNumComponents;
try {
outNumComponents = startDecompression(structPointer,
outColorSpaceCode, width, height);
if (outWidth < 0 || outHeight < 0 || outNumComponents < 0) {
throw new IOException("negative dimension.");
}
if (outWidth > (Integer.MAX_VALUE / outNumComponents)) {
throw new IOException("bad width.");
}
int scanlineStride = outWidth * outNumComponents;
if (scanlineStride > (Integer.MAX_VALUE / outHeight)) {
throw new IOException("bad height.");
}
byte[] array = new byte[scanlineStride*outHeight];
buffer = ByteBuffer.wrap(array);
decompressIndirect(structPointer, listeners != null && !listeners.isEmpty(), buffer.array());
} catch (IOException e) {
throw e;
} catch (Throwable t) {
throw new IOException(t);
} finally {
accessLock.unlock();
dispose();
}
if (buffer == null) {
throw new IOException("Error decompressing JPEG stream!");
}
if (outWidth != width || outHeight != height) {
buffer = ImageTools.scaleImage(buffer,
outWidth, outHeight, outNumComponents, width, height, smooth);
}
return new ImageFrame(outImageType, buffer,
width, height, width * outNumComponents, null, md);
}
private static class Lock {
private boolean locked;
public Lock() {
locked = false;
}
public synchronized boolean isLocked() {
return locked;
}
public synchronized void lock() {
if (locked) {
throw new IllegalStateException("Recursive loading is not allowed.");
}
locked = true;
}
public synchronized void unlock() {
if (!locked) {
throw new IllegalStateException("Invalid loader state.");
}
locked = false;
}
}
}
