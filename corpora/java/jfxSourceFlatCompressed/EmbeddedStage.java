package com.sun.javafx.tk.quantum;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import com.sun.javafx.embed.AbstractEvents;
import com.sun.javafx.embed.EmbeddedStageInterface;
import com.sun.javafx.embed.HostInterface;
import com.sun.javafx.tk.TKScene;
import com.sun.javafx.tk.Toolkit;
import javafx.application.Platform;
final class EmbeddedStage extends GlassStage implements EmbeddedStageInterface {
private HostInterface host;
public EmbeddedStage(HostInterface host) {
this.host = host;
}
@Override
public TKScene createTKScene(boolean depthBuffer, boolean msaa, @SuppressWarnings("removal") AccessControlContext acc) {
EmbeddedScene scene = new EmbeddedScene(host, depthBuffer, msaa);
scene.setSecurityContext(acc);
return scene;
}
@Override
public void setScene(TKScene scene) {
if (scene != null) {
assert scene instanceof EmbeddedScene;
}
super.setScene(scene);
}
@Override
public void setBounds(float x, float y, boolean xSet, boolean ySet,
float w, float h, float cw, float ch,
float xGravity, float yGravity,
float renderScaleX, float renderScaleY)
{
if (QuantumToolkit.verbose) {
System.err.println("EmbeddedStage.setBounds: x=" + x + " y=" + y + " xSet=" + xSet + " ySet=" + ySet +
" w=" + w + " h=" + " cw=" + cw + " ch=" + ch);
}
float newW = w > 0 ? w : cw;
float newH = h > 0 ? h : ch;
if ((newW > 0) && (newH > 0)) {
host.setPreferredSize((int)newW, (int)newH);
}
TKScene scene = getScene();
if ((renderScaleX > 0 || renderScaleY > 0)
&& scene instanceof EmbeddedScene)
{
EmbeddedScene escene = (EmbeddedScene) scene;
if (renderScaleX <= 0.0) renderScaleX = escene.getRenderScaleX();
if (renderScaleY <= 0.0) renderScaleY = escene.getRenderScaleY();
escene.setPixelScaleFactors(renderScaleX, renderScaleY);
}
}
@Override
public float getPlatformScaleX() {
return 1.0f;
}
@Override
public float getPlatformScaleY() {
return 1.0f;
}
@Override
public float getOutputScaleX() {
TKScene scene = getScene();
if (scene instanceof EmbeddedScene) {
return ((EmbeddedScene) scene).getRenderScaleX();
}
return 1.0f;
}
@Override
public float getOutputScaleY() {
TKScene scene = getScene();
if (scene instanceof EmbeddedScene) {
return ((EmbeddedScene) scene).getRenderScaleY();
}
return 1.0f;
}
@Override public void setMinimumSize(int minWidth, int minHeight) {
}
@Override public void setMaximumSize(int maxWidth, int maxHeight) {
}
@Override
protected void setPlatformEnabled(boolean enabled) {
super.setPlatformEnabled(enabled);
host.setEnabled(enabled);
}
@Override
public void setIcons(List icons) {
if (QuantumToolkit.verbose) {
System.err.println("EmbeddedStage.setIcons");
}
}
@Override
public void setTitle(String title) {
if (QuantumToolkit.verbose) {
System.err.println("EmbeddedStage.setTitle " + title);
}
}
@Override
public void setVisible(boolean visible) {
host.setEmbeddedStage(visible ? this : null);
super.setVisible(visible);
}
@Override
public void setOpacity(float opacity) {
}
@Override
public void setIconified(boolean iconified) {
if (QuantumToolkit.verbose) {
System.err.println("EmbeddedScene.setIconified " + iconified);
}
}
@Override
public void setMaximized(boolean maximized) {
if (QuantumToolkit.verbose) {
System.err.println("EmbeddedScene.setMaximized " + maximized);
}
}
@Override
public void setAlwaysOnTop(boolean alwaysOnTop) {
if (QuantumToolkit.verbose) {
System.err.println("EmbeddedScene.setAlwaysOnTop " + alwaysOnTop);
}
}
@Override
public void setResizable(boolean resizable) {
if (QuantumToolkit.verbose) {
System.err.println("EmbeddedStage.setResizable " + resizable);
}
}
@Override
public void setFullScreen(boolean fullScreen) {
if (QuantumToolkit.verbose) {
System.err.println("EmbeddedStage.setFullScreen " + fullScreen);
}
}
@Override
public void requestFocus() {
if (!host.requestFocus()) {
return;
}
super.requestFocus();
}
@Override
public void toBack() {
if (QuantumToolkit.verbose) {
System.err.println("EmbeddedStage.toBack");
}
}
@Override
public void toFront() {
if (QuantumToolkit.verbose) {
System.err.println("EmbeddedStage.toFront");
}
}
@Override public boolean grabFocus() {
return host.grabFocus();
}
@Override public void ungrabFocus() {
host.ungrabFocus();
}
@SuppressWarnings("removal")
private void notifyStageListener(final Runnable r) {
AccessControlContext acc = getAccessControlContext();
AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
r.run();
return null;
}, acc);
}
private void notifyStageListenerLater(final Runnable r) {
Platform.runLater(() -> notifyStageListener(r));
}
@Override
public void setLocation(final int x, final int y) {
Runnable r = () -> {
if (stageListener != null) {
stageListener.changedLocation(x, y);
}
};
if (Toolkit.getToolkit().isFxUserThread()) {
notifyStageListener(r);
} else {
notifyStageListenerLater(r);
}
}
@Override
public void setSize(final int width, final int height) {
Runnable r = () -> {
if (stageListener != null) {
stageListener.changedSize(width, height);
}
};
if (Toolkit.getToolkit().isFxUserThread()) {
notifyStageListener(r);
} else {
notifyStageListenerLater(r);
}
}
@Override
public void setFocused(final boolean focused, final int focusCause) {
Runnable r = () -> {
if (stageListener != null) {
stageListener.changedFocused(focused,
AbstractEvents.focusCauseToPeerFocusCause(focusCause));
}
};
if (Toolkit.getToolkit().isFxUserThread()) {
notifyStageListener(r);
} else {
notifyStageListenerLater(r);
}
}
@Override
public void focusUngrab() {
Runnable r = () -> {
if (stageListener != null) {
stageListener.focusUngrab();
}
};
if (Toolkit.getToolkit().isFxUserThread()) {
notifyStageListener(r);
} else {
notifyStageListenerLater(r);
}
}
@Override
public void requestInput(String text, int type, double width, double height,
double Mxx, double Mxy, double Mxz, double Mxt,
double Myx, double Myy, double Myz, double Myt,
double Mzx, double Mzy, double Mzz, double Mzt) {
throw new UnsupportedOperationException("Not supported yet.");
}
@Override
public void releaseInput() {
throw new UnsupportedOperationException("Not supported yet.");
}
@Override public void setRTL(boolean b) {
}
@Override
public void setEnabled(boolean enabled) {
}
@Override
public long getRawHandle() {
return 0L;
}
}
