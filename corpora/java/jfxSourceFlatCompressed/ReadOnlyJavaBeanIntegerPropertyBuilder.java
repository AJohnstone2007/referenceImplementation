package javafx.beans.property.adapter;
import com.sun.javafx.property.adapter.ReadOnlyJavaBeanPropertyBuilderHelper;
import com.sun.javafx.property.adapter.ReadOnlyPropertyDescriptor;
import java.lang.reflect.Method;
public final class ReadOnlyJavaBeanIntegerPropertyBuilder {
private final ReadOnlyJavaBeanPropertyBuilderHelper helper = new ReadOnlyJavaBeanPropertyBuilderHelper();
private ReadOnlyJavaBeanIntegerPropertyBuilder() {}
public static ReadOnlyJavaBeanIntegerPropertyBuilder create() {
return new ReadOnlyJavaBeanIntegerPropertyBuilder();
}
public ReadOnlyJavaBeanIntegerProperty build() throws NoSuchMethodException {
final ReadOnlyPropertyDescriptor descriptor = helper.getDescriptor();
if (!int.class.equals(descriptor.getType()) && !Number.class.isAssignableFrom(descriptor.getType())) {
throw new IllegalArgumentException("Not an int property");
}
return new ReadOnlyJavaBeanIntegerProperty(descriptor, helper.getBean());
}
public ReadOnlyJavaBeanIntegerPropertyBuilder name(String name) {
helper.name(name);
return this;
}
public ReadOnlyJavaBeanIntegerPropertyBuilder bean(Object bean) {
helper.bean(bean);
return this;
}
public ReadOnlyJavaBeanIntegerPropertyBuilder beanClass(Class<?> beanClass) {
helper.beanClass(beanClass);
return this;
}
public ReadOnlyJavaBeanIntegerPropertyBuilder getter(String getter) {
helper.getterName(getter);
return this;
}
public ReadOnlyJavaBeanIntegerPropertyBuilder getter(Method getter) {
helper.getter(getter);
return this;
}
}
