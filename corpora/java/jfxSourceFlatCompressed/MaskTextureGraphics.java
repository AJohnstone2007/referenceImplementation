package com.sun.prism;
public interface MaskTextureGraphics extends Graphics {
public void drawPixelsMasked(RTTexture imgtex, RTTexture masktex,
int dx, int dy, int dw, int dh,
int ix, int iy, int mx, int my);
public void maskInterpolatePixels(RTTexture imgtex, RTTexture masktex,
int dx, int dy, int dw, int dh,
int ix, int iy, int mx, int my);
}
