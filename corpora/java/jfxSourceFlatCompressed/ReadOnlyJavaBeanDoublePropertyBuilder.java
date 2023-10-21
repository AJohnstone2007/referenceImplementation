package javafx.beans.property.adapter;
import com.sun.javafx.property.adapter.ReadOnlyJavaBeanPropertyBuilderHelper;
import com.sun.javafx.property.adapter.ReadOnlyPropertyDescriptor;
import java.lang.reflect.Method;
public final class ReadOnlyJavaBeanDoublePropertyBuilder {
private final ReadOnlyJavaBeanPropertyBuilderHelper helper = new ReadOnlyJavaBeanPropertyBuilderHelper();
private ReadOnlyJavaBeanDoublePropertyBuilder() {}
public static ReadOnlyJavaBeanDoublePropertyBuilder create() {
return new ReadOnlyJavaBeanDoublePropertyBuilder();
}
public ReadOnlyJavaBeanDoubleProperty build() throws NoSuchMethodException {
final ReadOnlyPropertyDescriptor descriptor = helper.getDescriptor();
if (!double.class.equals(descriptor.getType()) && !Number.class.isAssignableFrom(descriptor.getType())) {
throw new IllegalArgumentException("Not a double property");
}
return new ReadOnlyJavaBeanDoubleProperty(descriptor, helper.getBean());
}
public ReadOnlyJavaBeanDoublePropertyBuilder name(String name) {
helper.name(name);
return this;
}
public ReadOnlyJavaBeanDoublePropertyBuilder bean(Object bean) {
helper.bean(bean);
return this;
}
public ReadOnlyJavaBeanDoublePropertyBuilder beanClass(Class<?> beanClass) {
helper.beanClass(beanClass);
return this;
}
public ReadOnlyJavaBeanDoublePropertyBuilder getter(String getter) {
helper.getterName(getter);
return this;
}
public ReadOnlyJavaBeanDoublePropertyBuilder getter(Method getter) {
helper.getter(getter);
return this;
}
}
