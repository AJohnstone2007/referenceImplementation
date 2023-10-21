package test.com.sun.javafx.pgstub;
import java.security.AccessControlContext;
import com.sun.javafx.sg.prism.NGCamera;
import com.sun.javafx.sg.prism.NGLightBase;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.tk.TKClipboard;
import com.sun.javafx.tk.TKScene;
import com.sun.javafx.tk.TKSceneListener;
import com.sun.javafx.tk.TKScenePaintListener;
import javafx.scene.Node;
public class StubScene implements TKScene {
StubStage stage;
private TKSceneListener listener;
private Object cursor;
private NGCamera camera;
Runnable inputMethodCompistionFinishedDelegate;
@Override
public void dispose() {
}
public void waitForRenderingToComplete() {
}
public void waitForSynchronization() {
}
public void releaseSynchronization(boolean updateState) {
}
public void setTKSceneListener(TKSceneListener listener) {
this.listener = listener;
}
public void setRoot(NGNode root) {
}
public void markDirty() {
}
public void setCamera(NGCamera ci) {
camera = ci;
}
public void setFillPaint(Object fillPaint) {
}
public void setCursor(Object cursor) {
this.cursor = cursor;
}
public Object getCursor() {
return cursor;
}
public void enableInputMethodEvents(boolean enable) {
}
@Override
public void finishInputMethodComposition() {
if (inputMethodCompistionFinishedDelegate != null) {
inputMethodCompistionFinishedDelegate.run();
}
}
public void setInputMethodCompositionFinishDelegate(Runnable r) {
inputMethodCompistionFinishedDelegate = r;
}
public void entireSceneNeedsRepaint() {
}
@Override
public TKClipboard createDragboard(boolean isDragSource) {
return StubToolkit.createDragboard();
}
@Override
public void setTKScenePaintListener(TKScenePaintListener listener) {
}
public TKSceneListener getListener() {
return listener;
}
@Override
public NGLightBase[] getLights() {
return null;
}
@Override
public void setLights(NGLightBase[] lights) {
}
public NGCamera getCamera() {
return camera;
}
@SuppressWarnings("removal")
@Override
public AccessControlContext getAccessControlContext() {
return null;
}
}
