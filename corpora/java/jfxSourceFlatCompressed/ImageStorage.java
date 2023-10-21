package com.sun.javafx.iio;
import com.sun.javafx.PlatformUtil;
import com.sun.javafx.iio.ImageFormatDescription.Signature;
import com.sun.javafx.iio.bmp.BMPImageLoaderFactory;
import com.sun.javafx.iio.common.ImageTools;
import com.sun.javafx.iio.gif.GIFImageLoaderFactory;
import com.sun.javafx.iio.ios.IosImageLoaderFactory;
import com.sun.javafx.iio.jpeg.JPEGImageLoaderFactory;
import com.sun.javafx.iio.png.PNGImageLoaderFactory;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.util.DataURI;
import com.sun.javafx.util.Logging;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
public class ImageStorage {
public static enum ImageType {
GRAY,
GRAY_ALPHA,
GRAY_ALPHA_PRE,
PALETTE,
PALETTE_ALPHA,
PALETTE_ALPHA_PRE,
PALETTE_TRANS,
RGB,
RGBA,
RGBA_PRE
};
private final HashMap<Signature, ImageLoaderFactory> loaderFactoriesBySignature;
private final HashMap<String, ImageLoaderFactory> loaderFactoriesByMimeSubtype;
private final ImageLoaderFactory[] loaderFactories;
private int maxSignatureLength;
private static final boolean isIOS = PlatformUtil.isIOS();
private static class InstanceHolder {
static final ImageStorage INSTANCE = new ImageStorage();
}
public static ImageStorage getInstance() {
return InstanceHolder.INSTANCE;
}
public ImageStorage() {
if (isIOS) {
loaderFactories = new ImageLoaderFactory[]{
IosImageLoaderFactory.getInstance()
};
} else {
loaderFactories = new ImageLoaderFactory[]{
GIFImageLoaderFactory.getInstance(),
JPEGImageLoaderFactory.getInstance(),
PNGImageLoaderFactory.getInstance(),
BMPImageLoaderFactory.getInstance()
};
}
loaderFactoriesBySignature = new HashMap<>(loaderFactories.length);
loaderFactoriesByMimeSubtype = new HashMap<>(loaderFactories.length);
for (int i = 0; i < loaderFactories.length; i++) {
addImageLoaderFactory(loaderFactories[i]);
}
}
public ImageFormatDescription[] getSupportedDescriptions() {
ImageFormatDescription[] formats = new ImageFormatDescription[loaderFactories.length];
for (int i = 0; i < loaderFactories.length; i++) {
formats[i] = loaderFactories[i].getFormatDescription();
}
return (formats);
}
public int getNumBands(ImageType type) {
int numBands = -1;
switch (type) {
case GRAY:
case PALETTE:
case PALETTE_ALPHA:
case PALETTE_ALPHA_PRE:
case PALETTE_TRANS:
numBands = 1;
break;
case GRAY_ALPHA:
case GRAY_ALPHA_PRE:
numBands = 2;
break;
case RGB:
numBands = 3;
break;
case RGBA:
case RGBA_PRE:
numBands = 4;
break;
default:
throw new IllegalArgumentException("Unknown ImageType " + type);
}
return numBands;
}
public void addImageLoaderFactory(ImageLoaderFactory factory) {
ImageFormatDescription desc = factory.getFormatDescription();
for (final Signature signature: desc.getSignatures()) {
loaderFactoriesBySignature.put(signature, factory);
}
for (String subtype : desc.getMIMESubtypes()) {
loaderFactoriesByMimeSubtype.put(subtype.toLowerCase(), factory);
}
synchronized (ImageStorage.class) {
maxSignatureLength = -1;
}
}
public ImageFrame[] loadAll(InputStream input, ImageLoadListener listener,
double width, double height, boolean preserveAspectRatio,
float pixelScale, boolean smooth) throws ImageStorageException {
ImageLoader loader = null;
ImageFrame[] images = null;
try {
if (isIOS) {
loader = IosImageLoaderFactory.getInstance().createImageLoader(input);
} else {
loader = getLoaderBySignature(input, listener);
}
if (loader != null) {
images = loadAll(loader, width, height, preserveAspectRatio, pixelScale, smooth);
} else {
throw new ImageStorageException("No loader for image data");
}
} catch (ImageStorageException ise) {
throw ise;
} catch (IOException e) {
throw new ImageStorageException(e.getMessage(), e);
} finally {
if (loader != null) {
loader.dispose();
}
}
return images;
}
public ImageFrame[] loadAll(String input, ImageLoadListener listener,
double width, double height, boolean preserveAspectRatio,
float devPixelScale, boolean smooth) throws ImageStorageException {
if (input == null || input.isEmpty()) {
throw new ImageStorageException("URL can't be null or empty");
}
ImageFrame[] images = null;
InputStream theStream = null;
ImageLoader loader = null;
try {
float imgPixelScale = 1.0f;
try {
DataURI dataUri = DataURI.tryParse(input);
if (dataUri != null) {
if (!"image".equalsIgnoreCase(dataUri.getMimeType())) {
throw new IllegalArgumentException("Unexpected MIME type: " + dataUri.getMimeType());
}
var factory = loaderFactoriesByMimeSubtype.get(dataUri.getMimeSubtype().toLowerCase());
if (factory == null) {
throw new IllegalArgumentException(
"Unsupported MIME subtype: image/" + dataUri.getMimeSubtype());
}
theStream = new ByteArrayInputStream(dataUri.getData());
ImageLoader loaderBySignature = getLoaderBySignature(theStream, listener);
if (loaderBySignature != null) {
boolean imageTypeMismatch = !factory.getFormatDescription().getFormatName().equals(
loaderBySignature.getFormatDescription().getFormatName());
if (imageTypeMismatch) {
var logger = Logging.getJavaFXLogger();
if (logger.isLoggable(PlatformLogger.Level.WARNING)) {
logger.warning(String.format(
"Image format '%s' does not match MIME type '%s/%s' in URI '%s'",
loaderBySignature.getFormatDescription().getFormatName(),
dataUri.getMimeType(), dataUri.getMimeSubtype(), dataUri));
}
}
loader = loaderBySignature;
} else {
theStream.close();
theStream = new ByteArrayInputStream(dataUri.getData());
loader = factory.createImageLoader(theStream);
}
} else {
if (devPixelScale >= 1.5f) {
try {
String name2x = ImageTools.getScaledImageName(input);
theStream = ImageTools.createInputStream(name2x);
imgPixelScale = 2.0f;
} catch (IOException ignored) {
}
}
if (theStream == null) {
theStream = ImageTools.createInputStream(input);
}
if (isIOS) {
loader = IosImageLoaderFactory.getInstance().createImageLoader(theStream);
} else {
loader = getLoaderBySignature(theStream, listener);
}
}
} catch (Exception e) {
throw new ImageStorageException(e.getMessage(), e);
}
if (loader != null) {
images = loadAll(loader, width, height, preserveAspectRatio, imgPixelScale, smooth);
} else {
throw new ImageStorageException("No loader for image data");
}
} finally {
if (loader != null) {
loader.dispose();
}
try {
if (theStream != null) {
theStream.close();
}
} catch (IOException ignored) {
}
}
return images;
}
private synchronized int getMaxSignatureLength() {
if (maxSignatureLength < 0) {
maxSignatureLength = 0;
for (final Signature signature:
loaderFactoriesBySignature.keySet()) {
final int signatureLength = signature.getLength();
if (maxSignatureLength < signatureLength) {
maxSignatureLength = signatureLength;
}
}
}
return maxSignatureLength;
}
private ImageFrame[] loadAll(ImageLoader loader,
double width, double height, boolean preserveAspectRatio,
float pixelScale, boolean smooth) throws ImageStorageException {
ImageFrame[] images = null;
ArrayList<ImageFrame> list = new ArrayList<ImageFrame>();
int imageIndex = 0;
ImageFrame image = null;
int imgw = (int) Math.round(width * pixelScale);
int imgh = (int) Math.round(height * pixelScale);
do {
try {
image = loader.load(imageIndex++, imgw, imgh, preserveAspectRatio, smooth);
} catch (Exception e) {
if (imageIndex > 1) {
break;
} else {
throw new ImageStorageException(e.getMessage(), e);
}
}
if (image != null) {
image.setPixelScale(pixelScale);
list.add(image);
} else {
break;
}
} while (true);
int numImages = list.size();
if (numImages > 0) {
images = new ImageFrame[numImages];
list.toArray(images);
}
return images;
}
private ImageLoader getLoaderBySignature(InputStream stream, ImageLoadListener listener) throws IOException {
byte[] header = new byte[getMaxSignatureLength()];
try {
ImageTools.readFully(stream, header);
} catch (EOFException ignored) {
return null;
}
for (final Entry<Signature, ImageLoaderFactory> factoryRegistration:
loaderFactoriesBySignature.entrySet()) {
if (factoryRegistration.getKey().matches(header)) {
InputStream headerStream = new ByteArrayInputStream(header);
InputStream seqStream = new SequenceInputStream(headerStream, stream);
ImageLoader loader = factoryRegistration.getValue().createImageLoader(seqStream);
if (listener != null) {
loader.addListener(listener);
}
return loader;
}
}
return null;
}
}
