package javafx.scene.control;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableMap;
public interface Toggle {
ToggleGroup getToggleGroup();
void setToggleGroup(ToggleGroup toggleGroup);
ObjectProperty<ToggleGroup> toggleGroupProperty();
boolean isSelected();
void setSelected(boolean selected);
BooleanProperty selectedProperty();
Object getUserData();
void setUserData(Object value);
ObservableMap<Object, Object> getProperties();
}
