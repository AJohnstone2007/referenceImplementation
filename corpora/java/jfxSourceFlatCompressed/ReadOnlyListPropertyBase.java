package javafx.beans.property;
import com.sun.javafx.binding.ListExpressionHelper;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
public abstract class ReadOnlyListPropertyBase<E> extends ReadOnlyListProperty<E> {
private ListExpressionHelper<E> helper;
public ReadOnlyListPropertyBase() {
}
@Override
public void addListener(InvalidationListener listener) {
helper = ListExpressionHelper.addListener(helper, this, listener);
}
@Override
public void removeListener(InvalidationListener listener) {
helper = ListExpressionHelper.removeListener(helper, listener);
}
@Override
public void addListener(ChangeListener<? super ObservableList<E>> listener) {
helper = ListExpressionHelper.addListener(helper, this, listener);
}
@Override
public void removeListener(ChangeListener<? super ObservableList<E>> listener) {
helper = ListExpressionHelper.removeListener(helper, listener);
}
@Override
public void addListener(ListChangeListener<? super E> listener) {
helper = ListExpressionHelper.addListener(helper, this, listener);
}
@Override
public void removeListener(ListChangeListener<? super E> listener) {
helper = ListExpressionHelper.removeListener(helper, listener);
}
protected void fireValueChangedEvent() {
ListExpressionHelper.fireValueChangedEvent(helper);
}
protected void fireValueChangedEvent(ListChangeListener.Change<? extends E> change) {
ListExpressionHelper.fireValueChangedEvent(helper, change);
}
}
