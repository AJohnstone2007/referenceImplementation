package com.sun.prism.impl.ps;
import com.sun.javafx.geom.RoundRectangle2D;
import com.sun.javafx.geom.Shape;
import com.sun.prism.Graphics;
import com.sun.prism.impl.shape.BasicRoundRectRep;
public class CachingRoundRectRep extends CachingShapeRep {
public CachingRoundRectRep() {
}
@Override
CachingShapeRepState createState() {
return new CachingRoundRectRepState();
}
}
class CachingRoundRectRepState extends CachingShapeRepState {
@Override
void fillNoCache(Graphics g, Shape shape) {
BasicRoundRectRep.fillRoundRect(g, (RoundRectangle2D)shape);
}
@Override
void drawNoCache(Graphics g, Shape shape) {
BasicRoundRectRep.drawRoundRect(g, (RoundRectangle2D)shape);
}
}
