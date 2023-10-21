package com.sun.scenario.effect.impl.prism.sw;
import java.nio.IntBuffer;
import java.util.Arrays;
import com.sun.glass.ui.Screen;
import com.sun.prism.Graphics;
import com.sun.prism.GraphicsPipeline;
import com.sun.prism.Image;
import com.sun.prism.RTTexture;
import com.sun.prism.ResourceFactory;
import com.sun.prism.Texture;
import com.sun.prism.Texture.Usage;
import com.sun.prism.Texture.WrapMode;
import com.sun.scenario.effect.impl.HeapImage;
import com.sun.scenario.effect.impl.prism.PrDrawable;
public class PSWDrawable extends PrDrawable implements HeapImage {
private RTTexture rtt;
private Image image;
private boolean heapDirty;
private boolean vramDirty;
private PSWDrawable(RTTexture rtt, boolean isDirty) {
super(rtt);
this.rtt = rtt;
vramDirty = isDirty;
}
public static PSWDrawable create(RTTexture rtt) {
return new PSWDrawable(rtt, true);
}
static int getCompatibleWidth(Screen screen, int w) {
ResourceFactory factory =
GraphicsPipeline.getPipeline().getResourceFactory(screen);
return factory.getRTTWidth(w, WrapMode.CLAMP_TO_ZERO);
}
static int getCompatibleHeight(Screen screen, int h) {
ResourceFactory factory =
GraphicsPipeline.getPipeline().getResourceFactory(screen);
return factory.getRTTHeight(h, WrapMode.CLAMP_TO_ZERO);
}
static PSWDrawable create(Screen screen, int width, int height) {
ResourceFactory factory =
GraphicsPipeline.getPipeline().getResourceFactory(screen);
RTTexture rtt =
factory.createRTTexture(width, height, WrapMode.CLAMP_TO_ZERO);
return new PSWDrawable(rtt, false);
}
public boolean isLost() {
return rtt == null || rtt.isSurfaceLost();
}
public void flush() {
if (rtt != null) {
rtt.dispose();
rtt = null;
image = null;
}
}
public Object getData() {
return this;
}
public int getContentWidth() {
return rtt.getContentWidth();
}
public int getContentHeight() {
return rtt.getContentHeight();
}
public int getMaxContentWidth() {
return rtt.getMaxContentWidth();
}
public int getMaxContentHeight() {
return rtt.getMaxContentHeight();
}
public void setContentWidth(int contentW) {
rtt.setContentWidth(contentW);
}
public void setContentHeight(int contentH) {
rtt.setContentHeight(contentH);
}
public int getPhysicalWidth() {
return rtt.getContentWidth();
}
public int getPhysicalHeight() {
return rtt.getContentHeight();
}
public int getScanlineStride() {
return rtt.getContentWidth();
}
public int[] getPixelArray() {
int pixels[] = rtt.getPixels();
if (pixels != null) {
return pixels;
}
if (image == null) {
int width = rtt.getContentWidth();
int height = rtt.getContentHeight();
pixels = new int[width*height];
image = Image.fromIntArgbPreData(pixels, width, height);
}
IntBuffer buf = (IntBuffer)image.getPixelBuffer();
if (vramDirty) {
rtt.readPixels(buf);
vramDirty = false;
}
heapDirty = true;
return buf.array();
}
@Override
public RTTexture getTextureObject() {
if (heapDirty) {
int width = rtt.getContentWidth();
int height = rtt.getContentHeight();
Screen screen = rtt.getAssociatedScreen();
ResourceFactory factory =
GraphicsPipeline.getPipeline().getResourceFactory(screen);
Texture tex =
factory.createTexture(image, Usage.DEFAULT, WrapMode.CLAMP_TO_EDGE);
Graphics g = createGraphics();
g.drawTexture(tex, 0, 0, width, height);
g.sync();
tex.dispose();
heapDirty = false;
}
return rtt;
}
public Graphics createGraphics() {
vramDirty = true;
return (Graphics)rtt.createGraphics();
}
@Override
public void clear() {
Graphics g = createGraphics();
g.clear();
if (image != null) {
IntBuffer buf = (IntBuffer)image.getPixelBuffer();
Arrays.fill(buf.array(), 0);
}
heapDirty = false;
vramDirty = false;
}
}
