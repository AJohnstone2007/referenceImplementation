package com.sun.javafx.iio.ios;
import com.sun.glass.utils.NativeLibLoader;
import com.sun.javafx.iio.common.*;
import com.sun.javafx.iio.ImageFrame;
import com.sun.javafx.iio.ImageMetadata;
import com.sun.javafx.iio.ImageStorage.ImageType;
import com.sun.javafx.iio.common.ImageLoaderImpl;
import com.sun.javafx.iio.common.ImageTools;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.net.URL;
import java.net.MalformedURLException;
public class IosImageLoader extends ImageLoaderImpl {
public static final int GRAY = 0;
public static final int GRAY_ALPHA = 1;
public static final int GRAY_ALPHA_PRE = 2;
public static final int PALETTE = 3;
public static final int PALETTE_ALPHA = 4;
public static final int PALETTE_ALPHA_PRE = 5;
public static final int PALETTE_TRANS = 6;
public static final int RGB = 7;
public static final int RGBA = 8;
public static final int RGBA_PRE = 9;
private static final Map<Integer, ImageType> COLOR_SPACE_MAPPING;
private long structPointer;
private int inWidth;
private int inHeight;
private int nImages;
private boolean isDisposed = false;
private int delayTime;
private int loopCount;
private static native void initNativeLoading();
private native long loadImage(final InputStream stream, boolean reportProgress) throws IOException;
private native long loadImageFromURL(final String url, boolean reportProgress) throws IOException;
private native void resizeImage(long structPointer, int width, int height);
private native byte[] getImageBuffer(long structPointer, int imageIndex);
private native int getNumberOfComponents(long structPointer);
private native int getColorSpaceCode(long structPointer);
private native int getDelayTime(long structPointer);
private static native void disposeLoader(long structPointer);
static {
@SuppressWarnings("removal")
var dummy = AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
NativeLibLoader.loadLibrary("nativeiio");
return null;
});
COLOR_SPACE_MAPPING = Map.of(
GRAY, ImageType.GRAY,
GRAY_ALPHA, ImageType.GRAY_ALPHA,
GRAY_ALPHA_PRE, ImageType.GRAY_ALPHA_PRE,
PALETTE, ImageType.PALETTE,
PALETTE_ALPHA, ImageType.PALETTE_ALPHA,
PALETTE_ALPHA_PRE, ImageType.PALETTE_ALPHA_PRE,
PALETTE_TRANS, ImageType.PALETTE_TRANS,
RGB, ImageType.RGB,
RGBA, ImageType.RGBA,
RGBA_PRE, ImageType.RGBA_PRE);
initNativeLoading();
}
private void setInputParameters(
int width,
int height,
int imageCount,
int loopCount) {
inWidth = width;
inHeight = height;
nImages = imageCount;
this.loopCount = loopCount;
}
private void updateProgress(float progressPercentage) {
updateImageProgress(progressPercentage);
}
private boolean shouldReportProgress() {
return listeners != null && !listeners.isEmpty();
}
private void checkNativePointer() throws IOException {
if (structPointer == 0L) {
throw new IOException("Unable to initialize image native loader!");
}
}
private void retrieveDelayTime() {
if (nImages > 1) {
delayTime = getDelayTime(structPointer);
}
}
public IosImageLoader(final String urlString, final ImageDescriptor desc) throws IOException {
super(desc);
try {
final URL url = new URL(urlString);
}
catch (MalformedURLException mue) {
throw new IllegalArgumentException("Image loader: Malformed URL!");
}
try {
structPointer = loadImageFromURL(urlString, shouldReportProgress());
} catch (IOException e) {
dispose();
throw e;
}
checkNativePointer();
retrieveDelayTime();
}
public IosImageLoader(final InputStream inputStream, final ImageDescriptor desc) throws IOException {
super(desc);
if (inputStream == null) {
throw new IllegalArgumentException("Image loader: input stream == null");
}
try {
structPointer = loadImage(inputStream, shouldReportProgress());
} catch (IOException e) {
dispose();
throw e;
}
checkNativePointer();
retrieveDelayTime();
}
public synchronized void dispose() {
if (!isDisposed && structPointer != 0L) {
isDisposed = true;
IosImageLoader.disposeLoader(structPointer);
structPointer = 0L;
}
}
public ImageFrame load(int imageIndex, int width, int height, boolean preserveAspectRatio, boolean smooth)
throws IOException {
if (imageIndex >= nImages) {
dispose();
return null;
}
int[] widthHeight = ImageTools.computeDimensions(inWidth, inHeight, width, height, preserveAspectRatio);
width = widthHeight[0];
height = widthHeight[1];
final ImageMetadata md = new ImageMetadata(
null,
true,
null,
null,
null,
delayTime == 0 ? null : delayTime,
nImages > 1 ? loopCount : null,
width,
height,
null,
null,
null);
updateImageMetadata(md);
resizeImage(structPointer, width, height);
final int nComponents = getNumberOfComponents(structPointer);
final int colorSpaceCode = getColorSpaceCode(structPointer);
final ImageType imageType = COLOR_SPACE_MAPPING.get(colorSpaceCode);
final byte[] pixels = getImageBuffer(structPointer, imageIndex);
return new ImageFrame(imageType,
ByteBuffer.wrap(pixels),
width,
height,
width * nComponents,
null,
md);
}
}
