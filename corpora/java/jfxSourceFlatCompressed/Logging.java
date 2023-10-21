package com.sun.javafx.util;
import com.sun.javafx.logging.PlatformLogger;
public class Logging {
private static PlatformLogger layoutLogger = null;
public static final PlatformLogger getLayoutLogger() {
if (layoutLogger == null) {
layoutLogger = PlatformLogger.getLogger("javafx.scene.layout");
}
return layoutLogger;
}
private static PlatformLogger focusLogger = null;
public static final PlatformLogger getFocusLogger() {
if (focusLogger == null) {
focusLogger = PlatformLogger.getLogger("javafx.scene.focus");
}
return focusLogger;
}
private static PlatformLogger inputLogger = null;
public static final PlatformLogger getInputLogger() {
if (inputLogger == null) {
inputLogger = PlatformLogger.getLogger("javafx.scene.input");
}
return inputLogger;
}
private static PlatformLogger cssLogger = null;
public static final PlatformLogger getCSSLogger() {
if (cssLogger == null) {
cssLogger = PlatformLogger.getLogger("javafx.css");
}
return cssLogger;
}
private static PlatformLogger javafxLogger = null;
public static final PlatformLogger getJavaFXLogger() {
if (javafxLogger == null) {
javafxLogger = PlatformLogger.getLogger("javafx");
}
return javafxLogger;
}
private static PlatformLogger accessibilityLogger = null;
public static final PlatformLogger getAccessibilityLogger() {
if (accessibilityLogger == null) {
accessibilityLogger = PlatformLogger.getLogger("javafx.accessibility");
}
return accessibilityLogger;
}
}
