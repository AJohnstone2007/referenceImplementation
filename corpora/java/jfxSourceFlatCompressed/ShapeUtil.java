package com.sun.prism.impl.shape;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.geom.PathConsumer2D;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.Shape;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.prism.BasicStroke;
import com.sun.prism.impl.PrismSettings;
import com.sun.prism.impl.PrismSettings.RasterizerType;
public class ShapeUtil {
private static final ShapeRasterizer shapeRasterizer;
static {
switch (PrismSettings.rasterizerSpec) {
default:
case DoubleMarlin:
shapeRasterizer = new DMarlinRasterizer();
break;
}
}
public static MaskData rasterizeShape(Shape shape,
BasicStroke stroke,
RectBounds xformBounds,
BaseTransform xform,
boolean close, boolean antialiasedShape)
{
return shapeRasterizer.getMaskData(shape, stroke, xformBounds, xform, close, antialiasedShape);
}
public static Shape createCenteredStrokedShape(Shape s, BasicStroke stroke) {
return DMarlinRasterizer.createCenteredStrokedShape(s, stroke);
}
private ShapeUtil() {
}
}
