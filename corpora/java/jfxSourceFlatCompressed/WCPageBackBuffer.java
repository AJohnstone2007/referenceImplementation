package com.sun.webkit.graphics;
public abstract class WCPageBackBuffer extends Ref {
public abstract WCGraphicsContext createGraphics();
public abstract void disposeGraphics(WCGraphicsContext gc);
public abstract void flush(WCGraphicsContext gc, int x, int y, int w, int h);
protected abstract void copyArea(int x, int y, int w, int h, int dx, int dy);
public abstract boolean validate(int width, int height);
}
