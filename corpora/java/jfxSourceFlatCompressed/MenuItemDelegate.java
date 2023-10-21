package com.sun.glass.ui.delegate;
import com.sun.glass.ui.MenuItem.Callback;
import com.sun.glass.ui.Pixels;
public interface MenuItemDelegate {
public boolean createMenuItem(String title, Callback callback,
int shortcutKey, int shortcutModifiers, Pixels pixels,
boolean enabled, boolean checked);
public boolean setTitle(String title);
public boolean setCallback(Callback callback);
public boolean setShortcut(int shortcutKey, int shortcutModifiers);
public boolean setPixels(Pixels pixels);
public boolean setEnabled(boolean enabled);
public boolean setChecked(boolean checked);
}
