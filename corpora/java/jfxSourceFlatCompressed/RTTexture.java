package com.sun.prism;
import java.nio.Buffer;
public interface RTTexture extends Texture, RenderTarget {
public int[] getPixels();
public boolean readPixels(Buffer pixels);
public boolean readPixels(Buffer pixels, int x, int y, int width, int height);
public boolean isVolatile();
}
