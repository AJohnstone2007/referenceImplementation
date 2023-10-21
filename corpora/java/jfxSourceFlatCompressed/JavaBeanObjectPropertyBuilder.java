package javafx.beans.property.adapter;
import com.sun.javafx.property.adapter.JavaBeanPropertyBuilderHelper;
import com.sun.javafx.property.adapter.PropertyDescriptor;
import java.lang.reflect.Method;
public final class JavaBeanObjectPropertyBuilder<T> {
private JavaBeanPropertyBuilderHelper helper = new JavaBeanPropertyBuilderHelper();
private JavaBeanObjectPropertyBuilder() {}
public static JavaBeanObjectPropertyBuilder create() {
return new JavaBeanObjectPropertyBuilder();
}
public JavaBeanObjectProperty<T> build() throws NoSuchMethodException {
final PropertyDescriptor descriptor = helper.getDescriptor();
return new JavaBeanObjectProperty<T>(descriptor, helper.getBean());
}
public JavaBeanObjectPropertyBuilder name(String name) {
helper.name(name);
return this;
}
public JavaBeanObjectPropertyBuilder bean(Object bean) {
helper.bean(bean);
return this;
}
public JavaBeanObjectPropertyBuilder beanClass(Class<?> beanClass) {
helper.beanClass(beanClass);
return this;
}
public JavaBeanObjectPropertyBuilder getter(String getter) {
helper.getterName(getter);
return this;
}
public JavaBeanObjectPropertyBuilder setter(String setter) {
helper.setterName(setter);
return this;
}
public JavaBeanObjectPropertyBuilder getter(Method getter) {
helper.getter(getter);
return this;
}
public JavaBeanObjectPropertyBuilder setter(Method setter) {
helper.setter(setter);
return this;
}
}
