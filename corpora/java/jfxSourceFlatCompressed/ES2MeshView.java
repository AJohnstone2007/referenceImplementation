package com.sun.prism.es2;
import com.sun.prism.Graphics;
import com.sun.prism.Material;
import com.sun.prism.impl.BaseMeshView;
import com.sun.prism.impl.Disposer;
class ES2MeshView extends BaseMeshView {
static int count = 0;
private final ES2Context context;
private final long nativeHandle;
private float ambientLightRed = 0;
private float ambientLightBlue = 0;
private float ambientLightGreen = 0;
private ES2Light[] lights = new ES2Light[3];
final private ES2Mesh mesh;
private ES2PhongMaterial material;
private ES2MeshView(ES2Context context, long nativeHandle, ES2Mesh mesh,
Disposer.Record disposerRecord) {
super(disposerRecord);
this.context = context;
this.mesh = mesh;
this.nativeHandle = nativeHandle;
count++;
}
static ES2MeshView create(ES2Context context, ES2Mesh mesh) {
long nativeHandle = context.createES2MeshView(mesh);
return new ES2MeshView(context, nativeHandle, mesh, new ES2MeshViewDisposerRecord(context, nativeHandle));
}
@Override
public void setCullingMode(int cullingMode) {
context.setCullingMode(nativeHandle, cullingMode);
}
@Override
public void setMaterial(Material material) {
context.setMaterial(nativeHandle, material);
this.material = (ES2PhongMaterial) material;
}
@Override
public void setWireframe(boolean wireframe) {
context.setWireframe(nativeHandle, wireframe);
}
@Override
public void setAmbientLight(float r, float g, float b) {
ambientLightRed = r;
ambientLightGreen = g;
ambientLightBlue = b;
context.setAmbientLight(nativeHandle, r, g, b);
}
float getAmbientLightRed() {
return ambientLightRed;
}
float getAmbientLightGreen() {
return ambientLightGreen;
}
float getAmbientLightBlue() {
return ambientLightBlue;
}
@Override
public void setLight(int index, float x, float y, float z, float r, float g, float b, float w,
float ca, float la, float qa, float isAttenuated, float maxRange, float dirX, float dirY, float dirZ,
float innerAngle, float outerAngle, float falloff) {
if (index >= 0 && index <= 2) {
lights[index] = new ES2Light(x, y, z, r, g, b, w, ca, la, qa, isAttenuated,
maxRange, dirX, dirY, dirZ, innerAngle, outerAngle, falloff);
context.setLight(nativeHandle, index, x, y, z, r, g, b, w, ca, la, qa, isAttenuated,
maxRange, dirX, dirY, dirZ, innerAngle, outerAngle, falloff);
}
}
ES2Light[] getLights() {
return lights;
}
@Override
public void render(Graphics g) {
material.lockTextureMaps();
context.renderMeshView(nativeHandle, g, this);
material.unlockTextureMaps();
}
ES2PhongMaterial getMaterial() {
return material;
}
@Override
public void dispose() {
material = null;
lights = null;
disposerRecord.dispose();
count--;
}
public int getCount() {
return count;
}
static class ES2MeshViewDisposerRecord implements Disposer.Record {
private final ES2Context context;
private long nativeHandle;
ES2MeshViewDisposerRecord(ES2Context context, long nativeHandle) {
this.context = context;
this.nativeHandle = nativeHandle;
}
void traceDispose() { }
@Override
public void dispose() {
if (nativeHandle != 0L) {
traceDispose();
context.releaseES2MeshView(nativeHandle);
nativeHandle = 0L;
}
}
}
}
