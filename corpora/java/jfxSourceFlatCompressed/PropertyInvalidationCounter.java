package test.com.sun.javafx.test;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
public final class PropertyInvalidationCounter<T>
implements InvalidationListener {
private int counter;
@Override
public void invalidated(final Observable observable) {
final ObservableValue<T> observableValue =
(ObservableValue<T>) observable;
observableValue.getValue();
++counter;
}
public int getCounter() {
return counter;
}
}
