package javafx.beans.binding;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
public interface Binding<T> extends ObservableValue<T> {
boolean isValid();
void invalidate();
ObservableList<?> getDependencies();
void dispose();
}
