package myapp4;
import java.lang.reflect.UndeclaredThrowableException;
import javafx.beans.property.adapter.JavaBeanDoubleProperty;
import javafx.beans.property.adapter.JavaBeanDoublePropertyBuilder;
import javafx.beans.property.adapter.JavaBeanObjectProperty;
import javafx.beans.property.adapter.JavaBeanObjectPropertyBuilder;
import javafx.beans.property.adapter.ReadOnlyJavaBeanStringProperty;
import javafx.beans.property.adapter.ReadOnlyJavaBeanStringPropertyBuilder;
import myapp4.pkg1.POJO;
import myapp4.pkg1.RefClass;
import static myapp4.Constants.*;
public class AppBeansUnexported {
public static void main(String[] args) {
try {
new AppBeansUnexported().doTest();
System.exit(ERROR_NONE);
} catch (Throwable t) {
t.printStackTrace(System.err);
System.exit(ERROR_ASSERTION_FAILURE);
}
}
private void checkException(UndeclaredThrowableException ex) {
Throwable cause = ex.getCause();
if (! (cause instanceof IllegalAccessException)) {
System.err.println("ERROR: unexpected cause: " + cause);
throw ex;
}
String message = cause.getMessage();
if (message == null) {
System.err.println("ERROR: detail message of cause is null");
throw ex;
}
boolean badMessage = false;
if (!message.contains(" cannot access class ")) badMessage = true;
if (!message.contains(" does not open ")) badMessage = true;
if (!message.endsWith(" to javafx.base")) badMessage = true;
if (badMessage) {
System.err.println("ERROR: detail message not formatted correctly: " + message);
throw ex;
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
try {
valProp.get();
throw new AssertionError("ERROR: did not get the expected exception");
} catch (UndeclaredThrowableException ex) {
checkException(ex);
}
val = 2.5;
try {
valProp.set(val);
throw new AssertionError("ERROR: did not get the expected exception");
} catch (UndeclaredThrowableException ex) {
checkException(ex);
}
JavaBeanObjectProperty<RefClass> objProp = JavaBeanObjectPropertyBuilder.create()
.bean(bean)
.name("obj")
.build();
try {
objProp.get();
throw new AssertionError("ERROR: did not get the expected exception");
} catch (UndeclaredThrowableException ex) {
checkException(ex);
}
obj = new RefClass();
try {
objProp.set(obj);
throw new AssertionError("ERROR: did not get the expected exception");
} catch (UndeclaredThrowableException ex) {
checkException(ex);
}
ReadOnlyJavaBeanStringProperty namePropRO = ReadOnlyJavaBeanStringPropertyBuilder.create()
.bean(bean)
.name("name")
.build();
try {
namePropRO.get();
throw new AssertionError("ERROR: did not get the expected exception");
} catch (UndeclaredThrowableException ex) {
checkException(ex);
}
}
}
