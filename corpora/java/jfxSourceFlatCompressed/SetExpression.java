package javafx.beans.binding;
import com.sun.javafx.binding.StringFormatter;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.value.ObservableSetValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
public abstract class SetExpression<E> implements ObservableSetValue<E> {
public SetExpression() {
}
private static final ObservableSet EMPTY_SET = new EmptyObservableSet();
private static class EmptyObservableSet<E> extends AbstractSet<E> implements ObservableSet<E> {
private static final Iterator iterator = new Iterator() {
@Override
public boolean hasNext() {
return false;
}
@Override
public Object next() {
throw new NoSuchElementException();
}
@Override
public void remove() {
throw new UnsupportedOperationException();
}
};
@Override
public Iterator<E> iterator() {
return iterator;
}
@Override
public int size() {
return 0;
}
@Override
public void addListener(SetChangeListener<? super E> setChangeListener) {
}
@Override
public void removeListener(SetChangeListener<? super E> setChangeListener) {
}
@Override
public void addListener(InvalidationListener listener) {
}
@Override
public void removeListener(InvalidationListener listener) {
}
}
@Override
public ObservableSet<E> getValue() {
return get();
}
public static <E> SetExpression<E> setExpression(final ObservableSetValue<E> value) {
if (value == null) {
throw new NullPointerException("Set must be specified.");
}
return value instanceof SetExpression ? (SetExpression<E>) value
: new SetBinding<E>() {
{
super.bind(value);
}
@Override
public void dispose() {
super.unbind(value);
}
@Override
protected ObservableSet<E> computeValue() {
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
public BooleanBinding isEqualTo(final ObservableSet<?> other) {
return Bindings.equal(this, other);
}
public BooleanBinding isNotEqualTo(final ObservableSet<?> other) {
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
final ObservableSet<E> set = get();
return (set == null)? EMPTY_SET.size() : set.size();
}
@Override
public boolean isEmpty() {
final ObservableSet<E> set = get();
return (set == null)? EMPTY_SET.isEmpty() : set.isEmpty();
}
@Override
public boolean contains(Object obj) {
final ObservableSet<E> set = get();
return (set == null)? EMPTY_SET.contains(obj) : set.contains(obj);
}
@Override
public Iterator<E> iterator() {
final ObservableSet<E> set = get();
return (set == null)? EMPTY_SET.iterator() : set.iterator();
}
@Override
public Object[] toArray() {
final ObservableSet<E> set = get();
return (set == null)? EMPTY_SET.toArray() : set.toArray();
}
@Override
public <T> T[] toArray(T[] array) {
final ObservableSet<E> set = get();
return (set == null)? (T[]) EMPTY_SET.toArray(array) : set.toArray(array);
}
@Override
public boolean add(E element) {
final ObservableSet<E> set = get();
return (set == null)? EMPTY_SET.add(element) : set.add(element);
}
@Override
public boolean remove(Object obj) {
final ObservableSet<E> set = get();
return (set == null)? EMPTY_SET.remove(obj) : set.remove(obj);
}
@Override
public boolean containsAll(Collection<?> objects) {
final ObservableSet<E> set = get();
return (set == null)? EMPTY_SET.contains(objects) : set.containsAll(objects);
}
@Override
public boolean addAll(Collection<? extends E> elements) {
final ObservableSet<E> set = get();
return (set == null)? EMPTY_SET.addAll(elements) : set.addAll(elements);
}
@Override
public boolean removeAll(Collection<?> objects) {
final ObservableSet<E> set = get();
return (set == null)? EMPTY_SET.removeAll(objects) : set.removeAll(objects);
}
@Override
public boolean retainAll(Collection<?> objects) {
final ObservableSet<E> set = get();
return (set == null)? EMPTY_SET.retainAll(objects) : set.retainAll(objects);
}
@Override
public void clear() {
final ObservableSet<E> set = get();
if (set == null) {
EMPTY_SET.clear();
} else {
set.clear();
}
}
}
