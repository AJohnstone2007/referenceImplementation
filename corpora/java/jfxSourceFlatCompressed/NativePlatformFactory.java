package com.sun.glass.ui.monocle;
import java.security.AccessController;
import java.security.PrivilegedAction;
public abstract class NativePlatformFactory {
protected abstract boolean matches();
protected abstract NativePlatform createNativePlatform();
protected abstract int getMajorVersion();
protected abstract int getMinorVersion();
private static NativePlatform platform;
private static final int majorVersion = 1;
private static final int minorVersion = 0;
public static synchronized NativePlatform getNativePlatform() {
if (platform == null) {
@SuppressWarnings("removal")
String platformFactoryProperty =
AccessController.doPrivileged((PrivilegedAction<String>) () -> System.getProperty("monocle.platform",
"MX6,OMAP,Dispman,Android,X11,Linux,Headless"));
String[] platformFactories = platformFactoryProperty.split(",");
for (int i = 0; i < platformFactories.length; i++) {
String factoryName = platformFactories[i].trim();
String factoryClassName;
if (factoryName.contains(".")) {
factoryClassName = factoryName;
} else {
factoryClassName = "com.sun.glass.ui.monocle."
+ factoryName + "PlatformFactory";
}
if (MonocleSettings.settings.tracePlatformConfig) {
MonocleTrace.traceConfig("Trying platform %s with class %s",
factoryName, factoryClassName);
}
try {
final ClassLoader loader = NativePlatformFactory.class.getClassLoader();
final Class<?> clazz = Class.forName(factoryClassName, false, loader);
if (!NativePlatformFactory.class.isAssignableFrom(clazz)) {
throw new IllegalArgumentException("Unrecognized Monocle platform: "
+ factoryClassName);
}
NativePlatformFactory npf = (NativePlatformFactory) clazz.getDeclaredConstructor().newInstance();
if (npf.matches() &&
npf.getMajorVersion() == majorVersion &&
npf.getMinorVersion() == minorVersion) {
platform = npf.createNativePlatform();
if (MonocleSettings.settings.tracePlatformConfig) {
MonocleTrace.traceConfig("Matched %s", factoryName);
}
return platform;
}
} catch (Exception e) {
if (MonocleSettings.settings.tracePlatformConfig) {
MonocleTrace.traceConfig("Failed to create platform %s",
factoryClassName);
}
e.printStackTrace();
}
}
throw new UnsupportedOperationException(
"Cannot load a native platform from: '"
+ platformFactoryProperty + "'");
}
return platform;
}
}
