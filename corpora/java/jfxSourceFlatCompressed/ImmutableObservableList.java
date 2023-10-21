package com.sun.javafx.collections;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
public class ImmutableObservableList<E> extends AbstractList<E> implements ObservableList<E> {
private final E[] elements;
public ImmutableObservableList(E... elements) {
this.elements = ((elements == null) || (elements.length == 0))?
null : Arrays.copyOf(elements, elements.length);
}
@Override
public void addListener(InvalidationListener listener) {
}
@Override
public void removeListener(InvalidationListener listener) {
}
@Override
public void addListener(ListChangeListener<? super E> listener) {
}
@Override
public void removeListener(ListChangeListener<? super E> listener) {
}
@Override
public boolean addAll(E... elements) {
throw new UnsupportedOperationException();
}
@Override
public boolean setAll(E... elements) {
throw new UnsupportedOperationException();
}
@Override
public boolean setAll(Collection<? extends E> col) {
throw new UnsupportedOperationException();
}
@Override
public boolean removeAll(E... elements) {
throw new UnsupportedOperationException();
}
@Override
public boolean retainAll(E... elements) {
throw new UnsupportedOperationException();
}
@Override
public void remove(int from, int to) {
throw new UnsupportedOperationException();
}
@Override
public E get(int index) {
if ((index < 0) || (index >= size())) {
throw new IndexOutOfBoundsException();
}
return elements[index];
}
@Override
public int size() {
return (elements == null)? 0 : elements.length;
}
}
