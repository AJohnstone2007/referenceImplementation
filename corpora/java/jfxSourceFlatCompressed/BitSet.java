package com.sun.javafx.css;
import com.sun.javafx.collections.SetListenerHelper;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
abstract class BitSet<T> implements ObservableSet<T> {
protected BitSet () {
this.bits = EMPTY_SET;
}
@Override
public int size() {
int size = 0;
if (bits.length > 0) {
for (int n = 0; n < bits.length; n++) {
final long mask = bits[n];
if (mask != 0) {
size += Long.bitCount(mask);
}
}
}
return size;
}
@Override
public boolean isEmpty() {
if (bits.length > 0) {
for (int n = 0; n < bits.length; n++) {
final long mask = bits[n];
if (mask != 0) {
return false;
}
}
}
return true;
}
@Override
public Iterator<T> iterator() {
return new Iterator<T>() {
int next = -1;
int element = 0;
int index = -1;
@Override
public boolean hasNext() {
if (bits == null || bits.length == 0) {
return false;
}
boolean found = false;
do {
if (++next >= Long.SIZE) {
if (++element < bits.length) {
next = 0;
} else {
return false;
}
}
long bit = 1l << next;
found = (bit & bits[element]) == bit;
} while( !found );
if (found) {
index = Long.SIZE * element + next;
}
return found;
}
@Override
public T next() {
try {
return getT(index);
} catch (IndexOutOfBoundsException e) {
throw new NoSuchElementException("["+element+"]["+next+"]");
}
}
@Override
public void remove() {
try {
T t = getT(index);
BitSet.this.remove(t);
} catch (IndexOutOfBoundsException e) {
throw new NoSuchElementException("["+element+"]["+next+"]");
}
}
};
}
@Override
public boolean add(T t) {
if (t == null) {
return false;
}
final int element = getIndex(t) / Long.SIZE;
final long bit = 1l << (getIndex(t) % Long.SIZE);
if (element >= bits.length) {
final long[] temp = new long[element + 1];
System.arraycopy(bits, 0, temp, 0, bits.length);
bits = temp;
}
final long temp = bits[element];
bits[element] = temp | bit;
final boolean modified = (bits[element] != temp);
if (modified && SetListenerHelper.hasListeners(listenerHelper)){
notifyObservers(t, Change.ELEMENT_ADDED);
}
return modified;
}
@Override
public boolean remove(Object o) {
if (o == null) {
return false;
}
T t = cast(o);
final int element = getIndex(t) / Long.SIZE;
final long bit = 1l << (getIndex(t) % Long.SIZE);
if (element >= bits.length) {
return false;
}
final long temp = bits[element];
bits[element] = temp & ~bit;
final boolean modified = (bits[element] != temp);
if (modified) {
if (SetListenerHelper.hasListeners(listenerHelper)) {
notifyObservers(t, Change.ELEMENT_REMOVED);
}
boolean isEmpty = true;
for (int n=0; n<bits.length && isEmpty; n++) {
isEmpty &= bits[n] == 0;
}
if (isEmpty) bits = EMPTY_SET;
}
return modified;
}
@Override
public boolean contains(Object o) {
if (o == null) {
return false;
}
final T t = cast(o);
final int element = getIndex(t) / Long.SIZE;
final long bit = 1l << (getIndex(t) % Long.SIZE);
return (element < bits.length) && (bits[element] & bit) == bit;
}
@Override
public boolean containsAll(Collection<?> c) {
if (c == null || this.getClass() != c.getClass()) {
return false;
}
BitSet other = (BitSet)c;
if (bits.length == 0 && other.bits.length == 0) {
return true;
}
if (bits.length < other.bits.length) {
return false;
}
for (int n = 0, max = other.bits.length; n < max; n++) {
if ((bits[n] & other.bits[n]) != other.bits[n]) {
return false;
}
}
return true;
}
@Override
public boolean addAll(Collection<? extends T> c) {
if (c == null || this.getClass() != c.getClass()) {
return false;
}
boolean modified = false;
BitSet other = (BitSet)c;
final long[] maskOne = this.bits;
final long[] maskTwo = other.bits;
final int a = maskOne.length;
final int b = maskTwo.length;
final int max = a < b ? b : a;
final long[] union = max > 0 ? new long[max] : EMPTY_SET;
for(int n = 0; n < max; n++) {
if (n < maskOne.length && n < maskTwo.length) {
union[n] = maskOne[n] | maskTwo[n];
modified |= (union[n] != maskOne[n]);
} else if (n < maskOne.length) {
union[n] = maskOne[n];
modified |= false;
} else {
union[n] = maskTwo[n];
modified = true;
}
}
if (modified) {
if (SetListenerHelper.hasListeners(listenerHelper)) {
for (int n = 0; n < max; n++) {
long bitsAdded = 0l;
if (n < maskOne.length && n < maskTwo.length) {
bitsAdded = ~maskOne[n] & maskTwo[n];
} else if (n < maskOne.length) {
continue;
} else {
bitsAdded = maskTwo[n];
}
for(int bit = 0; bit < Long.SIZE; bit++) {
long m = 1l << bit;
if ((m & bitsAdded) == m) {
T t = getT(n*Long.SIZE + bit);
notifyObservers(t, Change.ELEMENT_ADDED);
}
}
}
}
this.bits = union;
}
return modified;
}
@Override
public boolean retainAll(Collection<?> c) {
if (c == null || this.getClass() != c.getClass()) {
clear();
return true;
}
boolean modified = false;
BitSet other = (BitSet)c;
final long[] maskOne = this.bits;
final long[] maskTwo = other.bits;
final int a = maskOne.length;
final int b = maskTwo.length;
final int max = a < b ? a : b;
final long[] intersection = max > 0 ? new long[max] : EMPTY_SET;
modified |= (maskOne.length > max);
boolean isEmpty = true;
for(int n = 0; n < max; n++) {
intersection[n] = maskOne[n] & maskTwo[n];
modified |= intersection[n] != maskOne[n];
isEmpty &= intersection[n] == 0;
}
if (modified) {
if (SetListenerHelper.hasListeners(listenerHelper)) {
for (int n = 0; n < maskOne.length; n++) {
long bitsRemoved = 0l;
if (n < maskTwo.length) {
bitsRemoved = maskOne[n] & ~maskTwo[n];
} else {
bitsRemoved = maskOne[n];
}
for(int bit = 0; bit < Long.SIZE; bit++) {
long m = 1l << bit;
if ((m & bitsRemoved) == m) {
T t = getT(n*Long.SIZE + bit);
notifyObservers(t, Change.ELEMENT_REMOVED);
}
}
}
}
this.bits = isEmpty == false ? intersection : EMPTY_SET;
}
return modified;
}
@Override
public boolean removeAll(Collection<?> c) {
if (c == null || this.getClass() != c.getClass()) {
return false;
}
boolean modified = false;
BitSet other = (BitSet)c;
final long[] maskOne = bits;
final long[] maskTwo = other.bits;
final int a = maskOne.length;
final int b = maskTwo.length;
final int max = a < b ? a : b;
final long[] difference = max > 0 ? new long[max] : EMPTY_SET;
boolean isEmpty = true;
for(int n = 0; n < max; n++) {
difference[n] = maskOne[n] & ~maskTwo[n];
modified |= difference[n] != maskOne[n];
isEmpty &= difference[n] == 0;
}
if (modified) {
if (SetListenerHelper.hasListeners(listenerHelper)) {
for (int n = 0; n < max; n++) {
long bitsRemoved = maskOne[n] & maskTwo[n];
for(int bit = 0; bit < Long.SIZE; bit++) {
long m = 1l << bit;
if ((m & bitsRemoved) == m) {
T t = getT(n*Long.SIZE + bit);
notifyObservers(t, Change.ELEMENT_REMOVED);
}
}
}
}
this.bits = isEmpty == false ? difference : EMPTY_SET;
}
return modified;
}
@Override
public void clear() {
for (int n = 0; n < bits.length; n++) {
long bitsRemoved = bits[n];
for(int b = 0; b < Long.SIZE; b++) {
long m = 1l << b;
if ((m & bitsRemoved) == m) {
T t = getT(n*Long.SIZE + b);
notifyObservers(t, Change.ELEMENT_REMOVED);
}
}
}
bits = EMPTY_SET;
}
@Override
public int hashCode() {
int hash = 7;
if (bits.length > 0) {
for (int n = 0; n < bits.length; n++) {
final long mask = bits[n];
hash = 71 * hash + (int)(mask ^ (mask >>> 32));
}
}
return hash;
}
@Override
public boolean equals(Object obj) {
if (this == obj) {
return true;
}
if (obj == null || this.getClass() != obj.getClass()) {
return false;
}
final BitSet other = (BitSet) obj;
final int a = this.bits != null ? this.bits.length : 0;
final int b = other.bits != null ? other.bits.length : 0;
if (a != b) return false;
for(int m=0; m<a; m++) {
final long m0 = this.bits[m];
final long m1 = other.bits[m];
if (m0 != m1) {
return false;
}
}
return true;
}
abstract protected T getT(int index);
abstract protected int getIndex(T t);
abstract protected T cast(Object o);
protected long[] getBits() {
return bits;
}
private static final long[] EMPTY_SET = new long[0];
private long[] bits;
private SetListenerHelper<T> listenerHelper;
private class Change extends SetChangeListener.Change<T> {
private static final boolean ELEMENT_ADDED = false;
private static final boolean ELEMENT_REMOVED = true;
private final T element;
private final boolean removed;
public Change(T element, boolean removed) {
super(FXCollections.unmodifiableObservableSet(BitSet.this));
this.element = element;
this.removed = removed;
}
@Override
public boolean wasAdded() {
return removed != ELEMENT_REMOVED;
}
@Override
public boolean wasRemoved() {
return removed;
}
@Override
public T getElementAdded() {
return removed ? null : element;
}
@Override
public T getElementRemoved() {
return removed ? element : null;
}
}
@Override
public void addListener(SetChangeListener<? super T> setChangeListener) {
if (setChangeListener != null) {
listenerHelper = SetListenerHelper.addListener(listenerHelper, setChangeListener);
}
}
@Override
public void removeListener(SetChangeListener<? super T> setChangeListener) {
if (setChangeListener != null) {
SetListenerHelper.removeListener(listenerHelper, setChangeListener);
}
}
@Override
public void addListener(InvalidationListener invalidationListener) {
if (invalidationListener != null) {
listenerHelper = SetListenerHelper.addListener(listenerHelper, invalidationListener);
}
}
@Override
public void removeListener(InvalidationListener invalidationListener) {
if (invalidationListener != null) {
SetListenerHelper.removeListener(listenerHelper, invalidationListener);
}
}
private void notifyObservers(T element, boolean removed) {
if (element != null && SetListenerHelper.hasListeners(listenerHelper)) {
Change change = new Change(element, removed);
SetListenerHelper.fireValueChangedEvent(listenerHelper, change);
}
}
}
