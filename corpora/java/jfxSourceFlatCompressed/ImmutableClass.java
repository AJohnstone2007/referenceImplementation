package test.com.sun.javafx.fxml.builder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.beans.NamedArg;
public class ImmutableClass {
public double a;
public double b;
public List<Integer> list;
public ImmutableClass(@NamedArg("a") int a, @NamedArg("b") int b) {
this.a = a;
this.b = b;
}
public ImmutableClass(@NamedArg("a")float a, @NamedArg("b") float b) {
this.a = a;
this.b = b;
}
public ImmutableClass(@NamedArg("a")int a, @NamedArg("b") float b) {
this.a = a;
this.b = b;
}
public ImmutableClass(@NamedArg("a") float a, @NamedArg("b") int b) {
this.a = a;
this.b = b;
}
public ImmutableClass(@NamedArg("a") int a, @NamedArg("b") int b, @NamedArg("list") List<Integer> list) {
this.a = a;
this.b = b;
this.list = new ArrayList<>(list);
}
public ImmutableClass(@NamedArg("a") int a, @NamedArg("b") int b, @NamedArg("list") Integer... list) {
this.a = a;
this.b = b;
this.list = Arrays.asList(list);
}
}
