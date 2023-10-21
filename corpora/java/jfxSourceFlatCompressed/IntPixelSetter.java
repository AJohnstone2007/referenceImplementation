package com.sun.javafx.image;
import java.nio.IntBuffer;
public interface IntPixelSetter extends PixelSetter<IntBuffer> {
public void setArgb(int arr[], int offset, int argb);
public void setArgbPre(int arr[], int offset, int argbpre);
}
