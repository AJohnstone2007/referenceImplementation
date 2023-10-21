package com.sun.glass.ui.delegate;
import com.sun.glass.ui.Pixels;
public interface MenuDelegate {
public boolean createMenu(String title, boolean enabled);
public boolean setTitle(String title);
public boolean setEnabled(boolean enabled);
public boolean setPixels(Pixels pixels);
public boolean insert(MenuDelegate menu, int pos);
public boolean insert(MenuItemDelegate item, int pos);
public boolean remove(MenuDelegate menu, int pos);
public boolean remove(MenuItemDelegate item, int pos);
}
