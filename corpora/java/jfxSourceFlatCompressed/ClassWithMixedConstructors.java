package test.com.sun.javafx.fxml.builder;
import javafx.beans.NamedArg;
public class ClassWithMixedConstructors {
public double a;
public double b;
public String c;
public String d;
public ClassWithMixedConstructors(@NamedArg("a") double a, @NamedArg("b") double b) {
this.a = a;
this.b = b;
}
public ClassWithMixedConstructors(@NamedArg("c") String c, @NamedArg("d") String d) {
this.c = c;
this.d = d;
}
}
