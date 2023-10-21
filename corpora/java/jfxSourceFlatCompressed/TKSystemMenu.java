package com.sun.javafx.tk;
import java.util.List;
import com.sun.javafx.menu.MenuBase;
public interface TKSystemMenu {
public boolean isSupported();
public void setMenus(List<MenuBase> menus);
}
