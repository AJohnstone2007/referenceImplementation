package com.sun.glass.ui.monocle;
import java.nio.Buffer;
import java.nio.ByteBuffer;
public class FramebufferShim extends Framebuffer {
public FramebufferShim(ByteBuffer bb, int width, int height, int depth, boolean clear) {
super(bb, width, height, depth, clear);
}
@Override
public void composePixels(Buffer src,
int pX, int pY, int pW, int pH,
float alpha) {
super.composePixels(src, pX, pY, pW, pH, alpha);
}
@Override
public void reset() {
super.reset();
}
}
