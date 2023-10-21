package com.sun.prism.impl;
import com.sun.javafx.geom.Rectangle;
import com.sun.prism.Image;
import com.sun.prism.PixelFormat;
import com.sun.prism.ResourceFactory;
import com.sun.prism.ResourceFactoryListener;
import com.sun.prism.Texture;
import com.sun.prism.Texture.Usage;
import com.sun.prism.Texture.WrapMode;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.Collection;
import javafx.util.Pair;
public abstract class BaseResourceFactory implements ResourceFactory {
private final Map<Image,Texture> clampTexCache;
private final Map<Image,Texture> repeatTexCache;
private final Map<Image,Texture> mipmapTexCache;
private final WeakHashMap<ResourceFactoryListener,Boolean> listenerMap =
new WeakHashMap<ResourceFactoryListener,Boolean>();
private boolean disposed = false;
private Texture regionTexture;
private Texture glyphTexture;
private boolean superShaderAllowed;
public BaseResourceFactory() {
this(new WeakHashMap<Image,Texture>(),
new WeakHashMap<Image,Texture>(),
new WeakHashMap<Image,Texture>());
}
public BaseResourceFactory(Map<Image, Texture> clampTexCache,
Map<Image, Texture> repeatTexCache,
Map<Image, Texture> mipmapTexCache)
{
this.clampTexCache = clampTexCache;
this.repeatTexCache = repeatTexCache;
this.mipmapTexCache = mipmapTexCache;
}
@Override public void addFactoryListener(ResourceFactoryListener l) {
listenerMap.put(l, Boolean.TRUE);
}
@Override public void removeFactoryListener(ResourceFactoryListener l) {
listenerMap.remove(l);
}
@Override public boolean isDeviceReady() {
return !isDisposed();
}
protected void clearTextureCache() {
clearTextureCache(clampTexCache);
clearTextureCache(repeatTexCache);
clearTextureCache(mipmapTexCache);
}
protected void clearTextureCache(Map<Image,Texture> texCache) {
Collection<Texture> texAll = texCache.values();
for (Texture i : texAll) {
i.dispose();
}
texCache.clear();
}
protected ResourceFactoryListener[] getFactoryListeners() {
return listenerMap.keySet().toArray(new ResourceFactoryListener[0]);
}
private void disposeResources() {
clampTexCache.clear();
repeatTexCache.clear();
mipmapTexCache.clear();
if (regionTexture != null) {
regionTexture.dispose();
regionTexture = null;
}
if (glyphTexture != null) {
glyphTexture.dispose();
glyphTexture = null;
}
}
protected void notifyReset() {
disposeResources();
ResourceFactoryListener[] notifyList = getFactoryListeners();
for (ResourceFactoryListener listener : notifyList) {
if (null != listener) {
listener.factoryReset();
}
}
}
@Override
public void dispose() {
disposeResources();
disposed = true;
ResourceFactoryListener[] notifyList = getFactoryListeners();
for (ResourceFactoryListener listener : notifyList) {
if (null != listener) {
listener.factoryReleased();
}
}
}
static long sizeWithMipMap(int w, int h, PixelFormat format) {
long size = 0;
int bytesPerPixel = format.getBytesPerPixelUnit();
while (w > 1 && h > 1) {
size += ((long) w) * ((long) h);
w = (w + 1) >> 1;
h = (h + 1) >> 1;
}
size += 1;
return size * bytesPerPixel;
}
@Override
public Texture getCachedTexture(Image image, WrapMode wrapMode) {
if (checkDisposed()) return null;
return getCachedTexture(image, wrapMode, false);
}
@Override
public Texture getCachedTexture(Image image, WrapMode wrapMode, boolean useMipmap) {
if (checkDisposed()) return null;
if (image == null) {
throw new IllegalArgumentException("Image must be non-null");
}
Map<Image,Texture> texCache;
if (wrapMode == WrapMode.CLAMP_TO_EDGE) {
if (useMipmap) {
throw new IllegalArgumentException("Mipmap not supported with CLAMP mode: useMipmap = "
+ useMipmap + ", wrapMode = " + wrapMode);
}
texCache = clampTexCache;
} else if (wrapMode == WrapMode.REPEAT) {
texCache = useMipmap ? mipmapTexCache : repeatTexCache;
} else {
throw new IllegalArgumentException("no caching for "+wrapMode);
}
Texture tex = texCache.get(image);
if (tex != null) {
tex.lock();
if (tex.isSurfaceLost()) {
texCache.remove(image);
tex = null;
}
}
if (!useMipmap && tex == null) {
Texture othertex = (wrapMode == WrapMode.REPEAT
? clampTexCache
: repeatTexCache).get(image);
if (othertex != null) {
othertex.lock();
if (!othertex.isSurfaceLost()) {
tex = othertex.getSharedTexture(wrapMode);
if (tex != null) {
tex.contentsUseful();
texCache.put(image, tex);
}
}
othertex.unlock();
}
}
Pair <Integer, Rectangle> idRect = image.getSerial().getIdRect();
if (tex == null) {
int w = image.getWidth();
int h = image.getHeight();
TextureResourcePool pool = getTextureResourcePool();
long size = useMipmap ? sizeWithMipMap(w, h, image.getPixelFormat())
: pool.estimateTextureSize(w, h, image.getPixelFormat());
if (!pool.prepareForAllocation(size)) {
return null;
}
tex = createTexture(image, Usage.DEFAULT, wrapMode, useMipmap);
if (tex != null) {
tex.setLastImageSerial(idRect.getKey());
texCache.put(image, tex);
}
} else if (tex.getLastImageSerial() != idRect.getKey()) {
if (idRect.getKey() - tex.getLastImageSerial() == 1 && idRect.getValue() != null) {
Rectangle dirtyRect = idRect.getValue();
tex.update(image.getPixelBuffer(), image.getPixelFormat(),
dirtyRect.x, dirtyRect.y, dirtyRect.x, dirtyRect.y,
dirtyRect.width, dirtyRect.height,
image.getScanlineStride(), false);
} else {
tex.update(image, 0, 0, image.getWidth(), image.getHeight(), false);
}
tex.setLastImageSerial(idRect.getKey());
}
return tex;
}
@Override
public Texture createTexture(Image image, Usage usageHint, WrapMode wrapMode) {
if (checkDisposed()) return null;
return createTexture(image, usageHint, wrapMode, false);
}
@Override
public Texture createTexture(Image image, Usage usageHint, WrapMode wrapMode,
boolean useMipmap) {
if (checkDisposed()) return null;
PixelFormat format = image.getPixelFormat();
int w = image.getWidth();
int h = image.getHeight();
Texture tex = createTexture(format, usageHint, wrapMode, w, h, useMipmap);
if (tex != null) {
tex.update(image, 0, 0, w, h, true);
tex.contentsUseful();
}
return tex;
}
@Override
public Texture createMaskTexture(int width, int height, WrapMode wrapMode) {
return createTexture(PixelFormat.BYTE_ALPHA,
Usage.DEFAULT, wrapMode,
width, height);
}
@Override
public Texture createFloatTexture(int width, int height) {
return createTexture(PixelFormat.FLOAT_XYZW,
Usage.DEFAULT, WrapMode.CLAMP_TO_ZERO,
width, height);
}
@Override
public void setRegionTexture(Texture texture) {
if (checkDisposed()) return;
regionTexture = texture;
superShaderAllowed = PrismSettings.superShader &&
regionTexture != null &&
glyphTexture != null;
}
@Override
public Texture getRegionTexture() {
return regionTexture;
}
@Override
public void setGlyphTexture(Texture texture) {
if (checkDisposed()) return;
glyphTexture = texture;
superShaderAllowed = PrismSettings.superShader &&
regionTexture != null &&
glyphTexture != null;
}
@Override
public Texture getGlyphTexture() {
return glyphTexture;
}
@Override
public boolean isSuperShaderAllowed() {
return superShaderAllowed;
}
protected boolean canClampToZero() {
return true;
}
protected boolean canClampToEdge() {
return true;
}
protected boolean canRepeat() {
return true;
}
@Override
public boolean isWrapModeSupported(WrapMode mode) {
switch (mode) {
case CLAMP_NOT_NEEDED:
return true;
case CLAMP_TO_EDGE:
return canClampToEdge();
case REPEAT:
return canRepeat();
case CLAMP_TO_ZERO:
return canClampToZero();
case CLAMP_TO_EDGE_SIMULATED:
case CLAMP_TO_ZERO_SIMULATED:
case REPEAT_SIMULATED:
throw new InternalError("Cannot test support for simulated wrap modes");
default:
throw new InternalError("Unrecognized wrap mode: "+mode);
}
}
@Override
public boolean isDisposed() {
return disposed;
}
protected boolean checkDisposed() {
if (PrismSettings.verbose && isDisposed()) {
try {
throw new IllegalStateException("attempt to use resource after factory is disposed");
} catch (RuntimeException ex) {
ex.printStackTrace();
}
}
return isDisposed();
}
}
