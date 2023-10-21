package com.sun.glass.ui.ios;
import com.sun.glass.ui.MenuItem;
import com.sun.glass.ui.Pixels;
import com.sun.glass.ui.delegate.MenuDelegate;
import com.sun.glass.ui.delegate.MenuItemDelegate;
final class IosMenuDelegate implements MenuDelegate, MenuItemDelegate {
@Override
public boolean createMenu(String title, boolean enabled) {
return true;
}
@Override
public boolean createMenuItem(String title, MenuItem.Callback callback, int shortcutKey, int shortcutModifiers, Pixels pixels, boolean enabled, boolean checked) {
return true;
}
@Override
public boolean setTitle(String title) {
return true;
}
@Override
public boolean setCallback(MenuItem.Callback callback) {
return true;
}
@Override
public boolean setShortcut(int shortcutKey, int shortcutModifiers) {
return true;
}
@Override
public boolean setPixels(Pixels pixels) {
return true;
}
@Override
public boolean setEnabled(boolean enabled) {
return true;
}
@Override
public boolean setChecked(boolean checked) {
return true;
}
@Override
public boolean insert(MenuDelegate menu, int pos) {
return true;
}
@Override
public boolean insert(MenuItemDelegate item, int pos) {
return true;
}
@Override
public boolean remove(MenuDelegate menu, int pos) {
return true;
}
@Override
public boolean remove(MenuItemDelegate item, int pos) {
return true;
}
}
