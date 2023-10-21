package javafx.beans.property.adapter;
import com.sun.javafx.property.adapter.JavaBeanPropertyBuilderHelper;
import com.sun.javafx.property.adapter.PropertyDescriptor;
import java.lang.reflect.Method;
public final class JavaBeanIntegerPropertyBuilder {
private JavaBeanPropertyBuilderHelper helper = new JavaBeanPropertyBuilderHelper();
private JavaBeanIntegerPropertyBuilder() {}
public static JavaBeanIntegerPropertyBuilder create() {
return new JavaBeanIntegerPropertyBuilder();
}
public JavaBeanIntegerProperty build() throws NoSuchMethodException {
final PropertyDescriptor descriptor = helper.getDescriptor();
if (!int.class.equals(descriptor.getType()) && !Number.class.isAssignableFrom(descriptor.getType())) {
throw new IllegalArgumentException("Not an int property");
}
return new JavaBeanIntegerProperty(descriptor, helper.getBean());
}
public JavaBeanIntegerPropertyBuilder name(String name) {
helper.name(name);
return this;
}
public JavaBeanIntegerPropertyBuilder bean(Object bean) {
helper.bean(bean);
return this;
}
public JavaBeanIntegerPropertyBuilder beanClass(Class<?> beanClass) {
helper.beanClass(beanClass);
return this;
}
public JavaBeanIntegerPropertyBuilder getter(String getter) {
helper.getterName(getter);
return this;
}
public JavaBeanIntegerPropertyBuilder setter(String setter) {
helper.setterName(setter);
return this;
}
public JavaBeanIntegerPropertyBuilder getter(Method getter) {
helper.getter(getter);
return this;
}
public JavaBeanIntegerPropertyBuilder setter(Method setter) {
helper.setter(setter);
return this;
}
}
