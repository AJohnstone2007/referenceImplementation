package javafx.beans.property.adapter;
import com.sun.javafx.property.adapter.ReadOnlyJavaBeanPropertyBuilderHelper;
import com.sun.javafx.property.adapter.ReadOnlyPropertyDescriptor;
import java.lang.reflect.Method;
public final class ReadOnlyJavaBeanFloatPropertyBuilder {
private final ReadOnlyJavaBeanPropertyBuilderHelper helper = new ReadOnlyJavaBeanPropertyBuilderHelper();
private ReadOnlyJavaBeanFloatPropertyBuilder() {}
public static ReadOnlyJavaBeanFloatPropertyBuilder create() {
return new ReadOnlyJavaBeanFloatPropertyBuilder();
}
public ReadOnlyJavaBeanFloatProperty build() throws NoSuchMethodException {
final ReadOnlyPropertyDescriptor descriptor = helper.getDescriptor();
if (!float.class.equals(descriptor.getType()) && !Number.class.isAssignableFrom(descriptor.getType())) {
throw new IllegalArgumentException("Not a float property");
}
return new ReadOnlyJavaBeanFloatProperty(descriptor, helper.getBean());
}
public ReadOnlyJavaBeanFloatPropertyBuilder name(String name) {
helper.name(name);
return this;
}
public ReadOnlyJavaBeanFloatPropertyBuilder bean(Object bean) {
helper.bean(bean);
return this;
}
public ReadOnlyJavaBeanFloatPropertyBuilder beanClass(Class<?> beanClass) {
helper.beanClass(beanClass);
return this;
}
public ReadOnlyJavaBeanFloatPropertyBuilder getter(String getter) {
helper.getterName(getter);
return this;
}
public ReadOnlyJavaBeanFloatPropertyBuilder getter(Method getter) {
helper.getter(getter);
return this;
}
}
