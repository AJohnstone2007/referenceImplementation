package javafx.beans.value;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
public interface ObservableValue<T> extends Observable {
void addListener(ChangeListener<? super T> listener);
void removeListener(ChangeListener<? super T> listener);
T getValue();
}
