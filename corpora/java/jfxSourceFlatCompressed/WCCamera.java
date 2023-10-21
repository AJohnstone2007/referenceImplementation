package com.sun.webkit.graphics;
import com.sun.javafx.sg.prism.NGCamera;
import com.sun.javafx.sg.prism.NGDefaultCamera;
public class WCCamera extends NGDefaultCamera {
public static final NGCamera INSTANCE = new WCCamera();
public void validate(final int w, final int h) {
if ((w != viewWidth) || (h != viewHeight)) {
setViewWidth(w);
setViewHeight(h);
projViewTx.ortho(0.0, w, h, 0.0, -9999999, 99999);
}
}
}
