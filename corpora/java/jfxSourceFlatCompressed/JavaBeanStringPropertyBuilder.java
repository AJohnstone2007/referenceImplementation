package javafx.beans.property.adapter;
import com.sun.javafx.property.adapter.JavaBeanPropertyBuilderHelper;
import com.sun.javafx.property.adapter.PropertyDescriptor;
import java.lang.reflect.Method;
public final class JavaBeanStringPropertyBuilder {
private JavaBeanPropertyBuilderHelper helper = new JavaBeanPropertyBuilderHelper();
private JavaBeanStringPropertyBuilder() {}
public static JavaBeanStringPropertyBuilder create() {
return new JavaBeanStringPropertyBuilder();
}
public JavaBeanStringProperty build() throws NoSuchMethodException {
final PropertyDescriptor descriptor = helper.getDescriptor();
if (!String.class.equals(descriptor.getType())) {
throw new IllegalArgumentException("Not a String property");
}
return new JavaBeanStringProperty(descriptor, helper.getBean());
}
public JavaBeanStringPropertyBuilder name(String name) {
helper.name(name);
return this;
}
public JavaBeanStringPropertyBuilder bean(Object bean) {
helper.bean(bean);
return this;
}
public JavaBeanStringPropertyBuilder beanClass(Class<?> beanClass) {
helper.beanClass(beanClass);
return this;
}
public JavaBeanStringPropertyBuilder getter(String getter) {
helper.getterName(getter);
return this;
}
public JavaBeanStringPropertyBuilder setter(String setter) {
helper.setterName(setter);
return this;
}
public JavaBeanStringPropertyBuilder getter(Method getter) {
helper.getter(getter);
return this;
}
public JavaBeanStringPropertyBuilder setter(Method setter) {
helper.setter(setter);
return this;
}
}
