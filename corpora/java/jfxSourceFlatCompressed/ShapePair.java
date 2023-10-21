package com.sun.javafx.geom;
public abstract class ShapePair extends Shape {
public static final int INCLUDES_O_NOT_I = 1;
public static final int INCLUDES_I_NOT_O = 2;
public static final int INCLUDES_O_AND_I = 4;
public static final int INCLUDES_NEITHER = 8;
public static final int TYPE_SUBTRACT = (INCLUDES_O_NOT_I);
public static final int TYPE_INTERSECT = (INCLUDES_O_AND_I);
public abstract int getCombinationType();
public abstract Shape getOuterShape();
public abstract Shape getInnerShape();
}
