package com.sun.javafx.image;
import java.nio.Buffer;
public interface PixelAccessor<T extends Buffer>
extends PixelGetter<T>, PixelSetter<T>
{
}
