package javafx.beans.property.adapter;
import com.sun.javafx.property.adapter.JavaBeanPropertyBuilderHelper;
import com.sun.javafx.property.adapter.PropertyDescriptor;
import java.lang.reflect.Method;
public final class JavaBeanLongPropertyBuilder {
private JavaBeanPropertyBuilderHelper helper = new JavaBeanPropertyBuilderHelper();
private JavaBeanLongPropertyBuilder() {}
public static JavaBeanLongPropertyBuilder create() {
return new JavaBeanLongPropertyBuilder();
}
public JavaBeanLongProperty build() throws NoSuchMethodException {
final PropertyDescriptor descriptor = helper.getDescriptor();
if (!long.class.equals(descriptor.getType()) && !Number.class.isAssignableFrom(descriptor.getType())) {
throw new IllegalArgumentException("Not a long property");
}
return new JavaBeanLongProperty(descriptor, helper.getBean());
}
public JavaBeanLongPropertyBuilder name(String name) {
helper.name(name);
return this;
}
public JavaBeanLongPropertyBuilder bean(Object bean) {
helper.bean(bean);
return this;
}
public JavaBeanLongPropertyBuilder beanClass(Class<?> beanClass) {
helper.beanClass(beanClass);
return this;
}
public JavaBeanLongPropertyBuilder getter(String getter) {
helper.getterName(getter);
return this;
}
public JavaBeanLongPropertyBuilder setter(String setter) {
helper.setterName(setter);
return this;
}
public JavaBeanLongPropertyBuilder getter(Method getter) {
helper.getter(getter);
return this;
}
public JavaBeanLongPropertyBuilder setter(Method setter) {
helper.setter(setter);
return this;
}
}
