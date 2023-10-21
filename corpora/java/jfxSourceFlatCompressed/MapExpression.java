package javafx.beans.binding;
import com.sun.javafx.binding.StringFormatter;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.value.ObservableMapValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import java.util.*;
public abstract class MapExpression<K, V> implements ObservableMapValue<K, V> {
private static final ObservableMap EMPTY_MAP = new EmptyObservableMap();
private static class EmptyObservableMap<K, V> extends AbstractMap<K, V> implements ObservableMap<K, V> {
@Override
public Set<Entry<K, V>> entrySet() {
return Collections.emptySet();
}
@Override
public void addListener(MapChangeListener<? super K, ? super V> mapChangeListener) {
}
@Override
public void removeListener(MapChangeListener<? super K, ? super V> mapChangeListener) {
}
@Override
public void addListener(InvalidationListener listener) {
}
@Override
public void removeListener(InvalidationListener listener) {
}
}
@Override
public ObservableMap<K, V> getValue() {
return get();
}
public MapExpression() {
}
public static <K, V> MapExpression<K, V> mapExpression(final ObservableMapValue<K, V> value) {
if (value == null) {
throw new NullPointerException("Map must be specified.");
}
return value instanceof MapExpression ? (MapExpression<K, V>) value
: new MapBinding<K, V>() {
{
super.bind(value);
}
@Override
public void dispose() {
super.unbind(value);
}
@Override
protected ObservableMap<K, V> computeValue() {
return value.get();
}
@Override
public ObservableList<?> getDependencies() {
return FXCollections.singletonObservableList(value);
}
};
}
public int getSize() {
return size();
}
public abstract ReadOnlyIntegerProperty sizeProperty();
public abstract ReadOnlyBooleanProperty emptyProperty();
public ObjectBinding<V> valueAt(K key) {
return Bindings.valueAt(this, key);
}
public ObjectBinding<V> valueAt(ObservableValue<K> key) {
return Bindings.valueAt(this, key);
}
public BooleanBinding isEqualTo(final ObservableMap<?, ?> other) {
return Bindings.equal(this, other);
}
public BooleanBinding isNotEqualTo(final ObservableMap<?, ?> other) {
return Bindings.notEqual(this, other);
}
public BooleanBinding isNull() {
return Bindings.isNull(this);
}
public BooleanBinding isNotNull() {
return Bindings.isNotNull(this);
}
public StringBinding asString() {
return (StringBinding) StringFormatter.convert(this);
}
@Override
public int size() {
final ObservableMap<K, V> map = get();
return (map == null)? EMPTY_MAP.size() : map.size();
}
@Override
public boolean isEmpty() {
final ObservableMap<K, V> map = get();
return (map == null)? EMPTY_MAP.isEmpty() : map.isEmpty();
}
@Override
public boolean containsKey(Object obj) {
final ObservableMap<K, V> map = get();
return (map == null)? EMPTY_MAP.containsKey(obj) : map.containsKey(obj);
}
@Override
public boolean containsValue(Object obj) {
final ObservableMap<K, V> map = get();
return (map == null)? EMPTY_MAP.containsValue(obj) : map.containsValue(obj);
}
@Override
public V put(K key, V value) {
final ObservableMap<K, V> map = get();
return (map == null)? (V) EMPTY_MAP.put(key, value) : map.put(key, value);
}
@Override
public V remove(Object obj) {
final ObservableMap<K, V> map = get();
return (map == null)? (V) EMPTY_MAP.remove(obj) : map.remove(obj);
}
@Override
public void putAll(Map<? extends K, ? extends V> elements) {
final ObservableMap<K, V> map = get();
if (map == null) {
EMPTY_MAP.putAll(elements);
} else {
map.putAll(elements);
}
}
@Override
public void clear() {
final ObservableMap<K, V> map = get();
if (map == null) {
EMPTY_MAP.clear();
} else {
map.clear();
}
}
@Override
public Set<K> keySet() {
final ObservableMap<K, V> map = get();
return (map == null)? EMPTY_MAP.keySet() : map.keySet();
}
@Override
public Collection<V> values() {
final ObservableMap<K, V> map = get();
return (map == null)? EMPTY_MAP.values() : map.values();
}
@Override
public Set<Entry<K, V>> entrySet() {
final ObservableMap<K, V> map = get();
return (map == null)? EMPTY_MAP.entrySet() : map.entrySet();
}
@Override
public V get(Object key) {
final ObservableMap<K, V> map = get();
return (map == null)? (V) EMPTY_MAP.get(key) : map.get(key);
}
}
