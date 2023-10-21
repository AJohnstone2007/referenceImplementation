package test.com.sun.javafx.fxml.builder;
import com.sun.javafx.collections.TrackableObservableList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.beans.NamedArg;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
public class ClassWithCollection {
public double a;
public double b;
public List<Integer> list;
ObservableList<Integer> propertyList = new TrackableObservableList<Integer>() {
@Override
protected void onChanged(ListChangeListener.Change<Integer> c) {}
};
public ClassWithCollection(@NamedArg("a") double a) {
this.a = a;
this.b = b;
}
public ClassWithCollection(@NamedArg("a") double a, @NamedArg("b") double b) {
this.a = a;
this.b = b;
}
public ClassWithCollection(@NamedArg("a") int a, @NamedArg("b") int b, @NamedArg("list") List<Integer> list) {
this.a = a;
this.b = b;
this.list = new ArrayList<>(list);
}
public ClassWithCollection(@NamedArg("a") int a, @NamedArg("b") int b, @NamedArg("list") Integer... list) {
this.a = a;
this.b = b;
this.list = Arrays.asList(list);
}
public double getB() {
return b;
}
public void setB(double b) {
this.b = b;
}
public List<Integer> getList() {
return list;
}
public List<Integer> getPropertyList() {
return propertyList;
}
}
