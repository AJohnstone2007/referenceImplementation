package com.sun.javafx.sg.prism;
import javafx.scene.layout.Background;
import java.util.HashMap;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.Shape;
import com.sun.javafx.logging.PulseLogger;
import static com.sun.javafx.logging.PulseLogger.PULSE_LOGGING_ENABLED;
import com.sun.prism.Graphics;
import com.sun.prism.RTTexture;
import com.sun.prism.ResourceFactory;
import com.sun.prism.Texture.WrapMode;
import com.sun.prism.impl.packrect.RectanglePacker;
class RegionImageCache {
private final static int MAX_SIZE = 300 * 300;
private static final int WIDTH = 1024;
private static final int HEIGHT = 1024;
private HashMap<Integer, CachedImage> imageMap;
private RTTexture backingStore;
private RectanglePacker hPacker;
private RectanglePacker vPacker;
RegionImageCache(final ResourceFactory factory) {
imageMap = new HashMap<>();
WrapMode mode;
int pad;
if (factory.isWrapModeSupported(WrapMode.CLAMP_TO_ZERO)) {
mode = WrapMode.CLAMP_TO_ZERO;
pad = 0;
} else {
mode = WrapMode.CLAMP_NOT_NEEDED;
pad = 1;
}
backingStore = factory.createRTTexture(WIDTH + WIDTH, HEIGHT, mode);
backingStore.contentsUseful();
backingStore.makePermanent();
factory.setRegionTexture(backingStore);
hPacker = new RectanglePacker(backingStore, pad, pad, WIDTH-pad, HEIGHT-pad, false);
vPacker = new RectanglePacker(backingStore, WIDTH, pad, WIDTH, HEIGHT-pad, true);
}
boolean isImageCachable(int w, int h) {
return 0 < w && w < WIDTH &&
0 < h && h < HEIGHT &&
(w * h) < MAX_SIZE;
}
RTTexture getBackingStore() {
return backingStore;
}
boolean getImageLocation(Integer key, Rectangle rect, Background background,
Shape shape, Graphics g) {
CachedImage cache = imageMap.get(key);
if (cache != null) {
if (cache.equals(rect.width, rect.height, background, shape)) {
rect.x = cache.x;
rect.y = cache.y;
return false;
}
rect.width = rect.height = -1;
return false;
}
boolean vertical = rect.height > 64;
RectanglePacker packer = vertical ? vPacker : hPacker;
if (!packer.add(rect)) {
g.sync();
vPacker.clear();
hPacker.clear();
imageMap.clear();
packer.add(rect);
backingStore.createGraphics().clear();
if (PULSE_LOGGING_ENABLED) {
PulseLogger.incrementCounter("Region image cache flushed");
}
}
imageMap.put(key, new CachedImage(rect, background, shape));
return true;
}
static class CachedImage {
Background background;
Shape shape;
int x, y, width, height;
CachedImage(Rectangle rect, Background background, Shape shape) {
this.x = rect.x;
this.y = rect.y;
this.width = rect.width;
this.height = rect.height;
this.background = background;
this.shape = shape;
}
public boolean equals(int width, int height, Background background, Shape shape) {
return this.width == width &&
this.height == height &&
(this.background == null ? background == null : this.background.equals(background)) &&
(this.shape == null ? shape == null : this.shape.equals(shape));
}
}
}
