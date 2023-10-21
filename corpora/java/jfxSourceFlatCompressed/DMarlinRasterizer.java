package com.sun.prism.impl.shape;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.Shape;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.marlin.DMarlinRenderingEngine;
import com.sun.marlin.MarlinRenderer;
import com.sun.marlin.MaskMarlinAlphaConsumer;
import com.sun.marlin.RendererContext;
import com.sun.prism.BasicStroke;
import com.sun.prism.impl.PrismSettings;
public final class DMarlinRasterizer implements ShapeRasterizer {
private static final MaskData EMPTY_MASK = MaskData.create(new byte[1], 0, 0, 1, 1);
@Override
public MaskData getMaskData(Shape shape,
BasicStroke stroke,
RectBounds xformBounds,
BaseTransform xform,
boolean close, boolean antialiasedShape)
{
if (stroke != null && stroke.getType() != BasicStroke.TYPE_CENTERED) {
shape = stroke.createStrokedShape(shape);
stroke = null;
}
if (xformBounds == null) {
if (stroke != null) {
shape = stroke.createStrokedShape(shape);
stroke = null;
}
xformBounds = new RectBounds();
xformBounds = (RectBounds) xform.transform(shape.getBounds(), xformBounds);
}
if (xformBounds.isEmpty()) {
return EMPTY_MASK;
}
final RendererContext rdrCtx = DMarlinRenderingEngine.getRendererContext();
MarlinRenderer renderer = null;
try {
final Rectangle rclip = rdrCtx.clip;
rclip.setBounds(xformBounds);
renderer = DMarlinPrismUtils.setupRenderer(rdrCtx, shape, stroke, xform, rclip,
antialiasedShape);
final int outpix_xmin = renderer.getOutpixMinX();
final int outpix_xmax = renderer.getOutpixMaxX();
final int outpix_ymin = renderer.getOutpixMinY();
final int outpix_ymax = renderer.getOutpixMaxY();
final int w = outpix_xmax - outpix_xmin;
final int h = outpix_ymax - outpix_ymin;
if ((w <= 0) || (h <= 0)) {
return EMPTY_MASK;
}
MaskMarlinAlphaConsumer consumer = rdrCtx.consumer;
if (consumer == null || (w * h) > consumer.getAlphaLength()) {
final int csize = (w * h + 0xfff) & (~0xfff);
rdrCtx.consumer = consumer = new MaskMarlinAlphaConsumer(csize);
if (PrismSettings.verbose) {
System.out.println("new alphas with length = " + csize);
}
}
consumer.setBoundsNoClone(outpix_xmin, outpix_ymin, w, h);
renderer.produceAlphas(consumer);
return consumer.getMaskData();
} finally {
if (renderer != null) {
renderer.dispose();
}
DMarlinRenderingEngine.returnRendererContext(rdrCtx);
}
}
static Shape createCenteredStrokedShape(Shape s, BasicStroke stroke)
{
final float lw = (stroke.getType() == BasicStroke.TYPE_CENTERED) ?
stroke.getLineWidth() : stroke.getLineWidth() * 2.0f;
final RendererContext rdrCtx = DMarlinRenderingEngine.getRendererContext();
try {
final Path2D p2d = rdrCtx.getPath2D();
DMarlinPrismUtils.strokeTo(rdrCtx, s, stroke, lw,
rdrCtx.transformerPC2D.wrapPath2D(p2d)
);
return new Path2D(p2d);
} finally {
DMarlinRenderingEngine.returnRendererContext(rdrCtx);
}
}
}
