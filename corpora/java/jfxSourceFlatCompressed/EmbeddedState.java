package com.sun.javafx.tk.quantum;
import com.sun.glass.ui.Pixels;
import com.sun.prism.PixelSource;
final class EmbeddedState extends SceneState {
public EmbeddedState(GlassScene vs) {
super(vs);
}
@Override
public void uploadPixels(PixelSource source) {
if (isValid()) {
Pixels pixels = source.getLatestPixels();
if (pixels != null) {
try {
EmbeddedScene escene = (EmbeddedScene) scene;
escene.uploadPixels(pixels);
} finally {
source.doneWithPixels(pixels);
}
}
} else {
source.skipLatestPixels();
}
}
@Override
public boolean isValid() {
return scene != null && getWidth() > 0 && getHeight() > 0;
}
@Override
public void update() {
super.update();
float scalex = ((EmbeddedScene) scene).getRenderScaleX();
float scaley = ((EmbeddedScene) scene).getRenderScaleY();
update(1.0f, 1.0f, scalex, scaley, scalex, scaley);
if (scene != null) {
isWindowVisible = true;
isWindowMinimized = false;
}
}
}
