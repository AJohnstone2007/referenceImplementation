package test.com.sun.javafx.pgstub;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.tk.PlatformImage;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;
public final class StubPlatformImage implements PlatformImage {
private final StubImageLoader imageLoader;
private final int frame;
public StubPlatformImage(final StubImageLoader imageLoader,
final int frame) {
this.imageLoader = imageLoader;
this.frame = frame;
}
public int getFrame() {
return frame;
}
@Override
public float getPixelScale() {
return 1.0f;
}
public StubImageLoader getImageLoader() {
return imageLoader;
}
public StubPlatformImageInfo getImageInfo() {
return imageLoader.getImageInfo();
}
public Object getSource() {
return imageLoader.getSource();
}
@Override
public PixelFormat getPlatformPixelFormat() {
throw new UnsupportedOperationException("Not supported yet.");
}
@Override
public boolean isWritable() {
throw new UnsupportedOperationException("Not supported yet.");
}
@Override
public PlatformImage promoteToWritableImage() {
throw new UnsupportedOperationException("Not supported yet.");
}
@Override
public int getArgb(int x, int y) {
throw new UnsupportedOperationException("Not supported yet.");
}
@Override
public void setArgb(int x, int y, int argb) {
throw new UnsupportedOperationException("Not supported yet.");
}
@Override
public <T extends Buffer> void getPixels(int x, int y, int w, int h,
WritablePixelFormat<T> pixelformat,
T pixels, int scanlineBytes)
{
throw new UnsupportedOperationException("Not supported yet.");
}
@Override
public void getPixels(int x, int y, int w, int h,
WritablePixelFormat<ByteBuffer> pixelformat,
byte[] pixels, int offset, int scanlineBytes)
{
throw new UnsupportedOperationException("Not supported yet.");
}
@Override
public void getPixels(int x, int y, int w, int h,
WritablePixelFormat<IntBuffer> pixelformat,
int[] pixels, int offset, int scanlineInts)
{
throw new UnsupportedOperationException("Not supported yet.");
}
@Override
public <T extends Buffer> void setPixels(int x, int y, int w, int h,
PixelFormat<T> pixelformat,
T pixels, int scanlineBytes)
{
throw new UnsupportedOperationException("Not supported yet.");
}
@Override
public void setPixels(int x, int y, int w, int h,
PixelFormat<ByteBuffer> pixelformat,
byte[] pixels, int offset, int scanlineBytes)
{
throw new UnsupportedOperationException("Not supported yet.");
}
@Override
public void setPixels(int x, int y, int w, int h,
PixelFormat<IntBuffer> pixelformat,
int[] pixels, int offset, int scanlineInts)
{
throw new UnsupportedOperationException("Not supported yet.");
}
@Override
public void setPixels(int dstx, int dsty, int w, int h,
PixelReader reader, int srcx, int srcy)
{
throw new UnsupportedOperationException("Not supported yet.");
}
@Override
public String toString() {
final StringBuilder sb = new StringBuilder();
sb.append("StubPlatformImage[source = ")
.append(imageLoader.getSource())
.append(", width = ").append(imageLoader.getWidth())
.append(", height = ").append(imageLoader.getHeight())
.append(", frame = ").append(frame)
.append("]");
return sb.toString();
}
@Override
public void bufferDirty(Rectangle rect) {
throw new UnsupportedOperationException("Not supported yet.");
}
}
