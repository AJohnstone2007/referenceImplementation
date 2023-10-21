package javafx.collections;
import javafx.beans.Observable;
public interface ObservableArray<T extends ObservableArray<T>> extends Observable {
public void addListener(ArrayChangeListener<T> listener);
public void removeListener(ArrayChangeListener<T> listener);
public void resize(int size);
public void ensureCapacity(int capacity);
public void trimToSize();
public void clear();
public int size();
}
