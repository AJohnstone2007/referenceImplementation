package com.sun.prism.d3d;
import com.sun.prism.Image;
import com.sun.prism.PhongMaterial;
import com.sun.prism.Texture;
import com.sun.prism.TextureMap;
import com.sun.prism.impl.BasePhongMaterial;
import com.sun.prism.impl.Disposer;
import com.sun.javafx.logging.PlatformLogger;
class D3DPhongMaterial extends BasePhongMaterial implements PhongMaterial {
static int count = 0;
private final D3DContext context;
private final long nativeHandle;
private TextureMap maps[] = new TextureMap[MAX_MAP_TYPE];
private D3DPhongMaterial(D3DContext context, long nativeHandle,
Disposer.Record disposerRecord) {
super(disposerRecord);
this.context = context;
this.nativeHandle = nativeHandle;
count++;
}
static D3DPhongMaterial create(D3DContext context) {
long nativeHandle = context.createD3DPhongMaterial();
return new D3DPhongMaterial(context, nativeHandle, new D3DPhongMaterialDisposerRecord(context, nativeHandle));
}
long getNativeHandle() {
return nativeHandle;
}
@Override
public void setDiffuseColor(float r, float g, float b, float a) {
context.setDiffuseColor(nativeHandle, r, g, b, a);
}
@Override
public void setSpecularColor(boolean set, float r, float g, float b, float a) {
context.setSpecularColor(nativeHandle, set, r, g, b, a);
}
@Override
public void setTextureMap(TextureMap map) {
maps[map.getType().ordinal()] = map;
}
private Texture setupTexture(TextureMap map, boolean useMipmap) {
Image image = map.getImage();
Texture texture = (image == null) ? null
: context.getResourceFactory().getCachedTexture(image, Texture.WrapMode.REPEAT, useMipmap);
long hTexture = (texture != null) ? ((D3DTexture) texture).getNativeTextureObject() : 0;
context.setMap(nativeHandle, map.getType().ordinal(), hTexture);
return texture;
}
@Override
public void lockTextureMaps() {
for (int i = 0; i < MAX_MAP_TYPE; i++) {
Texture texture = maps[i].getTexture();
if (!maps[i].isDirty() && texture != null) {
texture.lock();
if (!texture.isSurfaceLost()) {
continue;
}
}
boolean useMipmap = (i == PhongMaterial.DIFFUSE) || (i == PhongMaterial.SELF_ILLUM);
texture = setupTexture(maps[i], useMipmap);
maps[i].setTexture(texture);
maps[i].setDirty(false);
if (maps[i].getImage() != null && texture == null) {
String logname = PhongMaterial.class.getName();
PlatformLogger.getLogger(logname).warning(
"Warning: Low on texture resources. Cannot create texture.");
}
}
}
@Override
public void unlockTextureMaps() {
for (int i = 0; i < MAX_MAP_TYPE; i++) {
Texture texture = maps[i].getTexture();
if (texture != null) {
texture.unlock();
}
}
}
@Override
public boolean isValid() {
return !context.isDisposed();
}
@Override
public void dispose() {
disposerRecord.dispose();
count--;
}
public int getCount() {
return count;
}
static class D3DPhongMaterialDisposerRecord implements Disposer.Record {
private final D3DContext context;
private long nativeHandle;
D3DPhongMaterialDisposerRecord(D3DContext context, long nativeHandle) {
this.context = context;
this.nativeHandle = nativeHandle;
}
void traceDispose() {}
@Override
public void dispose() {
if (nativeHandle != 0L) {
traceDispose();
context.releaseD3DPhongMaterial(nativeHandle);
nativeHandle = 0L;
}
}
}
}
