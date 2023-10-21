package com.sun.prism.shape;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.Shape;
import com.sun.prism.Graphics;
public interface ShapeRep {
public enum InvalidationType {
LOCATION,
LOCATION_AND_GEOMETRY
}
public boolean is3DCapable();
public void invalidate(InvalidationType type);
public void fill(Graphics g, Shape shape, BaseBounds bounds);
public void draw(Graphics g, Shape shape, BaseBounds bounds);
public void dispose();
}
