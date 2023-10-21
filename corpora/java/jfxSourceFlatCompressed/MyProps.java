package myapp4.pkg5;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
public class MyProps {
private final DoubleProperty foo = new SimpleDoubleProperty();
public final DoubleProperty fooProperty() {
return foo;
}
public final void setFoo(double foo) {
this.foo.set(foo);
}
public final double getFoo() {
return foo.get();
}
private final ObjectProperty<MyProps> next = new SimpleObjectProperty<>();
public final ObjectProperty<MyProps> nextProperty() {
return next;
}
public final void setNext(MyProps next) {
this.next.set(next);
}
public final MyProps getNext() {
return next.get();
}
}
