package javafx.beans;
import javafx.beans.value.ObservableValue;
@FunctionalInterface
public interface InvalidationListener {
public void invalidated(Observable observable);
}
