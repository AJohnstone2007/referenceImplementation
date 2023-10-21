package com.sun.prism.impl;
import com.sun.prism.PixelFormat;
public interface TextureResourcePool<T> extends ResourcePool<T> {
public long estimateTextureSize(int width, int height, PixelFormat format);
public long estimateRTTextureSize(int width, int height, boolean hasDepth);
}
