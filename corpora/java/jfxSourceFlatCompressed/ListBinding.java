package javafx.beans.binding;
import com.sun.javafx.binding.BindingHelperObserver;
import com.sun.javafx.binding.ListExpressionHelper;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
public abstract class ListBinding<E> extends ListExpression<E> implements Binding<ObservableList<E>> {
public ListBinding() {
}
private final ListChangeListener<E> listChangeListener = new ListChangeListener<E>() {
@Override
public void onChanged(Change<? extends E> change) {
invalidateProperties();
onInvalidating();
ListExpressionHelper.fireValueChangedEvent(helper, change);
}
};
private ObservableList<E> value;
private boolean valid = false;
private BindingHelperObserver observer;
private ListExpressionHelper<E> helper = null;
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
return ListBinding.this;
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
return ListBinding.this;
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
helper = ListExpressionHelper.addListener(helper, this, listener);
}
@Override
public void removeListener(InvalidationListener listener) {
helper = ListExpressionHelper.removeListener(helper, listener);
}
@Override
public void addListener(ChangeListener<? super ObservableList<E>> listener) {
helper = ListExpressionHelper.addListener(helper, this, listener);
}
@Override
public void removeListener(ChangeListener<? super ObservableList<E>> listener) {
helper = ListExpressionHelper.removeListener(helper, listener);
}
@Override
public void addListener(ListChangeListener<? super E> listener) {
helper = ListExpressionHelper.addListener(helper, this, listener);
}
@Override
public void removeListener(ListChangeListener<? super E> listener) {
helper = ListExpressionHelper.removeListener(helper, listener);
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
public final ObservableList<E> get() {
if (!valid) {
value = computeValue();
valid = true;
if (value != null) {
value.addListener(listChangeListener);
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
value.removeListener(listChangeListener);
}
valid = false;
invalidateProperties();
onInvalidating();
ListExpressionHelper.fireValueChangedEvent(helper);
}
}
@Override
public final boolean isValid() {
return valid;
}
protected abstract ObservableList<E> computeValue();
@Override
public String toString() {
return valid ? "ListBinding [value: " + get() + "]"
: "ListBinding [invalid]";
}
}
