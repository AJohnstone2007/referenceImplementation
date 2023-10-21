package com.sun.javafx.image;
import java.nio.ByteBuffer;
public interface BytePixelGetter extends PixelGetter<ByteBuffer> {
public int getArgb(byte arr[], int offset);
public int getArgbPre(byte arr[], int offset);
}
