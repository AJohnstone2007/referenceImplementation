package javafx.embed.swing;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.nio.IntBuffer;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.paint.Color;
public class SwingFXUtils {
private SwingFXUtils() {}
public static WritableImage toFXImage(BufferedImage bimg, WritableImage wimg) {
int bw = bimg.getWidth();
int bh = bimg.getHeight();
switch (bimg.getType()) {
case BufferedImage.TYPE_INT_ARGB:
case BufferedImage.TYPE_INT_ARGB_PRE:
break;
default:
BufferedImage converted =
new BufferedImage(bw, bh, BufferedImage.TYPE_INT_ARGB_PRE);
Graphics2D g2d = converted.createGraphics();
g2d.drawImage(bimg, 0, 0, null);
g2d.dispose();
bimg = converted;
break;
}
if (wimg != null) {
int iw = (int) wimg.getWidth();
int ih = (int) wimg.getHeight();
if (iw < bw || ih < bh) {
wimg = null;
} else if (bw < iw || bh < ih) {
int empty[] = new int[iw];
PixelWriter pw = wimg.getPixelWriter();
PixelFormat<IntBuffer> pf = PixelFormat.getIntArgbPreInstance();
if (bw < iw) {
pw.setPixels(bw, 0, iw-bw, bh, pf, empty, 0, 0);
}
if (bh < ih) {
pw.setPixels(0, bh, iw, ih-bh, pf, empty, 0, 0);
}
}
}
if (wimg == null) {
wimg = new WritableImage(bw, bh);
}
PixelWriter pw = wimg.getPixelWriter();
DataBufferInt db = (DataBufferInt)bimg.getRaster().getDataBuffer();
int data[] = db.getData();
int offset = bimg.getRaster().getDataBuffer().getOffset();
int scan = 0;
SampleModel sm = bimg.getRaster().getSampleModel();
if (sm instanceof SinglePixelPackedSampleModel) {
scan = ((SinglePixelPackedSampleModel)sm).getScanlineStride();
}
PixelFormat<IntBuffer> pf = (bimg.isAlphaPremultiplied() ?
PixelFormat.getIntArgbPreInstance() :
PixelFormat.getIntArgbInstance());
pw.setPixels(0, 0, bw, bh, pf, data, offset, scan);
return wimg;
}
static int
getBestBufferedImageType(PixelFormat<?> fxFormat, BufferedImage bimg,
boolean isOpaque)
{
if (bimg != null) {
int bimgType = bimg.getType();
if (bimgType == BufferedImage.TYPE_INT_ARGB ||
bimgType == BufferedImage.TYPE_INT_ARGB_PRE ||
(isOpaque &&
(bimgType == BufferedImage.TYPE_INT_BGR ||
bimgType == BufferedImage.TYPE_INT_RGB)))
{
return bimgType;
}
}
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
private static boolean checkFXImageOpaque(PixelReader pr, int iw, int ih) {
for (int x = 0; x < iw; x++) {
for (int y = 0; y < ih; y++) {
Color color = pr.getColor(x,y);
if (color.getOpacity() != 1.0) {
return false;
}
}
}
return true;
}
public static BufferedImage fromFXImage(Image img, BufferedImage bimg) {
PixelReader pr = img.getPixelReader();
if (pr == null) {
return null;
}
int iw = (int) img.getWidth();
int ih = (int) img.getHeight();
PixelFormat<?> fxFormat = pr.getPixelFormat();
boolean srcPixelsAreOpaque = false;
switch (fxFormat.getType()) {
case INT_ARGB_PRE:
case INT_ARGB:
case BYTE_BGRA_PRE:
case BYTE_BGRA:
if (bimg != null &&
(bimg.getType() == BufferedImage.TYPE_INT_BGR ||
bimg.getType() == BufferedImage.TYPE_INT_RGB)) {
srcPixelsAreOpaque = checkFXImageOpaque(pr, iw, ih);
}
break;
case BYTE_RGB:
srcPixelsAreOpaque = true;
break;
}
int prefBimgType = getBestBufferedImageType(pr.getPixelFormat(), bimg, srcPixelsAreOpaque);
if (bimg != null) {
int bw = bimg.getWidth();
int bh = bimg.getHeight();
if (bw < iw || bh < ih || bimg.getType() != prefBimgType) {
bimg = null;
} else if (iw < bw || ih < bh) {
Graphics2D g2d = bimg.createGraphics();
g2d.setComposite(AlphaComposite.Clear);
g2d.fillRect(0, 0, bw, bh);
g2d.dispose();
}
}
if (bimg == null) {
bimg = new BufferedImage(iw, ih, prefBimgType);
}
DataBufferInt db = (DataBufferInt)bimg.getRaster().getDataBuffer();
int data[] = db.getData();
int offset = bimg.getRaster().getDataBuffer().getOffset();
int scan = 0;
SampleModel sm = bimg.getRaster().getSampleModel();
if (sm instanceof SinglePixelPackedSampleModel) {
scan = ((SinglePixelPackedSampleModel)sm).getScanlineStride();
}
WritablePixelFormat<IntBuffer> pf = getAssociatedPixelFormat(bimg);
pr.getPixels(0, 0, iw, ih, pf, data, offset, scan);
return bimg;
}
}
