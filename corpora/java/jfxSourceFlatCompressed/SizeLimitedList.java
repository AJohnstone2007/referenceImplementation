package com.sun.javafx.scene.control;
import java.util.LinkedList;
import java.util.List;
public class SizeLimitedList<E> {
private final int maxSize;
private final List<E> backingList;
public SizeLimitedList(int maxSize) {
this.maxSize = maxSize;
this.backingList = new LinkedList<>();
}
public E get(int index) {
return backingList.get(index);
}
public void add(E item) {
backingList.add(0, item);
if (backingList.size() > maxSize) {
backingList.remove(maxSize);
}
}
public int size() {
return backingList.size();
}
public boolean contains(E item) {
return backingList.contains(item);
}
}
