package javafx.beans.property.adapter;
import com.sun.javafx.property.adapter.JavaBeanPropertyBuilderHelper;
import com.sun.javafx.property.adapter.PropertyDescriptor;
import java.lang.reflect.Method;
public final class JavaBeanDoublePropertyBuilder {
private final JavaBeanPropertyBuilderHelper helper = new JavaBeanPropertyBuilderHelper();
private JavaBeanDoublePropertyBuilder() {}
public static JavaBeanDoublePropertyBuilder create() {
return new JavaBeanDoublePropertyBuilder();
}
public JavaBeanDoubleProperty build() throws NoSuchMethodException {
final PropertyDescriptor descriptor = helper.getDescriptor();
if (!double.class.equals(descriptor.getType()) && !Number.class.isAssignableFrom(descriptor.getType())) {
throw new IllegalArgumentException("Not a double property");
}
return new JavaBeanDoubleProperty(descriptor, helper.getBean());
}
public JavaBeanDoublePropertyBuilder name(String name) {
helper.name(name);
return this;
}
public JavaBeanDoublePropertyBuilder bean(Object bean) {
helper.bean(bean);
return this;
}
public JavaBeanDoublePropertyBuilder beanClass(Class<?> beanClass) {
helper.beanClass(beanClass);
return this;
}
public JavaBeanDoublePropertyBuilder getter(String getter) {
helper.getterName(getter);
return this;
}
public JavaBeanDoublePropertyBuilder setter(String setter) {
helper.setterName(setter);
return this;
}
public JavaBeanDoublePropertyBuilder getter(Method getter) {
helper.getter(getter);
return this;
}
public JavaBeanDoublePropertyBuilder setter(Method setter) {
helper.setter(setter);
return this;
}
}
