package com.sun.javafx.image;
import java.nio.ByteBuffer;
public interface BytePixelSetter extends PixelSetter<ByteBuffer> {
public void setArgb(byte arr[], int offset, int argb);
public void setArgbPre(byte arr[], int offset, int argbpre);
}
