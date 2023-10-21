package com.sun.javafx.sg.prism;
import javafx.geometry.Point3D;
public class NGPointLight extends NGLightBase {
private static final float DEFAULT_CA = 1;
private static final float DEFAULT_LA = 0;
private static final float DEFAULT_QA = 0;
private static final float DEFAULT_MAX_RANGE = Float.POSITIVE_INFINITY;
private static final Point3D SIMULATED_DIRECTION = new Point3D(0, 0, 1);
private static final float SIMULATED_INNER_ANGLE = 0;
private static final float SIMULATED_OUTER_ANGLE = 180;
private static final float SIMULATED_FALLOFF = 0;
public NGPointLight() {
}
public static float getDefaultCa() {
return DEFAULT_CA;
}
public static float getDefaultLa() {
return DEFAULT_LA;
}
public static float getDefaultQa() {
return DEFAULT_QA;
}
public static float getDefaultMaxRange() {
return DEFAULT_MAX_RANGE;
}
public static Point3D getSimulatedDirection() {
return SIMULATED_DIRECTION;
}
public static float getSimulatedInnerAngle() {
return SIMULATED_INNER_ANGLE;
}
public static float getSimulatedOuterAngle() {
return SIMULATED_OUTER_ANGLE;
}
public static float getSimulatedFalloff() {
return SIMULATED_FALLOFF;
}
public Point3D getDirection() {
return SIMULATED_DIRECTION;
}
public float getInnerAngle() {
return SIMULATED_INNER_ANGLE;
}
public float getOuterAngle() {
return SIMULATED_OUTER_ANGLE;
}
public float getFalloff() {
return SIMULATED_FALLOFF;
}
private float ca = DEFAULT_CA;
public float getCa() {
return ca;
}
public void setCa(float ca) {
if (this.ca != ca) {
this.ca = ca;
visualsChanged();
}
}
private float la = DEFAULT_LA;
public float getLa() {
return la;
}
public void setLa(float la) {
if (this.la != la) {
this.la = la;
visualsChanged();
}
}
private float qa = DEFAULT_QA;
public float getQa() {
return qa;
}
public void setQa(float qa) {
if (this.qa != qa) {
this.qa = qa;
visualsChanged();
}
}
private float maxRange = DEFAULT_MAX_RANGE;
public float getMaxRange() {
return maxRange;
}
public void setMaxRange(float maxRange) {
maxRange = maxRange < 0 ? 0 : maxRange;
if (this.maxRange != maxRange) {
this.maxRange = maxRange;
visualsChanged();
}
}
}
