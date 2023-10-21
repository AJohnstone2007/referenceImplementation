package com.sun.javafx.webkit.prism;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.Base64;
import java.util.Iterator;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritablePixelFormat;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import com.sun.javafx.webkit.UIClientImpl;
import com.sun.prism.Graphics;
import com.sun.prism.Image;
import com.sun.webkit.graphics.WCImage;
abstract class PrismImage extends WCImage {
abstract Image getImage();
abstract Graphics getGraphics();
abstract void draw(Graphics g,
int dstx1, int dsty1, int dstx2, int dsty2,
int srcx1, int srcy1, int srcx2, int srcy2);
abstract void dispose();
@Override
public Object getPlatformImage() {
return getImage();
}
@Override
public void deref() {
super.deref();
if (!hasRefs()) {
dispose();
}
}
@Override
protected final byte[] toData(String mimeType) {
final BufferedImage image = toBufferedImage(mimeType.equals("image/jpeg"));
if (image != null) {
Iterator<ImageWriter> it = ImageIO.getImageWritersByMIMEType(mimeType);
while (it.hasNext()) {
ByteArrayOutputStream output = new ByteArrayOutputStream();
ImageWriter writer = it.next();
try {
writer.setOutput(ImageIO.createImageOutputStream(output));
writer.write((BufferedImage) image);
}
catch (IOException exception) {
continue;
}
finally {
writer.dispose();
}
return output.toByteArray();
}
}
return null;
}
@Override
protected final String toDataURL(String mimeType) {
final byte[] data = toData(mimeType);
if (data != null) {
StringBuilder sb = new StringBuilder();
sb.append("data:").append(mimeType).append(";base64,");
sb.append(Base64.getMimeEncoder().encodeToString(data));
return sb.toString();
}
return null;
}
private static int
getBestBufferedImageType(PixelFormat<?> fxFormat)
{
switch (fxFormat.getType()) {
default:
case BYTE_BGRA_PRE:
case INT_ARGB_PRE:
return BufferedImage.TYPE_INT_ARGB_PRE;
case BYTE_BGRA:
case INT_ARGB:
return BufferedImage.TYPE_INT_ARGB;
case BYTE_RGB:
return BufferedImage.TYPE_INT_RGB;
case BYTE_INDEXED:
return (fxFormat.isPremultiplied()
? BufferedImage.TYPE_INT_ARGB_PRE
: BufferedImage.TYPE_INT_ARGB);
}
}
private static WritablePixelFormat<IntBuffer>
getAssociatedPixelFormat(BufferedImage bimg)
{
switch (bimg.getType()) {
case BufferedImage.TYPE_INT_RGB:
case BufferedImage.TYPE_INT_ARGB_PRE:
return PixelFormat.getIntArgbPreInstance();
case BufferedImage.TYPE_INT_ARGB:
return PixelFormat.getIntArgbInstance();
default:
throw new InternalError("Failed to validate BufferedImage type");
}
}
private static BufferedImage fromFXImage(Image img, boolean forceRGB) {
final int iw = (int) img.getWidth();
final int ih = (int) img.getHeight();
final int destImageType = forceRGB ? BufferedImage.TYPE_INT_RGB : getBestBufferedImageType(img.getPlatformPixelFormat());
final BufferedImage bimg = new BufferedImage(iw, ih, destImageType);
final DataBufferInt db = (DataBufferInt) bimg.getRaster().getDataBuffer();
final int data[] = db.getData();
final int offset = bimg.getRaster().getDataBuffer().getOffset();
int scan = 0;
final SampleModel sm = bimg.getRaster().getSampleModel();
if (sm instanceof SinglePixelPackedSampleModel) {
scan = ((SinglePixelPackedSampleModel)sm).getScanlineStride();
}
final WritablePixelFormat<IntBuffer> pf = getAssociatedPixelFormat(bimg);
img.getPixels(0, 0, iw, ih, pf, data, offset, scan);
return bimg;
}
private BufferedImage toBufferedImage(boolean forceRGB) {
try {
return fromFXImage(getImage(), forceRGB);
} catch (Exception ex) {
ex.printStackTrace(System.err);
}
return null;
}
@Override
public BufferedImage toBufferedImage() {
return toBufferedImage(false);
}
}
