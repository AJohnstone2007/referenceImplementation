package com.sun.javafx;
import java.util.AbstractList;
import java.util.RandomAccess;
public class UnmodifiableArrayList<T> extends AbstractList<T> implements RandomAccess {
private T[] elements;
private final int size;
public UnmodifiableArrayList(T[] elements, int size) {
assert elements == null ? size == 0 : size <= elements.length;
this.size = size;
this.elements = elements;
}
@Override public T get(int index) {
return elements[index];
}
@Override public int size() {
return size;
}
}
