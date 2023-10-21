package com.sun.javafx.sg.prism.web;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.sg.prism.NGGroup;
import com.sun.prism.Graphics;
import com.sun.prism.PrinterGraphics;
import com.sun.webkit.WebPage;
import com.sun.webkit.graphics.WCGraphicsContext;
import com.sun.webkit.graphics.WCGraphicsManager;
import com.sun.webkit.graphics.WCRectangle;
public final class NGWebView extends NGGroup {
private final static PlatformLogger log =
PlatformLogger.getLogger(NGWebView.class.getName());
private volatile WebPage page;
private volatile float width, height;
public void setPage(WebPage page) {
this.page = page;
}
public void resize(float w, float h) {
if (width != w || height != h) {
width = w;
height = h;
geometryChanged();
if (page != null) {
page.setBounds(0, 0, (int)w, (int)h);
}
}
}
public void update() {
if (page != null) {
BaseBounds clip = getClippedBounds(new RectBounds(), BaseTransform.IDENTITY_TRANSFORM);
if (!clip.isEmpty()) {
log.finest("updating rectangle: {0}", clip);
page.updateContent(new WCRectangle(clip.getMinX(), clip.getMinY(),
clip.getWidth(), clip.getHeight()));
}
}
}
public void requestRender() {
visualsChanged();
}
@Override protected void renderContent(Graphics g) {
log.finest("rendering into {0}", g);
if (g == null || page == null || width <= 0 || height <= 0)
return;
WCGraphicsContext gc =
WCGraphicsManager.getGraphicsManager().createGraphicsContext(g);
try {
if (g instanceof PrinterGraphics) {
page.print(gc, 0, 0, (int) width, (int) height);
} else {
page.paint(gc, 0, 0, (int) width, (int) height);
}
gc.flush();
} finally {
gc.dispose();
}
}
@Override public boolean hasOverlappingContents() {
return false;
}
@Override protected boolean hasVisuals() {
return true;
}
}
