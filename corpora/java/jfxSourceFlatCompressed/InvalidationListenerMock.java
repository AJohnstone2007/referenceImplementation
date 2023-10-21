package test.javafx.beans;
import javafx.beans.InvalidationListener;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.Observable;
import static org.junit.Assert.assertEquals;
public class InvalidationListenerMock implements InvalidationListener {
private Observable observable = null;
private int counter = 0;
@Override public void invalidated(Observable valueModel) {
this.observable = valueModel;
counter++;
}
public void reset() {
observable = null;
counter = 0;
}
public void check(Observable observable, int counter) {
assertEquals(observable, this.observable);
assertEquals(counter, this.counter);
reset();
}
}
