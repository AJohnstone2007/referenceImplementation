package com.sun.javafx.sg.prism;
import com.sun.javafx.geom.PickRay;
public class NGPerspectiveCamera extends NGCamera {
private final boolean fixedEyeAtCameraZero;
private double fovrad;
private boolean verticalFieldOfView;
public NGPerspectiveCamera(boolean fixedEyeAtCameraZero) {
this.fixedEyeAtCameraZero = fixedEyeAtCameraZero;
}
public void setFieldOfView(float fieldOfViewDegrees) {
this.fovrad = Math.toRadians(fieldOfViewDegrees);
}
public void setVerticalFieldOfView(boolean verticalFieldOfView) {
this.verticalFieldOfView = verticalFieldOfView;
}
@Override
public PickRay computePickRay(float x, float y, PickRay pickRay) {
return PickRay.computePerspectivePickRay(x, y, fixedEyeAtCameraZero,
viewWidth, viewHeight, fovrad, verticalFieldOfView, worldTransform,
zNear, zFar,
pickRay);
}
}
