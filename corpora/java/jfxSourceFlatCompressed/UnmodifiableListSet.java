package com.sun.javafx.collections;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.List;
public final class UnmodifiableListSet<E> extends AbstractSet<E> {
private List<E> backingList;
public UnmodifiableListSet(List<E> backingList) {
if (backingList == null) throw new NullPointerException();
this.backingList = backingList;
}
@Override public Iterator<E> iterator() {
final Iterator<E> itr = backingList.iterator();
return new Iterator<E>() {
@Override public boolean hasNext() {
return itr.hasNext();
}
@Override public E next() {
return itr.next();
}
@Override public void remove() {
throw new UnsupportedOperationException();
}
};
}
@Override public int size() {
return backingList.size();
}
}
