package com.sun.scenario.effect.impl.prism.sw;
import java.lang.reflect.Constructor;
import com.sun.glass.ui.Screen;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.prism.Graphics;
import com.sun.prism.GraphicsPipeline;
import com.sun.prism.Image;
import com.sun.prism.RTTexture;
import com.sun.prism.ResourceFactory;
import com.sun.prism.Texture;
import com.sun.prism.Texture.Usage;
import com.sun.prism.Texture.WrapMode;
import com.sun.scenario.effect.Effect.AccelType;
import com.sun.scenario.effect.FilterContext;
import com.sun.scenario.effect.Filterable;
import com.sun.scenario.effect.ImageData;
import com.sun.scenario.effect.impl.EffectPeer;
import com.sun.scenario.effect.impl.Renderer;
import com.sun.scenario.effect.impl.prism.PrDrawable;
import com.sun.scenario.effect.impl.prism.PrImage;
import com.sun.scenario.effect.impl.prism.PrRenderer;
import com.sun.scenario.effect.impl.sw.RendererDelegate;
import static com.sun.scenario.effect.impl.Renderer.RendererState.*;
public class PSWRenderer extends PrRenderer {
private final Screen screen;
private final ResourceFactory resourceFactory;
private final RendererDelegate delegate;
private RendererState state;
private PSWRenderer(Screen screen, RendererDelegate delegate) {
this.screen = screen;
this.resourceFactory = null;
this.delegate = delegate;
synchronized (this) {
state = OK;
}
}
private PSWRenderer(ResourceFactory factory, RendererDelegate delegate) {
this.screen = null;
this.resourceFactory = factory;
this.delegate = delegate;
synchronized (this) {
state = OK;
}
}
@Override
public PrDrawable createDrawable(RTTexture rtt) {
return PSWDrawable.create(rtt);
}
public synchronized static PSWRenderer createJSWInstance(Screen screen) {
PSWRenderer ret = null;
try {
Class klass = Class.forName(rootPkg + ".impl.sw.java.JSWRendererDelegate");
RendererDelegate delegate = (RendererDelegate)klass.getDeclaredConstructor().newInstance();
ret = new PSWRenderer(screen, delegate);
} catch (Throwable e) {}
return ret;
}
public synchronized static PSWRenderer createJSWInstance(ResourceFactory factory) {
PSWRenderer ret = null;
try {
Class klass = Class.forName(rootPkg + ".impl.sw.java.JSWRendererDelegate");
RendererDelegate delegate = (RendererDelegate)klass.getDeclaredConstructor().newInstance();
ret = new PSWRenderer(factory, delegate);
} catch (Throwable e) {}
return ret;
}
public synchronized static PSWRenderer createJSWInstance(FilterContext fctx) {
PSWRenderer ret = null;
try {
ResourceFactory factory = (ResourceFactory)fctx.getReferent();
ret = createJSWInstance(factory);
} catch (Throwable e) {}
return ret;
}
private synchronized static PSWRenderer createSSEInstance(Screen screen) {
PSWRenderer ret = null;
try {
Class klass = Class.forName(rootPkg + ".impl.sw.sse.SSERendererDelegate");
RendererDelegate delegate = (RendererDelegate)klass.getDeclaredConstructor().newInstance();
ret = new PSWRenderer(screen, delegate);
} catch (Throwable e) {}
return ret;
}
public static Renderer createRenderer(FilterContext fctx) {
Object ref = fctx.getReferent();
GraphicsPipeline pipe = GraphicsPipeline.getPipeline();
if (pipe == null || !(ref instanceof Screen)) {
return null;
}
Screen screen = (Screen)ref;
Renderer renderer = createSSEInstance(screen);
if (renderer == null) {
renderer = createJSWInstance(screen);
}
return renderer;
}
@Override
public AccelType getAccelType() {
return delegate.getAccelType();
}
@Override
public synchronized RendererState getRendererState() {
return state;
}
@Override
protected Renderer getBackupRenderer() {
return this;
}
protected void dispose() {
synchronized (this) {
state = DISPOSED;
}
}
protected final synchronized void markLost() {
if (state == OK) {
state = LOST;
}
}
@Override
public int getCompatibleWidth(int w) {
if (screen != null) {
return PSWDrawable.getCompatibleWidth(screen, w);
} else {
return resourceFactory.getRTTWidth(w, WrapMode.CLAMP_TO_EDGE);
}
}
@Override
public int getCompatibleHeight(int h) {
if (screen != null) {
return PSWDrawable.getCompatibleHeight(screen, h);
} else {
return resourceFactory.getRTTHeight(h, WrapMode.CLAMP_TO_EDGE);
}
}
@Override
public final PSWDrawable createCompatibleImage(int w, int h) {
if (screen != null) {
return PSWDrawable.create(screen, w, h);
} else {
RTTexture rtt =
resourceFactory.createRTTexture(w, h, WrapMode.CLAMP_TO_EDGE);
return PSWDrawable.create(rtt);
}
}
@Override
public PSWDrawable getCompatibleImage(int w, int h) {
PSWDrawable im = (PSWDrawable)super.getCompatibleImage(w, h);
if (im == null) {
markLost();
}
return im;
}
private EffectPeer createIntrinsicPeer(FilterContext fctx, String name) {
Class klass = null;
EffectPeer peer;
try {
klass = Class.forName(rootPkg + ".impl.prism.Pr" + name + "Peer");
Constructor ctor = klass.getConstructor(new Class[]
{ FilterContext.class, Renderer.class, String.class });
peer = (EffectPeer)ctor.newInstance(new Object[] {fctx, this, name});
} catch (Exception e) {
return null;
}
return peer;
}
private EffectPeer createPlatformPeer(FilterContext fctx, String name,
int unrollCount)
{
String klassName = delegate.getPlatformPeerName(name, unrollCount);
EffectPeer peer;
try {
Class klass = Class.forName(klassName);
Constructor ctor = klass.getConstructor(new Class[]
{ FilterContext.class, Renderer.class, String.class });
peer = (EffectPeer)ctor.newInstance(new Object[] {fctx, this, name});
} catch (Exception e) {
System.err.println("Error: " + getAccelType() +
" peer not found for: " + name +
" due to error: " + e.getMessage());
return null;
}
return peer;
}
@Override
protected EffectPeer createPeer(FilterContext fctx, String name,
int unrollCount)
{
if (PrRenderer.isIntrinsicPeer(name)) {
return createIntrinsicPeer(fctx, name);
} else {
return createPlatformPeer(fctx, name, unrollCount);
}
}
@Override
public boolean isImageDataCompatible(final ImageData id) {
return (getRendererState() == OK &&
id.getUntransformedImage() instanceof PSWDrawable);
}
@Override
public void clearImage(Filterable filterable) {
PSWDrawable img = (PSWDrawable)filterable;
img.clear();
}
@Override
public ImageData createImageData(FilterContext fctx, Filterable src) {
if (!(src instanceof PrImage)) {
throw new IllegalArgumentException("Identity source must be PrImage");
}
Image img = ((PrImage)src).getImage();
int w = img.getWidth();
int h = img.getHeight();
PSWDrawable dst = createCompatibleImage(w, h);
if (dst == null) {
return null;
}
Graphics g = dst.createGraphics();
ResourceFactory factory = g.getResourceFactory();
Texture tex =
factory.createTexture(img, Usage.DEFAULT, WrapMode.CLAMP_TO_EDGE);
g.drawTexture(tex, 0, 0, w, h);
g.sync();
tex.dispose();
return new ImageData(fctx, dst, new Rectangle(w, h));
}
@Override
public Filterable transform(FilterContext fctx,
Filterable original,
BaseTransform transform,
Rectangle origBounds,
Rectangle xformBounds)
{
PSWDrawable dst = (PSWDrawable)
getCompatibleImage(xformBounds.width, xformBounds.height);
if (dst != null) {
Graphics g = dst.createGraphics();
g.translate(-xformBounds.x, -xformBounds.y);
g.transform(transform);
g.drawTexture(((PSWDrawable)original).getTextureObject(),
origBounds.x, origBounds.y,
origBounds.width, origBounds.height);
}
return dst;
}
@Override
public ImageData transform(FilterContext fctx, ImageData original,
BaseTransform transform,
Rectangle origBounds,
Rectangle xformBounds)
{
PSWDrawable dst = (PSWDrawable)
getCompatibleImage(xformBounds.width, xformBounds.height);
if (dst != null) {
PSWDrawable orig = (PSWDrawable)original.getUntransformedImage();
Graphics g = dst.createGraphics();
g.translate(-xformBounds.x, -xformBounds.y);
g.transform(transform);
g.drawTexture(orig.getTextureObject(),
origBounds.x, origBounds.y,
origBounds.width, origBounds.height);
}
original.unref();
return new ImageData(fctx, dst, xformBounds);
}
}
