package com.sun.javafx.collections;
import javafx.beans.InvalidationListener;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
public class ObservableSetWrapper<E> implements ObservableSet<E> {
private final Set<E> backingSet;
private SetListenerHelper<E> listenerHelper;
public ObservableSetWrapper(Set<E> set) {
this.backingSet = set;
}
private class SimpleAddChange extends SetChangeListener.Change<E> {
private final E added;
public SimpleAddChange(E added) {
super(ObservableSetWrapper.this);
this.added = added;
}
@Override
public boolean wasAdded() {
return true;
}
@Override
public boolean wasRemoved() {
return false;
}
@Override
public E getElementAdded() {
return added;
}
@Override
public E getElementRemoved() {
return null;
}
@Override
public String toString() {
return "added " + added;
}
}
private class SimpleRemoveChange extends SetChangeListener.Change<E> {
private final E removed;
public SimpleRemoveChange(E removed) {
super(ObservableSetWrapper.this);
this.removed = removed;
}
@Override
public boolean wasAdded() {
return false;
}
@Override
public boolean wasRemoved() {
return true;
}
@Override
public E getElementAdded() {
return null;
}
@Override
public E getElementRemoved() {
return removed;
}
@Override
public String toString() {
return "removed " + removed;
}
}
private void callObservers(SetChangeListener.Change<E> change) {
SetListenerHelper.fireValueChangedEvent(listenerHelper, change);
}
@Override
public void addListener(InvalidationListener listener) {
listenerHelper = SetListenerHelper.addListener(listenerHelper, listener);
}
@Override
public void removeListener(InvalidationListener listener) {
listenerHelper = SetListenerHelper.removeListener(listenerHelper, listener);
}
@Override
public void addListener(SetChangeListener<?super E> observer) {
listenerHelper = SetListenerHelper.addListener(listenerHelper, observer);
}
@Override
public void removeListener(SetChangeListener<?super E> observer) {
listenerHelper = SetListenerHelper.removeListener(listenerHelper, observer);
}
@Override
public int size() {
return backingSet.size();
}
@Override
public boolean isEmpty() {
return backingSet.isEmpty();
}
@Override
public boolean contains(Object o) {
return backingSet.contains(o);
}
@Override
public Iterator iterator() {
return new Iterator<E>() {
private final Iterator<E> backingIt = backingSet.iterator();
private E lastElement;
@Override
public boolean hasNext() {
return backingIt.hasNext();
}
@Override
public E next() {
lastElement = backingIt.next();
return lastElement;
}
@Override
public void remove() {
backingIt.remove();
callObservers(new SimpleRemoveChange(lastElement));
}
};
}
@Override
public Object[] toArray() {
return backingSet.toArray();
}
@Override
public <T> T[] toArray(T[] a) {
return backingSet.toArray(a);
}
@Override
public boolean add(E o) {
boolean ret = backingSet.add(o);
if (ret) {
callObservers(new SimpleAddChange(o));
}
return ret;
}
@Override
public boolean remove(Object o) {
boolean ret = backingSet.remove(o);
if (ret) {
callObservers(new SimpleRemoveChange((E)o));
}
return ret;
}
@Override
public boolean containsAll(Collection<?> c) {
return backingSet.containsAll(c);
}
@Override
public boolean addAll(Collection<?extends E> c) {
boolean ret = false;
for (E element : c) {
ret |= add(element);
}
return ret;
}
@Override
public boolean retainAll(Collection<?> c) {
return removeRetain(c, false);
}
@Override
public boolean removeAll(Collection<?> c) {
return removeRetain(c, true);
}
private boolean removeRetain(Collection<?> c, boolean remove) {
boolean removed = false;
for (Iterator<E> i = backingSet.iterator(); i.hasNext();) {
E element = i.next();
if (remove == c.contains(element)) {
removed = true;
i.remove();
callObservers(new SimpleRemoveChange(element));
}
}
return removed;
}
@Override
public void clear() {
for (Iterator<E> i = backingSet.iterator(); i.hasNext(); ) {
E element = i.next();
i.remove();
callObservers(new SimpleRemoveChange(element));
}
}
@Override
public String toString() {
return backingSet.toString();
}
@Override
public boolean equals(Object obj) {
return backingSet.equals(obj);
}
@Override
public int hashCode() {
return backingSet.hashCode();
}
}
