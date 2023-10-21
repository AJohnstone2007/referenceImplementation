package com.sun.prism.es2;
import com.sun.javafx.PlatformUtil;
import com.sun.prism.Image;
import com.sun.prism.PhongMaterial;
import com.sun.prism.Texture;
import com.sun.prism.TextureMap;
import com.sun.prism.impl.BasePhongMaterial;
import com.sun.prism.impl.Disposer;
import com.sun.prism.paint.Color;
import com.sun.javafx.logging.PlatformLogger;
class ES2PhongMaterial extends BasePhongMaterial implements PhongMaterial {
static int count = 0;
private final ES2Context context;
private final long nativeHandle;
TextureMap maps[] = new TextureMap[MAX_MAP_TYPE];
Color diffuseColor = Color.WHITE;
Color specularColor = Color.WHITE;
boolean specularColorSet = false;
private ES2PhongMaterial(ES2Context context, long nativeHandle,
Disposer.Record disposerRecord) {
super(disposerRecord);
this.context = context;
this.nativeHandle = nativeHandle;
count++;
}
static ES2PhongMaterial create(ES2Context context) {
long nativeHandle = context.createES2PhongMaterial();
return new ES2PhongMaterial(context, nativeHandle, new ES2PhongMaterialDisposerRecord(context, nativeHandle));
}
long getNativeHandle() {
return nativeHandle;
}
@Override
public void setDiffuseColor(float r, float g, float b, float a) {
diffuseColor = new Color(r,g,b,a);
}
@Override
public void setSpecularColor(boolean set, float r, float g, float b, float a) {
specularColorSet = set;
specularColor = new Color(r,g,b,a);
}
@Override
public void setTextureMap(TextureMap map) {
maps[map.getType().ordinal()] = map;
}
private Texture setupTexture(TextureMap map, boolean useMipmap) {
Image image = map.getImage();
Texture texture = (image == null) ? null
: context.getResourceFactory().getCachedTexture(image, Texture.WrapMode.REPEAT, useMipmap);
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
boolean useMipmap = (!PlatformUtil.isEmbedded()) && (i == PhongMaterial.DIFFUSE || i == PhongMaterial.SELF_ILLUM);
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
for (int i = 0; i < MAX_MAP_TYPE; i++ ) {
Texture texture = maps[i].getTexture();
if (texture != null) {
texture.unlock();
}
}
}
@Override
public void dispose() {
disposerRecord.dispose();
count--;
}
public int getCount() {
return count;
}
static class ES2PhongMaterialDisposerRecord implements Disposer.Record {
private final ES2Context context;
private long nativeHandle;
ES2PhongMaterialDisposerRecord(ES2Context context, long nativeHandle) {
this.context = context;
this.nativeHandle = nativeHandle;
}
void traceDispose() {}
@Override
public void dispose() {
if (nativeHandle != 0L) {
traceDispose();
context.releaseES2PhongMaterial(nativeHandle);
nativeHandle = 0L;
}
}
}
}
