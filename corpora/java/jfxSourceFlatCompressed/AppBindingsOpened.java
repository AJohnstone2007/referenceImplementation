package myapp4;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import myapp4.pkg4.MyProps;
import static myapp4.Constants.*;
public class AppBindingsOpened {
public static void main(String[] args) {
try {
new AppBindingsOpened().doTest();
System.exit(ERROR_NONE);
} catch (Throwable t) {
t.printStackTrace(System.err);
System.exit(ERROR_ASSERTION_FAILURE);
}
}
private final double EPSILON = 1.0e-4;
private void assertEquals(double expected, double observed) {
if (Math.abs(expected - observed) > EPSILON) {
throw new AssertionError("expected:<" + expected + "> but was:<" + observed + ">");
}
}
private void assertEquals(String expected, String observed) {
if (!expected.equals(observed)) {
throw new AssertionError("expected:<" + expected + "> but was:<" + observed + ">");
}
}
private void assertSame(Object expected, Object observed) {
if (expected != observed) {
throw new AssertionError("expected:<" + expected + "> but was:<" + observed + ">");
}
}
public void doTest() throws Exception {
MyProps root = new MyProps();
MyProps a = new MyProps();
MyProps b = new MyProps();
root.setNext(a);
a.setNext(b);
a.setFoo(1.2);
b.setFoo(2.3);
DoubleBinding binding1 = Bindings.selectDouble(root, "next", "foo");
assertEquals(1.2, binding1.get());
a.setFoo(3.4);
assertEquals(3.4, binding1.get());
ObjectBinding<MyProps> binding2 = Bindings.select(root, "next", "next");
assertEquals(2.3, binding2.get().getFoo());
b.setFoo(4.5);
assertEquals(4.5, binding2.get().getFoo());
RootProps root2 = new RootProps();
MyProps c = new MyProps();
MyProps d = new MyProps();
root2.setNext(c);
c.setNext(d);
c.setFoo(1.2);
d.setFoo(2.3);
DoubleBinding binding3 = Bindings.selectDouble(root2, "next", "foo");
assertEquals(1.2, binding3.get());
c.setFoo(3.4);
assertEquals(3.4, binding3.get());
ObjectBinding<MyProps> binding4 = Bindings.select(root2, "next", "next");
assertEquals(2.3, binding4.get().getFoo());
d.setFoo(4.5);
assertEquals(4.5, binding4.get().getFoo());
}
}
