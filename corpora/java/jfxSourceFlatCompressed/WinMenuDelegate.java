package com.sun.glass.ui.win;
import com.sun.glass.ui.Menu;
import com.sun.glass.ui.Pixels;
import com.sun.glass.ui.delegate.MenuDelegate;
import com.sun.glass.ui.delegate.MenuItemDelegate;
final class WinMenuDelegate extends WinMenuImpl implements MenuDelegate {
final private Menu owner;
private WinMenuImpl parent = null;
public WinMenuDelegate(Menu menu) {
this.owner = menu;
}
public Menu getOwner() {
return owner;
}
@Override public boolean createMenu(String title, boolean enabled) {
return create();
}
public void dispose() {
destroy();
}
@Override public boolean setTitle(String title) {
if (parent != null) {
return parent.setSubmenuTitle(this, title);
}
return true;
}
@Override public boolean setEnabled(boolean enabled) {
if (parent != null) {
return parent.enableSubmenu(this, enabled);
}
return true;
}
@Override public boolean setPixels(Pixels pixels) {
return false;
}
@Override public boolean insert(MenuDelegate menu, int pos) {
return insertSubmenu((WinMenuDelegate)menu, pos);
}
@Override public boolean insert(MenuItemDelegate item, int pos) {
return insertItem((WinMenuItemDelegate)item, pos);
}
@Override public boolean remove(MenuDelegate menu, int pos) {
return removeMenu((WinMenuDelegate)menu, pos);
}
@Override public boolean remove(MenuItemDelegate item, int pos) {
return removeItem((WinMenuItemDelegate)item, pos);
}
WinMenuImpl getParent() {
return parent;
}
void setParent(WinMenuImpl newParent) {
parent = newParent;
}
}
