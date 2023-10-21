package javafx.beans.property;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import com.sun.javafx.binding.ExpressionHelper;
public abstract class ReadOnlyStringPropertyBase extends ReadOnlyStringProperty {
ExpressionHelper<String> helper;
public ReadOnlyStringPropertyBase() {
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
public void addListener(ChangeListener<? super String> listener) {
helper = ExpressionHelper.addListener(helper, this, listener);
}
@Override
public void removeListener(ChangeListener<? super String> listener) {
helper = ExpressionHelper.removeListener(helper, listener);
}
protected void fireValueChangedEvent() {
ExpressionHelper.fireValueChangedEvent(helper);
}
}
