package com.sun.javafx.image;
import java.nio.IntBuffer;
public interface IntPixelGetter extends PixelGetter<IntBuffer> {
public int getArgb(int arr[], int offset);
public int getArgbPre(int arr[], int offset);
}
