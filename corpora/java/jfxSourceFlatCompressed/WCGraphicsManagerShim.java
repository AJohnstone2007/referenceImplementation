package com.sun.webkit.graphics;
import com.sun.webkit.graphics.WCGraphicsManager;
import com.sun.webkit.graphics.WCImage;
public class WCGraphicsManagerShim {
public static WCImage createRTImage(int w, int h) {
return WCGraphicsManager.getGraphicsManager().createRTImage(w, h);
}
}
