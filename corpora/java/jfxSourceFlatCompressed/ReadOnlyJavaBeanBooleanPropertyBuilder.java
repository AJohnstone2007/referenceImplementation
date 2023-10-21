package javafx.beans.property.adapter;
import com.sun.javafx.property.adapter.ReadOnlyJavaBeanPropertyBuilderHelper;
import com.sun.javafx.property.adapter.ReadOnlyPropertyDescriptor;
import java.lang.reflect.Method;
public final class ReadOnlyJavaBeanBooleanPropertyBuilder {
private final ReadOnlyJavaBeanPropertyBuilderHelper helper = new ReadOnlyJavaBeanPropertyBuilderHelper();
private ReadOnlyJavaBeanBooleanPropertyBuilder() {}
public static ReadOnlyJavaBeanBooleanPropertyBuilder create() {
return new ReadOnlyJavaBeanBooleanPropertyBuilder();
}
public ReadOnlyJavaBeanBooleanProperty build() throws NoSuchMethodException {
final ReadOnlyPropertyDescriptor descriptor = helper.getDescriptor();
if (!boolean.class.equals(descriptor.getType()) && !Boolean.class.equals(descriptor.getType())) {
throw new IllegalArgumentException("Not a boolean property");
}
return new ReadOnlyJavaBeanBooleanProperty(descriptor, helper.getBean());
}
public ReadOnlyJavaBeanBooleanPropertyBuilder name(String name) {
helper.name(name);
return this;
}
public ReadOnlyJavaBeanBooleanPropertyBuilder bean(Object bean) {
helper.bean(bean);
return this;
}
public ReadOnlyJavaBeanBooleanPropertyBuilder beanClass(Class<?> beanClass) {
helper.beanClass(beanClass);
return this;
}
public ReadOnlyJavaBeanBooleanPropertyBuilder getter(String getter) {
helper.getterName(getter);
return this;
}
public ReadOnlyJavaBeanBooleanPropertyBuilder getter(Method getter) {
helper.getter(getter);
return this;
}
}
