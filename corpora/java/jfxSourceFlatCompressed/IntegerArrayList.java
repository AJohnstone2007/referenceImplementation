package com.javafx.experiments.importers.obj;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.Vector;
public class IntegerArrayList extends AbstractList<Integer>
implements List<Integer>, RandomAccess, Cloneable, java.io.Serializable {
private transient int[] elementData;
private int size;
public IntegerArrayList(int initialCapacity) {
super();
if (initialCapacity < 0) {
throw new IllegalArgumentException(
"Illegal Capacity: " +
initialCapacity);
}
this.elementData = new int[initialCapacity];
}
public IntegerArrayList() {
this(10);
}
public IntegerArrayList(Collection<? extends Integer> c) {
elementData = new int[c.size()];
int i = 0;
for (Integer d : c) {
elementData[i] = d;
i++;
}
size = elementData.length;
}
public void trimToSize() {
modCount++;
int oldCapacity = elementData.length;
if (size < oldCapacity) {
elementData = Arrays.copyOf(elementData, size);
}
}
public void ensureCapacity(int minCapacity) {
if (minCapacity > 0) {
ensureCapacityInternal(minCapacity);
}
}
private void ensureCapacityInternal(int minCapacity) {
modCount++;
if (minCapacity - elementData.length > 0) {
grow(minCapacity);
}
}
private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
private void grow(int minCapacity) {
int oldCapacity = elementData.length;
int newCapacity = oldCapacity + (oldCapacity >> 1);
if (newCapacity - minCapacity < 0)
newCapacity = minCapacity;
if (newCapacity - MAX_ARRAY_SIZE > 0)
newCapacity = hugeCapacity(minCapacity);
elementData = Arrays.copyOf(elementData, newCapacity);
}
private static int hugeCapacity(int minCapacity) {
if (minCapacity < 0)
throw new OutOfMemoryError();
return (minCapacity > MAX_ARRAY_SIZE) ?
Integer.MAX_VALUE :
MAX_ARRAY_SIZE;
}
@Override public int size() {
return size;
}
@Override public boolean isEmpty() {
return size == 0;
}
@Override public boolean contains(Object o) {
return indexOf(o) >= 0;
}
@Override public int indexOf(Object o) {
if (o instanceof Integer) {
for (int i = 0; i < size; i++) {
if (o.equals(elementData[i])) {
return i;
}
}
}
return -1;
}
@Override public int lastIndexOf(Object o) {
if (o instanceof Integer) {
for (int i = size - 1; i >= 0; i--)
if (o.equals(elementData[i]))
return i;
}
return -1;
}
@Override public Object clone() {
try {
@SuppressWarnings("unchecked")
IntegerArrayList v = (IntegerArrayList) super.clone();
v.elementData = Arrays.copyOf(elementData, size);
v.modCount = 0;
return v;
} catch (CloneNotSupportedException e) {
throw new InternalError();
}
}
@Override public Object[] toArray() {
Integer[] array = new Integer[size];
for (int i = 0; i < size; i++) {
array[i] = elementData[i];
}
return array;
}
@SuppressWarnings("unchecked")
@Override public <T> T[] toArray(T[] a) {
if (a.length < size) {
return (T[]) Arrays.copyOf(toArray(), size, a.getClass());
}
System.arraycopy(elementData, 0, a, 0, size);
if (a.length > size)
a[size] = null;
return a;
}
public int[] toIntArray() {
int[] res = new int[size];
System.arraycopy(elementData, 0, res, 0, size);
return res;
}
@SuppressWarnings("unchecked") Integer elementData(int index) {
return (Integer) elementData[index];
}
@Override public Integer get(int index) {
rangeCheck(index);
return elementData(index);
}
@Override public Integer set(int index, Integer element) {
rangeCheck(index);
Integer oldValue = elementData(index);
elementData[index] = element;
return oldValue;
}
@Override public boolean add(Integer e) {
ensureCapacityInternal(size + 1);
elementData[size++] = e;
return true;
}
@Override public void add(int index, Integer element) {
rangeCheckForAdd(index);
ensureCapacityInternal(size + 1);
System.arraycopy(
elementData, index, elementData, index + 1,
size - index);
elementData[index] = element;
size++;
}
@Override public Integer remove(int index) {
rangeCheck(index);
modCount++;
Integer oldValue = elementData(index);
int numMoved = size - index - 1;
if (numMoved > 0)
System.arraycopy(
elementData, index + 1, elementData, index,
numMoved);
elementData[--size] = 0;
return oldValue;
}
@Override public boolean remove(Object o) {
if (o instanceof Integer) {
for (int index = 0; index < size; index++)
if (o.equals(elementData[index])) {
fastRemove(index);
return true;
}
}
return false;
}
private void fastRemove(int index) {
modCount++;
int numMoved = size - index - 1;
if (numMoved > 0)
System.arraycopy(
elementData, index + 1, elementData, index,
numMoved);
elementData[--size] = 0;
}
@Override public void clear() {
modCount++;
for (int i = 0; i < size; i++)
elementData[i] = 0;
size = 0;
}
@Override public boolean addAll(Collection<? extends Integer> c) {
Object[] a = c.toArray();
int numNew = a.length;
ensureCapacityInternal(size + numNew);
System.arraycopy(a, 0, elementData, size, numNew);
size += numNew;
return numNew != 0;
}
@Override public boolean addAll(int index, Collection<? extends Integer> c) {
rangeCheckForAdd(index);
Object[] a = c.toArray();
int numNew = a.length;
ensureCapacityInternal(size + numNew);
int numMoved = size - index;
if (numMoved > 0)
System.arraycopy(
elementData, index, elementData, index + numNew,
numMoved);
System.arraycopy(a, 0, elementData, index, numNew);
size += numNew;
return numNew != 0;
}
@Override protected void removeRange(int fromIndex, int toIndex) {
modCount++;
int numMoved = size - toIndex;
System.arraycopy(
elementData, toIndex, elementData, fromIndex,
numMoved);
int newSize = size - (toIndex - fromIndex);
while (size != newSize)
elementData[--size] = 0;
}
private void rangeCheck(int index) {
if (index >= size)
throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
}
private void rangeCheckForAdd(int index) {
if (index > size || index < 0)
throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
}
private String outOfBoundsMsg(int index) {
return "Index: " + index + ", Size: " + size;
}
@Override public boolean removeAll(Collection<?> c) {
return batchRemove(c, false);
}
@Override public boolean retainAll(Collection<?> c) {
return batchRemove(c, true);
}
private boolean batchRemove(Collection<?> c, boolean complement) {
final int[] elementData = this.elementData;
int r = 0, w = 0;
boolean modified = false;
try {
for (; r < size; r++)
if (c.contains(elementData[r]) == complement)
elementData[w++] = elementData[r];
} finally {
if (r != size) {
System.arraycopy(
elementData, r,
elementData, w,
size - r);
w += size - r;
}
if (w != size) {
for (int i = w; i < size; i++)
elementData[i] = 0;
modCount += size - w;
size = w;
modified = true;
}
}
return modified;
}
private void writeObject(java.io.ObjectOutputStream s)
throws java.io.IOException {
int expectedModCount = modCount;
s.defaultWriteObject();
s.writeInt(elementData.length);
for (int i = 0; i < size; i++)
s.writeObject(elementData[i]);
if (modCount != expectedModCount) {
throw new ConcurrentModificationException();
}
}
private void readObject(java.io.ObjectInputStream s)
throws java.io.IOException, ClassNotFoundException {
s.defaultReadObject();
int arrayLength = s.readInt();
int[] a = elementData = new int[arrayLength];
for (int i = 0; i < size; i++)
a[i] = (Integer) s.readObject();
}
@Override public ListIterator<Integer> listIterator(int index) {
if (index < 0 || index > size)
throw new IndexOutOfBoundsException("Index: " + index);
return new ListItr(index);
}
@Override public ListIterator<Integer> listIterator() {
return new ListItr(0);
}
@Override public Iterator<Integer> iterator() {
return new Itr();
}
private class Itr implements Iterator<Integer> {
int cursor;
int lastRet = -1;
int expectedModCount = modCount;
@Override public boolean hasNext() {
return cursor != size;
}
@SuppressWarnings("unchecked")
@Override public Integer next() {
checkForComodification();
int i = cursor;
if (i >= size)
throw new NoSuchElementException();
int[] elementData = IntegerArrayList.this.elementData;
if (i >= elementData.length)
throw new ConcurrentModificationException();
cursor = i + 1;
return (Integer) elementData[lastRet = i];
}
@Override public void remove() {
if (lastRet < 0)
throw new IllegalStateException();
checkForComodification();
try {
IntegerArrayList.this.remove(lastRet);
cursor = lastRet;
lastRet = -1;
expectedModCount = modCount;
} catch (IndexOutOfBoundsException ex) {
throw new ConcurrentModificationException();
}
}
final void checkForComodification() {
if (modCount != expectedModCount)
throw new ConcurrentModificationException();
}
}
private class ListItr extends Itr implements ListIterator<Integer> {
ListItr(int index) {
super();
cursor = index;
}
@Override public boolean hasPrevious() {
return cursor != 0;
}
@Override public int nextIndex() {
return cursor;
}
@Override public int previousIndex() {
return cursor - 1;
}
@SuppressWarnings("unchecked")
@Override public Integer previous() {
checkForComodification();
int i = cursor - 1;
if (i < 0)
throw new NoSuchElementException();
int[] elementData = IntegerArrayList.this.elementData;
if (i >= elementData.length)
throw new ConcurrentModificationException();
cursor = i;
return (Integer) elementData[lastRet = i];
}
@Override public void set(Integer e) {
if (lastRet < 0)
throw new IllegalStateException();
checkForComodification();
try {
IntegerArrayList.this.set(lastRet, e);
} catch (IndexOutOfBoundsException ex) {
throw new ConcurrentModificationException();
}
}
@Override public void add(Integer e) {
checkForComodification();
try {
int i = cursor;
IntegerArrayList.this.add(i, e);
cursor = i + 1;
lastRet = -1;
expectedModCount = modCount;
} catch (IndexOutOfBoundsException ex) {
throw new ConcurrentModificationException();
}
}
}
@Override public List<Integer> subList(int fromIndex, int toIndex) {
subListRangeCheck(fromIndex, toIndex, size);
return new IntegerArrayList.SubList(this, 0, fromIndex, toIndex);
}
static void subListRangeCheck(int fromIndex, int toIndex, int size) {
if (fromIndex < 0)
throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
if (toIndex > size)
throw new IndexOutOfBoundsException("toIndex = " + toIndex);
if (fromIndex > toIndex)
throw new IllegalArgumentException(
"fromIndex(" + fromIndex +
") > toIndex(" + toIndex + ")");
}
private class SubList extends IntegerArrayList implements RandomAccess {
private final IntegerArrayList parent;
private final int parentOffset;
private final int offset;
int size;
SubList(
IntegerArrayList parent,
int offset, int fromIndex, int toIndex) {
this.parent = parent;
this.parentOffset = fromIndex;
this.offset = offset + fromIndex;
this.size = toIndex - fromIndex;
this.modCount = IntegerArrayList.this.modCount;
}
@Override
public int[] toIntArray() {
int[] res = new int[size];
System.arraycopy(elementData, offset, res, 0, size);
return res;
}
@Override public Integer set(int index, Integer e) {
rangeCheck(index);
checkForComodification();
Integer oldValue = IntegerArrayList.this.elementData(offset + index);
IntegerArrayList.this.elementData[offset + index] = e;
return oldValue;
}
@Override public Integer get(int index) {
rangeCheck(index);
checkForComodification();
return IntegerArrayList.this.elementData(offset + index);
}
@Override public int size() {
checkForComodification();
return this.size;
}
@Override public void add(int index, Integer e) {
rangeCheckForAdd(index);
checkForComodification();
parent.add(parentOffset + index, e);
this.modCount = parent.modCount;
this.size++;
}
@Override public Integer remove(int index) {
rangeCheck(index);
checkForComodification();
Integer result = parent.remove(parentOffset + index);
this.modCount = parent.modCount;
this.size--;
return result;
}
@Override protected void removeRange(int fromIndex, int toIndex) {
checkForComodification();
parent.removeRange(
parentOffset + fromIndex,
parentOffset + toIndex);
this.modCount = parent.modCount;
this.size -= toIndex - fromIndex;
}
@Override public boolean addAll(Collection<? extends Integer> c) {
return addAll(this.size, c);
}
@Override public boolean addAll(int index, Collection<? extends Integer> c) {
rangeCheckForAdd(index);
int cSize = c.size();
if (cSize == 0)
return false;
checkForComodification();
parent.addAll(parentOffset + index, c);
this.modCount = parent.modCount;
this.size += cSize;
return true;
}
@Override public Iterator<Integer> iterator() {
return listIterator();
}
@Override public ListIterator<Integer> listIterator(final int index) {
checkForComodification();
rangeCheckForAdd(index);
final int offset = this.offset;
return new ListIterator<Integer>() {
int cursor = index;
int lastRet = -1;
int expectedModCount = IntegerArrayList.this.modCount;
@Override public boolean hasNext() {
return cursor != IntegerArrayList.SubList.this.size;
}
@SuppressWarnings("unchecked")
@Override public Integer next() {
checkForComodification();
int i = cursor;
if (i >= IntegerArrayList.SubList.this.size)
throw new NoSuchElementException();
int[] elementData = IntegerArrayList.this.elementData;
if (offset + i >= elementData.length)
throw new ConcurrentModificationException();
cursor = i + 1;
return (Integer) elementData[offset + (lastRet = i)];
}
@Override public boolean hasPrevious() {
return cursor != 0;
}
@SuppressWarnings("unchecked")
@Override public Integer previous() {
checkForComodification();
int i = cursor - 1;
if (i < 0)
throw new NoSuchElementException();
int[] elementData = IntegerArrayList.this.elementData;
if (offset + i >= elementData.length)
throw new ConcurrentModificationException();
cursor = i;
return (Integer) elementData[offset + (lastRet = i)];
}
@Override public int nextIndex() {
return cursor;
}
@Override public int previousIndex() {
return cursor - 1;
}
@Override public void remove() {
if (lastRet < 0)
throw new IllegalStateException();
checkForComodification();
try {
IntegerArrayList.SubList.this.remove(lastRet);
cursor = lastRet;
lastRet = -1;
expectedModCount = IntegerArrayList.this.modCount;
} catch (IndexOutOfBoundsException ex) {
throw new ConcurrentModificationException();
}
}
@Override public void set(Integer e) {
if (lastRet < 0)
throw new IllegalStateException();
checkForComodification();
try {
IntegerArrayList.this.set(offset + lastRet, e);
} catch (IndexOutOfBoundsException ex) {
throw new ConcurrentModificationException();
}
}
@Override public void add(Integer e) {
checkForComodification();
try {
int i = cursor;
IntegerArrayList.SubList.this.add(i, e);
cursor = i + 1;
lastRet = -1;
expectedModCount = IntegerArrayList.this.modCount;
} catch (IndexOutOfBoundsException ex) {
throw new ConcurrentModificationException();
}
}
final void checkForComodification() {
if (expectedModCount != IntegerArrayList.this.modCount)
throw new ConcurrentModificationException();
}
};
}
@Override public List<Integer> subList(int fromIndex, int toIndex) {
subListRangeCheck(fromIndex, toIndex, size);
return new IntegerArrayList.SubList(this, offset, fromIndex, toIndex);
}
private void rangeCheck(int index) {
if (index < 0 || index >= this.size)
throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
}
private void rangeCheckForAdd(int index) {
if (index < 0 || index > this.size)
throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
}
private String outOfBoundsMsg(int index) {
return "Index: " + index + ", Size: " + this.size;
}
private void checkForComodification() {
if (IntegerArrayList.this.modCount != this.modCount)
throw new ConcurrentModificationException();
}
}
}
