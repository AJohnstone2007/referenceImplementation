package javafx.beans.property;
import com.sun.javafx.binding.SetExpressionHelper;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
public abstract class ReadOnlySetPropertyBase<E> extends ReadOnlySetProperty<E> {
private SetExpressionHelper<E> helper;
public ReadOnlySetPropertyBase() {
}
@Override
public void addListener(InvalidationListener listener) {
helper = SetExpressionHelper.addListener(helper, this, listener);
}
@Override
public void removeListener(InvalidationListener listener) {
helper = SetExpressionHelper.removeListener(helper, listener);
}
@Override
public void addListener(ChangeListener<? super ObservableSet<E>> listener) {
helper = SetExpressionHelper.addListener(helper, this, listener);
}
@Override
public void removeListener(ChangeListener<? super ObservableSet<E>> listener) {
helper = SetExpressionHelper.removeListener(helper, listener);
}
@Override
public void addListener(SetChangeListener<? super E> listener) {
helper = SetExpressionHelper.addListener(helper, this, listener);
}
@Override
public void removeListener(SetChangeListener<? super E> listener) {
helper = SetExpressionHelper.removeListener(helper, listener);
}
protected void fireValueChangedEvent() {
SetExpressionHelper.fireValueChangedEvent(helper);
}
protected void fireValueChangedEvent(SetChangeListener.Change<? extends E> change) {
SetExpressionHelper.fireValueChangedEvent(helper, change);
}
}
