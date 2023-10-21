package com.sun.prism;
import java.nio.ByteBuffer;
public interface MediaFrame {
public ByteBuffer getBufferForPlane(int plane);
public PixelFormat getPixelFormat();
public int getWidth();
public int getHeight();
public int getEncodedWidth();
public int getEncodedHeight();
public int planeCount();
public int[] planeStrides();
public int strideForPlane(int planeIndex);
public MediaFrame convertToFormat(PixelFormat fmt);
public void holdFrame();
public void releaseFrame();
}
