package com.sun.webkit;
import com.sun.javafx.webkit.prism.WCBufferedContextShim;
import com.sun.javafx.webkit.prism.PrismInvokerShim;
import com.sun.webkit.WebPage;
import com.sun.webkit.event.WCMouseEvent;
import com.sun.webkit.event.WCMouseWheelEvent;
import com.sun.webkit.graphics.WCGraphicsContext;
import com.sun.webkit.graphics.WCGraphicsManager;
import com.sun.webkit.graphics.WCGraphicsManagerShim;
import com.sun.webkit.graphics.WCPageBackBuffer;
import com.sun.webkit.graphics.WCRectangle;
import java.awt.image.BufferedImage;
public class WebPageShim {
public static int getFramesCount(WebPage page) {
return page.test_getFramesCount();
}
private static WCGraphicsContext setupPageWithGraphics(WebPage page, int x, int y, int w, int h) {
page.setBounds(x, y, w, h);
page.updateContent(new WCRectangle(x, y, w, h));
return WCBufferedContextShim.createBufferedContext(w, h);
}
public static BufferedImage paint(WebPage page, int x, int y, int w, int h) {
final WCGraphicsContext gc = setupPageWithGraphics(page, x, y, w, h);
PrismInvokerShim.runOnRenderThread(() -> {
page.paint(gc, x, y, w, h);
});
return gc.getImage().toBufferedImage();
}
public static void mockPrint(WebPage page, int x, int y, int w, int h) {
final WCGraphicsContext gc = setupPageWithGraphics(page, x, y, w, h);
page.print(gc, x, y, w, h);
}
public static void mockPrintByPage(WebPage page, int pageNo, int x, int y, int w, int h) {
final WCGraphicsContext gc = setupPageWithGraphics(page, x, y, w, h);
page.beginPrinting(w, h);
page.print(gc, pageNo, w);
page.endPrinting();
}
public static void click(WebPage page, int x, int y) {
WCMouseEvent mousePressEvent =
new WCMouseEvent(WCMouseEvent.MOUSE_PRESSED, WCMouseEvent.BUTTON1,
1, x, y,
x, y,
System.currentTimeMillis(),
false, false, false, false, false);
WCMouseEvent mouseReleaseEvent =
new WCMouseEvent(WCMouseEvent.MOUSE_RELEASED, WCMouseEvent.BUTTON1,
1, x, y,
x, y,
System.currentTimeMillis(),
false, false, false, false, false);
page.dispatchMouseEvent(mousePressEvent);
page.dispatchMouseEvent(mouseReleaseEvent);
}
public static void scroll(WebPage page, int x, int y, int deltaX, int deltaY) {
WCMouseWheelEvent mouseWheelEvent =
new WCMouseWheelEvent(x, y, x, y,
System.currentTimeMillis(),
false, false, false, false,
deltaX, deltaY);
page.dispatchMouseWheelEvent(mouseWheelEvent);
}
}
