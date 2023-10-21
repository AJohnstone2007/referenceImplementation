package com.sun.prism.impl.shape;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.Shape;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.prism.BasicStroke;
public interface ShapeRasterizer {
public MaskData getMaskData(Shape shape, BasicStroke stroke,
RectBounds xformBounds,
BaseTransform xform,
boolean close, boolean antialiasedShape);
}
