package com.sun.javafx.sg.prism;
import java.util.List;
import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.prism.Graphics;
import com.sun.prism.paint.Color;
public class NGLightBase extends NGNode {
private Color color = Color.WHITE;
private boolean lightOn = true;
private Affine3D worldTransform;
protected NGLightBase() {
}
@Override
public void setTransformMatrix(BaseTransform tx) {
super.setTransformMatrix(tx);
}
@Override
protected void doRender(Graphics g) {}
@Override protected void renderContent(Graphics g) {}
@Override protected boolean hasOverlappingContents() {
return false;
}
public Color getColor() {
return color;
}
public void setColor(Object value) {
if (!this.color.equals(value)) {
this.color = (Color)value;
visualsChanged();
}
}
public boolean isLightOn() {
return lightOn;
}
public void setLightOn(boolean value) {
if (lightOn != value) {
visualsChanged();
lightOn = value;
}
}
public Affine3D getWorldTransform() {
return worldTransform;
}
public void setWorldTransform(Affine3D localToSceneTx) {
this.worldTransform = localToSceneTx;
}
List<NGNode> scopedNodes = List.of();
public void setScope(List<NGNode> scopedNodes) {
if (!this.scopedNodes.equals(scopedNodes)) {
this.scopedNodes = scopedNodes;
visualsChanged();
}
}
List<NGNode> excludedNodes = List.of();
public void setExclusionScope(List<NGNode> excludedNodes) {
if (!this.excludedNodes.equals(excludedNodes)) {
this.excludedNodes = excludedNodes;
visualsChanged();
}
}
final boolean affects(NGShape3D n3d) {
if (!lightOn) {
return false;
}
if (scopedNodes.isEmpty() && excludedNodes.isEmpty()) {
return true;
}
if (scopedNodes.contains(n3d)) {
return true;
}
if (excludedNodes.contains(n3d)) {
return false;
}
NGNode parent = n3d.getParent();
while (parent != null) {
if (scopedNodes.contains(parent)) {
return true;
}
if (excludedNodes.contains(parent)) {
return false;
}
parent = parent.getParent();
}
return scopedNodes.isEmpty();
}
@Override
public void release() {
}
}
