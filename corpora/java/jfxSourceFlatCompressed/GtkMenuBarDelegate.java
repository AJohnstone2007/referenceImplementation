package com.sun.glass.ui.gtk;
import com.sun.glass.ui.delegate.MenuBarDelegate;
import com.sun.glass.ui.delegate.MenuDelegate;
class GtkMenuBarDelegate implements MenuBarDelegate {
public GtkMenuBarDelegate() {
}
public boolean createMenuBar() {
return true;
}
public boolean insert(MenuDelegate menu, int pos) {
return true;
}
public boolean remove(MenuDelegate menu, int pos) {
return true;
}
public long getNativeMenu() {
return 0;
}
}
