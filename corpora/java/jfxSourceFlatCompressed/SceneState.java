package com.sun.javafx.tk.quantum;
import com.sun.glass.ui.Application;
import com.sun.javafx.sg.prism.NGCamera;
import com.sun.prism.PixelSource;
import com.sun.prism.PresentableState;
import com.sun.prism.paint.Color;
import com.sun.prism.paint.Paint;
class SceneState extends PresentableState {
final GlassScene scene;
private Color clearColor;
private Paint currentPaint;
private NGCamera camera;
public SceneState(GlassScene vs) {
super();
scene = vs;
}
@Override
public boolean isMSAA() {
return scene.isMSAA();
}
public GlassScene getScene() {
return scene;
}
public boolean isValid() {
return getWindow() != null && getView() != null && !isViewClosed() && getWidth() > 0 && getHeight() > 0;
}
public void update() {
view = scene.getPlatformView();
clearColor = scene.getClearColor();
currentPaint = scene.getCurrentPaint();
super.update();
camera = scene.getCamera();
if (camera != null) {
viewWidth = (int)camera.getViewWidth();
viewHeight = (int)camera.getViewHeight();
}
}
@Override
public void uploadPixels(PixelSource source) {
Application.invokeLater(() -> {
if (isValid()) {
SceneState.super.uploadPixels(source);
} else {
source.skipLatestPixels();
}
});
}
Color getClearColor() {
return clearColor;
}
Paint getCurrentPaint() {
return currentPaint;
}
NGCamera getCamera() {
return camera;
}
}
