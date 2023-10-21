package javafx.beans.value;
import javafx.collections.ObservableMap;
public interface WritableMapValue<K, V> extends WritableObjectValue<ObservableMap<K,V>>, ObservableMap<K, V> {
}
