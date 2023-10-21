package com.sun.javafx.webkit.theme;
import java.lang.ref.WeakReference;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.logging.PlatformLogger.Level;
import javafx.beans.Observable;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.ScrollBar;
import com.sun.webkit.graphics.Ref;
import com.sun.webkit.graphics.ScrollBarTheme;
import com.sun.webkit.graphics.WCGraphicsContext;
import com.sun.javafx.webkit.Accessor;
import com.sun.javafx.webkit.theme.RenderThemeImpl.Pool;
import com.sun.javafx.webkit.theme.RenderThemeImpl.ViewListener;
import com.sun.webkit.graphics.WCSize;
public final class ScrollBarThemeImpl extends ScrollBarTheme {
private final static PlatformLogger log = PlatformLogger.getLogger(ScrollBarThemeImpl.class.getName());
private WeakReference<ScrollBar> testSBRef =
new WeakReference<ScrollBar>(null);
private final Accessor accessor;
private final Pool<ScrollBarWidget> pool;
private static final class ScrollBarRef extends Ref {
private final WeakReference<ScrollBarWidget> sbRef;
private ScrollBarRef(ScrollBarWidget sb) {
this.sbRef = new WeakReference<ScrollBarWidget>(sb);
}
private Control asControl() {
return sbRef.get();
}
}
public ScrollBarThemeImpl(final Accessor accessor) {
this.accessor = accessor;
pool = new Pool<ScrollBarWidget>(
sb -> {
accessor.removeChild(sb);
}, ScrollBarWidget.class);
accessor.addViewListener(new ViewListener(pool, accessor) {
@Override public void invalidated(Observable ov) {
super.invalidated(ov);
ScrollBar testSB = new ScrollBarWidget(ScrollBarThemeImpl.this);
accessor.addChild(testSB);
testSBRef = new WeakReference<ScrollBar>(testSB);
}
});
}
ScrollBar getTestSBRef() {
return testSBRef.get();
}
private static Orientation convertOrientation(int orientation) {
return orientation == VERTICAL_SCROLLBAR ? Orientation.VERTICAL : Orientation.HORIZONTAL;
}
private void adjustScrollBar(ScrollBar sb, int w, int h, int orientation) {
Orientation current = convertOrientation(orientation);
if (current != sb.getOrientation()) {
sb.setOrientation(current);
}
if (current == Orientation.VERTICAL) {
w = ScrollBarTheme.getThickness();
} else {
h = ScrollBarTheme.getThickness();
}
if ((w != sb.getWidth()) || (h != sb.getHeight())) {
sb.resize(w, h);
}
}
private void adjustScrollBar(ScrollBar sb, int w, int h, int orientation,
int value, int visibleSize, int totalSize)
{
adjustScrollBar(sb, w, h, orientation);
boolean disable = totalSize <= visibleSize;
sb.setDisable(disable);
if (disable) {
return;
}
if (value < 0) {
value = 0;
} else if(value > (totalSize - visibleSize)) {
value = totalSize - visibleSize;
}
if (sb.getMax() != totalSize || sb.getVisibleAmount() != visibleSize) {
sb.setValue(0);
sb.setMax(totalSize);
sb.setVisibleAmount(visibleSize);
}
if (totalSize > visibleSize) {
float factor = ((float)totalSize) / (totalSize - visibleSize);
if (sb.getValue() != value * factor) {
sb.setValue(value * factor);
}
}
}
@Override protected Ref createWidget(long id, int w, int h, int orientation,
int value, int visibleSize,
int totalSize)
{
ScrollBarWidget sb = pool.get(id);
if (sb == null) {
sb = new ScrollBarWidget(this);
pool.put(id, sb, accessor.getPage().getUpdateContentCycleID());
accessor.addChild(sb);
}
adjustScrollBar(sb, w, h, orientation, value, visibleSize, totalSize);
return new ScrollBarRef(sb);
}
@Override public void paint(WCGraphicsContext g, Ref sbRef,
int x, int y, int pressedPart, int hoveredPart)
{
ScrollBar sb = (ScrollBar)((ScrollBarRef)sbRef).asControl();
if (sb == null) {
return;
}
if (log.isLoggable(Level.FINEST)) {
log.finest("[{0}, {1} {2}x{3}], {4}",
new Object[] {x, y, sb.getWidth(), sb.getHeight(),
sb.getOrientation() == Orientation.VERTICAL ? "VERTICAL" : "HORIZONTAL"});
}
g.saveState();
g.translate(x, y);
Renderer.getRenderer().render(sb, g);
g.restoreState();
}
@Override public WCSize getWidgetSize(Ref widget) {
ScrollBar sb = (ScrollBar)((ScrollBarRef)widget).asControl();
if (sb != null) {
return new WCSize((float)sb.getWidth(), (float)sb.getHeight());
}
return new WCSize(0, 0);
}
@Override protected void getScrollBarPartRect(long id, int part, int rect[]) {
ScrollBar sb = pool.get(id);
if (sb == null) {
return;
}
Node node = null;
if (part == FORWARD_BUTTON_START_PART) {
node = getIncButton(sb);
} else if (part == BACK_BUTTON_START_PART) {
node = getDecButton(sb);
} else if (part == TRACK_BG_PART) {
node = getTrack(sb);
}
assert rect.length >= 4;
if (node != null) {
Bounds bounds = node.getBoundsInParent();
rect[0] = (int)bounds.getMinX();
rect[1] = (int)bounds.getMinY();
rect[2] = (int)bounds.getWidth();
rect[3] = (int)bounds.getHeight();
} else {
rect[0] = rect[1] = rect[2] = rect[3] = 0;
}
log.finest("id {0} part {1} bounds {2},{3} {4}x{5}",
new Object[] {String.valueOf(id), String.valueOf(part), rect[0], rect[1], rect[2], rect[3]});
}
private static Node getTrack(ScrollBar scrollBar) {
return findNode(scrollBar, "track");
}
private static Node getIncButton(ScrollBar scrollBar) {
return findNode(scrollBar, "increment-button");
}
private static Node getDecButton(ScrollBar scrollBar) {
return findNode(scrollBar, "decrement-button");
}
private static Node findNode(ScrollBar scrollBar, String styleclass) {
for (Node n : scrollBar.getChildrenUnmodifiable()) {
if (n.getStyleClass().contains(styleclass)) {
return n;
}
}
return null;
}
}
