package com.sun.prism.j2d;
import com.sun.javafx.geom.Rectangle;
import com.sun.prism.PresentableState;
import com.sun.prism.PrinterGraphics;
public final class PrismPrintGraphics
extends J2DPrismGraphics
implements PrinterGraphics {
static class PrintResourceFactory extends J2DResourceFactory {
PrintResourceFactory() {
super(null);
}
@Override
J2DPrismGraphics createJ2DPrismGraphics(J2DPresentable target,
java.awt.Graphics2D g2d) {
J2DPrismGraphics pg = new PrismPrintGraphics(target, g2d);
Rectangle cr = new Rectangle(0, 0, target.getContentWidth(),
target.getContentHeight());
pg.setClipRect(cr);
return pg;
}
}
static class PagePresentable extends J2DPresentable {
private int width;
private int height;
static J2DResourceFactory factory = new PrintResourceFactory();
PagePresentable(int width, int height) {
super(null, factory);
this.width = width;
this.height = height;
}
@Override
public java.awt.image.BufferedImage createBuffer(int w, int h) {
throw new UnsupportedOperationException("cannot create new buffers for image");
}
@Override
public boolean lockResources(PresentableState pState) {
return false;
}
public boolean prepare(Rectangle dirtyregion) {
throw new UnsupportedOperationException("Cannot prepare an image");
}
public boolean present() {
throw new UnsupportedOperationException("Cannot present on image");
}
public int getContentWidth() {
return width;
}
public int getContentHeight() {
return height;
}
private boolean opaque;
public void setOpaque(boolean opaque) {
this.opaque = opaque;
}
public boolean isOpaque() {
return opaque;
}
}
protected void setTransformG2D(java.awt.geom.AffineTransform tx) {
g2d.setTransform(origTx2D);
g2d.transform(tx);
}
private java.awt.geom.AffineTransform origTx2D;
protected void captureTransform(java.awt.Graphics2D g2d) {
origTx2D = g2d.getTransform();
}
public PrismPrintGraphics(java.awt.Graphics2D g2d, int width, int height) {
super(new PagePresentable(width, height), g2d);
setClipRect(new Rectangle(0,0,width,height));
}
PrismPrintGraphics(J2DPresentable target, java.awt.Graphics2D g2d) {
super(target, g2d);
}
}
