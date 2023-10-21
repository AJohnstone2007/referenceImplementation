package com.sun.javafx.image;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
public interface IntToBytePixelConverter
extends PixelConverter<IntBuffer, ByteBuffer>
{
public void convert(int srcarr[], int srcoff, int srcscanints,
byte dstarr[], int dstoff, int dstscanbytes,
int w, int h);
public void convert(IntBuffer srcbuf, int srcoff, int srcscanints,
byte dstarr[], int dstoff, int dstscanbytes,
int w, int h);
public void convert(int srcarr[], int srcoff, int srcscanints,
ByteBuffer dstbuf, int dstoff, int dstscanbytes,
int w, int h);
}
