package com.sun.scenario.effect.impl.prism;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.prism.Graphics;
import com.sun.prism.Texture;
import com.sun.scenario.effect.FilterContext;
import com.sun.scenario.effect.ImageData;
import com.sun.scenario.effect.ImageDataRenderer;
public class PrRenderInfo implements ImageDataRenderer {
private Graphics g;
public PrRenderInfo(Graphics g) {
this.g = g;
}
public Graphics getGraphics() {
return g;
}
public void renderImage(ImageData image,
BaseTransform transform,
FilterContext fctx)
{
if (image.validate(fctx)) {
Rectangle r = image.getUntransformedBounds();
Texture tex = ((PrTexture)image.getUntransformedImage()).getTextureObject();
BaseTransform savedTx = null;
if (!transform.isIdentity()) {
savedTx = g.getTransformNoClone().copy();
g.transform(transform);
}
BaseTransform idtx = image.getTransform();
if (!idtx.isIdentity()) {
if (savedTx == null) savedTx = g.getTransformNoClone().copy();
g.transform(idtx);
}
g.drawTexture(tex, r.x, r.y, r.width, r.height);
if (savedTx != null) {
g.setTransform(savedTx);
}
}
}
}
