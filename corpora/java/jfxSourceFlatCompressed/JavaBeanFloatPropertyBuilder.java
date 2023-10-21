package javafx.beans.property.adapter;
import com.sun.javafx.property.adapter.JavaBeanPropertyBuilderHelper;
import com.sun.javafx.property.adapter.PropertyDescriptor;
import java.lang.reflect.Method;
public final class JavaBeanFloatPropertyBuilder {
private JavaBeanPropertyBuilderHelper helper = new JavaBeanPropertyBuilderHelper();
private JavaBeanFloatPropertyBuilder() {}
public static JavaBeanFloatPropertyBuilder create() {
return new JavaBeanFloatPropertyBuilder();
}
public JavaBeanFloatProperty build() throws NoSuchMethodException {
final PropertyDescriptor descriptor = helper.getDescriptor();
if (!float.class.equals(descriptor.getType()) && !Number.class.isAssignableFrom(descriptor.getType())) {
throw new IllegalArgumentException("Not a float property");
}
return new JavaBeanFloatProperty(descriptor, helper.getBean());
}
public JavaBeanFloatPropertyBuilder name(String name) {
helper.name(name);
return this;
}
public JavaBeanFloatPropertyBuilder bean(Object bean) {
helper.bean(bean);
return this;
}
public JavaBeanFloatPropertyBuilder beanClass(Class<?> beanClass) {
helper.beanClass(beanClass);
return this;
}
public JavaBeanFloatPropertyBuilder getter(String getter) {
helper.getterName(getter);
return this;
}
public JavaBeanFloatPropertyBuilder setter(String setter) {
helper.setterName(setter);
return this;
}
public JavaBeanFloatPropertyBuilder getter(Method getter) {
helper.getter(getter);
return this;
}
public JavaBeanFloatPropertyBuilder setter(Method setter) {
helper.setter(setter);
return this;
}
}
