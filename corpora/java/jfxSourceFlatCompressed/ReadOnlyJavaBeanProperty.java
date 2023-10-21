package javafx.beans.property.adapter;
import javafx.beans.property.ReadOnlyProperty;
public interface ReadOnlyJavaBeanProperty<T> extends ReadOnlyProperty<T> {
void fireValueChangedEvent();
void dispose();
}
