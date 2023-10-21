package javafx.scene.image;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import javafx.scene.paint.Color;
public interface PixelReader {
public PixelFormat getPixelFormat();
public int getArgb(int x, int y);
public Color getColor(int x, int y);
public <T extends Buffer>
void getPixels(int x, int y, int w, int h,
WritablePixelFormat<T> pixelformat,
T buffer, int scanlineStride);
public void getPixels(int x, int y, int w, int h,
WritablePixelFormat<ByteBuffer> pixelformat,
byte buffer[], int offset, int scanlineStride);
public void getPixels(int x, int y, int w, int h,
WritablePixelFormat<IntBuffer> pixelformat,
int buffer[], int offset, int scanlineStride);
}
