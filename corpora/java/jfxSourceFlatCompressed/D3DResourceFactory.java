package com.sun.prism.d3d;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import com.sun.glass.ui.Screen;
import com.sun.prism.Image;
import com.sun.prism.MediaFrame;
import com.sun.prism.Mesh;
import com.sun.prism.MeshView;
import com.sun.prism.MultiTexture;
import com.sun.prism.PhongMaterial;
import com.sun.prism.PixelFormat;
import com.sun.prism.Presentable;
import com.sun.prism.PresentableState;
import com.sun.prism.Texture;
import com.sun.prism.Texture.Usage;
import com.sun.prism.Texture.WrapMode;
import com.sun.prism.d3d.D3DResource.D3DRecord;
import com.sun.prism.impl.PrismSettings;
import com.sun.prism.impl.ps.BaseShaderFactory;
import com.sun.prism.impl.TextureResourcePool;
import com.sun.prism.ps.Shader;
import com.sun.prism.ps.ShaderFactory;
import java.util.WeakHashMap;
class D3DResourceFactory extends BaseShaderFactory {
private static final Map<Image,Texture> clampTexCache = new WeakHashMap<>();
private static final Map<Image,Texture> repeatTexCache = new WeakHashMap<>();
private static final Map<Image,Texture> mipmapTexCache = new WeakHashMap<>();
private final D3DContext context;
private final int maxTextureSize;
private final LinkedList<D3DResource.D3DRecord> records =
new LinkedList<D3DResource.D3DRecord>();
D3DResourceFactory(long pContext, Screen screen) {
super(clampTexCache, repeatTexCache, mipmapTexCache);
context = new D3DContext(pContext, screen, this);
context.initState();
maxTextureSize = computeMaxTextureSize();
if (PrismSettings.noClampToZero && PrismSettings.verbose) {
System.out.println("prism.noclamptozero not supported by D3D");
}
}
D3DContext getContext() {
return context;
}
@Override
public TextureResourcePool getTextureResourcePool() {
return D3DVramPool.instance;
}
static final int STATS_FREQUENCY = PrismSettings.prismStatFrequency;
private int nFrame = -1;
private D3DFrameStats frameStats;
private void displayPrismStatistics() {
if (STATS_FREQUENCY > 0) {
if (++nFrame == STATS_FREQUENCY) {
nFrame = 0;
frameStats = context.getFrameStats(true, frameStats);
if (frameStats != null) {
System.err.println(frameStats.toDebugString(STATS_FREQUENCY));
}
}
}
}
@Override
public boolean isDeviceReady() {
if (isDisposed()) {
return false;
}
displayPrismStatistics();
return context.testLostStateAndReset();
}
static int nextPowerOfTwo(int val, int max) {
if (val > max) {
return 0;
}
int i = 1;
while (i < val) {
i *= 2;
}
return i;
}
@Override
public boolean isCompatibleTexture(Texture tex) {
return tex instanceof D3DTexture;
}
@Override
public D3DTexture createTexture(PixelFormat format, Usage usagehint,
WrapMode wrapMode, int w, int h) {
return createTexture(format, usagehint, wrapMode, w, h, false);
}
@Override
public D3DTexture createTexture(PixelFormat format, Usage usagehint,
WrapMode wrapMode, int w, int h, boolean useMipmap) {
if (checkDisposed()) return null;
if (!isFormatSupported(format)) {
throw new UnsupportedOperationException(
"Pixel format " + format +
" not supported on this device");
}
if (format == PixelFormat.MULTI_YCbCr_420) {
throw new UnsupportedOperationException("MULTI_YCbCr_420 textures require a MediaFrame");
}
int allocw, alloch;
if (PrismSettings.forcePow2) {
allocw = nextPowerOfTwo(w, Integer.MAX_VALUE);
alloch = nextPowerOfTwo(h, Integer.MAX_VALUE);
} else {
allocw = w;
alloch = h;
}
D3DVramPool pool = D3DVramPool.instance;
long size = pool.estimateTextureSize(allocw, alloch, format);
if (!pool.prepareForAllocation(size)) {
return null;
}
long pResource = nCreateTexture(context.getContextHandle(),
format.ordinal(), usagehint.ordinal(),
false , allocw, alloch, 0, useMipmap);
if (pResource == 0L) {
return null;
}
int texw = nGetTextureWidth(pResource);
int texh = nGetTextureHeight(pResource);
if (wrapMode != WrapMode.CLAMP_NOT_NEEDED && (w < texw || h < texh)) {
wrapMode = wrapMode.simulatedVersion();
}
return new D3DTexture(context, format, wrapMode, pResource, texw, texh, w, h, useMipmap);
}
@Override
public Texture createTexture(MediaFrame frame) {
if (checkDisposed()) return null;
frame.holdFrame();
int width = frame.getWidth();
int height = frame.getHeight();
int texWidth = frame.getEncodedWidth();
int texHeight = frame.getEncodedHeight();
PixelFormat texFormat = frame.getPixelFormat();
if (texFormat == PixelFormat.MULTI_YCbCr_420) {
MultiTexture tex = new MultiTexture(texFormat, WrapMode.CLAMP_TO_EDGE, width, height);
for (int index = 0; index < frame.planeCount(); index++) {
int subWidth = texWidth;
int subHeight = texHeight;
if (index == PixelFormat.YCBCR_PLANE_CHROMABLUE
|| index == PixelFormat.YCBCR_PLANE_CHROMARED)
{
subWidth /= 2;
subHeight /= 2;
}
D3DTexture subTex = createTexture(PixelFormat.BYTE_ALPHA, Usage.DYNAMIC, WrapMode.CLAMP_TO_EDGE,
subWidth, subHeight);
if (subTex == null) {
tex.dispose();
return null;
}
tex.setTexture(subTex, index);
}
frame.releaseFrame();
return tex;
} else {
D3DVramPool pool = D3DVramPool.instance;
long size = pool.estimateTextureSize(texWidth, texHeight, texFormat);
if (!pool.prepareForAllocation(size)) {
return null;
}
long pResource = nCreateTexture(context.getContextHandle(),
texFormat.ordinal(), Usage.DYNAMIC.ordinal(),
false, texWidth, texHeight, 0, false);
if (0 == pResource) {
return null;
}
int physWidth = nGetTextureWidth(pResource);
int physHeight = nGetTextureHeight(pResource);
WrapMode wrapMode = (texWidth < physWidth || texHeight < physHeight)
? WrapMode.CLAMP_TO_EDGE_SIMULATED : WrapMode.CLAMP_TO_EDGE;
D3DTexture tex = new D3DTexture(context, texFormat, wrapMode, pResource,
physWidth, physHeight, width, height, false);
frame.releaseFrame();
return tex;
}
}
@Override
public int getRTTWidth(int w, WrapMode wrapMode) {
return w;
}
@Override
public int getRTTHeight(int h, WrapMode wrapMode) {
return h;
}
@Override
public D3DRTTexture createRTTexture(int width, int height, WrapMode wrapMode) {
return createRTTexture(width, height, wrapMode, false);
}
@Override
public D3DRTTexture createRTTexture(int width, int height, WrapMode wrapMode, boolean msaa) {
if (checkDisposed()) return null;
if (PrismSettings.verbose && context.isLost()) {
System.err.println("RT Texture allocation while the device is lost");
}
int createw = width;
int createh = height;
int cx = 0;
int cy = 0;
if (PrismSettings.forcePow2) {
createw = nextPowerOfTwo(createw, Integer.MAX_VALUE);
createh = nextPowerOfTwo(createh, Integer.MAX_VALUE);
}
D3DVramPool pool = D3DVramPool.instance;
int aaSamples;
if (msaa) {
int maxSamples = D3DPipeline.getInstance().getMaxSamples();
aaSamples = maxSamples < 2 ? 0 : (maxSamples < 4 ? 2 : 4);
} else {
aaSamples = 0;
}
long size = pool.estimateRTTextureSize(width, height, false);
if (!pool.prepareForAllocation(size)) {
return null;
}
long pResource = nCreateTexture(context.getContextHandle(),
PixelFormat.INT_ARGB_PRE.ordinal(),
Usage.DEFAULT.ordinal(),
true , createw, createh, aaSamples, false);
if (pResource == 0L) {
return null;
}
int texw = nGetTextureWidth(pResource);
int texh = nGetTextureHeight(pResource);
D3DRTTexture rtt = new D3DRTTexture(context, wrapMode, pResource, texw, texh,
cx, cy, width, height, aaSamples);
rtt.createGraphics().clear();
return rtt;
}
@Override
public Presentable createPresentable(PresentableState pState) {
if (checkDisposed()) return null;
if (PrismSettings.verbose && context.isLost()) {
System.err.println("SwapChain allocation while the device is lost");
}
long pResource = nCreateSwapChain(context.getContextHandle(),
pState.getNativeView(),
PrismSettings.isVsyncEnabled);
if (pResource != 0L) {
int width = pState.getRenderWidth();
int height = pState.getRenderHeight();
D3DRTTexture rtt = createRTTexture(width, height, WrapMode.CLAMP_NOT_NEEDED, pState.isMSAA());
if (PrismSettings.dirtyOptsEnabled) {
rtt.contentsUseful();
}
if (rtt != null) {
return new D3DSwapChain(context, pResource, rtt, pState.getRenderScaleX(), pState.getRenderScaleY());
}
D3DResourceFactory.nReleaseResource(context.getContextHandle(), pResource);
}
return null;
}
private static ByteBuffer getBuffer(InputStream is) {
if (is == null) {
throw new RuntimeException("InputStream must be non-null");
}
try {
int len = 4096;
byte[] data = new byte[len];
BufferedInputStream bis = new BufferedInputStream(is, len);
int offset = 0;
int readBytes = -1;
while ((readBytes = bis.read(data, offset, len - offset)) != -1) {
offset += readBytes;
if (len - offset == 0) {
len *= 2;
byte[] newdata = new byte[len];
System.arraycopy(data, 0, newdata, 0, data.length);
data = newdata;
}
}
bis.close();
ByteBuffer buf = ByteBuffer.allocateDirect(offset);
buf.put(data, 0, offset);
return buf;
} catch (IOException e) {
throw new RuntimeException("Error loading D3D shader object", e);
}
}
@Override
public Shader createShader(InputStream pixelShaderCode,
Map<String, Integer> samplers,
Map<String, Integer> params,
int maxTexCoordIndex,
boolean isPixcoordUsed,
boolean isPerVertexColorUsed)
{
if (checkDisposed()) return null;
long shaderHandle = D3DShader.init(
context.getContextHandle(), getBuffer(pixelShaderCode),
maxTexCoordIndex, isPixcoordUsed, isPerVertexColorUsed);
return new D3DShader(context, shaderHandle, params);
}
@Override
public Shader createStockShader(final String name) {
if (name == null) {
throw new IllegalArgumentException("Shader name must be non-null");
}
try {
@SuppressWarnings("removal")
InputStream stream = AccessController.doPrivileged(
(PrivilegedAction<InputStream>) () -> D3DResourceFactory.class.
getResourceAsStream("hlsl/" + name + ".obj")
);
Class klass = Class.forName("com.sun.prism.shader." + name + "_Loader");
Method m = klass.getMethod("loadShader",
new Class[] { ShaderFactory.class, InputStream.class });
return (Shader)m.invoke(null, new Object[] { this, stream });
} catch (Throwable e) {
e.printStackTrace();
throw new InternalError("Error loading stock shader " + name);
}
}
@Override
public boolean isFormatSupported(PixelFormat format) {
return true;
}
private int computeMaxTextureSize() {
int size = nGetMaximumTextureSize(context.getContextHandle());
if (PrismSettings.verbose) {
System.err.println("Maximum supported texture size: " + size);
}
if (size > PrismSettings.maxTextureSize) {
size = PrismSettings.maxTextureSize;
if (PrismSettings.verbose) {
System.err.println("Maximum texture size clamped to " + size);
}
}
return size;
}
@Override
public int getMaximumTextureSize() {
return maxTextureSize;
}
@Override
protected void notifyReset() {
for (ListIterator<D3DRecord> it = records.listIterator(); it.hasNext();) {
D3DRecord r = it.next();
if (r.isDefaultPool()) {
r.markDisposed();
it.remove();
}
}
super.notifyReset();
}
@Override
public void dispose() {
context.dispose();
for (ListIterator<D3DRecord> it = records.listIterator(); it.hasNext();) {
D3DRecord r = it.next();
r.markDisposed();
}
records.clear();
super.dispose();
}
void addRecord(D3DRecord record) {
records.add(record);
}
void removeRecord(D3DRecord record) {
records.remove(record);
}
@Override
public PhongMaterial createPhongMaterial() {
if (checkDisposed()) return null;
return D3DPhongMaterial.create(context);
}
@Override
public MeshView createMeshView(Mesh mesh) {
if (checkDisposed()) return null;
return D3DMeshView.create(context, (D3DMesh) mesh);
}
@Override
public Mesh createMesh() {
if (checkDisposed()) return null;
return D3DMesh.create(context);
}
static native long nGetContext(int adapterOrdinal);
static native boolean nIsDefaultPool(long pResource);
static native int nTestCooperativeLevel(long pContext);
static native int nResetDevice(long pContext);
static native long nCreateTexture(long pContext,
int format, int hint,
boolean isRTT,
int width, int height, int samples,
boolean useMipmap);
static native long nCreateSwapChain(long pContext, long hwnd,
boolean isVsyncEnabled);
static native int nReleaseResource(long pContext, long resource);
static native int nGetMaximumTextureSize(long pContext);
static native int nGetTextureWidth(long pResource);
static native int nGetTextureHeight(long pResource);
static native int nReadPixelsI(long pContext, long pResource,
long length,
Buffer pixels, int[] arr,
int contentWidth, int contentHeight);
static native int nReadPixelsB(long pContext, long pResource,
long length,
Buffer pixels, byte[] arr,
int contentWidth, int contentHeight);
static native int nUpdateTextureI(long contextHandle, long pResource,
IntBuffer buf, int[] pixels,
int dstx, int dsty,
int srcx, int srcy,
int srcw, int srch, int srcscan);
static native int nUpdateTextureF(long contextHandle, long pResource,
FloatBuffer buf, float[] pixels,
int dstx, int dsty,
int srcx, int srcy,
int srcw, int srch, int srcscan);
static native int nUpdateTextureB(long contextHandle, long pResource,
ByteBuffer buf, byte[] pixels,
int formatHint,
int dstx, int dsty,
int srcx, int srcy,
int srcw, int srch, int srcscan);
static native long nGetDevice(long pContext);
static native long nGetNativeTextureObject(long pResource);
}
