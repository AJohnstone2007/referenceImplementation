package com.sun.javafx.webkit.theme;
import com.sun.javafx.scene.control.ControlHelper;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
public class ScrollBarWidgetHelper extends ControlHelper {
private static final ScrollBarWidgetHelper theInstance;
private static ScrollBarWidgetAccessor scrollBarWidgetAccessor;
static {
theInstance = new ScrollBarWidgetHelper();
Utils.forceInit(ScrollBarWidget.class);
}
private static ScrollBarWidgetHelper getInstance() {
return theInstance;
}
public static void initHelper(ScrollBarWidget scrollBarWidge) {
setHelper(scrollBarWidge, getInstance());
}
@Override
protected void updatePeerImpl(Node node) {
super.updatePeerImpl(node);
scrollBarWidgetAccessor.doUpdatePeer(node);
}
public static void setScrollBarWidgetAccessor(final ScrollBarWidgetAccessor newAccessor) {
if (scrollBarWidgetAccessor != null) {
throw new IllegalStateException();
}
scrollBarWidgetAccessor = newAccessor;
}
public interface ScrollBarWidgetAccessor {
void doUpdatePeer(Node node);
}
}
