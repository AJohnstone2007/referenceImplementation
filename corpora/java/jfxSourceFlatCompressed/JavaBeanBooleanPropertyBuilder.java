package javafx.beans.property.adapter;
import com.sun.javafx.property.adapter.JavaBeanPropertyBuilderHelper;
import com.sun.javafx.property.adapter.PropertyDescriptor;
import java.lang.reflect.Method;
public final class JavaBeanBooleanPropertyBuilder {
private final JavaBeanPropertyBuilderHelper helper = new JavaBeanPropertyBuilderHelper();
private JavaBeanBooleanPropertyBuilder() {}
public static JavaBeanBooleanPropertyBuilder create() {
return new JavaBeanBooleanPropertyBuilder();
}
public JavaBeanBooleanProperty build() throws NoSuchMethodException {
final PropertyDescriptor descriptor = helper.getDescriptor();
if (!boolean.class.equals(descriptor.getType()) && !Boolean.class.equals(descriptor.getType())) {
throw new IllegalArgumentException("Not a boolean property");
}
return new JavaBeanBooleanProperty(descriptor, helper.getBean());
}
public JavaBeanBooleanPropertyBuilder name(String name) {
helper.name(name);
return this;
}
public JavaBeanBooleanPropertyBuilder bean(Object bean) {
helper.bean(bean);
return this;
}
public JavaBeanBooleanPropertyBuilder beanClass(Class<?> beanClass) {
helper.beanClass(beanClass);
return this;
}
public JavaBeanBooleanPropertyBuilder getter(String getter) {
helper.getterName(getter);
return this;
}
public JavaBeanBooleanPropertyBuilder setter(String setter) {
helper.setterName(setter);
return this;
}
public JavaBeanBooleanPropertyBuilder getter(Method getter) {
helper.getter(getter);
return this;
}
public JavaBeanBooleanPropertyBuilder setter(Method setter) {
helper.setter(setter);
return this;
}
}
