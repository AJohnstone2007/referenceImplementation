package com.sun.javafx.media;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.WeakHashMap;
import com.sun.glass.ui.Screen;
import com.sun.javafx.tk.RenderJob;
import com.sun.javafx.tk.Toolkit;
import com.sun.media.jfxmedia.control.VideoDataBuffer;
import com.sun.media.jfxmedia.control.VideoFormat;
import com.sun.prism.Graphics;
import com.sun.prism.GraphicsPipeline;
import com.sun.prism.MediaFrame;
import com.sun.prism.PixelFormat;
import com.sun.prism.ResourceFactory;
import com.sun.prism.ResourceFactoryListener;
import com.sun.prism.Texture;
public class PrismMediaFrameHandler implements ResourceFactoryListener {
private final Map<Screen, TextureMapEntry> textures = new WeakHashMap<>(1);
private static Map<Object, PrismMediaFrameHandler> handlers;
public synchronized static PrismMediaFrameHandler getHandler(Object provider) {
if (provider == null) {
throw new IllegalArgumentException("provider must be non-null");
}
if (handlers == null) {
handlers = new WeakHashMap<>(1);
}
PrismMediaFrameHandler ret = handlers.get(provider);
if (ret == null) {
ret = new PrismMediaFrameHandler(provider);
handlers.put(provider, ret);
}
return ret;
}
private WeakReference<ResourceFactory> registeredWithFactory = null;
private PrismMediaFrameHandler(Object provider) {
}
public Texture getTexture(Graphics g, VideoDataBuffer currentFrame) {
Screen screen = g.getAssociatedScreen();
TextureMapEntry tme = textures.get(screen);
if (null == currentFrame) {
if (textures.containsKey(screen)) {
textures.remove(screen);
}
return null;
}
if (null == tme) {
tme = new TextureMapEntry();
textures.put(screen, tme);
}
if (tme.texture != null) {
tme.texture.lock();
if (tme.texture.isSurfaceLost()) {
tme.texture = null;
}
}
if (null == tme.texture || tme.lastFrameTime != currentFrame.getTimestamp()) {
updateTexture(g, currentFrame, tme);
}
return tme.texture;
}
private void updateTexture(Graphics g, VideoDataBuffer vdb, TextureMapEntry tme)
{
Screen screen = g.getAssociatedScreen();
if (tme.texture != null &&
(tme.encodedWidth != vdb.getEncodedWidth() ||
tme.encodedHeight != vdb.getEncodedHeight()))
{
tme.texture.dispose();
tme.texture = null;
}
PrismFrameBuffer prismBuffer = new PrismFrameBuffer(vdb);
if (tme.texture == null) {
ResourceFactory factory = GraphicsPipeline.getDefaultResourceFactory();
if (registeredWithFactory == null || registeredWithFactory.get() != factory) {
factory.addFactoryListener(this);
registeredWithFactory = new WeakReference<>(factory);
}
tme.texture = GraphicsPipeline.getPipeline().
getResourceFactory(screen).
createTexture(prismBuffer);
tme.encodedWidth = vdb.getEncodedWidth();
tme.encodedHeight = vdb.getEncodedHeight();
}
if (tme.texture != null) {
tme.texture.update(prismBuffer, false);
}
tme.lastFrameTime = vdb.getTimestamp();
}
private void releaseData() {
for (TextureMapEntry tme : textures.values()) {
if (tme != null && tme.texture != null) {
tme.texture.dispose();
}
}
textures.clear();
}
private final RenderJob releaseRenderJob = new RenderJob(() -> {
releaseData();
});
public void releaseTextures() {
Toolkit tk = Toolkit.getToolkit();
tk.addRenderJob(releaseRenderJob);
}
public void factoryReset() {
releaseData();
}
public void factoryReleased() {
releaseData();
}
private class PrismFrameBuffer implements MediaFrame {
private final PixelFormat videoFormat;
private final VideoDataBuffer primary;
public PrismFrameBuffer(VideoDataBuffer sourceBuffer) {
if (null == sourceBuffer) {
throw new NullPointerException();
}
primary = sourceBuffer;
switch (primary.getFormat()) {
case BGRA_PRE:
videoFormat = PixelFormat.INT_ARGB_PRE;
break;
case YCbCr_420p:
videoFormat = PixelFormat.MULTI_YCbCr_420;
break;
case YCbCr_422:
videoFormat = PixelFormat.BYTE_APPLE_422;
break;
case ARGB:
default:
throw new IllegalArgumentException("Unsupported video format "+primary.getFormat());
}
}
@Override
public ByteBuffer getBufferForPlane(int plane) {
return primary.getBufferForPlane(plane);
}
@Override
public void holdFrame() {
primary.holdFrame();
}
@Override
public void releaseFrame() {
primary.releaseFrame();
}
@Override
public PixelFormat getPixelFormat() {
return videoFormat;
}
@Override
public int getWidth() {
return primary.getWidth();
}
@Override
public int getHeight() {
return primary.getHeight();
}
@Override
public int getEncodedWidth() {
return primary.getEncodedWidth();
}
@Override
public int getEncodedHeight() {
return primary.getEncodedHeight();
}
@Override
public int planeCount() {
return primary.getPlaneCount();
}
@Override
public int[] planeStrides() {
return primary.getPlaneStrides();
}
@Override
public int strideForPlane(int planeIndex) {
return primary.getStrideForPlane(planeIndex);
}
@Override
public MediaFrame convertToFormat(PixelFormat fmt) {
if (fmt == getPixelFormat()) {
return this;
}
if (fmt != PixelFormat.INT_ARGB_PRE) {
return null;
}
VideoDataBuffer newVDB = primary.convertToFormat(VideoFormat.BGRA_PRE);
if (null == newVDB) {
return null;
}
return new PrismFrameBuffer(newVDB);
}
}
private static class TextureMapEntry {
public double lastFrameTime = -1;
public Texture texture;
public int encodedWidth;
public int encodedHeight;
}
}
