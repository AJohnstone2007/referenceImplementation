package javafx.beans.property;
import com.sun.javafx.binding.MapExpressionHelper;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
public abstract class ReadOnlyMapPropertyBase<K, V> extends ReadOnlyMapProperty<K, V> {
private MapExpressionHelper<K, V> helper;
public ReadOnlyMapPropertyBase() {
}
@Override
public void addListener(InvalidationListener listener) {
helper = MapExpressionHelper.addListener(helper, this, listener);
}
@Override
public void removeListener(InvalidationListener listener) {
helper = MapExpressionHelper.removeListener(helper, listener);
}
@Override
public void addListener(ChangeListener<? super ObservableMap<K, V>> listener) {
helper = MapExpressionHelper.addListener(helper, this, listener);
}
@Override
public void removeListener(ChangeListener<? super ObservableMap<K, V>> listener) {
helper = MapExpressionHelper.removeListener(helper, listener);
}
@Override
public void addListener(MapChangeListener<? super K, ? super V> listener) {
helper = MapExpressionHelper.addListener(helper, this, listener);
}
@Override
public void removeListener(MapChangeListener<? super K, ? super V> listener) {
helper = MapExpressionHelper.removeListener(helper, listener);
}
protected void fireValueChangedEvent() {
MapExpressionHelper.fireValueChangedEvent(helper);
}
protected void fireValueChangedEvent(MapChangeListener.Change<? extends K, ? extends V> change) {
MapExpressionHelper.fireValueChangedEvent(helper, change);
}
}
