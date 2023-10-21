package myapp4;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
public class RootProps {
private final ObjectProperty<Object> next = new SimpleObjectProperty<>();
public final ObjectProperty<Object> nextProperty() {
return next;
}
public final void setNext(Object next) {
this.next.set(next);
}
public final Object getNext() {
return next.get();
}
}
