package com.sun.javafx.webkit.prism;
import com.sun.webkit.graphics.WCGraphicsContext;
import com.sun.webkit.graphics.WCRectangle;
import com.sun.webkit.graphics.WCRenderQueue;
final class WCRenderQueueImpl extends WCRenderQueue {
WCRenderQueueImpl(WCGraphicsContext gc) {
super(gc);
}
WCRenderQueueImpl(WCRectangle clip, boolean opaque) {
super(clip, opaque);
}
@Override
protected void flush() {
if (!isEmpty()) {
PrismInvoker.invokeOnRenderThread(() -> {
decode();
});
}
}
@Override
protected void disposeGraphics() {
PrismInvoker.invokeOnRenderThread(() -> {
if (gc != null) {
gc.dispose();
}
});
}
}
