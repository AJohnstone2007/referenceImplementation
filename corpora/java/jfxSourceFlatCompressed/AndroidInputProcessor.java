package com.sun.glass.ui.monocle;
import javafx.application.Platform;
class AndroidInputProcessor {
private final AndroidInputDevice device;
final TouchPipeline touchPipeline;
private final KeyInput keyInput = new KeyInput();
AndroidInputProcessor(AndroidInputDevice device) {
this.device = device;
touchPipeline = new TouchPipeline();
touchPipeline.add(TouchInput.getInstance().getBasePipeline());
}
void pushEvent(TouchState state) {
touchPipeline.pushState(state);
}
void processEvents(AndroidInputDevice device) {
touchPipeline.pushState(null);
}
synchronized void pushKeyEvent(KeyState keyState) {
keyInput.setState(keyState);
}
synchronized void dispatchKeyEvent(int type, int key, char[] chars, int modifiers) {
Platform.runLater( () -> {
MonocleWindow window = (MonocleWindow) MonocleWindowManager.getInstance().getFocusedWindow();
if (window == null) {
return;
}
MonocleView view = (MonocleView) window.getView();
if (view == null) {
return;
}
RunnableProcessor.runLater( () -> view.notifyKey(type, key, chars, modifiers));
});
}
}
