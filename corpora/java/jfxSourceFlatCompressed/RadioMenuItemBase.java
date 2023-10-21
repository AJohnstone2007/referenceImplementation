package com.sun.javafx.menu;
import javafx.beans.property.BooleanProperty;
public interface RadioMenuItemBase extends MenuItemBase {
public void setSelected(boolean value);
public boolean isSelected();
public BooleanProperty selectedProperty();
}
