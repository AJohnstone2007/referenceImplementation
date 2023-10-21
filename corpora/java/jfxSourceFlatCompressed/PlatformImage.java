package com.sun.javafx.tk;
import com.sun.javafx.geom.Rectangle;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;
public interface PlatformImage {
public float getPixelScale();
public int getArgb(int x, int y);
public void setArgb(int x, int y, int argb);
public PixelFormat getPlatformPixelFormat();
public boolean isWritable();
public PlatformImage promoteToWritableImage();
public <T extends Buffer> void getPixels(int x, int y, int w, int h,
WritablePixelFormat<T> pixelformat,
T pixels, int scanlineElems);
public void getPixels(int x, int y, int w, int h,
WritablePixelFormat<ByteBuffer> pixelformat,
byte pixels[], int offset, int scanlineBytes);
public void getPixels(int x, int y, int w, int h,
WritablePixelFormat<IntBuffer> pixelformat,
int pixels[], int offset, int scanlineInts);
public <T extends Buffer> void setPixels(int x, int y, int w, int h,
PixelFormat<T> pixelformat,
T pixels, int scanlineBytes);
public void setPixels(int x, int y, int w, int h,
PixelFormat<ByteBuffer> pixelformat,
byte pixels[], int offset, int scanlineBytes);
public void setPixels(int x, int y, int w, int h,
PixelFormat<IntBuffer> pixelformat,
int pixels[], int offset, int scanlineInts);
public void setPixels(int dstx, int dsty, int w, int h,
PixelReader reader, int srcx, int srcy);
public void bufferDirty(Rectangle rect);
}
