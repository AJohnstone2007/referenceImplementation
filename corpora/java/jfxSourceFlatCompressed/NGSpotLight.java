package com.sun.javafx.sg.prism;
import com.sun.javafx.geom.Vec3d;
import javafx.geometry.Point3D;
public class NGSpotLight extends NGPointLight {
private static final Point3D DEFAULT_DIRECTION = new Point3D(0, 0, 1);
private static final float DEFAULT_INNER_ANGLE = 0;
private static final float DEFAULT_OUTER_ANGLE = 30;
private static final float DEFAULT_FALLOFF = 1;
public NGSpotLight() {
}
public static Point3D getDefaultDirection() {
return DEFAULT_DIRECTION;
}
public static float getDefaultInnerAngle() {
return DEFAULT_INNER_ANGLE;
}
public static float getDefaultOuterAngle() {
return DEFAULT_OUTER_ANGLE;
}
public static float getDefaultFalloff() {
return DEFAULT_FALLOFF;
}
private Point3D direction = DEFAULT_DIRECTION;
private final Vec3d effectiveDir = new Vec3d();
@Override
public Point3D getDirection() {
var dir = new Vec3d(direction.getX(), direction.getY(), direction.getZ());
getWorldTransform().deltaTransform(dir, effectiveDir);
return new Point3D(effectiveDir.x, effectiveDir.y, effectiveDir.z);
}
public void setDirection(Point3D direction) {
if (!this.direction.equals(direction)) {
this.direction = direction;
visualsChanged();
}
}
private float innerAngle = DEFAULT_INNER_ANGLE;
@Override
public float getInnerAngle() {
return innerAngle;
}
public void setInnerAngle(float innerAngle) {
if (this.innerAngle != innerAngle) {
this.innerAngle = innerAngle;
visualsChanged();
}
}
private float outerAngle = DEFAULT_OUTER_ANGLE;
@Override
public float getOuterAngle() {
return outerAngle;
}
public void setOuterAngle(float outerAngle) {
if (this.outerAngle != outerAngle) {
this.outerAngle = outerAngle;
visualsChanged();
}
}
private float falloff = DEFAULT_FALLOFF;
@Override
public float getFalloff() {
return falloff;
}
public void setFalloff(float falloff) {
if (this.falloff != falloff) {
this.falloff = falloff;
visualsChanged();
}
}
}
