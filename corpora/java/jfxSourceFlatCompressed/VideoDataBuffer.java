package com.sun.media.jfxmedia.control;
import java.lang.annotation.Native;
import java.nio.ByteBuffer;
public interface VideoDataBuffer {
@Native public static final int PACKED_FORMAT_PLANE = 0;
@Native public static final int YCBCR_PLANE_LUMA = 0;
@Native public static final int YCBCR_PLANE_CR = 1;
@Native public static final int YCBCR_PLANE_CB = 2;
@Native public static final int YCBCR_PLANE_ALPHA = 3;
public ByteBuffer getBufferForPlane(int plane);
public double getTimestamp();
public int getWidth();
public int getHeight();
public int getEncodedWidth();
public int getEncodedHeight();
public VideoFormat getFormat();
public boolean hasAlpha();
public int getPlaneCount();
public int getStrideForPlane(int planeIndex);
public int[] getPlaneStrides();
public VideoDataBuffer convertToFormat(VideoFormat newFormat);
public void setDirty();
public void holdFrame();
public void releaseFrame();
}
