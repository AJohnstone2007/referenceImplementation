package test.com.sun.javafx.pgstub;
public class StubPopupStage extends StubStage {
@Override
public void close() {
getNotificationSender().closing();
}
@Override
public void setFullScreen(boolean fullScreen) {
getNotificationSender().changedFullscreen(fullScreen);
}
@Override
public void setIconified(boolean iconified) {
getNotificationSender().changedIconified(iconified);
}
@Override
public void setMaximized(boolean maximized) {
getNotificationSender().changedMaximized(maximized);
}
@Override
public void setAlwaysOnTop(boolean alwaysOnTop) {
getNotificationSender().changedAlwaysOnTop(alwaysOnTop);
}
@Override
public void setResizable(boolean resizable) {
getNotificationSender().changedResizable(resizable);
}
}
