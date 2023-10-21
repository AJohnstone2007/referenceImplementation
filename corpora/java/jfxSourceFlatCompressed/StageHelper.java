package com.sun.javafx.stage;
import com.sun.javafx.util.Utils;
import javafx.stage.Stage;
import javafx.stage.Window;
public class StageHelper extends WindowHelper {
private static final StageHelper theInstance;
private static StageAccessor stageAccessor;
static {
theInstance = new StageHelper();
Utils.forceInit(Stage.class);
}
private static WindowHelper getInstance() {
return theInstance;
}
public static void initHelper(Stage stage) {
setHelper(stage, getInstance());
}
@Override
protected void visibleChangingImpl(Window window, boolean visible) {
super.visibleChangingImpl(window, visible);
stageAccessor.doVisibleChanging(window, visible);
}
@Override
protected void visibleChangedImpl(Window window, boolean visible) {
super.visibleChangedImpl(window, visible);
stageAccessor.doVisibleChanged(window, visible);
}
public static void initSecurityDialog(Stage stage, boolean securityDialog) {
stageAccessor.initSecurityDialog(stage, securityDialog);
}
public static void setPrimary(Stage stage, boolean primary) {
stageAccessor.setPrimary(stage, primary);
}
public static void setImportant(Stage stage, boolean important) {
stageAccessor.setImportant(stage, important);
}
public static void setStageAccessor(StageAccessor a) {
if (stageAccessor != null) {
System.out.println("Warning: Stage accessor already set: " + stageAccessor);
Thread.dumpStack();
}
stageAccessor = a;
}
public static StageAccessor getStageAccessor() {
return stageAccessor;
}
public static interface StageAccessor {
void doVisibleChanging(Window window, boolean visible);
void doVisibleChanged(Window window, boolean visible);
public void initSecurityDialog(Stage stage, boolean securityDialog);
public void setPrimary(Stage stage, boolean primary);
public void setImportant(Stage stage, boolean important);
}
}
