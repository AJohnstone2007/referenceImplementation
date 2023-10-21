package test.javafx.beans;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakListener;
public class WeakInvalidationListenerMock implements InvalidationListener, WeakListener {
@Override public void invalidated(Observable observable) {}
@Override public boolean wasGarbageCollected() {
return true;
}
}
