package com.sun.javafx.sg.prism;
import com.sun.javafx.geom.Vec3d;
import javafx.geometry.Point3D;
public class NGDirectionalLight extends NGLightBase {
private static final Point3D DEFAULT_DIRECTION = new Point3D(0, 0, 1);
public NGDirectionalLight() {
}
public static Point3D getDefaultDirection() {
return DEFAULT_DIRECTION;
}
private Point3D direction = DEFAULT_DIRECTION;
private final Vec3d effectiveDir = new Vec3d();
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
}
