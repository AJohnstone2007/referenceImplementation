package com.sun.javafx.util;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Iterator;
public class WeakReferenceQueue<E> {
private final ReferenceQueue garbage = new ReferenceQueue();
private Object strongRef = new Object();
private ListEntry head = new ListEntry(strongRef, garbage);
int size = 0;
@SuppressWarnings("unchecked")
public void add(E obj) {
cleanup();
size++;
new ListEntry(obj, garbage).insert(head.prev);
}
public void remove(E obj) {
cleanup();
ListEntry entry = head.next;
while (entry != head) {
Object other = entry.get();
if (other == obj) {
size--;
entry.remove();
return;
}
entry = entry.next;
}
}
public void cleanup() {
ListEntry entry;
while ((entry = (ListEntry) garbage.poll()) != null) {
size--;
entry.remove();
}
}
public Iterator<? super E> iterator() {
return new Iterator() {
private ListEntry index = head;
private Object next = null;
public boolean hasNext() {
next = null;
while (next == null) {
ListEntry nextIndex = index.prev;
if (nextIndex == head) {
break;
}
next = nextIndex.get();
if (next == null) {
size--;
nextIndex.remove();
}
}
return next != null;
}
public Object next() {
hasNext();
index = index.prev;
return next;
}
public void remove() {
if (index != head) {
ListEntry nextIndex = index.next;
size--;
index.remove();
index = nextIndex;
}
}
};
}
private static class ListEntry extends WeakReference {
ListEntry prev, next;
public ListEntry(Object o, ReferenceQueue queue) {
super(o, queue);
prev = this;
next = this;
}
public void insert(ListEntry where) {
prev = where;
next = where.next;
where.next = this;
next.prev = this;
}
public void remove() {
prev.next = next;
next.prev = prev;
next = this;
prev = this;
}
}
}
