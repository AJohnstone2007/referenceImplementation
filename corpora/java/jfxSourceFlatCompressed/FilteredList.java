package javafx.collections.transformation;
import com.sun.javafx.collections.NonIterableChange.GenericAddRemoveChange;
import com.sun.javafx.collections.SortHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Predicate;
import javafx.beans.NamedArg;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
public final class FilteredList<E> extends TransformationList<E, E>{
private int[] filtered;
private int size;
private SortHelper helper;
private static final Predicate ALWAYS_TRUE = t -> true;
public FilteredList(@NamedArg("source") ObservableList<E> source, @NamedArg("predicate") Predicate<? super E> predicate) {
super(source);
filtered = new int[source.size() * 3 / 2 + 1];
if (predicate != null) {
setPredicate(predicate);
} else {
for (size = 0; size < source.size(); size++) {
filtered[size] = size;
}
}
}
public FilteredList(@NamedArg("source") ObservableList<E> source) {
this(source, null);
}
private ObjectProperty<Predicate<? super E>> predicate;
public final ObjectProperty<Predicate<? super E>> predicateProperty() {
if (predicate == null) {
predicate = new ObjectPropertyBase<Predicate<? super E>>() {
@Override
protected void invalidated() {
refilter();
}
@Override
public Object getBean() {
return FilteredList.this;
}
@Override
public String getName() {
return "predicate";
}
};
}
return predicate;
}
public final Predicate<? super E> getPredicate() {
return predicate == null ? null : predicate.get();
}
public final void setPredicate(Predicate<? super E> predicate) {
predicateProperty().set(predicate);
}
private Predicate<? super E> getPredicateImpl() {
if (getPredicate() != null) {
return getPredicate();
}
return ALWAYS_TRUE;
}
@Override
protected void sourceChanged(Change<? extends E> c) {
beginChange();
while (c.next()) {
if (c.wasPermutated()) {
permutate(c);
} else if (c.wasUpdated()) {
update(c);
} else {
addRemove(c);
}
}
endChange();
}
@Override
public int size() {
return size;
}
@Override
public E get(int index) {
if (index >= size) {
throw new IndexOutOfBoundsException();
}
return getSource().get(filtered[index]);
}
@Override
public int getSourceIndex(int index) {
if (index >= size) {
throw new IndexOutOfBoundsException();
}
return filtered[index];
}
@Override
public int getViewIndex(int index) {
return Arrays.binarySearch(filtered, 0, size, index);
}
private SortHelper getSortHelper() {
if (helper == null) {
helper = new SortHelper();
}
return helper;
}
private int findPosition(int p) {
if (filtered.length == 0) {
return 0;
}
if (p == 0) {
return 0;
}
int pos = Arrays.binarySearch(filtered, 0, size, p);
if (pos < 0 ) {
pos = ~pos;
}
return pos;
}
@SuppressWarnings("unchecked")
private void ensureSize(int size) {
if (filtered.length < size) {
int[] replacement = new int[size * 3/2 + 1];
System.arraycopy(filtered, 0, replacement, 0, this.size);
filtered = replacement;
}
}
private void updateIndexes(int from, int delta) {
for (int i = from; i < size; ++i) {
filtered[i] += delta;
}
}
private void permutate(Change<? extends E> c) {
int from = findPosition(c.getFrom());
int to = findPosition(c.getTo());
if (to > from) {
for (int i = from; i < to; ++i) {
filtered[i] = c.getPermutation(filtered[i]);
}
int[] perm = getSortHelper().sort(filtered, from, to);
nextPermutation(from, to, perm);
}
}
private void addRemove(Change<? extends E> c) {
Predicate<? super E> pred = getPredicateImpl();
ensureSize(getSource().size());
final int from = findPosition(c.getFrom());
final int to = findPosition(c.getFrom() + c.getRemovedSize());
for (int i = from; i < to; ++i) {
nextRemove(from, c.getRemoved().get(filtered[i] - c.getFrom()));
}
updateIndexes(to, c.getAddedSize() - c.getRemovedSize());
int fpos = from;
int pos = c.getFrom();
ListIterator<? extends E> it = getSource().listIterator(pos);
for (; fpos < to && it.nextIndex() < c.getTo();) {
if (pred.test(it.next())) {
filtered[fpos] = it.previousIndex();
nextAdd(fpos, fpos + 1);
++fpos;
}
}
if (fpos < to) {
System.arraycopy(filtered, to, filtered, fpos, size - to);
size -= to - fpos;
} else {
while (it.nextIndex() < c.getTo()) {
if (pred.test(it.next())) {
System.arraycopy(filtered, fpos, filtered, fpos + 1, size - fpos);
filtered[fpos] = it.previousIndex();
nextAdd(fpos, fpos + 1);
++fpos;
++size;
}
++pos;
}
}
}
private void update(Change<? extends E> c) {
Predicate<? super E> pred = getPredicateImpl();
ensureSize(getSource().size());
int sourceFrom = c.getFrom();
int sourceTo = c.getTo();
int filterFrom = findPosition(sourceFrom);
int filterTo = findPosition(sourceTo);
ListIterator<? extends E> it = getSource().listIterator(sourceFrom);
int pos = filterFrom;
while (pos < filterTo || sourceFrom < sourceTo) {
E el = it.next();
if (pos < size && filtered[pos] == sourceFrom) {
if (!pred.test(el)) {
nextRemove(pos, el);
System.arraycopy(filtered, pos + 1, filtered, pos, size - pos - 1);
--size;
--filterTo;
} else {
nextUpdate(pos);
++pos;
}
} else {
if (pred.test(el)) {
nextAdd(pos, pos + 1);
System.arraycopy(filtered, pos, filtered, pos + 1, size - pos);
filtered[pos] = sourceFrom;
++size;
++pos;
++filterTo;
}
}
sourceFrom++;
}
}
@SuppressWarnings("unchecked")
private void refilter() {
ensureSize(getSource().size());
List<E> removed = null;
if (hasListeners()) {
removed = new ArrayList<>(this);
}
size = 0;
int i = 0;
Predicate<? super E> pred = getPredicateImpl();
for (Iterator<? extends E> it = getSource().iterator();it.hasNext(); ) {
final E next = it.next();
if (pred.test(next)) {
filtered[size++] = i;
}
++i;
}
if (hasListeners()) {
fireChange(new GenericAddRemoveChange<>(0, size, removed, this));
}
}
}
