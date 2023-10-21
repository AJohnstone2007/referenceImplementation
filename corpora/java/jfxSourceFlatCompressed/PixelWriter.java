package javafx.scene.image;
import javafx.scene.paint.Color;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
public interface PixelWriter {
public PixelFormat getPixelFormat();
public void setArgb(int x, int y, int argb);
public void setColor(int x, int y, Color c);
public <T extends Buffer>
void setPixels(int x, int y, int w, int h,
PixelFormat<T> pixelformat,
T buffer, int scanlineStride);
public void setPixels(int x, int y, int w, int h,
PixelFormat<ByteBuffer> pixelformat,
byte buffer[], int offset, int scanlineStride);
public void setPixels(int x, int y, int w, int h,
PixelFormat<IntBuffer> pixelformat,
int buffer[], int offset, int scanlineStride);
public void setPixels(int dstx, int dsty, int w, int h,
PixelReader reader, int srcx, int srcy);
}
