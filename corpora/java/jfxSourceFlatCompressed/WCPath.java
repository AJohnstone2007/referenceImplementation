package com.sun.webkit.graphics;
import java.lang.annotation.Native;
public abstract class WCPath<P> extends Ref {
@Native public static final int RULE_NONZERO = 0;
@Native public static final int RULE_EVENODD = 1;
public abstract void addRect(double x, double y, double w, double h);
public abstract void addEllipse(double x, double y, double w, double h);
public abstract void addArcTo(double x1, double y1, double x2, double y2, double r);
public abstract void addArc(double x, double y, double r, double startAngle,
double endAngle, boolean aclockwise);
public abstract boolean contains(int rule, double x, double y);
public abstract WCRectangle getBounds();
public abstract void clear();
public abstract void moveTo(double x, double y);
public abstract void addLineTo(double x, double y);
public abstract void addQuadCurveTo(double x0, double y0, double x1, double y1);
public abstract void addBezierCurveTo(double x0, double y0,
double x1, double y1,
double x2, double y2);
public abstract void addPath(WCPath path);
public abstract void closeSubpath();
public abstract boolean isEmpty();
public abstract void translate(double x, double y);
public abstract void transform(double mxx, double myx,
double mxy, double myy,
double mxt, double myt);
public abstract int getWindingRule();
public abstract void setWindingRule(int rule);
public abstract P getPlatformPath();
public abstract WCPathIterator getPathIterator();
public abstract boolean strokeContains(double x, double y,
double thickness, double miterLimit,
int cap, int join, double dashOffset,
double[] dashArray);
}
