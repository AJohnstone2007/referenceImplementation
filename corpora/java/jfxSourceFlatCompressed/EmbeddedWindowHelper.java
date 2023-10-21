package com.sun.javafx.stage;
import static com.sun.javafx.stage.WindowHelper.setHelper;
import com.sun.javafx.util.Utils;
import javafx.stage.Window;
public class EmbeddedWindowHelper extends WindowHelper {
private static final EmbeddedWindowHelper theInstance;
private static EmbeddedWindowAccessor embeddedWindowAccessor;
static {
theInstance = new EmbeddedWindowHelper();
Utils.forceInit(EmbeddedWindow.class);
}
private static WindowHelper getInstance() {
return theInstance;
}
public static void initHelper(EmbeddedWindow embeddedWindow) {
setHelper(embeddedWindow, getInstance());
}
@Override
protected void visibleChangingImpl(Window window, boolean visible) {
super.visibleChangingImpl(window, visible);
embeddedWindowAccessor.doVisibleChanging(window, visible);
}
public static void setEmbeddedWindowAccessor(EmbeddedWindowAccessor newAccessor) {
if (embeddedWindowAccessor != null) {
throw new IllegalStateException();
}
embeddedWindowAccessor = newAccessor;
}
public interface EmbeddedWindowAccessor {
void doVisibleChanging(Window window, boolean visible);
}
}
