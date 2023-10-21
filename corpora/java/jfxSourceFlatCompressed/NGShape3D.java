package com.sun.javafx.sg.prism;
import com.sun.javafx.geom.Vec3d;
import com.sun.javafx.geom.Vec3f;
import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.util.Utils;
import com.sun.prism.Graphics;
import com.sun.prism.Material;
import com.sun.prism.MeshView;
import com.sun.prism.ResourceFactory;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.geometry.Point3D;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
public abstract class NGShape3D extends NGNode {
private NGPhongMaterial material;
private DrawMode drawMode;
private CullFace cullFace;
private boolean materialDirty = false;
private boolean drawModeDirty = false;
NGTriangleMesh mesh;
private MeshView meshView;
public void setMaterial(NGPhongMaterial material) {
this.material = material;
materialDirty = true;
visualsChanged();
}
public void setDrawMode(Object drawMode) {
this.drawMode = (DrawMode) drawMode;
drawModeDirty = true;
visualsChanged();
}
public void setCullFace(Object cullFace) {
this.cullFace = (CullFace) cullFace;
visualsChanged();
}
void invalidate() {
meshView = null;
visualsChanged();
}
private void renderMeshView(Graphics g) {
g.setup3DRendering();
ResourceFactory rf = g.getResourceFactory();
if (rf == null || rf.isDisposed()) {
return;
}
if (meshView != null && !meshView.isValid()) {
meshView.dispose();
meshView = null;
}
if (meshView == null && mesh != null) {
meshView = rf.createMeshView(mesh.createMesh(rf));
materialDirty = drawModeDirty = true;
}
if (meshView == null || !mesh.validate()) {
return;
}
Material mtl = material.createMaterial(rf);
if (materialDirty) {
meshView.setMaterial(mtl);
materialDirty = false;
}
int cullingMode = cullFace.ordinal();
if (cullFace.ordinal() != MeshView.CULL_NONE
&& g.getTransformNoClone().getDeterminant() < 0) {
cullingMode = cullingMode == MeshView.CULL_BACK
? MeshView.CULL_FRONT : MeshView.CULL_BACK;
}
meshView.setCullingMode(cullingMode);
if (drawModeDirty) {
meshView.setWireframe(drawMode == DrawMode.LINE);
drawModeDirty = false;
}
int lightIndex = 0;
if (g.getLights() == null || g.getLights()[0] == null) {
meshView.setAmbientLight(0.0f, 0.0f, 0.0f);
Vec3d cameraPos = g.getCameraNoClone().getPositionInWorld(null);
meshView.setLight(lightIndex++,
(float) cameraPos.x,
(float) cameraPos.y,
(float) cameraPos.z,
1.0f, 1.0f, 1.0f, 1.0f,
NGPointLight.getDefaultCa(),
NGPointLight.getDefaultLa(),
NGPointLight.getDefaultQa(),
1,
NGPointLight.getDefaultMaxRange(),
(float) NGPointLight.getSimulatedDirection().getX(),
(float) NGPointLight.getSimulatedDirection().getY(),
(float) NGPointLight.getSimulatedDirection().getZ(),
NGPointLight.getSimulatedInnerAngle(),
NGPointLight.getSimulatedOuterAngle(),
NGPointLight.getSimulatedFalloff());
} else {
float ambientRed = 0.0f;
float ambientBlue = 0.0f;
float ambientGreen = 0.0f;
for (NGLightBase lightBase : g.getLights()) {
if (lightBase == null) {
break;
}
if (!lightBase.affects(this)) {
continue;
}
float rL = lightBase.getColor().getRed();
float gL = lightBase.getColor().getGreen();
float bL = lightBase.getColor().getBlue();
if (rL == 0.0f && gL == 0.0f && bL == 0.0f) {
continue;
}
if (lightBase instanceof NGPointLight) {
var light = (NGPointLight) lightBase;
Affine3D lightWT = light.getWorldTransform();
meshView.setLight(lightIndex++,
(float) lightWT.getMxt(),
(float) lightWT.getMyt(),
(float) lightWT.getMzt(),
rL, gL, bL, 1.0f,
light.getCa(),
light.getLa(),
light.getQa(),
1,
light.getMaxRange(),
(float) light.getDirection().getX(),
(float) light.getDirection().getY(),
(float) light.getDirection().getZ(),
light.getInnerAngle(),
light.getOuterAngle(),
light.getFalloff());
} else if (lightBase instanceof NGDirectionalLight) {
var light = (NGDirectionalLight) lightBase;
meshView.setLight(lightIndex++,
0, 0, 0,
rL, gL, bL, 1.0f,
1, 0, 0,
0,
Float.POSITIVE_INFINITY,
(float) light.getDirection().getX(),
(float) light.getDirection().getY(),
(float) light.getDirection().getZ(),
0, 0, 0);
} else if (lightBase instanceof NGAmbientLight) {
ambientRed += rL;
ambientGreen += gL;
ambientBlue += bL;
}
}
ambientRed = Utils.clamp(0, ambientRed, 1);
ambientGreen = Utils.clamp(0, ambientGreen, 1);
ambientBlue = Utils.clamp(0, ambientBlue, 1);
meshView.setAmbientLight(ambientRed, ambientGreen, ambientBlue);
}
while (lightIndex < 3) {
meshView.setLight(lightIndex++,
0, 0, 0,
0, 0, 0, 0,
1, 0, 0, 1, 0,
0, 0, 1,
0, 0, 0);
}
meshView.render(g);
}
public void setMesh(NGTriangleMesh triangleMesh) {
this.mesh = triangleMesh;
meshView = null;
visualsChanged();
}
@Override
protected void renderContent(Graphics g) {
if (!Platform.isSupported(ConditionalFeature.SCENE3D) ||
material == null ||
g instanceof com.sun.prism.PrinterGraphics)
{
return;
}
renderMeshView(g);
}
@Override
boolean isShape3D() {
return true;
}
@Override
protected boolean hasOverlappingContents() {
return false;
}
@Override
public void release() {
}
}
