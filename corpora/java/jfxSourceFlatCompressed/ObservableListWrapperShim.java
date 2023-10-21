package javafx.collections;
import com.sun.javafx.collections.ListListenerHelper;
import com.sun.javafx.collections.ObservableListWrapper;
import java.util.List;
import javafx.beans.InvalidationListener;
public class ObservableListWrapperShim<E> extends ObservableListWrapper<E> {
public ObservableListWrapperShim(List<E> list) {
super(list);
}
public static final void fireChange(
ObservableListWrapper wrapper,
ListChangeListener.Change change) {
wrapper.fireChange(change);
}
public static final void beginChange(ObservableListWrapper wrapper) {
wrapper.beginChange();
}
public static final void endChange(ObservableListWrapper wrapper) {
wrapper.endChange();
}
public static final void nextUpdate(ObservableListWrapper wrapper, int i) {
wrapper.nextUpdate(i);
}
}
