package javafx.beans.property.adapter;
import com.sun.javafx.property.adapter.ReadOnlyJavaBeanPropertyBuilderHelper;
import com.sun.javafx.property.adapter.ReadOnlyPropertyDescriptor;
import java.lang.reflect.Method;
public final class ReadOnlyJavaBeanLongPropertyBuilder {
private final ReadOnlyJavaBeanPropertyBuilderHelper helper = new ReadOnlyJavaBeanPropertyBuilderHelper();
private ReadOnlyJavaBeanLongPropertyBuilder() {}
public static ReadOnlyJavaBeanLongPropertyBuilder create() {
return new ReadOnlyJavaBeanLongPropertyBuilder();
}
public ReadOnlyJavaBeanLongProperty build() throws NoSuchMethodException {
final ReadOnlyPropertyDescriptor descriptor = helper.getDescriptor();
if (!long.class.equals(descriptor.getType()) && !Number.class.isAssignableFrom(descriptor.getType())) {
throw new IllegalArgumentException("Not a long property");
}
return new ReadOnlyJavaBeanLongProperty(descriptor, helper.getBean());
}
public ReadOnlyJavaBeanLongPropertyBuilder name(String name) {
helper.name(name);
return this;
}
public ReadOnlyJavaBeanLongPropertyBuilder bean(Object bean) {
helper.bean(bean);
return this;
}
public ReadOnlyJavaBeanLongPropertyBuilder beanClass(Class<?> beanClass) {
helper.beanClass(beanClass);
return this;
}
public ReadOnlyJavaBeanLongPropertyBuilder getter(String getter) {
helper.getterName(getter);
return this;
}
public ReadOnlyJavaBeanLongPropertyBuilder getter(Method getter) {
helper.getter(getter);
return this;
}
}
