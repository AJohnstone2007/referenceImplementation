package javafx.collections;
import java.util.Set;
import javafx.beans.Observable;
public interface ObservableSet<E> extends Set<E>, Observable {
public void addListener(SetChangeListener<? super E> listener);
public void removeListener(SetChangeListener<? super E> listener);
}
