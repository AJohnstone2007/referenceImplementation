package com.sun.javafx.image;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
public interface ByteToIntPixelConverter
extends PixelConverter<ByteBuffer, IntBuffer>
{
public void convert(byte srcarr[], int srcoff, int srcscanbytes,
int dstarr[], int dstoff, int dstscanints,
int w, int h);
public void convert(ByteBuffer srcbuf, int srcoff, int srcscanbytes,
int dstarr[], int dstoff, int dstscanints,
int w, int h);
public void convert(byte srcarr[], int srcoff, int srcscanbytes,
IntBuffer dstbuf, int dstoff, int dstscanints,
int w, int h);
}
