package com.sun.glass.ui.ios;
import com.sun.glass.ui.delegate.MenuBarDelegate;
import com.sun.glass.ui.delegate.MenuDelegate;
final class IosMenuBarDelegate implements MenuBarDelegate {
@Override
public boolean createMenuBar() {
return true;
}
@Override
public boolean insert(MenuDelegate menu, int pos) {
return true;
}
@Override
public boolean remove(MenuDelegate menu, int pos) {
return true;
}
@Override
public long getNativeMenu() {
return 0;
}
}
