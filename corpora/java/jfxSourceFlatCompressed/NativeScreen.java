package com.sun.glass.ui.monocle;
import java.nio.Buffer;
import java.nio.ByteBuffer;
public interface NativeScreen {
int getDepth();
int getNativeFormat();
int getWidth();
int getHeight();
default int getOffsetX() {
return 0;
}
default int getOffsetY() {
return 0;
}
int getDPI();
long getNativeHandle();
void shutdown();
void uploadPixels(Buffer b,
int x, int y, int width, int height, float alpha);
public void swapBuffers();
public ByteBuffer getScreenCapture();
public static final Object framebufferSwapLock = new Object();
public float getScale();
}
