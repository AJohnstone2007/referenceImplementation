package com.sun.glass.ui.win;
import com.sun.glass.ui.MenuBar;
import com.sun.glass.ui.delegate.MenuBarDelegate;
import com.sun.glass.ui.delegate.MenuDelegate;
final class WinMenuBarDelegate extends WinMenuImpl implements MenuBarDelegate {
final private MenuBar owner;
WinMenuBarDelegate(MenuBar menubar) {
owner = menubar;
}
public MenuBar getOwner() {
return owner;
}
@Override public boolean createMenuBar() {
return create();
}
@Override public boolean insert(MenuDelegate menuDelegate, int pos) {
WinMenuDelegate menu = (WinMenuDelegate)menuDelegate;
if (menu.getParent() != null) {
}
return insertSubmenu(menu, pos);
}
@Override public boolean remove(MenuDelegate menuDelegate, int pos) {
WinMenuDelegate menu = (WinMenuDelegate)menuDelegate;
return removeMenu(menu, pos);
}
@Override public long getNativeMenu() {
return getHMENU();
}
}
