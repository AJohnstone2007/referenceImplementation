package com.sun.prism.d3d;
import com.sun.glass.ui.Screen;
import com.sun.prism.Graphics;
import com.sun.prism.Image;
import com.sun.prism.PixelFormat;
import com.sun.prism.RTTexture;
import com.sun.prism.ReadbackRenderTarget;
import com.sun.prism.Texture;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
class D3DRTTexture extends D3DTexture
implements D3DRenderTarget, RTTexture, ReadbackRenderTarget
{
private boolean opaque;
D3DRTTexture(D3DContext context, WrapMode wrapMode, long pResource,
int physicalWidth, int physicalHeight,
int contentWidth, int contentHeight)
{
super(context, PixelFormat.INT_ARGB_PRE, wrapMode, pResource,
physicalWidth, physicalHeight,
contentWidth, contentHeight, true);
this.opaque = false;
}
D3DRTTexture(D3DContext context, WrapMode wrapMode, long pResource,
int physicalWidth, int physicalHeight,
int contentX, int contentY,
int contentWidth, int contentHeight,
int samples)
{
super(context, PixelFormat.INT_ARGB_PRE, wrapMode, pResource,
physicalWidth, physicalHeight,
contentX, contentY, contentWidth, contentHeight, true, samples, false);
this.opaque = false;
}
@Override
public Texture getBackBuffer() {
return this;
}
@Override
public long getResourceHandle() {
return resource.getResource().getResource();
}
@Override
public Graphics createGraphics() {
return D3DGraphics.create(this, getContext());
}
@Override
public int[] getPixels() {
return null;
}
@Override
public boolean readPixels(Buffer pixels, int x, int y, int width, int height) {
if (x != getContentX() || y != getContentY()
|| width != getContentWidth() || height != getContentHeight())
{
throw new IllegalArgumentException("reading subtexture not yet supported!");
}
return readPixels(pixels);
}
@Override
public boolean readPixels(Buffer pixels) {
final D3DContext context = getContext();
if (context.isDisposed()) {
return false;
}
context.flushVertexBuffer();
long ctx = getContext().getContextHandle();
int res = D3DContext.D3D_OK;
if (pixels instanceof ByteBuffer) {
ByteBuffer buf = (ByteBuffer) pixels;
byte[] arr = buf.hasArray() ? buf.array() : null;
long length = buf.capacity();
res = D3DResourceFactory.nReadPixelsB(ctx, getNativeSourceHandle(),
length, pixels, arr,
getContentWidth(), getContentHeight());
} else if (pixels instanceof IntBuffer) {
IntBuffer buf = (IntBuffer) pixels;
int[] arr = buf.hasArray() ? buf.array() : null;
long length = buf.capacity()*4;
res = D3DResourceFactory.nReadPixelsI(ctx, getNativeSourceHandle(),
length, pixels, arr,
getContentWidth(), getContentHeight());
} else {
throw new IllegalArgumentException("Buffer of this type is " +
"not supported: "+pixels);
}
return context.validatePresent(res);
}
@Override
public Screen getAssociatedScreen() {
return getContext().getAssociatedScreen();
}
@Override
public void update(Image img) {
throw new UnsupportedOperationException("update() not supported for RTTextures");
}
@Override
public void update(Image img, int dstx, int dsty) {
throw new UnsupportedOperationException("update() not supported for RTTextures");
}
@Override
public void update(Image img, int dstx, int dsty, int w, int h) {
throw new UnsupportedOperationException("update() not supported for RTTextures");
}
@Override
public void update(Image img, int dstx, int dsty, int w, int h, boolean skipFlush) {
throw new UnsupportedOperationException("update() not supported for RTTextures");
}
@Override
public void update(Buffer pixels, PixelFormat format,
int dstx, int dsty,
int srcx, int srcy, int srcw, int srch, int srcscan,
boolean skipFlush)
{
throw new UnsupportedOperationException("update() not supported for RTTextures");
}
@Override
public void setOpaque(boolean isOpaque) {
this.opaque = isOpaque;
}
@Override
public boolean isOpaque() {
return opaque;
}
@Override
public boolean isVolatile() {
return getContext().isRTTVolatile();
}
@Override
public boolean isMSAA() {
return resource.getResource().getSamples() != 0;
}
}
