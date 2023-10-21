package javafx.scene.image;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.tk.Toolkit;
import javafx.geometry.Rectangle2D;
import javafx.util.Callback;
import java.lang.ref.WeakReference;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
public class PixelBuffer<T extends Buffer> {
private final T buffer;
private final int width;
private final int height;
private final PixelFormat<T> pixelFormat;
private final List<WeakReference<WritableImage>> imageRefs;
public PixelBuffer(int width, int height, T buffer, PixelFormat<T> pixelFormat) {
Objects.requireNonNull(buffer, "buffer must not be null.");
Objects.requireNonNull(pixelFormat, "pixelFormat must not be null.");
if (width <= 0 || height <= 0) {
throw new IllegalArgumentException("PixelBuffer dimensions must be positive (w,h > 0)");
}
switch (pixelFormat.getType()) {
case BYTE_BGRA_PRE:
if (buffer.capacity() / width / 4 < height) {
throw new IllegalArgumentException("Insufficient memory allocated for ByteBuffer.");
}
if (!(buffer instanceof ByteBuffer)) {
throw new IllegalArgumentException("PixelFormat<ByteBuffer> requires a ByteBuffer.");
}
break;
case INT_ARGB_PRE:
if (buffer.capacity() / width < height) {
throw new IllegalArgumentException("Insufficient memory allocated for IntBuffer.");
}
if (!(buffer instanceof IntBuffer)) {
throw new IllegalArgumentException("PixelFormat<IntBuffer> requires an IntBuffer.");
}
break;
default:
throw new IllegalArgumentException("Unsupported PixelFormat: " + pixelFormat.getType());
}
this.buffer = buffer;
this.width = width;
this.height = height;
this.pixelFormat = pixelFormat;
this.imageRefs = new LinkedList<>();
}
public T getBuffer() {
return buffer;
}
public int getWidth() {
return width;
}
public int getHeight() {
return height;
}
public PixelFormat<T> getPixelFormat() {
return pixelFormat;
}
public void updateBuffer(Callback<PixelBuffer<T>, Rectangle2D> callback) {
Toolkit.getToolkit().checkFxUserThread();
Objects.requireNonNull(callback, "callback must not be null.");
Rectangle2D rect2D = callback.call(this);
if (rect2D != null) {
if (rect2D.getWidth() > 0 && rect2D.getHeight() > 0) {
int x1 = (int) Math.floor(rect2D.getMinX());
int y1 = (int) Math.floor(rect2D.getMinY());
int x2 = (int) Math.ceil(rect2D.getMaxX());
int y2 = (int) Math.ceil(rect2D.getMaxY());
bufferDirty(new Rectangle(x1, y1, x2 - x1, y2 - y1));
}
} else {
bufferDirty(null);
}
}
private void bufferDirty(Rectangle rect) {
Iterator<WeakReference<WritableImage>> iter = imageRefs.iterator();
while (iter.hasNext()) {
final WritableImage image = iter.next().get();
if (image != null) {
image.bufferDirty(rect);
} else {
iter.remove();
}
}
}
void addImage(WritableImage image) {
imageRefs.add(new WeakReference<>(image));
imageRefs.removeIf(imageRef -> (imageRef.get() == null));
}
}
