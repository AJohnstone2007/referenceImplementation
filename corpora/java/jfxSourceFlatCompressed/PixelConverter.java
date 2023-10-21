package com.sun.javafx.image;
import java.nio.Buffer;
public interface PixelConverter<T extends Buffer, U extends Buffer> {
public void convert(T srcbuf, int srcoff, int srcscanelems,
U dstbuf, int dstoff, int dstscanelems,
int w, int h);
public PixelGetter<T> getGetter();
public PixelSetter<U> getSetter();
}
