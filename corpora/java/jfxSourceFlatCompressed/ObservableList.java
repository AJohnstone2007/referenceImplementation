package javafx.collections;
import java.text.Collator;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import javafx.beans.Observable;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
public interface ObservableList<E> extends List<E>, Observable {
public void addListener(ListChangeListener<? super E> listener);
public void removeListener(ListChangeListener<? super E> listener);
public boolean addAll(E... elements);
public boolean setAll(E... elements);
public boolean setAll(Collection<? extends E> col);
public boolean removeAll(E... elements);
public boolean retainAll(E... elements);
public void remove(int from, int to);
public default FilteredList<E> filtered(Predicate<E> predicate) {
return new FilteredList<>(this, predicate);
}
public default SortedList<E> sorted(Comparator<E> comparator) {
return new SortedList<>(this, comparator);
}
public default SortedList<E> sorted() {
Comparator naturalOrder = new Comparator<E>() {
@Override
public int compare(E o1, E o2) {
if (o1 == null && o2 == null) {
return 0;
}
if (o1 == null) {
return -1;
}
if (o2 == null) {
return 1;
}
if (o1 instanceof Comparable) {
return ((Comparable) o1).compareTo(o2);
}
return Collator.getInstance().compare(o1.toString(), o2.toString());
}
};
return sorted(naturalOrder);
}
}
