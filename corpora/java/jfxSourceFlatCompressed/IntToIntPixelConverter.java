package com.sun.javafx.image;
import java.nio.IntBuffer;
public interface IntToIntPixelConverter
extends PixelConverter<IntBuffer, IntBuffer>
{
public void convert(int srcarr[], int srcoff, int srcscanints,
int dstarr[], int dstoff, int dstscanints,
int w, int h);
public void convert(IntBuffer srcbuf, int srcoff, int srcscanints,
int dstarr[], int dstoff, int dstscanints,
int w, int h);
public void convert(int srcarr[], int srcoff, int srcscanints,
IntBuffer dstbuf, int dstoff, int dstscanints,
int w, int h);
}
