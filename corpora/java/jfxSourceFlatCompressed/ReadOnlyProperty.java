package javafx.beans.property;
import javafx.beans.value.ObservableValue;
public interface ReadOnlyProperty<T> extends ObservableValue<T> {
Object getBean();
String getName();
}
