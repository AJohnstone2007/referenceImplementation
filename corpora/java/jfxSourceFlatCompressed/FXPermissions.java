package com.sun.javafx;
import javafx.util.FXPermission;
public final class FXPermissions {
private FXPermissions() {
}
public static final FXPermission ACCESS_CLIPBOARD_PERMISSION =
new FXPermission("accessClipboard");
public static final FXPermission ACCESS_WINDOW_LIST_PERMISSION =
new FXPermission("accessWindowList");
public static final FXPermission CREATE_ROBOT_PERMISSION =
new FXPermission("createRobot");
public static final FXPermission CREATE_TRANSPARENT_WINDOW_PERMISSION =
new FXPermission("createTransparentWindow");
public static final FXPermission UNRESTRICTED_FULL_SCREEN_PERMISSION =
new FXPermission("unrestrictedFullScreen");
public static final FXPermission LOAD_FONT_PERMISSION =
new FXPermission("loadFont");
public static final FXPermission MODIFY_FXML_CLASS_LOADER_PERMISSION =
new FXPermission("modifyFXMLClassLoader");
public static final FXPermission SET_WINDOW_ALWAYS_ON_TOP_PERMISSION =
new FXPermission("setWindowAlwaysOnTop");
}
