package com.sun.javafx.sg.prism;
import com.sun.javafx.geom.PickRay;
public class NGParallelCamera extends NGCamera {
public NGParallelCamera() { }
@Override
public PickRay computePickRay(float x, float y, PickRay pickRay) {
return PickRay.computeParallelPickRay(x, y, viewHeight, worldTransform,
zNear, zFar, pickRay);
}
}
