package test.com.sun.javafx.pgstub;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.tk.PlatformImage;
public class StubWritablePlatformImage implements PlatformImage {
private final int w, h;
private final int[] data;
public StubWritablePlatformImage(int w, int h) {
this.w = w;
this.h = h;
this.data = new int[w * h];
}
@Override
public float getPixelScale() {
return 1;
}
@Override
public int getArgb(int x, int y) {
return data[w * y + x];
}
@Override
public void setArgb(int x, int y, int argb) {
data[w * y + x] = argb;
}
@Override
public PixelFormat getPlatformPixelFormat() {
return PixelFormat.getIntArgbInstance();
}
@Override
public boolean isWritable() {
return true;
}
@Override
public PlatformImage promoteToWritableImage() {
return this;
}
@Override
public <T extends Buffer> void getPixels(int x, int y, int w, int h, WritablePixelFormat<T> pixelformat, T pixels, int scanlineElems) {
}
@Override
public void getPixels(int x, int y, int w, int h, WritablePixelFormat<ByteBuffer> pixelformat, byte[] pixels, int offset, int scanlineBytes) {
}
@Override
public void getPixels(int x, int y, int w, int h, WritablePixelFormat<IntBuffer> pixelformat, int[] pixels, int offset, int scanlineInts) {
}
@Override
public <T extends Buffer> void setPixels(int x, int y, int w, int h, PixelFormat<T> pixelformat, T pixels, int scanlineBytes) {
}
@Override
public void setPixels(int x, int y, int w, int h, PixelFormat<ByteBuffer> pixelformat, byte[] pixels, int offset, int scanlineBytes) {
}
@Override
public void setPixels(int x, int y, int w, int h, PixelFormat<IntBuffer> pixelformat, int[] pixels, int offset, int scanlineInts) {
}
@Override
public void setPixels(int dstx, int dsty, int w, int h, PixelReader reader, int srcx, int srcy) {
}
@Override
public void bufferDirty(Rectangle rect) {
}
}
