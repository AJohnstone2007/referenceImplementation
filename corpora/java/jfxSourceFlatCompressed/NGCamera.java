package com.sun.javafx.sg.prism;
import com.sun.javafx.geom.PickRay;
import com.sun.javafx.geom.Vec3d;
import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.geom.transform.GeneralTransform3D;
import com.sun.prism.Graphics;
public abstract class NGCamera extends NGNode {
public static final NGCamera INSTANCE = new NGDefaultCamera();
protected Affine3D worldTransform = new Affine3D();
protected double viewWidth = 1.0;
protected double viewHeight = 1.0;
protected double zNear = 0.1;
protected double zFar = 100.0;
private Vec3d worldPosition = new Vec3d();
protected GeneralTransform3D projViewTx = new GeneralTransform3D();
@Override protected void doRender(Graphics g) {
}
@Override protected void renderContent(Graphics g) {
}
@Override protected boolean hasOverlappingContents() {
return false;
}
public void setNearClip(float nearClip) {
this.zNear = nearClip;
}
public double getNearClip() {
return zNear;
}
public void setFarClip(float farClip) {
this.zFar = farClip;
}
public double getFarClip() {
return zFar;
}
public void setViewWidth(double viewWidth) {
this.viewWidth = viewWidth;
}
public double getViewWidth() {
return viewWidth;
}
public void setViewHeight(double viewHeight) {
this.viewHeight = viewHeight;
}
public double getViewHeight() {
return viewHeight;
}
public void setProjViewTransform(GeneralTransform3D projViewTx) {
this.projViewTx.set(projViewTx);
}
public void setPosition(Vec3d position) {
worldPosition.set(position);
}
public void setWorldTransform(Affine3D localToWorldTx) {
worldTransform.setTransform(localToWorldTx);
}
public GeneralTransform3D getProjViewTx(GeneralTransform3D tx) {
if (tx == null) {
tx = new GeneralTransform3D();
}
return tx.set(projViewTx);
}
public Vec3d getPositionInWorld(Vec3d pos) {
if (pos == null) {
pos = new Vec3d();
}
pos.set(worldPosition);
return pos;
}
@Override
public void release() {
}
public abstract PickRay computePickRay(float x, float y, PickRay pickRay);
}
