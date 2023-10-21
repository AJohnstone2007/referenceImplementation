package test.javafx.beans.value;
import javafx.beans.WeakListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
public class WeakChangeListenerMock implements ChangeListener<Object>, WeakListener {
@Override public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) { }
@Override public boolean wasGarbageCollected() {
return true;
}
}
