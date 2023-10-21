package javafx.beans.property;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import com.sun.javafx.binding.ExpressionHelper;
public abstract class ReadOnlyBooleanPropertyBase extends ReadOnlyBooleanProperty {
ExpressionHelper<Boolean> helper;
public ReadOnlyBooleanPropertyBase() {
}
@Override
public void addListener(InvalidationListener listener) {
helper = ExpressionHelper.addListener(helper, this, listener);
}
@Override
public void removeListener(InvalidationListener listener) {
helper = ExpressionHelper.removeListener(helper, listener);
}
@Override
public void addListener(ChangeListener<? super Boolean> listener) {
helper = ExpressionHelper.addListener(helper, this, listener);
}
@Override
public void removeListener(ChangeListener<? super Boolean> listener) {
helper = ExpressionHelper.removeListener(helper, listener);
}
protected void fireValueChangedEvent() {
ExpressionHelper.fireValueChangedEvent(helper);
}
}
