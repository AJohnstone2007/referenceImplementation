package test.com.sun.javafx.fxml.builder;
import javafx.beans.NamedArg;
public class PartiallyMutableClass {
public int a;
public int b;
public int intValue;
public double doubleValue;
public String stringValue;
public PartiallyMutableClass(@NamedArg("a")int a, @NamedArg("b") int b) {
this.a = a;
this.b = b;
}
public PartiallyMutableClass(@NamedArg("a") String a, @NamedArg("b") String b) {
this.a = Integer.valueOf(a);
this.b = Integer.valueOf(b);
}
public PartiallyMutableClass(@NamedArg("a") int a, @NamedArg("b") int b, @NamedArg("stringValue") String stringValue) {
this.a = a;
this.b = b;
this.stringValue = stringValue;
}
public void setIntValue(int i) {
this.intValue = i;
}
public void setDoubleValue(double d) {
this.doubleValue = d;
}
public void setStringValue(String s) {
this.stringValue = s;
}
}
