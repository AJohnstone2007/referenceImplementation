package javafx.scene.image;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.tk.ImageLoader;
import com.sun.javafx.tk.PlatformImage;
import com.sun.javafx.tk.Toolkit;
import javafx.beans.NamedArg;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.paint.Color;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Objects;
public class WritableImage extends Image {
static {
Toolkit.setWritableImageAccessor(new Toolkit.WritableImageAccessor() {
@Override public void loadTkImage(WritableImage wimg, Object loader) {
wimg.loadTkImage(loader);
}
@Override public Object getTkImageLoader(WritableImage wimg) {
return wimg.getTkImageLoader();
}
});
}
private ImageLoader tkImageLoader;
public WritableImage(@NamedArg("width") int width, @NamedArg("height") int height) {
super(width, height);
}
private PixelBuffer<? extends Buffer> pixelBuffer = null;
public WritableImage(@NamedArg("PixelBuffer") PixelBuffer<? extends Buffer> pixelBuffer) {
super(validatePixelBuffer(pixelBuffer));
pixelBuffer.addImage(this);
this.pixelBuffer = pixelBuffer;
}
public WritableImage(@NamedArg("reader") PixelReader reader, @NamedArg("width") int width, @NamedArg("height") int height) {
super(width, height);
getPixelWriter().setPixels(0, 0, width, height, reader, 0, 0);
}
public WritableImage(@NamedArg("reader") PixelReader reader,
@NamedArg("x") int x, @NamedArg("y") int y, @NamedArg("width") int width, @NamedArg("height") int height)
{
super(width, height);
getPixelWriter().setPixels(0, 0, width, height, reader, x, y);
}
@Override
boolean isAnimation() {
return true;
}
@Override
boolean pixelsReadable() {
return true;
}
void bufferDirty(Rectangle rect) {
getWritablePlatformImage().bufferDirty(rect);
pixelsDirty();
}
private static PixelBuffer<? extends Buffer> validatePixelBuffer(PixelBuffer<? extends Buffer> pixelBuffer) {
return (Objects.requireNonNull(pixelBuffer, "pixelBuffer must not be null."));
}
private PixelWriter writer;
public final PixelWriter getPixelWriter() {
if (pixelBuffer != null) {
throw new UnsupportedOperationException("PixelWriter is not supported with PixelBuffer");
}
if (getProgress() < 1.0 || isError()) {
return null;
}
if (writer == null) {
writer = new PixelWriter() {
ReadOnlyObjectProperty<PlatformImage> pimgprop =
acc_platformImageProperty();
@Override
public PixelFormat getPixelFormat() {
PlatformImage pimg = getWritablePlatformImage();
return pimg.getPlatformPixelFormat();
}
@Override
public void setArgb(int x, int y, int argb) {
getWritablePlatformImage().setArgb(x, y, argb);
pixelsDirty();
}
@Override
public void setColor(int x, int y, Color c) {
if (c == null) throw new NullPointerException("Color cannot be null");
int a = (int) Math.round(c.getOpacity() * 255);
int r = (int) Math.round(c.getRed() * 255);
int g = (int) Math.round(c.getGreen() * 255);
int b = (int) Math.round(c.getBlue() * 255);
setArgb(x, y, (a << 24) | (r << 16) | (g << 8) | b);
}
@Override
public <T extends Buffer>
void setPixels(int x, int y, int w, int h,
PixelFormat<T> pixelformat,
T buffer, int scanlineStride)
{
if (pixelformat == null) throw new NullPointerException("PixelFormat cannot be null");
if (buffer == null) throw new NullPointerException("Buffer cannot be null");
PlatformImage pimg = getWritablePlatformImage();
pimg.setPixels(x, y, w, h, pixelformat,
buffer, scanlineStride);
pixelsDirty();
}
@Override
public void setPixels(int x, int y, int w, int h,
PixelFormat<ByteBuffer> pixelformat,
byte buffer[], int offset, int scanlineStride)
{
if (pixelformat == null) throw new NullPointerException("PixelFormat cannot be null");
if (buffer == null) throw new NullPointerException("Buffer cannot be null");
PlatformImage pimg = getWritablePlatformImage();
pimg.setPixels(x, y, w, h, pixelformat,
buffer, offset, scanlineStride);
pixelsDirty();
}
@Override
public void setPixels(int x, int y, int w, int h,
PixelFormat<IntBuffer> pixelformat,
int buffer[], int offset, int scanlineStride)
{
if (pixelformat == null) throw new NullPointerException("PixelFormat cannot be null");
if (buffer == null) throw new NullPointerException("Buffer cannot be null");
PlatformImage pimg = getWritablePlatformImage();
pimg.setPixels(x, y, w, h, pixelformat,
buffer, offset, scanlineStride);
pixelsDirty();
}
@Override
public void setPixels(int writex, int writey, int w, int h,
PixelReader reader, int readx, int ready)
{
if (reader == null) throw new NullPointerException("Reader cannot be null");
PlatformImage pimg = getWritablePlatformImage();
pimg.setPixels(writex, writey, w, h, reader, readx, ready);
pixelsDirty();
}
};
}
return writer;
}
private void loadTkImage(Object loader) {
if (!(loader instanceof ImageLoader)) {
throw new IllegalArgumentException("Unrecognized image loader: "
+ loader);
}
ImageLoader tkLoader = (ImageLoader)loader;
if (tkLoader.getWidth() != (int)this.getWidth()
|| tkLoader.getHeight() != (int)this.getHeight())
{
throw new IllegalArgumentException("Size of loader does not match size of image");
}
super.setPlatformImage(tkLoader.getFrame(0));
this.tkImageLoader = tkLoader;
}
private Object getTkImageLoader() {
return tkImageLoader;
}
}
