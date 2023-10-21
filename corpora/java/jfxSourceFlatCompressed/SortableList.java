package com.sun.javafx.collections;
import java.util.Comparator;
import java.util.List;
public interface SortableList<E> extends List<E> {
public void sort();
public void sort(Comparator<? super E> comparator);
}
