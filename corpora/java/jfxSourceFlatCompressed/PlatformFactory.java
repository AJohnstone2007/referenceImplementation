package com.sun.glass.ui;
import java.util.Locale;
import com.sun.glass.ui.delegate.ClipboardDelegate;
import com.sun.glass.ui.delegate.MenuBarDelegate;
import com.sun.glass.ui.delegate.MenuDelegate;
import com.sun.glass.ui.delegate.MenuItemDelegate;
public abstract class PlatformFactory {
private static PlatformFactory instance;
public static synchronized PlatformFactory getPlatformFactory() {
if (instance == null) {
try {
String platform = Platform.determinePlatform();
String factory = "com.sun.glass.ui." + platform.toLowerCase(Locale.ROOT) + "."+ platform + "PlatformFactory";
Class c = Class.forName(factory);
instance = (PlatformFactory) c.getDeclaredConstructor().newInstance();
} catch (Exception e) {
e.printStackTrace();
System.out.println("Failed to load Glass factory class");
}
}
return instance;
}
public abstract Application createApplication();
public abstract MenuBarDelegate createMenuBarDelegate(MenuBar menubar);
public abstract MenuDelegate createMenuDelegate(Menu menu);
public abstract MenuItemDelegate createMenuItemDelegate(MenuItem menuItem);
public abstract ClipboardDelegate createClipboardDelegate();
}
