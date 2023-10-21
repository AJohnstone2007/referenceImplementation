package myapp4;
import javafx.beans.property.adapter.JavaBeanDoubleProperty;
import javafx.beans.property.adapter.JavaBeanDoublePropertyBuilder;
import javafx.beans.property.adapter.JavaBeanObjectProperty;
import javafx.beans.property.adapter.JavaBeanObjectPropertyBuilder;
import javafx.beans.property.adapter.ReadOnlyJavaBeanStringProperty;
import javafx.beans.property.adapter.ReadOnlyJavaBeanStringPropertyBuilder;
import myapp4.pkg4.POJO;
import myapp4.pkg4.RefClass;
import static myapp4.Constants.*;
public class AppBeansOpened {
public static void main(String[] args) {
try {
new AppBeansOpened().doTest();
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
String name = "test object";
double val = 1.2;
RefClass obj = new RefClass();
POJO bean = new POJO(name, val, obj);
JavaBeanDoubleProperty valProp = JavaBeanDoublePropertyBuilder.create()
.bean(bean)
.name("val")
.build();
double retVal = valProp.get();
assertEquals(val, retVal);
val = 2.5;
valProp.set(val);
retVal = valProp.get();
assertEquals(val, retVal);
JavaBeanObjectProperty<RefClass> objProp = JavaBeanObjectPropertyBuilder.create()
.bean(bean)
.name("obj")
.build();
RefClass retObj = objProp.get();
assertSame(obj, retObj);
obj = new RefClass();
objProp.set(obj);
retObj = objProp.get();
assertSame(obj, retObj);
ReadOnlyJavaBeanStringProperty namePropRO = ReadOnlyJavaBeanStringPropertyBuilder.create()
.bean(bean)
.name("name")
.build();
String retName = namePropRO.get();
assertEquals(name, retName);
}
}
