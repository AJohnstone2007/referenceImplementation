package test.com.sun.javafx.property.adapter;
import com.sun.javafx.property.adapter.JavaBeanPropertyBuilderHelper;
import com.sun.javafx.property.adapter.PropertyDescriptor;
import org.junit.Before;
import org.junit.Test;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
public class JavaBeanPropertyBuilderHelperTest {
private JavaBeanPropertyBuilderHelper helperPOJOBean;
private JavaBeanPropertyBuilderHelper helperPOJOBeanWithNonStandardNames;
@Before
public void setUp() {
helperPOJOBean = new JavaBeanPropertyBuilderHelper();
helperPOJOBean.beanClass(POJOBean.class);
helperPOJOBean.name("x");
helperPOJOBeanWithNonStandardNames = new JavaBeanPropertyBuilderHelper();
helperPOJOBeanWithNonStandardNames.beanClass(POJOBeanWithNonStandardNames.class);
helperPOJOBeanWithNonStandardNames.name("x");
helperPOJOBeanWithNonStandardNames.getterName("readX");
helperPOJOBeanWithNonStandardNames.setterName("writeX");
}
@Test(expected = NullPointerException.class)
public void testSetup_WithNameIsNull() {
try {
helperPOJOBean.name(null);
helperPOJOBean.getDescriptor();
} catch (NoSuchMethodException e) {
fail();
}
}
@Test(expected = IllegalArgumentException.class)
public void testSetup_WithNameIsEmpty() {
try {
helperPOJOBean.name("");
helperPOJOBean.getDescriptor();
} catch (NoSuchMethodException e) {
fail();
}
}
@Test(expected = NullPointerException.class)
public void testSetup_WithBeanClassIsNull() {
try {
helperPOJOBean.beanClass(null);
helperPOJOBean.getDescriptor();
} catch (NoSuchMethodException e) {
fail();
}
}
@Test(expected = NullPointerException.class)
public void testSetup_WithNonStandardNames_WithNameIsNull() {
try {
helperPOJOBeanWithNonStandardNames.name(null);
helperPOJOBeanWithNonStandardNames.getDescriptor();
} catch (NoSuchMethodException e) {
fail();
}
}
@Test(expected = NullPointerException.class)
public void testSetup_WithNonStandardNames_WithBeanClassIsNull() {
try {
helperPOJOBeanWithNonStandardNames.beanClass(null);
helperPOJOBeanWithNonStandardNames.getDescriptor();
} catch (NoSuchMethodException e) {
fail();
}
}
@Test(expected = NoSuchMethodException.class)
public void testSetup_WithNonStandardNames_WithGetterNameIsNull() throws NoSuchMethodException {
helperPOJOBeanWithNonStandardNames.getterName(null);
helperPOJOBeanWithNonStandardNames.getDescriptor();
}
@Test(expected = NoSuchMethodException.class)
public void testSetup_WithNonStandardNames_WithSetterNameIsNull() throws NoSuchMethodException {
helperPOJOBeanWithNonStandardNames.setterName(null);
helperPOJOBeanWithNonStandardNames.getDescriptor();
}
@Test(expected = IllegalArgumentException.class)
public void testSetup_WithNonStandardNames_WithNameIsEmpty() {
try {
helperPOJOBeanWithNonStandardNames.name("");
helperPOJOBeanWithNonStandardNames.getDescriptor();
} catch (NoSuchMethodException e) {
fail();
}
}
@Test(expected = NoSuchMethodException.class)
public void testSetup_WithNonStandardNames_WithGetterNameIsEmpty() throws NoSuchMethodException {
helperPOJOBeanWithNonStandardNames.getterName("");
helperPOJOBeanWithNonStandardNames.getDescriptor();
}
@Test(expected = NoSuchMethodException.class)
public void testSetup_WithNonStandardNames_WithSetterNameIsEmpty() throws NoSuchMethodException {
helperPOJOBeanWithNonStandardNames.setterName("");
helperPOJOBeanWithNonStandardNames.getDescriptor();
}
@Test(expected = NullPointerException.class)
public void testSetup_WithNonStandardAccessors_WithNameIsNull() throws NoSuchMethodException {
helperPOJOBeanWithNonStandardNames.getterName(null);
helperPOJOBeanWithNonStandardNames.setterName(null);
try {
final Method getter = POJOBeanWithNonStandardNames.class.getMethod("readX");
final Method setter = POJOBeanWithNonStandardNames.class.getMethod("writeX", Object.class);
helperPOJOBeanWithNonStandardNames.getter(getter);
helperPOJOBeanWithNonStandardNames.setter(setter);
helperPOJOBeanWithNonStandardNames.name(null);
} catch (NoSuchMethodException e) {
fail("Error in test code. Should not happen.");
}
helperPOJOBeanWithNonStandardNames.getDescriptor();
}
@Test(expected = NoSuchMethodException.class)
public void testSetup_WithNonStandardAccessors_WithGetterIsNull() throws NoSuchMethodException {
helperPOJOBeanWithNonStandardNames.getterName(null);
helperPOJOBeanWithNonStandardNames.setterName(null);
try {
final Method setter = POJOBeanWithNonStandardNames.class.getMethod("writeX", Object.class);
helperPOJOBeanWithNonStandardNames.setter(setter);
helperPOJOBeanWithNonStandardNames.getter(null);
} catch (NoSuchMethodException e) {
fail("Error in test code. Should not happen.");
}
helperPOJOBeanWithNonStandardNames.getDescriptor();
}
@Test(expected = NoSuchMethodException.class)
public void testSetup_WithNonStandardAccessors_WithSetterIsNull() throws NoSuchMethodException {
helperPOJOBeanWithNonStandardNames.getterName(null);
helperPOJOBeanWithNonStandardNames.setterName(null);
try {
final Method getter = POJOBeanWithNonStandardNames.class.getMethod("readX");
helperPOJOBeanWithNonStandardNames.getter(getter);
helperPOJOBeanWithNonStandardNames.setter(null);
} catch (NoSuchMethodException e) {
fail("Error in test code. Should not happen.");
}
helperPOJOBeanWithNonStandardNames.getDescriptor();
}
@Test(expected = IllegalArgumentException.class)
public void testSetup_WithNonStandardAccessors_WithNameIsEmpty() throws NoSuchMethodException {
helperPOJOBeanWithNonStandardNames.getterName(null);
helperPOJOBeanWithNonStandardNames.setterName(null);
try {
final Method getter = POJOBeanWithNonStandardNames.class.getMethod("readX");
final Method setter = POJOBeanWithNonStandardNames.class.getMethod("writeX", Object.class);
helperPOJOBeanWithNonStandardNames.getter(getter);
helperPOJOBeanWithNonStandardNames.setter(setter);
helperPOJOBeanWithNonStandardNames.name("");
} catch (NoSuchMethodException e) {
fail("Error in test code. Should not happen.");
}
helperPOJOBeanWithNonStandardNames.getDescriptor();
}
@Test
public void testReusabilityWhenChangeOfBeanClass() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
Object x = new Object();
PropertyDescriptor descriptor = helperPOJOBean.getDescriptor();
assertEquals(x, descriptor.getGetter().invoke(new POJOBean(x)));
descriptor.getSetter().invoke(new POJOBean(x), new Object());
helperPOJOBean.beanClass(POJOBean2.class);
descriptor = helperPOJOBean.getDescriptor();
assertEquals(x, descriptor.getGetter().invoke(new POJOBean2(x)));
descriptor.getSetter().invoke(new POJOBean2(x), new Object());
}
public static class POJOBean {
private Object x;
public POJOBean(Object x) {this.x = x;}
public Object getX() {return x;}
public void setX(Object x) {this.x = x;}
}
public static class POJOBean2 {
private Object x;
public POJOBean2(Object x) {this.x = x;}
public Object getX() {return x;}
public void setX(Object x) {this.x = x;}
}
public static class POJOBeanWithNonStandardNames {
private Object x;
public POJOBeanWithNonStandardNames(Object x) {this.x = x;}
public Object readX() {return x;}
public void writeX(Object x) {this.x = x;}
}
}
