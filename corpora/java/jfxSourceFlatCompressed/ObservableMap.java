package javafx.collections;
import java.util.Map;
import javafx.beans.Observable;
public interface ObservableMap<K, V> extends Map<K, V>, Observable {
public void addListener(MapChangeListener<? super K, ? super V> listener);
public void removeListener(MapChangeListener<? super K, ? super V> listener);
}
