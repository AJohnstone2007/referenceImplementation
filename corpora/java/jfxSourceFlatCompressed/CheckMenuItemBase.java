package com.sun.javafx.menu;
import javafx.beans.property.BooleanProperty;
public interface CheckMenuItemBase extends MenuItemBase {
public void setSelected(boolean value);
public boolean isSelected();
public BooleanProperty selectedProperty();
}
