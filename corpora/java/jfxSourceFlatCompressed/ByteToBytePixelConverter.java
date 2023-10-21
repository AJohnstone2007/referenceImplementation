package com.sun.javafx.image;
import java.nio.ByteBuffer;
public interface ByteToBytePixelConverter
extends PixelConverter<ByteBuffer, ByteBuffer>
{
public void convert(byte srcarr[], int srcoff, int srcscanbytes,
byte dstarr[], int dstoff, int dstscanbytes,
int w, int h);
public void convert(ByteBuffer srcbuf, int srcoff, int srcscanbytes,
byte dstarr[], int dstoff, int dstscanbytes,
int w, int h);
public void convert(byte srcarr[], int srcoff, int srcscanbytes,
ByteBuffer dstbuf, int dstoff, int dstscanbytes,
int w, int h);
}
