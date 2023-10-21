package test.javafx.scene.shape;
import com.sun.javafx.sg.prism.NGShape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
public class StubNGShape extends NGShape {
private StrokeType pgStrokeType;
private StrokeLineCap pgStrokeLineCap;
private StrokeLineJoin pgStrokeLineJoin;
private float strokeWidth;
private float strokeMiterLimit;
private float[] strokeDashArray;
private float strokeDashOffset;
private Object stroke;
private NGShape.Mode mode;
private boolean smooth;
private Object fill;
public Object getFill() {
return fill;
}
public boolean isSmooth() {
return smooth;
}
public NGShape.Mode getMode() {
return mode;
}
public Object getStroke() {
return stroke;
}
public float getStrokeDashOffset() {
return strokeDashOffset;
}
public float getStrokeMiterLimit() {
return strokeMiterLimit;
}
public float getStrokeWidth() {
return strokeWidth;
}
public StrokeType getStrokeType() {
return pgStrokeType;
}
public StrokeLineCap getStrokeLineCap() {
return pgStrokeLineCap;
}
public StrokeLineJoin getStrokeLineJoin() {
return pgStrokeLineJoin;
}
public void setMode(NGShape.Mode mode) {
this.mode = mode;
}
@Override
public void setSmooth(boolean smooth) {
this.smooth = smooth;
}
@Override
public void setFillPaint(Object fillPaint) {
this.fill = fillPaint;
}
@Override
public void setDrawPaint(Object drawPaint) {
this.stroke = drawPaint;
}
@Override
public void setDrawStroke(float strokeWidth,
StrokeType type,
StrokeLineCap lineCap,
StrokeLineJoin lineJoin,
float strokeMiterLimit,
float[] strokeDashArray,
float strokeDashOffset) {
this.pgStrokeType = type;
this.pgStrokeLineCap = lineCap;
this.pgStrokeLineJoin = lineJoin;
this.strokeWidth = strokeWidth;
this.strokeMiterLimit = strokeMiterLimit;
this.strokeDashOffset = strokeDashOffset;
this.strokeDashArray = new float[strokeDashArray == null ? 0 : strokeDashArray.length];
System.arraycopy(strokeDashArray, 0, this.strokeDashArray, 0, this.strokeDashArray.length);
}
@Override
public com.sun.javafx.geom.Shape getShape() {
return null;
}
}
