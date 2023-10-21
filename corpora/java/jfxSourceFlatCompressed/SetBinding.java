package javafx.beans.binding;
import com.sun.javafx.binding.BindingHelperObserver;
import com.sun.javafx.binding.SetExpressionHelper;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
public abstract class SetBinding<E> extends SetExpression<E> implements Binding<ObservableSet<E>> {
public SetBinding() {
}
private final SetChangeListener<E> setChangeListener = new SetChangeListener<E>() {
@Override
public void onChanged(Change<? extends E> change) {
invalidateProperties();
onInvalidating();
SetExpressionHelper.fireValueChangedEvent(helper, change);
}
};
private ObservableSet<E> value;
private boolean valid = false;
private BindingHelperObserver observer;
private SetExpressionHelper<E> helper = null;
private SizeProperty size0;
private EmptyProperty empty0;
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
return SetBinding.this;
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
return SetBinding.this;
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
helper = SetExpressionHelper.addListener(helper, this, listener);
}
@Override
public void removeListener(InvalidationListener listener) {
helper = SetExpressionHelper.removeListener(helper, listener);
}
@Override
public void addListener(ChangeListener<? super ObservableSet<E>> listener) {
helper = SetExpressionHelper.addListener(helper, this, listener);
}
@Override
public void removeListener(ChangeListener<? super ObservableSet<E>> listener) {
helper = SetExpressionHelper.removeListener(helper, listener);
}
@Override
public void addListener(SetChangeListener<? super E> listener) {
helper = SetExpressionHelper.addListener(helper, this, listener);
}
@Override
public void removeListener(SetChangeListener<? super E> listener) {
helper = SetExpressionHelper.removeListener(helper, listener);
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
public final ObservableSet<E> get() {
if (!valid) {
value = computeValue();
valid = true;
if (value != null) {
value.addListener(setChangeListener);
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
value.removeListener(setChangeListener);
}
valid = false;
invalidateProperties();
onInvalidating();
SetExpressionHelper.fireValueChangedEvent(helper);
}
}
@Override
public final boolean isValid() {
return valid;
}
protected abstract ObservableSet<E> computeValue();
@Override
public String toString() {
return valid ? "SetBinding [value: " + get() + "]"
: "SetBinding [invalid]";
}
}
