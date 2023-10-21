package javafx.beans.value;
import java.util.ArrayList;
import java.util.List;
import com.sun.javafx.binding.ExpressionHelper;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
public abstract class ObservableValueBase<T> implements ObservableValue<T> {
private ExpressionHelper<T> helper;
public ObservableValueBase() {
}
@Override
public void addListener(InvalidationListener listener) {
helper = ExpressionHelper.addListener(helper, this, listener);
}
@Override
public void addListener(ChangeListener<? super T> listener) {
helper = ExpressionHelper.addListener(helper, this, listener);
}
@Override
public void removeListener(InvalidationListener listener) {
helper = ExpressionHelper.removeListener(helper, listener);
}
@Override
public void removeListener(ChangeListener<? super T> listener) {
helper = ExpressionHelper.removeListener(helper, listener);
}
protected void fireValueChangedEvent() {
ExpressionHelper.fireValueChangedEvent(helper);
}
}
