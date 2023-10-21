package com.sun.prism.impl.ps;
import com.sun.javafx.geom.Ellipse2D;
import com.sun.javafx.geom.Shape;
import com.sun.prism.Graphics;
public class CachingEllipseRep extends CachingShapeRep {
public CachingEllipseRep() {
}
@Override
CachingShapeRepState createState() {
return new CachingEllipseRepState();
}
}
class CachingEllipseRepState extends CachingShapeRepState {
@Override
void fillNoCache(Graphics g, Shape shape) {
Ellipse2D e = (Ellipse2D)shape;
g.fillEllipse(e.x, e.y, e.width, e.height);
}
@Override
void drawNoCache(Graphics g, Shape shape) {
Ellipse2D e = (Ellipse2D)shape;
g.drawEllipse(e.x, e.y, e.width, e.height);
}
}
