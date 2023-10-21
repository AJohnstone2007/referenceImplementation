package test.javafx.collections;
import javafx.beans.WeakListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
public class WeakListChangeListenerMock implements ChangeListener<Object>, WeakListener {
@Override public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) { }
@Override public boolean wasGarbageCollected() {
return true;
}
}
