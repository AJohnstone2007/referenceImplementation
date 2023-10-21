package com.sun.javafx.webkit.prism;
import com.sun.javafx.webkit.prism.PrismImage;
import com.sun.webkit.graphics.WCGraphicsContext;
import com.sun.webkit.graphics.WCGraphicsManagerShim;
import com.sun.webkit.graphics.WCImage;
public class WCBufferedContextShim {
public static WCGraphicsContext createBufferedContext(int w, int h) {
final WCImage img = WCGraphicsManagerShim.createRTImage(w, h);
return new WCBufferedContext((PrismImage) img);
}
}
