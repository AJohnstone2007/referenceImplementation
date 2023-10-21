package com.sun.javafx.tk.quantum;
import com.sun.prism.Image;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import com.sun.javafx.stage.WindowHelper;
public class WindowStageShim {
public static Image findBestImage(java.util.List icons, int width, int height) {
return WindowStage.findBestImage(icons, width, height);
}
public static StageStyle getStyle(final Window window) {
return ((WindowStage) com.sun.javafx.stage.WindowHelper.getPeer(window)).getStyle();
}
}
