package com.sun.javafx.scene.layout;
import com.sun.javafx.util.Utils;
import javafx.scene.layout.Pane;
public class PaneHelper extends RegionHelper {
private static final PaneHelper theInstance;
private static PaneAccessor paneAccessor;
static {
theInstance = new PaneHelper();
Utils.forceInit(Pane.class);
}
private static PaneHelper getInstance() {
return theInstance;
}
public static void initHelper(Pane pane) {
setHelper(pane, getInstance());
}
public static void setPaneAccessor(final PaneAccessor newAccessor) {
if (paneAccessor != null) {
throw new IllegalStateException();
}
paneAccessor = newAccessor;
}
public interface PaneAccessor {
}
}
