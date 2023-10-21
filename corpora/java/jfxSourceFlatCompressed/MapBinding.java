package javafx.beans.binding;
import com.sun.javafx.binding.BindingHelperObserver;
import com.sun.javafx.binding.MapExpressionHelper;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
public abstract class MapBinding<K, V> extends MapExpression<K, V> implements Binding<ObservableMap<K, V>> {
private final MapChangeListener<K, V> mapChangeListener = new MapChangeListener<K, V>() {
@Override
public void onChanged(Change<? extends K, ? extends V> change) {
invalidateProperties();
onInvalidating();
MapExpressionHelper.fireValueChangedEvent(helper, change);
}
};
private ObservableMap<K, V> value;
private boolean valid = false;
private BindingHelperObserver observer;
private MapExpressionHelper<K, V> helper = null;
private SizeProperty size0;
private EmptyProperty empty0;
public MapBinding() {
}
@Override
public ReadOnlyIntegerProperty sizeProperty() {
if (size0 == null) {
size0 = new SizeProperty();
}
return size0;
}
private class SizeProperty extends ReadOnlyIntegerPropertyBase {
@Override
public int get() {
return size();
}
@Override
public Object getBean() {
return MapBinding.this;
}
@Override
public String getName() {
return "size";
}
protected void fireValueChangedEvent() {
super.fireValueChangedEvent();
}
}
@Override
public ReadOnlyBooleanProperty emptyProperty() {
if (empty0 == null) {
empty0 = new EmptyProperty();
}
return empty0;
}
private class EmptyProperty extends ReadOnlyBooleanPropertyBase {
@Override
public boolean get() {
return isEmpty();
}
@Override
public Object getBean() {
return MapBinding.this;
}
@Override
public String getName() {
return "empty";
}
protected void fireValueChangedEvent() {
super.fireValueChangedEvent();
}
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
protected final void bind(Observable... dependencies) {
if ((dependencies != null) && (dependencies.length > 0)) {
if (observer == null) {
observer = new BindingHelperObserver(this);
}
for (final Observable dep : dependencies) {
if (dep != null) {
dep.addListener(observer);
}
}
}
}
protected final void unbind(Observable... dependencies) {
if (observer != null) {
for (final Observable dep : dependencies) {
if (dep != null) {
dep.removeListener(observer);
}
}
observer = null;
}
}
@Override
public void dispose() {
}
@Override
public ObservableList<?> getDependencies() {
return FXCollections.emptyObservableList();
}
@Override
public final ObservableMap<K, V> get() {
if (!valid) {
value = computeValue();
valid = true;
if (value != null) {
value.addListener(mapChangeListener);
}
}
return value;
}
protected void onInvalidating() {
}
private void invalidateProperties() {
if (size0 != null) {
size0.fireValueChangedEvent();
}
if (empty0 != null) {
empty0.fireValueChangedEvent();
}
}
@Override
public final void invalidate() {
if (valid) {
if (value != null) {
value.removeListener(mapChangeListener);
}
valid = false;
invalidateProperties();
onInvalidating();
MapExpressionHelper.fireValueChangedEvent(helper);
}
}
@Override
public final boolean isValid() {
return valid;
}
protected abstract ObservableMap<K, V> computeValue();
@Override
public String toString() {
return valid ? "MapBinding [value: " + get() + "]"
: "MapBinding [invalid]";
}
}
