package com.sun.javafx.stage;
import javafx.scene.Scene;
import javafx.stage.Window;
import com.sun.javafx.embed.HostInterface;
import com.sun.javafx.tk.Toolkit;
public class EmbeddedWindow extends Window {
static {
EmbeddedWindowHelper.setEmbeddedWindowAccessor(new EmbeddedWindowHelper.EmbeddedWindowAccessor() {
@Override public void doVisibleChanging(Window window, boolean visible) {
((EmbeddedWindow) window).doVisibleChanging(visible);
}
});
}
private HostInterface host;
public EmbeddedWindow(HostInterface host) {
this.host = host;
EmbeddedWindowHelper.initHelper(this);
}
public HostInterface getHost() {
return host;
}
@Override public final void setScene(Scene value) {
super.setScene(value);
}
@Override public final void show() {
super.show();
}
private void doVisibleChanging(boolean visible) {
Toolkit toolkit = Toolkit.getToolkit();
if (visible && (WindowHelper.getPeer(this) == null)) {
WindowHelper.setPeer(this, toolkit.createTKEmbeddedStage(host,
WindowHelper.getAccessControlContext(this)));
WindowHelper.setPeerListener(this, new WindowPeerListener(this));
}
}
}
