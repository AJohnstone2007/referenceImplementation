package com.sun.javafx.iio.common;
import java.nio.ByteBuffer;
public interface PushbroomScaler {
public ByteBuffer getDestination();
public boolean putSourceScanline(byte[] scanline, int off);
}
