package javafx.beans.property;
import javafx.collections.ObservableList;
import static javafx.collections.ListChangeListener.Change;
public class ReadOnlyListWrapper<E> extends SimpleListProperty<E> {
private ReadOnlyPropertyImpl readOnlyProperty;
public ReadOnlyListWrapper() {
}
public ReadOnlyListWrapper(ObservableList<E> initialValue) {
super(initialValue);
}
public ReadOnlyListWrapper(Object bean, String name) {
super(bean, name);
}
public ReadOnlyListWrapper(Object bean, String name,
ObservableList<E> initialValue) {
super(bean, name, initialValue);
}
public ReadOnlyListProperty<E> getReadOnlyProperty() {
if (readOnlyProperty == null) {
readOnlyProperty = new ReadOnlyPropertyImpl();
}
return readOnlyProperty;
}
@Override
protected void fireValueChangedEvent() {
super.fireValueChangedEvent();
if (readOnlyProperty != null) {
readOnlyProperty.fireValueChangedEvent();
}
}
@Override
protected void fireValueChangedEvent(Change<? extends E> change) {
super.fireValueChangedEvent(change);
if (readOnlyProperty != null) {
change.reset();
readOnlyProperty.fireValueChangedEvent(change);
}
}
private class ReadOnlyPropertyImpl extends ReadOnlyListPropertyBase<E> {
@Override
public ObservableList<E> get() {
return ReadOnlyListWrapper.this.get();
}
@Override
public Object getBean() {
return ReadOnlyListWrapper.this.getBean();
}
@Override
public String getName() {
return ReadOnlyListWrapper.this.getName();
}
@Override
public ReadOnlyIntegerProperty sizeProperty() {
return ReadOnlyListWrapper.this.sizeProperty();
}
@Override
public ReadOnlyBooleanProperty emptyProperty() {
return ReadOnlyListWrapper.this.emptyProperty();
}
}
}
