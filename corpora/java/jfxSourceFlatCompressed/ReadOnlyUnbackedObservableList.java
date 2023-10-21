package com.sun.javafx.scene.control;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import javafx.beans.InvalidationListener;
import com.sun.javafx.collections.ListListenerHelper;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import java.util.Collections;
import java.util.function.Consumer;
import static com.sun.javafx.collections.ListListenerHelper.fireValueChangedEvent;
public abstract class ReadOnlyUnbackedObservableList<E> extends ObservableListBase<E> {
@Override public abstract E get(int i);
@Override public abstract int size();
public void _beginChange() {
beginChange();
}
public void _endChange() {
endChange();
}
public void _nextUpdate(int pos) {
nextUpdate(pos);
}
public void _nextSet(int idx, E old) {
nextSet(idx, old);
}
public void _nextReplace(int from, int to, List<? extends E> removed) {
nextReplace(from, to, removed);
}
public void _nextRemove(int idx, List<? extends E> removed) {
nextRemove(idx, removed);
}
public void _nextRemove(E o) {
int indexOfObject = indexOf(o);
_nextRemove(indexOfObject, o);
}
public void _nextRemove(int idx, E removed) {
nextRemove(idx, removed);
}
public void _nextPermutation(int from, int to, int[] perm) {
nextPermutation(from, to, perm);
}
public void _nextAdd(int from, int to) {
nextAdd(from, to);
}
public void fireChange(Runnable r) {
_beginChange();
r.run();
_endChange();
}
public void callObservers(Change<E> c) {
fireChange(c);
}
@Override public int indexOf(Object o) {
if (o == null) return -1;
for (int i = 0, max = size(); i < max; i++) {
Object obj = get(i);
if (o.equals(obj)) return i;
}
return -1;
}
@Override public int lastIndexOf(Object o) {
if (o == null) return -1;
for (int i = size() - 1; i >= 0; i--) {
Object obj = get(i);
if (o.equals(obj)) return i;
}
return -1;
}
@Override public boolean contains(Object o) {
return indexOf(o) != -1;
}
@Override public boolean containsAll(Collection<?> c) {
for (Object o : c) {
if (! contains(o)) {
return false;
}
}
return true;
}
@Override public boolean isEmpty() {
return size() == 0;
}
@Override public ListIterator<E> listIterator() {
return new SelectionListIterator<E>(this);
}
@Override public ListIterator<E> listIterator(int index) {
return new SelectionListIterator<E>(this, index);
}
@Override
public Iterator<E> iterator() {
return new SelectionListIterator<E>(this);
}
@Override public List<E> subList(final int fromIndex, final int toIndex) {
if (fromIndex < 0 || toIndex > size() || fromIndex > toIndex) {
throw new IndexOutOfBoundsException("[ fromIndex: " + fromIndex + ", toIndex: " + toIndex + ", size: " + size() + " ]");
}
final List<E> outer = this;
return new ReadOnlyUnbackedObservableList<E>() {
@Override public E get(int i) {
return outer.get(i + fromIndex);
}
@Override public int size() {
return toIndex - fromIndex;
}
};
}
@Override
public Object[] toArray() {
int max = size();
Object[] arr = new Object[max];
for (int i = 0; i < max; i++) {
arr[i] = get(i);
}
return arr;
}
@SuppressWarnings("unchecked")
@Override
public <T> T[] toArray(T[] a) {
Object[] elementData = toArray();
int size = elementData.length;
if (a.length < size)
return (T[]) Arrays.copyOf(elementData, size, a.getClass());
System.arraycopy(elementData, 0, a, 0, size);
if (a.length > size)
a[size] = null;
return a;
}
@Override
public String toString() {
Iterator<E> i = iterator();
if (! i.hasNext())
return "[]";
StringBuilder sb = new StringBuilder();
sb.append('[');
for (;;) {
E e = i.next();
sb.append(e == this ? "(this Collection)" : e);
if (! i.hasNext())
return sb.append(']').toString();
sb.append(", ");
}
}
@Override public boolean add(E e) {
throw new UnsupportedOperationException("Not supported.");
}
@Override public void add(int index, E element) {
throw new UnsupportedOperationException("Not supported.");
}
@Override public boolean addAll(Collection<? extends E> c) {
throw new UnsupportedOperationException("Not supported.");
}
@Override public boolean addAll(int index, Collection<? extends E> c) {
throw new UnsupportedOperationException("Not supported.");
}
@Override public boolean addAll(E... elements) {
throw new UnsupportedOperationException("Not supported.");
}
@Override public E set(int index, E element) {
throw new UnsupportedOperationException("Not supported.");
}
@Override public boolean setAll(Collection<? extends E> col) {
throw new UnsupportedOperationException("Not supported.");
}
@Override public boolean setAll(E... elements) {
throw new UnsupportedOperationException("Not supported.");
}
@Override public void clear() {
throw new UnsupportedOperationException("Not supported.");
}
@Override public E remove(int index) {
throw new UnsupportedOperationException("Not supported.");
}
@Override public boolean remove(Object o) {
throw new UnsupportedOperationException("Not supported.");
}
@Override public boolean removeAll(Collection<?> c) {
throw new UnsupportedOperationException("Not supported.");
}
@Override public boolean retainAll(Collection<?> c) {
throw new UnsupportedOperationException("Not supported.");
}
@Override public void remove(int from, int to) {
throw new UnsupportedOperationException("Not supported.");
}
@Override public boolean removeAll(E... elements) {
throw new UnsupportedOperationException("Not supported.");
}
@Override public boolean retainAll(E... elements) {
throw new UnsupportedOperationException("Not supported.");
}
private static class SelectionListIterator<E> implements ListIterator<E> {
private int pos;
private final ReadOnlyUnbackedObservableList<E> list;
public SelectionListIterator(ReadOnlyUnbackedObservableList<E> list) {
this(list, 0);
}
public SelectionListIterator(ReadOnlyUnbackedObservableList<E> list, int pos) {
this.list = list;
this.pos = pos;
}
@Override public boolean hasNext() {
return pos < list.size();
}
@Override public E next() {
if (!hasNext()) {
throw new NoSuchElementException();
}
return list.get(pos++);
}
@Override public boolean hasPrevious() {
return pos > 0;
}
@Override public E previous() {
if (!hasPrevious()) {
throw new NoSuchElementException();
}
return list.get(--pos);
}
@Override public int nextIndex() {
return pos;
}
@Override public int previousIndex() {
return pos - 1;
}
@Override public void remove() {
throw new UnsupportedOperationException("Not supported.");
}
@Override public void set(E e) {
throw new UnsupportedOperationException("Not supported.");
}
@Override public void add(E e) {
throw new UnsupportedOperationException("Not supported.");
}
}
}
