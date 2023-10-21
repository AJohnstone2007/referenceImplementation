package com.sun.glass.ui.delegate;
public interface MenuBarDelegate {
public boolean createMenuBar();
public boolean insert(MenuDelegate menu, int pos);
public boolean remove(MenuDelegate menu, int pos);
public long getNativeMenu();
}
