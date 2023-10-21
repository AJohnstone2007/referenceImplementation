package javafx.collections;
import com.sun.javafx.collections.ListListenerHelper;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javafx.beans.InvalidationListener;
public abstract class ObservableListBase<E> extends AbstractList<E> implements ObservableList<E> {
private ListListenerHelper<E> listenerHelper;
private final ListChangeBuilder<E> changeBuilder = new ListChangeBuilder<E>(this);
public ObservableListBase() {
}
protected final void nextUpdate(int pos) {
changeBuilder.nextUpdate(pos);
}
protected final void nextSet(int idx, E old) {
changeBuilder.nextSet(idx, old);
}
protected final void nextReplace(int from, int to, List<? extends E> removed) {
changeBuilder.nextReplace(from, to, removed);
}
protected final void nextRemove(int idx, List<? extends E> removed) {
changeBuilder.nextRemove(idx, removed);
}
protected final void nextRemove(int idx, E removed) {
changeBuilder.nextRemove(idx, removed);
}
protected final void nextPermutation(int from, int to, int[] perm) {
changeBuilder.nextPermutation(from, to, perm);
}
protected final void nextAdd(int from, int to) {
changeBuilder.nextAdd(from, to);
}
protected final void beginChange() {
changeBuilder.beginChange();
}
protected final void endChange() {
changeBuilder.endChange();
}
@Override
public final void addListener(InvalidationListener listener) {
listenerHelper = ListListenerHelper.addListener(listenerHelper, listener);
}
@Override
public final void removeListener(InvalidationListener listener) {
listenerHelper = ListListenerHelper.removeListener(listenerHelper, listener);
}
@Override
public final void addListener(ListChangeListener<? super E> listener) {
listenerHelper = ListListenerHelper.addListener(listenerHelper, listener);
}
@Override
public final void removeListener(ListChangeListener<? super E> listener) {
listenerHelper = ListListenerHelper.removeListener(listenerHelper, listener);
}
protected final void fireChange(ListChangeListener.Change<? extends E> change) {
ListListenerHelper.fireValueChangedEvent(listenerHelper, change);
}
protected final boolean hasListeners() {
return ListListenerHelper.hasListeners(listenerHelper);
}
@Override
public boolean addAll(E... elements) {
return addAll(Arrays.asList(elements));
}
@Override
public boolean setAll(E... elements) {
return setAll(Arrays.asList(elements));
}
@Override
public boolean setAll(Collection<? extends E> col) {
throw new UnsupportedOperationException();
}
@Override
public boolean removeAll(E... elements) {
return removeAll(Arrays.asList(elements));
}
@Override
public boolean retainAll(E... elements) {
return retainAll(Arrays.asList(elements));
}
@Override
public void remove(int from, int to) {
removeRange(from, to);
}
}
